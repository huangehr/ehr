package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.service.DoctorMedicalRecordService;
import com.yihu.ehr.medicalRecord.service.MedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalRecords", description = "病历服务接口")
public class MedicalRecordsController extends EnvelopRestEndPoint {

    @Autowired
    MedicalRecordService mRService;

    @Autowired
    DoctorMedicalRecordService dMRService;

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    public MrMedicalRecordsEntity createMetadata(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalRecord) throws Exception
    {
        MrMedicalRecordsEntity mrMedicalRecord = toEntity(medicalRecord,MrMedicalRecordsEntity.class);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        mrMedicalRecord.setMedicalTime(t);

        mrMedicalRecord = mRService.saveMedicalRecord(mrMedicalRecord);

        return convertToModel(mrMedicalRecord,MrMedicalRecordsEntity.class);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新数据元")
    public MrMedicalRecordsEntity updateMetadata(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalRecord) throws Exception
    {
        MrMedicalRecordsEntity mrMedicalRecord = toEntity(medicalRecord,MrMedicalRecordsEntity.class);
        mrMedicalRecord = mRService.saveMedicalRecord(mrMedicalRecord);
        return convertToModel(mrMedicalRecord,MrMedicalRecordsEntity.class);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordByLastOne,method = RequestMethod.GET)
    @ApiOperation("根据医生ID和病人ID获取最近的一次病历")
    public MrMedicalRecordsEntity getMedicalHistoryByLastOne(
            @ApiParam(name="patientId",value="patientId",defaultValue = "")
            @RequestParam(value="patientId",required = false)int patientId,
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId",required = false)String doctorId) throws Exception
    {
        return mRService.getMedicalHistoryByLastOne(doctorId,patientId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.GET)
    @ApiOperation("根据ID获取病历")
    public MrMedicalRecordsEntity getMedicalRecord(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @RequestParam(value="id")int id) throws Exception
    {
        return mRService.getMedicalRecord(id);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteMetadata(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @RequestParam(value="id")int id) throws Exception
    {
        mRService.deleteRecord(id);
        return true;
    }
}