package com.yihu.ehr.ha.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeDetailModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.CDATypeClient;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yww on 2016/3/1.
 */
@RequestMapping(ApiVersion.Version1_0 + "/cdatype")
@RestController
public class CDATypeController extends BaseController {

    @Autowired
    CDATypeClient cdaTypeClient;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * 根据父级ID获取下级
     */
    @RequestMapping(value = "/children_cda_types/{parent_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据父级ID获取下级")
    public Envelop getChildrenByPatientId(
            @ApiParam(name = "parent_id", value = "父级id")
            @PathVariable(value = "parent_id") String parentId) throws Exception {
        Envelop envelop = new Envelop();
        List<MCDAType> mCdaTypeList = cdaTypeClient.getChildrenByPatientId(parentId);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(changeToCdaTypeModels(mCdaTypeList));
        return envelop;
    }

    /**
     * 将微服务返回结果转化为前端CdaTypeModel对象集合
     *
     * @param mCdaTypeList
     * @return
     */
    private List<CdaTypeModel> changeToCdaTypeModels(List<MCDAType> mCdaTypeList) {
        List<CdaTypeModel> cdaTypeModelList = (List<CdaTypeModel>) convertToModels(mCdaTypeList, new ArrayList<CdaTypeModel>(mCdaTypeList.size()), CdaTypeModel.class, null);
        return cdaTypeModelList;
    }

    /**
     * 根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）
     *
     * @param patientIds 父级ID
     * @param key        查询条件
     * @return
     */
    @RequestMapping(value = "/cda_types/patient_ids/key", method = RequestMethod.GET)
    @ApiOperation(value = "根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）")
    public Envelop getChildIncludeSelfByParentIdsAndKey(
            @ApiParam(name = "patient_ids", value = "父级id")
            @RequestParam(value = "patient_ids") String patientIds,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key) throws Exception {
        Envelop envelop = new Envelop();
        List<MCDAType> mCdaTypeList = cdaTypeClient.getChildIncludeSelfByParentIdsAndKey(patientIds, key);
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(changeToCdaTypeModels(mCdaTypeList));
        return envelop;
    }

    @RequestMapping(value = "/cda_types/code_name", method = RequestMethod.GET)
    @ApiOperation(value = "根据code或者name获取CDAType列表")
    public Envelop getCdaTypeByCodeOrName(
            @ApiParam(name = "code", value = "代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        List<MCDAType> mCdaTypeList = cdaTypeClient.getCdaTypeByCodeOrName(code, name);
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(changeToCdaTypeModels(mCdaTypeList));
        return envelop;
    }


    @RequestMapping(value = "/cda_types/id/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取CDAType")
    public Envelop getCdaTypeById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        MCDAType mCdaType = cdaTypeClient.getCdaTypeById(id);
        if (mCdaType == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取CDAType失败！");
            return envelop;
        }
        CdaTypeDetailModel cdaTypeDetailModel = convertToModel(mCdaType, CdaTypeDetailModel.class);
        envelop.setSuccessFlg(true);
        envelop.setObj(cdaTypeDetailModel);
        return envelop;
    }

    @RequestMapping(value = "/cda_types/ids/{ids}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取CDAType列表")
    public Envelop getCdaTypeByIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String ids) {
        Envelop envelop = new Envelop();
        List<MCDAType> mCdaTypeList = cdaTypeClient.getCdaTypeByIds(ids);
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(changeToCdaTypeModels(mCdaTypeList));
        return envelop;
    }


    @RequestMapping(value = "/cda_types", method = RequestMethod.POST)
    @ApiOperation(value = "新增CDAType")
    public Envelop saveCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        CdaTypeDetailModel cdaTypeDetailModel = objectMapper.readValue(jsonData, CdaTypeDetailModel.class);
        MCDAType mCdaTypeOld = convertToModel(cdaTypeDetailModel, MCDAType.class);
        if (cdaTypeClient.isCDATypeExists(mCdaTypeOld.getCode())){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该cda类别代码已经存在！");
            return envelop;
        }
        mCdaTypeOld.setCreateDate(new Date());
        String jsonDataNew = objectMapper.writeValueAsString(mCdaTypeOld);

        MCDAType mCdaTypeNew = cdaTypeClient.saveCDAType(jsonDataNew);
        if (mCdaTypeNew == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建cda类别失败！");
        }
        envelop.setSuccessFlg(true);
        CdaTypeDetailModel detailModel = convertToModel(mCdaTypeNew, CdaTypeDetailModel.class);
        envelop.setObj(detailModel);
        return envelop;
    }

    @RequestMapping(value = "/cda_types", method = RequestMethod.PUT)
    @ApiOperation(value = "修改CDAType")
    public Envelop updateCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {

        Envelop envelop = new Envelop();
        CdaTypeDetailModel cdaTypeDetailModel = objectMapper.readValue(jsonData, CdaTypeDetailModel.class);
        MCDAType mCdaTypeOld = convertToModel(cdaTypeDetailModel, MCDAType.class);
        if (cdaTypeClient.isCDATypeExists(mCdaTypeOld.getCode())){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该cda类别代码已经存在！");
            return envelop;
        }
        mCdaTypeOld.setUpdateDate(new Date());
        String jsonDataNew = objectMapper.writeValueAsString(mCdaTypeOld);

        MCDAType mCdaTypeNew = cdaTypeClient.updateCDAType(jsonDataNew);
        if (mCdaTypeNew == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新cda类别失败！");
        }
        envelop.setSuccessFlg(true);
        CdaTypeDetailModel detailModel = convertToModel(mCdaTypeNew, CdaTypeDetailModel.class);
        envelop.setObj(detailModel);
        return envelop;
    }

    @RequestMapping(value = "/cda_types/existence/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    public boolean isCDATypeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code) {
        return cdaTypeClient.isCDATypeExists(code);
    }


    /**
     * 删除CDA类别，若该类别存在子类别，将一并删除子类别
     * 先根据当前的类别ID获取全部子类别ID，再进行删除
     */
    @RequestMapping(value = "/cda_types/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除CDA类别，若该类别存在子类别，将一并删除子类别,多个id用逗号隔开")
    public boolean deleteCDATypeByPatientIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String ids) {
        return cdaTypeClient.deleteCDATypeByPatientIds(ids);
    }
}
