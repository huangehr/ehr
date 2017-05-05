package com.yihu.ehr.standard.cache;

import com.yihu.ehr.config.StdSessionFactoryBean;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.redis.schema.StdDataSetKeySchema;
import com.yihu.ehr.redis.schema.StdMetaDataKeySchema;
import com.yihu.ehr.redis.schema.StdVersionKeySchema;
import com.yihu.ehr.standard.version.service.CDAVersionService;
import com.yihu.ehr.util.CDAVersionUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    static String DataSetQuery = "SELECT id, code, name, multi_record FROM data.set.table";
    static String MetaDataQuery = "SELECT a.code AS data_set_code, b.inner_code, b.type, b.dict_id FROM data.set.table a, " +
            "meta.data.table b WHERE a.id = b.dataset_id";
    static String DictEntryQuery = "SELECT t.dict_id, t.code, t.value FROM dict.entry.table t";


    @Autowired
    StdDataSetKeySchema dataSetKeySchema;

    @Autowired
    StdVersionKeySchema versionKeySchema;

    @Autowired
    StdMetaDataKeySchema metaDataKeySchema;

    @Autowired
    protected StdSessionFactoryBean localSessionFactoryBean;

    @Autowired
    CDAVersionService versionService;

    /**
     * 获取所有版本缓存
     */
    public List<MCDAVersion> versions() {
        Map<String,Object> map = versionKeySchema.getAll();
        List<MCDAVersion> versions = new ArrayList<>();

        for (String key : map.keySet()) {
            MCDAVersion version = new MCDAVersion();
            version.setVersion(key.split(":")[1]);
            version.setVersionName(String.valueOf(map.get(key)));

            versions.add(version);
        }

        return versions;
    }

    /**
     * 获取某个版本缓存
     */
    public MCDAVersion version(String version) {
        String name = versionKeySchema.get(version);
        if (StringUtils.isEmpty(name)) return null;

        MCDAVersion mcdaVersion = new MCDAVersion();
        mcdaVersion.setVersion(version);
        mcdaVersion.setVersionName(name);

        return mcdaVersion;
    }

    /**
     * 缓存指定版本的数据元. 此处获取数据绕过了标准化的数据模型, 直接使用原始SQL提高效率.
     * @param version 内部版本号
     */
    public void cacheData(String version, boolean force) {
        if (force) clearStdData(version);

        Session session = openSession();
        try {
            String dataSetTable = CDAVersionUtil.getDataSetTableName(version);
            String metaDataTable = CDAVersionUtil.getMetaDataTableName(version);
            String dictEntryTable = CDAVersionUtil.getDictEntryTableName(version);
            Query query;

            // 数据元
            {
                query = session.createSQLQuery(MetaDataQuery.replace("data.set.table", dataSetTable).replace("meta.data.table", metaDataTable));
                List<Object[]> metaDataList = query.list();
                for (Object[] record : metaDataList) {
                    String dataSetCode = (String) record[0];
                    String innerCode = (String) record[1];
                    String type = (String) record[2];
                    long dictId = (Integer) record[3];

                    String metaDataTypeKey = metaDataKeySchema.makeKey(version, dataSetCode, innerCode);

                    if (!force && metaDataKeySchema.hasKey(metaDataTypeKey)) {
                        LogService.getLogger().warn("Meta data duplicated: " + metaDataTypeKey);
                    }

                    metaDataKeySchema.setMetaDataType(version, dataSetCode, innerCode, type);
                    metaDataKeySchema.setMetaDataDict(version, dataSetCode, innerCode, String.valueOf(dictId));
                }
            }

            // 数据集
            {
                query = session.createSQLQuery(DataSetQuery.replace("data.set.table", dataSetTable));
                List<Object[]> dataSetList = query.list();
                for (Object[] record : dataSetList) {
                    String id = record[0].toString();
                    String code = (String)record[1];
                    String name = (String)record[2];
                    boolean multiRecord = (boolean)record[3];

                    dataSetKeySchema.setDataSetCode(version, id,code);
                    dataSetKeySchema.setDataSetName(version, id,name);
                    dataSetKeySchema.setDataSetNameByCode(version, code,name);
                    dataSetKeySchema.setDataSetMultiRecord(version, code,multiRecord);
                }
            }

            // 字典项
            {
                query = session.createSQLQuery(DictEntryQuery.replace("dict.entry.table", dictEntryTable));
                List<Object[]> entryList = query.list();
                for (Object[] record : entryList) {
                    String dictId = record[0].toString();
                    String code = (String) record[1];
                    String value = (String) record[2];

                    metaDataKeySchema.setDictEntryValue(version, dictId, code,value);
                }
            }

            // 版本
            versionKeySchema.set(version, versionService.getVersion(version).getVersionName());
        } finally {
            if (null != session) session.close();
        }
    }

    /**
     * 清除标准数据缓存.
     *
     * @param version
     */
    public void clearStdData(String version) {

        versionKeySchema.delete(version);
        metaDataKeySchema.deleteMetaDataDict(version, "*", "*");
        metaDataKeySchema.deleteMetaDataType(version, "*", "*");
        metaDataKeySchema.deleteDictEntryValue(version, "*", "*");
    }

    public boolean isCached(String version) {
        return versionKeySchema.hasKey(version);
    }

    private Session openSession() {
        return localSessionFactoryBean.getObject().openSession();
    }
}
