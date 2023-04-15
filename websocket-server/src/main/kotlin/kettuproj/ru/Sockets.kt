package kettuproj.ru

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kettuproj.ru.model.Connection
import kettuproj.ru.protocol.Protocol
import kettuproj.ru.protocol.packet.ReceiveMessagePacket
import kettuproj.ru.protocol.packet.SendMessagePacket
import kettuproj.ru.protocol.packet.UserConnectedPacket
import kettuproj.ru.protocol.packet.UserDisconnectedPacket
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureSockets(protocol: Protocol) {
    //Initialize ktor WebSockets
    install(WebSockets) {
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        //Set connections collection
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket{

            //Add new connection when session starts
            val thisConnection = Connection(this)
            connections += thisConnection
            onConnection(thisConnection, connections)

            try{
                for (frame in incoming) {
                    if (frame !is Frame.Binary) continue
                    //Handle packet
                    when (val packet = protocol.manager.readPacket(frame.readBytes())) {
                        is SendMessagePacket -> onMessageSend(packet, thisConnection, connections)
                    }
                }
            }catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                //Disconnect user
                connections -= thisConnection
                onDisconnection(thisConnection, connections)
            }
        }
    }
}

/**
 * Send message to all users
 *
 * @param packet input message packet
 * @param sender sender of message
 * @param users receivers of packet
 */
suspend fun onMessageSend(packet: SendMessagePacket, sender: Connection, users: Set<Connection>){
    for(user in users)
        user.session.send(Frame.Binary(true, ReceiveMessagePacket(packet.message, sender.name).toByteArray()))
}

/**
 * Send connection packet
 *
 * @param sender sender of message
 * @param users receivers of packet
 */
suspend fun onConnection(sender: Connection, users: Set<Connection>){
    for(user in users)
        user.session.send(Frame.Binary(true, UserConnectedPacket(sender.name).toByteArray()))
}

/**
 * Send disconnection packet
 *
 * @param sender sender of message
 * @param users receivers of packet
 */
suspend fun onDisconnection(sender: Connection, users: Set<Connection>){
    for(user in users)
        user.session.send(Frame.Binary(true, UserDisconnectedPacket(sender.name).toByteArray()))
}