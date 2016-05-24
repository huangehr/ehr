package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceSolrSchemaClient {

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "导出档案的SolrSchema字段")
    List<Map<String, Object>> exportProfileSolrSchema(
            @RequestParam(value = "core") String core);

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "更新SolrSchema字段", notes = "对比现有的资源数据元与Solr中的Schema字段列表，若不存在则添加")
    void updateSolrSchema(
            @RequestParam(value = "core") String core,
            @RequestParam(value = "stored") boolean stored,
            @RequestParam(value = "indexed") boolean indexed);



}
