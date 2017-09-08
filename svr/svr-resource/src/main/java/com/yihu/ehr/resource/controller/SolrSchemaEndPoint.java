package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.service.RsMetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.20 14:03
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "SolrSchema", description = "资源数据元与SolrSchema同步服务")
public class SolrSchemaEndPoint {

    @Autowired
    RsMetadataService metaDataService;

    @Autowired
    SolrClient solrClient;

    @RequestMapping(value = "/rs/solr_schema", method = RequestMethod.GET)
    @ApiOperation(value = "导出档案的SolrSchema字段")
    public List<Map<String, Object>> exportProfileSolrSchema(
            @ApiParam(value = "core", defaultValue = "HealthProfile")
            @RequestParam String core) throws IOException, SolrServerException {

        SchemaRequest.Fields fieldsRequest = new SchemaRequest.Fields(null);
        SchemaResponse.FieldsResponse fieldsResponse = fieldsRequest.process(solrClient, core);
        List<Map<String, Object>> fields = fieldsResponse.getFields();

        return fields;
    }

    @RequestMapping(value = "/rs/solr_schema", method = RequestMethod.POST)
    @ApiOperation(value = "更新SolrSchema字段", notes = "对比现有的资源数据元与Solr中的Schema字段列表，若不存在则添加")
    public void updateSolrSchema(
            @ApiParam(value = "core", defaultValue = "HealthProfile")
            @RequestParam String core,
            @ApiParam(value = "stored", defaultValue = "false")
            @RequestParam boolean stored,
            @ApiParam(value = "indexed", defaultValue = "true")
            @RequestParam boolean indexed) throws IOException, SolrServerException {

        Page<RsMetadata> metaDatas = metaDataService.getMetadata("+id", 0, 100000);
        for (RsMetadata metaData : metaDatas.getContent()) {
            checkFieldExistence(metaData, core, stored, indexed);
        }
    }

    private void checkFieldExistence(RsMetadata metaData, String core, boolean stored, boolean indexed) throws IOException, SolrServerException {
        SchemaRequest.Field fieldRequest = new SchemaRequest.Field(metaData.getId());
        SchemaResponse.FieldResponse fieldResponse = fieldRequest.process(solrClient, core);

        Map<String, Object> fieldAttributes = fieldResponse.getField();
        if (fieldAttributes == null) {
            fieldAttributes = new HashMap<>();
            fieldAttributes.put("name", metaData.getId());
            fieldAttributes.put("type", getMetaDataSolrType(metaData.getColumnType()));
            fieldAttributes.put("stored", stored);
            fieldAttributes.put("indexed", indexed);

            SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(fieldAttributes);
            addFieldRequest.process(solrClient, core);
        }
    }

    private String getMetaDataSolrType(String metaDataType) {
        if (metaDataType.startsWith("S")) {
            return "text_ansj";
        } else if (metaDataType.startsWith("D")) {
            return "date";
        } else if (metaDataType.startsWith("L")) {
            return "string";
        } else if (metaDataType.startsWith("N")) {
            return "double";
        }

        return "string";
    }
}
