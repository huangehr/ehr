package com.yihu.ehr.portal.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MPortalSetting;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/17.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalSettingClient {

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSetting, method = RequestMethod.GET)
    @ApiOperation(value = "获取门户配置列表", notes = "根据查询条件获取门户配置列表在前端表格展示")
    ResponseEntity<List<MPortalSetting>> searchPortalSetting(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSettingAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取门户配置信息", notes = "门户配置信息")
    MPortalSetting getPortalSetting(@PathVariable(value = "portalSetting_id") Long portalSettingId);

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSetting, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建门户配置", notes = "重新绑定门户配置信息")
    MPortalSetting createPortalSetting(
            @ApiParam(name = "portalSetting_json_data", value = "", defaultValue = "")
            @RequestBody String portalSettingJsonData);

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSetting, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改门户配置", notes = "重新绑定门户配置信息")
    MPortalSetting updatePortalSetting(@ApiParam(name = "portalSetting_json_data", value = "", defaultValue = "")
                                           @RequestBody String portalSettingJsonData);

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSettingAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除门户配置", notes = "根据门户配置id删除")
    boolean deletePortalSetting(@PathVariable(value = "portalSetting_id") String portalSettingId);

}
