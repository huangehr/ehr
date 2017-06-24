package com.yihu.ehr.analysis.listener;

import com.yihu.ehr.analysis.model.BusinessDataModel;
import com.yihu.ehr.analysis.model.OperatorDataModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by Administrator on 2017/2/6.
 */
public class LabelDataListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MongoTemplate mongoTemplate;

    private static String mongoDb_Business_TableName = "EHR_BUSINESS_LOG";
    private static String mongoDb_Operator_TableName = "EHR_OPERATOR_LOG";

    private void saveLogToMongo(String logType, JSONObject jsonObject) throws Exception {
        switch (logType) {
            case "1": {
                //业务日志
                insertMongo(OperatorDataModel.getByJsonObject(jsonObject),mongoDb_Operator_TableName);
                break;
            }
            case "2": {
                //操作日志
                insertMongo(BusinessDataModel.getByJsonObject(jsonObject),mongoDb_Business_TableName);
                break;
            }
        }
    }

    private void insertMongo(Object data, String tableName) {
        mongoTemplate.insert(
                data, tableName
        );
    }
//    @Scheduled(fixedRate=20000)//每20秒执行一次。开始
//    public void testTasks() {
//    }
}