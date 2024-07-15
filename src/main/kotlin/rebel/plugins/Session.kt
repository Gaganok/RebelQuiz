package rebel.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import java.util.UUID

fun Application.configureSession() {

    install(Sessions) {
        cookie<QuizSession>("QUIZ_SESSION") {
            cookie.extensions["SameSite"] = "strict"
            cookie.httpOnly = true
        }
    }

}

data class QuizSession(
    val id: UUID = UUID.randomUUID(),
    val nickname: String,
    var room: String? = null
) {
    fun withRoom(roomName: String): QuizSession {
        this.room = roomName;
        return this;
    }
}
