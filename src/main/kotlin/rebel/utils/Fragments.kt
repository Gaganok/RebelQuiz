package rebel.utils

import io.ktor.server.thymeleaf.*
import rebel.service.Pack
import rebel.service.Question
import rebel.service.Room
import rebel.service.rooms

fun roomList() : ThymeleafContent {
    return ThymeleafContent("fragments/rooms", mapOf(Pair("rooms", rooms())))
}

fun question(question: Question) : ThymeleafContent {
    return ThymeleafContent("fragments/question", mapOf(Pair("question", question)))
}

fun questionAnswer(question: Question) : ThymeleafContent {
    return ThymeleafContent("fragments/question_answer", mapOf(Pair("question", question)))
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