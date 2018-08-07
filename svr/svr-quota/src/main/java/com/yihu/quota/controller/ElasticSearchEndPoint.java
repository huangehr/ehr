package com.yihu.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * EndPoint - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ElasticSearchEndPoint", description = "Es搜索服务", tags = {"分布式综合服务-Es搜索服务"})
public class ElasticSearchEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    @RequestMapping(value = ServiceApi.ElasticSearch.Mapping, method = RequestMethod.POST)
    @ApiOperation(value = "建立映射")
    public Envelop mapping(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "Json串值{\"field\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}", required = true)
            @RequestParam(value = "source") String source,
            @ApiParam(name = "setting", value = "Json串值{\"key\":\"value\"}")
            @RequestParam(value = "setting", required = false) String setting) throws Exception {
        Map<String, Map<String, String>> _mapping = objectMapper.readValue(source, Map.class);
        Map<String, Object> _setting = new HashMap<>();
        if (setting != null) {
            _setting = objectMapper.readValue(setting, Map.class);
        }
        elasticSearchUtil.mapping(index, type, _mapping, _setting);
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
        result = elasticSearchUtil.index(index, type, sourceMap);
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
        elasticSearchUtil.bulkDelete(index, type, id.split(","));
        return success(true);
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.DeleteByField, method = RequestMethod.GET)
    @ApiOperation(value = "删除数据（单次不能超过10000条）")
    public Envelop deleteByField(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") String value,
            @ApiParam(name = "field2", value = "字段2", required = false)
            @RequestParam(value = "field2", required = false) String field2,
            @ApiParam(name = "value2", value = "字段值", required = false)
            @RequestParam(value = "value2", required = false) String value2,
            @ApiParam(name = "dateField", value = "时间字段", required = false)
            @RequestParam(value = "dateField" , required = false) String dateField,
            @ApiParam(name = "startDate", value = "时间字段开始时间,时间格式如：2018-01-12", required = false)
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "时间字段结束时间,时间格式如：2018-01-12", required = false)
            @RequestParam(value = "endDate", required = false) String endDate) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(field, value);
        boolQueryBuilder.must(termsQueryBuilder);
        if(StringUtils.isNotEmpty(field2) && StringUtils.isNotEmpty(value2)){
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery(field2, value2);
            boolQueryBuilder.must(termsQueryBuilder2);
        }
        if(StringUtils.isNotEmpty(dateField) && startDate != null && endDate != null){
            RangeQueryBuilder rangeQueryStartTime = QueryBuilders.rangeQuery(dateField).gte(startDate);
            boolQueryBuilder.must(rangeQueryStartTime);
            RangeQueryBuilder rangeQueryEndTime = QueryBuilders.rangeQuery(dateField).lte(endDate);
            boolQueryBuilder.must(rangeQueryEndTime);
        }
        elasticSearchUtil.deleteByFilter(index, type, boolQueryBuilder);
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
        Map<String, Object> result = elasticSearchUtil.update(index, type, id, sourceMap);
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
        Map<String, Object> result = elasticSearchUtil.findById(index, type, id);
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
        List<Map<String, Object>> resultList = elasticSearchUtil.findByField(index, type, field, value);
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
        Page<Map<String, Object>> result = elasticSearchUtil.page(index, type, filter, page, size);
        Envelop envelop = getPageResult(result.getContent(), (int)result.getTotalElements(), page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.ElasticSearch.FindBySql, method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public List<Map<String, Object>> findBySql (
            @ApiParam(name = "fields", value = "字段", required = true)
            @RequestParam(value = "fields") String fields,
            @ApiParam(name = "sql", value = "SQL", required = true)
            @RequestParam(value = "sql") String sql) throws Exception {
        return elasticSearchUtil.findBySql(Arrays.asList(fields.split(",")), sql);
    }

}
