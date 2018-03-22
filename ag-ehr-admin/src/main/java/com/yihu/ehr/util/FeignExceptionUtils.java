package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.lang.SpringContext;
import feign.FeignException;

import java.io.IOException;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/5/26
 */
public class FeignExceptionUtils {

    public static String getErrorMsg(Exception e){
        try {
            FeignException feignException = (FeignException) e.getCause();
            String[] msg = feignException.getMessage().split("; content:\n");
            return String.valueOf(toEntity(msg[1], Map.class).get("message"));
        }catch (Exception e1){
            e1.printStackTrace();
            return "Error, see the log for stack trace.";
        }
    }

    public static <T> T toEntity(String json, Class<T> entityCls) {
        try {
            ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
            T entity = objectMapper.readValue(json, entityCls);
            return entity;
        } catch (IOException ex) {
            throw new ApiException("Convert JSON to entity failed, " + ex.getMessage());
        }
    }
}
