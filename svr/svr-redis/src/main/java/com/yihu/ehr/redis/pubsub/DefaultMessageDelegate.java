package com.yihu.ehr.redis.pubsub;

import org.apache.log4j.Logger;

/**
 * Redis 订阅发布的消息代理
 *
 * @author 张进军
 * @date 2017/11/3 10:51
 */
public class DefaultMessageDelegate implements MessageDelegate {

    Logger logger = Logger.getLogger(DefaultMessageDelegate.class);

//    @Autowired
//    RestTemplate restTemplate;

    private String subscriberUrl; // 订阅者回调服务地址

    public DefaultMessageDelegate(String subscriberUrl) {
        this.subscriberUrl = subscriberUrl;
    }

    @Override
    public void handleMessage(String message, String channel) {
        logger.info("\n--- log ---\n url: " + subscriberUrl + ", channel: " + channel + ", message: " + message);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        HttpEntity<String> entity = new HttpEntity<>(message, headers);
//        restTemplate.exchange(subscriberUrl, HttpMethod.POST, entity, String.class);
    }

}
