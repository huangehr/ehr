package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MUserSecurity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(MicroServices.Security)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface SecurityClient {

    @RequestMapping(value = "/securities/login/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥" , notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    MUserSecurity getUserSecurityByLoginCode(
            @ApiParam(name = "login_code", value = "用户名")
            @PathVariable(value = "login_code") String loginCode) ;

}
