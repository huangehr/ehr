package com.yihu.ehr.api.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.authorization.AuthorizationsEndPoint;
import com.yihu.ehr.api.model.MToken;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.GeographyClient;
import com.yihu.ehr.feign.PatientClient;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.service.oauth2.EhrUserDetailsService;
import com.yihu.ehr.util.IdCardValidator;
import com.yihu.ehr.util.encode.HashUtil;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.web.DateFormatter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 历史遗留接口，以rest开头。这些接口只被集成开放平台使用，目前先整合到这边来。待所有版本均升级好之后，可以去掉此接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 10:13
 */
@RestController
@RequestMapping(value = "/rest/v1.0")
@Api(protocols = "https", value = "legacy", description = "历史遗留接口，这些接口只被集成开放平台使用。")
public class LegacyEndPoint {

    @Autowired
    private GeographyClient geographyClient;
    @Autowired
    private PatientClient patientClient;


    @ApiOperation(value = "下载标准版本", response = String.class)
    @RequestMapping(value = "/adapter-dispatcher/versionplan", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String getVersion() {
        return null;
    }

    @ApiOperation(value = "下载标准版本列表", response = String.class)
    @RequestMapping(value = "/adapter-dispatcher/allSchemaMappingPlan", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String getAllVerions() {
        return null;
    }

    @ApiOperation(value = "机构数据元", response = String.class)
    @RequestMapping(value = "/adapter-dispatcher/schema", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String downloadOrgMeta() {
        return null;
    }

    @ApiOperation(value = "标准映射", response = String.class)
    @RequestMapping(value = "adapter-dispatcher/schemaMappingPlan", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String downloadMapper() {
        return null;
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
     *
     * @return map
     */
    @ApiOperation(value = "注册病人", notes = "根据病人的身份证号及其他病人信息在健康档案平台中注册病人")
    @RequestMapping(value = "/patient/registration", method = RequestMethod.POST)
    public Object patientRegister(
            @ApiParam(name = "user_info", value = "用户名")
            @RequestParam(value = "user_info", required = true) String userInfo) throws IOException, ParseException {

        Map<String, String> telMap = new HashMap<>();

        Map<String, Object> fields = new ObjectMapper().readValue(userInfo, Map.class);
        List<Map<String, Object>> patients = (List<Map<String, Object>>) fields.get("data");
        Map<String, Object> patient = patients.get(0);

        String inner_version = (String) fields.get("inner_version");
//        String patient_id = (String) fields.get("patient_id");
//        String event_no = (String) fields.get("event_no");
//        String event_time = (String) fields.get("event_time");
//        String org_code = (String) fields.get("org_code");
//        String ds_code = (String) fields.get("code");

        String userName = "";   //本人姓名
        String idCardNo = "";   //身份证件号码
        String birthday = "";   //出生日期
        String gender = "";   //性别代码
        String nation = "";   //民族
        String martialStatus = "";   //婚姻状况代码
        String nativePlace = "";   //籍贯

        MGeography homeAddress = new MGeography();
        String homeProvince = "";   //现住地址-省（自治区、直辖）
        String homeCity = "";   //现住地址-市（地区、州）
        String homeDistrict = "";   //现住地址-县（区）
        String homeTown = "";   //现住地址-乡（镇、街道办事）
        String homeStreet = "";   //现住地址-门牌号码

        MGeography birthAddress = new MGeography();
        String birthProvince = "";  //户籍地址-省（自治区、直辖）
        String birthCity = "";  //户籍地址-市（地区、州）
        String birthDistrict = "";  //户籍地址-县（区）
        String birthTown = "";  //户籍地址-乡（镇、街道办事）
        String birthStreet = "";  //户籍地址-门牌号码
        String birthExtra = ""; //户籍地址--详细信息（未结构化的完整地址暂存这里）

        String telphoneJson = "";   //电话
        String email = "";   //电子邮件地址

        //以下代码为临时对应方案
        if (inner_version.equals("000000000000")) {
            userName = (String) patient.get("HDSA00_01_009");
            idCardNo = (String) patient.get("HDSA00_01_017");
            birthday = (String) patient.get("HDSA00_01_012");
            gender = (String) patient.get("HDSA00_01_011");
            nation = (String) patient.get("HDSA00_01_014");
            martialStatus = (String) patient.get("HDSA00_01_015");
            nativePlace = (String) patient.get("HDSA00_01_022") + (String) patient.get("HDSA00_01_023") + (String) patient.get("HDSA00_01_024");

            homeProvince = (String) patient.get("HDSA00_01_029");
            homeCity = (String) patient.get("HDSA00_01_030");
            homeDistrict = (String) patient.get("HDSA00_01_031");
            homeTown = (String) patient.get("HDSA00_01_032");
            homeStreet = (String) patient.get("HDSA00_01_033");

            birthProvince = (String) patient.get("HDSA00_01_022");
            birthCity = (String) patient.get("HDSA00_01_023");
            birthDistrict = (String) patient.get("HDSA00_01_024");
            birthTown = (String) patient.get("HDSA00_01_025");
            birthStreet = (String) patient.get("HDSA00_01_027");

            telphoneJson = (String) patient.get("HDSA00_01_019");
            email = (String) patient.get("HDSA00_01_021");
        } else {
            userName = (String) patient.get("HDSD00_01_002");
            idCardNo = (String) patient.get("HDSA00_01_017");
            birthday = (String) patient.get("HDSA00_01_012");
            gender = (String) patient.get("HDSA00_01_011");
            nation = (String) patient.get("HDSA00_01_014");
            martialStatus = (String) patient.get("HDSD00_01_017");
            nativePlace = (String) patient.get("HDSA00_11_051");
            telphoneJson = (String) patient.get("HDSD00_01_008");
            email = (String) patient.get("HDSA00_01_021");
            birthExtra = (String) patient.get("JDSA00_01_000");//地址无法解析情况，是否直接存入详细地址栏？
        }

        telMap.put("联系电话", telphoneJson);

        if (!StringUtils.isEmpty(birthProvince)) {
            birthAddress = new MGeography(birthProvince, birthCity, birthDistrict, birthTown, birthStreet);
        }
        if (!StringUtils.isEmpty(homeProvince)) {
            homeAddress = new MGeography(homeProvince, homeCity, homeDistrict, homeTown, homeStreet);
        }
        //完整的户籍地址
        if (!StringUtils.isEmpty(birthExtra)) {
            birthAddress.setExtra(birthExtra);
        }

        //缺少身份证号
        if (idCardNo == null) {
            throw new ApiException(ErrorCode.MissParameter);
        }
        String errorInfo = IdCardValidator.doValidate(idCardNo);
        if (!StringUtils.isEmpty(errorInfo)) {
            throw new ApiException(ErrorCode.InvalidIdentityNo, errorInfo);
        }

        if (patientClient.isRegistered(idCardNo)) {
            return true;
        } else {
            Pattern pattern = Pattern.compile("([0-9]{17}([0-9]|X))|([0-9]{15})");
            Matcher matcher = pattern.matcher(idCardNo);
            if (!matcher.matches()) {
                throw new ApiException(ErrorCode.InvalidParameter);
            }

            MDemographicInfo demoInfo = new MDemographicInfo();
            demoInfo.setName(userName);
            if (!StringUtils.isEmpty(martialStatus)) {
                demoInfo.setMartialStatus(martialStatus);
            }
            if (!StringUtils.isEmpty(gender)) {
                demoInfo.setGender(gender);
            }

            if (!StringUtils.isEmpty(nation)) {
                demoInfo.setNation(nation);
            }

            ObjectMapper objectMaper = new ObjectMapper();
            String homeAddressJsonData = objectMaper.writeValueAsString(homeAddress);
            String birthPlacejsonData = objectMaper.writeValueAsString(birthAddress);

            demoInfo.setBirthday(DateFormatter.simpleDateParse(birthday));
            demoInfo.setNativePlace(nativePlace);
            demoInfo.setTelphoneNo(telMap.get(0));


            demoInfo.setHomeAddress(geographyClient.saveAddress(homeAddressJsonData));
            demoInfo.setBirthPlace(geographyClient.saveAddress(birthPlacejsonData));
            demoInfo.setWorkAddress(null);

            demoInfo.setEmail(email);

            String demoInfoJsonData = objectMaper.writeValueAsString(demoInfo);
            patientClient.createPatient(demoInfoJsonData);
            return true;
            //return succeedWithMessage("Patient register success.");
        }
    }


    @ApiOperation(value = "上传档案包", response = String.class)
    @RequestMapping(value = "/json_package", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String uploadPackage() {
        return null;
    }

    @ApiOperation(value = "公钥", response = String.class)
    @RequestMapping(value = "/user_key", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String getUserKey() {
        return null;
    }

    @Autowired
    private AuthorizationsEndPoint authEndPoint;

    @Autowired
    EhrUserDetailsService userDetailsService;

    @ApiOperation(value = "临时Token", response = String.class)
    @RequestMapping(value = "/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public Object getUserTempToken(@ApiParam(required = true, name = "user_name", value = "用户名")
                                   @RequestParam(value = "user_name", required = true) String userName,
                                   @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
                                   @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
                                   @ApiParam(required = true, name = "app_id", value = "APP ID")
                                   @RequestParam(value = "app_id", required = true) String appId,
                                   @ApiParam(required = true, name = "app_secret", value = "APP 密码")
                                   @RequestParam(value = "app_secret", required = true) String appSecret,
                                   HttpServletRequest request) throws Exception

    {

        String privateKey = ""; // 此处要获取用户RSA密钥
        Key priKey = RSA.genPrivateKey(privateKey);
        String password = RSA.decrypt(rsaPWD, priKey);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (userDetails == null || !HashUtil.hashStr(password).equals(userDetails.getPassword())){
            throw new InvalidParameterException("用户名或密码错误");
        }

        MToken token = authEndPoint.createTempToken(userDetails);
        if (token == null) return null;

        return token.getToken();
    }
}
