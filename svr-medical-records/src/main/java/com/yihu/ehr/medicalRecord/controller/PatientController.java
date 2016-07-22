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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public boolean addPatient(@ApiParam(name = "appUid", value = "appUid")
                              @RequestParam(value = "appUid", required = true) String appUid,
                              @ApiParam(name = "appPatientId", value = "appPatientId")
                              @RequestParam(value = "appPatientId", required = true) String appPatientId,
                              @ApiParam(name = "patientInformation", value = "患者信息")
                              @RequestParam(value = "patientInformation", required = true) String json){
        if(!patientService.IsCreated(appUid,appPatientId)) {
            MrPatientsEntity patient = toEntity(json, MrPatientsEntity.class);
            patient.setAppUid(appUid);
            patient.setAppPatientId(appPatientId);
            return patientService.addPatient(patient);
        }
        else
            return false;
    }

    @ApiOperation("获取患者个人信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.Patient, method = RequestMethod.GET)
    public MrPatientsEntity getPatientInformation(
            @ApiParam(name = "appUid", value = "appUid")
            @RequestParam(value = "appUid", required = true) String appUid,
            @ApiParam(name = "appPatientId", value = "appPatientId")
            @RequestParam(value = "appPatientId", required = true) String appPatientId){
        if(patientService.IsCreated(appUid,appPatientId)) {
            return patientService.getPatientInformation(appUid, appPatientId);
        }
        else
            return null;
    }

    @ApiOperation("获取患者所有诊断")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientDiagnosis, method = RequestMethod.GET)
    public List<String> getPatientDiagnosis(
            @ApiParam(name = "appUid", value = "appUid")
            @RequestParam(value = "appUid", required = true) String appUid,
            @ApiParam(name = "appPatientId", value = "appPatientId")
            @RequestParam(value = "appPatientId", required = true) String appPatientId,
            @ApiParam(name = "doctorId", value = "医生id")
            @RequestParam(value = "doctorId", required = true) String doctorId) {

        if (patientService.IsCreated(appUid, appPatientId)) {
           return patientService.getPatientDiagnosis(patientService.getPatientInformation(appUid, appPatientId).getId(), doctorId);
        }
        else
            return null;
    }

}
