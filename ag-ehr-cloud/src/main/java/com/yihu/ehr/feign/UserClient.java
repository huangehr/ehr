package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
}


