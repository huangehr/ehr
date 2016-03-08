package com.yihu.ehr.adaption.feignclient;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by AndyCai on 2016/3/3.
 */
@FeignClient(MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0 + "/standard-dispatcher")
@ApiIgnore
public interface StandardDispatchClient {

    @RequestMapping(value = "/schema", method = RequestMethod.POST)
    @ApiOperation(value = "生成适配方案摘要", produces = "application/json",
            notes = "")
    public Map createSchemeInfo(
            @ApiParam(required = true, name = "version", value = "要生成的目标版本")
            @RequestParam(value = "version", required = true) String version) throws Exception;
}
