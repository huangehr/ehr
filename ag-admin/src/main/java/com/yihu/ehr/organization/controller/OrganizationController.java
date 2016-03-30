package com.yihu.ehr.organization.controller;

import com.yihu.ehr.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.agModel.org.OrgModel;
import com.yihu.ehr.security.service.SecurityClient;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.operator.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "organization", description = "机构信息管理接口，用于机构信息管理", tags = {"机构信息管理接口"})
public class OrganizationController extends BaseController {

    @Autowired
    private OrganizationClient orgClient;

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @Autowired
    AddressClient addressClient;

    @Autowired
    SecurityClient securityClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    public Envelop searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {
            List<OrgModel> orgModelList = new ArrayList<>();
            ResponseEntity<List<MOrganization>> responseEntity = orgClient.searchOrgs(fields, filters, sorts, size, page);
            List<MOrganization> organizations = responseEntity.getBody();
            for (MOrganization mOrg : organizations) {
                OrgModel orgModel = convertToOrgModel(mOrg);
                orgModelList.add(orgModel);
            }
            int totalCount = getTotalCount(responseEntity);
            return getResult(orgModelList, totalCount, page, size);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     * 将微服务返回的结果转化为前端OrgModel模型
     *
     * @param mOrg
     * @return
     */
    public OrgModel convertToOrgModel(MOrganization mOrg) {

        if(mOrg==null)
        {
            return null;
        }

        OrgModel orgModel = convertToModel(mOrg, OrgModel.class);
        // 获取机构类别字典
        if(!StringUtils.isEmpty(mOrg.getOrgType())){
            MConventionalDict orgTypeDict = conDictEntryClient.getOrgType(mOrg.getOrgType());
            orgModel.setOrgTypeName(orgTypeDict == null ? "" : orgTypeDict.getValue());
        }
        else{
            orgModel.setOrgTypeName("");
        }
        // 获取机构地址信息
        String locationStrName = addressClient.getCanonicalAddress(mOrg.getLocation());
        orgModel.setLocationStrName(locationStrName);
        //获取机构接入方式
        if(!StringUtils.isEmpty(mOrg.getSettledWay())){
            MConventionalDict settledWayDict = conDictEntryClient.getSettledWay(mOrg.getSettledWay());
            orgModel.setSettledWayName(settledWayDict == null ? "" : settledWayDict.getValue());
        }
        else{
            orgModel.setSettledWayName("");
        }

        // 判断机构状态（是否已激活）
        orgModel.setActivityFlagName(mOrg.getActivityFlag() == 1 ? "是" : "否");
        //创建时间转化
        orgModel.setCreateDate(DateUtil.formatDate(mOrg.getCreateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));

        return orgModel;
    }



    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据机构代码删除机构")
    public Envelop deleteOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode){
        try {
            if (StringUtils.isEmpty(orgCode)) {
                return failed("机构代码不能为空！");
            }

            if (!securityClient.deleteKeyByOrgCode(orgCode)) {
                return failed("删除失败!");
            }

            if (!orgClient.deleteOrg(orgCode)) {
                return failed("删除失败!");
            }
            return success(null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     * 创建机构
     * @param mOrganizationJsonData
     * @param geographyModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构")
    public Envelop create(
            @ApiParam(name = "mOrganizationJsonData", value = "机构信息Json", defaultValue = "")
            @RequestParam(value = "mOrganizationJsonData", required = false) String mOrganizationJsonData,
            @ApiParam(name = "geography_model_json_data",value = "地址信息Json",defaultValue = "")
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData ){
        try {
            String errorMsg = "";

            GeographyModel geographyModel = objectMapper.readValue(geographyModelJsonData,GeographyModel.class);

            if (geographyModel.nullAddress()) {
                errorMsg+="机构地址不能为空！";
            }

            OrgDetailModel orgDetailModel = objectMapper.readValue(mOrganizationJsonData, OrgDetailModel.class);
            MOrganization mOrganization = convertToMOrganization(orgDetailModel);
            if (StringUtils.isEmpty(mOrganization.getOrgCode())) {
                errorMsg+="机构代码不能为空！";
            }
            if (StringUtils.isEmpty(mOrganization.getFullName())) {
                errorMsg+="机构全名不能为空！";
            }
            if (StringUtils.isEmpty(mOrganization.getShortName())) {
                errorMsg+="机构简称不能为空！";
            }
            if (StringUtils.isEmpty(mOrganization.getTel())) {
                errorMsg+="联系方式不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }
            String location = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
            if (StringUtils.isEmpty(location)) {
                return failed("保存失败!");
            }

            mOrganization.setLocation(location);
            String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
            MOrganization mOrgNew = orgClient.create(mOrganizationJson);
            if (mOrgNew == null) {
                return failed("保存失败!");
            }
            return success(convertToOrgDetailModel(mOrgNew));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    public Envelop update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构信息Json", defaultValue = "")
            @RequestParam(value = "mOrganizationJsonData", required = false) String mOrganizationJsonData,
            @ApiParam(name = "geography_model_json_data",value = "地址信息Json",defaultValue = "")
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData  ) {
      try {
          String errorMsg ="";
          GeographyModel geographyModel = objectMapper.readValue(geographyModelJsonData, GeographyModel.class);

          if (geographyModel.nullAddress()) {
              errorMsg+="机构地址不能为空！";
          }

          OrgDetailModel orgDetailModel = objectMapper.readValue(mOrganizationJsonData, OrgDetailModel.class);
          MOrganization mOrganization = convertToMOrganization(orgDetailModel);
          if (StringUtils.isEmpty(mOrganization.getOrgCode())) {
              errorMsg+="机构代码不能为空！";
          }
          if (StringUtils.isEmpty(mOrganization.getFullName())) {
              errorMsg+="机构全名不能为空！";
          }
          if (StringUtils.isEmpty(mOrganization.getShortName())) {
              errorMsg+="机构简称不能为空！";
          }
          if (StringUtils.isEmpty(mOrganization.getTel())) {
              errorMsg+="联系方式不能为空！";
          }
          String locationId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
          if (StringUtils.isEmpty(locationId)) {
              return failed("保存地址失败！");
          }
          if(StringUtils.isNotEmpty(errorMsg))
          {
              return failed(errorMsg);
          }

          mOrganization.setLocation(locationId);
          String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
          MOrganization mOrgNew = orgClient.update(mOrganizationJson);
          if (mOrgNew == null) {
             return  failed("更新失败");
          }
          return success(convertToOrgDetailModel(mOrgNew));
      }
      catch (Exception ex)
      {
          ex.printStackTrace();
          return failedSystem();
      }
    }

    /**
     * 根据机构代码获取机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    public Envelop getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) {
        try {
            if (StringUtils.isEmpty(orgCode)) {
                return failed("机构代码不能为空！");
            }
            MOrganization mOrg = orgClient.getOrg(orgCode);
            if (mOrg == null) {
                return failed("机构获取失败");
            }
            OrgDetailModel org = convertToOrgDetailModel(mOrg);

            return success(org);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     * 将微服务返回结果转化为OrgDetailModel
     * @param mOrg
     * @return
     */

    public OrgDetailModel convertToOrgDetailModel(MOrganization mOrg) {
        OrgDetailModel org = convertToModel(mOrg, OrgDetailModel.class);
        org.setCreateDate(DateToString(mOrg.getCreateDate(), AgAdminConstants.DateTimeFormat));

        //获取机构类别字典值
        MConventionalDict orgTypeDict = conDictEntryClient.getOrgType(mOrg.getOrgType());
        org.setOrgTypeName(orgTypeDict == null ? "" : orgTypeDict.getValue());
        //获取接入方式字典字典值
        MConventionalDict settledWayDict = conDictEntryClient.getSettledWay(mOrg.getSettledWay());
        org.setSettledWayName(settledWayDict == null ? "" : settledWayDict.getValue());
        //org.setTags(mOrg.getTags());
        //获取地址字典值明细
        MGeography addr = addressClient.getAddressById(mOrg.getLocation());
        if(addr != null) {
            org.setProvince(addr.getProvince());
            org.setCity(addr.getCity());
            org.setDistrict(addr.getDistrict());
            org.setTown(addr.getTown());
            org.setStreet(addr.getStreet());
            org.setExtra(addr.getExtra());
        }
        //获取公钥信息（公钥、有效区间、开始时间）
        MKey security = securityClient.getOrgKey(mOrg.getOrgCode());
        if(security!=null){
            org.setPublicKey(security.getPublicKey());
            org.setValidTime(DateUtil.toString(security.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                    + "~" + DateUtil.toString(security.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
            org.setStartTime(DateUtil.toString(security.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
        }
        return org;
    }


    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据名称获取机构编号列表ids")
    @RequestMapping(value = "/organizations/name", method = RequestMethod.GET)
    public Envelop getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(orgClient.getIdsByName(name));
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }



    /**
     * 更新机构激活状态
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "organizations/{org_code}/{activity_flag}" , method = RequestMethod.PUT)
    @ApiOperation(value = "更新机构激活状态")
    public boolean activity(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") int activityFlag) {
        try {
            return orgClient.activity(orgCode, activityFlag);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * 根据地址获取机构下拉列表
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "organizations/geography" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    public Envelop getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "市")
            @RequestParam(value = "district") String district) {
        try {
            Envelop envelop = new Envelop();
            Collection<MOrganization> mOrganizations = orgClient.getOrgsByAddress(province, city, district);
            envelop.setDetailModelList(mOrganizations == null ? null : (List) mOrganizations);
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping( value = "organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    public Envelop distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        try {
            Map<String, String> key = orgClient.distributeKey(orgCode);
            if (key.size() == 0) {
               return failed("机构秘钥分发失败!");
            }
            return success(key);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "organizations/existence/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    public boolean isOrgCodeExists(
            @ApiParam(name = "org_code", value = "org_code", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode){
        try {
            return orgClient.isOrgCodeExists(orgCode);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public MOrganization convertToMOrganization(OrgDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MOrganization mOrganization = convertToModel(detailModel, MOrganization.class);
        mOrganization.setCreateDate(StringToDate(detailModel.getCreateDate(),AgAdminConstants.DateTimeFormat));
        return mOrganization;
    }
}
