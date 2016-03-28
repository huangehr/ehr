package com.yihu.ehr.model.profile;

import com.yihu.ehr.util.StringBuilderUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>健康档案ID</p>
 * <p>
 *   ID生成原则:
 *   <lo>
 *        <li>机构代码: 数据产生机构的代码的hash代码</li>
 *        <li>病人机构内信息系统ID</li>
 *        <li>病人机构内信息系统事件号/流水号</li>
 *        <li>时间: 病人挂号、出院或体检时间</li>
 *   </lo>
 * <p>
 * <p>由16个字节构成, 分为以下段:</p>
 * <table border="1">
 * <caption>ProfileId layout</caption>
 * <tr>
 * <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td><td>15</td><td>16</td><td>13</td><td>14</td><td>15</td><td>16</td><td>17</td><td>18</td><td>19</td>
 * </tr>
 * <tr>
 * <td colspan="4">机构代码hash</td><td colspan="4">身份证号hash</td><td colspan="4">并发量</td> <td colspan="8">时间戳</td>
 * </tr>
 * </table>
 * <p>
 * <p>本类实例不可修改.</p>
 */
public final class ProfileId implements Comparable<ProfileId>, Serializable {
    private static final long serialVersionUID = 5121234589654483072L;

    private final static Pattern Pattern = java.util.regex.Pattern.compile("([\\da-zA-Z\\-]+)_([\\da-zA-Z\\-]+)_([\\da-zA-Z\\-]+)_(\\d+)");

    private final String orgCode;
    private final String patientId;
    private final String eventNo;
    private final long timestamp;

    /**
     * 创建一个新的ID对象.
     *
     * @return 新创建的ID对象.
     */
    public static ProfileId get(final String orgId, final String patientId, final String eventNo, final Date timestamp) {
        return new ProfileId(orgId, patientId, eventNo, timestamp);
    }

    public ProfileId(final String orgCode, final String patientId, final String eventNo, final Date timestamp) {
        this.orgCode = orgCode;
        this.patientId = patientId;
        this.eventNo = eventNo;
        this.timestamp = timestamp.getTime();
    }

    /**
     * 检查是否为一个合法的 ObjectId 对象.
     *
     * @param id 一个可能转换为对象ID的字符串.
     * @return 字符串是否为合法的ObjectId.
     * @throws IllegalArgumentException hexString为空的情况下抛出.
     */
    public static boolean isValid(final String id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        Matcher matcher = Pattern.matcher(id);
        return matcher.matches() && matcher.groupCount() == 4;
    }

    /**
     *
     * @param id the string to convert
     * @throws IllegalArgumentException if the string is not a valid hex string representation of an ObjectId
     */
    public ProfileId(final String id) {
        Matcher matcher = Pattern.matcher(id);
        if(matcher.find() && matcher.groupCount() == 4){
            orgCode = matcher.group(1);
            patientId = matcher.group(2);
            eventNo = matcher.group(3);
            timestamp = Long.parseLong(matcher.group(4));
        } else {
            throw new IllegalArgumentException("无效ID");
        }
    }

    /**
     * 获取时间戳代表的日期对象.
     *
     * @return 日期对象
     */
    public Date getDate() {
        return new Date(timestamp);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProfileId objectId = (ProfileId)o;

        if (orgCode != objectId.orgCode) {
            return false;
        }

        if (patientId != objectId.patientId) {
            return false;
        }

        if (eventNo != objectId.eventNo) {
            return false;
        }

        if (timestamp != objectId.timestamp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgCode, patientId, eventNo, timestamp);
    }

    @Override
    public int compareTo(final ProfileId other) {
        if (other == null) {
            throw new NullPointerException();
        }

        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return new StringBuilderUtil("%1_%2_%3_%4")
                .arg(orgCode)
                .arg(patientId)
                .arg(eventNo)
                .arg(timestamp)
                .toString();
    }
}

