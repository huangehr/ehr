package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
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
    @RequestMapping(value = "/medical_record/patient/addPatient", method = RequestMethod.PUT)
    public boolean addPatient(
            @ApiParam(name = "patientInformation", value = "患者信息") @RequestParam(value = "patient", required = true) String json){
        MrPatientsEntity patient=toEntity(json,MrPatientsEntity.class);
        return patientService.addPatient(patient);
    }

    @ApiOperation("获取患者个人信息by身份证号或手机号")
    @RequestMapping(value = "/medical_record/patient/getPatientInformation", method = RequestMethod.GET)
    public MrPatientsEntity getPatientInformationBydemographicIdOrPhone(
            @ApiParam(name = "phone", value = "手机号") @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = false) String demographicId){
        return patientService.getPatientInformationBydemographicIdOrPhone(demographicId,phone);
    }

}
