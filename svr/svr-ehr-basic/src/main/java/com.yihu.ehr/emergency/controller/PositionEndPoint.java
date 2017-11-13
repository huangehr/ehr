package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.service.AmbulanceService;
import com.yihu.ehr.emergency.service.PositionService;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * End - 定位数据信息表
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PositionEndPoint", description = "定位数据", tags = {"应急指挥-定位数据"})
public class PositionEndPoint extends BaseRestEndPoint {

    @Autowired
    private PositionService positionService;
    @Autowired
    private AmbulanceService ambulanceService;


    @RequestMapping(value = "/getRange", method = RequestMethod.GET)
    @ApiOperation(value = "获取一定范围内的所有车辆的位置，返回车辆信息，由前端处理数据")
    public Envelop getRange(
            @ApiParam(name = "longitude", value = "经度")
            @RequestParam(value = "longitude") double longitude,
            @ApiParam(name = "latitude", value = "纬度")
            @RequestParam(value = "latitude") double latitude) {
        Envelop envelop = new Envelop();
        try {
            List<Object> resultList = new ArrayList<Object>();
            List<Ambulance> ambulanceList = ambulanceService.search(null, "-status");

        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        return envelop;
    }

}
