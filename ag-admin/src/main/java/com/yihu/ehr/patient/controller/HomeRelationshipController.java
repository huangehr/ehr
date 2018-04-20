package com.yihu.ehr.patient.controller;

import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.patient.HomeGroupModel;
import com.yihu.ehr.agModel.patient.HomeRelationshipModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.family.MFamilies;
import com.yihu.ehr.model.family.MMembers;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.patient.service.FamiliesClient;
import com.yihu.ehr.patient.service.MembersClient;
import com.yihu.ehr.patient.service.PatientClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by AndyCai on 2016/4/20.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "home_relationship", description = "家庭关系管理", tags = {"人口管理-家庭关系管理"})
public class HomeRelationshipController extends BaseController {

    @Autowired
    private MembersClient membersClient;

    @Autowired
    private FamiliesClient familiesClient;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

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

            // 根据查询条件查询家庭组ID
            List<MFamilies> mFamilies = (List<MFamilies>)familiesClient.searchFamilies(fields, filters, sorts, size, page).getBody();
            if(mFamilies==null || mFamilies.size()==0)
            {
                return getResult(null, 0, page, size);
            }
            //根据查询所得的家庭组ID查询家庭成员
            filters="familyId="+mFamilies.get(0).getId();
            ResponseEntity<Collection<MMembers>> membersResponseEntity = membersClient.searchMembers(fields, filters, sorts, size, page);
            List<MMembers> mMemberses = (List<MMembers>)membersResponseEntity.getBody();
            List<HomeRelationshipModel> relationshipModels = new ArrayList<>();
            for(MMembers mMembers :mMemberses )
            {
                HomeRelationshipModel relationshipModel = convertToHomeRelationshipModel(mMembers);
                if(StringUtils.isNotBlank(mMembers.getFamilyRelation())){
                  MConventionalDict dict = conventionalDictClient.getFamilyRelationship(mMembers.getFamilyRelation());
                  if(dict!=null){
                    relationshipModel.setRelationShipName(dict.getValue());
                  }
                }
                relationshipModels.add(relationshipModel);
            }

            return getResult(relationshipModels, getTotalCount(membersResponseEntity), page, size);
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

            //根据查询条件获取家庭组ID
            List<MMembers> mMemberses = (List<MMembers>)membersClient.searchMembers(fields, filters, sorts, size, page).getBody();
            if(mMemberses==null || mMemberses.size()==0)
            {
                return getResult(null, 0, page, size);
            }
            String id="";
            String idCardNo = mMemberses.get(0).getIdCardNo();
            HashMap<String,String> familyRelationMap = new HashMap<String,String>();
            for(MMembers mMembers : mMemberses)
            {
                familyRelationMap.put(mMembers.getFamilyId(),mMembers.getFamilyRelation());
                id+=mMembers.getFamilyId()+",";
            }
            id = trimEnd(id,",");
            //根据家庭组ID获取家庭组信息
            filters="id="+id;

            ResponseEntity<Collection<MFamilies>> familyResponseEntity = familiesClient.searchFamilies(fields, filters, sorts, size, page);
            List<MFamilies> mFamiliesList = (List<MFamilies>)familyResponseEntity.getBody();
            List<HomeGroupModel> homeGroupModels = new ArrayList<>();
            for(MFamilies mFamilies:mFamiliesList)
            {
                HomeGroupModel groupModel = convertToHomeGroupModel(mFamilies,idCardNo);
                if(StringUtils.isNotBlank(familyRelationMap.get(mFamilies.getId()))){
                    MConventionalDict dict = conventionalDictClient.getFamilyRelationship(familyRelationMap.get(mFamilies.getId()));
                    if(dict!=null){
                        groupModel.setRelationshipName(dict.getValue());
                    }
                }
                homeGroupModels.add(groupModel);
            }

            return getResult(homeGroupModels, getTotalCount(familyResponseEntity), page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    public HomeRelationshipModel convertToHomeRelationshipModel(MMembers mMembers)
    {
        HomeRelationshipModel relationshipModel = new HomeRelationshipModel();

        relationshipModel.setId(mMembers.getId());
        if(StringUtils.isNotEmpty(mMembers.getIdCardNo()))
        {
            MDemographicInfo info =patientClient.getPatient(mMembers.getIdCardNo());
            relationshipModel.setAge(info==null?"0":String.valueOf(getAgeByBirthday(info.getBirthday())));
            relationshipModel.setName(info==null?"0":info.getName());
        }

//        relationshipModel.setRelationTime(DateToString(mMembers.getCreateDate(), AgAdminConstants.DateFormat));
        relationshipModel.setRelationTime(DateTimeUtil.simpleDateTimeFormat(mMembers.getCreateDate()));
        //TODO：获取关系字典信息


        return relationshipModel;
    }

    public HomeGroupModel convertToHomeGroupModel(MFamilies mFamilies,String idCardNo)
    {
        HomeGroupModel groupModel = new HomeGroupModel();

        groupModel.setId(mFamilies.getId());
        if(StringUtils.isNotEmpty(mFamilies.getHouseholderIdCardNo()))
        {
            MDemographicInfo info =patientClient.getPatient(mFamilies.getHouseholderIdCardNo());
            groupModel.setName(info==null?"0":info.getName());
        }
//        groupModel.setCreateTime(DateToString(mFamilies.getCreateDate(), AgAdminConstants.DateFormat));
        groupModel.setCreateTime(DateTimeUtil.simpleDateTimeFormat(mFamilies.getCreateDate()));
        String filters="familyId="+mFamilies.getId()+";idCardNo="+idCardNo;
        List<MMembers> mMemberses = (List<MMembers>)membersClient.searchMembers("", filters, "", 15, 1).getBody();
        if(mMemberses!=null && mMemberses.size()>0)
        {
            //获取关系ID
           // String relationId = mMemberses.get(0).getFamilyRelation();
            //TODO：获取关系字典信息
        }

        return groupModel;
    }
}
