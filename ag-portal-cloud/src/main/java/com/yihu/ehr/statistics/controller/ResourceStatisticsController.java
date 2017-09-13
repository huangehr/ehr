package com.yihu.ehr.statistics.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.statistics.service.StatisticsClient;
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
 * Created by zdm on 2017/9/12
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "ResourceStatistics", description = "数据中心首页统计相关", tags = {"数据中心首页-统计相关"})
public class ResourceStatisticsController extends BaseController {
    @Autowired
    private StatisticsClient statisticsClient;

    @RequestMapping(value = "/getStatisticsUserCards", method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    public Envelop getStatisticsUserCards() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(statisticsClient.getStatisticsUserCards());
//            envelop.setObj();
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "archive/getArchiveReportInfo", method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--图表")
    public Envelop getArchiveReportInfo(
            @ApiParam(name = "requestType", value = "类别")
            @RequestParam(name = "requestType") String requestType) {
        try {
            Envelop envelop = new Envelop();
            if ("archiveIdentify".equalsIgnoreCase(requestType)) {
                envelop = statisticsClient.getArchiveIdentifyReportInfo();
            } else if ("archiveHospital".equalsIgnoreCase(requestType)) {
                envelop = statisticsClient.getArchiveHospitalReportInfo();
            } else if ("archiveStatistical".equalsIgnoreCase(requestType)) {
                envelop = statisticsClient.getArchiveStatisticalReportInfo();
            } else if ("archiveTotalVisit".equalsIgnoreCase(requestType)) {
                envelop = statisticsClient.getArchiveTotalVisitReportInfo();
            }

            return envelop;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
