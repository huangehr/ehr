package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.user.MDoctor;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DoctorClient {

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生列表", notes = "根据查询条件获取医生列表在前端表格展示")
    ResponseEntity<List<MDoctor>> searchDoctors(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "orgCode", required = false) String orgCode);

    @RequestMapping(value = ServiceApi.Doctors.DoctorsExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断code是否存在")
    boolean isCodeExists(@PathVariable(value = "doctor_code") String code);

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生信息", notes = "医生信息")
    MDoctor getDoctor(@PathVariable(value = "doctor_id") Long doctorId);

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建医生", notes = "重新绑定医生信息")
    MDoctor createDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model);

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改医生", notes = "重新绑定医生信息")
    MDoctor updateDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model);

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除医生", notes = "根据医生id删除医生")
    boolean deleteDoctor(@PathVariable(value = "doctor_id") String doctorId);

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变医生状态", notes = "根据医生状态改变当前医生状态")
    boolean updDoctorStatus(
            @PathVariable(value = "doctor_id") Long doctorId,
            @RequestParam(value = "status") String status);


    @RequestMapping(value = ServiceApi.Doctors.DoctorPhoneExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    List idExistence(
            @RequestBody String phones);

    @RequestMapping(value = ServiceApi.Doctors.DoctorBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入医生")
    boolean createDoctorsPatch(
            @RequestBody String doctors);

    @RequestMapping(value = ServiceApi.Doctors.DoctorOnePhoneExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    boolean isExistence(
            @RequestParam(value="filters") String filters);

    @RequestMapping(value = ServiceApi.Doctors.DoctorEmailExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @RequestBody String emails);

    @RequestMapping(value = ServiceApi.Doctors.DoctorsIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证号是否存在")
    boolean isCardNoExists(@PathVariable(value = "doctor_idCardNo") String idCardNo);

    @RequestMapping(value = ServiceApi.Doctors.DoctoridCardNoExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在身份证号码")
    List idCardNoExistence(
            @RequestBody String idCardNos);

    @RequestMapping(value = ServiceApi.Doctors.DoctorByIdCardNo, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生信息，通过身份证获取", notes = "医生信息")
    MDoctor getDoctorByIdCardNo(@PathVariable(value = "idCardNo") String idCardNo);

}