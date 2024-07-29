package rebel.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import rebel.service.*
import rebel.utils.*

fun Application.configureRouting() {

    routing {

        staticResources("/resources", "static") {
            contentType { url ->
                when {
                    url.file.endsWith(".mp3", ignoreCase = true) -> ContentType.Audio.MPEG
                    url.file.endsWith(".png", ignoreCase = true) -> ContentType.Image.PNG
                    else -> ContentType.Text.Plain
                }
            }
        }

        get("/") {
            val template = call.sessions.get<QuizSession>()
                ?.let { welcomePlayground() }
                ?: welcome()

            call.respond(template)
        }

        get("/exit") {
            require(call.sessions.get<QuizSession>()?.room == null) {
                "Unable to exit when still assigned to a room"
            }

            call.sessions.clear<QuizSession>()
            call.respond(welcome())
        }

        post("/playground") {
            val params = call.receiveParameters()

            call.sessions.get<QuizSession>()
                ?: call.sessions.set(QuizSession(nickname = params.nicknameParam()))

            call.respond(playground())
        }

        get("/playground/refresh") {
            val session = call.quizSession();

            session.room
                ?.takeUnless {
                    playground.rooms[it]
                        ?.let { it.participants.any { it.name == session.nickname } }
                        ?: false
                }.let { call.sessions.set(session.apply { room = null }) }

            call.respond(roomList())
        }

        get("/room/create") {
            call.respond(createRoom())
        }

        post("/room/create") {
            val params = call.receiveParameters()

            val roomName = params.roomNameParam()
            val pack: Pack = params.packByParam()
            val session = call.quizSession()

            val room = newRoom(roomName, session.nickname, pack);

            session.room = room.name
            call.sessions.set(session)

            call.respond(room(room, room.host.id, ParticipantStatus.HOST))
        }

        post("/room/join") {
            val params = call.receiveParameters()

            val roomName = params.roomNameParam()
            val session = call.quizSession()

            val participant = Participant(name = session.nickname, points = 0)
            val room = joinRoom(participant, roomName)

            session.withRoom(room.name).let { call.sessions.set(it) }

            call.respond(room(room, participant.id, ParticipantStatus.VIEWER))
        }

        get("/room/leave") {
            val session = call.quizSession();

            require(session.room != null) {
                "User session doesn't contain a room"
            }

            val room = roomByName(session.room!!)

            if (room.host.name == session.nickname) {
                playground.rooms.remove(room.name)
                disbandRoom(room)
            } else {
                room.participants.find { it.name == session.nickname }
                    ?.let {
                        room.participants.remove(it)
                        leaveRoom(it, room)
                    } ?: throw IllegalStateException("User is not in the room");
            }

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/start") {
            val session = call.quizSession();

            val room = session.room
                ?.let { roomByName(it) }
                ?: throw IllegalStateException("Room is missing from session")

            require(room.host.name == session.nickname) {
                "Non-host participant is not allowed to start the room"
            }

            startQuiz(room)

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/question/{questionId}") {
            val session = call.quizSession()

            val room = session.room
                ?.let { roomByName(it) }
                ?: throw IllegalStateException("Room is missing from session")

            require(room.guesser == session.nickname || room.host.name == session.nickname) {
                "Invalid participant selected question"
            }

            pickQuestion(room, findQuestion(room, call.questionId()))

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/answer/{questionId}") {
            val session = call.quizSession()
            val room = session.room()

//            require(session.nickname != room.host.name) {"Host cannot answer a question"}

            answerQuestion(room, session.nickname, findQuestion(room, call.questionId()))

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/question/loaded") {
            val session = call.quizSession()
            val room = session.room()

            loaded(room)

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/correct/{questionId}") {
            val session = call.quizSession()
            val room = session.room()

            require(room.host.name == session.nickname) {
                "Non-host participant not allowed to judge"
            }

            val question = findQuestion(room, call.questionId())
            question.isAnswered = true

            room.participants.find { it.name == room.guesser }
                ?.let { it.points += question.value }
                ?: throw IllegalStateException("Participant not found")

            nextRound(room)
            applause(room)
            answerModal(room, question)

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/wrong/{questionId}") {
            val session = call.quizSession()
            val room = session.room()

            require(room.host.name == session.nickname) {
                "Non-host participant cannot judge"
            }

            val question = findQuestion(room, call.questionId())

            room.participants.find { it.name == room.guesser }
                ?.let { it.points -= question.value }
                ?: throw IllegalStateException("Participant not found")

            pickQuestion(room, findQuestion(room, call.questionId()))

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/skip/{questionId}") {
            val session = call.quizSession()
            val room = session.room()

            require(room.host.name == session.nickname) {
                "Non-host participant cannot skip"
            }

            val question = findQuestion(room, call.questionId())
                .apply { isAnswered = true }

            nextRound(room)
            answerModal(room, question)

            call.respond(HttpStatusCode.NoContent)
        }
    }
}