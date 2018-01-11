package com.yihu.ehr.basic.government.controller;

import com.yihu.ehr.basic.government.service.GovernmentBrowseLogService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "government_browse_log", description = "政府服务平台浏览记录", tags = {"政府服务平台"})
public class GovernmentBrowseLogEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private GovernmentBrowseLogService governmentBrowseLogService;

    @RequestMapping(value = ServiceApi.Government.AddGovernmentBrowseLog, method = RequestMethod.POST)
    @ApiOperation(value = "新增浏览记录")
    public GovernmentBrowseLog create(
            @ApiParam(name = "jsonData", value = " 记录信息Json", defaultValue = "")
            @RequestBody String jsonData) {
        GovernmentBrowseLog governmentBrowseLog = toEntity(jsonData, GovernmentBrowseLog.class);
        governmentBrowseLog.setCreateTime(new Date());
        governmentBrowseLog = governmentBrowseLogService.saveGovernmentBrowseLog(governmentBrowseLog);
        return governmentBrowseLog;
    }

    @RequestMapping(value = ServiceApi.Government.searchGovernmentBrowseLog, method = RequestMethod.GET)
    @ApiOperation(value = "浏览记录")
    public List<GovernmentBrowseLog> getBrowseName(
            @ApiParam(name = "userId", value = " 用户Id", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId) {
        List<GovernmentBrowseLog> browseName = governmentBrowseLogService.getBrowseName(userId);
        return browseName;
    }
}
