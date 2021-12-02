/*
 * Note: based on code from https://github.com/ralfhergert/pc2-telemetry
 */
package io.ppatierno.formula1.pc2.helpers;

import java.nio.ByteBuffer;

/**
 * This class reads the next bytes into a string.
 */
public class StringParser {

    public static String parse(ByteBuffer byteBuffer, int length) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < length && byteBuffer.hasRemaining(); i++) {
            byte aByte = byteBuffer.get();
            if (aByte == 0) {
                break; // null-terminated-string.
            }
            value.append((char) aByte);
        }
        return value.toString();
    }
}
