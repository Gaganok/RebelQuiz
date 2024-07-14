package rebel.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.selectUnbiased
import rebel.service.establishParticipantSession
import rebel.service.roomByName
import java.time.Duration

fun Application.configureSockets() {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws") { // websocketSession
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }

        webSocket("/ws/room/{name}") {
            val room = call.parameters["name"]
                ?.let { roomByName(it) }
                ?: return@webSocket close(CloseReason(CloseReason.Codes.NORMAL, "No room specified."))

            establishParticipantSession(room, this)

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    println(frame.readText())
                }
            }

        }
    }
}
