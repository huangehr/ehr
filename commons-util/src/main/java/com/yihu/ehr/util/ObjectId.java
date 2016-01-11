package com.yihu.ehr.util;

import com.yihu.ehr.constants.BizObject;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>对象全局唯一ID.</p>
 * <p>
 * 本类修改自 MongoDB 的org.bson.ObjectId. ID生成原则:
 * <lo>
 * <li>地区: 区分数据地理来源, 到市/自治区一级, 即四位数字, 如: 3502 表示厦门</li>
 * <li>模块: 区分业务对象, 如: 0001表示卡对象</li>
 * <li>时间: 数据产生时间</li>
 * <li>机器: 数据产生时, 位于哪台机器上</li>
 * <li>进程: 产生数据的进程ID</li>
 * <li>增量: 在上述所有因素均相同时, 并发序数</li>
 * </lo>
 * <p>
 * <p>由16个字节构成, 分为以下段:</p>
 * <table border="1">
 * <caption>ObjectID layout</caption>
 * <tr>
 * <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td><td>15</td>
 * </tr>
 * <tr>
 * <td colspan="2">地区</td><td colspan="2">模块</td><td colspan="4">时间</td><td colspan="3">机器名称</td> <td colspan="2">进程ID</td><td colspan="3">并发量</td>
 * </tr>
 * </table>
 * <p>
 * <p>本类实例不可修改.</p>
 */
public final class ObjectId implements Comparable<ObjectId>, Serializable {

    private static final long serialVersionUID = 3503056789654483072L;

    private static final int BYTE_ARRAY_LENGTH = 16;

    private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;

    private static final short ADMIN_REGION_IDENTIFIER;
    private static final int MACHINE_IDENTIFIER;
    private static final short PROCESS_IDENTIFIER;
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final char[] HEX_CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final short adminRegionIdentifier;
    private final short bizModuleIdentifier;
    private final int timestamp;
    private final int machineIdentifier;
    private final short processIdentifier;
    private final int counter;

    /**
     * 创建一个新的ID对象.
     *
     * @return 新创建的ID对象.
     */
    public static ObjectId get(final short adminRegionIdentifier, final BizObject bizModuleIdentifier) {
        return new ObjectId(adminRegionIdentifier, bizModuleIdentifier);
    }

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
     * 获取产生该ID对象的机器标识.
     *
     * @return 机器标识的int表示.
     */
    public static int getGeneratedMachineIdentifier() {
        return MACHINE_IDENTIFIER;
    }

    /**
     * 获取产生该ID对象的进程标识.
     *
     * @return 进程ID.
     */
    public static int getGeneratedProcessIdentifier() {
        return PROCESS_IDENTIFIER;
    }

    /**
     * 创建一个ID对象.
     */
    public ObjectId(final short adminRegionIdentifier,
                    final BizObject bizModuleIdentifier) {
        this(adminRegionIdentifier, bizModuleIdentifier, new Date());
    }

    /**
     * 以日期初始化一个ID对象.
     *
     * @param date 日期对象.
     */
    public ObjectId(final short adminRegionIdentifier,
                    final BizObject bizModuleIdentifier,
                    final Date date) {
        this(adminRegionIdentifier, bizModuleIdentifier, dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(), false);
    }

    /**
     * 以日期与计数初始化一个ID对象.
     *
     * @param date    日期
     * @param counter 计数
     * @throws IllegalArgumentException 在计数值的高位为非零的情况下抛出.
     */
    public ObjectId(final short adminRegionIdentifier,
                    final BizObject bizModuleIdentifier,
                    final Date date,
                    final int counter) {
        this(adminRegionIdentifier, bizModuleIdentifier, date, MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, counter);
    }

    /**
     * 以日期, 机器ID, 进程ID以及计数初始化一个ID对象.
     *
     * @param date              the date
     * @param machineIdentifier the machine identifier
     * @param processIdentifier the process identifier
     * @param counter           the counter
     * @throws IllegalArgumentException 在机器ID或计数值的高位为非零的情况下抛出.
     */
    public ObjectId(final short adminRegionIdentifier,
                    final BizObject bizModuleIdentifier,
                    final Date date,
                    final int machineIdentifier,
                    final short processIdentifier,
                    final int counter) {
        this(adminRegionIdentifier, bizModuleIdentifier, dateToTimestampSeconds(date), machineIdentifier, processIdentifier, counter);
    }

    /**
     * 以EPO毫秒数, 机器ID, 进程ID以及计数初始化一个ID对象.
     *
     * @param timestamp         the time in seconds
     * @param machineIdentifier the machine identifier
     * @param processIdentifier the process identifier
     * @param counter           the counter
     * @throws IllegalArgumentException 在机器ID或计数值的高位为非零的情况下抛出.
     */
    public ObjectId(final short adminRegionIdentifier,
                    final BizObject bizModuleIdentifier,
                    final int timestamp,
                    final int machineIdentifier,
                    final short processIdentifier,
                    final int counter) {
        this(adminRegionIdentifier, bizModuleIdentifier, timestamp, machineIdentifier, processIdentifier, counter, true);
    }

    private ObjectId(final short adminRegionIdentifier,
                     final BizObject bizModuleIdentifier,
                     final int timestamp,
                     final int machineIdentifier,
                     final short processIdentifier,
                     final int counter,
                     final boolean checkCounter) {
        if ((machineIdentifier & 0xff000000) != 0) {
            throw new IllegalArgumentException("机器ID必须位于 0 和 16777215 之间(即三个字节).");
        }
        if (checkCounter && ((counter & 0xff000000) != 0)) {
            throw new IllegalArgumentException("计数值必须位于 0 和 16777215 之间(即三个字节).");
        }

        this.adminRegionIdentifier = adminRegionIdentifier;
        this.bizModuleIdentifier = bizModuleIdentifier.getBizObject();
        this.timestamp = timestamp;
        this.machineIdentifier = machineIdentifier;
        this.processIdentifier = processIdentifier;
        this.counter = counter & LOW_ORDER_THREE_BYTES;
    }

    /**
     * 根据一个30个字节的字符串构造ID对象.
     *
     * @param hexString the string to convert
     * @throws IllegalArgumentException if the string is not a valid hex string representation of an ObjectId
     */
    public ObjectId(final String hexString) {
        this(parseHexString(hexString));
    }

    /**
     * 以字节数组创建一个对象ID.
     *
     * @param bytes 字节数组
     * @throws IllegalArgumentException 在字节数组为空, 或长度小于 BYTE_ARRAY_LENGTH 的时候抛出.
     */
    public ObjectId(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        if (bytes.length != BYTE_ARRAY_LENGTH) {
            throw new IllegalArgumentException("need " + BYTE_ARRAY_LENGTH + " bytes");
        }

        adminRegionIdentifier = (short) makeInt((byte) 0, (byte) 0, bytes[0], bytes[1]);
        bizModuleIdentifier = (short) makeInt((byte) 0, (byte) 0, bytes[2], bytes[3]);
        timestamp = makeInt(bytes[4], bytes[5], bytes[6], bytes[7]);
        machineIdentifier = makeInt((byte) 0, bytes[8], bytes[9], bytes[10]);
        processIdentifier = (short) makeInt((byte) 0, (byte) 0, bytes[11], bytes[12]);
        counter = makeInt((byte) 0, bytes[13], bytes[14], bytes[15]);
    }

    /**
     * 转换为字节数组. 以 big-endian 顺序存储.
     *
     * @return the byte array
     */
    public byte[] toByteArray() {
        byte[] bytes = new byte[BYTE_ARRAY_LENGTH];
        bytes[0] = int1(adminRegionIdentifier);
        bytes[1] = int0(adminRegionIdentifier);
        bytes[2] = int1(bizModuleIdentifier);
        bytes[3] = int0(bizModuleIdentifier);
        bytes[4] = int3(timestamp);
        bytes[5] = int2(timestamp);
        bytes[6] = int1(timestamp);
        bytes[7] = int0(timestamp);
        bytes[8] = int2(machineIdentifier);
        bytes[9] = int1(machineIdentifier);
        bytes[10] = int0(machineIdentifier);
        bytes[11] = short1(processIdentifier);
        bytes[12] = short0(processIdentifier);
        bytes[13] = int2(counter);
        bytes[14] = int1(counter);
        bytes[15] = int0(counter);
        return bytes;
    }

    /**
     * 获取区域.
     *
     * @return 区域编码.
     */
    public short getAdminRegion() {
        return adminRegionIdentifier;
    }

    /**
     * 获取业务模块.
     *
     * @return 业务模块编码.
     */
    public short getBizModuleIdentifier() {
        return bizModuleIdentifier;
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
     * 获取机器ID.
     *
     * @return 机器ID
     */
    public int getMachineIdentifier() {
        return machineIdentifier;
    }

    /**
     * 获取进程ID.
     *
     * @return 进程ID
     */
    public short getProcessIdentifier() {
        return processIdentifier;
    }

    /**
     * 获取计数.
     *
     * @return 计数
     */
    public int getCounter() {
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

        ObjectId objectId = (ObjectId) o;

        if (adminRegionIdentifier != objectId.adminRegionIdentifier) {
            return false;
        }
        if (bizModuleIdentifier != objectId.bizModuleIdentifier) {
            return false;
        }
        if (counter != objectId.counter) {
            return false;
        }
        if (machineIdentifier != objectId.machineIdentifier) {
            return false;
        }
        if (processIdentifier != objectId.processIdentifier) {
            return false;
        }
        return timestamp == objectId.timestamp;

    }

    @Override
    public int hashCode() {
        int result = timestamp;
        result = 31 * result + adminRegionIdentifier;
        result = 31 * result + bizModuleIdentifier;
        result = 31 * result + machineIdentifier;
        result = 31 * result + (int) processIdentifier;
        result = 31 * result + counter;
        return result;
    }

    @Override
    public int compareTo(final ObjectId other) {
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
            ADMIN_REGION_IDENTIFIER = 3502;
            MACHINE_IDENTIFIER = createMachineIdentifier();
            PROCESS_IDENTIFIER = createProcessIdentifier();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // build a 2-byte machine piece based on NICs info
    private static int createMachineIdentifier() {
        int machinePiece;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);
                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException shortHardwareAddressException) { //NOPMD
                        // mac with less than 6 bytes. continue
                    }
                }
            }
            machinePiece = sb.toString().hashCode();
        } catch (Throwable t) {
            // IBM的JAVA虚拟机有时候会出错, 使用随机数代替之
            machinePiece = (new SecureRandom().nextInt());
            //LOGGER.log(Level.WARNING, "根据网卡信息创建机器ID失败, 使用随机数代替", t);
        }

        machinePiece = machinePiece & LOW_ORDER_THREE_BYTES;
        return machinePiece;
    }

    // 创建进程ID. 此ID在每个类加载器不必唯一, 因为 NEXT_COUNTER 会创建一个唯一值.
    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
            } else {
                processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }

        } catch (Throwable t) {
            processId = (short) new SecureRandom().nextInt();
            //LOGGER.log(Level.WARNING, "Failed to getEntity process identifier from JMX, using random number instead", t);
        }

        return processId;
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

    // Big-Endian 帮助函数, 因为MongoDB里的BSON数字是 litten-endian.
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

    private static byte int0(final int x) {
        return (byte) (x);
    }

    private static byte short1(final short x) {
        return (byte) (x >> 8);
    }

    private static byte short0(final short x) {
        return (byte) (x);
    }
}

