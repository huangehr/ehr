package com.yihu.ehr.api.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.PatientClient;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.profile.SimpleDataSetResolver;
import com.yihu.ehr.util.DateFormatter;
import com.yihu.ehr.util.IdCardValidator;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * 患者注册接口。患者注册之后，可以为患者申请为平台用户提供快捷信息录入。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/patients")
@Api(protocols = "https", value = "patients", description = "患者服务")
public class PatientsEndPoint {
    @Autowired
    SimpleDataSetResolver dataSetResolver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PatientClient patientClient;

    @ApiOperation(value = "患者注册", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "根据患者的身份证号在健康档案平台中注册患者")
    @RequestMapping(value = "/{demographic_id}", method = {RequestMethod.POST})
    public void registerPatient(@ApiParam(value = "身份证号")
                                @PathVariable("demographic_id")
                                String demographicId,
                                @ApiParam(name = "json", value = "患者人口学数据集")
                                @RequestParam(value = "json", required = true)
                                String patientInfo) throws IOException, ParseException {
        ObjectNode patientNode = (ObjectNode) objectMapper.readTree(patientInfo);
        ProfileDataSet dataSet = dataSetResolver.parseJsonDataSet(patientNode, false);

        for (String key : dataSet.getRecordKeys()) {
            Map<String, String> record = dataSet.getRecord(key);

            if (!StringUtils.isEmpty(demographicId)) {
                throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.MissParameter, "Missing identity card no.");
            }

            String error = IdCardValidator.doValidate(demographicId);
            if (!StringUtils.isEmpty(error)) {
                throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.InvalidIdentityNo, error);
            }

            if (isPatientRegistered(demographicId)) {
                return;
            }

            MDemographicInfo demoInfo = new MDemographicInfo();
            if (dataSet.getCdaVersion().equals("000000000000")) {
                demoInfo.setName(record.get("HDSA00_01_009"));
                demoInfo.setIdCardNo(record.get("HDSA00_01_017"));
                demoInfo.setBirthday(DateFormatter.simpleDateParse(record.get("HDSA00_01_012")));
                demoInfo.setGender(record.get("HDSA00_01_011"));
                demoInfo.setNation(record.get("HDSA00_01_014"));
                demoInfo.setMartialStatus(record.get("HDSA00_01_015"));
                demoInfo.setNativePlace(record.get("HDSA00_01_022"));
                demoInfo.setEmail(record.get("HDSA00_01_021"));
                demoInfo.setTelphoneNo(record.get("HDSA00_01_019"));
            } else {
                demoInfo.setName(record.get("HDSD00_01_002"));
                demoInfo.setIdCardNo(record.get("HDSA00_01_017"));
                demoInfo.setBirthday(DateFormatter.simpleDateParse(record.get("HDSA00_01_012")));
                demoInfo.setGender(record.get("HDSA00_01_011"));
                demoInfo.setNation(record.get("HDSA00_01_014"));
                demoInfo.setMartialStatus(record.get("HDSD00_01_017"));
                demoInfo.setNativePlace(record.get("HDSA00_11_051"));
                demoInfo.setEmail(record.get("HDSA00_01_021"));
                demoInfo.setTelphoneNo(record.get("HDSD00_01_008"));
            }

            patientClient.createPatient(objectMapper.writeValueAsString(demoInfo));

            break;
        }
    }

    @ApiOperation(value = "更新患者", response = boolean.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = "/{demographic_id}", method = {RequestMethod.PATCH})
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
        return patientClient.isRegistered(demographicId);
    }
}
