package kettuproj.ru.protocol.packet

import ru.kettuproj.packager.Packet
import ru.kettuproj.packager.annotation.Protocol

@Protocol(4)
class SendMessagePacket : Packet {
    var message: String = ""
    constructor(buf: ByteArray) : super(buf){
        message = readString()
    }

    constructor(
        message: String
    ) : super(){
        writeString(message)
    }
}