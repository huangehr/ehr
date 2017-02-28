package com.yihu.ehr.portal.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MPortalStandards;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/21.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalStandardsClient {

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandards, method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范列表", notes = "根据查询条件获取标准规范列表在前端表格展示")
    ResponseEntity<List<MPortalStandards>> searchPortalStandards(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandardsAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范信息", notes = "标准规范信息")
    MPortalStandards getPortalStandard(@PathVariable(value = "portalStandard_id") Long portalStandardId);

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandards, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准规范", notes = "重新绑定标准规范信息")
    MPortalStandards createPortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestBody String portalstandardJsonData);

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandards, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改标准规范", notes = "重新绑定标准规范信息")
    MPortalStandards updatePortalStandard(@ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
                                          @RequestBody String portalStandardJsonData);

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandardsAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准规范", notes = "根据标准规范id删除")
    boolean deletePortalStandard(@PathVariable(value = "portalStandard_id") String portalStandardId);

}
