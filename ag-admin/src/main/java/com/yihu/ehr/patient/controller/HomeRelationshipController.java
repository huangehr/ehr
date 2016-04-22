package com.yihu.ehr.patient.controller;

import com.yihu.ehr.agModel.patient.HomeGroupModel;
import com.yihu.ehr.agModel.patient.HomeRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.patient.service.FamiliesClient;
import com.yihu.ehr.patient.service.MembersClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/4/20.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "home_relationship", description = "家庭关系管理", tags = {"家庭庴管理"})
public class HomeRelationshipController extends BaseController {

    @Autowired
    private MembersClient membersClient;

    @Autowired
    private FamiliesClient familiesClient;

    @RequestMapping(value = "/home_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查家庭关系")
    public Envelop getHomeRelationship(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {

            //TODO: 根据查询条件查询家庭组ID
            ResponseEntity<Collection<Object>> familiesResponseEntity = familiesClient.searchFamilies(fields, filters, sorts, size, page);

            //TODO:根据查询所得的家庭组ID查询家庭成员
            filters="familyId="+"";
            ResponseEntity<Collection<Object>> MemgersResponseEntity = membersClient.searchMembers(fields, filters, sorts, size, page);


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
            return getResult(relationshipModels, 0, page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/home_group", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询家庭群组")
    public Envelop getHomeGroup(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {

            //TODO：根据查询条件获取家庭组ID
            ResponseEntity<Collection<Object>> membersResponseEntity = membersClient.searchMembers(fields, filters, sorts, size, page);

            //TODO:根据家庭组ID获取家庭组信息
            filters="id="+"";

            ResponseEntity<Collection<Object>> familyResponseEntity = familiesClient.searchFamilies(fields, filters, sorts, size, page);
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
            return getResult(homeGroupModels, 0, page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    public HomeRelationshipModel ConvertToHomeRelationshipModel()
    {
        return null;
    }

    public HomeGroupModel ConvertToHomeGroupModel()
    {
        return null;
    }
}
