package com.yihu.ehr.standard.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.standard.model.CDAType;
import com.yihu.ehr.standard.service.CdaTypeService;
import com.yihu.ehr.util.id.BizObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by linAZ
 */
@RequestMapping(ApiVersion.Version1_0)
@RestController
@Api(value = "CdaTypeEndPoint", description = "CDA类别", tags = {"标准服务-CDA类别"})
public class CdaTypeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private CdaTypeService cdaTypeService;


    @RequestMapping(value = ServiceApi.Standards.TypeChildren, method = RequestMethod.GET)
    @ApiOperation(value = "根据父级ID获取下级")
    public List<MCDAType> getChildrenByPatientId(
            @ApiParam(name = "id", value = "父级id",defaultValue = "")
            @RequestParam(value = "id",required = false) String parentId) throws Exception {

        List<CDAType> listType = cdaTypeService.getChildrenCDATypeByParentId(parentId);
        return (List<MCDAType>)convertToModels(listType,new ArrayList<MCDAType>(listType.size()),MCDAType.class,"");
    }


    @RequestMapping(value = ServiceApi.Standards.TypesChildren, method = RequestMethod.GET)
    @ApiOperation(value = "根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）")
    public List<MCDAType> getChildIncludeSelfByParentIdsAndKey(
            @ApiParam(name = "patient_ids", value = "父级id")
            @RequestParam(value = "patient_ids") String[] patientIds,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key) throws Exception {

        List<CDAType> parentTypes = cdaTypeService.getCDATypeByIds(patientIds);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes,"");   //递归获取
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        List<CDAType> cdaTypeList = cdaTypeService.getChildrenType(childrenIds,key);
        return  (List<MCDAType>)convertToModels(cdaTypeList,new ArrayList<MCDAType>(cdaTypeList.size()),MCDAType.class,"");
    }


//    @RequestMapping(value = ServiceApi.Standards.TypeList,method = RequestMethod.GET)
//    @ApiOperation(value = "根据code或者name获取CDAType列表")
//    public List<MCDAType> getCdaTypeByCodeOrName(
//            @ApiParam(name = "code", value = "代码")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "名称")
//            @RequestParam(value = "name") String name) {
//
//        List<CDAType> cdaTypeList = cdaTypeManager.GetCdaTypeByCodeOrName(code,name);
//        return  (List<MCDAType>)convertToModels(cdaTypeList,new ArrayList<MCDAType>(cdaTypeList.size()),MCDAType.class,"");

//    @RequestMapping(value = "/cda_types/ids/{ids}",method = RequestMethod.GET)
//    @ApiOperation(value = "根据ids获取CDAType列表")
//    public List<MCDAType> getCdaTypeByIds(
//            @ApiParam(name = "ids", value = "ids")
//            @RequestParam(value = "ids") String[] ids) {
//
//        List<CDAType> cdaTypeList = cdaTypeManager.getCDATypeByIds(ids);
//        return (List<MCDAType>)convertToModels(cdaTypeList,new ArrayList<MCDAType>(cdaTypeList.size()),MCDAType.class,"");
//    }

    @RequestMapping(value = ServiceApi.Standards.Types, method = RequestMethod.GET)
    @ApiOperation(value = "标准类别分页搜索")
    public Collection<MCDAType> searchType(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List appList = cdaTypeService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, cdaTypeService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MCDAType.class, fields);
    }

    @RequestMapping(value = ServiceApi.Standards.NoPageTypes, method = RequestMethod.GET)
    @ApiOperation(value = "标准字典不分页搜索")
    public List<MCDAType> searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<CDAType> cdaTypes = cdaTypeService.search(filters);
        return (List<MCDAType>) convertToModels(cdaTypes, new ArrayList<MCDAType>(cdaTypes.size()), MCDAType.class, "");
    }



    @RequestMapping(value = ServiceApi.Standards.Type, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取CDAType")
    public MCDAType getCdaTypeById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) {

        CDAType cdaType = cdaTypeService.getCdaTypeById(id);
        return convertToModel(cdaType,MCDAType.class);
    }



    @RequestMapping(value = ServiceApi.Standards.Types, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增CDAType")
    public MCDAType saveCDAType(
            @ApiParam(name = "model", value = "model")
            @RequestBody String jsonData) throws Exception {

        CDAType cdaType =  toEntity(jsonData, CDAType.class);
        cdaType.setId(getObjectId(BizObject.CdaType));
        cdaType.setCreateDate(new Date());
        cdaType = cdaTypeService.save(cdaType);
        return convertToModel(cdaType,MCDAType.class);
    }


    @RequestMapping(value = ServiceApi.Standards.Type,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改CDAType")
    public MCDAType updateCDAType(
            @ApiParam(name = "id", value = "编号")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "model", value = "json模型")
            @RequestBody String jsonData) throws Exception {

        CDAType cdaType =  toEntity(jsonData, CDAType.class);
        cdaType.setUpdateDate(new Date());
        cdaType.setId(id);
        cdaType = cdaTypeService.save(cdaType);
        return convertToModel(cdaType,MCDAType.class);
    }


    @RequestMapping(value = ServiceApi.Standards.TypesCodeExistence , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    public boolean isCDATypeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code){

        return cdaTypeService.isCodeExist(code);
    }


    /**
     * 删除CDA类别，若该类别存在子类别，将一并删除子类别
     * 先根据当前的类别ID获取全部子类别ID，再进行删除
     */
    @RequestMapping(value = ServiceApi.Standards.Types, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除CDA类别，若该类别存在子类别，将一并删除子类别")
    public boolean deleteCDATypeByPatientIds(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String[] ids) {

        List<CDAType> parentTypes = cdaTypeService.getCDATypeByIds(ids);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes, "");
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        return cdaTypeService.deleteCdaType(childrenIds);
    }

    @RequestMapping(value = ServiceApi.Standards.TypeOther, method = RequestMethod.GET)
    @ApiOperation(value = "获取cdaType列表（不包含本身）")
    public List<MCDAType> getOtherCDAType(
            @ApiParam(name = "id", value = "cdaType编号")
            @PathVariable(value = "id") String id) throws Exception {
        List<CDAType> listType = cdaTypeService.getOtherCDAType(id);
        return (List<MCDAType>)convertToModels(listType,new ArrayList<MCDAType>(listType.size()),MCDAType.class,"");
    }


    @RequestMapping(value = ServiceApi.Standards.TypeParent, method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    public List<MCDAType> getCdaTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception {
        List<CDAType> parentTypes = cdaTypeService.getCDATypeByIds(new String[]{id});
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes,"");   //递归获取
        List<CDAType> cdaTypes = cdaTypeService.getCdaTypeExcludeSelfAndChildren(childrenIds);

        return  (List<MCDAType>)convertToModels(cdaTypes,new ArrayList<MCDAType>(cdaTypes.size()),MCDAType.class,"");
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
            List<CDAType> listChild = cdaTypeService.getChildrenCDATypeByParentId(typeInfo.getId());
            if(listChild.size()>0){
                childrenIds = getChildIncludeSelfByParentsAndChildrenIds(listChild,childrenIds);
            }
        }
        return childrenIds;
    }

}
