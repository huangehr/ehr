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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(value = "source") String source) throws Exception {
        Map<String, Map<String, String>> Mapping = objectMapper.readValue(source, Map.class);
        elasticSearchService.mapping(index, type, Mapping);
        return success(true);
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.Index, method = RequestMethod.POST)
    @ApiOperation(value = "添加数据")
    public Envelop index(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "值", required = true)
            @RequestParam(value = "source") String source) throws Exception {
        Map<String, Object> result;
        Map<String, Object> sourceMap = objectMapper.readValue(source, Map.class);
        result = elasticSearchService.index(index, type, sourceMap);
        return success(result);
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
        elasticSearchService.delete(index, type, id.split(","));
        return success(true);
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
        elasticSearchService.deleteByField(index, type, field, value);
        return success(true);
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
            @RequestParam(value = "source") String source) throws Exception {
        Map<String, Object> sourceMap = objectMapper.readValue(source, Map.class);
        Map<String, Object> result = elasticSearchService.update(index, type, id, sourceMap);
        if (result != null) {
            return success(result);
        }
        return failed("更新失败");
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
        Map<String, Object> result = elasticSearchService.findById(index, type, id);
        return success(result);
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
        List<Map<String, Object>> resultList = elasticSearchService.findByField(index, type, field, value);
        return success(resultList);
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
            @RequestParam(value = "size") int size) throws Exception {
        List<Map<String, Object>> filterMap;
        if (!StringUtils.isEmpty(filter)) {
            filterMap = objectMapper.readValue(filter, List.class);
        } else {
            filterMap = new ArrayList<>(0);
        }
        List<Map<String, Object>> resultList = elasticSearchService.page(index, type, filterMap, page, size);
        int count = (int)elasticSearchService.count(index, type, filterMap);
        Envelop envelop = getPageResult(resultList, count, page, size);
        return envelop;
    }

}
