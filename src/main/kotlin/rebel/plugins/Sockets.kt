package rebel.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import rebel.service.establishRoomSession
import rebel.utils.roomByParam
import java.time.Duration

fun Application.configureSockets() {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws/room/{room}/host/{id}") {
            val room = call.roomByParam()
                ?: return@webSocket close(CloseReason(CloseReason.Codes.NORMAL, "No room specified."))

            call.parameters["id"]
                ?.takeIf { room.host.id.toString() == it }
                ?.let { room.host.connection = this}
                ?: return@webSocket close(CloseReason(CloseReason.Codes.NORMAL, "No participant specified."))

            establishRoomSession(room, this)

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    println(frame.readText())
                }
            }
        }
    }

    routing {
        webSocket("/ws/room/{room}/participant/{id}") {
            val room = call.roomByParam()
                ?: return@webSocket close(CloseReason(CloseReason.Codes.NORMAL, "No room specified."))

            call.parameters["id"]
                ?.let { id ->  room.participants.find { it.id.toString() == id } }
                ?.let { it.connection = this}
                ?: return@webSocket close(CloseReason(CloseReason.Codes.NORMAL, "No participant specified."))

            establishRoomSession(room, this)

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    println(frame.readText())
                }
            }

        }
    }
}