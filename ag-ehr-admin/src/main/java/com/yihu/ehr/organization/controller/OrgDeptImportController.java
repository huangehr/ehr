package com.yihu.ehr.organization.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.organization.service.OrgDeptImportClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "orgDept", description = "机构&部门批量导入接口", tags = {"基础信息 - 机构&部门批量导入接口"})
public class OrgDeptImportController extends BaseController {

    @Autowired
    private OrgDeptImportClient orgDeptImportClient;

    @RequestMapping(value = "/orgDept/batch", method = RequestMethod.POST)
    @ApiOperation("批量导入机构&部门")
    public Envelop createOrgDeptsBatch(
            @ApiParam(name = "orgDepts", value = "JSON", defaultValue = "")
            @RequestParam(value = "orgDepts") String orgDepts) throws Exception {

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try{
            orgDeptImportClient.createOrgDeptsBatch(orgDepts);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("系统出错！");
        }
        return envelop;
    }

    @RequestMapping(value = "/code/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在部门编号")
    public List codeExistence(
            @ApiParam(name = "code", value = "", defaultValue = "")
            @RequestParam("code") String code) throws Exception {

        List existCode = orgDeptImportClient.codeExistence(code);
        return existCode;
    }

    @RequestMapping(value = "/name/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在部门名称")
    public List nameExistence(
            @ApiParam(name = "name", value = "", defaultValue = "")
            @RequestParam("name") String name) throws Exception {

        List existName = orgDeptImportClient.nameExistence(name);
        return existName;
    }

    @RequestMapping(value = "/orgCode/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在的机构代码")
    public List orgCodeExistence(
            @ApiParam(name = "orgCode", value = "", defaultValue = "")
            @RequestParam("orgCode") String orgCode) throws Exception {
        List existOrgCode = orgDeptImportClient.orgCodeExistence(orgCode);
        return existOrgCode;
    }

    @RequestMapping(value = "/orgDept/code/existence", method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public Envelop isExistence(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters) {

        try {
            return success(orgDeptImportClient.isExistence(filters));
        } catch (Exception e) {
            e.printStackTrace();
            return failed("查询出错！");
        }
    }
}
