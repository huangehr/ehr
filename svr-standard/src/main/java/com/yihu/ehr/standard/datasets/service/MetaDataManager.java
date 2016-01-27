package com.yihu.ehr.standard.datasets.service;

import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.commons.BaseManager;
import com.yihu.ehr.util.operator.StringUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据元管理接口实现。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.10 15:22
 */
@Transactional
@Service
public class MetaDataManager extends BaseManager{
    @Autowired
    private CDAVersionManager cdaVersionManager;


    @Transactional(propagation = Propagation.SUPPORTS)
    public MetaData createMetaData(DataSet dataSet) {
        MetaData metaData = new MetaData();
        metaData.setDataSet(dataSet);

        return metaData;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public MetaData getMetaData(DataSet dataSet, long metaDataId) {
        CDAVersion cdaVersion = cdaVersionManager.getVersion(dataSet.getInnerVersionId());

        String sql = "select t.id,t.dataset_id,t.code,t.inner_code, t.name, t.type, t.format, t.dict_id, t.definition,  t.nullable, t.column_type, t.column_name, t.column_length, t.primary_key, t.hash,d.name dict_name, d.code dict_code from " +
                cdaVersion.getMetaDataTableName() + " t "+
                " left join "+cdaVersion.getDictTableName()+" d on t.dict_id=d.id"+
                " where t.id = :metadata_id and t.dataset_id = :dataset_id";

        Session session = currentSession();
        Query query = session.createSQLQuery(sql);
        query.setLong("metadata_id", metaDataId);
        query.setLong("dataset_id", dataSet.getId());

        Object[] record = (Object[])query.uniqueResult();
        MetaData metaData = recordToMetaData(dataSet, record);

        return metaData;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public List<MetaData> getMetaDataList(DataSet dataSet) {
        CDAVersion cdaVersion = cdaVersionManager.getVersion(dataSet.getInnerVersionId());
        String sql = "select t.id,t.dataset_id,t.code,t.inner_code, t.name, t.type, t.format, t.dict_id, t.definition,  t.nullable, t.column_type, t.column_name, t.column_length, t.primary_key, t.hash,d.name dict_name, d.code dict_code from " +
                cdaVersion.getMetaDataTableName() + " t "+
                " left join "+cdaVersion.getDictTableName()+" d on t.dict_id=d.id"+
                " where t.dataset_id = :dataset_id";

        Session session = currentSession();
        Query query = session.createSQLQuery(sql);
        query.setLong("dataset_id", dataSet.getId());

        List<Object> records = query.list();
        List<MetaData> metaDataList = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); ++i){
            MetaData metaData = recordToMetaData(dataSet, (Object[])records.get(i));
            metaDataList.add(metaData);
        }

        return metaDataList;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public List<MetaData> getMetaDataList(DataSet dataSet, List<Integer> ids) {
        CDAVersion cdaVersion = cdaVersionManager.getVersion(dataSet.getInnerVersionId());
        if(ids.size() == 0) return null;

        // 组合ID
        String idList = "(";
        for (Integer id: ids){
            idList += id + ", ";
        }
        idList = idList.substring(0, idList.length() - 2) + ")";

        String sql = "select t.id,t.dataset_id,t.code,t.inner_code, t.name, t.type, t.format, t.dict_id, t.definition,  t.nullable, t.column_type, t.column_name, t.column_length, t.primary_key, t.hash,d.name dict_name, d.code dict_code from " +
                cdaVersion.getMetaDataTableName() + " t "+
                " left join "+cdaVersion.getDictTableName()+" d on t.dict_id=d.id"+
                " where t.id in " + idList;

        // 查询
        List<Object> records = currentSession().createSQLQuery(sql).list();
        List<MetaData> metaDataList = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); ++i){
            MetaData metaData = recordToMetaData(dataSet, (Object[])records.get(i));
            metaDataList.add(metaData);
        }

        return metaDataList;
    }


    public int saveMetaData(DataSet dataSet, MetaData metaData) {
        CDAVersion cdaVersion = cdaVersionManager.getVersion(dataSet.getInnerVersionId());
        Session session = currentSession();
        String sql = null;
        if(metaData.getId() == 0){
            Object object = session.createSQLQuery("select max(id) from " + cdaVersion.getMetaDataTableName()).uniqueResult();
            metaData.setId(object == null ? 1 : Long.parseLong(object.toString()) + 1);

            sql = "insert into " + cdaVersion.getMetaDataTableName() +
                    "(id, dataset_id, code, inner_code, name, type, format, dict_id, definition, nullable, column_type, column_name, column_length, primary_key, hash) " +
                    "values(:id, :dataset_id, :code, :inner_code, :name, :type, :format, :dict_id, :definition, :nullable, :column_type, :column_name, :column_length, :primary_key, :hash)";
        } else {
            sql = "update " + cdaVersion.getMetaDataTableName() + " set " +
                    "dataset_id = :dataset_id, " +
                    "code = :code, " +
                    "inner_code = :inner_code, " +
                    "name = :name, " +
                    "type = :type, " +
                    "format = :format, " +
                    "dict_id = :dict_id, " +
                    "definition = :definition, " +
                    "nullable = :nullable, " +
                    "column_type = :column_type, " +
                    "column_name = :column_name, " +
                    "column_length = :column_length, " +
                    "primary_key = :primary_key, " +
                    "hash = :hash " +
                    "where id = :id";
        }

        Query query = session.createSQLQuery(sql);
        query.setLong("id", metaData.getId());
        //todo 是否需确认存在
        query.setLong("dataset_id", metaData.getDataSetId());
        query.setLong("dict_id", metaData.getDictId());

        query.setString("code", metaData.getCode());
        query.setString("inner_code", metaData.getInnerCode());
        query.setString("name", metaData.getName());
        query.setString("type", metaData.getType());
        query.setString("format", metaData.getFormat());
        query.setString("definition", metaData.getDefinition());
        query.setInteger("nullable", metaData.isNullable() ? 1 : 0);
        query.setString("column_type", metaData.getColumnType());
        query.setString("column_name", metaData.getColumnName());
        query.setString("column_length", metaData.getColumnLength());
        query.setInteger("primary_key", metaData.isPrimaryKey()?1:0);
        query.setInteger("hash", metaData.getHashCode());
        return query.executeUpdate();

    }


    public void removeMetaData(DataSet dataSet, MetaData metaData) {
        Session session = currentSession();
        CDAVersion cdaVersion = cdaVersionManager.getVersion(dataSet.getInnerVersionId());
        String sql = "delete from " + cdaVersion.getMetaDataTableName() + " where id = " + metaData.getId();
        session.createSQLQuery(sql).executeUpdate();
    }


    @Transactional(propagation= Propagation.SUPPORTS)
    public int removeMetaDataBySetId(String version, long setId) {
        int row=0;
        Session session = currentSession();
        String sql = "delete from " + CDAVersion.getMetaDataTableName(version) + " where dataset_id = " + setId;
        try {
            row = session.createSQLQuery(sql).executeUpdate();
        }
        catch (Exception e)
        {
            row=-1;
        }

        return row;
    }

    public int removeMetaData(String version, long metaDataId) {
        Session session = currentSession();
        String sql = "delete from " + CDAVersion.getMetaDataTableName(version) + " where id = " + metaDataId +" or dataset_id = "+metaDataId;
        return session.createSQLQuery(sql).executeUpdate();
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    private MetaData recordToMetaData(DataSet dataSet, Object[] record){
        MetaData metaData = new MetaData();

        metaData.setDataSet(dataSet);
        metaData.setId(Integer.parseInt(record[0].toString()));
        metaData.setDataSetId(Integer.parseInt(record[1].toString()));
        metaData.setCode(record[2].toString());
        metaData.setInnerCode(record[3].toString());
        metaData.setName(record[4].toString());
        metaData.setType(record[5].toString());
        metaData.setFormat(record[6].toString());
        metaData.setDictId(Long.parseLong(record[7].toString()));
        metaData.setDefinition(record[8]==null?"":record[8].toString());
        metaData.setNullable(Boolean.parseBoolean(record[9].toString()));
        metaData.setColumnType(record[10].toString());
        metaData.setColumnName(record[11].toString());
        metaData.setColumnLength(record[12].toString());
        metaData.setPrimaryKey(record[13] == null ? false :Boolean.parseBoolean(record[13].toString()));
        metaData.setHashCode(Integer.parseInt(record[14].toString()));
        metaData.setDictName(record[15] == null ? "" : record[15].toString());
        metaData.setDictCode(record[16] == null ? "" : record[16].toString());
        return metaData;
    }

    /**
     * 初始化和条件查询的方法
     * @param dataSet
     * @param from
     * @param count
     * @return
     */
    @Transactional(propagation= Propagation.SUPPORTS)
    public List<MetaDataModel> searchMetaDataList(DataSet dataSet,int from,int count) {
        CDAVersion cdaVersion = cdaVersionManager.getVersion(dataSet.getInnerVersionId());
        StringBuilder sb = new StringBuilder();

        sb.append("   select id,code, dataset_id,inner_code, name, type, format, dict_id, definition, nullable, column_type, column_name, column_length, primary_key, hash from ");
        sb.append(cdaVersion.getMetaDataTableName());
        sb.append("  where dataset_id= "+dataSet.getId()+"     ");

        if (!(dataSet.getCode() == null || dataSet.getCode().equals(""))) {

            sb.append("    and ( code like '"+"%"+ dataSet.getCode() +"%"+ "' ");
        }
        if (!(dataSet.getName() == null || dataSet.getName().equals(""))) {

            sb.append(" or name like '"+"%"+dataSet.getName()+"%"+"')");
        }

        String hql = sb.toString();

        Session session = currentSession();
        SQLQuery query = session.createSQLQuery(hql);

        query.setMaxResults(count);
        query.setFirstResult((from - 1)*count);

        List metaDataList = query.list();
        List<MetaDataModel> metaDataModelslist = new ArrayList<>();
       for(int i=0;i<metaDataList.size();i++){

            Object[] record = (Object[])metaDataList.get(i);
            MetaDataModel metaDataModel = new MetaDataModel();

            metaDataModel.setId((Integer)record[0]);
            metaDataModel.setCode((String) record[1]);
            metaDataModel.setDataSetIds((Integer) record[2]);
            metaDataModel.setInnerCode((String) record[3]);
            metaDataModel.setName((String) record[4]);
            metaDataModel.setType((String) record[5]);
            metaDataModel.setFormat((String) record[6]);
            metaDataModel.setDictId((Integer) record[7]);
            metaDataModel.setDefinition((String) record[8]);
           metaDataModel.setNullable((boolean)record[9]);
           metaDataModel.setColumnType((String) record[10]);
           metaDataModel.setColumnName((String)record[11]);
           metaDataModel.setColumnLength((String) record[12]);
           metaDataModel.setPrimaryKey((boolean) record[13]);
           metaDataModel.setHashCode((Integer) record[14]);

           String dictNames = getDictName(cdaVersion,(Integer)record[7]);
           metaDataModel.setDictName(dictNames);

           metaDataModelslist.add(metaDataModel);
        }

        return metaDataModelslist;
    }
    public String getDictName(CDAVersion version,Integer dictId){

        String strDictName = version.getDictTableName();
        String dictName=null;
        StringBuilder sb = new StringBuilder();

       sb.append("select name ");
        sb.append("from "+strDictName);
        sb.append(" where id = "+dictId);

        Session session = currentSession();
        String sql = sb.toString();
        SQLQuery query = session.createSQLQuery(sql);
       if(query.list().size()>0){
             dictName = query.list().get(0).toString();
        }else {
           dictName = "-";
           return dictName;
       }

        return dictName;
    }

    /**
     * 查询总条数的方法
     * @param dataSetModel
     * @return
     */
    @Transactional(propagation= Propagation.SUPPORTS)
    public Integer searchDataSetInt(DataSet dataSetModel) {
        Session session = currentSession();

        StringBuilder sb = new StringBuilder();

        sb.append("   select count(*) num from ");
        sb.append(CDAVersion.getMetaDataTableName(dataSetModel.getInnerVersionId()));
        sb.append("  where dataset_id= "+dataSetModel.getId()+"     ");

        if (!(dataSetModel.getCode() == null || dataSetModel.getCode().equals(""))) {

            sb.append("    and ( code like '"+"%"+ dataSetModel.getCode() +"%"+ "' ");
        }
        if (!(dataSetModel.getName() == null || dataSetModel.getName().equals(""))) {

            sb.append(" or name like '"+"%"+dataSetModel.getName()+"%"+"')");
        }

        String hql = sb.toString();

        Query query = session.createSQLQuery(hql);


        BigInteger bigInteger = (BigInteger)query.list().get(0);
        String num = bigInteger.toString();
        Integer totalCount = Integer.parseInt(num);
        return totalCount;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public List<MetaData> getMetaDatas(DataSet dataSet, long metaDataId) {

        String sql = "select t.id,t.dataset_id,t.code,t.inner_code, t.name, t.type, t.format, t.dict_id, t.definition,  t.nullable, t.column_type, t.column_name, t.column_length, t.primary_key, t.hash, d.name dict_name,d.code dict_code from " +
                CDAVersion.getMetaDataTableName(dataSet.getInnerVersionId()) + " t "+
                " left join "+ CDAVersion.getDictTableName(dataSet.getInnerVersionId())+" d on t.dict_id=d.id"+
                " where t.id =" + metaDataId;

        // 查询
        List<Object> records = currentSession().createSQLQuery(sql).list();

        List<MetaData> metaDataList = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); ++i){
            MetaData metaData = recordToMetaData(dataSet, (Object[])records.get(i));
            metaDataList.add(metaData);
        }

        return metaDataList;

    }

    /**
     *  批量删除数据源的方法
     * @param metaDataId
     * @param version
     * @return
     */
    @Transactional(propagation= Propagation.SUPPORTS)
    public int deleteMetaDatas(String metaDataId,String version){

        int iResult =0;
        try {
            Session session = currentSession();

            List<Integer> idList = new ArrayList<>();
            if (!StringUtil.isEmpty(metaDataId)) {
                metaDataId = metaDataId.substring(0, metaDataId.length());

                for (String id : metaDataId.split(",")) {
                    idList.add(Integer.parseInt(id));
                }
                if (idList.size()==0) {
                    idList.add(Integer.MIN_VALUE);
                }
            }


            String hql = "delete from " + CDAVersion.getMetaDataTableName(version) + " where id in (:listId)";
            SQLQuery query = session.createSQLQuery(hql);
            query.setParameterList("listId", idList);
            iResult=query.executeUpdate();
        }
        catch (Exception e)
        {
            iResult=-1;
        }

        return iResult;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public Integer getCountByCode(String versioncode,String innerCode,String strSetId) {

        String strTableName = CDAVersion.getMetaDataTableName(versioncode);

        Session session = currentSession();

        StringBuilder sb = new StringBuilder();

        sb.append("   select count(*) num from ");
        sb.append(strTableName);
        sb.append("  where dataset_id= :strSetId ");
        sb.append(" and inner_code =:innerCode");

        String hql = sb.toString();

        Query query = session.createSQLQuery(hql);
        query.setParameter("strSetId", strSetId);
        query.setParameter("innerCode", innerCode);
        BigInteger bigInteger = (BigInteger)query.list().get(0);
        String num = bigInteger.toString();
        Integer totalCount = Integer.parseInt(num);
        return totalCount;
    }

    @Transactional(propagation= Propagation.SUPPORTS)
    public Integer getCountByColumnName(String versioncode,String strColumnName,String strSetId) {

        String strTableName = CDAVersion.getMetaDataTableName(versioncode);

        Session session = currentSession();

        StringBuilder sb = new StringBuilder();

        sb.append("   select count(*) num from ");
        sb.append(strTableName);
        sb.append("  where dataset_id= :strSetId ");
        sb.append(" and column_name =:strColumnName");

        String hql = sb.toString();

        Query query = session.createSQLQuery(hql);
        query.setParameter("strSetId", strSetId);
        query.setParameter("strColumnName", strColumnName);
        BigInteger bigInteger = (BigInteger)query.list().get(0);
        String num = bigInteger.toString();
        Integer totalCount = Integer.parseInt(num);
        return totalCount;
    }

}