package com.yihu.ehr.basic.apps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.basic.apps.service.UserAppService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yeshijie
 * @version 1.0
 * @created 2017年2月16日18:04:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "UserApp", description = "用户应用关联", tags = {"平台应用-用户应用关联"})
public class UserAppEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private UserAppService userAppService;

    @RequestMapping(value = ServiceApi.UserApp.UserAppList, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户id获取App列表")
    public Collection<MUserApp> getAppApiNoPage(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId", required = true) String userId) throws Exception {
        String filters = "userId="+userId;
        List<UserApp> userAppList =  userAppService.search(filters);
        return convertToModels(userAppList,new ArrayList<>(userAppList.size()),MUserApp.class, "");
    }


    @RequestMapping(value = ServiceApi.UserApp.UserAppShow, method = RequestMethod.GET)
    @ApiOperation(value = "更新用户权限应用的云门户展示状态")
    public MUserApp updateUserAppShowFlag(
            @ApiParam(name = "id", value = "用户APP关联ID")
            @RequestParam(value = "id", required = true) String id,
            @ApiParam(name = "flag", value = "要更新的展示状态", defaultValue = "1")
            @RequestParam(value = "flag", required = true) String flag) throws Exception {

            List<UserApp> userAppList = userAppService.findByField("id", id);
            UserApp userApp = new UserApp();
            if(userAppList != null){
                userApp = userAppList.get(0);
                userApp.setShowFlag(Integer.parseInt(flag));
                MUserApp mUserApp = convertToModel(userAppService.save(userApp),MUserApp.class);
                return mUserApp;
            }else{
                return null;
        }
    }

    @RequestMapping(value = ServiceApi.UserApp.CreateUserApp, method = RequestMethod.GET)
    @ApiOperation(value = "创建用户与app关联")
    public MUserApp createUserApp(
            @ApiParam(name = "userAppJson", value = "用户APP对象json")
            @RequestParam(value = "userAppJson", required = true) String userAppJson) throws Exception {
            UserApp userApp =objectMapper.readValue(userAppJson, UserApp.class);
            userApp.setShowFlag(1);
            userApp.setOrgId("");
            userApp.setOrgName("");
            MUserApp mUserApp = convertToModel(userAppService.save(userApp),MUserApp.class);
            return mUserApp;
    }
}
