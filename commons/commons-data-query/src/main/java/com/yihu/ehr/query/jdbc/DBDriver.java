package com.yihu.ehr.query.jdbc;


import com.yihu.ehr.query.common.enums.DBType;

/**
 * 数据库驱动
 * Created by hzp on 20160324.
 */
public class DBDriver {

    /**
     * 驱动注册
     */
    public static void registerDriver(String uri) {
        try {
            Class.forName(DBDriver.getDriver(uri));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取驱动类型
     * @return
     */
    public static String getDriver(String uri) {

        if (uri.startsWith("jdbc:mysql"))
        {
            return "com.mysql.jdbc.Driver";
        }
        else if (uri.startsWith("jdbc:oracle")) {

            return "oracle.jdbc.driver.OracleDriver";
        }
        else if (uri.startsWith("jdbc:hive2")) {

            return "org.apache.hive.jdbc.HiveDriver";
        }
        else if (uri.startsWith("jdbc:microsoft:sqlserver")) {

            return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        }
        else if (uri.startsWith("jdbc:sqlite")) {

            return "org.sqlite.JDBC";
        }
        else
        {
            return "";
        }
    }

    /**
     * 获取数据库类型
     * @return
     */
    public static DBType getDbType(String uri){

        if (uri.startsWith("jdbc:mysql")) {

            return DBType.Mysql;
        }
        else if (uri.startsWith("jdbc:oracle")) {

            return DBType.Oracle;
        }
        else if (uri.startsWith("jdbc:hive2")) {

            return DBType.Hive;
        }
        else if (uri.startsWith("jdbc:microsoft:sqlserver")) {

            return DBType.Sqlserver;
        }
        else if (uri.startsWith("jdbc:sqlite")) {

            return DBType.Sqlite;
        }
        else{
            return null;
        }

    }
}
