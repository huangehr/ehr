package com.yihu.ehr.util;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>CDA版本对象.</p>
 *
 * <p>由6个字节构成, 分为以下段:</p>
 * <table border="1">
 *     <caption>字节构成</caption>
 *     <tr>
 *         <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td>
 *     </tr>
 *     <tr>
 *         <td colspan="4">时间</td><td colspan="2">并发量</td>
 *     </tr>
 * </table>
 *
 * <p>本类实例不可修改.</p>
 */
public final class ObjectVersion implements Comparable<ObjectVersion>, Serializable {

    private static final long serialVersionUID = 789056789654456072L;
    private static final int BYTE_ARRAY_LENGTH = 6;
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());
    private static final char[] HEX_CHARS = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private final int timestamp;
    private final short counter;

    /**
     * 检查是否为一个合法的 ObjectId 对象.
     *
     * @param hexString 一个可能转换为对象ID的字符串.
     * @return 字符串是否为合法的ObjectId.
     * @throws IllegalArgumentException hexString为空的情况下抛出.
     */
    public static boolean isValid(final String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException();
        }

        int len = hexString.length();
        if (len != BYTE_ARRAY_LENGTH * 2) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            char c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                continue;
            }
            if (c >= 'A' && c <= 'F') {
                continue;
            }

            return false;
        }

        return true;
    }

    /**
     * 获取自增长对象的当前值.
     *
     * @return 当前自增长量.
     */
    public static int getCurrentCounter() {
        return NEXT_COUNTER.get();
    }

    /**
     * 创建一个ID对象.
     */
    public ObjectVersion() {
        this(new Date());
    }

    /**
     * 以日期初始化一个ID对象.
     *
     * @param date 日期对象.
     */
    public ObjectVersion(final Date date) {
        this(dateToTimestampSeconds(date), (short)NEXT_COUNTER.getAndIncrement());
    }

    /**
     * 以日期与计数初始化一个ID对象.
     *
     * @param date    日期
     * @param counter 计数
     * @throws IllegalArgumentException 在计数值的高位为非零的情况下抛出.
     */
    public ObjectVersion(final Date date,
                         final short counter) {
        this(dateToTimestampSeconds(date), counter);
    }

    /**
     * 以日期与计数初始化一个ID对象.
     *
     * @param timestamp     时间戳
     * @param counter       计数
     */
    public ObjectVersion(final int timestamp,
                         final short counter) {
        this.timestamp = timestamp;
        this.counter = counter;
    }

    /**
     * 根据一个 BYTE_ARRAY_LENGTH/2 个字节的字符串构造ID对象.
     *
     * @param hexString 待转换的字符串对象.
     * @throws IllegalArgumentException 若是一个无效的对象版本描述字符串, 抛出此异常.
     */
    public ObjectVersion(final String hexString) {
        this(parseHexString(hexString));
    }

    /**
     * 以字节数组创建一个对象ID.
     *
     * @param bytes 字节数组
     * @throws IllegalArgumentException 在字节数组为空, 或长度小于 BYTE_ARRAY_LENGTH 的时候抛出.
     */
    public ObjectVersion(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        if (bytes.length != BYTE_ARRAY_LENGTH) {
            throw new IllegalArgumentException("need " + BYTE_ARRAY_LENGTH + " bytes");
        }

        timestamp = makeInt(bytes[0], bytes[1], bytes[2], bytes[3]);
        counter = (short)makeInt((byte)0, (byte)0, bytes[4], bytes[5]);
    }

    /**
     * 转换为字节数组. 以 big-endian 顺序存储.
     *
     * @return the byte array
     */
    public byte[] toByteArray() {
        byte[] bytes = new byte[BYTE_ARRAY_LENGTH];
        bytes[0] = int3(timestamp);
        bytes[1] = int2(timestamp);
        bytes[2] = int1(timestamp);
        bytes[3] = int0(timestamp);
        bytes[4] = int1(counter);
        bytes[5] = int0(counter);

        return bytes;
    }

    /**
     * 获取时间戳代表的日期对象.
     *
     * @return 日期对象
     */
    public Date getDate() {
        return new Date(timestamp * 1000L);
    }

    /**
     * 获取时间戳 (基于 Unix epoch 时间的秒数).
     *
     * @return 时间戳
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * 获取计数.
     *
     * @return 计数
     */
    public short getCounter() {
        return counter;
    }

    /**
     * 将对象转换为一个含30个16进制字符的字符串表示.
     *
     * @return ID对象的16进制字符串表示.
     */
    public String toHexString() {
      char[] chars = new char[BYTE_ARRAY_LENGTH * 2];
      int i = 0;
      for (byte b : toByteArray()) {
        chars[i++] = HEX_CHARS[b >> 4 & 0xF];
        chars[i++] = HEX_CHARS[b & 0xF];
      }

      return new String(chars);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectVersion objectId = (ObjectVersion) o;

        if (counter != objectId.counter) {
            return false;
        }
        if (timestamp != objectId.timestamp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp;
        result = 31 * result + counter;
        return result;
    }

    @Override
    public int compareTo(final ObjectVersion other) {
        if (other == null) {
            throw new NullPointerException();
        }

        byte[] byteArray = toByteArray();
        byte[] otherByteArray = other.toByteArray();
        for (int i = 0; i < BYTE_ARRAY_LENGTH; i++) {
            if (byteArray[i] != otherByteArray[i]) {
                return ((byteArray[i] & 0xff) < (otherByteArray[i] & 0xff)) ? -1 : 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return toHexString();
    }

    static {
        try {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] parseHexString(final String s) {
        if (!isValid(s)) {
            throw new IllegalArgumentException("invalid hexadecimal representation of an ObjectId: [" + s + "]");
        }

        byte[] b = new byte[BYTE_ARRAY_LENGTH];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    private static int dateToTimestampSeconds(final Date time) {
        return (int) (time.getTime() / 1000);
    }

    private static int makeInt(final byte b3, final byte b2, final byte b1, final byte b0) {
        return (((b3) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    private static byte int3(final int x) {
        return (byte) (x >> 24);
    }

    private static byte int2(final int x) {
        return (byte) (x >> 16);
    }

    private static byte int1(final int x) {
        return (byte) (x >> 8);
    }

    private static byte int0(final int x) { return (byte) (x); }
}

