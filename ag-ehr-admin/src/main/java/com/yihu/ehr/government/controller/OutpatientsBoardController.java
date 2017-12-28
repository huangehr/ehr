package com.yihu.ehr.government.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.government.service.OutpatientsBoardClient;
import com.yihu.ehr.model.common.ListResult;
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
                @ApiParam(name = "periodType", value = " 周期类型：1-本月  2-本年", defaultValue = "")
                @RequestParam(value = "periodType", required = false) String periodType,
                @ApiParam(name = "startDate", value = " 周期开始时间", defaultValue = "")
                @RequestParam(value = "startDate", required = false) String startDate,
                @ApiParam(name = "endDate", value = " 周期结束时间", defaultValue = "")
                @RequestParam(value = "endDate", required = false) String endDate,
                @ApiParam(name = "quotaCode", value = " 指标编码", defaultValue = "")
                @RequestParam(value = "quotaCode", required = false) String quotaCode) {
            Envelop envelop = new Envelop();

            ListResult listResult=outpatientsBoardClient.outpatientsBoardCount(periodType,startDate,endDate,quotaCode);

            envelop.setSuccessFlg(true);
            return envelop;
        }


}
