package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Airhead
 * @created 2018-01-21
 */
@Service
public class QcRuleCheckService {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RedisServiceClient redisServiceClient;

    private void saveCheckResult(DataElementValue value, String checkInfo) {

    }

    /**
     * 检查值是否为空
     *
     * @param data
     */
    public void emptyCheck(String data) {
        try {
            DataElementValue value = parse(data);
            Boolean isNullable = redisServiceClient.isMetaDataNullable(value.getVersion(), value.getTable(), value.getCode());
            if (!isNullable && StringUtils.isEmpty(value.getValue())) {
                saveCheckResult(value, "不能为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 检查值类型是否正确，通过将值转为对应类型是否成功来判断
     * 暂未实现
     *
     * @param data
     */
    public void typeCheck(String data) {
        try {
            DataElementValue value = parse(data);
            String type = redisServiceClient.getMetaDataType(value.getVersion(), value.getTable(), value.getCode());
            switch (type) {
                case "L":
                    break;
                case "N":
                    break;
                case "D":
                    break;
                case "DT":
                    break;
                case "T":
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查数据格式是否正确，可通过正则表达式进行
     * 暂未实现
     *
     * @param data
     */
    public void formatCheck(String data) {
    }

    /**
     * 检查数据是否在值域范围
     *
     * @param data
     */
    public void valueCheck(String data) {
        try {
            DataElementValue value = parse(data);
            String dict = redisServiceClient.getMetaDataDict(value.getVersion(), value.getTable(), value.getCode());
            if (StringUtils.isEmpty(dict) || dict.equals("0")) {
                return;
            }

            Boolean isExist = redisServiceClient.isDictCodeExist(value.getVersion(), dict, value.getCode());
            if (!isExist) {
                saveCheckResult(value, "超出值域范围");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataElementValue parse(String data) throws IOException {
        return objectMapper.readValue(data, DataElementValue.class);
    }

}
