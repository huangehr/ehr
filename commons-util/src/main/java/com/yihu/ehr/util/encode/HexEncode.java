package com.yihu.ehr.util.encode;

/**
 * @created Created by Air on 2015/6/2.
 */
public class HexEncode {
    static public String toHexString(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aByte : bytes) {
            if (Integer.toHexString(0xFF & aByte).length() == 1) {
                stringBuffer.append("0").append(Integer.toHexString(0xFF & aByte));
            } else {
                stringBuffer.append(Integer.toHexString(0xFF & aByte));
            }
        }

        return stringBuffer.toString();
    }

    static public byte[] toBytes(String hexString) {
        byte[] bytes;
        bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }
}
