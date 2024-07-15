package rebel.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import rebel.plugins.QuizSession
import rebel.service.Pack
import rebel.service.Room
import rebel.service.packByName
import rebel.service.roomByName

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

fun ApplicationCall.quizSession(): QuizSession {
    return this.sessions.get<QuizSession>() ?: throw IllegalArgumentException("Session info is missing");
}