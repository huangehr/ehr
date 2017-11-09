package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.emergency.client.PositionClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller - 定位数据
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "PositionController", description = "定位数据", tags = {"应急指挥"})
public class PositionController extends BaseController {

    @Autowired
    private PositionClient positionClient;

    @RequestMapping(value = "/getRange", method = RequestMethod.GET)
    @ApiOperation(value = "获取一定范围内的所有车辆的位置，返回车辆信息，由前端处理数据")
    public Envelop getRange(
            @ApiParam(name = "longitude", value = "经度")
            @RequestParam(value = "longitude") double longitude,
            @ApiParam(name = "latitude", value = "纬度")
            @RequestParam(value = "latitude") double latitude) {
        return positionClient.getRange(longitude, latitude);
    }

}
