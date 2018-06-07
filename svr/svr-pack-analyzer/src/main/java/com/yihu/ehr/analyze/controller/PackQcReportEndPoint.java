package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.dataQuality.DqDatasetWarningService;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhengwei
 * @Date: 2018/5/31 16:20
 * @Description: 质控报表
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PackQcReportEndPoint", description = "新质控管理报表", tags = {"新质控管理报表"})
public class PackQcReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PackQcReportService packQcReportService;
    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private ElasticSearchUtil esUtil;

    @RequestMapping(value = ServiceApi.PackQcReport.dailyReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取医院数据")
    public Envelop dailyReport(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.dailyReport(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.datasetWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "预警数据集列表")
    public Envelop datasetWarningList(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(name = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类型(1平台接收，2平台上传)")
            @RequestParam(name = "type") String type,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        try {
            String filters = "type=" + type;
            if (!StringUtils.isEmpty(orgCode)) {
                filters += ";orgCode=" + orgCode;
            }
            String fields = "datasetCode,datasetName";
            List<DqDatasetWarning> redisMqChannelList = dqDatasetWarningService.search(fields, filters, "", page, size);
            int count = (int) dqDatasetWarningService.getCount(filters);
            List<DqDatasetWarning> list = (List<DqDatasetWarning>) convertToModels(redisMqChannelList, new ArrayList<DqDatasetWarning>(), DqDatasetWarning.class, fields);
            envelop = getPageResult(list, count, page, size);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.resourceSuccessfulCount, method = RequestMethod.GET)
    @ApiOperation(value = "资源化成功的计数统计")
    public Envelop resourceSuccessfulCount(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception{
        return packQcReportService.resourceSuccessfulCount(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.archiveReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取接收档案数据")
    public Envelop archiveReport(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.archiveReport(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.dataSetList, method = RequestMethod.GET)
    @ApiOperation(value = "获取接收数据集列表")
    public Envelop dataSetList(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.dataSetList(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.archiveFailed, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源化解析失败")
    public Envelop archiveFailed(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.archiveFailed(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.metadataError, method = RequestMethod.GET)
    @ApiOperation(value = "获取解析异常")
    public Envelop metadataError(
            @ApiParam(name = "step", value = "异常环节")
            @RequestParam(name = "step") String step,
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.metadataError(step, startDate, endDate, orgCode);
    }
}
