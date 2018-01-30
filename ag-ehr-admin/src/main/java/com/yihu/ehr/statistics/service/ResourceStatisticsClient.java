package com.yihu.ehr.statistics.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by zdm on 2017/9/12
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceStatisticsClient {

    @RequestMapping(value =ServiceApi.StasticReport.getStatisticsUserCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    Envelop getStatisticsUserCards();

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
    Envelop getStatisticsElectronicMedicalCount();

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsMedicalEventTypeCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历 - 今天 门诊住院数统计")
    Envelop getStatisticsElectronicMedicalEventTypeCount();

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsDemographicsAgeCount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 年龄段人数统计")
    Envelop getStatisticsDemographicsAgeCount();

    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "按机构医生、护士、床位的统计")
    Envelop getStatisticsDoctorByRoleType();

    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsCityDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "全市医生、护士、床位的统计")
    Envelop getStatisticsCityDoctorByRoleType();

    @RequestMapping(value =  ServiceApi.StasticReport.GetArchiveReportAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取一段时间内健康档案数据")
    Envelop getArchiveReportAll(@ApiParam(name = "startDate", value = "开始日期")
                                @RequestParam(name = "startDate") String startDate,
                                @ApiParam(name = "endDate", value = "结束日期")
                                @RequestParam(name = "endDate") String endDate);

    @RequestMapping(value =  ServiceApi.StasticReport.GetRecieveOrgCount, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    Envelop getRecieveOrgCount(@ApiParam(name = "date", value = "日期")
                                @RequestParam(name = "date") String date);

    @RequestMapping(value =  ServiceApi.StasticReport.GetArchivesInc, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    Envelop getArchivesInc(@ApiParam(name = "date", value = "日期")
                                @RequestParam(name = "date") String date,
                           @ApiParam(name = "orgCode", value = "医院代码")
                           @RequestParam(name = "orgCode",required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesFull, method = RequestMethod.GET)
    @ApiOperation(value = "完整性分析")
    Envelop getArchivesFull(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesTime, method = RequestMethod.GET)
    @ApiOperation(value = "及时性分析")
    Envelop getArchivesTime(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode);

    @RequestMapping(value = ServiceApi.StasticReport.GetDataSetCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集数量")
    Envelop getDataSetCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode);
}
