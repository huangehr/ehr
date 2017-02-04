package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.entity.Doctors;
import com.yihu.ehr.user.entity.User;
import com.yihu.ehr.user.feign.ConventionalDictClient;
import com.yihu.ehr.user.feign.SecurityClient;
import com.yihu.ehr.user.service.DoctorService;
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.util.hash.HashUtil;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * 2017-02-04 add  by hzp
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "doctor", description = "医生管理接口")
public class DoctorEndPoint extends EnvelopRestEndPoint {

    @Autowired
    DoctorService doctorService;

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生列表", notes = "根据查询条件获取医生列表在前端表格展示")
    public List<MUser> searchUsers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        /*List<User> userList = userManager.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, userManager.getCount(filters), page, size);

        return (List<MUser>) convertToModels(userList, new ArrayList<MUser>(userList.size()), MUser.class, fields);*/

        return null;
    }

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    public Boolean createUser(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData) throws Exception {
        doctorService.saveDoctor(doctoJsonData);
        return true;
    }

    /*@RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public MUser updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestBody String userJsonData) throws Exception {
        User user = toEntity(userJsonData, User.class);
        String userType = user.getUserType();
        MConventionalDict dict = conventionalDictClient.getUserType(userType);
        if(dict!=null){
            user.setDType(userType);
        }
        userManager.saveUser(user);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取用户信息")
    public MUser getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        User user = userManager.getUser(userId);
        MUser userModel = convertToModel(user, MUser.class);
        return userModel;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据id删除用户")
    public boolean deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userManager.deleteUser(userId);
        return true;
    }*/


}