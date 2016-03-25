package com.yihu.ehr.cache;

import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.schema.XKeySchema;
import com.yihu.ehr.standard.version.service.CDAVersion;
import com.yihu.ehr.standard.version.service.CDAVersionService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

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
public class StdCache {
    XKeySchema keySchema;

    static String DataSetQuery = "SELECT code, name FROM data.set.table";
    static String MetaDataQuery = "SELECT a.code AS data_set_code, b.inner_code, b.type, b.dict_id FROM data.set.table a, " +
            "meta.data.table b WHERE a.id = b.dataset_id";
    static String DictEntryQuery = "SELECT t.dict_id, t.code, t.value FROM dict.entry.table t";

    //@Autowired
    //StdKeySchema keySchema;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${redis-key-schema.std.}")
    private String DataSetTable = "std_data_set_";

    @Value("${redis-key-schema.std.}")
    private String MetaDataTable = "std_meta_data_";

    @Value("${redis-key-schema.std.}")
    private String DictEntryTable = "std_dictionary_entry_";

    @Autowired
    EntityManager entityManager;

    @Autowired
    CDAVersionService versionService;

    /**
     * 缓存指定版本的数据元. 此处获取数据绕过了标准化的数据模型, 直接使用原始SQL提高效率.
     *
     * @param version 内部版本号
     */
    public void loadStdData(String version, boolean force) {
        String versionKey = keySchema.makeKey(version);
        if (force) {
            redisTemplate.opsForHash().get(versionKey, versionKey.hashCode());
        }

        Session session = entityManager.unwrap(Session.class);
        try {
            String dataSetTable = CDAVersionUtil.getDataSetTableName(version);
            String metaDataTable = CDAVersionUtil.getMetaDataTableName(version);
            String dictEntryTable = CDAVersionUtil.getDictEntryTableName(version);
            Query query = null;

            // 缓存数据元
            {
                query = session.createSQLQuery(MetaDataQuery.replace("data.set.table", dataSetTable).replace("meta.data.table", metaDataTable));
                List<Object[]> metaDataList = query.list();
                for (Object[] record : metaDataList) {
                    String dataSetCode = (String) record[0];
                    String innerCode = (String) record[1];
                    String type = (String) record[2];
                    long dictId = (Integer) record[3];

                    String metaDataTypeKey = keySchema.metaDataType(version, dataSetCode, innerCode);
                    String metaDataDictKey = keySchema.metaDataDict(version, dataSetCode, innerCode);

                    redisTemplate.opsForHash().put(metaDataTypeKey, metaDataTypeKey.hashCode(), type);
                    redisTemplate.opsForHash().put(metaDataDictKey, metaDataDictKey.hashCode(), dictId);
                }
            }

            // 缓存字典项
            {
                query = session.createSQLQuery(DictEntryQuery.replace("dict.entry.table", dictEntryTable));
                List<Object[]> entryList = query.list();
                for (Object[] record : entryList) {
                    long dictId = (Integer) record[0];
                    String code = (String) record[1];
                    String value = (String) record[2];

                    String entryValueKey = keySchema.dictEntryValue(version, dictId, code);
                    redisTemplate.opsForHash().put(entryValueKey, entryValueKey.hashCode(), value);
                }
            }

            redisTemplate.opsForHash().put(versionKey, versionKey.hashCode(), version);
        } finally {
            session.close();
        }

    }

    /**
     * 清除标准数据缓存.
     *
     * @param version
     */
    public void clearStdData(String version) {
        String versionKey = keySchema.versionName(version);

        redisTemplate.delete(versionKey);
        redisTemplate.delete(DataSetTable + version + "*");
        redisTemplate.delete(MetaDataTable + version + "*");
        redisTemplate.delete(DictEntryTable + version + "*");
    }

    /**
     * 重新加载标准数据缓存.
     *
     * @param version
     */
    public void reloadStdCache(String version) {
        clearStdData(version);
        loadStdData(version, true);
    }

    public boolean isCached(String version) {
        return redisTemplate.hasKey(keySchema.versionName(version));
    }
}
