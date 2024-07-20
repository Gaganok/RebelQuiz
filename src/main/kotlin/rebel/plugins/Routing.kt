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
            call.respond(welcome())
        }

        post("/playground") {
            val params = call.receiveParameters()

            call.sessions.get<QuizSession>()
                ?: call.sessions.set(QuizSession(nickname = params.nicknameParam()))

            call.respond(playground())
        }

        get("/playground/refresh") {
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
                "Invalid participant participant selected question"
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

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/wrong/{questionId}") {
            val session = call.quizSession()
            val room = session.room()

            require(room.host.name == session.nickname) {
                "Non-host participant cannot judge"
            }

            pickQuestion(room, findQuestion(room, call.questionId()))

            call.respond(HttpStatusCode.NoContent)
        }

        get("/room/skip/{questionId}") {
            val session = call.quizSession()
            val room = session.room()

            require(room.host.name == session.nickname) {
                "Non-host participant cannot skip"
            }

            findQuestion(room, call.questionId()).isAnswered = true

            nextRound(room)

            call.respond(HttpStatusCode.NoContent)
        }
    }
}