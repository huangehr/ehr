package com.yihu.ehr.ha.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdadocument.CDAModel;
import com.yihu.ehr.agModel.standard.cdadocument.CdaDataSetRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.CDAClient;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/cda")
@RestController
public class CDAController extends BaseController{

    @Autowired
    private CDAClient cdaClient;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/cdas", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    public Envelop GetCdaListByKey(
            @ApiParam(name = "code", value = "CDA代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "CDA名称")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cdaType", value = "cda类别")
            @RequestParam(value = "cdaType") String cdaType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
            @RequestParam(value = "rows") int rows) {

        List<MCDADocument> mcdaDocumentList = cdaClient.GetCDADocuments(versionCode,code,name,cdaType,page,rows);
        List<CDAModel> cdaModels = (List<CDAModel>)convertToModels(mcdaDocumentList,new ArrayList<CDAModel>(mcdaDocumentList.size()),CDAModel.class,null);
        //TODO:获取总条数
//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;
        Envelop envelop = getResult(cdaModels,0,page,rows);

        return envelop;
    }

    @RequestMapping(value = "/cda", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取cda")
    public Envelop getCDAInfoById(
            @ApiParam(name = "cdaId", value = "cdaID")
            @PathVariable(value = "cdaId") String cdaId,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @PathVariable(value = "versionCode") String versionCode) {

        Envelop envelop = new Envelop();
        CDAModel cdaModel = null;

        List<MCDADocument> mcdaDocumentList = cdaClient.getCDADocumentById(cdaId, versionCode);
        for (MCDADocument mcdaDocument:mcdaDocumentList){
            cdaModel = convertToModel(mcdaDocument,CDAModel.class);
        }

        if (cdaModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(cdaModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("查询失败");
        }

        return envelop;
    }



    @RequestMapping(value = "cda",method = RequestMethod.POST)
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


    @RequestMapping(value = "uploadCDA",method = RequestMethod.POST)
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
            @PathVariable(value = "cdaId") String cdaId,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @PathVariable(value = "versionCode") String versionCode) {

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
