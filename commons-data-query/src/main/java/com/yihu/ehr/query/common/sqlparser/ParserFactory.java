package com.yihu.ehr.query.common.sqlparser;


import com.yihu.ehr.query.common.enums.DBType;
import com.yihu.ehr.query.common.model.QueryEntity;

/**
 * sql解析工厂类
 * Created by hzp on 2016/05/24.
 */
public class ParserFactory  {

    /**
     * 默认Mysql数据库
     * @return
     */
    public static ParserSql getParserSql()
    {
        return new ParserMysql();
    }

    /**
     * 根据数据库类型获取SQL解析器
     * @param type
     * @return
     */
    public static ParserSql getParserSql(DBType type)
    {
        ParserSql ps = null;
        switch (type)
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


}
