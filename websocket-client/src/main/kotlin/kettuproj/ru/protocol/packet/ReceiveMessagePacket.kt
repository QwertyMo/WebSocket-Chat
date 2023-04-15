package kettuproj.ru.protocol.packet

import ru.kettuproj.packager.Packet
import ru.kettuproj.packager.annotation.Protocol

@Protocol(3)
class ReceiveMessagePacket : Packet {
    var message: String = ""
    var user: String = ""

    constructor(buf: ByteArray) : super(buf){
        message = readString()
        user = readString()
    }

    constructor(
        message: String,
        user: String
    ) : super(){
        writeString(message)
        writeString(user)
    }
}