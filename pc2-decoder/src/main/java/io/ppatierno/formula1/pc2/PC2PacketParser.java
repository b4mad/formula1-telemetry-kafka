/*
 * Packet parser definition (based on code from https://github.com/ralfhergert/pc2-telemetry)
 */

package io.ppatierno.formula1.pc2;

import io.netty.buffer.ByteBuf;
import io.ppatierno.formula1.interfaces.PacketParser;

/**
 * This parser converts {@link }
 */
public class PC2PacketParser implements PacketParser<BasePacket> {

    @Override
    public BasePacket parse(ByteBuf byteBuffer) throws IllegalArgumentException {
        try {
            byte[] packetData = new byte[byteBuffer.readableBytes()];
            byteBuffer.duplicate().readBytes(packetData);

            BasePacket basePacket = new BasePacket(packetData);

            if (basePacket.getPacketType() == PacketTypes.CarPhysics && basePacket.getPacketVersion() == 3) {
                return new CarPhysicsPacket(packetData);
            } else {
                return basePacket;
            }
        } catch (Exception ex) {
            // FIXME: remove this when done
            ex.printStackTrace();
            return null;
        }
    }
}
