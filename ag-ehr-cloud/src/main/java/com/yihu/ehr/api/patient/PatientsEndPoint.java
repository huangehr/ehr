package com.yihu.ehr.api.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.ConventionalDictEntryClient;
import com.yihu.ehr.feign.GeographyClient;
import com.yihu.ehr.feign.PatientClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.profile.util.DataSetParser;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.id.IdValidator;
import com.yihu.ehr.util.rest.Envelop;
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
import java.util.List;
import java.util.Map;

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
public class PatientsEndPoint  extends BaseController {
    @Autowired
    DataSetParser dataSetParser;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private GeographyClient geographyClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

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
     * 根据身份证号查找人
     *
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}", method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号查找人")
    public Envelop getPatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception {

        MDemographicInfo demographicInfo = patientClient.getPatient(idCardNo);
//        if (!StringUtils.isEmpty(demographicInfo.getPicPath())) {
//            Map<String, String> map = toEntity(demographicInfo.getPicPath(), Map.class);
//            String imagePath[] = demographicInfo.getPicPath().split(":");
//            String localPath = patientClient.downloadPicture(imagePath[0], imagePath[1]);
//            demographicInfo.setLocalPath(localPath);
//        }

        if (demographicInfo == null) {
            return failed("数据获取失败！");
        }
        PatientDetailModel detailModel = convertToPatientDetailModel(demographicInfo);

        return success(detailModel);
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


    public PatientDetailModel convertToPatientDetailModel(MDemographicInfo demographicInfo) throws Exception {

        if (demographicInfo == null) {
            return null;
        }

        PatientDetailModel detailModel = convertToModel(demographicInfo, PatientDetailModel.class);
//        detailModel.setBirthday(DateToString(demographicInfo.getBirthday(), AgAdminConstants.DateFormat));
        detailModel.setBirthday(demographicInfo.getBirthday()==null?"": DateTimeUtil.simpleDateFormat(demographicInfo.getBirthday()));
        MConventionalDict dict = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(detailModel.getMartialStatus())) {
            dict = conventionalDictClient.getMartialStatus(detailModel.getMartialStatus());
        }
        detailModel.setMartialStatusName(dict == null ? "" : dict.getValue());

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(detailModel.getNation())) {
            dict = conventionalDictClient.getNation(detailModel.getNation());
        }
        detailModel.setNationName(dict == null ? "" : dict.getValue());

        MGeography mGeography = null;
        if (!org.apache.commons.lang3.StringUtils.isEmpty(demographicInfo.getHomeAddress())) {
            //家庭地址
            mGeography = geographyClient.getAddressById(demographicInfo.getHomeAddress());
            if (mGeography != null) {
                detailModel.setHomeAddressFull(mGeography.fullAddress());
                detailModel.setHomeAddressInfo(convertToModel(mGeography, GeographyModel.class));

                detailModel.getHomeAddressInfo().setProvinceId(geographyToCode(detailModel.getHomeAddressInfo().getProvince(),156));
                detailModel.getHomeAddressInfo().setCityId(geographyToCode(detailModel.getHomeAddressInfo().getCity(),detailModel.getHomeAddressInfo().getProvinceId()));
                detailModel.getHomeAddressInfo().setDistrictId(geographyToCode(detailModel.getHomeAddressInfo().getDistrict(),detailModel.getHomeAddressInfo().getCityId()));
            }
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(demographicInfo.getBirthPlace())) {
            //户籍地址
            mGeography = geographyClient.getAddressById(demographicInfo.getBirthPlace());
            if (mGeography != null) {
                detailModel.setBirthPlaceFull(mGeography.fullAddress());
                detailModel.setBirthPlaceInfo(convertToModel(mGeography, GeographyModel.class));

                detailModel.getBirthPlaceInfo().setProvinceId(geographyToCode(detailModel.getBirthPlaceInfo().getProvince(),156));
                detailModel.getBirthPlaceInfo().setCityId(geographyToCode(detailModel.getBirthPlaceInfo().getCity(),detailModel.getBirthPlaceInfo().getProvinceId()));
                detailModel.getBirthPlaceInfo().setDistrictId(geographyToCode(detailModel.getBirthPlaceInfo().getDistrict(),detailModel.getBirthPlaceInfo().getCityId()));
            }
        }
        //工作地址
        if (!org.apache.commons.lang3.StringUtils.isEmpty(demographicInfo.getWorkAddress())) {
            mGeography = geographyClient.getAddressById(demographicInfo.getWorkAddress());
            if (mGeography != null) {
                detailModel.setWorkAddressFull(mGeography.fullAddress());
                detailModel.setWorkAddressInfo(convertToModel(mGeography, GeographyModel.class));

                detailModel.getWorkAddressInfo().setProvinceId(geographyToCode(detailModel.getWorkAddressInfo().getProvince(),156));
                detailModel.getWorkAddressInfo().setCityId(geographyToCode(detailModel.getWorkAddressInfo().getCity(),detailModel.getWorkAddressInfo().getProvinceId()));
                detailModel.getWorkAddressInfo().setDistrictId(geographyToCode(detailModel.getWorkAddressInfo().getDistrict(),detailModel.getWorkAddressInfo().getCityId()));
            }
        }

        //联系电话
        String tag = "联系电话";
        Map<String, String> telephoneNo = null;
        try {
            telephoneNo = objectMapper.readValue(detailModel.getTelephoneNo(), Map.class);
        } catch (Exception e) {
            telephoneNo = null;
        }

        detailModel.setTelephoneNo(null);
        if (telephoneNo != null && telephoneNo.containsKey(tag)) {
            detailModel.setTelephoneNo(telephoneNo.get(tag));
        }

        return detailModel;
    }

    public int geographyToCode(String name,int code){
        String[] fields = {"name","pid"};
        String[] values = {name,String.valueOf(code)};
        List<MGeographyDict> geographyDictList = (List<MGeographyDict>) geographyClient.getAddressDict(fields,values);
        return geographyDictList.get(0).getId();
    }

}
