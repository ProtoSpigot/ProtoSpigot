package com.github.protospigot.util;

import java.io.DataOutputStream;
import java.io.IOException;

public final class WriteUtil {

    public static void writeVarInt(DataOutputStream stream, int value) throws IOException {
        while (true) {
            if ((value & ~ReadUtil.SEGMENT_BITS) == 0) {
                stream.writeByte(value);
                return;
            }

            stream.writeByte((value & ReadUtil.SEGMENT_BITS) | ReadUtil.CONTINUE_BIT);
            value >>>= 7;
        }
    }

    private WriteUtil() {}
}