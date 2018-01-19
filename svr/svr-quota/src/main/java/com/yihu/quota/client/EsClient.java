package com.yihu.quota.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Elasticsearch操作 Client
 *
 * @author 张进军
 * @date 2017/12/27 09:14
 */
@FeignClient(name = MicroServices.Dfs)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface EsClient {

    @ApiOperation("添加ES索引")
    @RequestMapping(value = ServiceApi.ElasticSearch.Index, method = RequestMethod.POST)
    public Envelop index(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source", value = "值（JSON字符串）", required = true)
            @RequestBody String source);

    @ApiOperation("删除ES索引")
    @RequestMapping(value = ServiceApi.ElasticSearch.Delete, method = RequestMethod.POST)
    public Envelop delete(
            @ApiParam(name = "index", value = "索引名称", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "id", value = "id(多个id值以,分隔)", required = true)
            @RequestParam(value = "id") String id);

}
