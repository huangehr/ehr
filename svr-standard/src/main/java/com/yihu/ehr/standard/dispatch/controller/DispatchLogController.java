package com.yihu.ehr.standard.dispatch.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.standard.MDispatchLog;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dispatch.service.DispatchLog;
import com.yihu.ehr.standard.dispatch.service.DispatchLogService;
import com.yihu.ehr.standard.dispatch.service.DispatchService;
import com.yihu.ehr.util.RestEcho;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/std/dispatch")
@Api(protocols = "https", value = "dispatch-log", description = "标准分发日志接口", tags = {"标准化", "适配方案", "分发", "日志"})
public class DispatchLogController extends ExtendController<MDispatchLog> {

    @Autowired
    private DispatchLogService dispatchLogService;

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    @ApiOperation(value = "获取日志信息")
    public MDispatchLog getLog(
            @ApiParam(required = true, name = "versionCode", value = "版本号")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode", required = true) String orgCode) throws Exception{

        List ls = dispatchLogService.findByFields(
                        new String[]{"stdVersionId", "orgId"},
                        new Object[]{versionCode, orgCode});
        if(ls.size()>0)
            return getModel(ls.get(0));
        return null;
    }
}

