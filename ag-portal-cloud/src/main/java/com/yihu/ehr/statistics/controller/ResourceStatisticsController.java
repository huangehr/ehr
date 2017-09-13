package com.yihu.ehr.statistics.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.statistics.service.ResourceStatisticsClient;
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
    private ResourceStatisticsClient resourceStatisticsClient;

    @RequestMapping(value = "/getStatisticsUserCards", method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    public Envelop getStatisticsUserCards() {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(resourceStatisticsClient.getStatisticsUserCards());
//            envelop.setObj();
            envelop.setSuccessFlg(true);
            return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveReportInfo, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--图表")
    public Envelop getArchiveReportInfo(
            @ApiParam(name = "requestType", value = "类别")
            @RequestParam(name = "requestType") String requestType) {
            Envelop envelop = new Envelop();
            if ("archiveIdentify".equalsIgnoreCase(requestType)) {
                envelop = resourceStatisticsClient.getArchiveIdentifyReportInfo();
            } else if ("archiveHospital".equalsIgnoreCase(requestType)) {
                envelop = resourceStatisticsClient.getArchiveHospitalReportInfo();
            } else if ("archiveStatistical".equalsIgnoreCase(requestType)) {
                envelop = resourceStatisticsClient.getArchiveStatisticalReportInfo();
            } else if ("archiveTotalVisit".equalsIgnoreCase(requestType)) {
                envelop = resourceStatisticsClient.getArchiveTotalVisitReportInfo();
            }

            return envelop;
    }


    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsElectronicMedicalCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历-最近七天采集总数统计，门诊住院数 - 柱状")
    public Envelop getStatisticsElectronicMedicalCount() {
            Envelop envelop = resourceStatisticsClient.getStatisticsElectronicMedicalCount();
            return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsMedicalEventTypeCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历 - 今天 门诊住院数统计 - 饼状")
    public Envelop getStatisticsElectronicMedicalEventTypeCount() {
            Envelop  envelop = resourceStatisticsClient.getStatisticsElectronicMedicalCount();
            return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsDemographicsAgeCount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 年龄段人数统计 -柱状")
    public Envelop getStatisticsDemographicsAgeCount() {
            Envelop envelop = resourceStatisticsClient.getStatisticsElectronicMedicalCount();
            return envelop;
    }


}
