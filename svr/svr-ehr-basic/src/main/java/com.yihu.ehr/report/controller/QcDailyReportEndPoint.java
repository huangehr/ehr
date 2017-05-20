package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.patient.MedicalCards;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.entity.report.QcQuotaDict;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.report.service.QcDailyReportDetailService;
import com.yihu.ehr.report.service.QcDailyReportService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "qcDailyReport", description = "档案上传日报", tags = {"档案上传日报"})
public class QcDailyReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    QuotaStatisticsEndPoint quotaStatisticsEndPoint;
    @Autowired
    QcDailyReportService qcDailyReportService;
    @Autowired
    QcDailyReportDetailService qcDailyReportDetailService;

    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询档案上传日报")
    public ListResult getQcDailyReportList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<QcDailyReport> qcDailyReportList = qcDailyReportService.search(fields, filters, sorts, page, size);
        if(qcDailyReportList != null){
            listResult.setDetailModelList(qcDailyReportList);
            listResult.setTotalCount((int)qcDailyReportService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增档案上传日报")
    public MQcDailyReport add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody QcDailyReport model) throws Exception{
        model.setAddDate(new Date());
        return getModel( qcDailyReportService.save(model) );
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改档案上传日报")
    public MQcDailyReport update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody QcDailyReport model) throws Exception{
        return getModel(qcDailyReportService.save(model));
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案上传日报")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        qcDailyReportService.delete(id);
        return true;
    }


    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportDetail, method = RequestMethod.GET)
    @ApiOperation(value = "查询数据完整性详细数据")
    QcDailyReportDetail search( @RequestParam(value = "filters", required = false) String filters) throws ParseException {
        List<QcDailyReportDetail> list = qcDailyReportDetailService.search(filters);
        if(list.isEmpty()){
            return  null;
        }else{
            return list.get(0);
        }
    }

    @RequestMapping(value = ServiceApi.Report.AddQcDailyReportDetailList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据完整性详细数据日报")
    boolean addQcDailyReportDetailList(@RequestBody String details ) throws IOException {
        List<QcDailyReportDetail> list =  getModelList(details);
        for(QcDailyReportDetail qcDailyReportDetail:list){
            qcDailyReportDetailService.save(qcDailyReportDetail);
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Report.AddOrUpdateQcDailyReportDetail, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增修改质控包数据完整性详细数据日报")
    ObjectResult addQcDailyReportDetail(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws IOException {
        QcDailyReportDetail obj = objectMapper.readValue(model,QcDailyReportDetail.class);
        obj = qcDailyReportDetailService.save(obj);
        return Result.success("更新成功！", obj);
    }

    protected List<QcDailyReportDetail> getModelList(String modelStr) throws IOException {
        List<Map<String, Object>> models = new ArrayList<>();
        models = objectMapper.readValue(modelStr, new TypeReference<List>() {});
        List<QcDailyReportDetail> list =  new ArrayList<>();
        for(int i=0; i < models.size(); i++) {
            QcDailyReportDetail qcDailyReportDetail = new QcDailyReportDetail();
            Map<String, Object> model = models.get(i);
            qcDailyReportDetail.setEventNo(model.get("eventNo").toString());
            if(model.get("eventTime") != null){
                qcDailyReportDetail.setEventTime(DateUtil.parseDate(model.get("eventTime").toString(), "yyyy-MM-dd HH:mm:ss"));
            }
            if(model.get("patientId") != null){
                qcDailyReportDetail.setPatientId(model.get("patientId").toString());
            }
            if(model.get("reportId") != null){
                qcDailyReportDetail.setReportId(model.get("reportId").toString());
            }
            if(model.get("archiveType") != null){
                qcDailyReportDetail.setArchiveType(model.get("archiveType").toString());
            }
            if(model.get("acqFlag") != null){
                int af = Integer.valueOf(model.get("acqFlag").toString());
                qcDailyReportDetail.setAcqFlag(af);
            }
            if(model.get("timelyFlag") != null){
                int tf = Integer.valueOf(model.get("timelyFlag").toString());
                qcDailyReportDetail.setTimelyFlag(tf);
            }
            qcDailyReportDetail.setAddDate(new Date());
            list.add(qcDailyReportDetail);
        }
        return  list;
    }

    protected MQcDailyReport getModel(QcDailyReport o){
        return convertToModel(o, MQcDailyReport.class);
    }

    protected MQcDailyReportDetail getModelDetail(QcDailyReportDetail o){
        return convertToModel(o, MQcDailyReportDetail.class);
    }


    @RequestMapping(value = ServiceApi.Report.StatisticQuotaDataReportData, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "统计指标日报数据")
    void statisticQuotaDataReportData(
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId",required = true) String quotaId,
            @ApiParam(name = "orgId", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgId",required = true) String orgId,
            @ApiParam(name = "quotaDate", value = "统计时间", defaultValue = "")
            @RequestParam(value = "quotaDate",required = true) String quotaDate ) throws ParseException {
             quotaStatisticsEndPoint.statisticQuotaData(quotaId,orgId, quotaDate);
    }





}
