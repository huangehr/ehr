package com.yihu.ehr.constrant;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 15:37
 */
public enum ArchiveStatus {
    Received,                   // 已缓存
    Acquired,                   // 正在入库库
    Failed,                     // 入库失败
    Finished,                   // 已入库
    InDoubt;                    // 未能入库的档案
}
