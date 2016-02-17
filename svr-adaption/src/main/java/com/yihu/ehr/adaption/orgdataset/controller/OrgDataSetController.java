package com.yihu.ehr.adaption.orgdataset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSet;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSetService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.query.PageModel;
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
@RequestMapping(ApiVersionPrefix.CommonVersion + "adapter/org")
@Api(protocols = "https", value = "orgdataset", description = "机构数据集管理接口", tags = {"机构数据集"})

public class OrgDataSetController extends BaseRestController {

    @Autowired
    private OrgDataSetService orgDataSetManager;

    @RequestMapping(value = "/dataset", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Result getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") long id) {
        Result result = new Result();
        try {
            OrgDataSet orgDataSet = orgDataSetManager.findOne(id);
            result.setObj(orgDataSet);
            result.setSuccessFlg(true);
        } catch (Exception es) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public Result createOrgDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = getSuccessResult(false);
        try {
            boolean isExist = orgDataSetManager.isExistOrgDataSet(orgCode, code, name);   //重复校验
            if (isExist) {
                result.setErrorMsg("该数据集已存在！");
                return result;
            }
            OrgDataSet orgDataSet = new OrgDataSet();
            orgDataSet.setCode(code);
            orgDataSet.setName(name);
            orgDataSet.setDescription(description);
            orgDataSet.setOrganization(orgCode);
            orgDataSet.setCreateDate(new Date());
            orgDataSet.setCreateUser(userId);
            orgDataSetManager.createOrgDataSet(orgDataSet);
            result.setSuccessFlg(true);
        } catch (Exception ex) {

        }
        return result;
    }


    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public boolean deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") long id) {

        try {
            orgDataSetManager.deleteOrgDataSet(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据集")
    public Result updateOrgDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = getSuccessResult(false);
        try {
            OrgDataSet orgDataSet = orgDataSetManager.findOne(id);
            if (orgDataSet == null) {
                result.setErrorMsg("该数据集不存在！");
                return result;
            } else {
                //重复校验
                boolean updateFlg = orgDataSet.getCode().equals(code) || !orgDataSetManager.isExistOrgDataSet(orgCode, code, name);
                if (updateFlg) {
                    orgDataSet.setCode(code);
                    orgDataSet.setName(name);
                    orgDataSet.setDescription(description);
                    orgDataSet.setUpdateDate(new Date());
                    orgDataSet.setUpdateUser(userId);
                    orgDataSetManager.save(orgDataSet);
                    result.setSuccessFlg(true);
                } else
                    result.setErrorMsg("该数据集已存在！");

            }
        } catch (Exception e) {

        }
        return result;
    }



    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Result searchOrgDataSets(
            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {
        Result result = new Result();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
            result = orgDataSetManager.getEnvelop(pageModel);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }

}
