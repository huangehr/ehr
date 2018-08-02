package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.feign.HosAdminServiceClient;
import com.yihu.ehr.analyze.model.AdapterDatasetModel;
import com.yihu.ehr.analyze.model.AdapterMetadataModel;
import com.yihu.ehr.analyze.service.dataQuality.DqDatasetWarningService;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.hos.model.standard.MStdMetaData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Author: zhengwei
 * @Date: 2018/5/31 16:20
 * @Description: 质控报表
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PackQcReportEndPoint", description = "档案分析服务", tags = {"档案分析服务-新质控管理报表"})
public class PackQcReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PackQcReportService packQcReportService;
    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private HosAdminServiceClient hosAdminServiceClient;
    @Value("${quality.cloud}")
    private String cloud;
    @Autowired
    private RedisClient redisClient;


    @RequestMapping(value = ServiceApi.PackQcReport.dailyReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取医院数据")
    public Envelop dailyReport(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码", required = false)
            @RequestParam(name = "orgCode") String orgCode) throws Exception {
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
            if (!StringUtils.isEmpty(orgCode)&&!cloud.equals(orgCode)) {
                filters += ";orgCode=" + orgCode;
            }
            List<DqDatasetWarning> list = dqDatasetWarningService.search(null, filters, "", page, size);
            int count = (int) dqDatasetWarningService.getCount(filters);
            envelop = getPageResult(list, count, page, size);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.resourceSuccess, method = RequestMethod.GET)
    @ApiOperation(value = "资源化成功的计数统计")
    public Envelop resourceSuccess(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.resourceSuccess(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.resourceSuccessPage, method = RequestMethod.GET)
    @ApiOperation(value = "资源化成功的计数统计(分页)")
    public Envelop resourceSuccessPage(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        return packQcReportService.resourceSuccess(startDate, endDate, orgCode,size,page);
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

    @RequestMapping(value = ServiceApi.PackQcReport.dataSetListPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取接收数据集列表")
    public Envelop dataSetListPage(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        return packQcReportService.getDataSetListPage(startDate, endDate, orgCode,size,page);
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

    @RequestMapping(value = ServiceApi.PackQcReport.archiveFailedPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源化解析失败")
    public Envelop archiveFailedPage(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        return packQcReportService.archiveFailed(startDate, endDate, orgCode,size,page);
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

    @RequestMapping(value = ServiceApi.PackQcReport.metadataErrorPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取解析异常")
    public Envelop metadataErrorPage(
            @ApiParam(name = "step", value = "异常环节")
            @RequestParam(name = "step") String step,
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        return packQcReportService.metadataError(step, startDate, endDate, orgCode,size,page);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.analyzeErrorList, method = RequestMethod.GET)
    @ApiOperation(value = "解析失败问题查询")
    public Envelop analyzeErrorList(
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        if(StringUtils.isNotEmpty(filters)){
            filters="analyze_status=2||archive_status=2;"+filters;
        }else{
            filters="analyze_status=2||archive_status=2";
        }
        List<Map<String, Object>> list = packQcReportService.analyzeErrorList(filters, sorts, page, size);
        int count = (int) elasticSearchUtil.count("json_archives", "info", filters);
        Envelop envelop = getPageResult(list, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.metadataErrorList, method = RequestMethod.GET)
    @ApiOperation(value = "异常数据元列表")
    public Envelop metadataErrorList(
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {

        List<Map<String, Object>> list = packQcReportService.metadataErrorList(filters, sorts, page, size);
        int count = (int) elasticSearchUtil.count("json_archives_qc", "qc_metadata_info", filters);
        Envelop envelop = getPageResult(list, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.metadataErrorDetail, method = RequestMethod.GET)
    @ApiOperation(value = "异常数据元详情")
    public Envelop metadataErrorDetail(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") String id) throws Exception {

        Envelop envelop = packQcReportService.metadataErrorDetail(id);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.archiveList, method = RequestMethod.GET)
    @ApiOperation(value = "档案包列表")
    public Envelop archiveList(
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {

//        if(StringUtils.isNotEmpty(filters)){
//            filters="archive_status=3;"+filters;
//        }else{
//            filters="archive_status=3";
//        }
        List<Map<String, Object>> list = packQcReportService.archiveList(filters, sorts, page, size);
        int count = (int) elasticSearchUtil.count("json_archives", "info", filters);
        Envelop envelop = getPageResult(list, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.archiveDetail, method = RequestMethod.GET)
    @ApiOperation(value = "档案详情")
    public Envelop archiveDetail(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") String id) throws Exception {

        Envelop envelop = packQcReportService.archiveDetail(id);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.uploadRecordList, method = RequestMethod.GET)
    @ApiOperation(value = "上传记录列表")
    public Envelop uploadRecordList(
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        List<Map<String, Object>> list = packQcReportService.uploadRecordList(filters, sorts, page, size);
        int count = (int) elasticSearchUtil.count("upload", "record", filters);
        Envelop envelop = getPageResult(list, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.uploadRecordDetail, method = RequestMethod.GET)
    @ApiOperation(value = "上传记录详情")
    public Envelop uploadRecordDetail(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") String id) throws Exception {

        Envelop envelop = packQcReportService.uploadRecordDetail(id);
        return envelop;
    }

    @RequestMapping(value = "/packQcReport/adapterDatasetList", method = RequestMethod.GET)
    @ApiOperation(value = "上传数据集列表")
    public Envelop adapterDatasetList(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String version,
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size){
        if(StringUtils.isNotEmpty(filters)){
            filters="needCrawer=1;"+filters;
        }else{
            filters="needCrawer=1;";
        }
        ResponseEntity<Collection<AdapterDatasetModel>> res = hosAdminServiceClient.adapterDatasetList(version, null, filters , sorts ,page, size);
        List<AdapterDatasetModel> list = (List<AdapterDatasetModel>)res.getBody();
        int totalCount = getTotalCount(res);
        return getPageResult(list,totalCount,page,size);
    }

    @RequestMapping(value = "/packQcReport/adapterMetadataList", method = RequestMethod.GET)
    @ApiOperation(value = "上传数据元列表")
    public Envelop adapterMetadataList(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String version,
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size){
        if(StringUtils.isNotEmpty(filters)){
            filters="needCrawer=1;"+filters;
        }else{
            filters="needCrawer=1;";
        }
        ResponseEntity<Collection<AdapterMetadataModel>> res = hosAdminServiceClient.adapterMetadataList(version, null, filters , sorts ,page, size);
        List<AdapterMetadataModel> list = (List<AdapterMetadataModel>)res.getBody();
        int totalCount = getTotalCount(res);
        return getPageResult(list,totalCount,page,size);
    }

    @RequestMapping(value = "/packQcReport/datasetDetail", method = RequestMethod.GET)
    @ApiOperation(value = "抽取数据集")
    public Envelop datasetDetail(
            @ApiParam(name = "date", value = "日期", required = true)
            @RequestParam(value = "date") String date) throws Exception {
        Envelop envelop = packQcReportService.datasetDetail(date);
        return envelop;
    }

    @RequestMapping(value = "/packQcReport/setStartTime", method = RequestMethod.GET)
    @ApiOperation(value = "设置抽取时间")
    public Envelop setStartTime(
            @ApiParam(name = "date", value = "日期", required = true)
            @RequestParam(value = "date") String date) throws Exception {
        Envelop envelop = new Envelop();
        redisClient.set("start_date",date);
        envelop.setSuccessFlg(true);
        return envelop;
    }
}
