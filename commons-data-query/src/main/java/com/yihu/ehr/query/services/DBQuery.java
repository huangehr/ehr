package com.yihu.ehr.query.services;

import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.common.model.QueryEntity;
import com.yihu.ehr.query.common.sqlparser.*;
import com.yihu.ehr.query.jdbc.DBHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * JDBC查询类
 * Created by hzp on 2015/11/28.
 */
@Service
public class DBQuery {
    private DBHelper db;
    private ParserSql ps;

    public DBHelper getDB() throws Exception
    {
        if(db==null)
        {
            db.connect();
        }
        return db;
    }

    public ParserSql getParserSql() throws Exception
    {
        switch (getDB().dbType)
        {
            case Oracle:
                ps = new ParserOracle();
                break;
            case Sqlserver:
                ps = new ParserSqlserver();
                break;
            case Hive:
                ps = new ParserHive();
                break;
            case Sqlite:
                ps = new ParserSqlite();
                break;
            default:
                ps = new ParserMysql();
        }
        return ps;
    }

    /**
     * 初始化连接
     */
    public void connect() throws Exception
    {
        db.connect();
    }

    /**
     * 自定义连接
     * uri包含用户名/密码
     */
    public void connect(String name,String uri) throws Exception
    {
        db.connect(name,uri);
    }

    /**
     * 自定义连接
     */
    public void connect(String name,String uri,String user,String password) throws Exception
    {
        db.connect(name,uri,user,password);
    }


    /**************************************************************************/
    /**
     * 查询单条记录
     */
    public Map<String,Object> load(QueryEntity qe) throws Exception
    {
        String sql = ps.getSql(qe);
        return db.load(sql);
    }

    /**
     * 查询单实体
     */
    public <T> T load(Class<T> cls,QueryEntity qe) throws Exception
    {
        String sql = ps.getSql(qe);
        return db.load(cls,sql);
    }

    /**
     * 查询实体列表
     */
    public DataList query(QueryEntity qe) throws Exception
    {
        DataList list = new DataList(qe.getPage(),qe.getRows());
        list.setName(qe.getTableName());

        int count = count(qe);
        list.setCount(count);

        if(count!=0)
        {
            String sql = ps.getSql(qe);
            list.setList(db.query(sql));
        }

        return list;
    }

    /**
     * 查询实体列表
     */
    public <T> List<T> query(Class<T> cls,QueryEntity qe) throws Exception
    {
        String sql = ps.getSql(qe);
        return db.query(cls, sql);
    }

    /**
     * 获取总条数
     * @return
     */
    public int count(QueryEntity qe) throws Exception
    {
        String sqlCount = ps.getCountSql(qe);
        return Integer.parseInt(String.valueOf(db.scalar(sqlCount)));
    }

    /********************** SQL语句 ************************************/
    /**
     * SQL查询
     * @param sql
     * @return
     * @throws Exception
     */
    public DataList queryBySql(String sql) throws Exception {
        DataList re = new DataList();
        List<Map<String,Object>> list = db.query(sql);
        re.setList(list);
        return re;
    }

    /**
     * SQL分页查询
     * @param sql
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public DataList queryBySql(String sql,int page,int size) throws Exception {
        DataList re = new DataList(page,size);
        String sqlPage = ps.getPageSql(sql,page,size);
        String sqlCount = ps.getCountSql(sql);
        List<Map<String,Object>> list = db.query(sqlPage);
        re.setList(list);
        Integer count = (Integer)db.scalar(sqlCount);
        re.setCount(count);
        return re;
    }
}
