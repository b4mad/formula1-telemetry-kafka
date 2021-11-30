// camel-k: language=java
package io.ppatierno.formula1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.ppatierno.formula1.interfaces.PacketParser;

import java.util.List;

/**
 * Decodes incoming packets into pc2 data types.
 */
@ChannelHandler.Sharable
public class Decoder<TParsed> extends MessageToMessageDecoder<DatagramPacket> {
    private final PacketParser<TParsed> packetParser;

    public Decoder(PacketParser<TParsed> packetParser) {
        this.packetParser = packetParser;
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> list) {
        ByteBuf buffer = datagramPacket.content();

        TParsed packet = packetParser.parse(buffer);

        list.add(packet);
    }
}