package rebel.utils

import io.ktor.server.thymeleaf.*
import rebel.service.Pack
import rebel.service.Room
import rebel.service.rooms

fun roomList() : ThymeleafContent {
    return ThymeleafContent("fragments/rooms", mapOf(Pair("rooms", rooms())))
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