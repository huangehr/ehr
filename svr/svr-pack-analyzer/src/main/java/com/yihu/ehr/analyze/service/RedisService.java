package com.yihu.ehr.analyze.service;

import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Redis管理
 * @author hzp add at 20170425
 */
@Service
public class RedisService {

    @Autowired
    private OrgKeySchema orgKeySchema;
    @Autowired
    private StdMetaDataKeySchema stdMetaDataKeySchema;

    /**
     *获取机构名称redis
     * @return
     */
    public String getOrgName(String key) {
       return orgKeySchema.get(key);
    }

    /**
     *获取机构区域redis
     * @return
     */
    public String getOrgArea(String key) {
        return orgKeySchema.getOrgArea(key);
    }

    /**
     * 获取标准数据元对应类型 redis
     */
    public String getMetaDataType(String version, String dataSetCode, String innerCode) {

        return stdMetaDataKeySchema.metaDataType(version, dataSetCode, innerCode);
    }

    /**
     * 获取数据元格式
     *
     * @param version
     * @param dataSetCode
     * @param innerCode
     * @return
     */
    public String getMetaDataFormat(String version, String dataSetCode, String innerCode) {

        return stdMetaDataKeySchema.metaDataFormat(version, dataSetCode, innerCode);
    }

    /**
     * 获取标准数据元对应字典 redis
     */
    public String getMetaDataDict(String version, String dataSetCode, String innerCode) {
        return stdMetaDataKeySchema.metaDataDict(version, dataSetCode,innerCode);
    }

    /**
     * 获取标准数据字典对应值 redis
     */
    public String getDictEntryValue(String version, String dictId, String entryCode) {

        return stdMetaDataKeySchema.dictEntryValue(version, dictId , entryCode);
    }
}
