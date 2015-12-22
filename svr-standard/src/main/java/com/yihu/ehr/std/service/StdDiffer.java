package com.yihu.ehr.std.service;

import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.std.data.SQLGeneralDAO;
import com.yihu.ehr.std.model.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 标准化对象差异计算器.
 *
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:04
 */
@Service
public class StdDiffer extends SQLGeneralDAO {
    private DataSet sourceDataSet;      // 源数据集
    private DataSet targetDataSet;      // 目标数据集

    private Dict sourceDict;
    private Dict targetDict;

    public StdDiffer() {
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSetDifference getDataSetDifference() {
        if (!isDataSetComparable()) return null;

        DataSetDifference difference = new DataSetDifference();
        try {
            // 数据集属性比较
            Field[] fields = DataSet.class.getDeclaredFields();
            for (Field field : fields) {
                DifferIgnored ignored = field.getAnnotation(DifferIgnored.class);
                if (ignored != null) continue;

                String propertyName = field.getName();
                Object originValue = field.get(sourceDataSet);
                Object newValue = field.get(targetDataSet);
                if (originValue.equals(newValue)) continue;

                difference.addPropertyDifference(propertyName, originValue, newValue);
            }

            // 数据元比较
            dataSetDiffOfMetaData(difference, sourceDataSet, targetDataSet);      // 计算被修改的数据元
            dataSetDiff(difference, sourceDataSet, targetDataSet);                // 计算数据元之间的差集
        } catch (IllegalAccessException ex) {
        }

        return difference;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public MetaDataDifference getMetaDifference(MetaData sourceMetaData, MetaData targetMetaData) {
        if (!isMetaDataComparable(sourceMetaData, targetMetaData)) return null;

        MetaDataDifference difference = new MetaDataDifference();
        try {
            Field[] fields = MetaData.class.getDeclaredFields();
            for (Field field : fields) {
                DifferIgnored ignored = field.getAnnotation(DifferIgnored.class);
                if (ignored != null) continue;

                String propertyName = field.getName();
                Object originValue = field.get(sourceMetaData);
                Object newValue = field.get(targetMetaData);

                if (propertyName.contains("dict")) {
                    // 比较字典ID
                    if (originValue != newValue) {
                        originValue = originValue == null ? new Long(0) : ((Dict) originValue).getId();
                        newValue = newValue == null ? new Long(0) : ((Dict) newValue).getId();

                        if (originValue.equals(newValue)) continue;
                    } else {
                        continue;
                    }
                } else {
                    if (originValue.equals(newValue)) continue;
                }

                difference.addPropertyDifference(propertyName, originValue, newValue);
            }
        } catch (IllegalAccessException ex) {
        }

        return difference;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isDataSetComparable() {
        if (sourceDataSet == null || targetDataSet == null) return false;

        if (sourceDataSet == targetDataSet) return false;

        if (!(sourceDataSet instanceof DataSet) || !(targetDataSet instanceof DataSet)) return false;

        return true;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isMetaDataComparable(MetaData sourceMetaData, MetaData targetMetaData) {
        if (sourceMetaData == null || targetMetaData == null) return false;

        if (sourceMetaData == targetMetaData) return false;

        if (!(sourceMetaData instanceof MetaData) || !(targetMetaData instanceof MetaData)) return false;

        return true;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public void setSourceDataSet(DataSet dataSet) {
        this.sourceDataSet = dataSet;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public void setTargetDataSet(DataSet dataSet) {
        this.targetDataSet = dataSet;
    }

    /**
     * 获取同一数据集中，相同数据元的属性差异。
     *
     * @param a
     * @param b
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    private void dataSetDiffOfMetaData(DataSetDifference difference, DataSet a, DataSet b) {

        String sql = "select a.id from " + a.getInnerVersion().getMetaDataTableName() + " as a, " + b.getInnerVersion().getMetaDataTableName() +
                " as b where a.id = b.id and a.dataset_id = b.dataset_id and a.hash <> b.hash and a.dataset_id = " + a.getId();

        Session session = currentSession();
        List<Integer> ids = session.createSQLQuery(sql).list();

        for (Integer id : ids) {
            MetaData origin = a.getMetaData(id);
            MetaData now = b.getMetaData(id);
            difference.addChangedMetaData(origin, now);
        }
    }

    /**
     * 获取数据集中的数据元差集。sourceDataSet - targetDataSet 与 targetDataSet - sourceDataSet。
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    private void dataSetDiff(DataSetDifference difference, DataSet source, DataSet target) {
        String A_BSQL = "SELECT a.id " +
                "FROM " + source.getInnerVersion().getMetaDataTableName() + " as a " +
                "WHERE a.dataset_id = " + source.getId() + " AND a.id NOT IN (SELECT b.id " +
                "                                        FROM " + target.getInnerVersion().getMetaDataTableName() + " AS b " +
                "                                        WHERE b.dataset_id = " + source.getId() + ")";
        String B_ASQL = "SELECT a.id " +
                "FROM " + target.getInnerVersion().getMetaDataTableName() + " as a " +
                "WHERE a.dataset_id = " + source.getId() + " AND a.id NOT IN (SELECT b.id " +
                "                                        FROM " + source.getInnerVersion().getMetaDataTableName() + " AS b " +
                "                                        WHERE a.dataset_id = " + source.getId() + ")";

        Session session = currentSession();
        List<Integer> A_B = session.createSQLQuery(A_BSQL).list();
        List<Integer> B_A = session.createSQLQuery(B_ASQL).list();

        List<MetaData> result;

        result = source.getMetaDataList(A_B);
        if (result != null) {
            difference.addNewMetaData(result);
        }

        result = target.getMetaDataList(B_A);
        if (result != null) {
            difference.addRemovedMetaData(result);
        }
    }

    /******************字典差值对照 Start**********************/
    /**
     * 获取字典间的差异。包括新增，减少及修改的字典值。
     */

    @Transactional(Transactional.TxType.SUPPORTS)
    public DictDifference getDictDifference() {

        if (!isDictComparable()) return null;

        DictDifference difference = new DictDifference();
        try {
            // 数据集属性比较
            Field[] fields = Dict.class.getDeclaredFields();
            for (Field field : fields) {
                DifferIgnored ignored = field.getAnnotation(DifferIgnored.class);
                if (ignored != null) continue;

                String propertyName = field.getName();
                Object originValue = field.get(sourceDict);
                Object newValue = field.get(targetDict);

                if (originValue == null && newValue == null) continue;

                if (originValue.equals(newValue)) continue;

                difference.addProPertyDifference(propertyName, originValue, newValue);
            }

            // 值比较
            DictDiffOfDictEntry(difference, sourceDict, targetDict);      // 计算被修改的字典值
            dictDiff(difference, sourceDict, targetDict);                // 计算字典值之间的差集
        } catch (IllegalAccessException ex) {
        }

        return difference;
    }

    /**
     * 获取字典值之间的差异,仅包括字典值的属性。
     *
     * @return
     */

    @Transactional(Transactional.TxType.SUPPORTS)
    public DictEntryDifference getDictEntryDifference(DictEntry sourceDictEntry, DictEntry targetDictEntry) {
        if (!isDictEntryComparable(sourceDictEntry, targetDictEntry)) return null;

        DictEntryDifference difference = new DictEntryDifference();
        try {
            Field[] fields = DictEntry.class.getDeclaredFields();
            for (Field field : fields) {
                DifferIgnored ignored = field.getAnnotation(DifferIgnored.class);
                if (ignored != null) continue;

                String propertyName = field.getName();
                Object originValue = field.get(sourceDictEntry);
                Object newValue = field.get(targetDictEntry);
                if (originValue == null && newValue == null) continue;
                if (originValue.equals(newValue)) continue;

                difference.addPropertyDifference(propertyName, originValue, newValue);
            }
        } catch (IllegalAccessException ex) {
            int i=0;
        }

        return difference;
    }

    /**
     * 获取同一字典中，相同字典值的属性差异。
     *
     * @param a
     * @param b
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    private void DictDiffOfDictEntry(DictDifference difference, Dict a, Dict b) {
        Session session = currentSession();
        Query query = session.createSQLQuery("select a.id,a.code from " + a.getInnerVersion().getDictEntryTableName() + " as a, " + b.getInnerVersion().getDictEntryTableName() +
                " as b where a.id = b.id and a.dict_id = b.dict_id and a.hash <> b.hash and a.dict_id = " + a.getId());


        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            String strCode = (String) record[1];
            DictEntry origin = a.getDictEntry(strCode);
            DictEntry now = b.getDictEntry(strCode);
            difference.addChangedDictEntry(origin, now);
        }

    }

    /**
     * 获取数据集中的数据元差集。sourceDataSet - targetDataSet 与 targetDataSet - sourceDataSet。
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    private void dictDiff(DictDifference difference, Dict source, Dict target) {
        String A_BSQL = "SELECT a.id " +
                "FROM " + source.getInnerVersion().getDictEntryTableName() + " as a " +
                "WHERE a.dict_id = " + source.getId() + " AND a.id NOT IN (SELECT b.id " +
                "                                        FROM " + target.getInnerVersion().getDictEntryTableName() + " AS b " +
                "                                        WHERE b.dict_id = " + source.getId() + ")";
        String B_ASQL = "SELECT a.id " +
                "FROM " + target.getInnerVersion().getDictEntryTableName() + " as a " +
                "WHERE a.dict_id = " + source.getId() + " AND a.id NOT IN (SELECT b.id " +
                "                                        FROM " + source.getInnerVersion().getDictEntryTableName() + " AS b " +
                "                                        WHERE a.dict_id = " + source.getId() + ")";

        Session session = currentSession();
        List<Integer> A_B = session.createSQLQuery(A_BSQL).list();
        List<Integer> B_A = session.createSQLQuery(B_ASQL).list();

        DictEntry[] result;
        if (A_B.size() > 0) {
            result = source.getDictEntries(A_B);
            if (result != null) {
                difference.addNewDictEntry(result);
            }
        }
        if (B_A.size() > 0) {
            result = target.getDictEntries(B_A);
            if (result != null) {
                difference.addRemoveDictEntry(result);
            }
        }

    }

    /**
     * 判断两个字典是否可比较。
     */

    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isDictComparable() {
        if (sourceDict == null || targetDict == null)
            return false;
        if (sourceDict == targetDict)
            return false;
        if (!(sourceDict instanceof Dict) || !(targetDict instanceof Dict))
            return false;

        return true;
    }

    /**
     * 判断两个字典值是否可比较。
     */

    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isDictEntryComparable(DictEntry sourceDictEntry, DictEntry targetDictEntry) {
        if (sourceDictEntry == null || targetDictEntry == null)
            return false;
        if (sourceDictEntry == targetDictEntry)
            return false;
        if (!(sourceDictEntry instanceof DictEntry) || !(targetDictEntry instanceof DictEntry))
            return false;

        return true;
    }

    /**
     * 设置要比较的源字典。
     *
     * @param dict
     */
    public void setSourceDict(Dict dict) {
        this.sourceDict = dict;
    }

    /**
     * 设置要比较的字典
     *
     * @param dict
     */
    public void setTargetDict(Dict dict) {
        this.targetDict = dict;
    }

    /******************
     * 字典差值对照 End
     **********************/

    // Interface
    public void acquire() {
    }

    public void release() {
    }

    public <T> T queryInterface(Class<T> type) {
        if (type.isInterface() && type.isInstance(this)) {
            return (T) this;
        }

        return null;
    }

    /*
    *获取两个数据集版本间的差集，即获取新增的数据集或者获取删除的数据集
    * @param strSourTableName 对比表
    * @param strTargetTableName 被对比表
    * @return 返回 数据集 差集
    */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DataSet> getNewOrRemoveDataSetList(String strSourTableName, String strTargetTableName, String strType) {
        List<DataSet> listDataSet = new ArrayList<>();
        //获取数据集差集
        Session session = currentSession();
        Query query = session.createSQLQuery("SELECT\n" +
                "\tid, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary\n" +
                "FROM " + strSourTableName + " t\n" +
                "WHERE t.id NOT IN (SELECT id FROM " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listDataSet.add(getDataSet(record, strType));
        }
        return listDataSet;
    }

    /*
   *获取两个数据集版本间的修改内容
   * @param strSourTableName 对比表
   * @param strTargetTableName 被对比表
   * @return 返回 数据集 修改内容
   */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DataSet> getUpdateDataSetList(String strSourTableName, String strTargetTableName) {
        List<DataSet> listDataSet = new ArrayList<>();
        //获取修改的数据集
        Session session = currentSession();
        Query query = session.createSQLQuery("select \n" +
                "\tt.id, t.code, t.name, t.publisher, t.ref_standard, t.std_version, t.lang, t.catalog, t.hash, t.document_id, t.summary\n" +
                "from " + strSourTableName + " t\n" +
                "join " + strTargetTableName + " d on t.id=d.id and t.`hash`<>d.`hash`");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listDataSet.add(getDataSet(record, "2"));
        }
        return listDataSet;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSet getDataSet(Object[] record, String strType) {
        DataSet dataSet = new DataSet();
        dataSet.setId((Integer) record[0]);
        dataSet.setCode((String) record[1]);
        dataSet.setName((String) record[2]);
        dataSet.setReference(record[4].toString());
        dataSet.setStdVersion((String) record[5]);
        dataSet.setLang((Integer) record[6]);
        dataSet.setCatalog((Integer) record[7]);
        dataSet.setHashCode((Integer) record[8]);
        dataSet.setDocumentId((Integer) record[9]);
        dataSet.setSummary((String) record[10]);
        dataSet.setInnerVersionId(String.valueOf(record[3]));
        dataSet.setOperationType(strType);

        return dataSet;
    }

    /*
   *获取两个数据元版本间的差集，即获取新增的数据元或者获取删除的数据元
   * @param strSourTableName 对比表
   * @param strTargetTableName 被对比表
   * @return 返回 数据元 差集
   */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MetaData> getNewOrRemoveMetaDataList(String strSourTableName,String strSourceDictTable, String strTargetTableName, String strType) {
        List<MetaData> listMetaData = new ArrayList<>();
        //获取数据元差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select t.code, t.inner_code, t.name, t.type, t.format, d.code dict_code, t.definition, t.nullable, t.column_type, t.column_name, t.column_length, t.primary_key, t.hash, t.id ,t.dataset_id\n" +
                "from " + strSourTableName + " t\n" +
                " left join "+strSourceDictTable+" d on t.dict_id=d.id "+
                "where t.id not in (SELECT id from " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listMetaData.add(recordToMetaData(record, strType));
        }

        return listMetaData;
    }

    /*
     *获取两个数据元版本间的修改集
     * @param strSourTableName 对比表
     * @param strTargetTableName 被对比表
     * @return 返回 数据元 修改集
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MetaData> getUpdateMetaDataList(String strSourTableName,String strSourceDictTable, String strTargetTableName) {
        List<MetaData> listMetaData = new ArrayList<>();
        //获取数据元差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select t.code, t.inner_code, t.name, t.type, t.format, dt.code dict_code, t.definition, t.nullable, t.column_type, t.column_name, t.column_length, t.primary_key, t.hash, t.id ,t.dataset_id\n" +
                " from " + strSourTableName + " t\n" +
                " left join "+strSourceDictTable+" dt on t.dict_id=dt.id "+
                " join " + strTargetTableName + " d on t.id=d.id and t.`hash`<>d.`hash`");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listMetaData.add(recordToMetaData(record, "2"));
        }
        return listMetaData;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    private MetaData recordToMetaData(Object[] record, String strType) {
        MetaData metaData = new MetaData();

        metaData.setCode(record[0].toString());
        metaData.setInnerCode(record[1].toString());
        metaData.setName(record[2].toString());
        metaData.setType(record[3].toString());
        metaData.setFormat(record[4].toString());
        //metaData.setDictId(record[5] == null ? 0 : Long.parseLong(record[5].toString()));
        metaData.setDict_code(record[5].toString());
        metaData.setDefinition(record[6] == null ? "" : record.toString());
        metaData.setNullable(Boolean.parseBoolean(record[7].toString()));
        metaData.setColumnType(record[8].toString());
        metaData.setColumnName(record[9].toString());
        metaData.setColumnLength(record[10].toString());
        metaData.setPrimaryKey(Boolean.parseBoolean(record[11].toString()));
        metaData.setHashCode(Integer.parseInt(record[12].toString()));
        metaData.setId(Integer.parseInt(record[13].toString()));
        metaData.setDataSetId(Long.parseLong(record[14].toString()));
        metaData.setOperationType(strType);

        return metaData;
    }

    /*
     *获取两个字典版本间的差集，即获取新增的字典或者获取删除的字典
     * @param strSourTableName 对比表
     * @param strTargetTableName 被对比表
     * @param strType 操作类型 即获取为新增集合 1 或者删除集合 2
     * @return 返回 字典 差集
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Dict> getNewOrRemoveDictList(String strSourTableName, String strTargetTableName, String strType) {
        List<Dict> listDict = new ArrayList<>();
        //获取字典 差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select \n" +
                "id, code, name,author,base_dict, create_date, description, StdVersion,instage,source,hash\n" +
                "from " + strSourTableName + " t\n" +
                "where t.id not in (SELECT id from " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listDict.add(recordToDict(record, strType));
        }
        return listDict;
    }

    /*
    *获取两个字典版本间的修改集，
    * @param strSourTableName 对比表
    * @param strTargetTableName 被对比表
    * @return 返回 字典 修改集
    */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Dict> getUpdateDictList(String strSourTableName, String strTargetTableName) {
        List<Dict> listDict = new ArrayList<>();
        //获取字典 修改值
        Session session = currentSession();
        Query query = session.createSQLQuery("select \n" +
                "\tt.id, t.code, t.name,t.author,t.base_dict, t.create_date, t.description, t.StdVersion,t.instage,t.source,t.hash\n" +
                "from " + strSourTableName + " t\n" +
                "join " + strTargetTableName + " d on t.id=d.id and t.`hash`<>d.`hash`\n");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listDict.add(recordToDict(record, "2"));
        }
        return listDict;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Dict recordToDict(Object[] record, String strType) {
        Dict dict = new Dict();
        dict.setId((Integer) record[0]);
        dict.setCode((String) record[1]);
        dict.setName((String) record[2]);
        dict.setAuthor((String) record[3]);
        dict.setBaseDictId((Integer) record[4]);
        dict.setCreateDate((Date) record[5]);
        dict.setDescription((String) record[6]);
        dict.setStdVersion(String.valueOf(record[7]));
        dict.setInStage((Boolean) record[8] ? 1 : 0);
        dict.setHashCode((Integer) record[10]);
        dict.setOperationType(strType);
        return dict;
    }


    /*
    *获取两个字典值版本间的差集，即获取新增的字典或者获取删除的字典值
    * @param strSourTableName 对比表
    * @param strTargetTableName 被对比表
    * @return 返回 字典值 差集
    */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DictEntry> getNewOrRemoveDictEntryList(String strSourTableName,String strSourceDictTable, String strTargetTableName, String strType) {
        List<DictEntry> listDictEntry = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select t.id, t.code,t.value,t.dict_id,t.description,d.code dict_code\n" +
                "from " + strSourTableName + " t \n" +
                " left join "+strSourceDictTable+" d on t.dict_id=d.id"+
                "where t.id not in (SELECT id from " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listDictEntry.add(recordToDictEntry(record, strType));
        }
        return listDictEntry;
    }

    /*
       *获取两个字典值版本间的修改集
       * @param strSourTableName 对比表
       * @param strTargetTableName 被对比表
       * @return 返回 字典值 修改集
       */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DictEntry> getUpdateDictEntryList(String strSourTableName,String strSourceDictTable, String strTargetTableName) {
        List<DictEntry> listDictEntry = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select t.id, t.code,t.value,t.dict_id,t.description,d.code dict_code \n" +
                "from " + strSourTableName + " t\n" +
                " left join "+strSourceDictTable+" dt on t.dict_id=dt.id "+
                "join " + strTargetTableName + " d on t.id=d.id and t.`hash`<>d.`hash`");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            listDictEntry.add(recordToDictEntry(record, "2"));
        }
        return listDictEntry;
    }

    public DictEntry recordToDictEntry(Object[] record, String strType) {
        DictEntry dictEntry = new DictEntry();
        dictEntry.setId(Long.parseLong(String.valueOf(record[0])));
        dictEntry.setCode((String) record[1]);
        dictEntry.setValue((String) record[2]);
        dictEntry.setDictId(Long.parseLong(record[3].toString()));
        dictEntry.setDesc((String) record[4]);
        dictEntry.setOperationType(strType);
        dictEntry.setDictCode((String) record[5]);
        return dictEntry;
    }

    /*
    *获取两个标准来源版本间的差集，即获取新增的标准来源或者获取删除的标准来源
    * @param strSourTableName 对比表
    * @param strTargetTableName 被对比表
    * @return 返回 标准来源 差集
    */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<StandardSource> getNewOrRemoveSourceList(String strSourTableName, String strTargetTableName, String strType) {
        List<StandardSource> xStandardSources = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select id,code,name,source_type,description from " + strSourTableName + " t\n" +
                "where t.id not in (SELECT id from " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            StandardSource info = new StandardSource();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setSourceType(record[3].toString());
            info.setDescription(record[4]==null?null:record[4].toString());
            info.setOperationType(strType);
            xStandardSources.add(info);
        }
        return xStandardSources;
    }

    /*
       *获取两个标准来源版本间的修改集
       * @param strSourTableName 对比表
       * @param strTargetTableName 被对比表
       * @return 返回 标准来源 修改集
       */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<StandardSource> getUpdateSourceList(String strSourTableName, String strTargetTableName) {
        List<StandardSource> xStandardSources = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select id,code,name,source_type,description from " + strSourTableName + " t\n" +
                "join " + strTargetTableName + " d on t.id=d.id and t.`hash`<>d.`hash`");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            StandardSource info = new StandardSource();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setSourceType(record[3].toString());
            info.setDescription(record[4]==null?null:record[4].toString());
            info.setOperationType("2");
            xStandardSources.add(info);
        }
        return xStandardSources;
    }

    /*
    *获取两个CDA版本间的差集，即获取新增的CDA或者获取删除的CDA
    * @param strSourTableName 对比表
    * @param strTargetTableName 被对比表
    * @return 返回 CDADocument 差集
    */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDADocument> getNewOrRemoveCDAList(String strSourTableName, String strTargetTableName, String strType) {
        List<CDADocument> xcdaDocuments = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select t.id, t.`code`, t.`name`, t.print_out, t.`schema_path`, t.source_id,t.description from " + strSourTableName + " t\n" +
                "where t.id not in (SELECT id from " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CDADocument info = new CDADocument();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setPrintOut(record[3] == null ? null :record[3].toString());
            info.setSchema(record[4] == null ? null :record[4].toString());
            info.setSchema(record[5] == null ? null : record[5].toString());
            info.setDescription(record[6]==null?null:record[6].toString());
            info.setOperationType(strType);
            xcdaDocuments.add(info);
        }
        return xcdaDocuments;
    }

    /*
       *获取两个CDA版本间的修改集
       * @param strSourTableName 对比表
       * @param strTargetTableName 被对比表
       * @return 返回 CDADocument 修改集
       */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDADocument> getUpdateCDAList(String strSourTableName, String strTargetTableName) {
        List<CDADocument> xcdaDocuments = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select id, code, name, print_out, schema_path, source_id,description from " + strSourTableName + " t\n" +
                "join " + strTargetTableName + " d on t.id=d.id and t.`hash`<>d.`hash`");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CDADocument info = new CDADocument();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setPrintOut(record[3] == null ? null : record[3].toString());
            info.setSchema(record[4] == null ? null : record[4].toString());
            info.setSchema(record[5] == null ? null : record[5].toString());
            info.setDescription(record[6]==null?null:record[6].toString());
            info.setOperationType("2");
            xcdaDocuments.add(info);
        }
        return xcdaDocuments;
    }

    /*
    *获取两个关系差集版本间的差集，即获取新增的关系差集或者获取删除的关系差集
    * @param strSourTableName 对比表
    * @param strTargetTableName 被对比表
    * @return 返回 关系差集 差集
    */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDADatasetRelationship> getNewOrRemoveRelationshipList(String strSourTableName, String strTargetTableName, String strType) {
        List<CDADatasetRelationship> xCDADatasetRelationships = new ArrayList<>();
        //获取 字典项的差集
        Session session = currentSession();
        Query query = session.createSQLQuery("select t.id,t.cda_id,t.dataset_id from " + strSourTableName + " t\n" +
                "where t.id not in (SELECT id from " + strTargetTableName + ");");

        List<Object> records = query.list();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CDADatasetRelationship info = new CDADatasetRelationship();
            info.setId(record[0].toString());
            info.setCDAId(record[1].toString());
            info.setDatasetId(record[2].toString());

            info.setOperationType(strType);
            xCDADatasetRelationships.add(info);
        }
        return xCDADatasetRelationships;
    }
}