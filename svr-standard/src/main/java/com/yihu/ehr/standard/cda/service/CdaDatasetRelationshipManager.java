package com.yihu.ehr.standard.cda.service;
import org.apache.commons.lang.StringUtils;
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
public class CdaDatasetRelationshipManager{


    @PersistenceContext
    private EntityManager entityManager;

    Session currentSession(){
        return entityManager.unwrap(org.hibernate.Session.class);
    }

    /**
     * 根据关系ID删除关联关系
     *
     * @param ids
     */
    public int deleteRelationshipById(String strVersion, List<String> ids) {

        int result = 0;
        try {
            String strIds = StringUtils.join(ids, "','");
            strIds = "'" + strIds + "'";

            Session session = currentSession();
            String sql = "delete from " + CDAVersion.getCDADatasetRelationshipTableName(strVersion) + " where id in(" + strIds + ")";
            result = session.createSQLQuery(sql).executeUpdate();
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    /**
     * 根据CDAID删除CDA数据集关联关系
     *
     * @param strVersion
     * @param cdaIds
     */
    public int deleteRelationshipByCdaId(String strVersion, List<String> cdaIds) {

        int result = 0;
        try {
            String strIds = StringUtils.join(cdaIds, "','");
            strIds = "'" + strIds + "'";

            Session session = currentSession();
            String sql = "delete from " + CDAVersion.getCDADatasetRelationshipTableName(strVersion) + " where cda_id in(" + strIds + ")";
            result = session.createSQLQuery(sql).executeUpdate();
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    /**
     * 根据CDAID获取关联关系
     *
     * @param strCdaId
     * @param strVersionCode
     */
    public List<CdaDatasetRelationship> getRelationshipByCdaId(String strCdaId, String strVersionCode, String strKey, Integer page, Integer pageSize) {

        List<CdaDatasetRelationship> infos = null;
        try {

        } catch (Exception ex) {
            int i = 0;
        }Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);
        String strDatasetTable = CDAVersion.getDataSetTableName(strVersionCode);

        String strSql = "SELECT t.id,t.cda_id,t.dataset_id,d.code,d.name,d.summary\n" +
                " from " + strTableName + " t \n" +
                " join " + strDatasetTable + " d on t.dataset_id=d.id" +
                " where t.cda_id = :cda_id ";

        if (strKey != null && strKey != "") {
            strSql += " and (d.code like :strkey or d.name like :strkey)";
        }
        Query query = session.createSQLQuery(strSql);
        if (strKey != null && strKey != "")
            query.setString("strkey", "%" + strKey + "%");

        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);

        query.setString("cda_id", strCdaId);

        List<Object> records = query.list();

        infos = new ArrayList<>();

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CdaDatasetRelationship info = new CdaDatasetRelationship();
            info.setId(record[0].toString());
            info.setCdaId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersionCode(strVersionCode);
            info.setDataSetCode(record[3].toString());
            info.setDataSetName(record[4].toString());
            info.setSummary(record[5].toString());
            infos.add(info);
        }

        return infos;
    }

    /*
    *
    *
    * */
    public int getRelationshipCountByCdaId(String strCdaId, String strVersionCode, String strKey) {

        int iCount = 0;
        try {
            Session session = currentSession();
            String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);
            String strDatasetTable = CDAVersion.getDataSetTableName(strVersionCode);

            String strSql = "SELECT t.id,t.cda_id,t.dataset_id,d.code,d.name,d.summary\n" +
                    " from " + strTableName + " t\n" +
                    " join " + strDatasetTable + " d on t.dataset_id=d.id" +
                    " where t.cda_id = :cda_id ";

            if (strKey != null && strKey != "") {
                strSql += " and (d.code like :key or d.name like :key)";
            }
            Query query = session.createSQLQuery(strSql);
            if (strKey != null && strKey != "")
                query.setString("key", "%" + strKey + "%");
            query.setString("cda_id", strCdaId);

            List<Object> records = query.list();

            iCount = records.size();
        } catch (Exception ex) {

        }

        return iCount;
    }

    /**
     * 根据CDAID获取关联关系
     *
     * @param strCdaId
     * @param strVersionCode
     */
    public CdaDatasetRelationship[] getRelationshipByCdaId(String strCdaId, String strVersionCode) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);
        String strDatasetTable = CDAVersion.getDataSetTableName(strVersionCode);

        String strSql = "SELECT t.id,t.cda_id,t.dataset_id,d.code,d.name,d.summary\n" +
                " from " + strTableName + " t\n" +
                " join " + strDatasetTable + " d on t.dataset_id=d.id" +
                " where t.cda_id = :cda_id ";

        Query query = session.createSQLQuery(strSql);

        query.setString("cda_id", strCdaId);

        List<Object> records = query.list();

        CdaDatasetRelationship[] infos = new CdaDatasetRelationship[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CdaDatasetRelationship info = new CdaDatasetRelationship();
            info.setId(record[0].toString());
            info.setCdaId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersionCode(strVersionCode);
            info.setDataSetCode(record[3].toString());
            info.setDataSetName(record[4].toString());
            info.setSummary(record[5].toString());

            infos[i] = info;
        }

        return infos;
    }

    /**
     * 根据关系ID获取关联关系
     *
     * @param strVersionCode
     * @param Ids
     */
    public CdaDatasetRelationship[] getRelationshipById(String strVersionCode, List<String> Ids) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);


        String strIds = StringUtils.join(Ids, "','");
        strIds = "'" + strIds + "'";

        Query query = session.createSQLQuery("SELECT t.id,t.cda_id,t.dataset_id " +
                "FROM " + strTableName + " t where t.id in(" + strIds + ");");


        List<Object> records = query.list();

        CdaDatasetRelationship[] infos = new CdaDatasetRelationship[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CdaDatasetRelationship info = new CdaDatasetRelationship();
            info.setId(record[0].toString());
            info.setCdaId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersionCode(strVersionCode);

            infos[i] = info;
        }

        return infos;
    }

    /**
     * 新增关联关系
     *
     * @param xCdaDatasetRelationships
     */
    public int addRelationship(CdaDatasetRelationship[] xCdaDatasetRelationships) {

        int result = 0;
        try {
            if (xCdaDatasetRelationships.length <= 0)
                return -1;
            Session session = currentSession();

            String strTableName = CDAVersion.getCDADatasetRelationshipTableName(xCdaDatasetRelationships[0].getVersionCode());
            String sql;
            Query query;

            String strValues = "";
            for (int i = 0; i < xCdaDatasetRelationships.length; i++) {
                CdaDatasetRelationship info = (CdaDatasetRelationship) xCdaDatasetRelationships[i];

                if (info.getId() == "" || info.getId() == null) {

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

            result = query.executeUpdate();
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    public CdaDatasetRelationship[] getRelationshipByVersion(String strVersionCode) {
        Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);

        Query query = session.createSQLQuery("SELECT id,cda_id,dataset_id " +
                "FROM " + strTableName);


        List<Object> records = query.list();

        CdaDatasetRelationship[] infos = new CdaDatasetRelationship[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CdaDatasetRelationship info = new CdaDatasetRelationship();
            info.setId(record[0].toString());
            info.setCdaId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersionCode(strVersionCode);

            infos[i] = info;
        }

        return infos;
    }

}//end CdaDatasetRelationshipManager