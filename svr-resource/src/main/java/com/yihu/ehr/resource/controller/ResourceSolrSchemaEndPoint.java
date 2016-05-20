package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.service.MetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.20 14:03
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/solr_schema")
@Api(value = "资源Solr模式更新接口", description = "更新资源数据元在Solr中的索引模式信息")
public class ResourceSolrSchemaEndPoint {
    private String HealthProfileCore = "HealthProfile";
    private String HealthProfileSubCore = "HealthProfileSub";

    @Autowired
    MetadataService metaDataService;

    @Autowired
    SolrClient solrClient;

    @RequestMapping(value = "{adaption_id}", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件获取标准字典列表", notes = "根据查询条件获取标准字典列表")
    public void updateSolrSchema(@ApiParam("adaption_id")
                                 @PathVariable("adaption_id") int resourceAdaption) throws IOException, SolrServerException {

        Page<RsMetadata> metaDatas = metaDataService.getMetadata("+id", 0, 100000);

        SchemaRequest.Fields fieldsRequest = new SchemaRequest.Fields(null);
        SchemaResponse.FieldsResponse fieldsResponse = fieldsRequest.process(solrClient);
        List<Map<String, Object>> fields = fieldsResponse.getFields();

        for (Map<String, Object> field : fields){

        }
    }
}
