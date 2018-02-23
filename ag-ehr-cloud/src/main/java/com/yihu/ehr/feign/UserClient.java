package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 15:01
 */
@ApiIgnore
@FeignClient(name = MicroServices.User)
public interface UserClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Users.Users, method = GET)
    List<MUser> getUsers(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Users.User, method = GET)
    MUser getUserByUserName(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Users.UserVerification, method = GET)
    MUser getUserByNameAndPassword(@RequestParam(value = "user_name") String userName,
                                   @RequestParam(value = "password") String password);

    @RequestMapping(value = ApiVersion.Version1_0+  ServiceApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    boolean isIdCardExists(@RequestParam(value = "id_card_no") String idCardNo);

    @RequestMapping(value = ApiVersion.Version1_0+  ServiceApi.Users.UserEmailNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户邮件是否存在")
    boolean isEmailExists(@RequestParam(value = "email") String email);

    @RequestMapping(value = ApiVersion.Version1_0+  ServiceApi.Users.UserTelephoneNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断电话号码是否存在")
    boolean isTelephoneExists(@RequestParam(value = "telephone") String telephone);

    @RequestMapping(value =ApiVersion.Version1_0+ ServiceApi.Users.UserExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断账户是否存在")
    boolean isUserNameExists(@PathVariable(value = "user_name") String userName);

    @RequestMapping(value =ApiVersion.Version1_0+ ServiceApi.Users.Users, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    MUser createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestBody String userJsonData);

    @RequestMapping(value =ApiVersion.Version1_0+  ServiceApi.Users.Users, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    MUser updateUser(@RequestBody String userJsonData);

    @RequestMapping(value =ApiVersion.Version1_0+  ServiceApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据用户id删除用户")
    boolean deleteUser(@PathVariable(value = "user_id") String userId);

    @RequestMapping(value = ApiVersion.Version1_0+  ServiceApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "包括地址信息等")
    MUser getUser(@PathVariable(value = "user_id") String userId);

}


