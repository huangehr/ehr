package com.yihu.ehr.standard.document.service;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author AndyCai
 * @version 1.0
 * @created 02-9月-2015 14:00:55
 */
@Service
public class CDADataSetRelationshipManager extends BaseHbmService<ICDADataSetRelationship> {

    private final static String ENTITY_PRE = "com.yihu.ehr.standard.document.service.CDADataSetRelationship";


    public Class getRelationshipServiceEntity(String version){
        try {
            return Class.forName(ENTITY_PRE + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "CDA数据集关系", version);
        }
    }

    /**
     * 根据关系ID删除关联关系
     *
     * @param ids
     */
    public boolean deleteRelationshipById(String version, String[] ids) {
        delete(ids, getRelationshipServiceEntity(version));
        return true;
    }

    /**
     * 根据CDAID删除CDA数据集关联关系
     * @param version
     * @param cdaIds
     */
    public void deleteRelationshipByCdaIds(String version,String[] cdaIds) {
        Class entityClass = getRelationshipServiceEntity(version);
        List<ICDADataSetRelationship> ls = search(entityClass, "cdaId="+ String.join(", ", cdaIds));
        String ids = "";
        for(int i = 0;i<ls.size();i++){
            ICDADataSetRelationship cdaDataSetRelationship = ls.get(i);
            if(i==0){
                ids+=cdaDataSetRelationship.getId();
            }else {
                ids+=","+cdaDataSetRelationship.getId();
            }
            //ids += cdaDataSetRelationship.getId();
        }

        delete(ids.split(","), getRelationshipServiceEntity(version));
//        Session session = currentSession();
//        String sql = "delete from " + CDAVersionUtil.getCDADatasetRelationshipTableName(strVersion) + " where cda_id in(:cdaIds)";
//        Query query = session.createSQLQuery(sql);
//        query.setParameterList("cdaIds",cdaIds)
//        .executeUpdate();
    }

    /**
     * 根据CDAID获取关联关系
     * @param entityClass
     * @param fields
     * @param filters
     * @param sorts
     * @param size
     * @param page
     * @return
     */
    public List getCDADataSetRelationships(Class entityClass,String fields,String filters,String sorts,int size,int page) {

        List ls = search(entityClass, fields, filters, sorts, page, size);
        return ls;
    }

    public List getCDADataSetRelationshipByCDAId(Class entityClass,String cdaId) {

        List ls = search(entityClass, "cdaId="+cdaId);
        return ls;
    }


    /**
     * 获取关联关系
     */
    public List<CDADataSetRelationship> getCDADataSetRelationship(String versionCode) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDADatasetRelationshipTableName(versionCode);
        String strSql = "SELECT t.id," +
                "t.cda_id," +
                "t.dataSet_id" +
                " from " + strTableName + " t ";
        List<Object> records = session.createSQLQuery(strSql).list();
        List<CDADataSetRelationship> infos = new ArrayList<>();
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            CDADataSetRelationship info = new CDADataSetRelationship();
            info.setId(record[0].toString());
            info.setCdaId(record[1].toString());
            info.setDataSetId(record[2].toString());
            infos.add(info);
        }
        return infos;
    }

    public int getRelationshipCountByCdaId(String cdaId,String VersionCode) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDADatasetRelationshipTableName(VersionCode);
        String strSql = "SELECT t.id," +
                "t.cda_id," +
                "t.dataSet_id" +
                " from " + strTableName + " t where  t.cda_id = :cdaId ";
        Query query = session.createSQLQuery(strSql);
        query.setString("cdaId", cdaId);
        List<Object> records = query.list();
        return records.size();
    }


    /**
     * 新增关联关系
     *
     * @param cdaDatasetRelationships
     */
    public boolean addRelationship(List<CDADataSetRelationship> cdaDatasetRelationships,String versionCode) {
        Session session = currentSession();

        String strTableName = CDAVersionUtil.getCDADatasetRelationshipTableName(versionCode);
        String sql;
        Query query;

        String strValues = "";
        for (int i = 0; i < cdaDatasetRelationships.size(); i++) {
            CDADataSetRelationship info = (CDADataSetRelationship) cdaDatasetRelationships.get(i);
            if (org.springframework.util.StringUtils.isEmpty(info.getId())) {
                String strId = UUID.randomUUID().toString();
                info.setId(strId);
            }
            strValues += "('" + info.getId() + "','" + info.getCdaId() + "','" + info.getDataSetId() + "'),";
        }
        strValues = strValues.substring(0, strValues.length() - 1);

        sql = "insert into " + strTableName +
                " (id,cda_id,dataset_id) " +
                "values " + strValues;
        query = session.createSQLQuery(sql);
        query.executeUpdate();
        return true;
    }


}