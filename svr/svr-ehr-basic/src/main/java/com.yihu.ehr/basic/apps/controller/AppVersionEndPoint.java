package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.service.AppVersionService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.app.version.AppVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Trick on 2018/3/12.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AppVersion", description = "上饶APP资源版本", tags = {"上饶APP应用版本管理"})
public class AppVersionEndPoint extends EnvelopRestEndPoint{

    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping(value = ServiceApi.AppVersion.FindAppVersion, method = RequestMethod.GET)
    @ApiOperation(value = "获取App版本")
    public Envelop getAppVersion(
            @ApiParam(name = "id", value = "当前版本ID", defaultValue = "")
            @RequestParam(value = "id")String id,
            @ApiParam(name = "version", value = "要获取的版本", defaultValue = "")
            @RequestParam(value = "version", required = false)Double version){
        AppVersion temp = appVersionService.getAppVersion(id);
        if (temp == null) {
            return failed("无效的APP类型失败！");
        }
        if (version > 0) {
            if (temp.getVersionInt() > version) {
                // 有新的版本号
                return success(temp);
            } else {
                // 已是最新版本
                return failed("已经是最新版本！");
            }
        } else {
            return failed("版本号有误");
        }
    }
}
