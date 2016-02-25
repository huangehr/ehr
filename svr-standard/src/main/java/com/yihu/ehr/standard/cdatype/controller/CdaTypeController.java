package com.yihu.ehr.standard.cdatype.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.standard.cdatype.service.CDAType;
import com.yihu.ehr.standard.cdatype.service.CDATypeManager;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    public List<CDAType> getChildrenByPatientId(
            @ApiParam(name = "parent_id", value = "父级id")
            @PathVariable(value = "parent_id") String parentId) throws Exception {
        List<CDAType> listType = cdaTypeManager.getChildrenCDATypeByParentId(parentId);
        return listType;
    }

    /**
     * 根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）
     * @param patientIds 父级ID
     * @param key 查询条件
     * @return
     */
    @RequestMapping(value = "/cda_types/patient_ids/key",method = RequestMethod.GET)
    @ApiOperation(value = "根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）")
    public List<CDAType> getChildIncludeSelfByParentTypesAndKey(
            @ApiParam(name = "patient_ids", value = "父级id")
            @RequestParam(value = "patient_ids") String[] patientIds,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key) throws Exception {
        List<CDAType> parentTypes = cdaTypeManager.getCDATypeByIds(patientIds);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes,"");   //递归获取
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        return  cdaTypeManager.getParentType(childrenIds,key);
    }



    @RequestMapping(value = "/cda_types/code_name",method = RequestMethod.GET)
    @ApiOperation(value = "根据code或者name获取CDAType列表")
    public List<CDAType> getCdaTypeByCodeOrName(
            @ApiParam(name = "code", value = "代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name) {
        return cdaTypeManager.GetCdaTypeByCodeOrName(code,name);
    }


    @RequestMapping(value = "/cda_types/id/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取CDAType")
    public CDAType getCdaTypeById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) {
        return cdaTypeManager.getCdaTypeById(id);
    }

    @RequestMapping(value = "/cda_types/ids/{ids}",method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取CDAType列表")
    public List<CDAType> getCdaTypeByIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String[] ids) {
        return cdaTypeManager.getCDATypeByIds(ids);
    }



    @RequestMapping(value = "/cda_types",method = RequestMethod.POST)
    @ApiOperation(value = "保存CDAType,新增或者修改")
    public Object saveCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CDAType cdaType =  objectMapper.readValue(jsonData, CDAType.class);
        if (!StringUtils.isEmpty(cdaType.getId())) {      //add
            cdaType.setUpdateDate(new Date());
        } else {
            cdaType.setCreateDate(new Date());
        }
        return cdaTypeManager.save(cdaType);
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
    public Object deleteCDATypeByPatientIds(
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
