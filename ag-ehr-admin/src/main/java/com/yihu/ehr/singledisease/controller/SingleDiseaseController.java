package com.yihu.ehr.singledisease.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wxw on 2018/2/27.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "SingleDiseaseController", description = "单病种", tags = {"单病种-数据分析"})
public class SingleDiseaseController extends EnvelopRestEndPoint {


    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    public Envelop heatMap() {
        Envelop envelop = new Envelop();

        return envelop;
    }
}
