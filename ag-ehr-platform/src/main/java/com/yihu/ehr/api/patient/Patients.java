package com.yihu.ehr.api.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.DemographicIndexClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

/**
 * 患者注册接口。患者注册之后，若在平台上设置密码将成为平台的用户，若需要访问与用户相关的数据，请调用users接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/patients")
@Api(protocols = "https", value = "patients", description = "患者服务")
public class Patients {
    @Autowired
    ObjectMapper objectMapper;

    private DemographicIndexClient demographicIndex;

    /**
     * patientInfo格式:
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
     * "patien_id":" ",		        //患者ID
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
     * 患者注册信息
     */
    @ApiOperation(value = "注册患者", response = boolean.class, produces = "application/json", notes = "根据患者的身份证号及其他患者信息在健康档案平台中注册患者")
    @RequestMapping(value = "", method = {RequestMethod.POST})
    public String registerPatient(@ApiParam(name = "json", value = "患者人口学数据集")
                                  @RequestParam(value = "json", required = true) String patientInfo) throws IOException, ParseException {
        String demographicId = null;
        if (isPatientRegistered(demographicId)) {
            return null;
        }

        return "";
    }

    @ApiOperation(value = "获取患者", response = boolean.class, produces = "application/json")
    @RequestMapping(value = "/{demographic_id}", method = {RequestMethod.GET})
    public ResponseEntity<Json> getPatient(@ApiParam(name = "demographic_id", value = "身份证号")
                                           @PathVariable(value = "demographic_id") String demographicId) {
        if (isPatientRegistered(demographicId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.PatientRegisterFailedForExist);
        }

        return new ResponseEntity<>(new Json(""), HttpStatus.OK);
    }

    @ApiOperation(value = "更新患者", response = boolean.class, produces = "application/json")
    @RequestMapping(value = "/{demographic_id}", method = {RequestMethod.PUT})
    public String updatePatient(@ApiParam(name = "demographic_id", value = "身份证号")
                                @PathVariable(value = "demographic_id") String demographicId,
                                @ApiParam(name = "json", value = "患者人口学数据集")
                                @RequestParam(value = "json", required = true) String patient) throws IOException, ParseException {

        if (isPatientRegistered(demographicId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.PatientRegisterFailedForExist);
        }

        return "";
    }

    /**
     * 判断患者是否已注册。
     *
     * @param demographicId
     * @return
     */
    protected boolean isPatientRegistered(String demographicId) {
        return demographicIndex.isRegistered(demographicId);
    }
}
