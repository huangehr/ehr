package com.yihu.ehr.resource.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.resource.client.RsResourceClient;
import com.yihu.ehr.resource.client.RsResourceQuotaClient;
import com.yihu.ehr.resource.client.RsResourceStatisticsClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/12/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "rs_resources_statistics",  description = "资源报表统计视图", tags = {"资源报表统计视图接口"})
public class RsResourceStatisticsController extends ExtendController {

    @Autowired
    private RsResourceStatisticsClient rsResourceStatisticsClient;
    @Autowired
    private RsResourceClient resourcesClient;
    @Autowired
    private RsResourceQuotaClient resourceQuotaClient;

    @RequestMapping(value = ServiceApi.Resources.StatisticsGetDoctorsGroupByTown, method = RequestMethod.GET)
    @ApiOperation(value = "获取各行政区划总卫生人员")
    public Envelop statisticsGetDoctorsGroupByTown() {
        Envelop envelop = new Envelop();
        try {
            envelop = rsResourceStatisticsClient.statisticsGetDoctorsGroupByTown();
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

    @ApiOperation(value = "获取特殊机构指标执行结果分页")
    @RequestMapping(value = ServiceApi.TJ.TjGetOrgHealthCategoryQuotaResult, method = RequestMethod.GET)
    public Envelop getOrgHealthCategoryQuotaResult(
            @ApiParam(name = "code", value = "指标任务code", required = true)
            @RequestParam(value = "code" , required = true) String code,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "需要统计不同维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension ) {
        try {
            return rsResourceStatisticsClient.getOrgHealthCategoryQuotaResult(code,filters,dimension);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "获取视图 统计报表 二维表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReportTwoDimensionalTable, method = RequestMethod.GET)
    public Envelop getQuotaReportTwoDimensionalTable(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "filters", value = "检索条件 多个条件用 and 拼接 如：town=361002 and org=10000001 ", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        Envelop resourceResult =  resourcesClient.getResourceById(resourceId);
        String quotaCodeStr = "";
        if(!resourceResult.isSuccessFlg()){
            envelop.setErrorMsg("视图不存在，请确认！");
            return envelop;
        }else {
            List<ResourceQuotaModel> list = resourceQuotaClient.getByResourceId(resourceId);
            if (list != null && list.size() > 0) {
                for (ResourceQuotaModel resourceQuotaModel : list) {
                    quotaCodeStr = quotaCodeStr + resourceQuotaModel.getQuotaCode() + ",";
                }
            }
        }
        RsResourcesModel rsResourcesModel = objectMapper.convertValue(resourceResult.getObj(), RsResourcesModel.class);
        List<Map<String, Object>> resultList = rsResourceStatisticsClient.getQuotaReportTwoDimensionalTable(quotaCodeStr, filters, StringUtils.isEmpty(dimension) ? rsResourcesModel.getDimension() : dimension, top);
        envelop.setObj(resultList);
        envelop.setSuccessFlg(true);
        return  envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案总数")
    public Envelop getArchiveCount() throws Exception {
        Envelop envelop = rsResourceStatisticsClient.getArchiveCount();
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetArchiveManCount, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案的建档人数数量")
    public Envelop getArchiveManCount() throws Exception {
        Envelop envelop = rsResourceStatisticsClient.getArchiveManCount();
        return envelop;
    }

    @ApiOperation("根据条件到solr中获取记录数")
    @RequestMapping(value = "/report/searchSolrByParam", method = RequestMethod.POST)
    public Envelop searchSolrByParam(
            @ApiParam(name = "core", value = "solr core名称")
            @RequestParam(value = "core") String core,
            @ApiParam(name = "eventType", value = "solr的查询条件")
            @RequestParam(value = "eventType") String eventType,
            @ApiParam(name = "time", value = "过滤时间")
            @RequestParam(value = "time", required = false) String time,
            @ApiParam(name = "month", value = "获取几个月数据", defaultValue = "0")
            @RequestParam(value = "month", defaultValue = "0", required = false) Integer month) {
        return rsResourceStatisticsClient.searchSolrByParam(core, eventType, time, month);
    }
}
