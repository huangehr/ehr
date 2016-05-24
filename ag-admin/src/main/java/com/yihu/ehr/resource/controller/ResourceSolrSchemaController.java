package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.client.ResourceSolrSchemaClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin" + "/rs/solr_schema")
@Api(value = "资源Solr模式更新接口", description = "更新资源数据元在Solr中的索引模式信息")
public class ResourceSolrSchemaController extends BaseController{
    @Autowired
    ResourceSolrSchemaClient resourceSolrSchemaClient;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "导出档案的SolrSchema字段")
    public List<Map<String, Object>> exportProfileSolrSchema(
            @ApiParam(value = "core", defaultValue = "HealthProfile")
            @RequestParam(value = "core") String core) throws IOException, SolrServerException {
        return resourceSolrSchemaClient.exportProfileSolrSchema(core);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "更新SolrSchema字段", notes = "对比现有的资源数据元与Solr中的Schema字段列表，若不存在则添加")
    public void updateSolrSchema(
            @ApiParam(value = "core", defaultValue = "HealthProfile")
            @RequestParam(value = "core") String core,
            @ApiParam(value = "stored", defaultValue = "false")
            @RequestParam(value = "stored") boolean stored,
            @ApiParam(value = "indexed", defaultValue = "true")
            @RequestParam(value = "indexed") boolean indexed) throws IOException, SolrServerException {
        resourceSolrSchemaClient.updateSolrSchema(core,stored,indexed);
    }

}
