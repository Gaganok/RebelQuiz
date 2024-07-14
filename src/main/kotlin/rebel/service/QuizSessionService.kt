package rebel.service

import io.ktor.server.thymeleaf.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import rebel.plugins.RoomTemplateData
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap

val roomSessions = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
val templateEngine = createTemplateEngine();

fun establishParticipantSession(room: Room, session: WebSocketSession) {
    roomSessions.computeIfAbsent(room.name) { mutableListOf() }.add(session)
}

fun notifyParticipantJoined(room: Room) {
    val template = renderToString(participants(RoomTemplateData(room)));
    runBlocking {
        launch { roomSessions[room.name]
            ?.forEach {it.outgoing.send(Frame.Text(template))} }
    }
}

fun participants(data: RoomTemplateData) : ThymeleafContent {
    return ThymeleafContent("fragments/participants", mapOf(Pair("data", data)))
}

fun renderToString(content: ThymeleafContent): String {
    val stringWriter = StringWriter()

    val context = Context()
    context.setVariables(content.model)

    templateEngine.process(content.template, context, stringWriter)
    return stringWriter.toString()
}

fun createTemplateEngine(): TemplateEngine {
    val templateResolver = ClassLoaderTemplateResolver().apply {
        prefix = "/templates/"
        suffix = ".html"
        templateMode = org.thymeleaf.templatemode.TemplateMode.HTML
        characterEncoding = "UTF-8"
    }
    return TemplateEngine().apply {
        setTemplateResolver(templateResolver)
    }
}