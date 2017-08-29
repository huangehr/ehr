package com.yihu.ehr.saas.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by zdm on 2017/5/26.
 */
@FeignClient(name=MicroServices.Geography)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgSaasClient {


    @ApiOperation(value = "根据机构获取机构相关授权")
    @RequestMapping(value = "/OrgSaasByOrg", method = RequestMethod.GET)
    ListResult getOrgSaasByorgCode(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type) ;
    /**
     * 机构授权并保存
     * @return
     */

    @ApiOperation(value = "机构授权检查,如果被授权的机构或者区域在指定机构总不存在，这新增这条记录，否则返回地址id")
    @RequestMapping(value = "/orgSaasSave", method = RequestMethod.POST)
    boolean saveOrgSaas(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestBody String jsonData);


    @RequestMapping(value = "/orgSaasDel", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构授权", notes = "根据机构code和授权类别删除机构授权")
    boolean deleteOrgSaas(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type);

}
