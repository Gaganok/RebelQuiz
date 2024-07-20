package rebel.service

fun findQuestion(room: Room, questionId: String): Question {
    return room.quizPack.questionnaire.flatMap { it.qa }.find { it.id.toString() == questionId }
        ?: throw IllegalArgumentException("Question $questionId not found!")
}