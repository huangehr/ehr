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
import com.yihu.ehr.model.common.ListResult;
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

    @RequestMapping(value = ServiceApi.Report.AddQcDailyReportDetailList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据完整性详细数据日报")
    void addQcDailyReportDetailList(@RequestBody String details ) throws IOException {
        List<Map<String, Object>> models = new ArrayList<>();
        models = objectMapper.readValue(details, new TypeReference<List>() {});
        List<QcDailyReportDetail> list =  new ArrayList<>();
        for(int i=0; i < models.size(); i++) {
            QcDailyReportDetail qcDailyReportDetail = new QcDailyReportDetail();
            Map<String, Object> model = models.get(i);
            qcDailyReportDetail.setEventNo(model.get("eventNo").toString());
            if( !StringUtils.isEmpty(model.get("eventTime"))){
                qcDailyReportDetail.setEventTime(DateUtil.parseDate(model.get("eventTime").toString(),"yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            }
            qcDailyReportDetail.setPatientId(model.get("patientId").toString());
            qcDailyReportDetail.setReportId(model.get("reportId").toString());
            if( !StringUtils.isEmpty(model.get("archiveType"))){
                qcDailyReportDetail.setArchiveType(model.get("archiveType").toString());
            }
            if( !StringUtils.isEmpty(model.get("acqFlag").toString())){
                int af = Integer.valueOf(model.get("acqFlag").toString());
                qcDailyReportDetail.setAcqFlag(af);
            }
            if( !StringUtils.isEmpty(model.get("timelyFlag"))){
                int tf = Integer.valueOf(model.get("acqFlag").toString());
                qcDailyReportDetail.setTimelyFlag(tf);
            }
            qcDailyReportDetailService.save(qcDailyReportDetail);
        }
    }


    @RequestMapping(value = ServiceApi.Report.AddQcDailyReportDetail, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据完整性详细数据日报")
    MQcDailyReportDetail addQcDailyReportDetail(@RequestBody QcDailyReportDetail model){
        return getModelDetail(qcDailyReportDetailService.save(model) );
    }

    protected MQcDailyReport getModel(QcDailyReport o){
        return convertToModel(o, MQcDailyReport.class);
    }

    protected MQcDailyReportDetail getModelDetail(QcDailyReportDetail o){
        return convertToModel(o, MQcDailyReportDetail.class);
    }

}
