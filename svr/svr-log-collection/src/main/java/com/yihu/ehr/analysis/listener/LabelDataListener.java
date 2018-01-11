package com.yihu.ehr.analysis.listener;

import com.yihu.ehr.analysis.listener.save.LogSaver;
import com.yihu.ehr.analysis.listener.save.impl.ESLogSaver;
import com.yihu.ehr.analysis.model.BusinessDataModel;
import com.yihu.ehr.analysis.model.OperatorDataModel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Administrator on 2017/2/6.
 */
public class LabelDataListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private OperatorDataModel operatorDataModel;
    @Autowired
    private BusinessDataModel businessDataModel;

    private LogSaver logSaver;

    public static String mongoDb_Business_TableName = "cloud_business_log";
    public static String mongoDb_Operator_TableName = "cloud_operator_log";

    //@Scheduled(cron = "0 0/1 * * * ?") //每分钟执行一次
    //正式库的 topic名字是flumeLog
    @KafkaListener(topics = "flumeLog_ehr")
    @Transactional
    public void labelData(ConsumerRecord<?, ?> record) {
        Long startTime = System.currentTimeMillis();
        System.out.println("Kafka开始消费:" + record.topic() + " - " + record.toString());
        logger.debug("Kafka开始消费");
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            try {
                JSONObject jsonObject = new JSONObject(message.toString());
                if (jsonObject.has("logType")) {
                    String logType = String.valueOf(jsonObject.get("logType"));

                    // 根据日志类别保存到mongodb
                    saveLogToMongo(logType, jsonObject);

                    Long endTime = System.currentTimeMillis();
                    Long time = startTime - endTime;
                    logger.debug("time(ms):" + time);
                    logger.debug("保存成功 message:" + message);
                } else {
                    logger.error("数据格式错误,message:" + message);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        logger.debug("Kafka结束消费");
    }

    private void saveLogToMongo(String logType, JSONObject jsonObject) throws Exception {
        switch (logType) {
            case "1": {
                //统一网关的日志
                logSaver.save(operatorDataModel.getByJsonObject(jsonObject), mongoDb_Operator_TableName);
                break;
            }
            case "3": {
                //云平台后台业务操作日志
                logSaver.save(businessDataModel.getByJsonObject(jsonObject), mongoDb_Business_TableName);
                break;
            }
            case "2": {
                //采集日志
                logSaver.save(businessDataModel.getByJsonObject(jsonObject), mongoDb_Business_TableName);
                break;
            }
        }

    }

    public LogSaver getLogSaveManager() {
        return logSaver;
    }

    public void setLogSaveManager(LogSaver logSaver) {
        this.logSaver = logSaver;
    }

//    @Scheduled(fixedRate=20000)//每20秒执行一次。开始
//    public void testTasks() {
//    }
}