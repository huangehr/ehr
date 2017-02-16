package com.yihu.ehr.api.doctor.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.api.doctor.service.UserAppClient;
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
@RequestMapping(ApiVersion.Version1_0 )
@RestController
@Api(value = "UserApp", description = "UserApp", tags = {"用户App关联"})
public class UserAppController extends BaseController {

    @Autowired
    UserAppClient userAppClient;

    @RequestMapping(value = ServiceApi.UserApp.UserAppList, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户id获取App列表")
    public Envelop getAppApis(
            @ApiParam(name = "userId", value = "用户id", defaultValue = "1")
            @RequestParam(value = "userId", required = true) String userId){
        List<MUserApp> list = userAppClient.getUserAppById(userId);

        Envelop envelop = getResult(list,list.size(),1,list.size());
        return envelop;
    }
}
