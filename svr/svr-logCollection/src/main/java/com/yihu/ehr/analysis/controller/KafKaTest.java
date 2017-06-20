package com.yihu.ehr.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2017/2/6.
 */
@RestController
public class KafKaTest {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping("/produce")
    public void contextLoads() throws InterruptedException {
        System.out.println("发送消息开始");
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("topicTest", "ABC");
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("success");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("failed");
            }
        });
        System.out.println(Thread.currentThread().getId());

    }
}
