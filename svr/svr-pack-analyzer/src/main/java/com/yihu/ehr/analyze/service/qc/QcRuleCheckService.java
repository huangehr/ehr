package com.yihu.ehr.analyze.service.qc;

import com.yihu.ehr.analyze.service.RedisService;
import com.yihu.ehr.profile.ErrorType;
import com.yihu.ehr.profile.exception.IllegalEmptyCheckException;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalValueCheckException;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Airhead
 * @created 2018-01-21
 */
@Service
public class QcRuleCheckService {

    @Autowired
    private RedisService redisService;

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
        //String nullable = redisClient.get(makeKey("std_data_set_" + version, dataSetCode + "." + metadata, "nullable"));
        if (StringUtils.isBlank(value)) {
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
    public ErrorType emptyCheckThrowable (String version, String dataSetCode, String metadata, String value) throws Exception {
        ErrorType errorType = this.emptyCheck(version, dataSetCode, metadata, value);
        if (ErrorType.Normal != errorType) {
            throw new IllegalEmptyCheckException(String.format("Meta data %s of %s in %s is empty", metadata, dataSetCode, version));
        }
        return ErrorType.Normal;
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
    public ErrorType typeCheck (String version, String dataSetCode, String metadata, String value) throws Exception {
        String type = redisService.getMetaDataType(version, dataSetCode, metadata);
        if (StringUtils.isBlank(type)) {
            return ErrorType.Normal;
        }
        switch (type) {
            case "D":
                if (value != null) {
                    if (!value.contains("T") && !value.contains("Z")) {
                        StringBuilder error = new StringBuilder();
                        error.append("Invalid date time format ")
                                .append(dataSetCode)
                                .append(" ")
                                .append(metadata)
                                .append(" ")
                                .append(value)
                                .append(" for std version ")
                                .append(version)
                                .append(".");
                        throw new IllegalJsonDataException(error.toString());
                    }
                }
                return ErrorType.Normal;
            case "DT":
                if (value != null) {
                    if (!value.contains("T") && !value.contains("Z")) {
                        StringBuilder error = new StringBuilder();
                        error.append("Invalid date time format ")
                                .append(dataSetCode)
                                .append(" ")
                                .append(metadata)
                                .append(" ")
                                .append(value)
                                .append(" for std version ")
                                .append(version)
                                .append(".");
                        throw new IllegalJsonDataException(error.toString());
                    }
                }
                return ErrorType.Normal;
            case "L":
                if (!("F".equals(value) && "T".equals(value) && "0".equals(value) && "1".equals(value))) {
                    return ErrorType.TypeError;
                }
                return ErrorType.Normal;
            case "N":
                if (!StringUtils.isNumeric(value)) {
                    return ErrorType.TypeError;
                }
                return ErrorType.Normal;
            case "T":
                if (DateUtil.strToDate(value) == null) {
                    return ErrorType.TypeError;
                }
                return ErrorType.Normal;
            case "BY":
                return ErrorType.Normal;
            // S1,S2,S3,BY
            default:
                return ErrorType.Normal;
        }
    }

    /**
     *
     * @param version
     * @param dataSetCode
     * @param metadata
     * @param value
     * @return
     * @throws Exception
     */
    public ErrorType formatCheck (String version, String dataSetCode, String metadata, String value) throws Exception {
        String type = redisService.getMetaDataType(version, dataSetCode, metadata);
        if (StringUtils.isBlank(type)) {
            return ErrorType.Normal;
        }
        switch (type) {
            case "D":
                if (value != null) {
                    if (!value.contains("T") && !value.contains("Z")) {
                        StringBuilder error = new StringBuilder();
                        error.append("Invalid date time format ")
                                .append(dataSetCode)
                                .append(" ")
                                .append(metadata)
                                .append(" ")
                                .append(value)
                                .append(" for std version ")
                                .append(version)
                                .append(".");
                        throw new IllegalJsonDataException(error.toString());
                    }
                }
                return ErrorType.Normal;
            case "DT":
                if (value != null) {
                    if (!value.contains("T") && !value.contains("Z")) {
                        StringBuilder error = new StringBuilder();
                        error.append("Invalid date time format ")
                                .append(dataSetCode)
                                .append(" ")
                                .append(metadata)
                                .append(" ")
                                .append(value)
                                .append(" for std version ")
                                .append(version)
                                .append(".");
                        throw new IllegalJsonDataException(error.toString());
                    }
                }
                return ErrorType.Normal;
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
    public ErrorType valueCheck (String version, String dataSetCode, String metadata, String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            return ErrorType.ValueError;
        }
        String dictId = redisService.getMetaDataDict(version, dataSetCode, metadata);
        if (StringUtils.isNotBlank(dictId)) {
            String _value = redisService.getDictEntryValue(version, dictId, value);
            if (StringUtils.isBlank(_value)) {
                return ErrorType.ValueError;
            }
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
    public ErrorType valueCheckThrowable (String version, String dataSetCode, String metadata, String value) throws Exception {
        ErrorType type = this.valueCheck(version, dataSetCode, metadata, value);
        if (type != ErrorType.Normal) {
            throw new IllegalValueCheckException(String.format("Value %s for meta data %s of %s in %s out of range", value, metadata, dataSetCode, version));
        }
        return type;
    }

}
