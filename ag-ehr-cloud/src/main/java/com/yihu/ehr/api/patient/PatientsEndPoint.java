package com.yihu.ehr.api.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.PatientClient;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.profile.util.DataSetParser;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.id.IdValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

/**
 * 患者注册接口。患者注册之后，可以为患者申请为平台用户提供快捷信息录入。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "patients", description = "患者服务")
public class PatientsEndPoint {
    @Autowired
    DataSetParser dataSetParser;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PatientClient patientClient;

    @ApiOperation(value = "患者注册", notes = "根据患者的身份证号在健康档案平台中注册患者")
    @RequestMapping(value = "/patients/{demographic_id}", method = RequestMethod.POST)
    public void registerPatient(
            @ApiParam(value = "身份证号")
            @PathVariable("demographic_id") String demographicId,
            @ApiParam(name = "json", value = "患者人口学数据集")
            @RequestParam(value = "json", required = true) String patientInfo) throws IOException, ParseException {
        ObjectNode patientNode = (ObjectNode) objectMapper.readTree(patientInfo);
        PackageDataSet dataSet = dataSetParser.parseStructuredJsonDataSet(patientNode, false);

        for (String key : dataSet.getRecordKeys()) {
            MetaDataRecord record = dataSet.getRecord(key);

            if (StringUtils.isEmpty(demographicId)) {
                throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.MissParameter, "Missing identity card no.");
            }

            String error = IdValidator.validateIdCardNo(demographicId);
            if (!StringUtils.isEmpty(error)) {
                throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.InvalidIdentityNo, error);
            }

            if (isPatientRegistered(demographicId)) {
                throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.RepeatedIdentityNo, error);
            }

            MDemographicInfo demoInfo = new MDemographicInfo();
            if (dataSet.getCdaVersion().equals("000000000000")) {
                demoInfo.setName(record.getMetaData("HDSA00_01_009"));
                demoInfo.setIdCardNo(record.getMetaData("HDSA00_01_017"));
                demoInfo.setBirthday(DateTimeUtil.simpleDateParse(record.getMetaData("HDSA00_01_012")));
                demoInfo.setGender(record.getMetaData("HDSA00_01_011"));
                demoInfo.setNation(record.getMetaData("HDSA00_01_014"));
                demoInfo.setMartialStatus(record.getMetaData("HDSA00_01_015"));
                demoInfo.setNativePlace(record.getMetaData("HDSA00_01_022"));
                demoInfo.setEmail(record.getMetaData("HDSA00_01_021"));
                demoInfo.setTelephoneNo(record.getMetaData("HDSA00_01_019"));
            } else {
                demoInfo.setName(record.getMetaData("HDSD00_01_002"));
                demoInfo.setIdCardNo(record.getMetaData("HDSA00_01_017"));
                demoInfo.setBirthday(DateTimeUtil.simpleDateParse(record.getMetaData("HDSA00_01_012")));
                demoInfo.setGender(record.getMetaData("HDSA00_01_011"));
                demoInfo.setNation(record.getMetaData("HDSA00_01_014"));
                demoInfo.setMartialStatus(record.getMetaData("HDSD00_01_017"));
                demoInfo.setNativePlace(record.getMetaData("HDSA00_11_051"));
                demoInfo.setEmail(record.getMetaData("HDSA00_01_021"));
                demoInfo.setTelephoneNo(record.getMetaData("HDSD00_01_008"));
            }

            patientClient.createPatient(objectMapper.writeValueAsString(demoInfo));

            break;
        }
    }

    @ApiOperation(value = "更新患者", response = boolean.class)
    @RequestMapping(value = "/patients/{demographic_id}", method = {RequestMethod.PATCH})
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
