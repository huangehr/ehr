package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.service.AppVersionService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.app.version.AppVersion;
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
@Api(value = "AppVersion", description = "上饶APP资源版本", tags = {"上饶APP应用"})
public class AppVersionEndPoint {

    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping(value = ServiceApi.AppVersion.FindAppVersion, method = RequestMethod.GET)
    @ApiOperation(value = "获取App版本")
    public Envelop getAppVersion(@ApiParam(name = "id", value = "当前版本ID", defaultValue = "")
                                 @RequestParam(value = "id", required = true)String id,
                                 @ApiParam(name = "verison", value = "要获取的版本", defaultValue = "")
                                 @RequestParam(value = "verison", required = false)Double verison){
        Envelop envelop = new Envelop();
        try {
            AppVersion temp = appVersionService.getAppVersion(id);
            if (temp == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorCode(-1);
                envelop.setErrorMsg("无效的APP类型失败！");
                return envelop;
            }
            if (verison > 0) {
                if (temp.getVersionInt() > verison) {
                    // 有新的版本号
                    envelop.setSuccessFlg(true);
                    envelop.setObj(temp);
                    return envelop;
                } else {
                    // 已是最新版本
                    envelop.setSuccessFlg(false);
                    envelop.setErrorCode(-2);
                    envelop.setErrorMsg("已经是最新版本！");
                    return envelop;
                }
            } else {
                return envelop;
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorCode(-3);
            envelop.setErrorMsg("获取失败！");
            return envelop;
        }
    }
}
