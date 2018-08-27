package com.yihu.ehr.profile.qualilty;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  质控- 数据展示类型
 * @author HZY
 * @created 2018/8/22 13:44
 */
public enum DqDataType {
    complete(0),          // 完整性
    imTime(1),            // 及时性
    correct(2);           // 准确性

    int type;

    DqDataType(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return type;
    }

    public static DqDataType create(String ordinal) {
        return create(Integer.parseInt(ordinal));
    }

    public static DqDataType create(int ordinal) {
        return DqDataType.values()[ordinal];
    }
}