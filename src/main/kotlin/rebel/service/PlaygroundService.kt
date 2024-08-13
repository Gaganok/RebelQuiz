package rebel.service

import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

enum class QuestionType {
    IMAGE,
    VIDEO,
    AUDIO,
    TEXT;
}

@Serializable
data class Question(
    val question: String,
    var answer: String,
    val type: QuestionType,
    val value: Int,
    @Transient val id: UUID = UUID.randomUUID(),
    var isAnswered: Boolean = false)

@Serializable
data class Category(val name: String, val qa: List<Question>, val description: String? = null)

@Serializable
data class Pack(val name: String, val questionnaire: List<Category>) {
    fun hasNoQuestionLeft(): Boolean {
        return !questionnaire.flatMap { it.qa }.any { !it.isAnswered }
    }

    fun categoryByQuestion(question: Question): Category {
        return questionnaire.find { it.qa.contains(question) }
            ?: throw IllegalArgumentException("Question does not belong to the pack")
    }
}
data class Participant(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var points: Int,
    var connection: WebSocketSession? = null)

data class Host(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var connection: WebSocketSession? = null
)

data class Room(
    var name: String,
    var host: Host,
    var quizPack: Pack,
    var participants: MutableList<Participant> = mutableListOf(),
    var guesser: String? = null,
    var winner: String? = null
) {
    fun selectWinner() {
        winner = participants.maxBy { it.points }.name
    }
}

data class Playground(val rooms: MutableMap<String, Room>)

val playground = initPlayground();

fun initPlayground(): Playground {
    return Playground(mutableMapOf())
}

fun rooms(): Collection<Room> {
    return playground.rooms.values.filter { roomStatus(it) == RoomState.WAITING_PARTICIPANTS }
}

fun roomByName(name: String): Room {
    return playground.rooms[name] ?:throw IllegalArgumentException("Room with name $name is not found");
}