package com.yihu.ehr.standard;

import com.yihu.ehr.config.StdSessionFactoryBean;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.standard.version.service.CDAVersion;
import com.yihu.ehr.standard.version.service.CDAVersionService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    static String DataSetQuery = "SELECT code, name FROM data.set.table";
    static String MetaDataQuery = "SELECT a.code AS data_set_code, b.inner_code, b.type, b.dict_id FROM data.set.table a, " +
            "meta.data.table b WHERE a.id = b.dataset_id";
    static String DictEntryQuery = "SELECT t.dict_id, t.code, t.value FROM dict.entry.table t";

    @Autowired
    RedisClient redisClient;

    @Autowired
    StdKeySchema keySchema;

    @Autowired
    protected StdSessionFactoryBean localSessionFactoryBean;

    @Autowired
    CDAVersionService versionService;

    public List<MCDAVersion> versions(){
        Set<String> keys = redisClient.keys(keySchema.versionName("*"));
        List<MCDAVersion> versions = new ArrayList<>(keys.size());

        for (String key : keys){
            MCDAVersion version = new MCDAVersion();
            version.setVersion(key.split(":")[1]);
            version.setVersionName(redisClient.get(key));

            versions.add(version);
        }

        return versions;
    }

    public MCDAVersion version(String version){
        String name = redisClient.get(keySchema.versionName(version));
        if (StringUtils.isEmpty(name)) return null;

        MCDAVersion mcdaVersion = new MCDAVersion();
        mcdaVersion.setVersion(version);
        mcdaVersion.setVersionName(name);

        return mcdaVersion;
    }

    /**
     * 缓存指定版本的数据元. 此处获取数据绕过了标准化的数据模型, 直接使用原始SQL提高效率.
     *
     * @param version 内部版本号
     */
    public void cacheData(String version, boolean force) {
        if(force) clearStdData(version);

        Session session = openSession();
        try {
            String versionName = keySchema.versionName(version);

            String dataSetTable = CDAVersionUtil.getDataSetTableName(version);
            String metaDataTable = CDAVersionUtil.getMetaDataTableName(version);
            String dictEntryTable = CDAVersionUtil.getDictEntryTableName(version);
            Query query;

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

                    redisClient.set(metaDataTypeKey, type);
                    redisClient.set(metaDataDictKey, dictId);
                }
            }

            // 缓存字典项
            {
                query = session.createSQLQuery(DictEntryQuery.replace("dict.entry.table", dictEntryTable));
                List<Object[]> entryList = query.list();
                for (Object[] record : entryList) {
                    String dictId = record[0].toString();
                    String code = (String) record[1];
                    String value = (String) record[2];

                    String entryValueKey = keySchema.dictEntryValue(version, dictId, code);
                    redisClient.set(entryValueKey, value);
                }
            }

            // 缓存版本
            redisClient.set(versionName, version);
        } finally {
            if(null != session) session.close();
        }
    }

    /**
     * 清除标准数据缓存.
     *
     * @param version
     */
    public void clearStdData(String version) {
        String versionName = keySchema.versionName(version);

        redisClient.delete(versionName);
        redisClient.delete(keySchema.metaDataDict(version, "*", "*"));
        redisClient.delete(keySchema.metaDataType(version, "*", "*"));
        redisClient.delete(keySchema.dictEntryValue(version, "*", "*"));
    }

    public boolean isCached(String version) {
        return redisClient.hasKey(keySchema.versionName(version));
    }

    private Session openSession(){
        return localSessionFactoryBean.getObject().openSession();
    }
}
