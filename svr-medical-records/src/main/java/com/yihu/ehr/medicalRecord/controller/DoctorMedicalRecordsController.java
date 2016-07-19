package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import com.yihu.ehr.medicalRecord.service.DoctorDraftService;
import com.yihu.ehr.medicalRecord.service.DoctorMedicalRecordService;
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
@Api(value = "DoctorMedicalRecords", description = "医生病历关联接口")
public class DoctorMedicalRecordsController extends EnvelopRestEndPoint {

    @Autowired
    DoctorMedicalRecordService dMRService;

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecords,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    public MrDoctorMedicalRecordsEntity createMetadata(
            @ApiParam(name="doctorMedicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String doctorMedicalRecords) throws Exception
    {
        MrDoctorMedicalRecordsEntity doctorMedicalRecord = toEntity(doctorMedicalRecords,MrDoctorMedicalRecordsEntity.class);

        doctorMedicalRecord = dMRService.saveDoctorMedicalRecord(doctorMedicalRecord);
        return convertToModel(doctorMedicalRecord,MrDoctorMedicalRecordsEntity.class);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecords,method = RequestMethod.GET)
    @ApiOperation("根据医生ID病历ID获取医生病历关联信息")
    public List<MrDoctorMedicalRecordsEntity> MedicalLabelByRecordId(
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId") int doctorId,
            @ApiParam(name="recordId",value="recordId",defaultValue = "")
            @RequestParam(value="recordId") int recordId) throws Exception
    {
        return dMRService.getInfoByDocIdrecId(doctorId,recordId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DeleteDoctorMedicalRecords,method = RequestMethod.DELETE)
    @ApiOperation("数据元全删除")
    public boolean deleteRecords(
            @ApiParam(name="recordId",value="病历ID",defaultValue = "")
            @PathVariable(value="recordId")int recordId) throws Exception
    {
        dMRService.deleteRecords(recordId);
        return true;
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecords,method = RequestMethod.DELETE)
    @ApiOperation("医生病历取消关联")
    public boolean deleteRecordRelation(
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId") int doctorId,
            @ApiParam(name="recordId",value="recordId",defaultValue = "")
            @RequestParam(value="recordId") int recordId) throws Exception
    {
        dMRService.deleteRecordRelation(doctorId,recordId);
        return true;
    }
}