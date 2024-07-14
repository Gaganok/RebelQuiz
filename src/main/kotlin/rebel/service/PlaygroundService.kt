package rebel.service

import io.ktor.util.*

enum class QuestionType {
    IMAGE,
    VIDEO,
    TEXT
}

data class Question(val question: String, val answer: String, val type: QuestionType, val value: Int)
data class Pack(val name: String, val qa: List<Question>)
data class Participant(val nickname: String, val points: Int)
data class Room(var name: String, var host: String, var quizPack: Pack? = null, var participants: MutableList<Participant> = mutableListOf())
data class Playground(val rooms: MutableMap<String, Room>)

val playground = initPlayground();

fun initPlayground(): Playground {
    val testQuestion = Question("What is the capital of Great Britain?", "London", QuestionType.TEXT, 100)
    val testPack = Pack("Test Pack", listOf(testQuestion))
    val testRoom = Room("Test room", "None", testPack)
    return Playground(mutableMapOf(Pair(testRoom.name.toLowerCasePreservingASCIIRules(), testRoom)))
}

fun rooms(): Collection<Room> {
    return playground.rooms.values;
}

fun roomByName(name: String): Room {
    return playground.rooms[name.toLowerCasePreservingASCIIRules()]
        ?:throw IllegalArgumentException("Room with name $name is not found");
}