package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalRecord;
import com.yihu.ehr.medicalRecord.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecord.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/29.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "病人个人信息", description = "病人个人信息管理")
public class PatientEndPoint extends BaseRestEndPoint {

    @Autowired
    PatientService patientService;

    /***************************************************************************/
    @ApiOperation("获取患者所有诊断")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientICD10, method = RequestMethod.GET)
    public Map<String,String> getPatientDiagnosis(
            @ApiParam(name = "patient_id", value = "患者ID")
            @PathVariable(value = "patient_id") String patientId,
            @ApiParam(name = "doctor_id", value = "医生id")
            @RequestParam(value = "doctor_id", required = true) String doctorId) throws Exception{
        return patientService.getPatientDiagnosis(patientId, doctorId);
    }

    @ApiOperation("获取患者所有病历")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientRecords, method = RequestMethod.GET)
    public List<MedicalRecord> getPatientRecords(
            @ApiParam(name = "patient_id", value = "患者ID")
            @PathVariable(value = "patient_id") String patientId,
            @ApiParam(name = "label", value = "label")
            @RequestParam(value = "label", required = false) String label,
            @ApiParam(name = "medical_time_from", value = "就诊时间范围开始")
            @RequestParam(value = "medical_time_from", required = true) String medicalTimeFrom,
            @ApiParam(name = "medical_time_end", value = "就诊时间范围结束")
            @RequestParam(value = "medical_time_end", required = true) String medicalTimeEnd,
            @ApiParam(name = "record_type", value = "病历类型 0线上诊断")
            @RequestParam(value = "record_type", required = false) String recordType,
            @ApiParam(name = "medical_diagnosis_code", value = "诊断代码")
            @RequestParam(value = "medical_diagnosis_code", required = false) String medicalDiagnosisCode,
            @ApiParam(name = "doctorId", value = "医生id")
            @RequestParam(value = "doctorId", required = true) String doctorId) throws Exception{

        return patientService.getPatientRecords(patientId, label,medicalTimeFrom,medicalTimeEnd,recordType,medicalDiagnosisCode,doctorId);
    }

    /************************* 患者信息 ****************************************/
    @ApiOperation("获取患者个人信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientInfo, method = RequestMethod.GET)
    public MrPatientsEntity getPatientInformation(
            @ApiParam(name = "patient_id", value = "患者ID")
            @PathVariable(value = "patient_id") String patientId)throws Exception{
        return patientService.getPatient(patientId);
    }

    @ApiOperation("增加患者")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientInfoManage, method = RequestMethod.POST)
    public boolean addPatient(@ApiParam(name = "json", value = "患者信息json")
                              @RequestParam(value = "json", required = true) String json)throws Exception{
            MrPatientsEntity patient = toEntity(json, MrPatientsEntity.class);
            return patientService.savePatient(patient);
    }

    @ApiOperation("更新患者个人信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientInfoManage, method = RequestMethod.PUT)
    public boolean updatePatientInformation(
            @ApiParam(name = "json", value = "患者信息json")
            @RequestParam(value = "json", required = true) String json)throws Exception{
        MrPatientsEntity mrPatientsEntity=toEntity(json,MrPatientsEntity.class);
        return patientService.savePatient(mrPatientsEntity);
    }







}
