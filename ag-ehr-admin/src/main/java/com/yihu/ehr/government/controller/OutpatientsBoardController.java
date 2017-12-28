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
        @ApiOperation(value = "根据统计周期类型、起止时间、指标编码统计结果")
        public Envelop outpatientsBoardCount(
                @ApiParam(name = "core", value = "集合", required = true)
                @RequestParam(value = "core") String core,
                @ApiParam(name = "filter", value = "查询条件", required = true)
                @RequestParam(value = "filter") String filter) {
            Envelop envelop = new Envelop();
            envelop=outpatientsBoardClient.outpatientsBoardCount(core,filter);
            return envelop;
        }


}
