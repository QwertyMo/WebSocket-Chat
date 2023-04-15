package kettuproj.ru.model

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Connection data for user session
 *
 * @param session WebSocket session
 */
class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}