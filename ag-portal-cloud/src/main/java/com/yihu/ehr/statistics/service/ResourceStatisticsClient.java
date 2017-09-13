package com.yihu.ehr.statistics.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.tj.EchartReportModel;
import com.yihu.ehr.model.tj.MapDataModel;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zdm on 2017/9/12
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceStatisticsClient {

    @RequestMapping(value = "/getStatisticsUserCards", method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    List<EchartReportModel> getStatisticsUserCards();

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveIdentifyReportInfo, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--档案识别")
    Envelop getArchiveIdentifyReportInfo();

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveHospitalReportInfo, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--住院/门诊")
    Envelop getArchiveHospitalReportInfo();

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveStatisticalReportInfo, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--数据统计")
    Envelop getArchiveStatisticalReportInfo();

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveTotalVisitReportInfo, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--累计就诊人次")
    Envelop getArchiveTotalVisitReportInfo();

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsElectronicMedicalCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历-最近七天采集总数统计，门诊住院数")
    List<EchartReportModel> getStatisticsElectronicMedicalCount();

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsMedicalEventTypeCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历 - 今天 门诊住院数统计")
    List<EchartReportModel> getStatisticsElectronicMedicalEventTypeCount();

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsDemographicsAgeCount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 年龄段人数统计")
    List<EchartReportModel> getStatisticsDemographicsAgeCount();
}
