package com.yihu.ehr.medicalRecord.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalRecord;
import com.yihu.ehr.medicalRecord.model.Entity.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecord.service.DoctorMedicalRecordService;
import com.yihu.ehr.medicalRecord.service.MedicalLabelService;
import com.yihu.ehr.medicalRecord.service.MedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalRecords", description = "病历服务接口")
public class MedicalRecordsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    MedicalRecordService recordService;

    @Autowired
    DoctorMedicalRecordService doctorRecordService;

    @Autowired
    MedicalLabelService medicalLabelService;

    /**
     * 根据医生ID和病人ID获取最近的一次病历
     *
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.GET)
    @ApiOperation("系统接入接口")
    public Map<String,Object> medicalRecord(
            @ApiParam(name="patientId",value="病人ID",defaultValue = "B834C7A0417E4CA4BEC00FD3524EE870")
            @RequestParam(value="patientId",required = true)String patientId,
            @ApiParam(name="userId",value="用户ID",defaultValue = "B834C7A0417E4CA4BEC00FD3524EE870")
            @RequestParam(value="userId",required = true)String userId,
            @ApiParam(name="ticket",value="ticket",defaultValue = "")
            @RequestParam(value="ticket",required = true)String ticket,
            @ApiParam(name="appUid",value="应用轻ID",defaultValue = "")
            @RequestParam(value="appUid",required = true)String appUid) throws Exception
    {
        return recordService.medicalRecord(patientId, userId,ticket,appUid);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.AddRecord,method = RequestMethod.POST)
    @ApiOperation("新增病历")
    public MedicalRecord addRecord(
            @ApiParam(name="doctor_id",value="医生ID",defaultValue = "B834C7A0417E4CA4BEC00FD3524EE870")
            @RequestParam(value="doctor_id",required = true)String doctorId,
            @ApiParam(name="patient_id",value="患者ID",defaultValue = "B834C7A0417E4CA4BEC00FD3524EE870")
            @RequestParam(value="patient_id",required = true)String patientId) throws Exception
    {
        return recordService.addRecord(doctorId, patientId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalInfo,method = RequestMethod.PUT)
    @ApiOperation("修改病历")
    public boolean editRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "")
            @RequestParam(value="record_id",required = true)String recordId,
            @ApiParam(name="json",value="修改内容",defaultValue = "{}")
            @RequestParam(value="json",required = true)String json) throws Exception
    {
        Map<String,Object> map = toEntity(json,Map.class);
        return recordService.editRecord(recordId, map);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalInfo,method = RequestMethod.DELETE)
    @ApiOperation("删除病历")
    public boolean deleteRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "")
            @RequestParam(value="record_id",required = true)String recordId) throws Exception
    {
        return recordService.deleteRecord(recordId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalInfo,method = RequestMethod.GET)
    @ApiOperation("获取病历")
    public Map<String,Object> getMedicalRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "")
            @RequestParam(value="record_id",required = true)String recordId) throws Exception
    {
        return recordService.getMedicalRecord(recordId);
    }


    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalShare,method = RequestMethod.GET)
    @ApiOperation("病历分享")
    public boolean shareRecord(
            @ApiParam(name="record_id",value="病历ID")
            @RequestParam(value="record_id",required = true)String recordId,
            @ApiParam(name="patient_id",value="患者ID")
            @RequestParam(value="patient_id",required = true)String patientId,
            @ApiParam(name="doctor_id",value="医生ID")
            @RequestParam(value="doctor_id",required = true)String doctorId) throws Exception
    {
        return recordService.shareRecord(recordId,patientId,doctorId);
    }

    /******************************* 病历标签 *****************************************************/
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabel,method = RequestMethod.GET)
    @ApiOperation("获取病历标签")
    public List<MrMedicalLabelEntity> getMedicalLabelByRecordId(
            @ApiParam(name="record_id",value="病历ID")
            @RequestParam(value="record_id",required = true)String recordId) throws Exception
    {
        return medicalLabelService.getMedicalLabelByRecordId(recordId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabel,method = RequestMethod.POST)
    @ApiOperation("批量保存病历标签")
    public boolean saveMedicalLabel(
            @ApiParam(name="record_id",value="病历ID")
            @RequestParam(value="record_id",required = true)String recordId,
            @ApiParam(name="doctor_id",value="医生ID")
            @RequestParam(value="doctor_id",required = true)String doctorId,
            @ApiParam(name="list",value="标签列表")
            @RequestParam(value="list",required = true)List<String> list) throws Exception
    {
        return medicalLabelService.saveMedicalLabel(recordId,doctorId,list);
    }


}