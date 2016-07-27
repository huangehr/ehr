package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
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
    @RequestMapping(value = ServiceApi.MedicalRecords.AddPatient, method = RequestMethod.POST)
    public boolean addPatient(@ApiParam(name = "patientJson", value = "patientJson")
                              @RequestParam(value = "patientJson", required = true) String patientJson)throws Exception{
            MrPatientsEntity patient = toEntity(patientJson, MrPatientsEntity.class);
            return patientService.addPatient(patient);
    }

    @ApiOperation("获取患者个人信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientInfo, method = RequestMethod.GET)
    public MrPatientsEntity getPatientInformation(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) String id)throws Exception{
            return patientService.getPatientInformation(id);
    }
    @ApiOperation("更新患者个人信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientInfo, method = RequestMethod.GET)
    public boolean updatePatientInformation(
            @ApiParam(name = "patientInformation", value = "patientInformation")
            @RequestParam(value = "patientInformation", required = true) String patientInformation)throws Exception{
        MrPatientsEntity mrPatientsEntity=toEntity(patientInformation,MrPatientsEntity.class);
        return patientService.updatePatientInformationByID(mrPatientsEntity);
    }
    @ApiOperation("自定义查询")
    @RequestMapping(value = ServiceApi.MedicalRecords.SearchPatient, method = RequestMethod.GET)
    public List<MrPatientsEntity> searchPatient(
            @ApiParam(name = "queryCondition", value = "queryCondition")
            @RequestParam(value = "queryCondition", required = true) String queryCondition)throws Exception{
        return patientService.searchPatient(queryCondition);
    }

    @ApiOperation("获取患者所有诊断")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientDiagnosis, method = RequestMethod.GET)
    public List<String> getPatientDiagnosis(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) String id,
            @ApiParam(name = "doctorId", value = "医生id")
            @RequestParam(value = "doctorId", required = true) String doctorId) {

        if (patientService.IsCreated(id)) {
            return patientService.getPatientDiagnosis(id, doctorId);
        }
        else
            return null;
    }

    @ApiOperation("获取患者所有病历")
    @RequestMapping(value = ServiceApi.MedicalRecords.PatientRecords, method = RequestMethod.GET)
    public List<String> getPatientRecords(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) String id,
            @ApiParam(name = "doctorId", value = "医生id")
            @RequestParam(value = "doctorId", required = true) String doctorId) {

        if (patientService.IsCreated(id)) {
            return patientService.getPatientDiagnosis(id, doctorId);
        }
        else
            return null;
    }

}
