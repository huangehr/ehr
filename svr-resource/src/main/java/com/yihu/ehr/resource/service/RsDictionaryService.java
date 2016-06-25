package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsDictionaryDao;
import com.yihu.ehr.resource.model.RsDictionary;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsDictionaryService extends BaseJpaService<RsDictionary, RsDictionaryDao>  {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private RsDictionaryDao dictionaryDao;


    public RsDictionary findById(int id) {
        return dictionaryDao.findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void batchInsertDictsAndEntry(List<Map<String, Object>> models) throws SQLException {
        Session session = null;
        Transaction transaction =null;
        Connection connection = null;
        try {
            session = currentSession().getSessionFactory().openSession();
            transaction = session.beginTransaction();

            String title = "INSERT INTO rs_dictionary(code, name, description) VALUES ";
            StringBuilder sql = new StringBuilder(title);
            int i = 1;
            List<Map<String, Object>> dictEntry = new ArrayList<>();
            //插入字典
            for(Map<String, Object> map: models){
                sql.append("('"+  map.get("code") +"'");
                sql.append(",'"+  map.get("name") +"'");
                sql.append(",'"+  null2Space(map.get("description")) +"')");

                if(i%100==0 || i == models.size()){
                    session.createSQLQuery(sql.toString()).executeUpdate();
                    sql = new StringBuilder(title) ;
                }
                else
                    sql.append(",");

                dictEntry.addAll((List)map.get("children"));
                i++;
            }

            //插入字典项
            title = "INSERT INTO rs_dictionary_entry(dict_code, code, name, description) VALUES ";
            sql = new StringBuilder(title);
            i = 1;
            for(Map<String, Object> map: dictEntry){
                sql.append("('"+ map.get("dictCode") +"'");
                sql.append(",'"+  map.get("code") +"'");
                sql.append(",'"+  map.get("name") +"'");
                sql.append(",'"+  null2Space(map.get("description")) +"')");

                if(i%100==0 || i == dictEntry.size()){
                    session.createSQLQuery(sql.toString()).executeUpdate();
                    sql = new StringBuilder(title) ;
                }
                else
                    sql.append(",");
                i++;
            }
            transaction.commit();
            session.close();

            //更新字典项关联字典字段
            sql = new StringBuilder("UPDATE rs_dictionary_entry e,rs_dictionary d SET e.dict_id =d.id WHERE e.dict_code=d.code AND e.dict_id=0");
            jdbcTemplate.execute(sql.toString());
        } catch (Exception e){
            if(transaction!=null)transaction.rollback();
            if(session!=null) session.close();
            if(connection!=null)connection.close();
            throw e;
        }
    }

    public Object null2Space(Object str){
        return str==null? "" : str;
    }
    /**
     * 查询编码是否已存在， 返回已存在资源标准编码
     */
    public List codeExist(String[] codes)
    {
        String sql = "SELECT DISTINCT code FROM rs_dictionary WHERE code in(:codes)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("codes", codes);
        return sqlQuery.list();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RsDictionary insert(RsDictionary model) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement pst = connection.prepareStatement(
                        "INSERT INTO rs_dictionary(code, name, description) VALUES(?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, model.getCode());
                pst.setString(2, model.getName());
                pst.setString(3, model.getDescription());
                return pst;
            }
        }, keyHolder);
        model.setId(keyHolder.getKey().intValue());
        return model;
    }
}
