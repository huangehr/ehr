package com.yihu.ehr.emergency.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 定位数据表
 * Created by progr1mmer on 2017/11/8.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface PositionClient {

    @RequestMapping(value = "/getRange", method = RequestMethod.GET)
    @ApiOperation(value = "获取一定范围内的所有车辆的位置，返回车辆信息，由前端处理数据")
    Envelop getRange(
            @RequestParam(value = "longitude") double longitude,
            @RequestParam(value = "latitude") double latitude);

}
