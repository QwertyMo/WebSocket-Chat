package kettuproj.ru.protocol

import kettuproj.ru.protocol.packet.ReceiveMessagePacket
import kettuproj.ru.protocol.packet.SendMessagePacket
import kettuproj.ru.protocol.packet.UserConnectedPacket
import kettuproj.ru.protocol.packet.UserDisconnectedPacket
import ru.kettuproj.packager.PacketManager

class Protocol {
    val manager = PacketManager()

    init{
        manager.registerPacket(SendMessagePacket::class)
        manager.registerPacket(ReceiveMessagePacket::class)
        manager.registerPacket(UserDisconnectedPacket::class)
        manager.registerPacket(UserConnectedPacket::class)
    }
}