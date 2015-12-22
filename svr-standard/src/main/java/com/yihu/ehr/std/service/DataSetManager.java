package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.std.data.SQLGeneralDAO;
import com.yihu.ehr.std.model.DataSetModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * 数据集管理器.
 *
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:05
 */
@Transactional
@Service
public class DataSetManager extends SQLGeneralDAO{
    public DataSetManager() {
    }

    public void finalize() throws Throwable {
        super.finalize();
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSet createDataSet(CDAVersion version) {
        DataSet dataSet = new DataSet();
        dataSet.setInnerVersion(version);

        return dataSet;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSet getDataSet(long dataSetId, CDAVersion innerVersion) {
        Session session = currentSession();
        String dataSetTable = innerVersion.getDataSetTableName();

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary from " + dataSetTable + " where id = :id");
        query.setLong("id", dataSetId);

        Object[] record = (Object[]) query.uniqueResult();

        DataSet dataSet = new DataSet();
        if(record!=null) {
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer)record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersion(innerVersion);
        }
        return dataSet;
    }

    /**
     *
     * @param dataSet
     */
    public DataSetModel getDataSet(DataSet dataSet){
        DataSetModel dataSetModel = new DataSetModel();
        dataSetModel.setCode(dataSet.getCode());
        dataSetModel.setName(dataSet.getName());
        dataSetModel.setRefStandard(dataSet.getReference());
        dataSetModel.setSummary(dataSet.getSummary());
        return  dataSetModel;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSet[] getDataSetByIds(List<String> listIds,  String versionid) {
        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(versionid);

        String strIds=String.join(",", listIds);

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                "from " + dataSetTable + " where id in("+strIds+")");

        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i){
            Object[] record = (Object[])records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(versionid);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSet getDataSet(String dataSetCode, String version) {
        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(version);

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary from " + dataSetTable + " where code = :code");
        query.setString("code", dataSetCode);

        Object[] record = (Object[]) query.uniqueResult();

        DataSet dataSet = null;
        if(record!=null) {
            dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference( record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer)record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(version);
        }
        return dataSet;
    }


    @Transactional(Transactional.TxType.SUPPORTS)

  public DataSet[] getDataSetList(int from, int count, CDAVersion innerVersion) {
        return getDataSetList(from, count, innerVersion.getVersion());
    }

    public DataSet[] getDataSetList(int from, int count, String innerVersion){
        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(innerVersion);

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                "from " + dataSetTable);
        if (count > 0) {
            query.setFirstResult(from);
            query.setMaxResults(count);
        }
        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i){
            Object[] record = (Object[])records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(innerVersion);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public DataSet[] getAllDataSet(CDAVersion innerVersion) {
        Session session = currentSession();
        String dataSetTable = innerVersion.getDataSetTableName();

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                "from " + dataSetTable);

        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i){
            Object[] record = (Object[])records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer)record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersion(innerVersion);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }


    public DataSet[] getDataSets(String[] dataSetCodes, String innerVersion) {

        if (dataSetCodes == null || dataSetCodes.length == 0){
           return null;
        }

        String dataSetJoin = "(" + String.join(",", dataSetCodes) + ")";

        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(innerVersion);

        Query query = session.createSQLQuery(" select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                " from " + dataSetTable +
                " where code in " + dataSetJoin);

        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i){
            Object[] record = (Object[])records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer)record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(innerVersion);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }


    public boolean saveDataSet(DataSet xdataSet) {

        DataSet dataSet = (DataSet) xdataSet;
        Session session = currentSession();
        String sql;
        Query query;
        long id = dataSet.getId();
        try {
            if (id == 0) {
                sql = "select max(id) from " + dataSet.getInnerVersion().getDataSetTableName();
                query = session.createSQLQuery(sql);
                Object object = query.uniqueResult();

                id = object == null ? 1 : Long.parseLong(object.toString()) + 1;
                dataSet.setId(id);

                sql = "insert into " + dataSet.getInnerVersion().getDataSetTableName() +
                        "(id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary) " +
                        "values(:id, :code, :name, :publisher, :ref_standard, :std_version, :lang, :catalog, :hash, :document_id, :summary)";
            } else {
                sql = "update " + dataSet.getInnerVersion().getDataSetTableName() +
                        " set " +
                        "code = :code, " +
                        "name = :name, " +
                        "publisher = :publisher, " +
                        "ref_standard = :ref_standard, " +
                        "std_version = :std_version, " +
                        "lang = :lang, " +
                        "catalog = :catalog, " +
                        "hash = :hash, " +
                        "document_id = :document_id, " +
                        "summary = :summary " +
                        "where id = :id";
            }
            String d = dataSet.getReference();
            query = session.createSQLQuery(sql);
            query.setLong("id", dataSet.getId());
            query.setString("code", dataSet.getCode());
            query.setString("name", dataSet.getName());
            query.setInteger("publisher", dataSet.getPublisher());
            query.setString("ref_standard", dataSet.getReference());
            query.setString("std_version", dataSet.getStdVersion());
            query.setInteger("lang", dataSet.getLang());
            query.setInteger("catalog", dataSet.getCatalog());
            query.setInteger("hash", dataSet.getHashCode());
            query.setLong("document_id", dataSet.getDocumentId());
            query.setString("summary", dataSet.getSummary());
            query.executeUpdate();
        }
        catch (Exception ex)
        {
            return false;
        }

        return true;
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public int deleteDataSet(long dataSetId, String version) {
        int iResult=0;
        try {
            if (version == null || version.length() == 0) throw new IllegalArgumentException("无效版本");

            Session session = currentSession();
            String sql = "delete from " + CDAVersion.getDataSetTableName(version) + " where id = :id";
            Query query = session.createSQLQuery(sql);
            query.setLong("id", dataSetId);
            iResult = query.executeUpdate();
        }
        catch (Exception ex)
        {
            iResult=-1;
        }
        return iResult;
    }

    /**
     * 查询总条数的方法
     * @param cdaVersion
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Integer searchDataSetInt(CDAVersion cdaVersion) {

        String version = cdaVersion.getVersion();
        String dataSetTable = CDAVersion.getDataSetTableName(version);

        Session session = currentSession();

        Query query = session.createSQLQuery(" Select count(*) from " + dataSetTable);

        BigInteger bigInteger = (BigInteger)query.list().get(0);
        String num = bigInteger.toString();
        Integer totalCount = Integer.parseInt(num);
        return totalCount;
    }


    /**
     * 初始化和条件查询的方法
     * @param codename
     * @param from
     * @param count
     * @param version
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DataSet> searchDataSets(String codename,int from, int count,String version) {

        //参数获取处理

        String dataSetTable = CDAVersion.getDataSetTableName( version);
        Session session = currentSession();
        String sql = null;
        if(codename == null||codename.equals(0)||codename.equals("")){
            sql = " select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                    " from " + dataSetTable ;
        }else {
            sql = " select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                    " from " + dataSetTable +
                    " where code LIKE '"+"%"+codename+"%"+"' "+"or name LIKE '"+"%"+codename+"%"+"'" ;
        }

        Query query = session.createSQLQuery(sql);

        if(count!=0) {
            query.setMaxResults(count);
            query.setFirstResult((from - 1) * count);
        }
        return query.list();
    }


    public List<DataSetModel> searchDataSetList(String codename,int from, int count, CDAVersion innerVersion) {

        //参数获取处理
        List DataSetList = searchDataSets(codename, from, count, innerVersion.getVersion());
        List<DataSetModel> dataSetModels = new ArrayList<>();

        for(int i=0;i<DataSetList.size();i++){

            Object[] record = (Object[])DataSetList.get(i);
            DataSetModel dataSetModel = new DataSetModel();
            dataSetModel.setId((Integer)record[0]);
            dataSetModel.setCode((String)record[1]);
            dataSetModel.setName((String)record[2]);
/*            dataSetModel.setPublisher((int)record[3]);
            dataSetModel.setRefStandard((String)record[4]);
            dataSetModel.setStdVersion((String)record[5]);
            dataSetModel.setLang((int)record[6]);
            dataSetModel.setCatalog((int)record[7]);
            dataSetModel.setHash((int)record[8]);
            dataSetModel.setDocumentId((int) record[9]);
            dataSetModel.setSummary((String)record[10]);*/

            dataSetModels.add(dataSetModel);
        }

        return dataSetModels;
    }
}