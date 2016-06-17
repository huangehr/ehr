package com.yihu.ehr.profile.feign;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by Administrator on 2016/6/14.
 */

@ApiIgnore
@FeignClient(value ="svr-prescription")
public interface XThridPrescriptionClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/prescriptioToImage/htmlToImage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String prescriptioToImage(
            @ApiParam(value = "eventNo" ) @RequestParam String eventNo,
            @ApiParam(value = "orgId") @RequestParam String orgId);
}
