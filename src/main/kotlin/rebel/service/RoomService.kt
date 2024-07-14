package rebel.service

import io.ktor.util.*
import kotlinx.coroutines.launch

fun newRoom(name: String, host: String, pack: Pack): Room {
    val room = Room(name, host, pack)
    playground.rooms.put(name.toLowerCasePreservingASCIIRules(), room)
    return room
}

fun joinRoom(participantName: String, roomName: String): Room {
    val room = roomByName(roomName)
    val participant = Participant(participantName, 0)
    room.participants.add(participant)

    notifyParticipantJoined(room)

    return room
}