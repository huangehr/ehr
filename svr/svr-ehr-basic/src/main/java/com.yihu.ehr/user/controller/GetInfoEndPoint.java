package com.yihu.ehr.user.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.geography.service.GeographyDictService;
import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.saas.service.OrgSaasService;
import com.yihu.ehr.user.service.GetInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "userInfo", description = "获取用户可查询的机构&地区", tags = {"获取用户可查询的机构&地区"})
public class GetInfoEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgMemberRelationService orgMemberRelationService;
    @Autowired
    private GeographyService geographyService;
    @Autowired
    private OrgSaasService orgSaasService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private GeographyDictService geographyDictService;
    @Autowired
    private GetInfoService getInfoService;

    @RequestMapping(value = "/userInfo/getOrgCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构代码")
    List<String> getOrgCode(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        List<String> orgCodeList = getOrgCodesList(userId);
        return orgCodeList;
    }

    @RequestMapping(value = "/getOrgByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构名称")
    List<String> getOrgByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        return getOrgCodes(userId);
    }

    @RequestMapping(value = "/userInfo/getUserDistrictCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的地区Code列表")
    List<String> getUserDistrictCode(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        return getSaasCodeList(userId);
    }

    @RequestMapping(value = "/getDistrictByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的地区名称列表")
    List<String> getDistrictByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        return getDistrict(userId);
    }

    @RequestMapping(value = "/getUserIdList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构下所对应的userId列表")
    List<String> getUserIdList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        return getUserIdInMember(userId);
    }

    @RequestMapping(value = "/getDistrictList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构所对应的区域列表")
    List<String> getDistrictList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        return getGeographyId(userId);
    }

    /**
     * 获取当前用户可查询的地区名称列表
     * @param userId
     * @return
     */
    public List<String> getDistrict(String userId) {
        List<String> orgCodes = orgMemberRelationService.getOrgCodes(userId);
        List<String> saasNameList = new ArrayList<>();
        for (String s : orgCodes) {
            List<String> saasName = orgSaasService.getSaasName(s, "1");
            saasNameList.addAll(saasName);
        }
        return saasNameList;
    }

    /**
     * 获取当前用户可查询的区域ID即saasCode
     * @param userId
     * @return
     */
    public List<String> getSaasCodeList(String userId) {
        List<String> saasCodeList = new ArrayList<>();
        List<String> orgCodes = orgMemberRelationService.getOrgCodes(userId);
        for (String s : orgCodes) {
            List<String> saasCode = orgSaasService.getSaasCode(s, "1");
            saasCodeList.addAll(saasCode);
        }
        return saasCodeList;
    }

    /**
     * 获取当前用户可查询的机构名称列表
     * @param userId
     * @return
     */
    public List<String> getOrgCodes(String userId) {
        List<String> saasNameList = new ArrayList<>();
        List<String> orgCodes = orgMemberRelationService.getOrgCodes(userId);
        for (String s : orgCodes) {
            List<String> saasName = orgSaasService.getSaasName(s, "2");
            saasNameList.addAll(saasName);
        }
        return saasNameList;
    }

    /**
     * 获取当前用户可查询的机构Code列表
     * @param userId
     * @return
     */
    public List<String> getOrgCodesList(String userId) {
        List<String> saasCodeList = new ArrayList<>();
        List<String> orgCodes = orgMemberRelationService.getOrgCodes(userId);
        for (String s : orgCodes) {
            List<String> saasCode = orgSaasService.getSaasCode(s, "2");
            saasCodeList.addAll(saasCode);
        }
        return saasCodeList;
    }


    /**
     * 获取当前用户可查询的地区在addresses表中的地址ID
     * @param userId
     * @return
     */
    public List<String> getGeographyId(String userId) {
        List<String> districtList = getDistrict(userId);
        if (null != districtList && districtList.size() > 0) {
            String[] strings = new String[districtList.size()];
            String[] district = districtList.toArray(strings);
            List<String> geographyIdList = geographyService.getIdByDistrict(districtList);
            return geographyIdList;
        }
        return null;
    }

    /**
     * 获取当前用户可查询的机构编码orgCode列表
     * @param userId
     * @return
     */
    public List<String> getOrgCodeByName(String userId) {
        List<String> orgCodeList = getOrgCodes(userId);
        if (null != orgCodeList && orgCodeList.size() > 0) {
            List<String> orgList = orgService.getOrgCodeByFullName(orgCodeList);
            return orgList;
        }
        return null;
    }

    /**
     *获取当前用户可查询的机构所对应的人员列表
     * @param userId
     * @return
     */
    public List<String> getUserIdInMember(String userId) {
        List<String> orgCodeList = getOrgCodeByName(userId);
        if (null != orgCodeList && orgCodeList.size() > 0) {
            List<String> orgIdList = orgService.getOrgIdByOrgCodeList(orgCodeList);
            if (null != orgIdList && orgIdList.size() > 0) {
                return orgMemberRelationService.getUserIdByOrgId(orgIdList);
            }
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.GetInfo.GetAppIdsByUserId, method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户所在的角色组所对应的应用列表")
    public String getAppIdsByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        String appsId = getInfoService.getAppsId(userId);
        return appsId;
    }

    @RequestMapping(value = ServiceApi.GetInfo.GetIdCardNoByOrgCode, method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户所在的角色组下机构所对应的人员身份证号")
    public String getIdCardNoByOrgCode(
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {
        String idCardNo = getInfoService.getIdCardNo(orgCode);
        return idCardNo;
    }
}
