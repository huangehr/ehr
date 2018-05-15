package com.yihu.ehr.resolve.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Logger - 日志
 * 档案入库的日志记录器
 */
public class PackResolveLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackResolveLogger.class);

    /**
     * @param info
     */
    public static void info(String info) {
        LOGGER.info(info);
    }

    /**
     *
     * @param warn
     */
    public static void warn(String warn) {
        LOGGER.warn(warn);
    }

    /**
     *
     * @param error
     */
    public static void error(String error) {
        LOGGER.error(error);
    }

    /**
     *
     * @param error
     * @param e
     */
    public static void error(String error, Exception e) {
        LOGGER.error(error, e);
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