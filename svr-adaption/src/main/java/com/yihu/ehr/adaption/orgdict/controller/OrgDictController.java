package com.yihu.ehr.adaption.orgdict.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.orgdict.service.OrgDict;
import com.yihu.ehr.adaption.orgdict.service.OrgDictManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.parm.FieldCondition;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgdict")
@Api(protocols = "https", value = "orgdict", description = "机构字典管理接口", tags = {"机构字典"})
public class OrgDictController extends BaseRestController {

    @Autowired
    private OrgDictManager orgDictManager;

//    @RequestMapping(value = "/info", method = RequestMethod.GET)
//    @ApiOperation(value = "根据id查询实体")
//    public Result getOrgDict(
//            @ApiParam(name = "id", value = "查询条件", defaultValue = "")
//            @RequestParam(value = "id", required = false) long id) {
//
//        Result result = new Result();
//        try {
//            OrgDict orgDict = orgDictManager.findOne(id);
//            result.setObj(orgDict);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    @ApiOperation(value = "创建机构字典")
//    public Result createOrgDict(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "code", value = "code", defaultValue = "")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "name", defaultValue = "")
//            @RequestParam(value = "name") String name,
//            @ApiParam(name = "orgCode", value = "name", defaultValue = "")
//            @RequestParam(value = "orgCode") String orgCode,
//            @ApiParam(name = "description", value = "description", defaultValue = "")
//            @RequestParam(value = "description") String description,
//            @ApiParam(name = "userId", value = "userId", defaultValue = "")
//            @RequestParam(value = "userId") String userId) {
//
//        Result result = new Result();
//        try {
//            //重复校验
//            if (orgDictManager.isExistOrgDict(orgCode, code)) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典已存在！");
//                return result;
//            }
//            OrgDict orgDict = new OrgDict();
//            orgDict.setCode(code);
//            orgDict.setName(name);
//            orgDict.setDescription(description);
//            orgDict.setOrganization(orgCode);
//            orgDict.setCreateDate(new Date());
//            orgDict.setCreateUser(userId);
//            orgDictManager.createOrgDict(orgDict);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//
//    @RequestMapping(value = "", method = RequestMethod.DELETE)
//    @ApiOperation(value = "删除机构字典")
//    public boolean deleteOrgDict(
//            @ApiParam(name = "id", value = "编号", defaultValue = "")
//            @RequestParam(value = "id") long id) {
//
//        try {
//            orgDictManager.deleteOrgDict(id);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
//    @RequestMapping(value = "", method = RequestMethod.PUT)
//    @ApiOperation(value = "修改机构字典")
//    public Result updateOrgDict(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
//            @RequestParam(value = "orgCode") String orgCode,
//            @ApiParam(name = "id", value = "id", defaultValue = "")
//            @RequestParam(value = "id") long id,
//            @ApiParam(name = "code", value = "code", defaultValue = "")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "name", defaultValue = "")
//            @RequestParam(value = "name") String name,
//            @ApiParam(name = "description", value = "description", defaultValue = "")
//            @RequestParam(value = "description") String description,
//            @ApiParam(name = "userId", value = "userId", defaultValue = "")
//            @RequestParam(value = "userId") String userId) {
//
//        Result result = getSuccessResult(false);
//        try {
//            OrgDict orgDict = orgDictManager.findOne(id);
//            if (orgDict == null) {
//                result.setErrorMsg("该字典不存在！");
//                return result;
//            }
//            //重复校验
//            boolean updateFlg = orgDict.getCode().equals(code) || !orgDictManager.isExistOrgDict(orgCode, code);
//            if (updateFlg) {
//                orgDict.setCode(code);
//                orgDict.setName(name);
//                orgDict.setDescription(description);
//                orgDict.setUpdateDate(new Date());
//                orgDict.setUpdateUser(userId);
//                orgDictManager.save(orgDict);
//                result.setSuccessFlg(true);
//            }
//            else
//                result.setErrorMsg("该字典已存在！");
//        } catch (Exception e) {
//            result.setErrorMsg("修改字典失败！");
//        }
//        return  result;
//    }
//
//
//    @RequestMapping(value = "/page", method = RequestMethod.GET)
//    @ApiOperation(value = "条件查询")
//    public Object searchOrgDicts(
//            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
//            @RequestParam(value = "parmJson", required = false) String parmJson) {
//        Result result = new Result();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
//            result = orgDictManager.pagesToResult(pageModel);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//    @RequestMapping(value = "/combo", method = RequestMethod.GET)
//    @ApiOperation(value = "机构字典下拉")
//    public Result getOrgDict(
//            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
//            @RequestParam(value = "orgCode", required = false) String orgCode){
//        Result result = new Result();
//        try {
//            PageModel pageModel = new PageModel();
//            pageModel.addFieldCondition(new FieldCondition("organization", "=", orgCode));
//            List<OrgDict> orgDictList = orgDictManager.pages(pageModel);
//            List<String> orgDicts = new ArrayList<>();
//            for (OrgDict orgDict : orgDictList) {
//                orgDicts.add(String.valueOf(orgDict.getSequence())+','+orgDict.getName());
//            }
//            result.setSuccessFlg(true);
//            result.setDetailModelList(orgDicts);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }

}
