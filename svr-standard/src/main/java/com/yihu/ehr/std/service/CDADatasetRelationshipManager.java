package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.std.data.SQLGeneralDAO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author AndyCai
 * @version 1.0
 * @created 02-9月-2015 14:00:55
 */
@Service
public class CDADatasetRelationshipManager extends SQLGeneralDAO {

    public CDADatasetRelationshipManager() {

    }

    public void finalize() throws Throwable {

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
    public int deleteRelationshipByCDAId(String strVersion, List<String> cdaIds) {

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
     * @param strCDAId
     * @param strVersionCode
     */
    public CDADatasetRelationship[] getRelationshipByCDAId(String strCDAId, String strVersionCode, String strKey, Integer page, Integer pageSize) {

        CDADatasetRelationship[] infos = null;
        try {
            Session session = currentSession();
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

            query.setString("cda_id", strCDAId);

            List<Object> records = query.list();

            infos = new CDADatasetRelationship[records.size()];

            for (int i = 0; i < records.size(); ++i) {
                Object[] record = (Object[]) records.get(i);

                CDADatasetRelationship info = new CDADatasetRelationship();
                info.setId(record[0].toString());
                info.setCDAId(record[1].toString());
                info.setDatasetId(record[2].toString());
                info.setVersion_code(strVersionCode);
                info.setDataset_code(record[3].toString());
                info.setDataset_name(record[4].toString());
                info.setSummary(record[5].toString());
                infos[i] = info;
            }
        } catch (Exception ex) {
            int i = 0;
        }

        return infos;
    }

    /*
    *
    *
    * */
    public int getRelationshipCountByCDAId(String strCDAId, String strVersionCode, String strKey) {

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
            query.setString("cda_id", strCDAId);

            List<Object> records = query.list();

            iCount = records.size();
        } catch (Exception ex) {

        }

        return iCount;
    }

    /**
     * 根据CDAID获取关联关系
     *
     * @param strCDAId
     * @param strVersionCode
     */
    public CDADatasetRelationship[] getRelationshipByCDAId(String strCDAId, String strVersionCode) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);
        String strDatasetTable = CDAVersion.getDataSetTableName(strVersionCode);

        String strSql = "SELECT t.id,t.cda_id,t.dataset_id,d.code,d.name,d.summary\n" +
                " from " + strTableName + " t\n" +
                " join " + strDatasetTable + " d on t.dataset_id=d.id" +
                " where t.cda_id = :cda_id ";

        Query query = session.createSQLQuery(strSql);

        query.setString("cda_id", strCDAId);

        List<Object> records = query.list();

        CDADatasetRelationship[] infos = new CDADatasetRelationship[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CDADatasetRelationship info = new CDADatasetRelationship();
            info.setId(record[0].toString());
            info.setCDAId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersion_code(strVersionCode);
            info.setDataset_code(record[3].toString());
            info.setDataset_name(record[4].toString());
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
    public CDADatasetRelationship[] getRelationshipById(String strVersionCode, List<String> Ids) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);


        String strIds = StringUtils.join(Ids, "','");
        strIds = "'" + strIds + "'";

        Query query = session.createSQLQuery("SELECT t.id,t.cda_id,t.dataset_id " +
                "FROM " + strTableName + " t where t.id in(" + strIds + ");");


        List<Object> records = query.list();

        CDADatasetRelationship[] infos = new CDADatasetRelationship[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CDADatasetRelationship info = new CDADatasetRelationship();
            info.setId(record[0].toString());
            info.setCDAId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersion_code(strVersionCode);
            infos[i] = info;
        }

        return infos;
    }

    /**
     * 新增关联关系
     *
     * @param xCDADatasetRelationships
     */
    public int addRelationship(CDADatasetRelationship[] xCDADatasetRelationships) {

        int result = 0;
        try {
            if (xCDADatasetRelationships.length <= 0)
                return -1;
            Session session = currentSession();

            String strTableName = CDAVersion.getCDADatasetRelationshipTableName(xCDADatasetRelationships[0].getVersion_code());
            String sql;
            Query query;

            String strValues = "";
            for (int i = 0; i < xCDADatasetRelationships.length; i++) {
                CDADatasetRelationship info = (CDADatasetRelationship) xCDADatasetRelationships[i];

                if (info.getId() == "" || info.getId() == null) {

                    String strId = UUID.randomUUID().toString();
                    info.setId(strId);

                }

                strValues += "('" + info.getId() + "','" + info.getCDAId() + "','" + info.getDataset_id() + "'),";
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

    public CDADatasetRelationship[] getRelationshipByVersion(String strVersionCode) {
        Session session = currentSession();
        String strTableName = CDAVersion.getCDADatasetRelationshipTableName(strVersionCode);

        Query query = session.createSQLQuery("SELECT id,cda_id,dataset_id " +
                "FROM " + strTableName);


        List<Object> records = query.list();

        CDADatasetRelationship[] infos = new CDADatasetRelationship[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            CDADatasetRelationship info = new CDADatasetRelationship();
            info.setId(record[0].toString());
            info.setCDAId(record[1].toString());
            info.setDatasetId(record[2].toString());
            info.setVersion_code(strVersionCode);
            infos[i] = info;
        }

        return infos;
    }

}//end CDADatasetRelationshipManager