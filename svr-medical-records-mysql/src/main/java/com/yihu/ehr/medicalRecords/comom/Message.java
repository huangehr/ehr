package com.yihu.ehr.medicalRecords.comom;

/**
 * Created by hzp on 2016/8/2.
 */
public class Message {

    /**
     * 打印错误
     * @param msg
     * @throws Exception
     */
    public static void debug(String msg)
    {
        System.out.print(msg);
    }

    /**
     * 打印错误，并抛出异常
     * @param msg
     * @throws Exception
     */
    public static void error(String msg) throws Exception
    {
        System.out.print(msg);
        throw new Exception(msg);
    }

    /**
     * 打印错误，并抛出异常,log4j日志
     * @param msg
     * @throws Exception
     */
    public static void logError(String msg) throws Exception
    {
        System.out.print(msg);
        //log4j日志

        throw new Exception(msg);
    }
}
