package com.yihu.ehr.government.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.government.GovernmentBrowseLogModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import com.yihu.ehr.government.service.GovernmentBrowseLogClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "government_menu", description = "政府服务平台浏览记录", tags = {"政府服务平台"})
public class GovernmentBrowseLogController extends ExtendController<GovernmentBrowseLogModel> {
    @Autowired
    private GovernmentBrowseLogClient governmentBrowseLogClient;

    @RequestMapping(value = ServiceApi.Government.AddGovernmentBrowseLog, method = RequestMethod.POST)
    @ApiOperation(value = "新增浏览记录")
    public Envelop create(
            @ApiParam(name = "jsonData", value = " 记录信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData) {
        GovernmentBrowseLog governmentBrowseLog = governmentBrowseLogClient.saveGovernmentBrowseLog(jsonData);
        if (null == governmentBrowseLog) {
            return failed("保存失败!");
        }
        return success(governmentBrowseLog);
    }

    @RequestMapping(value = ServiceApi.Government.searchGovernmentBrowseLog, method = RequestMethod.GET)
    @ApiOperation(value = "浏览记录")
    public List<String> getBrowseName(
            @ApiParam(name = "userId", value = " 用户Id", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId) {
        return governmentBrowseLogClient.getBrowseName(userId);
    }
}
