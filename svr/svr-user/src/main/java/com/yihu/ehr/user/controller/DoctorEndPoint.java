package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MDoctor;
import com.yihu.ehr.user.entity.Doctors;
import com.yihu.ehr.user.service.DoctorService;
import com.yihu.ehr.util.phonics.PinyinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 2017-02-04 add  by hzp
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "doctors", description = "医生管理接口")
public class DoctorEndPoint extends EnvelopRestEndPoint {

    @Autowired
    DoctorService doctorService;

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生列表", notes = "根据查询条件获取医生列表在前端表格展示")
    public List<MDoctor> searchDoctors(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<Doctors> doctorsList = doctorService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, doctorService.getCount(filters), page, size);

        return (List<MDoctor>) convertToModels(doctorsList, new ArrayList<MDoctor>(doctorsList.size()), MDoctor.class, fields);
    }

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建医生", notes = "创建医生信息")
    public MDoctor createDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData) throws Exception {
        Doctors doctor = toEntity(doctoJsonData, Doctors.class);
        doctor.setInsertTime(new Date());
        doctor.setUpdateTime(new Date());
        doctor.setStatus("1");
        doctor.setPyCode(PinyinUtil.getPinYinHeadChar(doctor.getName(), false));
        doctorService.save(doctor);
        return convertToModel(doctor, MDoctor.class);
    }

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改医生", notes = "重新绑定医生信息")
    public MDoctor updateDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData) throws Exception {
        Doctors doctors = toEntity(doctoJsonData, Doctors.class);
        doctors.setUpdateTime(new Date());
        doctorService.save(doctors);
        return convertToModel(doctors, MDoctor.class);
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorsExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断医生code是否存在")
    public boolean isDoctorCodeExists(
            @ApiParam(name = "doctor_code", value = "医生code", defaultValue = "")
            @PathVariable(value = "doctor_code") String doctorCode) {
        return doctorService.getByCode(doctorCode) != null;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取医生信息")
    public MDoctor getDoctor(
            @ApiParam(name = "doctor_id", value = "", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId) {
        Doctors doctors = doctorService.getDoctor(doctorId);
        MDoctor doctorModel = convertToModel(doctors, MDoctor.class);
        return doctorModel;
    }


    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除医生", notes = "根据id删除医生")
    public boolean deleteDoctor(
            @ApiParam(name = "doctor_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId) throws Exception {
        doctorService.deleteDoctor(doctorId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变医生状态", notes = "根据id更新医生")
    public boolean updDoctorStatus(
            @ApiParam(name = "doctor_id", value = "id", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status) throws Exception {
        doctorService.updDoctorStatus(doctorId, status);
        return true;
    }

}