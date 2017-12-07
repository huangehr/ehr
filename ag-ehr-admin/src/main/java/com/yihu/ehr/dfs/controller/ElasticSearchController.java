package com.yihu.ehr.dfs.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.client.ElasticSearchClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * EndPoint - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "ElasticSearchController", description = "Es搜索服务", tags = {"分布式综合服务-Es搜索服务"})
public class ElasticSearchController extends EnvelopRestEndPoint {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @RequestMapping(value = "/elasticSearch/mapping", method = RequestMethod.POST)
    @ApiOperation(value = "建立索引")
    public Envelop mapper(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "Json串值", required = true)
            @RequestParam(value = "source") String source) {
        return elasticSearchClient.mapping(index, type, source);
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
        return elasticSearchClient.index(index, type, source);
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
        return elasticSearchClient.delete(index, type, id);
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
        return elasticSearchClient.update(index, type, id, source);
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
        return elasticSearchClient.index(index, type, id);
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
        return elasticSearchClient.findByField(index, type, field, value);
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
        return elasticSearchClient.page(index, type, filter, page, size);
    }

}
