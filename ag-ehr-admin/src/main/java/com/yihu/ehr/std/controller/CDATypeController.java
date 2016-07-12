package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdadocument.CDAModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeDetailModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeTreeModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.std.service.CDAClient;
import com.yihu.ehr.std.service.CDATypeClient;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.std.service.CDAVersionClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by yww on 2016/3/1.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/cda_type")
@RestController
public class CDATypeController extends BaseController {

    @Autowired
    CDATypeClient cdaTypeClient;

    @Autowired
    CDAVersionClient cdaVersionClient;

    @Autowired
    CDAClient cdaClient;

    @Autowired
    ConventionalDictEntryClient dictEntryClient;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/cda_types", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询CDAType列表")
    public Envelop searchCDATypes(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {
            ResponseEntity<Collection<MCDAType>> responseEntity = cdaTypeClient.searchType(fields, filters, sorts, size, page);
            List<MCDAType> mcdaTypeList = (List<MCDAType>) responseEntity.getBody();
            int totalCount = getTotalCount(responseEntity);
            return getResult(mcdaTypeList, totalCount, page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/cda_types/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "CDAType条件搜索(不分页)")
    public Envelop searchCDATypesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Envelop envelop = new Envelop();
        ResponseEntity<Collection<MCDAType>> responseEntity = cdaTypeClient.search(filters);
        List<MCDAType> mcdaTypeList = (List<MCDAType>) responseEntity.getBody();
        envelop.setDetailModelList(mcdaTypeList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

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
     * 用于cda类别前端页面树形显示
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cda_types/cda_types_tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有cda类别转成的CdaTypeTreeModel列表，初始页面显示")
    public Envelop getCdaTypeTreeModels(
            @ApiParam(name = "code_name", value = "cda类别的编码或名称")
            @RequestParam(value = "code_name") String codeName) throws Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        //获取到的顶级cda类别集合
        List<MCDAType> mcdaTypesAll = cdaTypeClient.getChildrenByPatientId("");
        //顶级类别中符合条件的类别集合
        List<MCDAType> mcdaTypesSome = new ArrayList<>();
        //顶级类别中不符合条件的类别集合
        List<MCDAType> mcdaTypesOthers = new ArrayList<>();
        if (mcdaTypesAll.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的cda类别！");
            return envelop;
        }
        List<CdaTypeTreeModel> treeList = new ArrayList<>();
        if(StringUtils.isEmpty(codeName)){
            treeList = getCdaTypeTreeModelChild(mcdaTypesAll);
            envelop.setDetailModelList(treeList);
            return envelop;
        }
        for(MCDAType mcdaType :mcdaTypesAll){
            if(mcdaType.getCode().contains(codeName) || mcdaType.getName().contains(codeName)){
                mcdaTypesSome.add(mcdaType);
                continue;
            }
            mcdaTypesOthers.add(mcdaType);
        }
        if (mcdaTypesSome.size()!=0){
            treeList.addAll(getCdaTypeTreeModelChild(mcdaTypesSome));
        }
        treeList .addAll(getCdaTypeTreeModelByCodeName(mcdaTypesOthers,codeName));
        envelop.setDetailModelList(treeList);
        return envelop;
    }

    /**
     * 递归不满足的父级类别集合的子集中满足条件TreeModel集合的方法
     * @param mcdaTypes 不符合的父级类别的集合
     * @param codeName
     * @return
     */
    public  List<CdaTypeTreeModel> getCdaTypeTreeModelByCodeName(List<MCDAType> mcdaTypes,String codeName){
        //结构：treeList 包含treeModel，treeModel包含listOfParent
        List<CdaTypeTreeModel> treeList = new ArrayList<>();
        for(MCDAType mcdaType:mcdaTypes){
            List<CdaTypeTreeModel> childList = new ArrayList<>();
            CdaTypeTreeModel treeModel = convertToModel(mcdaType,CdaTypeTreeModel.class);
            String parentId = mcdaType.getId();
            //获取所有下一级cda类别
            List<MCDAType> listAll = cdaTypeClient.getChildrenByPatientId(parentId);
            if(listAll.size() == 0){
                continue;
            }
            //获取所有下一级符合要求的cda类别
            String filters ="parentId="+parentId+";code?" +codeName+" g1;name?"+codeName+" g1;";
            ResponseEntity<Collection<MCDAType>> responseEntity = cdaTypeClient.search(filters);
            List<MCDAType> listSome = (List<MCDAType>)responseEntity.getBody();
            if(listSome.size()!=0){
                childList.addAll(getCdaTypeTreeModelChild(listSome));
            }
            //取剩下不符合要求的进行递归
            listAll.removeAll(listSome);
            if(listAll.size() != 0){
                childList.addAll(getCdaTypeTreeModelByCodeName(listAll,codeName));
            }
            if(childList.size() != 0){
                treeModel.setChildren(childList);
                treeList.add(treeModel);
            }
        }
        return treeList;
    }

    /**
     *
     * 根据父级信息获取全部的子级信息（树形model）
     * @param info 父级信息
     * @return 全部子级信息
     */
    public List<CdaTypeTreeModel> getCdaTypeTreeModelChild(List<MCDAType> info) {
        List<CdaTypeTreeModel> treeInfo = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            MCDAType typeInfo = info.get(i);
            CdaTypeTreeModel tree = new CdaTypeTreeModel();
            tree = convertToModel(typeInfo,CdaTypeTreeModel.class);
            List<MCDAType> listChild = cdaTypeClient.getChildrenByPatientId(typeInfo.getId());
            List<CdaTypeTreeModel> listChildTree = getCdaTypeTreeModelChild(listChild);
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

//    @RequestMapping(value = "/cda_types/code_name", method = RequestMethod.GET)
//    @ApiOperation(value = "根据code或者name获取CDAType列表")
//    public Envelop getCdaTypeByCodeOrName(
//            @ApiParam(name = "code", value = "代码")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "名称")
//            @RequestParam(value = "name") String name) {
//        Envelop envelop = new Envelop();
//        List<MCDAType> mCdaTypeList = (List) cdaTypeClient.searchType("","code?"+code+" g1;name?"+name+" g1", "", 1000, 1);
//        if (mCdaTypeList.size() == 0){
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("没有匹配条件的cda类别！");
//            return envelop;
//        }
//        envelop.setSuccessFlg(true);
//        envelop.setDetailModelList(convertToCdaTypeModels(mCdaTypeList));
//        return envelop;
//    }


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


    @RequestMapping(value = "/types/parent", method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    public Envelop getCdaTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        List<MCDAType> mcdaTypeList = cdaTypeClient.getCdaTypeExcludeSelfAndChildren(id);
        if(mcdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配的cda类别列表！");
        }
        envelop.setDetailModelList(convertToCdaTypeModels(mcdaTypeList));
        return  envelop;
    }

    /**
     * 删除cda类别前先判断是否有关联的cda文档
     * @param ids
     * @return
     */
    @RequestMapping(value = "/isExitRelativeCDA", method = RequestMethod.GET)
    @ResponseBody
    public Object isExitRelativeCDA(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String ids) throws Exception {
        //获取所有子集（含自身）
        Envelop envelop = new Envelop();
        List<MCDAType> mcdaTypes = cdaTypeClient.getChildIncludeSelfByParentIdsAndKey(ids, "");
        if (mcdaTypes == null){
            return failed("获取cda类别失败！");
        }
        String cdaTypeIds ="";
        for(MCDAType cdaTypeModel: mcdaTypes){
            cdaTypeIds += cdaTypeIds+cdaTypeModel.getId()+",";
        }
        cdaTypeIds = cdaTypeIds.substring(0, cdaTypeIds.length() - 1);
        String filters = "type="+cdaTypeIds;
        //获取当前所有版本
        ResponseEntity<Collection<MCDAVersion>> entity = cdaVersionClient.searchCDAVersions("", "", "", 1000, 1);
        Collection<MCDAVersion> mCdaVersions = entity.getBody();
        envelop.setSuccessFlg(false);
        for (MCDAVersion mcdaVersion : mCdaVersions){
            //对应每个版本的cda文档是否有关联指定的cda类别
            ResponseEntity<List<MCDADocument>> responseEntity  = cdaClient.GetCDADocuments("", filters,"", 1000, 1, mcdaVersion.getVersion());
            List<MCDADocument> mcdaDocuments = responseEntity.getBody();
            if (mcdaDocuments.size()!=0){
                envelop.setSuccessFlg(true);
            }
        }
        return envelop;
    }
    @RequestMapping(value = "/types/getCdaByTypeForBrowser", method = RequestMethod.GET)
    @ResponseBody
    public Envelop getCdaByTypeForBrowser(
            @ApiParam(name = "version", value = "version")
            @RequestParam(value = "version") String version) throws Exception {
        //获取浏览器需要的cda类别名称列表（只到最底层类别）
        Envelop envelop = new Envelop();

        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getCdaTypeForBrowserList();
        if(mConventionalDicts.size() == 0 || mConventionalDicts == null){
            return failed("获取cda类别清单失败！");
        }

        Map cdaType = new HashMap<>();
        List<Map> cdaList = new ArrayList<>();

        for(MConventionalDict mConventionalDict : mConventionalDicts ){
            cdaType.clear();
            //typeId信息维护成CDA类别ID
            cdaType.put("typeId",mConventionalDict.getCatalog().toString());
            cdaType.put("value", mConventionalDict.getValue().toString());

            String filters = "type=" + mConventionalDict.getCatalog().toString();
            ResponseEntity<List<MCDADocument>> responseEntity  = cdaClient.GetCDADocuments("", filters,"", 1000, 1, version);
            List<MCDADocument> mcdaDocuments = responseEntity.getBody();
            if(mcdaDocuments.size() == 0 ||mcdaDocuments == null ){
                cdaType.put("cda", "");
            }
            else{
                cdaType.put("cda", mcdaDocuments);
            }

            cdaList.add(cdaType);
        }

        envelop.setSuccessFlg(true);
        envelop.setObj(cdaList);
        return envelop;
    }

}
