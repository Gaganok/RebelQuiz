package rebel.utils

import io.ktor.server.thymeleaf.*
import rebel.service.*

fun roomList() : ThymeleafContent {
    return ThymeleafContent("fragments/rooms", mapOf(Pair("rooms", rooms())))
}

fun question(question: Question, category: Category) : ThymeleafContent {
    return ThymeleafContent("fragments/question", mapOf(
        Pair("question", question),
        Pair("category", category))
    )
}

fun answerModal(question: Question) : ThymeleafContent {
    return ThymeleafContent("fragments/modal/answer_modal", mapOf(Pair("question", question)))
}

fun applause() : ThymeleafContent {
    return ThymeleafContent("fragments/script/applause", mapOf())
}

fun winnerBoard() : ThymeleafContent {
    return ThymeleafContent("fragments/winner-board", mapOf())
}

fun answerControl(question: Question) : ThymeleafContent {
    return ThymeleafContent("fragments/control/answer-control", mapOf(Pair("question", question)))
}

fun noControl() : ThymeleafContent {
    return ThymeleafContent("fragments/control/no-control", mapOf())
}

fun answerInactiveControl() : ThymeleafContent {
    return ThymeleafContent("fragments/control/answer-inactive-control", mapOf())
}

fun judgeControl(question: Question) : ThymeleafContent {
    return ThymeleafContent("fragments/control/judge-control", mapOf(Pair("question", question)))
}

fun judgeSkipControl(question: Question) : ThymeleafContent {
    return ThymeleafContent("fragments/control/judge-skip-control", mapOf(Pair("question", question)))
}

fun questionAnswer(question: Question, category: Category) : ThymeleafContent {
    return ThymeleafContent("fragments/question_answer", mapOf(
        Pair("question", question),
        Pair("category", category)
    ))
}

fun displayQuestion() : ThymeleafContent {
    return ThymeleafContent("fragments/script/display_question", mapOf())
}

fun participants(room: Room) : ThymeleafContent {
    return ThymeleafContent("fragments/participants", mapOf(Pair("room", room)))
}

fun questionnaire(pack: Pack, active: Boolean) : ThymeleafContent {
    return ThymeleafContent("fragments/questionnaire", mapOf(
        Pair("questionnaire", pack.questionnaire),
        Pair("active", active),
    ))
}