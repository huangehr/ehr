package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.HosAdminServiceClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.profile.ErrorType;
import com.yihu.ehr.profile.exception.IllegalEmptyCheckException;
import com.yihu.ehr.profile.exception.IllegalValueCheckException;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.string.StringBuilderEx;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    @Autowired
    private RedisClient redisClient;

    /**
     * 检查值是否为空
     *
     * @param version
     * @param dataSetCode
     * @param metadata
     * @param value
     * @throws Exception
     */
    public ErrorType emptyCheck(String version, String dataSetCode, String metadata, String value) throws Exception {
        String nullable = redisClient.get(makeKey("std_data_set_" + version, dataSetCode + "." + metadata, "nullable"));
        if (!"1".equals(nullable) && StringUtils.isEmpty(value)) {
            return ErrorType.EmptyError;
        }
        return ErrorType.Normal;
    }

    /**
     * 检查值是否为空
     *
     * @param version
     * @param dataSetCode
     * @param metadata
     * @param value
     * @throws Exception
     */
    public ErrorType emptyCheckThrowable(String version, String dataSetCode, String metadata ,String value) throws Exception {
        ErrorType type = emptyCheck(version, dataSetCode, metadata, value);
        if (type != ErrorType.Normal){
            throw new IllegalEmptyCheckException(String.format("Meta data %s of %s in %s is empty", metadata, dataSetCode, version));
        }
        return type;
    }

    /**
     * 检查值类型是否正确，通过将值转为对应类型是否成功来判断
     * 暂未实现
     *
     * @param version
     * @param dataSetCode
     * @param metadata
     * @param value
     */
    public ErrorType typeCheck (String version, String dataSetCode, String metadata ,String value) throws Exception {
        String type = hosAdminServiceClient.getMetaDataType(version, dataSetCode, metadata);
        switch (type) {
            case "L":
                if (!("F".equals(value) && "T".equals(value) && "0".equals(value) && "1".equals(value))) {
                    return ErrorType.TypeError;
                }
                break;
            case "N":
                if (!StringUtils.isNumeric(value)) {
                    return ErrorType.TypeError;
                }
                break;
            case "D":
            case "DT":
            case "T":
                if (DateUtil.strToDate(value) == null) {
                    return ErrorType.TypeError;
                }
                break;
            case "BY":
                break;
            // S1,S2,S3,BY
            default:
                break;
        }
        return ErrorType.Normal;
    }

    /**
     * 检查数据格式是否正确，可通过正则表达式进行
     * 暂未实现
     *
     * @param data
     */
    public ErrorType formatCheck(String data) throws Exception {
        DataElementValue value = parse(data);
        String format = hosAdminServiceClient.getMetaDataFormat(value.getVersion(), value.getTable(), value.getCode());
        switch (format) {
            default:
                return ErrorType.Normal;
        }
    }

    /**
     * 检查数据是否在值域范围
     *
     * @param version
     * @param dataSetCode
     * @param metadata
     * @param value
     * @throws Exception
     */
    public ErrorType valueCheck(String version, String dataSetCode, String metadata ,String value) throws Exception {
        String dict = redisClient.get(makeKey("std_meta_data_" + version,dataSetCode + "." + metadata,"dict_id"));
        if (StringUtils.isEmpty(dict) || dict.equals("0")) {
            return ErrorType.Normal;
        }
        Boolean isExist =  redisClient.hasKey(makeKey("std_dictionary_entry_" + version, dict + "." + value, "value"));
        if (!isExist) {
            return ErrorType.ValueError;
        }
        return ErrorType.Normal;
    }

    /**
     * 检查值是否为空
     *
     * @param version
     * @param dataSetCode
     * @param metadata
     * @param value
     * @throws Exception
     */
    public ErrorType valueCheckThrowable(String version, String dataSetCode, String metadata , String value) throws Exception {
        ErrorType type = valueCheck(version, dataSetCode, metadata, value);
        if (type != ErrorType.Normal) {
            throw new IllegalValueCheckException(String.format("Value %s for meta data %s of %s in %s out of range", value, metadata, dataSetCode, version));
        }
        return type;
    }

    private void saveCheckResult(DataElementValue value, String errorCode, String errorMsg) throws Exception {
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
        elasticSearchUtil.index("qc", "receive_data_element", map);
    }

    private DataElementValue parse(String data) throws IOException {
        return objectMapper.readValue(data, DataElementValue.class);
    }

    /**
     * 获取key
     */
    public String makeKey(String table, String key, String column) {
        return new StringBuilderEx("%1:%2:%3")
                .arg(table)
                .arg(key)
                .arg(column)
                .toString();
    }

}
