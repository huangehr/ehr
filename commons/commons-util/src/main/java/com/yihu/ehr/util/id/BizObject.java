package com.yihu.ehr.util.id;

/**
 * 业务对象划分, 全局唯一ID会使用此划分. 因为 ObjectId 中仅使用一个short作为
 * ID的一部分, 因为枚举值会在 0 ~ 2^16 - 1之间.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.06.19 14:38
 */
public enum BizObject {
    Geography((short) 0x0001),
    Organization((short) 0x0002),
    User((short) 0x0003),
    Card((short) 0x0004),
    JsonPackage((short) 0x0005),
    StdProfile((short) 0x0006),
    HealthEvent((short) 0x0007),
    ADAPTER((short) 0x0008),
    STANDARD((short) 0x0009),
    App((short) 0x00010),
    Dict((short) 0x0011),
    CdaType((short) 0x0012),
    Families((short) 0x0013),
    Members((short) 0x0014),
    Resources((short) 0x0015),
    Dimensions((short) 0x0016),
    DimensionsCategories((short) 0x0017),
    ResourceMetadata((short) 0x0018),
    AppResource((short) 0x0019),
    AppResourceMetadata((short) 0x0020),
    ResourceCategory((short) 0x0021),
    FileResource((short) 0x0022),
    RsMetadata((short) 0x0023),
    RsAdapterSchema((short) 0x0024),
    RsAdapterMetadata((short) 0x0025),
    RsDictionary((short) 0x0026),
    RsDictionaryEntry((short) 0x0027),
    RsSystemDictionary((short) 0x0028),
    RsSystemDictionaryEntry((short) 0x0029),
    RsInterface((short) 0x0030),
    RsAdapterDictionary((short) 0x0031),
    RsParam((short) 0x0032),
    RolesResource((short) 0x0033),
    RolesResourceMetadata((short) 0x0034),
    OrgResource((short) 0x0035),
    OrgResourceMetadata((short) 0x0036),
    ResourcesDefaultQuery((short) 0x0037);

    public short getBizObject() {
        return bizObject;
    }

    private short bizObject;

    private BizObject(short bizObject) {
        this.bizObject = bizObject;
    }

}
