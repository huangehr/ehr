package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import com.yihu.ehr.medicalRecord.service.MedicalReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalReports", description = "病历报告服务接口")
public class MedicalReportsController extends EnvelopRestEndPoint {

    @Autowired
    MedicalReportService mRService;

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    public MrMedicalRecordsEntity createMetadata(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalReport) throws Exception
    {
        MrMedicalReportEntity mrMedicalReport = toEntity(medicalReport,MrMedicalReportEntity.class);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        mrMedicalReport.setReportDatetime(t);

        mrMedicalReport = mRService.saveMedicalRecord(mrMedicalReport);
        return convertToModel(mrMedicalReport,MrMedicalRecordsEntity.class);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新数据元")
    public MrMedicalRecordsEntity updateMetadata(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalReport) throws Exception
    {
        MrMedicalReportEntity mrMedicalReport = toEntity(medicalReport,MrMedicalReportEntity.class);

        mrMedicalReport = mRService.saveMedicalRecord(mrMedicalReport);
        return convertToModel(mrMedicalReport,MrMedicalRecordsEntity.class);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.GET)
    @ApiOperation("根据病历ID获取病历报告")
    public List<MrMedicalReportEntity> MedicalLabelByRecordId(
            @ApiParam(name="id",value="id",defaultValue = "")
            @RequestParam(value="id") int id) throws Exception
    {
        return mRService.getMedicalReportInfoByRecordId(id);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteMetadata(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @RequestParam(value="id")int id) throws Exception
    {
        mRService.deleteRecord(id);
        return true;
    }
}