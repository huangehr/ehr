package com.yihu.ehr.standard.cdatype.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.standard.cdatype.service.CDAType;
import com.yihu.ehr.standard.cdatype.service.CDATypeManager;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by linAZ
 */
@RequestMapping(ApiVersion.Version1_0+"/std")
@RestController
@Api(protocols = "https", value = "CDAType", description = "CDAType管理", tags = {"CDAType管理"})
public class CdaTypeController extends BaseRestController {

    @Autowired
    private CDATypeManager cdaTypeManager;


    /**
     * 根据父级ID获取下级
     */
    @RequestMapping(value = "/children_cda_types/{parent_id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据父级ID获取下级")
    public List<MCDAType> getChildrenByPatientId(
            @ApiParam(name = "parent_id", value = "父级id")
            @PathVariable(value = "parent_id") String parentId) throws Exception {
        List<CDAType> listType = cdaTypeManager.getChildrenCDATypeByParentId(parentId);
        return (List<MCDAType>)convertToModels(listType,new ArrayList<MCDAType>(listType.size()),MCDAType.class,"");
    }

    /**
     * 根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）
     * @param patientIds 父级ID
     * @param key 查询条件
     * @return
     */
    @RequestMapping(value = "/cda_types/patient_ids/key",method = RequestMethod.GET)
    @ApiOperation(value = "根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）")
    public List<MCDAType> getChildIncludeSelfByParentIdsAndKey(
            @ApiParam(name = "patient_ids", value = "父级id")
            @RequestParam(value = "patient_ids") String[] patientIds,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key) throws Exception {
        List<CDAType> parentTypes = cdaTypeManager.getCDATypeByIds(patientIds);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes,"");   //递归获取
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        List<CDAType> cdaTypeList = cdaTypeManager.getParentType(childrenIds,key);
        return  (List<MCDAType>)convertToModels(cdaTypeList,new ArrayList<MCDAType>(cdaTypeList.size()),MCDAType.class,"");
    }



//    public List<CDAType> getParentType(String[] parentIds, String key) {
//        List<CDAType> listParentType = cdaTypeManager.getCDATypeByIds(parentIds);
//        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(listParentType,"");
//        if(childrenIds.length()>0) {
//            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
//        }
//
//        List<CDAType> listType = cdaTypeManager.getParentType(childrenIds,key);
//        return listType;
//    }


    @RequestMapping(value = "/cda_types/code_name",method = RequestMethod.GET)
    @ApiOperation(value = "根据code或者name获取CDAType列表")
    public List<MCDAType> getCdaTypeByCodeOrName(
            @ApiParam(name = "code", value = "代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name) {
        List<CDAType> cdaTypeList = cdaTypeManager.GetCdaTypeByCodeOrName(code,name);
        return  (List<MCDAType>)convertToModels(cdaTypeList,new ArrayList<MCDAType>(cdaTypeList.size()),MCDAType.class,"");
    }


    @RequestMapping(value = "/cda_types/id/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取CDAType")
    public MCDAType getCdaTypeById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) {
        CDAType cdaType = cdaTypeManager.getCdaTypeById(id);
        return convertToModel(cdaType,MCDAType.class);
    }

    @RequestMapping(value = "/cda_types/ids/{ids}",method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取CDAType列表")
    public List<MCDAType> getCdaTypeByIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String[] ids) {
        List<CDAType> cdaTypeList = cdaTypeManager.getCDATypeByIds(ids);
        return (List<MCDAType>)convertToModels(cdaTypeList,new ArrayList<MCDAType>(cdaTypeList.size()),MCDAType.class,"");
    }



    @RequestMapping(value = "/cda_types",method = RequestMethod.POST)
    @ApiOperation(value = "新增CDAType")
    public MCDAType saveCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        CDAType cdaType =  new ObjectMapper().readValue(jsonData, CDAType.class);
        cdaType.setId(getObjectId(BizObject.CdaType));
        cdaType.setCreateDate(new Date());
        cdaType = cdaTypeManager.save(cdaType);
        return convertToModel(cdaType,MCDAType.class);
    }

    @RequestMapping(value = "/cda_types",method = RequestMethod.PUT)
    @ApiOperation(value = "修改CDAType")
    public MCDAType updateCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        CDAType cdaType =  new ObjectMapper().readValue(jsonData, CDAType.class);
        cdaType.setUpdateDate(new Date());
        cdaType = cdaTypeManager.save(cdaType);
        return convertToModel(cdaType,MCDAType.class);
    }

    @RequestMapping(value = "/cda_types/existence/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    public boolean isCDATypeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return cdaTypeManager.isCodeExist(code);
    }




    /**
     * 删除CDA类别，若该类别存在子类别，将一并删除子类别
     * 先根据当前的类别ID获取全部子类别ID，再进行删除
     */
    @RequestMapping(value = "/cda_types/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除CDA类别，若该类别存在子类别，将一并删除子类别")
    public boolean deleteCDATypeByPatientIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String[] ids) {
        List<CDAType> parentTypes = cdaTypeManager.getCDATypeByIds(ids);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes, "");
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        return cdaTypeManager.deleteCdaType(childrenIds);
    }

    /**
     * 根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）
     * @param parentTypes 父级信息
     * @param childrenIds   子级ID
     */
    public String getChildIncludeSelfByParentsAndChildrenIds(List<CDAType> parentTypes,String childrenIds) {
        for (int i = 0; i < parentTypes.size(); i++) {
            CDAType typeInfo = parentTypes.get(i);
            childrenIds+=typeInfo.getId()+",";
            List<CDAType> listChild = cdaTypeManager.getChildrenCDATypeByParentId(typeInfo.getId());
            if(listChild.size()>0){
                childrenIds = getChildIncludeSelfByParentsAndChildrenIds(listChild,childrenIds);
            }
        }
        return childrenIds;
    }

}
