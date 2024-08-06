package rebel.utils

import io.ktor.server.thymeleaf.*
import rebel.service.Room
import rebel.service.packNames
import rebel.service.rooms
import java.util.*

enum class ParticipantStatus {
    HOST,
    VIEWER,
    GUESSER
}

//data class RoomTemplateData(
//    val room: Room,
//    val participantId: UUID,
//    val participantStatus: ParticipantStatus)

fun room(room: Room, participantId: UUID, participantStatus: ParticipantStatus) : ThymeleafContent {
    return ThymeleafContent("room_min", mapOf(
        Pair("room", room),
        Pair("participantId", participantId),
        Pair("participantStatus", participantStatus)
    ))
}

fun createRoom() : ThymeleafContent {
    return ThymeleafContent("create_room_min", mapOf(Pair("packNames", packNames())))
}

fun createPack() : ThymeleafContent {
    return ThymeleafContent("create_pack", mapOf())
}

fun playground() : ThymeleafContent {
    return ThymeleafContent("playground_min", mapOf(Pair("rooms", rooms())))
}

fun welcome() : ThymeleafContent {
    return ThymeleafContent("welcome", mapOf())
}

fun welcomePlayground() : ThymeleafContent {
    return ThymeleafContent("welcome_playground", mapOf())
}