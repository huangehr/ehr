package com.yihu.ehr.ha.adapter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.controller.BaseRestController;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/1
 */
public class ExtendController extends BaseRestController {

    private ObjectMapper objectMapper = new ObjectMapper();

    protected String objToJson(Object obj) throws JsonProcessingException {

        return objectMapper.writeValueAsString(obj);
    }

}
