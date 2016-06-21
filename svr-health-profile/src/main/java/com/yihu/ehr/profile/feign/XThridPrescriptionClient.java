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

    @RequestMapping(value = ApiVersion.Version1_0+"/prescriptioToImage/htmlToImage", method = RequestMethod.GET)
    String prescriptioToImage(
            @ApiParam(value = "eventNo" ) @RequestParam String eventNo,
            @ApiParam(value = "处方ID" ) @RequestParam String prescriptoId,
            @ApiParam(value = "orgId") @RequestParam(required=false) String orgId,
            @ApiParam(value = "cdaType") @RequestParam(required=false) String cdaType,
            @ApiParam(value = "version") @RequestParam(required=false) String version,
            @ApiParam(value = "height(默认600)") @RequestParam(required=false,defaultValue = "600") int height,
            @ApiParam(value = "width(默认400)") @RequestParam(required=false,defaultValue = "400") int width);
}
