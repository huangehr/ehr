package com.yihu.ehr.ha.patient.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/patient")
@RestController
public class PatientController extends BaseRestController {

    @RequestMapping(value = "/patientDetail",method = RequestMethod.GET)
    public Object getPatientDetailByIdCardNo(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                       @PathVariable(value = "api_version") String apiVersion,
                                       @ApiParam(name = "idCardNo",value = "身份证号")
                                       @RequestParam(value = "idCardNo") String idCardNo){
        //TODO:根据身份证号获取病人明细信息
        return null;
    }

    @RequestMapping(value = "patients", method = RequestMethod.GET)
    public Object getPatientsByNameOrIdCardNo(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                              @PathVariable(value = "api_version") String apiVersion,
                                              @ApiParam(name = "name", value = "名称")
                                              @RequestParam(value = "name") String name,
                                              @ApiParam(name = "idCardNo", value = "身份证号")
                                              @RequestParam(value = "idCardNo") String idCardNo,
                                              @ApiParam(name = "province", value = "省")
                                              @RequestParam(value = "province") String province,
                                              @ApiParam(name = "city", value = "市")
                                              @RequestParam(value = "city") String city,
                                              @ApiParam(name = "district", value = "区")
                                              @RequestParam(value = "district") String district,
                                              @ApiParam(name = "page", value = "当前页数", defaultValue = "0")
                                              @RequestParam(value = "page") int page,
                                              @ApiParam(name = "rows", value = "每页显示数", defaultValue = "15")
                                              @RequestParam(value = "rows") int rows) {
        //TODO:根据姓名和身份证号查询病人列表
        return null;
    }

    @RequestMapping(value = "deletePatient",method = RequestMethod.DELETE)
    public Object deletePatient(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "idCardNo", value = "身份证号")
                                @RequestParam(value = "idCardNo") String idCardNo) {
        //TODO：删除病人信息
        return null;
    }

    @RequestMapping(value = "/fileLoad", method = RequestMethod.POST)
    public String upload(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                         @PathVariable(value = "api_version") String apiVersion,
                         @ApiParam(name = "inputStream", value = "文件流")
                         @RequestParam(value = "inputStream") InputStream inputStream,
                         @ApiParam(name = "fileName", value = "文件名称")
                         @RequestParam(value = "fileName") String fileName) throws IOException {
        //TODO:上传头像文件，返回文件路径 path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
        return null;
    }

    @RequestMapping(value = "/downFile",method = RequestMethod.GET)
    public Object downFile(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "api_version") String apiVersion,
                           @ApiParam(name = "groupName", value = "服务器组名")
                           @RequestParam(value = "groupName") String groupName,
                           @ApiParam(name = "filePath", value = "文件路径")
                           @RequestParam(value = "filePath") String filePath) {

        //TODO:获取文件
        return null;
    }

    @RequestMapping(value = "/updatePatient",method = RequestMethod.POST)
    public Object updatePatient(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "patientJsonData", value = "病人详细信息")
                                @RequestParam(value = "patientJsonData") String patientJsonData) {
        //TODO:修改病人信息
        return null;
    }

    @RequestMapping(value = "/resetPass",method = RequestMethod.PUT)
    public Object resetPass(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
                            @ApiParam(name = "idCardNo", value = "身份证号")
                            @RequestParam(value = "idCardNo") String idCardNo) {
        //TODO:密码重置
        return null;
    }
}
