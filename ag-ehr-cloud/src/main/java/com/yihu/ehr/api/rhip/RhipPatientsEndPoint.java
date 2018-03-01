package com.yihu.ehr.api.rhip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.util.json.JSONObject;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.AddressClient;
import com.yihu.ehr.feign.ConventionalDictEntryClient;
import com.yihu.ehr.feign.GeographyClient;
import com.yihu.ehr.feign.PatientClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.profile.util.DataSetParser;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 患者注册接口。患者注册之后，可以为患者申请为平台用户提供快捷信息录入。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@EnableFeignClients
@RestController
@RequestMapping(value = ApiVersion.Version1_0+"/rhip", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "patients", description = "区域卫生信息平台-患者服务", tags = {"区域卫生信息平台-患者服务"})
public class RhipPatientsEndPoint extends BaseController {
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
    @Autowired
    private AddressClient addressClient;
    @Autowired
    private ConventionalDictEntryClient dictEntryClient;

    @ApiOperation(value = "患者注册", notes = "根据患者的身份证号在健康档案平台中注册患者")
    @RequestMapping(value = "/patient/{demographic_id}", method = RequestMethod.POST)
    public Envelop registerPatient(
            @ApiParam(value = "身份证号")
            @PathVariable("demographic_id") String demographicId,
            @ApiParam(name = "json", value = "患者人口学数据集")
            @RequestParam(value = "json", required = true) String patientInfo) throws Exception{

        if (isPatientRegistered(demographicId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.PatientRegisterFailedForExist);
        }

        PatientDetailModel detailModel = objectMapper.readValue(patientInfo, PatientDetailModel.class);
//        if (!StringUtils.isEmpty(path)) {
//            detailModel.setPicPath(path);
//        }
        String errorMsg = "";
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "姓名不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getTelephoneNo())) {
            errorMsg += "联系方式不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getNativePlace())) {
            errorMsg += "籍贯不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getNation())) {
            errorMsg += "籍贯不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        //身份证校验
        if (patientClient.isRegistered(detailModel.getIdCardNo())) {
            return failed("身份证号已存在!");
        }

        //新增家庭地址信息
        GeographyModel geographyModel = detailModel.getHomeAddressInfo();
        detailModel.setHomeAddress("");
        if (geographyModel != null && !geographyModel.nullAddress()) {
            String addressId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setHomeAddress(addressId);
        }
        //新增户籍地址信息
        geographyModel = detailModel.getBirthPlaceInfo();
        detailModel.setBirthPlace("");
        if (geographyModel !=null  && !geographyModel.nullAddress()) {
            String addressId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setBirthPlace(addressId);
        }

        //新增工作地址信息
        geographyModel = detailModel.getWorkAddressInfo();
        detailModel.setWorkAddress("");
        if (geographyModel!= null && !geographyModel.nullAddress()) {
            String addressId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setWorkAddress(addressId);
        }

        //新增人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        try {
            info.setBirthday(DateTimeUtil.simpleDateTimeParse(detailModel.getBirthday()));
            info = patientClient.createPatient(objectMapper.writeValueAsString(info));
            if (info == null) {
                return failed("保存失败!");
            }
            detailModel = convertToPatientDetailModel(info);
            return success(detailModel);
        } catch (ParseException e) {
            e.printStackTrace();
            return failed("保存失败,出生日期格式有误!");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return failed("保存人口学信息失败!");
        }


    }

    @ApiOperation(value = "更新患者", response = boolean.class)
    @RequestMapping(value = "/patient/{demographic_id}", method = RequestMethod.PUT)
    public Envelop updatePatient(@ApiParam(name = "demographic_id", value = "身份证号")
                                @PathVariable(value = "demographic_id") String demographicId,
                                @ApiParam(name = "json", value = "患者人口学数据集")
                                @RequestParam(value = "json", required = true) String patient) throws Exception {

        if (!isPatientRegistered(demographicId)) {
//            throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.PatientRegisterFailedForExist);
            return failed("病人信息未注册!");
        }

        PatientDetailModel detailModel = objectMapper.readValue(patient, PatientDetailModel.class);
        String errorMsg = "";
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "姓名不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getTelephoneNo())) {
            errorMsg += "联系方式不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getNativePlace())) {
            errorMsg += "籍贯不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(detailModel.getNation())) {
            errorMsg += "籍贯不能为空!";
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }

        //新增家庭地址信息
        GeographyModel geographyModel = detailModel.getHomeAddressInfo();
        detailModel.setHomeAddress("");
        if (geographyModel !=null && !geographyModel.nullAddress()) {
            String addressId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setHomeAddress(addressId);
        }
        //新增户籍地址信息
        geographyModel = detailModel.getBirthPlaceInfo();
        detailModel.setBirthPlace("");
        if (geographyModel !=null && !geographyModel.nullAddress()) {
            String addressId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setBirthPlace(addressId);
        }

        //新增工作地址信息
        geographyModel = detailModel.getWorkAddressInfo();
        detailModel.setWorkAddress("");
        if (geographyModel != null && !geographyModel.nullAddress()) {
            String addressId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setWorkAddress(addressId);
        }

        //修改人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        try {
            info.setBirthday(DateTimeUtil.simpleDateTimeParse(detailModel.getBirthday()));
        } catch (ParseException e) {
            e.printStackTrace();
            return failed("保存失败,出生日期格式有误!");
        }
        info = patientClient.updatePatient(objectMapper.writeValueAsString(info));
        if (info == null) {
            return failed("保存失败!");
        }

        detailModel = convertToPatientDetailModel(info);
        return success(detailModel);

    }

    /**
     * 根据身份证号查找人
     *
     * @param demographic_id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/patient/{demographic_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号查找人")
    public Envelop getPatient(
            @ApiParam(name = "demographic_id", value = "身份证号", defaultValue = "")
            @PathVariable(value = "demographic_id") String demographic_id) throws Exception {

        MDemographicInfo demographicInfo = patientClient.getPatient(demographic_id);
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
        if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(code)){
            String[] values = {name,String.valueOf(code)};
            List<MGeographyDict> geographyDictList = (List<MGeographyDict>) geographyClient.getAddressDict(fields,values);
            if(null != geographyDictList && geographyDictList.size()>0){
                return geographyDictList.get(0).getId();
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }

    @ApiOperation(value = "通过json对象注册患者", notes = "根据患者的身份证号在健康档案平台中注册患者-弘扬对接接口")
    @RequestMapping(value = "/registerPatientByPJson", method = RequestMethod.POST)
    public Envelop registerPatientByPJson(
            @ApiParam(name = "json", value = "患者人口学数据集")
            @RequestParam(value = "json", required = true) String patientInfo) throws Exception{
        String errorMsg = "";
        PatientDetailModel detailModel = new PatientDetailModel();
        Map<String, String>  detailModelJson = objectMapper.readValue(patientInfo, Map.class);
        if(null != detailModelJson && detailModelJson.size()>0){
            if (null != detailModelJson.get("name") && !StringUtils.isEmpty(detailModelJson.get("name").toString())) {
                detailModel.setName(detailModelJson.get("name").toString());
            }else{
                errorMsg += "姓名不能为空!";
            }
            if (null != detailModelJson.get("idCardNo") && !StringUtils.isEmpty(detailModelJson.get("idCardNo").toString())) {
                detailModel.setIdCardNo(detailModelJson.get("idCardNo").toString());
                //身份证校验
                if (patientClient.isRegistered(detailModel.getIdCardNo())) {
                    return failed("身份证号已存在!");
                }
            }else{
                errorMsg += "身份证号不能为空!";
            }
            if (null != detailModelJson.get("telephoneNo") && !StringUtils.isEmpty(detailModelJson.get("telephoneNo").toString())) {
                if(isDigital(detailModelJson.get("telephoneNo").toString())){
                    //联系电话
                    Map<String, String> telphoneNo = new HashMap<>();
                    String tag = "联系电话";
                    telphoneNo.put(tag,detailModelJson.get("telephoneNo").toString());
                    detailModel.setTelephoneNo(toJson(telphoneNo));
                }else{
                    errorMsg += "联系方式格式不正确!";
                }

            }else{
                errorMsg += "联系方式不能为空!";
            }
            //籍贯 nativePlace
            if (null != detailModelJson.get("nativePlace") && !StringUtils.isEmpty(detailModelJson.get("nativePlace").toString())) {
                detailModel.setNativePlace(detailModelJson.get("nativePlace").toString());
            }
            //民族 nation
            if (null != detailModelJson.get("nation") && !StringUtils.isEmpty(detailModelJson.get("nation").toString())) {
                String nation = getDictKeyByValueAndDictList(dictEntryClient.getDictEntryByDictId("5"),detailModelJson.get("nation").toString());
                if(!StringUtils.isEmpty(nation)){
                    detailModel.setNation(nation);
                }else{
                    errorMsg += "该民族不存在!";
                }
            }
            //出生日期 birthday
            if (null != detailModelJson.get("birthday") && !StringUtils.isEmpty(detailModelJson.get("birthday").toString())) {
                detailModel.setBirthday(detailModelJson.get("birthday").toString());
            }

            //邮箱 email
            if (null != detailModelJson.get("email") &&!StringUtils.isEmpty(detailModelJson.get("email").toString())) {
                detailModel.setName(detailModelJson.get("email").toString());
            }
            //性别 gender
            if (null != detailModelJson.get("gender") && !StringUtils.isEmpty(detailModelJson.get("gender").toString())) {
                String gender = getDictKeyByValueAndDictList(dictEntryClient.getDictEntryByDictId("3"),detailModelJson.get("gender").toString());
                if(!StringUtils.isEmpty(gender)){
                    detailModel.setGender(gender);
                }else{
                    errorMsg += "性别不存在!";
                }
            }
            //婚姻状况 martialStatus
            if (null != detailModelJson.get("martialStatus") && !StringUtils.isEmpty(detailModelJson.get("martialStatus").toString())) {
                String martialStatus = getDictKeyByValueAndDictList(dictEntryClient.getDictEntryByDictId("4"),detailModelJson.get("martialStatus").toString());
                if(!StringUtils.isEmpty(martialStatus)){
                    detailModel.setMartialStatus(martialStatus);
                }else{
                    errorMsg += "婚姻状况不存在!";
                }
            }
            //户口性质 residenceType temp 农村，usual 非农村
            if (null != detailModelJson.get("residenceType") && !StringUtils.isEmpty(detailModelJson.get("residenceType").toString())) {
                if("农村".equals(detailModelJson.get("residenceType").toString())){
                    detailModel.setResidenceType("temp");
                }else if("非农村".equals(detailModelJson.get("residenceType").toString())){
                    detailModel.setResidenceType("usual");
                }else{
                    errorMsg += "户籍性质（residenceType）应该为农村或者非农村!";
                }

            }
            //头像路径 picPath
            if (null != detailModelJson.get("picPath") && !StringUtils.isEmpty(detailModelJson.get("picPath").toString())) {
                detailModel.setPicPath(detailModelJson.get("picPath").toString());
            }
            GeographyModel geographyModel;
            //新增户籍地址信息 出生地 birthPlace
            if (null != detailModelJson.get("birthPlace") && !StringUtils.isEmpty(detailModelJson.get("birthPlace").toString())) {
                geographyModel=  getAdressFormat(detailModelJson.get("birthPlace").toString());
                if (!geographyModel.nullAddress()) {
                    String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                    detailModel.setBirthPlace(addressId);
                }
            }
            //工作地址 workAddress
            if (null != detailModelJson.get("workAddress") && !StringUtils.isEmpty(detailModelJson.get("workAddress").toString())) {
                geographyModel=  getAdressFormat(detailModelJson.get("workAddress").toString());
                if (!geographyModel.nullAddress()) {
                    String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                    detailModel.setWorkAddress(addressId);
                }
            }
            //家庭住址 homeAddress
            if (null != detailModelJson.get("homeAddress") && !StringUtils.isEmpty(detailModelJson.get("homeAddress").toString())) {
                geographyModel=  getAdressFormat(detailModelJson.get("homeAddress").toString());
                if (!geographyModel.nullAddress()) {
                    String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                    detailModel.setHomeAddress(addressId);
                }
            }
        }

        if (!StringUtils.isEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        //新增人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        try {
            info.setBirthday(DateUtil.strToDate(detailModel.getBirthday()));
            info = patientClient.createPatient(objectMapper.writeValueAsString(info));
            if (info == null) {
                return failed("保存失败!");
            }
            detailModel = convertToPatientDetailModel(info);
            return success(detailModel);
        } catch (ParseException e) {
            e.printStackTrace();
            return failed("保存失败,出生日期格式有误!");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return failed("保存人口学信息失败!");
        }
    }


    @ApiOperation(value = "通过json对象更新患者", notes = "根据患者的身份证号在健康档案平台中更新患者-弘扬对接接口")
    @RequestMapping(value = "/updatePatientByPJson", method = RequestMethod.PUT)
    public Envelop updatePatientByPJson(
            @ApiParam(name = "json", value = "患者人口学数据集")
            @RequestParam(value = "json", required = true) String patientInfo) throws Exception{
        String errorMsg = "";
        PatientDetailModel detailModel = new PatientDetailModel();
        Map<String, String>  detailModelJson = objectMapper.readValue(patientInfo, Map.class);
        if(null != detailModelJson && detailModelJson.size()>0){
            if (null != detailModelJson.get("name") && !StringUtils.isEmpty(detailModelJson.get("name").toString())) {
                detailModel.setName(detailModelJson.get("name").toString());
            }else{
                errorMsg += "姓名不能为空!";
            }
            if (null != detailModelJson.get("idCardNo") && !StringUtils.isEmpty(detailModelJson.get("idCardNo").toString())) {
                detailModel.setIdCardNo(detailModelJson.get("idCardNo").toString());
                //身份证校验
                if (!patientClient.isRegistered(detailModel.getIdCardNo())) {
                    return failed("身份证号不存在!");
                }
            }else{
                errorMsg += "身份证号不能为空!";
            }
            if (null != detailModelJson.get("telephoneNo") && !StringUtils.isEmpty(detailModelJson.get("telephoneNo").toString())) {
                if(isDigital(detailModelJson.get("telephoneNo").toString())){
                    //联系电话
                    Map<String, String> telphoneNo = new HashMap<>();
                    String tag = "联系电话";
                    telphoneNo.put(tag,detailModelJson.get("telephoneNo").toString());
                    detailModel.setTelephoneNo(toJson(telphoneNo));
                }else{
                    errorMsg += "联系方式格式不正确!";
                }
            }else{
                errorMsg += "联系方式不能为空!";
            }
            //籍贯 nativePlace
            if (null != detailModelJson.get("nativePlace") && !StringUtils.isEmpty(detailModelJson.get("nativePlace").toString())) {
                detailModel.setNativePlace(detailModelJson.get("nativePlace").toString());
            }
            //民族 nation
            if (null != detailModelJson.get("nation") && !StringUtils.isEmpty(detailModelJson.get("nation").toString())) {
                String nation = getDictKeyByValueAndDictList(dictEntryClient.getDictEntryByDictId("5"),detailModelJson.get("nation").toString());
                if(!StringUtils.isEmpty(nation)){
                    detailModel.setNation(nation);
                }else{
                    errorMsg += "该民族不存在!";
                }
            }
            //出生日期 birthday
            if (null != detailModelJson.get("birthday") && !StringUtils.isEmpty(detailModelJson.get("birthday").toString())) {
                detailModel.setBirthday(detailModelJson.get("birthday").toString());
            }

            //邮箱 email
            if (null != detailModelJson.get("email") &&!StringUtils.isEmpty(detailModelJson.get("email").toString())) {
                detailModel.setName(detailModelJson.get("email").toString());
            }
            //性别 gender
            if (null != detailModelJson.get("gender") && !StringUtils.isEmpty(detailModelJson.get("gender").toString())) {
                String gender = getDictKeyByValueAndDictList(dictEntryClient.getDictEntryByDictId("3"),detailModelJson.get("gender").toString());
                if(!StringUtils.isEmpty(gender)){
                    detailModel.setGender(gender);
                }else{
                    errorMsg += "性别不存在!";
                }
            }
            //婚姻状况 martialStatus
            if (null != detailModelJson.get("martialStatus") && !StringUtils.isEmpty(detailModelJson.get("martialStatus").toString())) {
                String martialStatus = getDictKeyByValueAndDictList(dictEntryClient.getDictEntryByDictId("4"),detailModelJson.get("martialStatus").toString());
                if(!StringUtils.isEmpty(martialStatus)){
                    detailModel.setMartialStatus(martialStatus);
                }else{
                    errorMsg += "婚姻状况不存在!";
                }
            }
            //户口性质 residenceType temp 农村，usual 非农村
            if (null != detailModelJson.get("residenceType") && !StringUtils.isEmpty(detailModelJson.get("residenceType").toString())) {
                if("农村".equals(detailModelJson.get("residenceType").toString())){
                    detailModel.setResidenceType("temp");
                }else if("非农村".equals(detailModelJson.get("residenceType").toString())){
                    detailModel.setResidenceType("usual");
                }else{
                    errorMsg += "户籍性质（residenceType）应该为农村或者非农村!";
                }

            }
            //头像路径 picPath
            if (null != detailModelJson.get("picPath") && !StringUtils.isEmpty(detailModelJson.get("picPath").toString())) {
                detailModel.setPicPath(detailModelJson.get("picPath").toString());
            }
            GeographyModel geographyModel;
            //新增户籍地址信息 出生地 birthPlace
            if (null != detailModelJson.get("birthPlace") && !StringUtils.isEmpty(detailModelJson.get("birthPlace").toString())) {
                geographyModel=  getAdressFormat(detailModelJson.get("birthPlace").toString());
                if (!geographyModel.nullAddress()) {
                    String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                    detailModel.setBirthPlace(addressId);
                }
            }
            //工作地址 workAddress
            if (null != detailModelJson.get("workAddress") && !StringUtils.isEmpty(detailModelJson.get("workAddress").toString())) {
                geographyModel=  getAdressFormat(detailModelJson.get("workAddress").toString());
                if (!geographyModel.nullAddress()) {
                    String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                    detailModel.setWorkAddress(addressId);
                }
            }
            //家庭住址 homeAddress
            if (null != detailModelJson.get("homeAddress") && !StringUtils.isEmpty(detailModelJson.get("homeAddress").toString())) {
                geographyModel=  getAdressFormat(detailModelJson.get("homeAddress").toString());
                if (!geographyModel.nullAddress()) {
                    String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                    detailModel.setHomeAddress(addressId);
                }
            }
        }

        if (!StringUtils.isEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        //新增人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        try {
            info.setBirthday(DateUtil.strToDate(detailModel.getBirthday()));
            info = patientClient.updatePatient(objectMapper.writeValueAsString(info));
            if (info == null) {
                return failed("更新失败!");
            }
            detailModel = convertToPatientDetailModel(info);
            return success(detailModel);
        } catch (ParseException e) {
            e.printStackTrace();
            return failed("更新失败,出生日期格式有误!");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return failed("更新人口学信息失败!");
        }
    }

    /**
     * 根据传过来的字典项list和参照做对比，若list中存在参照则获取字典的key值
     * @param mDictionaryEntryList
     * @param value
     * @return
     */
    private String  getDictKeyByValueAndDictList(List<MDictionaryEntry> mDictionaryEntryList,String value){
        String key ="";
        Map<String,String> map = new HashMap<>();
        if(null != mDictionaryEntryList && mDictionaryEntryList.size()>0){
            for(MDictionaryEntry mDictionaryEntry : mDictionaryEntryList){
               if(value.equals(mDictionaryEntry.getValue())){
                   key = mDictionaryEntry.getCode();
               }
            }
        }
        return key;
    }

    /**
     * 根据传过来的地址转换成地址对象
     * @param adress
     * @return
     */
    private GeographyModel  getAdressFormat(String adress){
        GeographyModel geographyModel = new GeographyModel();
        int  provinceLen =adress.indexOf("省")+1;
        int  cityLen =adress.indexOf("市")+1;
        String province ="" ;
        String city ="" ;
        String street ="";
        if(provinceLen>0){
            province =adress.substring(0,provinceLen) ;
            geographyModel.setProvince(province);
        }
        if(cityLen>0){
            city =adress.substring(provinceLen,cityLen) ;
            geographyModel.setCity(city);
            street =adress.substring(cityLen,adress.length()) ;
            geographyModel.setStreet(street);
        }
        return geographyModel;
    }

    /**
     * 数字验证
     *
     * @param str
     * @return
     */
    public boolean isDigital(String str) {
        return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
    }

}
