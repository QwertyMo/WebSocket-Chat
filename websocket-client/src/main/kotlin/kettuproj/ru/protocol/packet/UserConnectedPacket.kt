package kettuproj.ru.protocol.packet

import ru.kettuproj.packager.Packet
import ru.kettuproj.packager.annotation.Protocol

@Protocol(2)
class UserConnectedPacket : Packet {
    var user: String = ""

    constructor(buf: ByteArray) : super(buf){
        user = readString()
    }

    constructor(
        user: String
    ) : super(){
        writeString(user)
    }
}