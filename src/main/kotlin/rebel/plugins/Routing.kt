package rebel.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import rebel.service.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respond(welcome())
        }

        post("/playground") {
            val params = call.receiveParameters()
            val nickname: String = params["nickname"]
                ?.takeUnless { it.isBlank() }
                ?: throw IllegalArgumentException("Nickname is missing or blank")

            call.sessions.get<QuizSession>() ?: call.sessions.set(QuizSession(nickname = nickname))
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
            val name: String = params["name"]
                ?.takeUnless { it.isBlank() }
                ?: throw IllegalArgumentException("Room name is missing or blank")

            val pack: Pack = params["selectedPack"]
                ?.let { packByName(it) }
                ?: throw IllegalArgumentException("Pack selection is missing")

            val host = call.sessions.get<QuizSession>()?.nickname
                ?: throw IllegalArgumentException("Session info is missing");

            val room = newRoom(name, host, pack);

            call.respond(room(RoomTemplateData(room, true)))
        }

        post("/room/join") {
            val params = call.receiveParameters()
            val roomName: String = params["selectedRoom"]
                ?.takeUnless { it.isBlank() }
                ?: throw IllegalArgumentException("Room name is missing or blank")

            val participantName = call.sessions.get<QuizSession>()?.nickname
                ?: throw IllegalArgumentException("Session info is missing");

            val room = joinRoom(participantName, roomName)

            call.respond(room(RoomTemplateData(room)))
        }
    }
}

data class RoomTemplateData(val room: Room, val isHost: Boolean = false)

fun roomList() : ThymeleafContent {
    return ThymeleafContent("fragments/rooms", mapOf(Pair("rooms", rooms())))
}

fun room(data: RoomTemplateData) : ThymeleafContent {
    return ThymeleafContent("room_min", mapOf(Pair("data", data)))
}

fun createRoom() : ThymeleafContent {
    return ThymeleafContent("create_room_min", mapOf(Pair("packNames", packNames())))
}

fun playground() : ThymeleafContent {
    return ThymeleafContent("playground_min", mapOf(Pair("rooms", rooms())))
}

fun welcome() : ThymeleafContent {
    return ThymeleafContent("welcome", mapOf())
}