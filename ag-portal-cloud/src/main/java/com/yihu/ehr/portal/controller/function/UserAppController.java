package com.yihu.ehr.portal.controller.function;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.portal.service.common.AppClient;
import com.yihu.ehr.portal.service.function.UserAppClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MUserApp;
import com.yihu.ehr.util.rest.Envelop;
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
 * Created by cws on 2017年2月4日
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "UserApp", description = "UserApp", tags = {"用户App关联"})
public class UserAppController extends BaseController {

    @Autowired
    UserAppClient userAppClient;
    @Autowired
    AppClient appClient;

    @RequestMapping(value = ServiceApi.UserApp.UserAppList, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户id获取App列表")
    public Envelop getAppApis(
            @ApiParam(name = "userId", value = "用户id", defaultValue = "1")
            @RequestParam(value = "userId", required = true) String userId){
        List<MUserApp> list = userAppClient.getUserAppById(userId);
        if (list != null && list.size() > 0 ){
            for (MUserApp mUserApp : list){
                MApp mapp =  appClient.getApp(mUserApp.getAppId());
                if (mapp != null){
                    mUserApp.setLinkUrl(mapp.getUrl());
                }
            }
        }

        Envelop envelop = getResult(list,list.size(),1,list.size());
        return envelop;
    }

    @RequestMapping(value = ServiceApi.UserApp.UserAppShow, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID更新APP的展示状态")
    public Envelop updateUserAppShowFlag(
            @ApiParam(name = "id", value = "用户APP关联ID")
            @RequestParam(value = "id", required = true) String id,
            @ApiParam(name = "flag", value = "用户id", defaultValue = "1")
            @RequestParam(value = "flag", required = true) String flag){

        Envelop envelop = new Envelop();
        MUserApp mUserApp = userAppClient.updateUserAppShowFlag(id,flag);
        if(mUserApp == null){
            envelop = success("");
        }else{
            envelop = failed("状态更新失败。");
        }
        return envelop;
    }
}
