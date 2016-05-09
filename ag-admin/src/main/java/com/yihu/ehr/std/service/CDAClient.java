package com.yihu.ehr.std.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by wq on 2016/3/4.
 */

@FeignClient(name = MicroServices.Standard)
@ApiIgnore
public interface CDAClient {

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.Documents, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    ResponseEntity<List<MCDADocument>> GetCDADocuments(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "version") String version);

    @RequestMapping(value = ApiVersion.Version1_0 + "/std/CDADocuments/ids", method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取cda列表")
    List<MCDADocument> getCDADocumentById(
            @ApiParam(name = "ids", value = "文档编号")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.DataSetRelationships, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship列表")
    List<MCdaDataSetRelationship> getCDADataSetRelationships(
            @ApiParam(name = "document_Id", value = "文档编号")
            @RequestParam(value = "document_Id") String cdaId,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.Documents, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存CDADocuments")
    MCDADocument saveCDADocuments(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "文档json数据模型")
            @RequestBody String cdaDocumentJsonData);

    @ApiOperation(value = "修改CDADocuments")
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.Document, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MCDADocument updateCDADocuments(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "model", value = "文档json数据模型")
            @RequestBody String cdaDocumentJsonData);

    @ApiOperation(value = "删除CDADocuments")
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.Documents, method = RequestMethod.DELETE)
    boolean deleteCDADocuments(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") List<String> ids,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode);

    @ApiOperation(value = "保存CDADataSetRelationship")
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.DataSetRelationships, method = RequestMethod.POST)
    boolean saveCDADataSetRelationship(
            @ApiParam(name = "data_set_ids", value = "data_set_ids")
            @RequestParam(value = "data_set_ids") String dataSetIds,
            @ApiParam(name = "document_Id", value = "cda_id")
            @RequestParam(value = "document_Id") String cdaId,
            @ApiParam(name = "version", value = "versionCode")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "xml_info", value = "xml_info")
            @RequestParam(value = "xml_info") String xmlInfo);

    @ApiOperation(value = "根基id删除CDADataSetRelationship")
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.DataSetRelationships, method = RequestMethod.DELETE)
    boolean deleteCDADataSetRelationship(
            @ApiParam(name = "version", value = "versionCode")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ApiVersion.Version1_0 + "/std/cda_data_set_relationships/cda_id", method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取getCDADataSetRelationship列表")
    List<MCdaDataSetRelationship> getCDADataSetRelationshipByCDAId(
            @ApiParam(name = "version", value = "versionCode")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "document_Id", value = "document_Id")
            @RequestParam(value = "document_Id") String cdaId);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.DocumentFileExistence, method = RequestMethod.GET)
    @ApiOperation(value = "根据cdaId和versionCode判断文件是否存在")
    boolean FileExists(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.DocumentCreateFile, method = RequestMethod.GET)
    @ResponseBody
    boolean createCDASchemaFile(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId);


    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.DocumentGetFile, method = RequestMethod.GET)
    @ResponseBody
    String getCdaXmlFileInfo(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId);

    @ApiOperation(value = "获取CDADocuments")
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.Document, method = RequestMethod.GET)
    MCDADocument getCDADocuments(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号")
            @PathVariable(value = "id") String id);
}
