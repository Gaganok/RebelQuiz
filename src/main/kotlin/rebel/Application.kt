package rebel

import io.ktor.server.application.*
import rebel.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureHTTP()
    configureRouting()
    configureSession()
    configureThymeleaf()
}
