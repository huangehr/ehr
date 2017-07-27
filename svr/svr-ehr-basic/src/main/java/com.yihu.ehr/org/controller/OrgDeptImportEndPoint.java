package com.yihu.ehr.org.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.service.OrgDeptImportService;
import com.yihu.ehr.org.service.OrgDeptService;
import com.yihu.ehr.org.service.OrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Administrator on 2017/7/15.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "orgDept", description = "机构&部门批量导入接口", tags = {"基础信息 - 机构&部门批量导入接口"})
public class OrgDeptImportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgDeptImportService orgDeptImportService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private OrgDeptService orgDeptService;

    @RequestMapping(value = "/orgDept/batch", method = RequestMethod.POST)
    @ApiOperation("批量导入机构&部门")
    public boolean createOrgDeptsBatch(
            @ApiParam(name = "orgDepts", value = "JSON", defaultValue = "")
            @RequestBody String orgDepts) throws Exception{
        List models = objectMapper.readValue(orgDepts, new TypeReference<List>() {});
        orgDeptImportService.addOrgDeptBatch(models);
        return true;
    }

    @RequestMapping(value = "/code/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在部门编号")
    public List codeExistence(
            @ApiParam(name = "code", value = "", defaultValue = "")
            @RequestBody String code) {
        List codes = orgDeptImportService.codeExistence(toEntity(code, String[].class));
        return  codes;
    }

    @RequestMapping(value = "/name/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在部门名称")
    public List nameExistence(
            @ApiParam(name = "name", value = "", defaultValue = "")
            @RequestBody String name) {
        List names = orgDeptImportService.nameExistence(toEntity(name, String[].class));
        return  names;
    }

    @RequestMapping(value = "/orgCode/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在的机构代码")
    public List orgCodeExistence(
            @ApiParam(name = "orgCode", value = "", defaultValue = "")
            @RequestBody String orgCode) {
        List orgCodes = orgService.orgCodeExistence(toEntity(orgCode, String[].class));
        return orgCodes;
    }

    @RequestMapping(value = "/orgDept/code/existence", method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistence(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) throws Exception {

        List<OrgDept> orgDepts = orgDeptService.search("",filters,"", 1, 1);
        return orgDepts!=null && orgDepts.size()>0;
    }
}
