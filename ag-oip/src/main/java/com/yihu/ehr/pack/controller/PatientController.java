package com.yihu.ehr.pack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.pack.feign.ConventionalDictClient;
import com.yihu.ehr.pack.feign.DemographicIndexClient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

/**
 * 病人注册接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersionPrefix.Version1_0 + "/patient")
public class PatientController {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DemographicIndexClient demographicIndex;

    protected boolean isPatientRegistered(String demographicId) {
        return demographicIndex.isRegistered(demographicId);
    }

    @RequestMapping(value = "/registration", method = {RequestMethod.GET})
    @ApiOperation(value = "判断病人是否已注册", response = boolean.class, produces = "application/json", notes = "根据病人的身份证号判断病人是否已在健康档案平台中注册")
    public boolean isRegistered(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "demographic_id", value = "用户名")
            @RequestParam(value = "demographic_id", required = true) String demographicId) {

        return isPatientRegistered(demographicId);
    }

    /**
     * 病人注册。requestBody格式:
     * <p>
     * {
     * "inner_version": "000000000000",
     * "code": "H_PC000001",
     * "event_no": "77021917049",
     * "patient_id": "6000343811",
     * "org_code": "00001",
     * "data" :
     * [
     * {
     * "event_code":" ",		        //标准数据集编码
     * "org_code":" ",		            //就诊卡发卡机构编码
     * "org_type":" ",		            //机构类型
     * "patien_id":" ",		        //病人ID
     * "card_no":" ",		            //就诊卡编号
     * "HDSD00_02_052":"",	            //医疗保险类别代码
     * "card_type":"",		            //卡类型代码
     * "DE99_03_009_00":"",            //医疗保险号
     * "HDSD00_01_005":"",             //身份证类别代码
     * "HDSD00_01_007":"",             //工作单位名称
     * "HDSD00_01_009":"",             //联系人-姓名
     * "HDSD00_01_010":"",             //联系人-电话
     * "HDSD00_01_015":"",             //学历代码
     * "HDSD00_01_016":"",             //职业类别代码
     * "creat_date":"",	            //建卡时间
     * "creat_operate":"",             //操作人员
     * "HDSD00_01_002":"龙龙",	        //用户名
     * "HDSD00_01_003":"Male",	        //性别
     * "HDSD00_01_004":"2000-11-11",	//出生日期
     * "HDSD00_01_006":"111222", 	    //身份证号
     * JDSA00_01_000                            //现住地完整地址
     * JDSA00_01_01                              //户籍完整地址
     * "HDSD00_01_008":
     * {
     * "liu": "12548481244",
     * "huang": "18250165552"
     * },                          //电话列表
     * "HDSD00_01_012":"Hanzu",		//民族
     * "HDSD00_01_017":"Marriaged"		//婚姻状况
     * }
     * ]
     * }
     * <p>
     * 病人注册信息
     */
    @ApiOperation(value = "注册病人", response = boolean.class, produces = "application/json", notes = "根据病人的身份证号及其他病人信息在健康档案平台中注册病人")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public boolean patientRegister(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "user_info", value = "用户名")
            @RequestParam(value = "user_info", required = true) String userInfo,
            HttpServletRequest request) throws IOException, ParseException {
        return false;
    }
}


