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
@RequestMapping(ApiVersion.Version1_0 +"/admin")
@RestController
@Api(value = "ResourceStatistics", description = "数据中心首页统计相关", tags = {"数据中心首页-统计相关"})
public class ResourceStatisticsController extends BaseController {
    @Autowired
    private ResourceStatisticsClient resourceStatisticsClient;

    @RequestMapping(value =ServiceApi.StasticReport.getStatisticsUserCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    public Envelop getStatisticsUserCards() {
        try {
            Envelop envelop = new Envelop();
            envelop=resourceStatisticsClient.getStatisticsUserCards();
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
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
            Envelop  envelop = resourceStatisticsClient.getStatisticsElectronicMedicalEventTypeCount();
            return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsDemographicsAgeCount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 年龄段人数统计 -柱状")
    public Envelop getStatisticsDemographicsAgeCount() {
            Envelop envelop = resourceStatisticsClient.getStatisticsDemographicsAgeCount();
            return envelop;
    }


    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "按机构医生、护士、床位的统计")
    public Envelop getStatisticsDoctorByRoleType() {
        try {
            Envelop envelop =null;
            envelop=resourceStatisticsClient.getStatisticsDoctorByRoleType();
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsCityDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "全市医生、护士、床位的统计")
    public Envelop getStatisticsCityDoctorByRoleType() {
        try {
            Envelop envelop =null;
            envelop=resourceStatisticsClient.getStatisticsCityDoctorByRoleType();
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveReportAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取一段时间内数据解析情况")
    public Envelop getArchiveReportAll(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate) {
        Envelop envelop = resourceStatisticsClient.getArchiveReportAll(startDate,endDate);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetRecieveOrgCount, method = RequestMethod.GET)
    @ApiOperation(value = "根据接收日期统计各个医院的数据解析情况")
    public Envelop getRecieveOrgCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date) {
        Envelop envelop = resourceStatisticsClient.getRecieveOrgCount(date);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesInc, method = RequestMethod.GET)
    @ApiOperation(value = "获取某天数据新增情况")
    public Envelop getArchivesInc(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        Envelop envelop = resourceStatisticsClient.getArchivesInc(date,orgCode);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesFull, method = RequestMethod.GET)
    @ApiOperation(value = "完整性分析")
    public Envelop getArchivesFull(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        Envelop envelop = resourceStatisticsClient.getArchivesFull(startDate,endDate,orgCode);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesTime, method = RequestMethod.GET)
    @ApiOperation(value = "及时性分析")
    public Envelop getArchivesTime(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        return resourceStatisticsClient.getArchivesTime(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetDataSetCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集数量")
    public Envelop getDataSetCount(
            @ApiParam(name = "date", value = "日期")
            @RequestParam(name = "date") String date,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        return resourceStatisticsClient.getDataSetCount(date, orgCode);
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchivesRight, method = RequestMethod.GET)
    @ApiOperation(value = "准确性分析")
    public Envelop getArchivesRight(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode",required = false) String orgCode) {
        return resourceStatisticsClient.getArchivesRight(startDate, endDate, orgCode);
    }
}
