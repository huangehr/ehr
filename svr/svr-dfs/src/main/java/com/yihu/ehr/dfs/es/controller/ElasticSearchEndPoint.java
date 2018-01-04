package com.yihu.ehr.dfs.es.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.es.service.ElasticSearchService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EndPoint - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ElasticSearchEndPoint", description = "Es搜索服务", tags = {"分布式综合服务-Es搜索服务"})
public class ElasticSearchEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping(value = ServiceApi.ElasticSearch.Mapping, method = RequestMethod.POST)
    @ApiOperation(value = "建立映射")
    public Envelop mapping(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "Json串值", required = true)
            @RequestParam(value = "source") String source) {
        Envelop envelop = new Envelop();
        try {
            String mapper = source.replaceAll("\\[","{").replaceAll("\\]","}");
            Map<String, Map<String, String>> Mapping = objectMapper.readValue(mapper, Map.class);
            elasticSearchService.mapping(index, type, Mapping);
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.Index, method = RequestMethod.POST)
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
        envelop.setSuccessFlg(true);
        envelop.setObj(result);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.Delete, method = RequestMethod.DELETE)
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

    @RequestMapping(value = ServiceApi.ElasticSearch.DeleteByField, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据（单次不能超过10000条）")
    public Envelop deleteByField(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") String value) {
        Envelop envelop = new Envelop();
        elasticSearchService.deleteByField(index, type, field, value);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.Update, method = RequestMethod.PUT)
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

    @RequestMapping(value = ServiceApi.ElasticSearch.FindById, method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.ElasticSearch.FindByField, method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop findByField(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") Object value) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList = elasticSearchService.findByField(index, type, field, value);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(resultList);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.Page, method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop page(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter" , required = false) String filter,
            @ApiParam(name = "page", value = "页码", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> filterMap;
        if(!StringUtils.isEmpty(filter)) {
            try {
                filterMap = objectMapper.readValue(filter, List.class);
            } catch (IOException e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
        }else {
            filterMap = new ArrayList<Map<String, Object>>(0);
        }
        List<Map<String, Object>> resultList = elasticSearchService.page(index, type, filterMap, page, size);
        int count = (int)elasticSearchService.count(index, type, filterMap);
        envelop = getPageResult(resultList, count, page, size);
        return envelop;
    }

}
