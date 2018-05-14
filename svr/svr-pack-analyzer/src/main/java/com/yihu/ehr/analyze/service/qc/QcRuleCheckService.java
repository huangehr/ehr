package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.HosAdminServiceClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Airhead
 * @created 2018-01-21
 */
@Service
public class QcRuleCheckService {
    private static final Logger logger = LoggerFactory.getLogger(QcRuleCheckService.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HosAdminServiceClient hosAdminServiceClient;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 检查值是否为空
     *
     * @param data
     */
    public void emptyCheck(String data) {
        DataElementValue value = null;
        try {
            value = parse(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        }

        logger.info("code:" + value.getCode() + ",value:" + value.getValue());

        Boolean isNullable = hosAdminServiceClient.isMetaDataNullable(value.getVersion(), value.getTable(), value.getCode());
        if (!isNullable && StringUtils.isEmpty(value.getValue())) {
            saveCheckResult(value, "E00001", "不能为空");
            logger.warn("code:" + value.getCode() + ",value:" + value.getValue());
        }
    }

    /**
     * 检查值类型是否正确，通过将值转为对应类型是否成功来判断
     * 暂未实现
     *
     * @param data
     */
    public void typeCheck(String data) {
        DataElementValue value = null;
        try {
            value = parse(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        }

        String type = hosAdminServiceClient.getMetaDataType(value.getVersion(), value.getTable(), value.getCode());
        switch (type) {
            case "L":
                if (!("F".equals(data) && "T".equals(data) && "0".equals(data) && "1".equals(data))) {
                    saveCheckResult(value, "E00003", "值类型错误");
                }

                break;
            case "N":
                if (!StringUtils.isNumeric(data)) {
                    saveCheckResult(value, "E00003", "值类型错误");
                }

                break;
            case "D":
            case "DT":
            case "T":
                if (DateUtil.strToDate(data) == null) {
                    saveCheckResult(value, "E00003", "值类型错误");
                }
                break;
            case "BY":
                break;
            // S1,S2,S3,BY
            default:
                break;
        }
    }

    /**
     * 检查数据格式是否正确，可通过正则表达式进行
     * 暂未实现
     *
     * @param data
     */
    public void formatCheck(String data) {
        DataElementValue value = null;
        try {
            value = parse(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        }

        String format = hosAdminServiceClient.getMetaDataFormat(value.getVersion(), value.getTable(), value.getCode());
        switch (format) {
            default:
                break;
        }
    }

    /**
     * 检查数据是否在值域范围
     *
     * @param data
     */
    public void valueCheck(String data) {
        DataElementValue value = null;
        try {
            value = parse(data);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        logger.info("code:" + value.getCode() + ",value:" + value.getValue());
        String dict = hosAdminServiceClient.getMetaDataDict(value.getVersion(), value.getTable(), value.getCode());
        if (StringUtils.isEmpty(dict) || dict.equals("0")) {
            return;
        }

        logger.info("code:" + value.getCode() + ",value:" + value.getValue() + ",dict:" + dict);
        Boolean isExist = hosAdminServiceClient.isDictCodeExist(value.getVersion(), dict, value.getCode());
        if (!isExist) {
            saveCheckResult(value, "E00002", "超出值域范围");
            logger.warn("code:" + value.getCode() + ",value:" + value.getValue() + ",dict:" + dict);
        }
    }

    private void saveCheckResult(DataElementValue value, String errorCode, String errorMsg) {
        Map<String, Object> map = new HashMap<>(12);
        map.put("rowKey", value.getRowKey());
        map.put("table", value.getTable());
        map.put("version", value.getVersion());
        map.put("code", value.getCode());
        map.put("value", value.getValue());
        map.put("orgCode", value.getOrgCode());
        map.put("patientId", value.getPatientId());
        map.put("eventNo", value.getEventNo());
        Date eventTime = DateUtil.strToDate(value.getEventTime());
        map.put("eventTime", eventTime);
        Date receiveTime = DateUtil.strToDate(value.getReceiveTime());
        map.put("receiveTime", receiveTime);
        map.put("errorCode", errorCode);
        map.put("errorMsg", errorMsg);

        try {
            elasticSearchUtil.index("qc", "receive_data_element", map);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private DataElementValue parse(String data) throws IOException {
        return objectMapper.readValue(data, DataElementValue.class);
    }

}
