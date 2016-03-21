package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by wq on 2016/3/4.
 */

@FeignClient(MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface CDAClient {

    @RequestMapping(value = RestApi.Standards.Documents ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    ResponseEntity<List<MCDADocument>> GetCDADocuments(
            @ApiParam(name = "version_code", value = "版本号")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "code", value = "code")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "type")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows);

    @RequestMapping(value = "/std/CDADocuments/ids" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取cda列表")
    List<MCDADocument> getCDADocumentById(
            @ApiParam(name = "ids", value = "文档编号")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = RestApi.Standards.DataSetRelationships ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship列表")
    List<MCdaDataSetRelationship> getCDADataSetRelationships(
            @ApiParam(name = "document_Id", value = "文档编号")
            @RequestParam(value = "document_Id") String cdaId,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode);

    @RequestMapping(value = RestApi.Standards.Documents,method = RequestMethod.POST)
    @ApiOperation(value = "保存CDADocuments")
    MCDADocument saveCDADocuments(
            @ApiParam(name = "model", value = "文档json数据模型")
            @RequestParam(value = "model") String cdaDocumentJsonData);

    @ApiOperation(value = "修改CDADocuments")
    @RequestMapping(value = RestApi.Standards.Document,method = RequestMethod.PUT)
    MCDADocument updateCDADocuments(
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "model", value = "文档json数据模型")
            @RequestParam(value = "model") String cdaDocumentJsonData);

    @ApiOperation(value = "删除CDADocuments")
    @RequestMapping(value = RestApi.Standards.Documents,method = RequestMethod.DELETE)
    boolean deleteCDADocuments(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") List<String> ids,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode);

    @ApiOperation(value = "保存CDADataSetRelationship")
    @RequestMapping(value = RestApi.Standards.DataSetRelationships,method = RequestMethod.POST)
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
    @RequestMapping(value = RestApi.Standards.DataSetRelationships,method = RequestMethod.DELETE)
    boolean deleteCDADataSetRelationship(
            @ApiParam(name = "version", value = "versionCode")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = "/std/cda_data_set_relationships/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取getCDADataSetRelationship列表")
    List<MCdaDataSetRelationship> getCDADataSetRelationshipByCDAId(
            @ApiParam(name = "version", value = "versionCode")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "document_Id", value = "document_Id")
            @RequestParam(value = "document_Id") String cdaId);

    @RequestMapping(value = RestApi.Standards.DocumentFileExistence ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cdaId和versionCode判断文件是否存在")
    boolean FileExists(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId);

    @RequestMapping(value = RestApi.Standards.DocumentCreateFile,method = RequestMethod.GET)
    @ResponseBody
    boolean createCDASchemaFile(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId);


    @RequestMapping(value = RestApi.Standards.DocumentGetFile, method = RequestMethod.GET)
    @ResponseBody
    Object getCdaXmlFileInfo(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId);

}
