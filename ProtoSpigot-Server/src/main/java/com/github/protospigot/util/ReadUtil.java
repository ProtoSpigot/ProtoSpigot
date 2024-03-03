package com.github.protospigot.util;

import com.google.common.base.Charsets;

import java.io.DataInputStream;
import java.io.IOException;

public final class ReadUtil {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    /**
     * Reads a 1.7+ var-int from an input stream.
     *
     * @param stream the input stream
     * @return the var int
     */
    public static int readVarInt(DataInputStream stream) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = stream.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0)
                break;

            position += 7;

            if (position >= 32)
                throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    /**
     * Reads a 1.7+ var-int from an input stream.
     *
     * @param stream the input stream
     * @return the var int
     */
    public static int readVarInt(int firstByte, DataInputStream stream) throws IOException {
        int value = 0;
        int position = 0;
        Byte currentByte = null;

        while (true) {
            if (currentByte == null)
                currentByte = (byte) firstByte;
            else
                currentByte = stream.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0)
                break;

            position += 7;

            if (position >= 32)
                throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    /**
     * Reads a 1.7+ string with a max length from an input stream.
     *
     * @param stream the stream
     * @param maxLength the max length
     * @return the string
     */
    public static String readModernString(DataInputStream stream, int maxLength) throws IOException {
        int length = readVarInt(stream);
        if (length > maxLength) {
            throw new IOException("Received string length longer than maximum allowed (" + length + " > " + maxLength + ")");
        }

        if (length < 0) {
            throw new IOException("Received string length is less than zero! Weird string!");
        }

        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = stream.readByte();

        return new String(bytes, Charsets.UTF_8);
    }

    private ReadUtil() {}
}
