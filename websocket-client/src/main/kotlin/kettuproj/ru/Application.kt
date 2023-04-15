package kettuproj.ru

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kettuproj.ru.protocol.Protocol
import kettuproj.ru.protocol.packet.ReceiveMessagePacket
import kettuproj.ru.protocol.packet.SendMessagePacket
import kettuproj.ru.protocol.packet.UserConnectedPacket
import kettuproj.ru.protocol.packet.UserDisconnectedPacket
import kotlinx.coroutines.*

val protocol = Protocol()

fun main() = runBlocking {

    val client = HttpClient(CIO) {
        install(WebSockets) {

        }
    }

    //Open connection to server
    client.webSocket(host = "127.0.0.1", port = 8080) {
        while (true) {
            val messageOutputRoutine = launch { outputMessages() }
            val userInputRoutine = launch { inputMessages() }

            userInputRoutine.join()
            messageOutputRoutine.cancelAndJoin()
        }
    }
    client.close()

}

/**
 * Read all data from server
 */
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        for (message in incoming) {
            message as? Frame.Binary ?: continue
            when(val packet = protocol.manager.readPacket(message.readBytes())){
                is ReceiveMessagePacket -> onMessageReceive(packet)
                is UserDisconnectedPacket -> onUserLeave(packet)
                is UserConnectedPacket -> onUserJoin(packet)
            }
        }
    } catch (e: Exception) {
        println("Error while receiving: " + e.localizedMessage)
    }
}

/**
 * Read from console, and send message to server
 */
suspend fun DefaultClientWebSocketSession.inputMessages() {
    while (true) {
        val message = readLine() ?: ""
        try {
            send(Frame.Binary(true, SendMessagePacket(message).toByteArray()))
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}

/**
 * Call when user receive message
 *
 * @param packet input packet
 */
fun onMessageReceive(packet: ReceiveMessagePacket){
    println("${packet.user}: ${packet.message}")
}

/**
 * Call when user join chat
 *
 * @param packet input packet
 */
fun onUserJoin(packet: UserConnectedPacket){
   println("[+] ${packet.user}")
}

/**
 * Call when user leave chat
 *
 * @param packet input packet
 */
fun onUserLeave(packet: UserDisconnectedPacket){
    println("[-] ${packet.user}")
}