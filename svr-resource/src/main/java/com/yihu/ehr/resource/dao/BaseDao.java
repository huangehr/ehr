package com.yihu.ehr.resource.dao;


import com.yihu.ehr.dbhelper.common.DBQuery;
import com.yihu.ehr.dbhelper.jdbc.DBHelper;
import org.springframework.beans.factory.annotation.Value;


/**
 * Created by hzp on 2016/4/19.
 */
public class BaseDao {
    @Value("${data-source.url}")
    String dbUri;

    @Value("${data-source.user-name}")
    String dbUserName;

    @Value("${data-source.password}")
    String dbPassword;


    private DBHelper _db;
    protected DBHelper getDB()
    {
        if(_db==null)
        {
            _db = new DBHelper("svr-resource",dbUri,dbUserName,dbPassword);
        }
        return _db;
    }

    private DBQuery _qe;
    protected DBQuery getQE()
    {
        if(_qe==null)
        {
            _qe = new DBQuery("svr-resource",dbUri,dbUserName,dbPassword);
        }
        return _qe;
    }


}
