package rebel.service

import io.ktor.server.thymeleaf.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import rebel.utils.*
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap

val roomSessions = ConcurrentHashMap<String, RoomSession>()
val templateEngine = createTemplateEngine();

fun establishRoomSession(room: Room, session: WebSocketSession) {
    roomSessions.computeIfAbsent(room.name) { RoomSession() }.connections.add(session)
}

fun updateParticipants(room: Room) {
    val roomSession = roomSession(room)

    room.participants.sortByDescending { it.points }

    val connections = roomSession.connections
        .takeUnless { it.isEmpty() }
        ?: return;

    runBlocking {
        launch { connections.forEach { it.outgoing.send(participants(room).frameText()) } }
    }
}

fun startQuiz(room: Room) {
    val participants = room.participants;

    require(participants.isNotEmpty()) {
        "Not allowed to start an empty room"
    }

    resetRoom(room)
    nextRound(room)
}

fun leaveRoom(participant: Participant, room: Room) {
    val roomSession = roomSession(room)

    runBlocking {
        launch {
            participant.connection?.close(CloseReason(CloseReason.Codes.NORMAL, "Left"))
            roomSession.connections.remove(participant.connection)
        }
    }

    updateParticipants(room)
}

fun disbandRoom(room: Room) {
    val roomSession = roomSession(room)

    runBlocking {
        launch {
            roomSession.connections.forEach { it.close(CloseReason(CloseReason.Codes.NORMAL, "Disband")) }
        }
    }

    roomSessions.remove(room.name)
}

fun resetRoom(room: Room) {
    room.participants.let {
        it.shuffle()
        it.forEach{ it.points = 0 }
    }

    room.guesser = room.participants[0].name
    room.winner = null;

    room.quizPack.questionnaire
        .flatMap { it.qa }
        .forEach {it.isAnswered = false}
}

fun nextRound(room: Room) {
    roomSession(room).let { it.state = RoomState.PICKING_QUESTION }

    removeControl(room)

    if (room.quizPack.hasNoQuestionLeft()) {
        return quitQuiz(room)
    }

    val participants = room.participants;

    runBlocking {
        launch {
            room.host.connection?.outgoing?.send(questionnaire(room.quizPack, true).frameText())
            participants.forEach { it.connection?.outgoing?.send(
                questionnaire(room.quizPack, it.name == room.guesser).frameText()
            ) }
        }
    }

    updateParticipants(room)
}

fun removeControl(room: Room) {
    runBlocking {
        launch {
            room.host.connection?.outgoing?.send(noControl().frameText())
            room.participants.forEach { it.connection?.outgoing?.send(noControl().frameText()) }
        }
    }
}

fun quitQuiz(room: Room) {
    val roomSession = roomSession(room)
    roomSession.let { it.state = RoomState.WAITING_PARTICIPANTS }

    room.selectWinner()

    runBlocking {
        launch {
            roomSession.connections.forEach {
                it.outgoing.send(winnerBoard().frameText())
            }
        }
    }

    updateParticipants(room)
}

fun pickQuestion(room: Room, question: Question) {
    val roomSession = roomSession(room)

    room.guesser = null;

    require(roomSession.state == RoomState.PICKING_QUESTION
            || roomSession.state == RoomState.JUDGING)

    roomSession.state = RoomState.ANSWERING

    val category = room.quizPack.categoryByQuestion(question)

    runBlocking {
        launch {
            room.host.connection?.outgoing?.let {
                it.send(questionAnswer(question, category).frameText())
                it.send(judgeSkipControl(question).frameText())
            }
            room.participants.forEach {
                it.connection?.outgoing?.send(question(question, category).frameText())
                it.connection?.outgoing?.send(answerControl(question).frameText())
            }
        }
    }

    updateParticipants(room)
}

fun loaded(room: Room) {
    val roomSession = roomSession(room)

    require(roomSession.state == RoomState.ANSWERING)

    runBlocking {
        roomSession
            .apply { ++loadedCount }
            .takeIf { room.participants.size + 1 == it.loadedCount }
            ?.apply { loadedCount = 0 }
            .run {
                room.host.connection?.outgoing?.send(displayQuestion().frameText())
                room.participants.forEach {
                    it.connection?.outgoing?.send(displayQuestion().frameText())
                }
            }
    }
}

fun answerQuestion(room: Room, candidateGuesser: String, question: Question) {
    val roomSession = roomSession(room)

    runBlocking {
        require(roomSession.state == RoomState.ANSWERING)
            {"Someone is already answering the question"}

        roomSession.state = RoomState.JUDGING
        room.guesser = candidateGuesser

        launch {
            room.host.connection?.outgoing?.send(judgeControl(question).frameText())
            room.participants.forEach { it.connection?.outgoing?.send(answerInactiveControl().frameText()) }
        }

        updateParticipants(room)
    }
}

fun renderToString(content: ThymeleafContent): String {
    val stringWriter = StringWriter()

    val context = Context()
    context.setVariables(content.model)

    templateEngine.process(content.template, context, stringWriter)
    return stringWriter.toString()
}

fun createTemplateEngine(): TemplateEngine {
    val templateResolver = ClassLoaderTemplateResolver().apply {
        prefix = "/templates/"
        suffix = ".html"
        templateMode = org.thymeleaf.templatemode.TemplateMode.HTML
        characterEncoding = "UTF-8"
    }
    return TemplateEngine().apply {
        setTemplateResolver(templateResolver)
    }
}

fun roomSession(room: Room): RoomSession {
    return roomSessions[room.name]
        ?: throw IllegalStateException("No room session found!")
}

fun roomStatus(room: Room): RoomState {
    return roomSession(room).state
}

enum class RoomState{
    WAITING_PARTICIPANTS,
    PICKING_QUESTION,
    ANSWERING,
    JUDGING
}

data class RoomSession(
    val connections: MutableList<WebSocketSession> = mutableListOf(),
    var state: RoomState = RoomState.WAITING_PARTICIPANTS,
    var loadedCount: Int = 0
)