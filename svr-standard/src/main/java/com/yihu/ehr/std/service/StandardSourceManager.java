package com.yihu.ehr.std.service;

import com.yihu.ehr.std.data.SQLGeneralDAO;
import com.yihu.ehr.std.model.StandardSourceModel;
import com.yihu.ehr.util.operator.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author AndyCai
 * @version 1.0
 * @created 06-9月-2015 11:49:21
 */
@Transactional
@Service
public class StandardSourceManager extends SQLGeneralDAO {
    public static final String StandardSourceTableName = "std_standard_source";

    public StandardSourceManager() {
    }

    public void finalize() throws Throwable {
    }

    /**
     * 根据ID删除标准来源
     *
     * @param ids
     */
    public int deleteSource(List<String> ids) {
        int result = 0;
        try {
            String strIds = StringUtils.join(ids, "','");
            strIds = "'" + strIds + "'";

            Session session = currentSession();
            String sql = "delete from " + StandardSourceTableName + " where id in(" + strIds + ")";

            result = session.createSQLQuery(sql).executeUpdate();
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    /**
     * @param ids
     */
    public StandardSource[] getSourceById(List<String> ids) {
        Session session = currentSession();
        String strIds = StringUtils.join(ids, "','");
        strIds = "'" + strIds + "'";

        Query query = session.createSQLQuery(" select id,code,name,source_type,description,create_date,create_user,update_date,update_user " +
                "FROM " + StandardSourceTableName + " where id in (" + strIds + ");");

        List<Object> records = query.list();

        StandardSource[] xStandardSources = new StandardSource[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            StandardSource info = new StandardSource();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setSourceType(record[3].toString());
            info.setDescription(record[4] == null ? null : record[4].toString());
            info.setCreateDate((Date) record[5]);
            info.setCreateUser(record[6].toString());
            info.setUpdateDate(record[7] == null ? null : (Date) record[7]);
            info.setUpdateUser(record[8] == null ? null : record[8].toString());
            xStandardSources[i] = info;
        }

        return xStandardSources;
    }

    /**
     * 查询所有的资源列表
     */
    public StandardSource[] getSourceList() {
        Session session = currentSession();

        Query query = session.createSQLQuery(" select id,code,name,source_type,description,create_date,create_user,update_date,update_user " +
                "FROM " + StandardSourceTableName);

        List<Object> records = query.list();

        StandardSource[] xStandardSources = new StandardSource[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            StandardSource info = new StandardSource();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setSourceType(record[3].toString());
            info.setDescription(record[4] == null ? null : record[4].toString());
            info.setCreateDate((Date) record[5]);
            info.setCreateUser(record[6].toString());
            info.setUpdateDate(record[7] == null ? null : (Date) record[7]);
            info.setUpdateUser(record[8] == null ? null : record[8].toString());
            xStandardSources[i] = info;
        }

        return xStandardSources;
    }

    /**
     * @param id
     */
    public StandardSource getSourceBySingleId(String id) {
        Session session = currentSession();
        Query query = session.createSQLQuery(" select id,code,name,source_type,description,create_date,create_user,update_date,update_user " +
                "FROM " + StandardSourceTableName + " where id = (" + id + ");");

        List<Object> records = query.list();

        StandardSource[] xStandardSources = new StandardSource[records.size()];

        Object[] record = (Object[]) records.get(0);
        StandardSource info = new StandardSource();
        info.setId(record[0].toString());
        info.setCode(record[1].toString());
        info.setName(record[2].toString());
        info.setSourceType(record[3].toString());
        info.setDescription(record[4] == null ? null : record[4].toString());
        info.setCreateDate((Date) record[5]);
        info.setCreateUser(record[6].toString());
        info.setUpdateDate(record[7] == null ? null : (Date) record[7]);
        info.setUpdateUser(record[8] == null ? null : record[8].toString());

        return info;
    }

    /**
     * @param strKey
     */
    public StandardSource[] getSourceByKey(String strKey) {
        Session session = currentSession();

        String strSql = " select id,code,name,source_type,description,create_date,create_user,update_date,update_user " +
                "FROM " + StandardSourceTableName;
        if (strKey != null && !strKey.equals("")) {
            strSql += " where code like '%" + strKey + "%' or name like '%" + strKey + "%'";
        }

        Query query = session.createSQLQuery(strSql);

        List<Object> records = query.list();

        StandardSource[] xStandardSources = new StandardSource[records.size()];

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            StandardSource info = new StandardSource();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setSourceType(record[3].toString());
            info.setDescription(record[4] == null ? null : record[4].toString());
            info.setCreateDate((Date) record[5]);
            info.setCreateUser(record[6].toString());
            info.setUpdateDate(record[7] == null ? null : (Date) record[7]);
            info.setUpdateUser(record[8] == null ? null : record[8].toString());
            xStandardSources[i] = info;
        }

        return xStandardSources;
    }


    public StandardSourceModel getSourceByKey(StandardSource standardSource){
        StandardSourceModel standardSourceModel = new StandardSourceModel();
        standardSourceModel.setId(standardSource.getId());
        standardSourceModel.setCode(standardSource.getCode());
        standardSourceModel.setName(standardSource.getName());
        standardSourceModel.setCreateDate(DateUtil.toString(standardSource.getCreateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
        standardSourceModel.setCreateUser(standardSource.getCreateUser());
        standardSourceModel.setUpdateDate(DateUtil.toString(standardSource.getUpdateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
        standardSourceModel.setUpdateUser(standardSource.getUpdateUser());
        standardSourceModel.setDescription(standardSource.getDescription());
        return standardSourceModel;
    }


    public List<StandardSource> getSourceByKey(String strKey, int page, int rows) {
        Session session = currentSession();

        String strSql = " select id,code,name,source_type,description,create_date,create_user,update_date,update_user " +
                "FROM " + StandardSourceTableName;
        if (strKey != null && !strKey.equals("")) {
            strSql += " where code like '%" + strKey + "%' or name like '%" + strKey + "%'";
        }

        Query query = session.createSQLQuery(strSql);
        query.setMaxResults(rows);
        query.setFirstResult((page - 1) * rows);
        List<Object> records = query.list();

        List<StandardSource> xStandardSources = new ArrayList<>();
        ;

        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            StandardSource info = new StandardSource();
            info.setId(record[0].toString());
            info.setCode(record[1].toString());
            info.setName(record[2].toString());
            info.setSourceType(record[3].toString());
            info.setDescription(record[4] == null ? null : record[4].toString());
            info.setCreateDate((Date) record[5]);
            info.setCreateUser(record[6].toString());
            info.setUpdateDate(record[7] == null ? null : (Date) record[7]);
            info.setUpdateUser(record[8] == null ? null : record[8].toString());
            xStandardSources.add(info);
        }
        return xStandardSources;
    }


    public Integer getSourceByKeyInt(String strKey, int page, int rows) {
        Session session = currentSession();

        String strSql = " select 1 FROM " + StandardSourceTableName;
        if (strKey != null && !strKey.equals("")) {
            strSql += " where code like '%" + strKey + "%' or name like '%" + strKey + "%'";
        }

        Query query = session.createSQLQuery(strSql);
        return query.list().size();
    }

    public boolean isSourceCodeExist(String code) {
        Session session = currentSession();
        String strSql = " select 1 FROM " + StandardSourceTableName;
        strSql += " where code = '" + code + "'";
        Query query = session.createSQLQuery(strSql);
        return query.list().size()>0?true:false;
    }

    public int saveSourceInfo(StandardSource xStandardSource) {

        int result = 0;
        try {
            StandardSource info = (StandardSource) xStandardSource;
            Session session = currentSession();

            String sql;
            Query query;

            List<String> ids = new ArrayList<>();
            ids.add(info.getId());
            StandardSource[] xStandardSources = getSourceById(ids);

            if (xStandardSources.length == 0) {

                sql = "insert into " + StandardSourceTableName +
                        " ( id,code,name,source_type,description,create_date,create_user,update_date,update_user,hash)  " +
                        "values(:id, :code, :name, :source_type, :description, :create_date, :create_user, :update_date, :update_user,:hash)";
            } else {
                sql = "update " + StandardSourceTableName +
                        " set " +
                        "code = :code, " +
                        "name = :name, " +
                        "source_type = :source_type, " +
                        "description = :description, " +
                        "create_date = :create_date, " +
                        "create_user = :create_user, " +
                        "update_date=:update_date," +
                        "update_user=:update_user," +
                        "hash=:hash" +
                        " where id = :id";
            }

            query = session.createSQLQuery(sql);
            query.setString("id", info.getId());
            query.setString("code", info.getCode());
            query.setString("name", info.getName());
            query.setString("source_type", info.getSourceType());
            query.setString("description", info.getDescription());
            query.setDate("create_date", info.getCreateDate());
            query.setString("create_user", info.getCreateUser());
            query.setDate("update_date", info.getUpdateDate());
            query.setString("update_user", info.getUpdateUser());
            query.setInteger("hash", info.getHashCode());
            result = query.executeUpdate();
        } catch (Exception ex) {
            result = -1;
        }
        return result;

    }
}