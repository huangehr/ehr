package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.org.OrgModel;
import com.yihu.ehr.agModel.standard.cdadocument.CDAModel;
import com.yihu.ehr.agModel.standard.cdadocument.CdaDataSetRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.std.service.CDAClient;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/cda")
@RestController
public class CDAController extends BaseController{

    @Autowired
    private CDAClient cdaClient;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/cdas", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    public Envelop GetCdaListByKey(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {
        List<MCDADocument> cdaDocumentList = new ArrayList<>();
        ResponseEntity<List<MCDADocument>> responseEntity = cdaClient.GetCDADocuments(fields, filters, sorts, size, page,version);
        List<MCDADocument> cdaDocuments = responseEntity.getBody();
        for (MCDADocument cdaDocument : cdaDocuments) {
            cdaDocumentList.add(cdaDocument);
        }
        int totalCount = getTotalCount(responseEntity);
        return getResult(cdaDocumentList, totalCount, page, size);
    }

    @RequestMapping(value = "/cda", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取cda")
    public Envelop getCDAInfoById(
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId,
            @ApiParam(name = "version_code", value = "标准版本代码")
            @RequestParam(value = "version_code") String versionCode) {

        try {

            List<MCDADocument> mcdaDocumentList = cdaClient.getCDADocumentById(cdaId, versionCode);
            List<CDAModel> cdaModelList = (List<CDAModel>)convertToModels(mcdaDocumentList,new ArrayList<CDAModel>(mcdaDocumentList.size()),CDAModel.class,null);

            if (cdaModelList == null) {
                return failed("数据获取失败!");
            }

            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(cdaModelList);
            return envelop;
        }
        catch (Exception ex)
        {
            return failedSystem();
        }
    }



    @RequestMapping(value = "/cda",method = RequestMethod.POST)
    @ApiOperation(value = "保存CDADocuments")
    public Envelop SaveCdaInfo(
            @ApiParam(name = "cdaInfoJson", value = "CDAJson")
            @RequestParam(value = "cdaInfoJson") String cdaInfoJson) {

        Envelop envelop = new Envelop();

        MCDADocument mcdaDocument = cdaClient.saveCDADocuments(cdaInfoJson);
        CDAModel cdaModel = convertToModel(mcdaDocument,CDAModel.class);

        if (cdaModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(cdaModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("新增CDA文档失败");
        }

        return envelop;
    }


    @RequestMapping(value = "/cda",method = RequestMethod.PUT)
    @ApiOperation(value = "修改CDADocuments")
    public Envelop updateCDADocuments(
            @ApiParam(name = "cdaInfoJson", value = "CDAJson")
            @RequestParam(value = "cdaInfoJson") String cdaInfoJson) {

        Envelop envelop = new Envelop();
        MCDADocument model = toEntity(cdaInfoJson, MCDADocument.class);
        MCDADocument mcdaDocument = cdaClient.updateCDADocuments(model.getId(), cdaInfoJson);
        CDAModel cdaModel = convertToModel(mcdaDocument,CDAModel.class);

        if (cdaModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(cdaModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改CDA文档失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/cda",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除CDADocuments")
    public Envelop deleteCdaInfo(
            @ApiParam(name = "cdaId", value = "cdaID")
            @RequestParam(value = "cdaId") String cdaId,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @RequestParam(value = "versionCode") String versionCode) {

        Envelop envelop = new Envelop();

        boolean bo = cdaClient.deleteCDADocuments(Arrays.asList(cdaId),versionCode);

        envelop.setSuccessFlg(bo);

        return envelop;
    }


    @RequestMapping(value = "/relationships",method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship")
    public Envelop getRelationByCdaId(
            @ApiParam(name = "cdaId", value = "cdaID")
            @PathVariable(value = "cdaId") String cdaId,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @PathVariable(value = "versionCode") String versionCode) {

        Envelop envelop = new Envelop();

        List<MCdaDataSetRelationship> mcdaDocumentList = cdaClient.getCDADataSetRelationshipByCDAId(versionCode, cdaId);
        List<CdaDataSetRelationshipModel> cdaDataSetRelationshipModels = (List<CdaDataSetRelationshipModel>)convertToModels(mcdaDocumentList,new ArrayList<CdaDataSetRelationshipModel>(mcdaDocumentList.size()),CdaDataSetRelationshipModel.class,null);

        if (cdaDataSetRelationshipModels != null){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(cdaDataSetRelationshipModels);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("查询失败");
        }

        return envelop;
    }

    @RequestMapping("SaveRelationship")
    @ApiOperation(value = "保存CDADataSetRelationship")
    public Envelop SaveRelationship(
            @ApiParam(name = "dataSetIds", value = "数据集ID(多ID以逗号隔开)")
            @RequestParam(value = "dataSetIds") String dataSetIds,
            @ApiParam(name = "cdaId", value = "cdaID")
            @PathVariable(value = "cdaId") String cdaId,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @PathVariable(value = "versionCode") String versionCode,
            @ApiParam(name = "xmlInfo", value = "XML文件信息")
            @RequestParam(value = "xmlInfo") String xmlInfo) {

        Envelop envelop = new Envelop();

        boolean bo = cdaClient.saveCDADataSetRelationship(dataSetIds,cdaId,versionCode,xmlInfo);

        envelop.setSuccessFlg(bo);

        return envelop;
    }

//    @RequestMapping(value = "/getDatasetByCdaId",method = RequestMethod.GET)
//    public Object getDatasetByCdaId(
//                                    @ApiParam(name = "cdaId", value = "cdaID")
//                                    @PathVariable(value = "cdaId") String cdaId,
//                                    @ApiParam(name = "versionCode", value = "标准版本代码")
//                                    @PathVariable(value = "versionCode") String versionCode) {
//        return null;
//    }


    @ApiOperation(value = "根基id删除CDADataSetRelationship")
    @RequestMapping(value = "/cda_data_set_relationships",method = RequestMethod.DELETE)
    public Envelop deleteCDADataSetRelationship(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String ids){

        Envelop envelop = new Envelop();

        boolean bo = cdaClient.deleteCDADataSetRelationship(versionCode,ids);

        envelop.setSuccessFlg(bo);

        return envelop;
    }



    @RequestMapping(value = "/file/existence/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cdaId和versionCode判断文件是否存在")
    public Envelop FileExists(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId){

        Envelop envelop = new Envelop();

        boolean bo =  cdaClient.FileExists(versionCode, cdaId);

        envelop.setSuccessFlg(bo);

        return envelop;
    }




    @RequestMapping(value = "documentExist",method = RequestMethod.GET)
    public Object documentExist(
            @ApiParam(name = "code", value = "CDA代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @PathVariable(value = "versionCode") String versionCode){

        return null;
    }

//    @RequestMapping(value = "/getCdaXmlFileInfo",method = RequestMethod.GET)
//    public Envelop getCdaXmlFileInfo(
//                                    @ApiParam(name = "cdaId", value = "cdaID")
//                                    @PathVariable(value = "cdaId") String cdaId,
//                                    @ApiParam(name = "versionCode", value = "标准版本代码")
//                                    @PathVariable(value = "versionCode") String versionCode) {
//
//        Envelop envelop = new Envelop();
//
//        CDAModel cdaModel = (CDAModel) cdaClient.getCdaXmlFileInfo(cdaId, versionCode);
//
//        if (cdaModel != null){
//            envelop.setObj(cdaModel);
//            envelop.setSuccessFlg(true);
//        }else {
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("获取cda文档XML文件信息失败");
//        }
//      return envelop;
//    }

    @RequestMapping(value = "createCDASchemaFile",method = RequestMethod.POST)
    @ApiOperation(value = "生成CDA文件")
    public Envelop createCDASchemaFile(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId){

        Envelop envelop = new Envelop();

        boolean bo = cdaClient.createCDASchemaFile(versionCode,cdaId);

        envelop.setSuccessFlg(bo);

        return envelop;
    }



}
