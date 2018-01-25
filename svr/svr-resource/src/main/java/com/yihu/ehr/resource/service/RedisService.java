package com.yihu.ehr.resource.service;



import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RsMetadataKeySchema rsMetadataKeySchema;
    @Autowired
    private StdDataSetKeySchema stdDataSetKeySchema;
    @Autowired
    private RsAdapterMetaKeySchema rsAdapterMetaKeySchema;

    public String getRsMetaData(String key) {
        return rsMetadataKeySchema.get(key);
    }

    public String getDataSetName(String version, String id) {
        return stdDataSetKeySchema.dataSetName(version, id);
    }

    public String getRsAdapterMetaData(String cdaVersion, String dictCode, String srcDictEntryCode) {
        return rsAdapterMetaKeySchema.getMetaData(cdaVersion, dictCode, srcDictEntryCode);
    }

}
