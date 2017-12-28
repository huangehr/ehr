package com.yihu.ehr.government.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.government.service.OutpatientsBoardClient;
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
 * Created by zdm on 2017/12/28.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "outpatientsBoard", description = "政府服务平台-门诊仪表盘", tags = {"政府服务平台-门诊仪表盘"})
public class OutpatientsBoardController extends BaseController {

        @Autowired
        private OutpatientsBoardClient outpatientsBoardClient;

        @RequestMapping(value = ServiceApi.Government.OutpatientsBoardCount, method = RequestMethod.POST)
        @ApiOperation(value = "统计门诊 当月相关数据")
        public Envelop outpatientsBoardCount(
                @ApiParam(name = "core", value = "集合", required = true)
                @RequestParam(value = "core") String core,
                @ApiParam(name = "position", value = "指标位置", required = true)
                @RequestParam(value = "position") String position) {
            Envelop envelop = new Envelop();
            envelop=outpatientsBoardClient.outpatientsBoardCount(core,position);
            return envelop;
        }


    @ApiOperation("医院门急诊人次分布")
    @RequestMapping(value = ServiceApi.Government.GetMonthDistribution, method = RequestMethod.POST)
    public Envelop getMonthDistribution(
            @ApiParam(name = "core", value = "集合", required = true)
            @RequestParam(value = "core") String core,
            @ApiParam(name = "year", value = "年份", required = true)
            @RequestParam(value = "year") String year) {
        Envelop envelop = new Envelop();
        envelop=outpatientsBoardClient.getMonthDistribution(core,year);
        return envelop;
    }

    @ApiOperation("本月各类医院门急诊人次")
    @RequestMapping(value = ServiceApi.Government.GetRescue, method = RequestMethod.POST)
    public Envelop getRescue() {
        Envelop envelop =outpatientsBoardClient.getRescue();
        return envelop;
    }


}
