package com.yihu.ehr.ha.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.agModel.patient.PatientModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.ha.patient.service.PatientClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "patient", description = "人口管理接口，用于人口信息管理", tags = {"人口管理接口"})
public class PatientController extends BaseController {

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private AddressClient addressClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    @RequestMapping(value = "/populations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    public Envelop searchPatient(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception {

        ResponseEntity<List<MDemographicInfo>> responseEntity = patientClient.searchPatient(search, province, city, district, page, rows);
        List<MDemographicInfo> demographicInfos = responseEntity.getBody();
        List<PatientModel> patients = new ArrayList<>();
        for (MDemographicInfo patientInfo : demographicInfos) {

            PatientModel patient = convertToModel(patientInfo, PatientModel.class);
            //获取家庭地址信息
            String homeAddressId = patientInfo.getHomeAddress();
            if(StringUtils.isNotEmpty(homeAddressId)) {
                MGeography geography = addressClient.getAddressById(homeAddressId);
                String homeAddress = "";
                if (geography != null) {
                    if (StringUtils.isNotEmpty(geography.getProvince())) homeAddress += geography.getProvince();
                    if (StringUtils.isNotEmpty(geography.getCity())) homeAddress += geography.getCity();
                    if (StringUtils.isNotEmpty(geography.getDistrict())) homeAddress += geography.getDistrict();
                    if (StringUtils.isNotEmpty(geography.getTown())) homeAddress += geography.getTown();
                    if (StringUtils.isNotEmpty(geography.getStreet())) homeAddress += geography.getStreet();
                    if (StringUtils.isNotEmpty(geography.getExtra())) homeAddress += geography.getExtra();
                }
                patient.setHomeAddress(homeAddress);
            }
            //性别
            if(StringUtils.isNotEmpty(patientInfo.getGender())) {
                MConventionalDict dict = conventionalDictClient.getGender(patientInfo.getGender());
                patient.setGender(dict == null ? "" : dict.getValue());
            }
            //联系电话
            Map<String, String> telephoneNo;
            String tag="联系电话";
            telephoneNo = objectMapper.readValue(patient.getTelephoneNo(), Map.class);
            if (telephoneNo != null && telephoneNo.containsKey(tag)) {
                patient.setTelephoneNo(telephoneNo.get(tag));
            } else {
                patient.setTelephoneNo(null);
            }

            patients.add(patient);
        }

        return getResult(patients, getTotalCount(responseEntity), page, rows);
    }


    /**
     * 根据身份证号删除人
     *
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据身份证号删除人")
    public Envelop deletePatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception {

        boolean result = patientClient.deletePatient(idCardNo);
        if (!result) {
            return failed("删除失败!");
        }
        return success(null);
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
        if (demographicInfo == null) {
            return failed("数据获取失败！");
        }
        PatientDetailModel detailModel = convertToPatientDetailModel(demographicInfo);

        return success(detailModel);
    }


    /**
     * 根据前端传回来的json新增一个人口信息
     *
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations", method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    public Envelop createPatient(
            @ApiParam(name = "patientModelJsonData", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patientModelJsonData") String patientModelJsonData) throws Exception {

        PatientDetailModel detailModel = objectMapper.readValue(patientModelJsonData, PatientDetailModel.class);
        String errorMsg = "";
        if (StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "姓名不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getTelephoneNo())) {
            errorMsg += "联系方式不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getNativePlace())) {
            errorMsg += "籍贯不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getNation())) {
            errorMsg += "籍贯不能为空!";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        //身份证校验
        if(patientClient.isExistIdCardNo(detailModel.getIdCardNo()))
        {
            return failed("身份证号已存在!");
        }

        //新增家庭地址信息
        GeographyModel geographyModel = detailModel.getHomeAddressInfo();
        detailModel.setHomeAddress("");
        if (!isNullAddress(geographyModel)) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setHomeAddress(addressId);
        }
        //新增户籍地址信息
        geographyModel = detailModel.getBirthPlaceInfo();
        detailModel.setBirthPlace("");
        if (!isNullAddress(geographyModel)) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setBirthPlace(addressId);
        }

        //新增工作地址信息
        geographyModel = detailModel.getWorkAddressInfo();
        detailModel.setWorkAddress("");
        if (!isNullAddress(geographyModel)) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setWorkAddress(addressId);
        }

        //新增人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        info.setBirthday(StringToDate(detailModel.getBirthday(),AgAdminConstants.DateFormat));
        info = patientClient.createPatient(objectMapper.writeValueAsString(info));
        if (info == null) {
            return failed("保存失败!");
        }
        detailModel = convertToPatientDetailModel(info);
        return success(detailModel);
    }

    /**
     * 根据前端传回来的json修改人口信息
     *
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations", method = RequestMethod.PUT)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    public Envelop updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData) throws Exception {

        PatientDetailModel detailModel = objectMapper.readValue(patientModelJsonData, PatientDetailModel.class);
        String errorMsg = "";
        if (StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "姓名不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getTelephoneNo())) {
            errorMsg += "联系方式不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getNativePlace())) {
            errorMsg += "籍贯不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getNation())) {
            errorMsg += "籍贯不能为空!";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }

        //新增家庭地址信息
        GeographyModel geographyModel = detailModel.getHomeAddressInfo();
        detailModel.setHomeAddress("");
        if (!isNullAddress(geographyModel)) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setHomeAddress(addressId);
        }
        //新增户籍地址信息
        geographyModel = detailModel.getBirthPlaceInfo();
        detailModel.setBirthPlace("");
        if (!isNullAddress(geographyModel)) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setBirthPlace(addressId);
        }

        //新增工作地址信息
        geographyModel = detailModel.getWorkAddressInfo();
        detailModel.setWorkAddress("");
        if (!isNullAddress(geographyModel)) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setWorkAddress(addressId);
        }

        //修改人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        info.setBirthday(StringToDate(detailModel.getBirthday(),AgAdminConstants.DateFormat));
        info = patientClient.updatePatient(objectMapper.writeValueAsString(info));
        if (info == null) {
            return failed("保存失败!");
        }

        detailModel = convertToPatientDetailModel(info);
        return success(detailModel);

    }

    /**
     * 身份证是否已存在校验
     *
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/is_exist/{id_card_no}", method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    public boolean isExistIdCardNo(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception {
        return patientClient.isExistIdCardNo(idCardNo);
    }

    /**
     * 初始化密码
     *
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/password/{id_card_no}", method = RequestMethod.PUT)
    @ApiOperation(value = "初始化密码", notes = "用户忘记密码时重置密码，初始密码为123456")
    public boolean resetPass(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception {
        return patientClient.resetPass(idCardNo);
    }

    public PatientDetailModel convertToPatientDetailModel(MDemographicInfo demographicInfo) throws Exception {

        if (demographicInfo == null) {
            return null;
        }

        PatientDetailModel detailModel = convertToModel(demographicInfo, PatientDetailModel.class);
        detailModel.setBirthday(DateToString(demographicInfo.getBirthday(), AgAdminConstants.DateFormat));
        MConventionalDict dict = null;
        if (StringUtils.isNotEmpty(detailModel.getMartialStatus())) {
            dict = conventionalDictClient.getMartialStatus(detailModel.getMartialStatus());
        }
        detailModel.setMartialStatusName(dict == null ? "" : dict.getValue());

        if (StringUtils.isNotEmpty(detailModel.getNation())) {
            dict = conventionalDictClient.getNation(detailModel.getNation());
        }
        detailModel.setNationName(dict == null ? "" : dict.getValue());

        MGeography mGeography = null;
        if (!StringUtils.isEmpty(demographicInfo.getHomeAddress())) {
            //家庭地址
            mGeography = addressClient.getAddressById(demographicInfo.getHomeAddress());
            if (mGeography != null) {
                detailModel.setHomeAddressFull(getFullAddress(mGeography));
                detailModel.setHomeAddressInfo(convertToModel(mGeography, GeographyModel.class));
            }
        }
        if (!StringUtils.isEmpty(demographicInfo.getBirthPlace())) {
            //户籍地址
            mGeography = addressClient.getAddressById(demographicInfo.getBirthPlace());
            if (mGeography != null) {
                detailModel.setBirthPlaceFull(getFullAddress(mGeography));
                detailModel.setBirthPlaceInfo(convertToModel(mGeography, GeographyModel.class));
            }
        }
        //工作地址
        if (!StringUtils.isEmpty(demographicInfo.getWorkAddress())) {
            mGeography = addressClient.getAddressById(demographicInfo.getWorkAddress());
            if (mGeography != null) {
                detailModel.setWorkAddressFull(getFullAddress(mGeography));
                detailModel.setWorkAddressInfo(convertToModel(mGeography, GeographyModel.class));
            }
        }

        //联系电话
        String tag = "联系电话";
        Map<String, String> telephoneNo = objectMapper.readValue(detailModel.getTelephoneNo(), Map.class);
        detailModel.setTelephoneNo(null);
        if (telephoneNo != null && telephoneNo.containsKey(tag)) {
            detailModel.setTelephoneNo(telephoneNo.get(tag));
        }

        return detailModel;
    }

    public String getFullAddress(MGeography geography) {
        String fullAddress = "";

        if (StringUtils.isNotEmpty(geography.getProvince())) fullAddress += geography.getProvince();
        if (StringUtils.isNotEmpty(geography.getCity())) fullAddress += geography.getCity();
        if (StringUtils.isNotEmpty(geography.getDistrict())) fullAddress += geography.getDistrict();
        if (StringUtils.isNotEmpty(geography.getTown())) fullAddress += geography.getTown();
        if (StringUtils.isNotEmpty(geography.getStreet())) fullAddress += geography.getStreet();
        if (StringUtils.isNotEmpty(geography.getExtra())) fullAddress += geography.getExtra();

        return fullAddress;
    }

    public boolean isNullAddress (GeographyModel geographyModel)
    {
        if(geographyModel==null)
            return true;
        if(StringUtils.isEmpty(geographyModel.getProvince())
                && StringUtils.isEmpty(geographyModel.getCity())
                && StringUtils.isEmpty(geographyModel.getDistrict())
                && StringUtils.isEmpty(geographyModel.getTown())
                && StringUtils.isEmpty(geographyModel.getStreet())
                && StringUtils.isEmpty(geographyModel.getExtra()))
        {
            return true;
        }

        return false;
    }
}
