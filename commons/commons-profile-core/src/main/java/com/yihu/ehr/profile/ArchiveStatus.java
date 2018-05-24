package com.yihu.ehr.profile;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 15:37
 */
public enum ArchiveStatus {
    Received,                   // 0待入库
    Acquired,                   // 1正在入库库
    Failed,                     // 2入库失败
    Finished                   // 3已入库
    //LegacyIgnored;              // 未能入库的档案
}
