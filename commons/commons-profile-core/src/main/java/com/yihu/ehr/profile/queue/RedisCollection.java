package com.yihu.ehr.profile.queue;

/**
 * Created by progr1mmer on 2017/12/18.
 */
public class RedisCollection {
    //主质控队列
    public static final String AnalyzeQueue = "analyze_queue";
    //次质控队列
    public static final String AnalyzeQueueVice = "analyze_queue_vice";
    //主解析队列
    public static final String ResolveQueue = "resolve_queue";
    //次解析队列
    public static final String ResolveQueueVice = "resolve_queue_vice";
    //省平台
    public static final String ProvincialPlatformQueue = "provincial_platform_queue";
    /**省平台补传队列 */
    public static final String PROVINCIAL_PLATFORM_QUEUE_SUPPLEMENT= "provincial_platform_queue_supplement";

    /**
     * 待发布的消息缓存集合
     */
    public static final String PUB_WAITING_MESSAGES = "pub_waiting_messages";

    /**
     * 订阅失败的消息缓存集合
     */
    public static final String SUB_FAILED_MESSAGES = "sub_failed_messages";

    /**
     * 订阅成功的消息缓存集合
     */
    public static final String SUB_SUCCESSFUL_MESSAGES = "sub_successful_messages";

}
