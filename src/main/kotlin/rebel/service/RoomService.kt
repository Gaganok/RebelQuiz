package rebel.service

import io.ktor.util.*

fun newRoom(name: String, hostName: String, pack: Pack): Room {
    val host = Host(name = hostName)
    val room = Room(name, host, pack)
    playground.rooms.put(name.toLowerCasePreservingASCIIRules(), room)
    return room
}

fun joinRoom(participant: Participant, roomName: String): Room {
    val room = roomByName(roomName)
    room.participants.add(participant)

    updateParticipants(room)

    return room
}