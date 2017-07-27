package com.yihu.ehr.organization.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgDeptImportClient {

    @RequestMapping(value = "/orgDept/batch", method = RequestMethod.POST)
    @ApiOperation("批量导入机构&部门")
    boolean createOrgDeptsBatch(@RequestBody String orgDepts);

    @RequestMapping(value = "/code/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在部门编号")
    List codeExistence(@RequestBody String code);

    @RequestMapping(value = "/name/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在部门名称")
    List nameExistence(@RequestBody String name);

    @RequestMapping(value = "/orgCode/existence",method = RequestMethod.POST)
    @ApiOperation("获取已存在的机构代码")
    List orgCodeExistence(@RequestBody String orgCode);

    @RequestMapping(value = "/orgDept/code/existence", method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    boolean isExistence(
            @RequestParam(value="filters") String filters);
}
