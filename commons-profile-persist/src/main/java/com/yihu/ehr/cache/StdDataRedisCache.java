package com.yihu.ehr.cache;

import com.yihu.ehr.redis.RedisClient;

/**
 * 标准化数据数据缓存工具. 向Redis数据库初始化标准字典及数据元信息, 在档案入库的时候可用于翻译字典, 按版本缓存.
 * <p>
 * 要清除某个版本的缓存, 直接使用版本ID即可清除.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 16:53
 */
public class StdDataRedisCache {
    final static String VersionMagic = "Magic";

    static String DataSetQuery = "SELECT code, name FROM data.set.table";
    static String MetaDataQuery = "SELECT a.code AS data_set_code, b.inner_code, b.type, b.dict_id FROM data.set.table a, " +
            "meta.data.table b WHERE a.id = b.dataset_id";
    static String DictEntryQuery = "SELECT t.dict_id, t.code, t.value FROM dict.entry.table t";

    /**
     * 缓存指定版本的数据元. 此处获取数据绕过了标准化的数据模型, 直接使用原始SQL提高效率.
     *
     * @param version 内部版本号
     */
    public static void loadStdData(String version, boolean force) {
//        RedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//        if (!force && isCached(redisDAO, version)) return;
//
//        XSQLGeneralDAO sqlGeneralDAO = ServiceFactory.getService(Services.SQLGeneralDAO);
//        Session session = sqlGeneralDAO.getCurrentSession();
//
//        try {
//            if (!session.isConnected()) {
//                session = sqlGeneralDAO.openSession();
//            }
//
//            String dataSetTable = CDAVersion.getDataSetTableName(version);
//            String metaDataTable = CDAVersion.getMetaDataTableName(version);
//            String dictEntryTable = CDAVersion.getDictEntryTableName(version);
//            Query query = null;
//
//            // 缓存数据集
//            {
//                query = session.createSQLQuery(DataSetQuery.replace("data.set.table", dataSetTable));
//                List<Object[]> dataSetList = query.list();
//                for (Object[] record : dataSetList) {
//                    CachedDataSet dataSet = new CachedDataSet((String)record[0], (String)record[1]);
//
//                    String dataSetKey = StdDataRedisKeySchema.makeDataSetKey(version, (String)record[0]);
//                    redisDAO.set(dataSetKey, dataSet);
//                }
//            }
//
//            // 缓存数据元
//            {
//                query = session.createSQLQuery(MetaDataQuery.replace("data.set.table", dataSetTable).replace("meta.data.table", metaDataTable));
//                List<Object[]> metaDataList = query.list();
//                for (Object[] record : metaDataList) {
//                    String dataSetCode = (String) record[0];
//                    String innerCode = (String) record[1];
//                    String type = (String) record[2];
//                    long dictId = (Integer) record[3];
//
//                    String metaDataKey = StdDataRedisKeySchema.makeMetaDataKey(version, dataSetCode, innerCode);
//                    redisDAO.set(metaDataKey, new CachedMetaData(innerCode, type, dictId));
//                }
//            }
//
//            // 缓存字典项
//            {
//                query = session.createSQLQuery(DictEntryQuery.replace("dict.entry.table", dictEntryTable));
//                List<Object[]> entryList = query.list();
//                for (Object[] record : entryList) {
//                    long dictId = (Integer) record[0];
//                    String code = (String) record[1];
//                    String value = (String) record[2];
//
//                    String dictEntryKey = StdDataRedisKeySchema.makeDictEntryKey(version, dictId, code);
//                    redisDAO.set(dictEntryKey, value);
//                }
//            }
//
//            redisDAO.set(getVersionMagic(version), "Magic");
//        } finally
//        {
//            session.close();
//        }

    }

    /**
     * 清除标准数据缓存.
     *
     * @param version
     */
    public static void clearStdData(String version) {
//        XRedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//
//        Set<String> keys = redisDAO.keys(version + "*");
//        redisDAO.delete(keys);
    }

    /**
     * 重新加载标准数据缓存.
     *
     * @param version
     */
    public static void reloadStdCache(String version) {
        clearStdData(version);
        loadStdData(version, true);
    }

    public static CachedDataSet getDataSet(String innerVersion, String dataSetCode){
//        String key = StdDataRedisKeySchema.makeDataSetKey(innerVersion, dataSetCode);
//
//        XRedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//        return redisDAO.get(key);

        return null;
    }

    /**
     * 获取数据元.
     *
     * @param innerVersion
     * @param dataSetCode
     * @param metaDataInnerCode
     * @return
     */
    public static CachedMetaData getMetaData(String innerVersion, String dataSetCode, String metaDataInnerCode) {
//        String key = StdDataRedisKeySchema.makeMetaDataKey(innerVersion, dataSetCode, metaDataInnerCode);
//
//        XRedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//        return redisDAO.get(key);
        return null;
    }

    /**
     * 获取字典项的缓存值.
     *
     * @param innerVersion
     * @param dictId
     * @param entryCode
     * @return
     */
    public static String getDictEntryValue(String innerVersion, long dictId, String entryCode) {
//        String key = StdDataRedisKeySchema.makeDictEntryKey(innerVersion, dictId, entryCode);
//
//        XRedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//        return redisDAO.get(key);
        return null;
    }

    public static boolean isCached(RedisClient redisDAO, String version) {
        return redisDAO.hasKey(getVersionMagic(version));
    }

    private static String getVersionMagic(String version) {
        return version + "." + VersionMagic;
    }
}
