package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.client.ResourceStatisticsClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller - 数据资源中心
 * Created by Progr1mmer on 2018/01/05.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ResourceStatisticsController", description = "数据资源中心首页", tags = {"资源服务-数据资源中心首页"})
public class ResourceStatisticsController extends EnvelopRestEndPoint {

    @Autowired
    private ResourceStatisticsClient resourceStatisticsClient;

    @RequestMapping(value = ServiceApi.DataCenter.GetPatientArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 居民建档数")
    public Envelop getPatientArchiveCount(){
        return resourceStatisticsClient.getPatientArchiveCount();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetMedicalResourcesCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 医疗资源建档数")
    public Envelop getMedicalResourcesCount() {
        return resourceStatisticsClient.getMedicalResourcesCount();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetHealthArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 健康档案建档数")
    public Envelop getHealthArchiveCount() {
        return resourceStatisticsClient.getHealthArchiveCount();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetHealthArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 电子病例建档数")
    public Envelop getElectronicCasesCount(){
        return resourceStatisticsClient.getElectronicCasesCount();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetHealthCardBindingAmount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 健康卡绑定量")
    public Envelop getHealthCardBindingAmount() {
        return resourceStatisticsClient.getHealthCardBindingAmount();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetInfoDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 信息分布")
    public Envelop getInfoDistribution() {
        return resourceStatisticsClient.getInfoDistribution();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetNewSituation, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 新增情况")
    public Envelop getNewSituation() {
        return resourceStatisticsClient.getNewSituation();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetOrgArchives, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医疗机构建档分布")
    public Envelop getOrgArchives() {
        return resourceStatisticsClient.getOrgArchives();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetMedicalStaffDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医疗人员分布")
    public Envelop getMedicalStaffDistribution() {
        return resourceStatisticsClient.getMedicalStaffDistribution();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetMedicalStaffRatio, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医护人员比例")
    public Envelop getMedicalStaffRatio() {
        return resourceStatisticsClient.getMedicalStaffRatio();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetCumulativeIntegration, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 累计整合档案数")
    public Envelop getCumulativeIntegration() {
        return resourceStatisticsClient.getCumulativeIntegration();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GteTotallyToBeIntegrated, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 累计待整合档案数")
    public Envelop gteTotallyToBeIntegrated() {
        return resourceStatisticsClient.gteTotallyToBeIntegrated();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetArchiveSource, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 档案来源分布情况")
    public Envelop getArchiveSource() {
       return resourceStatisticsClient.getArchiveSource();
    }


    @RequestMapping(value = ServiceApi.DataCenter.GetArchiveDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 健康档案分布情况")
    public Envelop getArchiveDistribution() {
        return resourceStatisticsClient.getArchiveDistribution();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetStorageAnalysis, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 健康档案入库情况分析")
    public Envelop getStorageAnalysis() {
        return resourceStatisticsClient.getStorageAnalysis();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalSource, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病例来源分布情况")
    public Envelop getElectronicMedicalSource() {
        return resourceStatisticsClient.getElectronicMedicalSource();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalOrgDistributed, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集医院分布")
    public Envelop getElectronicMedicalOrgDistributed() throws Exception {
        return resourceStatisticsClient.getElectronicMedicalOrgDistributed();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalDeptDistributed, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集科室分布")
    public Envelop getElectronicMedicalDeptDistributed() {
       return resourceStatisticsClient.getElectronicMedicalDeptDistributed();
    }

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalAcquisitionSituation, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集采集情况")
    public Envelop getElectronicMedicalAcquisitionSituation() throws Exception {
        return resourceStatisticsClient.getElectronicMedicalAcquisitionSituation();
    }

}
