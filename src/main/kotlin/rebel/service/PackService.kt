package rebel.service

import java.lang.IllegalArgumentException
import java.util.*

fun findQuestion(room: Room, questionId: String): Question {
    return room.quizPack.questionnaire.flatMap { it.qa }.find { it.id.toString() == questionId }
        ?: throw IllegalArgumentException("Question $questionId not found!")
}