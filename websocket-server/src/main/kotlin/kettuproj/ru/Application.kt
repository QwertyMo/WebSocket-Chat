package kettuproj.ru

import io.ktor.server.application.*
import kettuproj.ru.protocol.Protocol

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSockets(Protocol())
}
