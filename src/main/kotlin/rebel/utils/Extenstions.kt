package rebel.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import io.ktor.websocket.*
import rebel.plugins.QuizSession
import rebel.service.*

fun ApplicationCall.roomByParam(): Room? {
    return this.parameters["room"]
        ?.let { roomByName(it) }
}

fun Parameters.nicknameParam(): String {
    return this["nickname"]
        ?.takeUnless { it.isBlank() }
        ?: throw IllegalArgumentException("Nickname is missing or blank")
}

fun Parameters.roomNameParam(): String {
    return this["roomName"]
        ?.takeUnless { it.isBlank() }
        ?: throw IllegalArgumentException("Room name is missing or blank")
}

fun Parameters.packByParam(): Pack {
    return this["selectedPack"]
        ?.let { packByName(it) }
        ?: throw IllegalArgumentException("Pack selection is missing")

}

fun QuizSession.room(): Room {
    return this.room
        ?.let { roomByName(it) }
        ?: throw IllegalStateException("Room is missing from session")
}

fun ApplicationCall.quizSession(): QuizSession {
    return this.sessions.get<QuizSession>() ?: throw IllegalArgumentException("Session info is missing");
}

fun ApplicationCall.questionId(): String {
    return this.parameters["questionId"]
        ?.takeUnless { it.isBlank() }
        ?: throw IllegalArgumentException("Question id is missing or blank")
}

fun ThymeleafContent.frameText(): Frame.Text {
    return Frame.Text(renderToString(this))
}