package io.ppatierno.formula1.interfaces;

import io.netty.buffer.ByteBuf;

/**
 * This interface defines the behaviors required of all
 * packet parsers.
 * @param <TParsed> The parsed data type.
 */
public interface PacketParser<TParsed> {
    public TParsed parse(ByteBuf byteBuffer) throws IllegalArgumentException;
}
