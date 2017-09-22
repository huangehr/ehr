package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.util.json.JSONObject;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.agModel.patient.PatientModel;
import com.yihu.ehr.agModel.user.PlatformAppRolesTreeModel;
import com.yihu.ehr.agModel.user.RoleUserModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.entity.patient.UserCards;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.patient.service.PatientCardsClient;
import com.yihu.ehr.patient.service.PatientClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.users.service.RoleUserClient;
import com.yihu.ehr.users.service.RolesClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "patient", description = "人口管理", tags = {"人口管理"})
public class PatientController extends BaseController {

    @Autowired
    private PatientClient patientClient;
    @Autowired
    private AddressClient addressClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;
    @Autowired
    private FileResourceClient fileResourceClient;
    @Autowired
    PatientCardsClient patientCardsClient;
    @Autowired
    SystemDictClient systemDictClient;
    @Autowired
    private ConventionalDictEntryClient dictEntryClient;
    @Autowired
    private AppClient appClient;
    @Autowired
    private RolesClient rolesClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private RoleUserClient roleUserClient;

    @RequestMapping(value = "/populations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    public Envelop searchPatient(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province") String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city") String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district") String district,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        try {
            ResponseEntity<List<MDemographicInfo>> responseEntity = patientClient.searchPatient(search, province, city, district, page, rows);
            List<MDemographicInfo> demographicInfos = responseEntity.getBody();
            List<PatientModel> patients = new ArrayList<>();
            for (MDemographicInfo patientInfo : demographicInfos) {

                PatientModel patient = convertToModel(patientInfo, PatientModel.class);
                //patient.setRegisterTime(DateToString(patientInfo.getRegisterTime(), AgAdminConstants.DateTimeFormat));
                patient.setRegisterTime(patientInfo.getRegisterTime() == null ? "" : DateTimeUtil.simpleDateTimeFormat(patientInfo.getRegisterTime()));
                //获取家庭地址信息
                String homeAddressId = patientInfo.getHomeAddress();
                if (StringUtils.isNotEmpty(homeAddressId)) {
                    MGeography geography = addressClient.getAddressById(homeAddressId);
                    String homeAddress = geography != null ? geography.fullAddress() : "";
                    patient.setHomeAddress(homeAddress);
                }
                //性别
                if (StringUtils.isNotEmpty(patientInfo.getGender())) {
                    MConventionalDict dict = conventionalDictClient.getGender(patientInfo.getGender());
                    patient.setGender(dict == null ? "" : dict.getValue());
                }
                //联系电话
                Map<String, String> telephoneNo;
                String tag = "联系电话";
                try {
                    telephoneNo = objectMapper.readValue(patient.getTelephoneNo(), Map.class);
                } catch (Exception e) {
                    telephoneNo = null;
                }
                if (telephoneNo != null && telephoneNo.containsKey(tag)) {
                    patient.setTelephoneNo(telephoneNo.get(tag));
                } else {
                    patient.setTelephoneNo(null);
                }

                patients.add(patient);
            }

            return getResult(patients, getTotalCount(responseEntity), page, rows);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
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
        try {
            fileResourceClient.filesDelete(idCardNo);
        } catch (Exception e) {
            return success("数据删除成功！头像图片删除失败！");
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
//        if (!StringUtils.isEmpty(demographicInfo.getPicPath())) {
//            Map<String, String> map = toEntity(demographicInfo.getPicPath(), Map.class);
//            String imagePath[] = demographicInfo.getPicPath().split(":");
//            String localPath = patientClient.downloadPicture(imagePath[0], imagePath[1]);
//            demographicInfo.setLocalPath(localPath);
//        }
        String fields = "id,demographicId,realName";
        String filters = "demographicId=" + idCardNo;
        String sorts = "+demographicId,+realName";
        int size = 20;
        int page = 1;
        ResponseEntity<List<MUser>> responseEntity = userClient.searchUsers(fields, filters, sorts, size, page);
        List<MUser> mUsers = responseEntity.getBody();

        if (demographicInfo == null) {
            return failed("数据获取失败！");
        }
        PatientDetailModel detailModel = convertToPatientDetailModel(demographicInfo);
        if (null != mUsers && mUsers.size() > 0) {
            for (MUser u : mUsers) {
                detailModel.setUserId(u.getId());
            }
        }
        return success(detailModel);
    }


    /**
     * 根据前端传回来的居民信息json注册居民
     *
     * @param patientJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的居民信息json注册居民")
    public Envelop registerPatient(
            @ApiParam(name = "loginCode", value = "登录名", defaultValue = "")
            @RequestParam(value = "loginCode") String loginCode,
            @ApiParam(name = "patientModelJsonData", value = "居民注册信息", defaultValue = "{\"idCardNo\":\"230903195402270067\",\"birthday\":\"2017-06-06 00:00:00\",\"birthPlace\":null,\"birthPlaceInfo\":{\"id\":null,\"country\":null,\"province\":\"安徽省\",\"city\":\"六安市\",\"district\":\"裕安区\",\"town\":null,\"street\":null,\"extra\":null,\"postalCode\":null,\"countryId\":0,\"cityId\":0,\"provinceId\":0,\"districtId\":0},\"birthPlaceFull\":null,\"nativePlace\":\"福建\",\"email\":\"990616454@qq.com\",\"gender\":\"1\",\"name\":\"zhang1\",\"martialStatus\":\"10\",\"martialStatusName\":null,\"nation\":\"1\",\"nationName\":null,\"residenceType\":\"temp\",\"residenceTypeName\":null,\"workAddress\":null,\"workAddressInfo\":{\"id\":null,\"country\":null,\"province\":\"安徽省\",\"city\":\"淮北市\",\"district\":\"相山区\",\"town\":null,\"street\":null,\"extra\":null,\"postalCode\":null,\"countryId\":0,\"cityId\":0,\"provinceId\":0,\"districtId\":0},\"workAddressFull\":null,\"homeAddress\":null,\"homeAddressInfo\":{\"id\":null,\"country\":null,\"province\":\"黑龙江省\",\"city\":\"鹤岗市\",\"district\":\"向阳区\",\"town\":null,\"street\":null,\"extra\":null,\"postalCode\":null,\"countryId\":0,\"cityId\":0,\"provinceId\":0,\"districtId\":0},\"homeAddressFull\":null,\"password\":null,\"telephoneNo\":\"{\\\"联系电话\\\":\\\"13253541190\\\"}\",\"picPath\":\"\",\"localPath\":\"\",\"registerTime\":null,\"userId\":null}")
            @RequestParam(value = "patientModelJsonData") String patientJsonData) throws Exception {

        PatientDetailModel detailModel = objectMapper.readValue(patientJsonData, PatientDetailModel.class);
        UserDetailModel userModel = objectMapper.readValue(patientJsonData, UserDetailModel.class);
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
//      注册信息验证
        JSONObject telephone = new JSONObject(detailModel.getTelephoneNo());
        String phone = telephone.getField("联系电话").toString();
        if (userClient.isUserNameExists(loginCode)) {
            return failed("账户已存在!");
        }

        if (userClient.isIdCardExists(detailModel.getIdCardNo())) {
            return failed("身份证号已存在!");
        }

        if (userClient.isEmailExists(detailModel.getEmail())) {
            return failed("邮箱已存在!");
        }
        if (userClient.isTelephoneExists(phone)) {
            return failed("电话号码已存在!");
        }
//            系统中已存在该身份证号，系统直接作关联处理(已在demographics再次存到users表中 进行demographic_id字段关联)
//      取家庭地址
        MUser mUser = convertToMUser(userModel);
        mUser.setUserType("Patient");
        mUser.setLoginCode(loginCode);
        mUser.setDemographicId(detailModel.getIdCardNo());
//       账号信息密码设为默认
        mUser.setPassword("123456");

        mUser.setId(detailModel.getUserId());
        mUser.setRealName(detailModel.getName());
        mUser.setIdCardNo(detailModel.getIdCardNo());
        mUser.setGender(detailModel.getGender());
        mUser.setMartialStatus(detailModel.getMartialStatus());
        mUser.setEmail(detailModel.getEmail());
        mUser.setTelephone(phone);

        /*mUser.setProvinceId();
        mUser.setProvinceName();
        mUser.setCityId();
        mUser.setCityName();
        mUser.setAreaId();
        mUser.setAreaName();*/


        mUser = userClient.createUser(objectMapper.writeValueAsString(mUser));


        if (mUser == null) {
            return failed("保存失败!");
        }

        //系统demographics是否存在居民信息校验
        if (!patientClient.isExistIdCardNo(detailModel.getIdCardNo())) {
            //            系统中不存在该身份证号，新增居民

            //新增家庭地址信息
            GeographyModel geographyModel = detailModel.getHomeAddressInfo();
            detailModel.setHomeAddress("");
            if (!geographyModel.nullAddress()) {
                String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                detailModel.setHomeAddress(addressId);
            }
            //新增户籍地址信息
            geographyModel = detailModel.getBirthPlaceInfo();
            detailModel.setBirthPlace("");
            if (!geographyModel.nullAddress()) {
                String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                detailModel.setBirthPlace(addressId);
            }

            //新增工作地址信息
            geographyModel = detailModel.getWorkAddressInfo();
            detailModel.setWorkAddress("");
            if (!geographyModel.nullAddress()) {
                String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                detailModel.setWorkAddress(addressId);
            }

            //新增人口信息
            MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
            info.setBirthday(DateTimeUtil.simpleDateTimeParse(detailModel.getBirthday()));
            info = patientClient.createPatient(objectMapper.writeValueAsString(info));
            if (info == null) {
                return failed("保存失败!");
            }
            detailModel = convertToPatientDetailModel(info);
            return success(detailModel);
        }
        return success("保存成功!");
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

        //头像上传,接收头像保存的远程路径  path
//        String path = null;
//        if (!StringUtils.isEmpty(inputStream)) {
//            String jsonData = inputStream + "," + imageName;
//            path = patientClient.uploadPicture(jsonData);
//        }

        PatientDetailModel detailModel = objectMapper.readValue(patientModelJsonData, PatientDetailModel.class);
//        if (!StringUtils.isEmpty(path)) {
//            detailModel.setPicPath(path);
//        }
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
        if (patientClient.isExistIdCardNo(detailModel.getIdCardNo())) {
            return failed("身份证号已存在!");
        }

        //新增家庭地址信息
        GeographyModel geographyModel = detailModel.getHomeAddressInfo();
        detailModel.setHomeAddress("");
        if (!geographyModel.nullAddress()) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setHomeAddress(addressId);
        }
        //新增户籍地址信息
        geographyModel = detailModel.getBirthPlaceInfo();
        detailModel.setBirthPlace("");
        if (!geographyModel.nullAddress()) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setBirthPlace(addressId);
        }

        //新增工作地址信息
        geographyModel = detailModel.getWorkAddressInfo();
        detailModel.setWorkAddress("");
        if (!geographyModel.nullAddress()) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setWorkAddress(addressId);
        }

        //新增人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        info.setBirthday(DateTimeUtil.simpleDateTimeParse(detailModel.getBirthday()));
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
    @RequestMapping(value = "/population", method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    public Envelop updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData) throws Exception {

        //头像上传,接收头像保存的远程路径  path
//        String path = null;
//        if (!StringUtils.isEmpty(inputStream)) {
//            String jsonData = inputStream + "," + imageName;
//            path = patientClient.uploadPicture(jsonData);
//        }

        PatientDetailModel detailModel = objectMapper.readValue(patientModelJsonData, PatientDetailModel.class);
//        if (!StringUtils.isEmpty(path)) {
//            detailModel.setPicPath(path);
//            detailModel.setLocalPath("");
//        }
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
        if (!geographyModel.nullAddress()) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setHomeAddress(addressId);
        }
        //新增户籍地址信息
        geographyModel = detailModel.getBirthPlaceInfo();
        detailModel.setBirthPlace("");
        if (!geographyModel.nullAddress()) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setBirthPlace(addressId);
        }

        //新增工作地址信息
        geographyModel = detailModel.getWorkAddressInfo();
        detailModel.setWorkAddress("");
        if (!geographyModel.nullAddress()) {
            String addressId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            detailModel.setWorkAddress(addressId);
        }

        //修改人口信息
        MDemographicInfo info = (MDemographicInfo) convertToModel(detailModel, MDemographicInfo.class);
        info.setBirthday(DateTimeUtil.simpleDateTimeParse(detailModel.getBirthday()));
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
     * 手机号码是否已存在校验
     *
     * @param telphoneNumber
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/telphoneNumberIs_exist/{telphone_number}", method = RequestMethod.GET)
    @ApiOperation(value = "判断手机号码是否存在")
    public boolean isExisttelphoneNumber(
            @ApiParam(name = "telphone_number", value = "手机号码", defaultValue = "")
            @PathVariable(value = "telphone_number") String telphoneNumber) throws Exception {
        return patientClient.isExisttelphoneNumber(telphoneNumber);
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
//        detailModel.setBirthday(DateToString(demographicInfo.getBirthday(), AgAdminConstants.DateFormat));
        detailModel.setBirthday(demographicInfo.getBirthday() == null ? "" : DateTimeUtil.simpleDateFormat(demographicInfo.getBirthday()));
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
                detailModel.setHomeAddressFull(mGeography.fullAddress());
                detailModel.setHomeAddressInfo(convertToModel(mGeography, GeographyModel.class));

                detailModel.getHomeAddressInfo().setProvinceId(geographyToCode(detailModel.getHomeAddressInfo().getProvince(), 156));
                detailModel.getHomeAddressInfo().setCityId(geographyToCode(detailModel.getHomeAddressInfo().getCity(), detailModel.getHomeAddressInfo().getProvinceId()));
                detailModel.getHomeAddressInfo().setDistrictId(geographyToCode(detailModel.getHomeAddressInfo().getDistrict(), detailModel.getHomeAddressInfo().getCityId()));
            }
        }
        if (!StringUtils.isEmpty(demographicInfo.getBirthPlace())) {
            //户籍地址
            mGeography = addressClient.getAddressById(demographicInfo.getBirthPlace());
            if (mGeography != null) {
                detailModel.setBirthPlaceFull(mGeography.fullAddress());
                detailModel.setBirthPlaceInfo(convertToModel(mGeography, GeographyModel.class));

                detailModel.getBirthPlaceInfo().setProvinceId(geographyToCode(detailModel.getBirthPlaceInfo().getProvince(), 156));
                detailModel.getBirthPlaceInfo().setCityId(geographyToCode(detailModel.getBirthPlaceInfo().getCity(), detailModel.getBirthPlaceInfo().getProvinceId()));
                detailModel.getBirthPlaceInfo().setDistrictId(geographyToCode(detailModel.getBirthPlaceInfo().getDistrict(), detailModel.getBirthPlaceInfo().getCityId()));
            }
        }
        //工作地址
        if (!StringUtils.isEmpty(demographicInfo.getWorkAddress())) {
            mGeography = addressClient.getAddressById(demographicInfo.getWorkAddress());
            if (mGeography != null) {
                detailModel.setWorkAddressFull(mGeography.fullAddress());
                detailModel.setWorkAddressInfo(convertToModel(mGeography, GeographyModel.class));

                detailModel.getWorkAddressInfo().setProvinceId(geographyToCode(detailModel.getWorkAddressInfo().getProvince(), 156));
                detailModel.getWorkAddressInfo().setCityId(geographyToCode(detailModel.getWorkAddressInfo().getCity(), detailModel.getWorkAddressInfo().getProvinceId()));
                detailModel.getWorkAddressInfo().setDistrictId(geographyToCode(detailModel.getWorkAddressInfo().getDistrict(), detailModel.getWorkAddressInfo().getCityId()));
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

    public int geographyToCode(String name, int code) {
        String[] fields = {"name", "pid"};
        String[] values = {name, String.valueOf(code)};
        List<MGeographyDict> geographyDictList = (List<MGeographyDict>) addressClient.getAddressDict(fields, values);
        if (geographyDictList != null && geographyDictList.size() > 0) {
            return (int)geographyDictList.get(0).getId();
        } else {
            return 0;
        }
    }

    //用户信息 查询（添加查询条件修改）
    @RequestMapping(value = "/populationsByParams", method = RequestMethod.GET)
    @ApiOperation(value = "用户信息 查询（添加查询条件修改）")
    public Envelop searchPatientByParams(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
            @ApiParam(name = "gender", value = "性别", defaultValue = "")
            @RequestParam(value = "gender") String gender,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province") String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city") String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district") String district,
            @ApiParam(name = "searchRegisterTimeStart", value = "注册开始时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeStart") String searchRegisterTimeStart,
            @ApiParam(name = "searchRegisterTimeEnd", value = "注册结束时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeEnd") String searchRegisterTimeEnd,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        try {
            ResponseEntity<List<MDemographicInfo>> responseEntity = patientClient.searchPatientByParams(search, gender, province, city, district, searchRegisterTimeStart, searchRegisterTimeEnd, page, rows);
            List<MDemographicInfo> demographicInfos = responseEntity.getBody();
            List<PatientModel> patients = new ArrayList<>();
            for (MDemographicInfo patientInfo : demographicInfos) {

                PatientModel patient = convertToModel(patientInfo, PatientModel.class);
                //patient.setRegisterTime(DateToString(patientInfo.getRegisterTime(), AgAdminConstants.DateTimeFormat));
                patient.setRegisterTime(patientInfo.getRegisterTime() == null ? "" : DateTimeUtil.simpleDateTimeFormat(patientInfo.getRegisterTime()));
                //获取家庭地址信息
                String homeAddressId = patientInfo.getHomeAddress();
                if (StringUtils.isNotEmpty(homeAddressId)) {
                    MGeography geography = addressClient.getAddressById(homeAddressId);
                    String homeAddress = geography != null ? geography.fullAddress() : "";
                    patient.setHomeAddress(homeAddress);
                }
                //性别
                if (StringUtils.isNotEmpty(patientInfo.getGender())) {
                    MConventionalDict dict = conventionalDictClient.getGender(patientInfo.getGender());
                    patient.setGender(dict == null ? "" : dict.getValue());
                }

                //获取居民账户id
                String fields= "id,demographicId,realName";
                String filters="demographicId="+patientInfo.getIdCardNo();
                String sorts="+demographicId,+realName";
                ResponseEntity<List<MUser>> userEntity = userClient.searchUsers(fields, filters, sorts, 20, 1);
                List<MUser> mUsers = userEntity.getBody();
                if(null!=mUsers&&mUsers.size()>0){
                    for(MUser u:mUsers){
                        patient.setUserId(u.getId());
                    }
                }
                //联系电话
                Map<String, String> telephoneNo;
                String tag = "联系电话";
                try {
                    telephoneNo = objectMapper.readValue(patient.getTelephoneNo(), Map.class);
                } catch (Exception e) {
                    telephoneNo = null;
                }
                if (telephoneNo != null && telephoneNo.containsKey(tag)) {
                    patient.setTelephoneNo(telephoneNo.get(tag));
                } else {
                    patient.setTelephoneNo(null);
                }

                patients.add(patient);
            }

            return getResult(patients, getTotalCount(responseEntity), page, rows);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/populationsByParams2", method = RequestMethod.GET)
    @ApiOperation(value = "用户信息 查询（添加查询条件修改）")
    public Envelop searchPatientByParams2(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
            @ApiParam(name = "gender", value = "性别", defaultValue = "")
            @RequestParam(value = "gender") String gender,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province") String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city") String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district") String district,
            @ApiParam(name = "searchRegisterTimeStart", value = "注册开始时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeStart") String searchRegisterTimeStart,
            @ApiParam(name = "searchRegisterTimeEnd", value = "注册结束时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeEnd") String searchRegisterTimeEnd,
            @ApiParam(name = "districtList", value = "区域", defaultValue = "")
            @RequestParam(value = "districtList") String districtList,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        try {
            ResponseEntity<List<MDemographicInfo>> responseEntity = patientClient.searchPatientByParams2(search, gender, province, city, district, searchRegisterTimeStart, searchRegisterTimeEnd, districtList, page, rows);
            List<MDemographicInfo> demographicInfos = responseEntity.getBody();
            List<PatientModel> patients = new ArrayList<>();
            for (MDemographicInfo patientInfo : demographicInfos) {

                PatientModel patient = convertToModel(patientInfo, PatientModel.class);
                //patient.setRegisterTime(DateToString(patientInfo.getRegisterTime(), AgAdminConstants.DateTimeFormat));
                patient.setRegisterTime(patientInfo.getRegisterTime() == null ? "" : DateTimeUtil.simpleDateTimeFormat(patientInfo.getRegisterTime()));
                //获取家庭地址信息
                String homeAddressId = patientInfo.getHomeAddress();
                if (StringUtils.isNotEmpty(homeAddressId)) {
                    MGeography geography = addressClient.getAddressById(homeAddressId);
                    String homeAddress = geography != null ? geography.fullAddress() : "";
                    patient.setHomeAddress(homeAddress);
                }
                //性别
                if (StringUtils.isNotEmpty(patientInfo.getGender())) {
                    MConventionalDict dict = conventionalDictClient.getGender(patientInfo.getGender());
                    patient.setGender(dict == null ? "" : dict.getValue());
                }

                //获取居民账户id
                String fields= "id,demographicId,realName";
                String filters="demographicId="+patientInfo.getIdCardNo();
                String sorts="+demographicId,+realName";
                ResponseEntity<List<MUser>> userEntity = userClient.searchUsers(fields, filters, sorts, 20, 1);
                List<MUser> mUsers = userEntity.getBody();
                if(null!=mUsers&&mUsers.size()>0){
                    for(MUser u:mUsers){
                        patient.setUserId(u.getId());
                    }
                }
                //联系电话
                Map<String, String> telephoneNo;
                String tag = "联系电话";
                try {
                    telephoneNo = objectMapper.readValue(patient.getTelephoneNo(), Map.class);
                } catch (Exception e) {
                    telephoneNo = null;
                }
                if (telephoneNo != null && telephoneNo.containsKey(tag)) {
                    patient.setTelephoneNo(telephoneNo.get(tag));
                } else {
                    patient.setTelephoneNo(null);
                }

                patients.add(patient);
            }

            return getResult(patients, getTotalCount(responseEntity), page, rows);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/PatientCardByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "用户获取个人卡列表")
    public Envelop cardList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,cardType,cardNo,releaseOrg,validityDateBegin,validityDateEnd,status")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+createDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "999")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        ResponseEntity<List<UserCards>> responseEntity = patientCardsClient.getUserCardList(fields, filters, sorts, size, page);
        List<UserCards> list = responseEntity.getBody();
        //系统字典-卡类别-10
        String code = "";
        ListResult mConventionalDict = dictEntryClient.GetAlldictionariesByDictId();
        List<Map<String, Object>> mConventionalDictList = mConventionalDict.getDetailModelList();
        Map<String, String> cardMap = new HashedMap();
        if (null != mConventionalDictList && mConventionalDictList.size() > 0) {
            for (int i = 0; i < mConventionalDictList.size(); i++) {
//                Object obj = mConventionalDictList.get(i);
                cardMap.put(mConventionalDictList.get(i).get("sort").toString(), mConventionalDictList.get(i).get("value").toString());
            }
        }
        int totalCout = 0;
        List<UserCards> lt = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (UserCards userCard : list) {
                String cardTypeName = cardMap.get(userCard.getCardType());
                if (null != cardMap.get(userCard.getCardType())) {
                    userCard.setCardType(cardMap.get(userCard.getCardType()));
                    lt.add(userCard);
                }
            }
            totalCout = list.size();
        }
        return getResult(lt, totalCout, page, size);
    }


    private List convertCardModels(List<Map<String, Object>> userCards) {
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=43", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for (MDictionaryEntry entry : statusDicts) {
            statusMap.put(entry.getCode(), entry.getValue());
        }
        for (Map<String, Object> info : userCards) {
            info.put("statusName", statusMap.get(info.get("status")));
        }

        return userCards;
    }

    //根据就诊卡id删除就诊卡(改变就诊卡的审核状态)
    @RequestMapping(value = "/deletePatientCardByCardId", method = RequestMethod.POST)
    @ApiOperation(value = "根据就诊卡id删除就诊卡(改变就诊卡的审核状态)")
    public Envelop cardVerifyManager(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id,
            @ApiParam(name = "status", value = "status", defaultValue = "")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam(value = "auditor", required = false) String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(value = "auditReason", required = false) String auditReason) throws Exception {

        Result result = patientCardsClient.cardVerifyManager(id, status, auditor, auditReason);
        if (result.getCode() == 200) {
            return success("删除成功！");
        } else {
            return failed("删除失败！");
        }
    }

    @RequestMapping(value = "/roles/platformAllAppRolesTree", method = RequestMethod.GET)
    @ApiOperation(value = "获取平台应用角色组列表,tree")
    public Envelop getPlatformAppRolesTree(
            @ApiParam(name = "type", value = "角色组类型，应用角色/用户角色字典值")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source_type", value = "平台应用sourceType字典值")
            @RequestParam(value = "source_type") String sourceType,
            @ApiParam(name = "user_id",value = "用户id")
            @RequestParam(value = "user_id") String userId) {
//        if(org.apache.commons.lang.StringUtils.isEmpty(type)){
//            return failed("角色组类型不能为空！");
//        }
//        if(org.apache.commons.lang.StringUtils.isEmpty(sourceType)){
//            return failed("平台应用类型不能为空！！");
//        }
        sourceType = "";
        Envelop envelop = new Envelop();
        //平台应用-应用表中source_type为1
        Collection<MApp> mApps = appClient.getAppsNoPage(sourceType);
        List<PlatformAppRolesTreeModel> appRolesTreeModelList = new ArrayList<>();
        Map<String,String> appRolesTreeModelMap=new HashedMap();
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId="+userId);
        Map<String,String> roleUserModel=new HashedMap();
        for (MRoleUser model : mRoleUsers){
            roleUserModel.put(String.valueOf(model.getRoleId()),String.valueOf(model.getRoleId()));
        }
        //平台应用-角色组对象模型列表

        for (MApp mApp : mApps) {
            boolean checkFlag=false;
            Collection<MRoles> mRoles = rolesClient.searchRolesNoPaging("appId=" + mApp.getId());
            List<PlatformAppRolesTreeModel> roleTreeModelList = new ArrayList<>();
            for (MRoles m : mRoles) {
                PlatformAppRolesTreeModel modelTree = new PlatformAppRolesTreeModel();
                modelTree.setId(m.getId() + "");
                modelTree.setName(m.getName());
                modelTree.setType("1");
                modelTree.setPid(mApp.getId());
                modelTree.setChildren(null);
                if(null!=roleUserModel.get(String.valueOf(m.getId()))){
                    modelTree.setIschecked(true);
                    checkFlag=true;
                }else{
                    modelTree.setIschecked(false);
                }
                roleTreeModelList.add(modelTree);
                appRolesTreeModelMap.put(String.valueOf(m.getId()),m.getName());
            }
            if (roleTreeModelList.size() == 0) {
                continue;
            }
            PlatformAppRolesTreeModel app = new PlatformAppRolesTreeModel();
            app.setId(mApp.getId());
            app.setName(mApp.getName());
            app.setType(String.valueOf(mApp.getSourceType()));
            app.setPid(null);
            app.setChildren(roleTreeModelList);
            app.setIschecked(checkFlag);
            appRolesTreeModelList.add(app);

        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appRolesTreeModelList);

        List<MRoleUser> mRoleUserList = new ArrayList<>();
        MRoleUser mr;
        for (MRoleUser m : mRoleUsers){
            if(null!=appRolesTreeModelMap.get(String.valueOf(m.getRoleId()))){
                mr=new MRoleUser();
                mr.setRoleId(m.getRoleId());
                mr.setRoleName(appRolesTreeModelMap.get(String.valueOf(m.getRoleId())));
                MRoles roles = rolesClient.getRolesById(m.getRoleId());
                Collection<MApp> appCollection = appClient.getAppsNoPage("id=" + roles.getAppId());
                for(MApp app : appCollection){
                    mr.setAppName(app.getName());
                }
                mRoleUserList.add(mr);
            }
        }
        envelop.setObj(mRoleUserList);
        return envelop;
    }

    /**
     * 居民信息-角色授权-角色组保存
     *
     * @return
     */
    @RequestMapping(value = "/appUserRolesSave",method = RequestMethod.PUT)
    @ApiOperation(value = "居民信息-角色授权-角色组保存")
    public Envelop saveRoleUser(
            @ApiParam(name = "userId", value = "居民账户id", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        RoleUserModel roleUserModel = new RoleUserModel();
        jsonData = URLDecoder.decode(jsonData);
        String[] newJsonData = jsonData.split("&");
        String id = patientClient.saveRoleUser(userId, newJsonData[0]);

        if (id != null) {
            envelop.setSuccessFlg(true);
            roleUserModel.setId(Long.parseLong(id));
        } else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("居民角色授权失败！");
        }
        return envelop;
    }



    public MUser convertToMUser(UserDetailModel detailModel) {
        if (detailModel == null) {
            return null;
        }
        MUser mUser = convertToModel(detailModel, MUser.class);
        mUser.setCreateDate(StringToDate(detailModel.getCreateDate(), AgAdminConstants.DateTimeFormat));
        mUser.setLastLoginTime(StringToDate(detailModel.getLastLoginTime(), AgAdminConstants.DateTimeFormat));

        return mUser;
    }


}
