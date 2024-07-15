package rebel.service

import io.ktor.server.thymeleaf.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import rebel.utils.participants
import rebel.utils.questionnaire
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap

val roomSessions = ConcurrentHashMap<String, RoomSession>()
val templateEngine = createTemplateEngine();

fun establishRoomSession(room: Room, session: WebSocketSession) {
    roomSessions.computeIfAbsent(room.name) { RoomSession() }.connections.add(session)
}

fun updateParticipants(room: Room) {
    val roomSession = roomSessions[room.name]
        ?: throw IllegalStateException("Room has no session")

    val connections = roomSession.connections
        .takeUnless { it.isEmpty() }
        ?: return;

    val template = renderToString(participants(room));
    runBlocking {
        launch { connections.forEach {it.outgoing.send(Frame.Text(template))} }
    }
}

fun startQuiz(room: Room) {
    val participants = room.participants;

    require(participants.isNotEmpty()) {
        "Not allowed to start an empty room"
    }

    participants.shuffle()
    room.guesser = participants[0].name

    roomSessions[room.name]
        ?.let { it.state = RoomState.ONGOING }
        ?: throw IllegalStateException("No room session found!")

    val activeTemplate = renderToString(questionnaire(room.quizPack, true));
    val passiveTemplate = renderToString(questionnaire(room.quizPack, false));

    runBlocking {
        launch {
            room.host.connection?.outgoing?.send(Frame.Text(activeTemplate))
            participants[0].connection?.outgoing?.send(Frame.Text(activeTemplate))
            participants.drop(1).forEach { it.connection?.outgoing?.send(Frame.Text(passiveTemplate)) }
        }
    }

    updateParticipants(room)
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

enum class RoomState{
    WAITING_PARTICIPANTS,
    ONGOING,
    FINISHED
}

data class RoomSession(
    val connections: MutableList<WebSocketSession> = mutableListOf(),
    var state: RoomState = RoomState.WAITING_PARTICIPANTS,
)