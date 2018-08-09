package com.yihu.ehr.profile;

/**
 * Created by progr1mmer on 2018/5/23.
 */
public enum AnalyzeStatus {
    Received,                   // 0待质控, 未加入消息队列
    Acquired,                   // 1正在质控/等待质控(已加入消息队列)
    Failed,                     // 2质控失败
    Finished                   // 3质控完成
}
