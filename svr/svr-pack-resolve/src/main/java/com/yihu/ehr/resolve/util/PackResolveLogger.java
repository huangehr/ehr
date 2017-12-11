package com.yihu.ehr.resolve.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Logger - 日志
 * 档案入库的日志记录器
 */
public class PackResolveLogger {

    private static Logger logger = LoggerFactory.getLogger(PackResolveLogger.class);

    /**
     * @param info
     */
    public static void info(String info) {
        logger.info(info);
    }

    /**
     *
     * @param warn
     */
    public static void warn(String warn) {
        logger.warn(warn);
    }

    /**
     *
     * @param error
     */
    public static void error(String error) {
        logger.error(error);
    }

    /**
    public static void info(String caller, JsonNode info) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode log = objectMapper.createObjectNode();
            log.put("caller", caller);
            log.put("time", DateTimeUtil.simpleDateTimeFormat(new Date()));
            log.put("logType", "3"); // TODO - 2017.7.4 张进军 - 待确定。
            log.set("data", info);
            logger.info(log.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */

}