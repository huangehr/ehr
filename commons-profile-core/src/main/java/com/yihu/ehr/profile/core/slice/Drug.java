package com.yihu.ehr.profile.core.slice;

import java.util.Date;

/**
 * 药品。
 *
 * @author Sand
 * @created 2016.04.25 16:56
 */
public class Drug {
    String name;
    String unit;
    String specification;
    Date lastUsedDate;          // 最近一次使用时间

    String last3Month;          // 最近三个月用量
    String last6Month;          // 最后六个月用量
}
