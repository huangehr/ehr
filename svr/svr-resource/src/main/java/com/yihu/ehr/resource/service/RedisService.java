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

    public String getRsMetaData(String key) {
        return rsMetadataKeySchema.get(key);
    }

    //获取其他标准的数据元
    public String getOtherStdMetadata(String code) {
        if(null == code) {
            return null;
        }
        return null;
    }

    //获取其他标准的数据元对应的平台标准的数据元
    public String transformMetadata(String otherStdMetadata) {
        if(null == otherStdMetadata) {
            return null;
        }
        return null;
    }

    //获取其他标准的数据元对应的名称
    public String transformMetadataName(String otherStdMetadata) {
        if(null == otherStdMetadata) {
            return null;
        }
        return null;
    }

    public String getDataSetName(String version, String id) {
        return stdDataSetKeySchema.dataSetName(version, id);
    }

}
