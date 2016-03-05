package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by wq on 2016/3/4.
 */

@FeignClient(MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0+"/std")
@ApiIgnore
public interface CDAClient {

    @RequestMapping(value = "/CDADocuments" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    List<MCDADocument> GetCDADocuments(
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

    @RequestMapping(value = "/CDADocuments/ids" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取cda列表")
    List<MCDADocument> getCDADocumentById(
            @ApiParam(name = "ids", value = "版本号")
            @RequestParam(value = "ids") List<String> ids,
            @ApiParam(name = "version_code", value = "version_code")
            @RequestParam(value = "version_code") String version);

    @RequestMapping(value = "/cda_data_set_relationships" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship列表")
    List<MCdaDataSetRelationship> getCDADataSetRelationships(
            @ApiParam(name = "cda_Id", value = "cda_Id")
            @RequestParam(value = "cda_Id") String cdaId,
            @ApiParam(name = "version_code", value = "版本号")
            @RequestParam(value = "version_code") String versionCode);

    @RequestMapping(value = "/cda_documents",method = RequestMethod.POST)
    @ApiOperation(value = "保存CDADocuments")
    MCDADocument saveCDADocuments(
            @ApiParam(name = "json_date", value = "json_date")
            @RequestParam(value = "json_date") String cdaDocumentJsonData);

    @ApiOperation(value = "修改CDADocuments")
    @RequestMapping(value = "/cda_documents",method = RequestMethod.PUT)
    MCDADocument updateCDADocuments(
            @ApiParam(name = "json_date", value = "json_date")
            @RequestParam(value = "json_date") String cdaDocumentJsonData);

    @ApiOperation(value = "删除CDADocuments")
    @RequestMapping(value = "cda_documents",method = RequestMethod.DELETE)
    boolean deleteCDADocuments(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") List<String> ids,
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode);

    @ApiOperation(value = "保存CDADataSetRelationship")
    @RequestMapping(value = "/cda_data_set_relationships",method = RequestMethod.POST)
    boolean saveCDADataSetRelationship(
            @ApiParam(name = "data_set_ids", value = "data_set_ids")
            @RequestParam(value = "data_set_ids") String[] dataSetIds,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId,
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "xml_info", value = "xml_info")
            @RequestParam(value = "xml_info") String xmlInfo);

    @ApiOperation(value = "根基id删除CDADataSetRelationship")
    @RequestMapping(value = "/cda_data_set_relationships",method = RequestMethod.DELETE)
    boolean deleteCDADataSetRelationship(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String[] ids);

    @RequestMapping(value = "/cda_data_set_relationships/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取getCDADataSetRelationship列表")
    List<MCdaDataSetRelationship> getCDADataSetRelationshipByCDAId(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId);

    @RequestMapping(value = "/file/existence/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cdaId和versionCode判断文件是否存在")
    boolean FileExists(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId);

    @RequestMapping(value = "/createCDASchemaFile",method = RequestMethod.GET)
    @ResponseBody
    boolean createCDASchemaFile(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId);

    @RequestMapping(value = "/getCdaXmlFileInfo",method = RequestMethod.GET)
    @ResponseBody
    Object getCdaXmlFileInfo(@RequestParam(value = "cda_id")String cdaId,@RequestParam(value = "versionCode") String versionCode);

}
