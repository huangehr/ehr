package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.report.EventDetailModel;
import com.yihu.ehr.agModel.report.EventsModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.report.service.QcDailyReportClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcDailyReport", description = "质控包数据完整性日报", tags = {"报表管理-质控包数据完整性日报"})
public class QcDailyReportController extends ExtendController<QcDailyReport> {

    @Autowired
    QcDailyReportClient qcDailyReportClient;


    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportList, method = RequestMethod.GET)
    @ApiOperation(value = "质控包数据完整性日报列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ResponseEntity<List<MQcDailyReport>> responseEntity = qcDailyReportClient.search(fields, filters, sorts, size, page);
        return getResult(responseEntity.getBody(), getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.POST)
    @ApiOperation(value = "新增质控包数据完整性日报")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            return success(qcDailyReportClient.add(model) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.PUT)
    @ApiOperation(value = "修改质控包数据完整性日报")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            MQcDailyReport qcDailyReportDatasets = convertToMModel(model, MQcDailyReport.class);

            if( StringUtils.isNotEmpty(qcDailyReportDatasets.getId()) )
                return failed("编号不能为空");
            return success(qcDailyReportClient.update(qcDailyReportDatasets) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除质控包数据完整性日报")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            qcDailyReportClient.delete(id);
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取质控包数据完整性日报信息")
    public Envelop getInfo(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            MQcDailyReport qcDailyReportDatasets = qcDailyReportClient.getInfo(id);
            if(qcDailyReportDatasets == null)
                return failed("没有找到该质控包数据完整性日报信息！");
            return success(qcDailyReportDatasets);
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }



    @RequestMapping(value = ServiceApi.Report.GetEventDataReport, method = RequestMethod.POST)
    @ApiOperation(value = "日报数据采集上传")
    public Envelop  getEventDataReport(
            @ApiParam(name = "eventsData", value = "采集json数据", defaultValue = "")
            @RequestParam("eventsData") String collectionData) {
        try {

            EventsModel eventsModel = objectMapper.readValue(collectionData, EventsModel.class);

            MQcDailyReport qcDailyReport = new MQcDailyReport();
            List<MQcDailyReportDetail> list = new ArrayList<MQcDailyReportDetail>();
            if(eventsModel != null){
                if(StringUtils.isEmpty(eventsModel.getCreate_date())){
                    return failed("采集时间不能为空");
                }
                if(StringUtils.isEmpty(eventsModel.getOrg_code())){
                    return failed("机构编码不能为空");
                }
                Date createDate = DateUtil.parseDate(eventsModel.getCreate_date(), "yyyy-MM-dd hh:mm:ss");
                qcDailyReport.setOrgCode(eventsModel.getOrg_code());
                qcDailyReport.setCreateDate(createDate );
                qcDailyReport.setInnerVersion(eventsModel.getInner_version());
                qcDailyReport.setRealHospitalNum(eventsModel.getReal_hospital_num());
                qcDailyReport.setTotalHospitalNum(eventsModel.getTotal_hospital_num());
                qcDailyReport.setRealOutpatientNum(eventsModel.getReal_outpatient_num());
                qcDailyReport.setTotalOutpatientNum(eventsModel.getTotal_outpatient_num());
                qcDailyReport = qcDailyReportClient.add(objectMapper.writeValueAsString(qcDailyReport));
                if(eventsModel.getTotal_hospital() != null){
                    addList(list,eventsModel.getTotal_hospital(),qcDailyReport.getId(),createDate);
                }
                if(eventsModel.getReal_hospital() != null){
                    addList(list,eventsModel.getReal_hospital(),qcDailyReport.getId(),createDate);
                }
                if(eventsModel.getTotal_outpatient() != null){
                    addList(list,eventsModel.getTotal_outpatient(),qcDailyReport.getId(),createDate);
                }
                if(eventsModel.getReal_outpatient() != null){
                    addList(list,eventsModel.getReal_outpatient(),qcDailyReport.getId(),createDate);
                }

                qcDailyReportClient.addQcDailyReportDetailList(objectMapper.writeValueAsString(list));
            }
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    public List<MQcDailyReportDetail> addList(List<MQcDailyReportDetail> list,List<EventDetailModel> modelList,String qcDailyReportId,Date createTime){
        for(EventDetailModel eventDetailModel : modelList){
            MQcDailyReportDetail qcDailyReportDetail = new MQcDailyReportDetail();
            qcDailyReportDetail.setEventNo(eventDetailModel.getEvent_no());
            qcDailyReportDetail.setEventTime(DateUtil.parseDate(eventDetailModel.getEvent_time(), "yyyy-MM-dd hh:mm:ss"));
            qcDailyReportDetail.setPatientId(eventDetailModel.getPatient_id());
            qcDailyReportDetail.setReportId(qcDailyReportId);
            qcDailyReportDetail.setArchiveType("");
            qcDailyReportDetail.setAcqFlag(1);
            int day = 0;
            if(StringUtils.isNotEmpty(eventDetailModel.getEvent_time())){
                Date eventDate = DateUtil.parseDate(eventDetailModel.getEvent_time(), "yyyy-MM-dd hh:mm:ss");
                long intervalMilli = DateUtil.compareDateTime(createTime ,eventDate);
                day = (int) (intervalMilli / (24 * 60 * 60 * 1000));
            }
            qcDailyReportDetail.setTimelyFlag(day>2?1:0);
            list.add(qcDailyReportDetail);
        }
        return list;
    }

}
