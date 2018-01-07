package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Client - 数据资源中心
 * Created by Progr1mmer on 2018/01/05.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
public interface ResourceCenterStatisticsClient {

    @RequestMapping(value = ServiceApi.DataCenter.GetPatientArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 居民建档数")
    Envelop getPatientArchiveCount();

    @RequestMapping(value = ServiceApi.DataCenter.GetMedicalResourcesCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 医疗资源建档数")
    Envelop getMedicalResourcesCount();

    @RequestMapping(value = ServiceApi.DataCenter.GetHealthArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 健康档案建档数")
    Envelop getHealthArchiveCount();

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicCasesCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 电子病例建档数")
    Envelop getElectronicCasesCount();

    @RequestMapping(value = ServiceApi.DataCenter.GetHealthCardBindingAmount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 健康卡绑定量")
    Envelop getHealthCardBindingAmount();

    @RequestMapping(value = ServiceApi.DataCenter.GetInfoDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 信息分布")
    Envelop getInfoDistribution();

    @RequestMapping(value = ServiceApi.DataCenter.GetNewSituation, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 新增情况")
    Envelop getNewSituation();

    @RequestMapping(value = ServiceApi.DataCenter.GetOrgArchives, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医疗机构建档分布")
    Envelop getOrgArchives();

    @RequestMapping(value = ServiceApi.DataCenter.GetMedicalStaffDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医疗人员分布")
    Envelop getMedicalStaffDistribution();

    @RequestMapping(value = ServiceApi.DataCenter.GetMedicalStaffRatio, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医护人员比例")
    Envelop getMedicalStaffRatio();

    @RequestMapping(value = ServiceApi.DataCenter.GetCumulativeIntegration, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 累计整合档案数")
    Envelop getCumulativeIntegration();

    @RequestMapping(value = ServiceApi.DataCenter.GteTotallyToBeIntegrated, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 累计待整合档案数")
    Envelop gteTotallyToBeIntegrated();

    @RequestMapping(value = ServiceApi.DataCenter.GetArchiveSource, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 档案来源分布情况")
    Envelop getArchiveSource();

    @RequestMapping(value = ServiceApi.DataCenter.GetArchiveDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 健康档案分布情况")
    Envelop getArchiveDistribution();

    @RequestMapping(value = ServiceApi.DataCenter.GetStorageAnalysis, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 健康档案入库情况分析")
    Envelop getStorageAnalysis();

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalSource, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病例来源分布情况")
    Envelop getElectronicMedicalSource();

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalOrgDistributed, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集医院分布")
    Envelop getElectronicMedicalOrgDistributed();

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalDeptDistributed, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集科室分布")
    Envelop getElectronicMedicalDeptDistributed();

    @RequestMapping(value = ServiceApi.DataCenter.GetElectronicMedicalAcquisitionSituation, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集采集情况")
    Envelop getElectronicMedicalAcquisitionSituation();
}
