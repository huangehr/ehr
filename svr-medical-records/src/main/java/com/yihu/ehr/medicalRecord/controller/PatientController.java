package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.medicalRecord.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "病人个人信息", description = "病人个人信息管理")
public class PatientController extends BaseRestEndPoint {

    @Autowired
    PatientService patientService;


    @ApiOperation("增加患者")
    @RequestMapping(value = ServiceApi.MedicalRecords.Patient, method = RequestMethod.POST)
    public boolean addPatient(
            @ApiParam(name = "patientInformation", value = "患者信息") @RequestParam(value = "patientInformation", required = true) String json){
        MrPatientsEntity patient=toEntity(json,MrPatientsEntity.class);
        return patientService.addPatient(patient);
    }

    @ApiOperation("获取患者个人信息by身份证号或手机号")
    @RequestMapping(value = ServiceApi.MedicalRecords.Patient, method = RequestMethod.GET)
    public MrPatientsEntity getPatientInformationBydemographicIdOrPhone(
            @ApiParam(name = "phone", value = "手机号") @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = false) String demographicId){
        return patientService.getPatientInformationBydemographicIdOrPhone(demographicId,phone);
    }

    @ApiOperation("获取患者所有诊断")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientDiagnosis, method = RequestMethod.GET)
    public List<String> getPatientDiagnosis(
            @ApiParam(name = "phone", value = "手机号") @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = false) String demographicId,
            @ApiParam(name = "doctorId", value = "医生id")
            @RequestParam(value = "doctorId", required = true) String doctorId){
        int id=patientService.getPatientInformationBydemographicIdOrPhone(demographicId,phone).getId();
        return patientService.getPatientDiagnosis(id,Integer.parseInt(doctorId));
    }

    @ApiOperation("确认患者信息是否存在")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientExistence, method = RequestMethod.GET)
    public int PatientExistence(
            @ApiParam(name="AppPatientId",value="AppPatientId",defaultValue = "")
            @PathVariable(value="app_patient_id")String AppPatientId,
            @ApiParam(name="appUId",value="appUId",defaultValue = "")
            @RequestParam(value="app_uid",required = false)String appUId,
            @ApiParam(name="patientJson",value="患者信息JSON",defaultValue = "")
            @RequestBody String patientJson) throws Exception
    {

        MrPatientsEntity mrPatientsEntity = toEntity(patientJson,MrPatientsEntity.class);
        MrPatientsEntity mrPatient = patientService.checkInfo(appUId, AppPatientId, mrPatientsEntity);

        return mrPatient.getId();
    }

}
