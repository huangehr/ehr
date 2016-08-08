package com.yihu.ehr.medicalRecords.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalReportDTO;
import com.yihu.ehr.medicalRecords.service.MedicalReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by hzp on 2016/8/1.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalReports", description = "病历报告服务接口")
public class MedicalReportsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    MedicalReportService reportService;

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.GET)
    @ApiOperation("获取病历报告")
    public MedicalReportDTO getMedicalReport(
            @ApiParam(name="record_id",value="病历id")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="report_id",value="报告id")
            @PathVariable(value="report_id") String reportId) throws Exception
    {
        return reportService.getMedicalReport(recordId,reportId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReports,method = RequestMethod.GET)
    @ApiOperation("获取某病历下的所有报告")
    public List<MedicalReportDTO> getMedicalReports(
            @ApiParam(name="record_id",value="病历id")
            @PathVariable(value="record_id") String recordId) throws Exception
    {
        return reportService.getMedicalReports(recordId);
    }

    /**
     * 保存病历报告
     *
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportManage,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存病历报告")
    public boolean saveMedicalReport(
            @ApiParam(name="record_id",value="病历id")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="json",value="数据元JSON")
            @RequestBody String json) throws Exception
    {
        MedicalReportDTO obj = toEntity(json,MedicalReportDTO.class);
        return reportService.saveMedicalReport(recordId,obj);
    }


    /**
     * 根据报告id删除病历报告
     *
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportManage,method = RequestMethod.DELETE)
    @ApiOperation("根据报告id删除病历报告")
    public boolean deleteReportById(
            @ApiParam(name="record_id",value="病历id")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="report_id",value="报告id")
            @RequestParam(value="report_id",required = true) String reportId) throws Exception
    {
        return reportService.deleteReportById(recordId,reportId);
    }

    
}