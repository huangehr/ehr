package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeDetailModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeTreeModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.std.service.CDATypeClient;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yww on 2016/3/1.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/cda_type")
@RestController
public class CDATypeController extends BaseController {

    @Autowired
    CDATypeClient cdaTypeClient;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * 根据父级ID获取下级
     */
    @RequestMapping(value = "/children_cda_types", method = RequestMethod.GET)
    @ApiOperation(value = "根据父级ID获取下级")
    public Envelop getChildrenByPatientId(
            @ApiParam(name = "parent_id", value = "父级id")
            @RequestParam(value = "parent_id") String parentId) throws Exception {
        Envelop envelop = new Envelop();
        List<MCDAType> mCdaTypeList = cdaTypeClient.getChildrenByPatientId(parentId);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
        return envelop;
    }

    /**
     * 将微服务返回结果转化为前端CdaTypeModel对象集合
     *
     * @param mCdaTypeList
     * @return
     */
    private List<CdaTypeModel> convertToCdaTypeModels(List<MCDAType> mCdaTypeList) {
        List<CdaTypeModel> cdaTypeModelList = (List<CdaTypeModel>) convertToModels(mCdaTypeList, new ArrayList<CdaTypeModel>(mCdaTypeList.size()), CdaTypeModel.class, null);
        return cdaTypeModelList;
    }

    /**
     * 获取可以作为父类别的cda类别列表
     * （不含该类别及子类别、子类别的子类类别。。所剩下的类别）
     * @param id
     * @param key
     * @return
     */
    @RequestMapping(value = "/cda_types/as_parent_type", method = RequestMethod.GET)
    @ApiOperation(value = "根据cda类别Id获取可作为该cda类别父级的cda类别（不含自身及其以下子集）")
    public Envelop getAsParentType(
            @ApiParam(name = "id", value = "父级id")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key) throws Exception {
        //TODO 待微服务提供not in
        Envelop envelop = new Envelop();
        List<String> ids = new ArrayList<>();
        if(!StringUtils.isEmpty(id)){
            List<MCDAType> mCdaTypeSomeList = cdaTypeClient.getChildIncludeSelfByParentIdsAndKey(id, "");
            for(MCDAType m : mCdaTypeSomeList){
                ids.add(m.getId());
            }
        }
        List<MCDAType> mCdaTypeAllList = (List) cdaTypeClient.searchType("","","", 999, 1);
        if(ids.size()==0){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeAllList));
            return envelop;

        }
        List<MCDAType> mCdaTypeList = new ArrayList<>();
        for (MCDAType m : mCdaTypeAllList){
            if (!ids.contains(m.getId())){
                mCdaTypeList.add(m);
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
        return envelop;
    }


    /**
     * 用于cda类别前端页面树形显示
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cda_types/cda_types_tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有cda类别转成的CdaTypeTreeModel列表，初始页面显示")
    public Envelop getCdaTypeTreeModels() throws Exception {
        Envelop envelop = new Envelop();
        //顶级cda类别的父级id在数库是为空的
        List<MCDAType> mCdaTypeList = cdaTypeClient.getChildrenByPatientId("");
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        List<CdaTypeTreeModel> treeList = getCdaTypeChild(mCdaTypeList);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(treeList);
        return envelop;
    }

    /**
     *
     * 根据父级信息获取全部的子级信息（树形model）
     * @param info 父级信息
     * @return 全部子级信息
     */
    public List<CdaTypeTreeModel> getCdaTypeChild(List<MCDAType> info) {
        List<CdaTypeTreeModel> treeInfo = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            MCDAType typeInfo = info.get(i);
            CdaTypeTreeModel tree = new CdaTypeTreeModel();
            tree = convertToModel(typeInfo,CdaTypeTreeModel.class);
            List<MCDAType> listChild = cdaTypeClient.getChildrenByPatientId(typeInfo.getId());
            List<CdaTypeTreeModel> listChildTree = getCdaTypeChild(listChild);
            tree.setChildren(listChildTree);
            treeInfo.add(tree);
        }
        return treeInfo;
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
        envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
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
        List<MCDAType> mCdaTypeList = (List) cdaTypeClient.searchType("","code?"+code+" g1;name?"+name+" g1", "", 1000, 1);
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
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
        CdaTypeDetailModel cdaTypeDetailModel = convertToCdaTypeDetailModel(mCdaType);
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
        List<MCDAType> mCdaTypeList = (List) cdaTypeClient.searchType("","id="+ids, "", 100, 1);
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
        return envelop;
    }


    @RequestMapping(value = "/cda_types", method = RequestMethod.POST)
    @ApiOperation(value = "新增CDAType")
    public Envelop saveCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        CdaTypeDetailModel cdaTypeDetailModel = objectMapper.readValue(jsonData, CdaTypeDetailModel.class);
        MCDAType mCdaTypeOld = convertToMCDAType(cdaTypeDetailModel);
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
        CdaTypeDetailModel detailModel = convertToCdaTypeDetailModel(mCdaTypeNew);
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
        MCDAType mCdaTypeOld = convertToMCDAType(cdaTypeDetailModel);
        MCDAType mcdaType = cdaTypeClient.getCdaTypeById(mCdaTypeOld.getId());
        if (cdaTypeClient.isCDATypeExists(mCdaTypeOld.getCode()) && !mCdaTypeOld.getCode().equals(mcdaType.getCode())){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该cda类别代码已经存在！");
            return envelop;
        }
        mCdaTypeOld.setUpdateDate(new Date());
        String jsonDataNew = objectMapper.writeValueAsString(mCdaTypeOld);

        MCDAType mCdaTypeNew = cdaTypeClient.updateCDAType(mCdaTypeOld.getId(), jsonDataNew);
        if (mCdaTypeNew == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新cda类别失败！");
        }
        envelop.setSuccessFlg(true);
        CdaTypeDetailModel detailModel = convertToCdaTypeDetailModel(mCdaTypeNew);
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

    public MCDAType convertToMCDAType (CdaTypeDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MCDAType mcdaType = convertToModel(detailModel,MCDAType.class);
        mcdaType.setCreateDate(StringToDate(detailModel.getCreateDate(), AgAdminConstants.DateFormat));
        mcdaType.setUpdateDate(StringToDate(detailModel.getUpdateDate(), AgAdminConstants.DateFormat));

        return mcdaType;
    }

    public CdaTypeDetailModel convertToCdaTypeDetailModel(MCDAType mcdaType)
    {
        if(mcdaType==null)
        {
            return null;
        }
        CdaTypeDetailModel detailModel = convertToModel(mcdaType,CdaTypeDetailModel.class);
        detailModel.setCreateDate(DateToString(mcdaType.getCreateDate(),AgAdminConstants.DateFormat));
        detailModel.setUpdateDate(DateToString(mcdaType.getUpdateDate(),AgAdminConstants.DateFormat));

        return detailModel;
    }

    @RequestMapping(value = "/types/parent_id/other", method = RequestMethod.GET)
    @ApiOperation(value = "获取cdaType列表（不包含本身及其子类）")
    public List<MCDAType> getOtherCDAType(
            @ApiParam(name = "id", value = "cdaType编号")
            @RequestParam(value = "id") String id) throws Exception {
        List<MCDAType> listType = cdaTypeClient.getOtherCDAType(id);
        return listType;
    }

    /**
     * 获取CDAType列表
     * @param code
     * @param name
     * @return
     */
    @RequestMapping(value = "/cda_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取CDAType列表")
    public Envelop getCdaTypeList(
            @ApiParam(name = "code", value = "代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        List<MCDAType> mCdaTypeList = (List) cdaTypeClient.getCdaTypeList(code,name);
        if (mCdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
        }else {
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
        }
        return envelop;
    }
}
