package com.yihu.ehr.standard.dispatch.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.MDispatchLog;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dispatch.service.DispatchLog;
import com.yihu.ehr.standard.dispatch.service.DispatchLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "dispatch-log", description = "分发与下载日志服务")
public class DispatchLogController extends ExtendController<MDispatchLog> {

    @Autowired
    private DispatchLogService dispatchLogService;

    @RequestMapping(value = ServiceApi.Standards.DispatchLogs, method = RequestMethod.GET)
    @ApiOperation(value = "获取日志信息")
    public MDispatchLog getLog(
            @ApiParam(required = true, name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) throws Exception {

        List ls = dispatchLogService.findByFields(
                new String[]{"stdVersionId", "orgId"},
                new Object[]{versionCode, orgCode});
        if (ls.size() > 0)
            return getModel(ls.get(0));
        return null;
    }

    @RequestMapping(value = ServiceApi.Standards.DispatchLogs, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除日志信息")
    public boolean deleteLog(
            @ApiParam(required = true, name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) throws Exception {

        return dispatchLogService.delete(versionCode, orgCode);
    }

    @RequestMapping(value = ServiceApi.Standards.DispatchLogs, method = RequestMethod.POST)
    @ApiOperation(value = "新增日志信息")
    public MDispatchLog saveLog(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) throws Exception {

        DispatchLog dispatchLog = toEntity(model, DispatchLog.class);
        dispatchLog.createId();
        return getModel(dispatchLogService.save(dispatchLog));
    }
}

