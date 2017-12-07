package com.yihu.ehr.dfs.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Dfs)
@RequestMapping(ApiVersion.Version1_0)
public interface ElasticSearchClient {

    @RequestMapping(value = "/elasticSearch/mapping", method = RequestMethod.POST)
    @ApiOperation(value = "建立索引")
    Envelop mapping(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "source") String source);

    @RequestMapping(value = "/elasticSearch/index", method = RequestMethod.POST)
    @ApiOperation(value = "添加数据")
    Envelop index(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "source") String source);

    @RequestMapping(value = "/elasticSearch/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据")
    Envelop delete(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/elasticSearch/update", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据")
    Envelop update(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "id") String id,
            @RequestParam(value = "source") String source);

    @RequestMapping(value = "/elasticSearch/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条数据")
    Envelop findById(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/elasticSearch/findByField", method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    Envelop findByField(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "field") String field,
            @RequestParam(value = "value") String value);

    @RequestMapping(value = "/elasticSearch/page", method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    Envelop page(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

}
