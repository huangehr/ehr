package com.yihu.ehr.adaption.orgdict.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.orgdict.service.OrgDict;
import com.yihu.ehr.adaption.orgdict.service.OrgDictManager;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaData;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lincl on 2016.1.29
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgdict")
@Api(protocols = "https", value = "orgdict", description = "机构字典管理接口", tags = {"机构字典"})
public class OrgDictController extends BaseRestController {

    @Autowired
    private OrgDictManager orgDictManager;

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping(value = "/info" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Object getOrgDict(
            @ApiParam(name = "id", value = "查询条件", defaultValue = "")
            @RequestParam(value = "id", required = false) long id) {

        Result result = new Result();
        try {
            OrgDict orgDict  = orgDictManager.getOrgDict(id);
            result.setObj(orgDict);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result;
    }

    /**
     * 创建机构字典
     * @param apiVersion
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典")
    public Object createOrgDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "orgCode", value = "name", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId){

        Result result = new Result();
        try {
            boolean isExist = orgDictManager.isExistOrgDict(orgCode, code);   //重复校验

            if(isExist){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典已存在！");
                return  false;
            }
            OrgDict orgDict = new OrgDict();
            orgDict.setCode(code);
            orgDict.setName(name);
            orgDict.setDescription(description);
            orgDict.setOrganization(orgCode);
            orgDict.setCreateDate(new Date());
            orgDict.setCreateUser(userId);
            orgDictManager.createOrgDict(orgDict);
            return true;
        }catch (Exception ex){
            result.setSuccessFlg(false);
            return false;
        }
    }

    /**
     * 删除机构字典
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典")
    public Object deleteOrgDict(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") long id) {

        Result result = new Result();
        try {
            orgDictManager.deleteOrgDict(id);
            return true;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("删除字典失败！");
            return false;
        }
    }

    /**
     * 修改机构字典
     * @param apiVersion
     * @param orgCode
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构字典")
    public Object updateOrgDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = new Result();
        try{
            OrgDict orgDict = orgDictManager.getOrgDict(id);
            if(orgDict == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典不存在！");
                return false;
            }else {
                //重复校验
                boolean updateFlg = orgDict.getCode().equals(code) || !orgDictManager.isExistOrgDict(orgCode, code);
                if (updateFlg) {
                    orgDict.setCode(code);
                    orgDict.setName(name);
                    orgDict.setDescription(description);
                    orgDict.setUpdateDate(new Date());
                    orgDict.setUpdateUser(userId);
                    orgDictManager.updateOrgDict(orgDict);
                    return true;
                }
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典已存在！");
                return false;
            }
        }catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("修改字典失败！");
            return false;
        }
    }


    /**
     * 条件查询
     *
     * @param parmJson
     * @return
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Object searchOrgDicts(
            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {
        Result result = new Result();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
            pageModel.setModelClass(OrgDict.class);
            List<OrgDict> detailModelList = orgDictManager.searchOrgDicts(pageModel);
            Integer totalCount = orgDictManager.searchTotalCount(pageModel);
            result = getResult(detailModelList, totalCount, pageModel.getPage(), pageModel.getRows());
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }

}
