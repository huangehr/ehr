package com.yihu.ehr.security.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.User)
public interface UserClient {

    @RequestMapping(value = "/rest/{api_version}/user", method = GET )
    MUser getUser(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "user_id") String userId);

    @RequestMapping(value = "/rest/{api_version}/{login_code}", method = GET )
    MUser getUserByLoginCode(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "login_code") String loginCode);


    @RequestMapping(value = "/rest/{api_version}/login_indetify/{login_code}/{psw}", method = GET )
    MUser loginIndetification(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "login_code") String loginCode,
            @PathVariable(value = "psw") String psw);

}
