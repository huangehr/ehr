package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 打印档案入库的日志
 */
public class PackResolveLog {

    private static Logger logger = LoggerFactory.getLogger(PackResolveLog.class);

    /**
     * 业务日志输出
     *
     * @param caller 调用者
     * @param info 日志信息
     */
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
}