package rebel.service

import io.ktor.util.*
import io.ktor.websocket.*
import java.util.UUID

enum class QuestionType {
    IMAGE,
    VIDEO,
    TEXT
}

data class Question(
    val question: String,
    val answer: String,
    val type: QuestionType,
    val value: Int,
    val id: UUID = UUID.randomUUID(),
    var isAnswered: Boolean = false)
data class Category(val name: String, val qa: List<Question>)
data class Pack(val name: String, val questionnaire: List<Category>) {
    fun hasNoQuestionLeft(): Boolean {
        return !questionnaire.flatMap { it.qa }.any { !it.isAnswered }
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
    val testHost = Host(name = "None")
    val testRoom = Room("room", testHost, genTestPack())
    return Playground(mutableMapOf(Pair(testRoom.name.toLowerCasePreservingASCIIRules(), testRoom)))
}

fun rooms(): Collection<Room> {
    return playground.rooms.values;
}

fun roomByName(name: String): Room {
    return playground.rooms[name.toLowerCasePreservingASCIIRules()]
        ?:throw IllegalArgumentException("Room with name $name is not found");
}