package com.yihu.ehr.constants;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 15:37
 */
public enum ArchiveStatus {
    Received,                   // 0已缓存
    Acquired,                   // 1正在入库库
    Failed,                     // 2入库失败
    Finished                   // 3已入库
    //LegacyIgnored;              // 未能入库的档案
}
