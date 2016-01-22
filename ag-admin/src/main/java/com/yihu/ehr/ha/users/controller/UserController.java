package com.yihu.ehr.ha.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.users.model.UserModel;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/user")
@RestController
public class UserController extends BaseRestController {
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUserByOrgOrRealName(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                         @PathVariable(value = "api_version") String apiVersion,
                                         @ApiParam(name = "orgCode", value = "机构代码")
                                         @RequestParam(value = "orgCode") String orgCode,
                                         @ApiParam(name = "realName", value = "真实名称")
                                         @RequestParam(value = "realName") String realName,
                                         @ApiParam(name = "searchType", value = "用户类型")
                                         @RequestParam(value = "searchType") String searchType,
                                         @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                         @RequestParam(value = "page") int page,
                                         @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                         @RequestParam(value = "rows") int rows) {

        return null;
    }

    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public String deleteUser(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "userId", value = "用户ID")
                             @RequestParam(value = "userId") String userId) {

        return null;
    }

    @RequestMapping(value = "/userStatus", method = RequestMethod.POST)
    public String changeUserStatus(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "api_version") String apiVersion,
                                   @ApiParam(name = "userId", value = "用户ID")
                                   @RequestParam(value = "userId") String userId,
                                   @ApiParam(name = "status", value = "状态")
                                   @RequestParam("status") boolean status) {

        return null;

    }

    @RequestMapping(value = "/updateUser", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public String updateUser(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "userModelJsonData", value = "用户信息")
                             @RequestParam(value = "userModelJsonData") String userModelJsonData) throws Exception {

        String strUser = URLDecoder.decode(userModelJsonData, "UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        UserModel userModel = objectMapper.readValue(strUser, UserModel.class);
        //TODO:保存用户信息
        return null;
    }

    @RequestMapping(value = "/fileLoad", method = RequestMethod.POST)
    public String upload(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                         @PathVariable(value = "api_version") String apiVersion,
                         @ApiParam(name = "inputStream", value = "文件流")
                         @RequestParam(value = "inputStream") InputStream inputStream,
                         @ApiParam(name = "fileName", value = "文件名称")
                         @RequestParam(value = "fileName") String fileName) throws IOException {
        //TODO:上传头像文件，返回文件路径 path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
        return null;
    }

    @RequestMapping(value = "/downFile",method = RequestMethod.GET)
    public Object downFile(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "api_version") String apiVersion,
                           @ApiParam(name = "groupName", value = "服务器组名")
                           @RequestParam(value = "groupName") String groupName,
                           @ApiParam(name = "filePath", value = "文件路径")
                           @RequestParam(value = "filePath") String filePath) {

        //TODO:获取文件
        return null;
    }

    @RequestMapping(value = "/resetPass", method = RequestMethod.POST)
    public String resetPass(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
                            @ApiParam(name = "userId", value = "用户ID")
                            @RequestParam(value = "userId") String userId) {
        //TODO:重置用户密码
        return null;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Object getUserDetailById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "userId", value = "用户ID")
                                    @RequestParam(value = "userId") String userId) {
        //TODO:获取用户信息，并转换为UserModel输出
        return null;
    }

    @RequestMapping(value = "/distributeKey", method = RequestMethod.GET)
    public Object distributeKey(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "loginCode", value = "登陆名称")
                                @RequestParam(value = "loginCode") String loginCode) {
        //TODO:重置秘钥
        return null;
    }

    @RequestMapping(value = "/isUserExist", method = RequestMethod.GET)
    public Object isUserExist(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "api_version") String apiVersion,
                              @ApiParam(name = "type", value = "验证类别")
                              @RequestParam(value = "type") String type,
                              @ApiParam(name = "value", value = "待验证值")
                              @RequestParam(value = "value") String value) {

        //TODO:重复值校验
        return null;
    }

    @RequestMapping(value = "/saveLastLoginTime",method = RequestMethod.PUT)
    public Object saveLastLoginTime(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "userId",value = "用户ID")
                                    @RequestParam(value = "userId")String userId)    {
        //TODO:修改用户最后登录时间(取系统当前时间)
        return null;
    }

    @RequestMapping(value = "/userLogin",method = RequestMethod.GET)
    public Object userLogin(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
                            @ApiParam(name = "userName",value = "用户名")
                            @RequestParam(value = "userName")String userName,
                            @ApiParam(name = "password",value = "密码")
                            @RequestParam(value = "password")String password){
        //TODO:用户登录
        return null;
    }
}
