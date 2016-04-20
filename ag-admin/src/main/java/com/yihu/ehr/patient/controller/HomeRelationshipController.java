package com.yihu.ehr.patient.controller;

import com.yihu.ehr.agModel.patient.HomeGroupModel;
import com.yihu.ehr.agModel.patient.HomeRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/4/20.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "home_relationship", description = "家庭关系管理", tags = {"家庭庴管理"})
public class HomeRelationshipController extends BaseController{


    @RequestMapping(value = "/home_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    public Envelop getHomeRelationship(
            @ApiParam(name = "id", value = "患者Id", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        try {
            ResponseEntity<List<MDemographicInfo>> responseEntity = null;
            //List<MDemographicInfo> demographicInfos = responseEntity.getBody();
            List<HomeRelationshipModel> relationshipModels = new ArrayList<>();
//            for (HomeRelationshipModel relationshipModel : demographicInfos) {
//
//                PatientModel patient = convertToModel(patientInfo, PatientModel.class);
//                patient.setRegisterTime(DateToString(patientInfo.getRegisterTime(), AgAdminConstants.DateTimeFormat));
//                //获取家庭地址信息
//                String homeAddressId = patientInfo.getHomeAddress();
//                if (StringUtils.isNotEmpty(homeAddressId)) {
//                    MGeography geography = addressClient.getAddressById(homeAddressId);
//                    String homeAddress = geography != null ? geography.fullAddress() : "";
//                    patient.setHomeAddress(homeAddress);
//                }
//                //性别
//                if (StringUtils.isNotEmpty(patientInfo.getGender())) {
//                    MConventionalDict dict = conventionalDictClient.getGender(patientInfo.getGender());
//                    patient.setGender(dict == null ? "" : dict.getValue());
//                }
//                //联系电话
//                Map<String, String> telephoneNo;
//                String tag = "联系电话";
//                try {
//                    telephoneNo = objectMapper.readValue(patient.getTelephoneNo(), Map.class);
//                } catch (Exception e) {
//                    telephoneNo = null;
//                }
//                if (telephoneNo != null && telephoneNo.containsKey(tag)) {
//                    patient.setTelephoneNo(telephoneNo.get(tag));
//                } else {
//                    patient.setTelephoneNo(null);
//                }
//
//                relationshipModels.add(patient);
//            }
            //getTotalCount(responseEntity)
            return getResult(relationshipModels,0 , page, rows);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/home_group", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    public Envelop getHomeGroup(
            @ApiParam(name = "id", value = "患者Id", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        try {
            //ResponseEntity<List<MDemographicInfo>> responseEntity = null;
            //List<MDemographicInfo> demographicInfos = responseEntity.getBody();
            List<HomeGroupModel> homeGroupModels = new ArrayList<>();
//            for (HomeRelationshipModel relationshipModel : demographicInfos) {
//
//                PatientModel patient = convertToModel(patientInfo, PatientModel.class);
//                patient.setRegisterTime(DateToString(patientInfo.getRegisterTime(), AgAdminConstants.DateTimeFormat));
//                //获取家庭地址信息
//                String homeAddressId = patientInfo.getHomeAddress();
//                if (StringUtils.isNotEmpty(homeAddressId)) {
//                    MGeography geography = addressClient.getAddressById(homeAddressId);
//                    String homeAddress = geography != null ? geography.fullAddress() : "";
//                    patient.setHomeAddress(homeAddress);
//                }
//                //性别
//                if (StringUtils.isNotEmpty(patientInfo.getGender())) {
//                    MConventionalDict dict = conventionalDictClient.getGender(patientInfo.getGender());
//                    patient.setGender(dict == null ? "" : dict.getValue());
//                }
//                //联系电话
//                Map<String, String> telephoneNo;
//                String tag = "联系电话";
//                try {
//                    telephoneNo = objectMapper.readValue(patient.getTelephoneNo(), Map.class);
//                } catch (Exception e) {
//                    telephoneNo = null;
//                }
//                if (telephoneNo != null && telephoneNo.containsKey(tag)) {
//                    patient.setTelephoneNo(telephoneNo.get(tag));
//                } else {
//                    patient.setTelephoneNo(null);
//                }
//
//                relationshipModels.add(patient);
//            }
            //getTotalCount(responseEntity)
            return getResult(homeGroupModels,0 , page, rows);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
