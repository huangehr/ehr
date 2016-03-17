package com.yihu.ehr.standard.document.service;

import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author AndyCai
 * @version 1.0
 * @created 02-9月-2015 14:00:55
 */
@Service
public class CDADataSetRelationshipManager {


    @PersistenceContext
    private EntityManager entityManager;

    Session currentSession(){
        return entityManager.unwrap(Session.class);
    }

    /**
     * 根据关系ID删除关联关系
     *
     * @param ids
     */
    public boolean deleteRelationshipById(String strVersion, String[] ids) {
        Session session = currentSession();
        String sql = "delete from " + CDAVersionUtil.getCDADatasetRelationshipTableName(strVersion) + " where id in(:ids)";
        Query query = session.createSQLQuery(sql);
        query.setParameterList("ids",ids)
        .executeUpdate();
        return true;
    }

    /**
     * 根据CDAID删除CDA数据集关联关系
     * @param strVersion
     * @param cdaIds
     */
    public void deleteRelationshipByCdaId(String strVersion,String[] cdaIds) {
        Session session = currentSession();
        String sql = "delete from " + CDAVersionUtil.getCDADatasetRelationshipTableName(strVersion) + " where cda_id in(:cdaIds)";
        Query query = session.createSQLQuery(sql);
        query.setParameterList("cdaIds",cdaIds)
        .executeUpdate();
    }

    /**
     * 根据CDAID获取关联关系
     * @param cdaId
     * @param versionCode
     * @param page
     * @param pageSize
     * @return
     */
    public List<CDADataSetRelationship> getCDADataSetRelationshipByCDAId(String versionCode,String cdaId,int page,int pageSize) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDADatasetRelationshipTableName(versionCode);
        String strSql = "SELECT t.id," +
                "t.cda_id," +
                "t.dataSet_id" +
                " from " + strTableName + " t where  t.cda_id = :cdaId ";
        Query query = session.createSQLQuery(strSql);
        if(page!=0 && pageSize!=0){
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }
        query.setString("cdaId", cdaId);
        List<Object> records = query.list();
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