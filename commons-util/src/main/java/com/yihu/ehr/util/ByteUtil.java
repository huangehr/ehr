package com.yihu.ehr.util;

import java.nio.ByteBuffer;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.29 14:41
 */
public class ByteUtil {
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, x);

        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip

        return buffer.getLong();
    }
}