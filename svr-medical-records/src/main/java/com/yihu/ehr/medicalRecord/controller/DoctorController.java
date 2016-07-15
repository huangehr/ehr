package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "医生个人信息", description = "医生个人信息管理")
public class DoctorController extends BaseRestEndPoint {


    @Autowired
    DoctorService doctorService;

    @ApiOperation("获取医生信息by身份证号")
    @RequestMapping(value = "/medical_record/doctor/getDoctorInformation", method = RequestMethod.GET)
    public MrDoctorsEntity getDoctorInformationByDemographicId(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true)String demographicId){
        if(demographicId.length()==0)
            return null;
        else
             return doctorService.getDoctorInformationByDemographicId(demographicId);
    }

    @ApiOperation("增加医生")
    @RequestMapping(value = "/medical_record/doctor/addDoctor", method = RequestMethod.POST)
    public boolean addDoctor( @ApiParam(name = "name", value = "名字") @RequestParam(value = "name", required = true) String name,
                              @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
                              @ApiParam(name = "sex", value = "性别") @RequestParam(value = "sex", required = false) String sex,
                              @ApiParam(name = "birthday", value = "生日") @RequestParam(value = "birthday", required = false) String birthday,
                              @ApiParam(name = "orgCode", value = "所属医院代码")@RequestParam(value = "orgCode", required = false)String orgCode,
                              @ApiParam(name = "orgName", value = "所属医院名字")@RequestParam(value = "orgName", required = false) String orgName,
                              @ApiParam(name = "orgDept", value = "科室")@RequestParam(value = "orgDept", required = false) String orgDept,
                              @ApiParam(name = "title", value = "职称")@RequestParam(value = "title", required = false) String title,
                              @ApiParam(name = "good", value = "擅长")@RequestParam(value = "good", required = false) String good,
                              @ApiParam(name = "photo", value = "头像")@RequestParam(value = "photo", required = false) String photo,
                              @ApiParam(name = "phone", value = "电话号码")@RequestParam(value = "phone", required = false) String phone,
                              @ApiParam(name = "status", value = "状态")@RequestParam(value = "status", required = false) String status){
        MrDoctorsEntity doctor=new MrDoctorsEntity();
        doctor.setName(name);
        doctor.setDemographicId(demographicId);
        doctor.setSex(sex);
        if(birthday!=null&&birthday.length()>0) {
            doctor.setBirthday(Timestamp.valueOf(birthday));
        }
        doctor.setOrgCode(orgCode);doctor.setOrgName(orgName);doctor.setOrgDept(orgDept);doctor.setTitle(title);
        doctor.setGood(good);doctor.setPhoto(photo);doctor.setPhone(phone);doctor.setStatus(status);
        return doctorService.addDoctor(doctor);
    }

    @ApiOperation("更新医生职业状态")
    @RequestMapping(value = "/medical_record/doctor/updateDoctorStatus", method = RequestMethod.POST)
    public boolean updateDoctorStatusByDemographicId(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "status", value = "状态")
            @RequestParam(value = "status", required = true)String status){
        return doctorService.updateDoctorStatusByDemographicId(status, demographicId);
    }

    @ApiOperation("更新医生信息")
    @RequestMapping(value = "/medical_record/doctor/updateDoctor", method = RequestMethod.POST)
    public boolean updateDoctorInformationByDemographicId(
            @ApiParam(name = "doctorInformation", value = "医生信息")
            @RequestParam(value = "json", required = true)String json) {
        MrDoctorsEntity doctor=toEntity(json,MrDoctorsEntity.class);
        return doctorService.updateDoctorInformationByDemographicId(doctor);
    }
}
