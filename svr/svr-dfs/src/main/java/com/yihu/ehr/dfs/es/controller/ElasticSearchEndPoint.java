package com.yihu.ehr.dfs.es.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.es.service.ElasticSearchService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EndPoint - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ElasticSearchEndPoint", description = "Es搜索服务", tags = {"ElasticSearch-Service"})
public class ElasticSearchEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping(value = "/elasticSearch/create", method = RequestMethod.POST)
    @ApiOperation(value = "建立索引")
    public Envelop create(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "Json串值", required = true)
            @RequestParam(value = "source") String source) {
        Envelop envelop = new Envelop();
        try {
            List<Map<String, String>> sourceList = objectMapper.readValue(source, List.class);
            elasticSearchService.create(index, type, sourceList);
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/elasticSearch/index", method = RequestMethod.POST)
    @ApiOperation(value = "添加数据")
    public Envelop index(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "值", required = true)
            @RequestParam(value = "source") String source) {
        Envelop envelop = new Envelop();
        Map<String, Object> result;
        try {
            Map<String, Object> sourceMap = objectMapper.readValue(source, Map.class);
            result = elasticSearchService.index(index, type, sourceMap);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        if (result != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(result);
            return envelop;
        }
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg("插入数据前请先建立索引");
        return envelop;
    }

    @RequestMapping(value = "/elasticSearch/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据")
    public Envelop delete(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "id", value = "id(多个id值以,分隔)", required = true)
            @RequestParam(value = "id") String id) {
        Envelop envelop = new Envelop();
        elasticSearchService.delete(index, type, id.split(","));
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/elasticSearch/update", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据")
    public Envelop update(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "id", value = "id", required = true)
            @RequestParam(value = "id") String id,
            @ApiParam(name = "source", value = "值", required = true)
            @RequestParam(value = "source") String source) {
        Envelop envelop = new Envelop();
        Map<String, Object> sourceMap;
        try {
            sourceMap = objectMapper.readValue(source, Map.class);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        Map<String, Object> result;
        try {
            result = elasticSearchService.update(index, type, id, sourceMap);
        }catch (DocumentMissingException e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        if (result != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(result);
            return envelop;
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @RequestMapping(value = "/elasticSearch/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条数据")
    public Envelop findById(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "id", value = "id", required = true)
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        Map<String, Object> result = elasticSearchService.findById(index, type, id);
        if(result != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(result);
            return envelop;
        }
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg("无相关数据");
        return envelop;
    }

    @RequestMapping(value = "/elasticSearch/findByField", method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop findByField(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") String value) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList = elasticSearchService.findByField(index, type, field, value);
        envelop.setDetailModelList(resultList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/elasticSearch/page", method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop page(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "filter", value = "过滤条件", required = true)
            @RequestParam(value = "filter") String filter,
            @ApiParam(name = "page", value = "页码", required = true)
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true)
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        List<Map<String, String>> filterMap;
        try {
            filterMap = objectMapper.readValue(filter, List.class);
        }catch (IOException e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        List<Map<String, Object>> resultList = elasticSearchService.page(index, type, filterMap, page, size);
        envelop.setDetailModelList(resultList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
