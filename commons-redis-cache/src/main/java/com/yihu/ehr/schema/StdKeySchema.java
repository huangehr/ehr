package com.yihu.ehr.schema;

import com.yihu.ehr.redis.RedisClient;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

/**
 * 标准化数据数据缓存工具. 向Redis数据库初始化标准字典及数据元信息, 在档案入库的时候可用于翻译字典, 按版本缓存.
 * <p>
 * 要清除某个版本的缓存, 直接使用版本ID即可清除.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 16:53
 */
@Component
public class StdKeySchema {
    static String DataSetQuery = "SELECT code, name FROM data.set.table";
    static String MetaDataQuery = "SELECT a.code AS data_set_code, b.inner_code, b.type, b.dict_id FROM data.set.table a, " +
            "meta.data.table b WHERE a.id = b.dataset_id";
    static String DictEntryQuery = "SELECT t.dict_id, t.code, t.value FROM dict.entry.table t";
    
    @Autowired
    StdKeySchema keySchema;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${redis-key-schema.std.}")
    private String DataSetTable = "std_data_set_";

    @Value("${redis-key-schema.std.}")
    private String MetaDataTable = "std_meta_data_";

    @Value("${redis-key-schema.std.}")
    private String DictEntryTable = "std_dictionary_entry_";

    /**
     * 获取数据元.
     *
     * @param innerVersion
     * @param dataSetCode
     * @param metaDataInnerCode
     * @return
     */
    public String getMetaDataType(String innerVersion, String dataSetCode, String metaDataInnerCode) {
        String key = keySchema.metaDataType(innerVersion, dataSetCode, metaDataInnerCode);

        return (String)redisTemplate.opsForHash().get(key, key.hashCode());
    }

    public long getMetaDataDict(String version, String dataSetCode, String metaDataInnerCode){
        String key = keySchema.metaDataDict(version, dataSetCode, metaDataInnerCode);

        Object object = redisTemplate.opsForHash().get(key, key.hashCode());
        return object == null ? 0 : (long)object;
    }

    /**
     * 获取字典项的缓存值.
     *
     * @param innerVersion
     * @param dictId
     * @param entryCode
     * @return
     */
    public String getDictEntryValue(String innerVersion, long dictId, String entryCode) {
        String key = keySchema.dictEntryValue(innerVersion, dictId, entryCode);

        return (String)redisTemplate.opsForHash().get(key, key.hashCode());
    }

    public boolean isCached(String version) {
        return redisTemplate.hasKey(keySchema.versionName(version));
    }
}
