package com.yihu.ehr.constants;

/**
 * 业务对象划分, 全局唯一ID会使用此划分. 因为 ObjectId 中仅使用一个short作为
 * ID的一部分, 因为枚举值会在 0 ~ 2^16 - 1之间.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.06.19 14:38
 */
public enum BizObject {
    Geography((short)0x0001),
    Organization((short)0x0002),
    User((short)0x0003),
    Card((short)0x0004),
    JsonPackage((short)0x0005),
    StdProfile((short)0x0006),
    HealthEvent((short)0x0007),
    ADAPTER((short)0x0008),
    STANDARD((short)0x0009),
    App((short)0x00010),
    Dict((short)0x0011),
    CdaType((short)0x0012),
    Families((short)0x0013),
    Members((short)0x0014);

    public short getBizObject(){
        return bizObject;
    }

    private short bizObject;
    private BizObject(short bizObject){
        this.bizObject = bizObject;
    }
}
