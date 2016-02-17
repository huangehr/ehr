package com.yihu.ehr.adaption.orgmetaset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaData;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgmetadata")
@Api(protocols = "https", value = "orgmetadata", description = "机构数据元", tags = {"机构数据元"})
public class OrgMetaDataController extends BaseRestController {

    @Autowired
    private OrgMetaDataManager orgMetaDataManager;


//    @RequestMapping(value = "", method = RequestMethod.GET)
//    @ApiOperation(value = "根据id获取机构数据元")
//    public Result getOrgMetaData(
//            @ApiParam(name = "id", value = "编号", defaultValue = "")
//            @RequestParam(value = "id") long id) {
//        Result result = new Result();
//        try {
//            OrgMetaData orgMetaData = orgMetaDataManager.findOne(id);
//            result.setObj(orgMetaData);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    @ApiOperation(value = "新增数据元")
//    public Result createOrgMetaData(
//            @ApiParam(name = "api_version", value = "API?汾??", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
//            @RequestParam(value = "orgDataSetSeq") int orgDataSetSeq,
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
//        Result result = new Result();
//        try {
//            boolean isExist = orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code);//重复验证
//
//            if (isExist) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元已存在");
//                return result;
//            }
//            OrgMetaData orgMetaData = new OrgMetaData();
//            orgMetaData.setCode(code);
//            orgMetaData.setName(name);
//            orgMetaData.setOrgDataSet(orgDataSetSeq);
//            orgMetaData.setCreateDate(new Date());
//            orgMetaData.setCreateUser(userId);
//            orgMetaData.setOrganization(orgCode);
//            orgMetaData.setDescription(description);
//            orgMetaDataManager.createOrgMetaData(orgMetaData);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(true);
//        }
//        return result;
//    }
//
//
//    @RequestMapping(value = "", method = RequestMethod.DELETE)
//    @ApiOperation(value = "删除数据元")
//    public boolean deleteOrgMetaData(
//            @ApiParam(name = "id", value = "编号", defaultValue = "")
//            @RequestParam(value = "id") long id) {
//
//        try {
//            orgMetaDataManager.delete(id);
//            return true;
//        } catch (Exception e) {
//
//            return false;
//        }
//    }
//
//    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
//    @ApiOperation(value = "批量删除数据元")
//    public boolean deleteOrgMetaDataList(
//            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
//            @RequestParam(value = "ids[]") Long[] ids) {
//
//        if (ids == null || ids.length == 0)
//            return true;
//
//        try {
//            orgMetaDataManager.delete(ids, "id");
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @RequestMapping(value = "/orgMetaData", method = RequestMethod.PUT)
//    @ApiOperation(value = "修改数据元")
//    public Result updateOrgMetaData(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
//            @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
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
//        Result result = new Result();
//        try {
//            OrgMetaData orgMetaData = orgMetaDataManager.findOne(id);
//            if (orgMetaData == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("不存在该数据元");
//                return result;
//            } else {
//                //重复验证
//                boolean updateFlg = orgMetaData.getCode().equals(code) || !orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code);
//                if (updateFlg) {
//                    orgMetaData.setCode(code);
//                    orgMetaData.setName(name);
//                    orgMetaData.setDescription(description);
//                    orgMetaData.setUpdateDate(new Date());
//                    orgMetaData.setUpdateUser(userId);
//                    orgMetaData.setOrganization(orgCode);
//                    orgMetaDataManager.save(orgMetaData);
//                    result.setSuccessFlg(true);
//                }
//                else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("数据元代码重复！");
//                }
//            }
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//    @RequestMapping(value = "/page", method = RequestMethod.GET)
//    @ApiOperation(value = "分页查询")
//    public Result searchOrgMetaDatas(
//            @ApiParam(name = "parmJson", value = "分页模型", defaultValue = "")
//            @RequestParam(value = "parmJson", required = false) String parmJson) {
//        Result result = new Result();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
//            result = orgMetaDataManager.pagesToResult(pageModel);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }


}
