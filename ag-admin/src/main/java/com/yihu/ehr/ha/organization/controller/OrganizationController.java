package com.yihu.ehr.ha.organization.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.organization.service.OrganizationClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.agModel.org.OrgModel;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.operator.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
            @RequestParam(value = "page", required = false) int page,
            HttpServletResponse response) throws Exception{
        List<OrgModel> orgModelList = new ArrayList<>();
        List<MOrganization> organizations = orgClient.searchOrgs(fields,filters,sorts,size,page);
        for(MOrganization org : organizations){
            OrgModel orgModel = new OrgModel();
            orgModel.setOrgCode(org.getOrgCode());
            orgModel.setFullName(org.getFullName());
            orgModel.setAdmin(org.getAdmin());
            orgModel.setTel(org.getTel());
            // 获取机构类别字典
            String orgTypeCode = org.getOrgCode();
            orgModel.setOrgTypeCode(orgTypeCode);
            orgModel.setOrgTypeName(conDictEntryClient.getOrgType(orgTypeCode).getValue());
            // 获取机构地址信息
            String locationId = org.getLocation();
            orgModel.setLocationStrName(addressClient.getCanonicalAddress(locationId));
            //获取机构接入方式
            String settledWayCode = org.getSettledWay();
            orgModel.setSettledWayCode(settledWayCode);
            orgModel.setSettledWayName(conDictEntryClient.getSettledWay(settledWayCode).getValue());
            // 判断机构状态（是否已激活）
            String activityFlag = org.getActivityFlag();
            orgModel.setActivityFlagCode(activityFlag);
            orgModel.setActivityFlagName(activityFlag=="1"?"是":"否");

            orgModelList.add(orgModel);
        }
        //获取符合条件总条数
        String count = response.getHeader(AgAdminConstants.ResourceCount);
        int totalCount = StringUtils.isNotEmpty(count)?Integer.parseInt(count):0;

        return getResult(orgModelList,totalCount,page,size);
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
            @PathVariable(value = "org_code") String orgCode) throws Exception{
        Envelop envelop = new Envelop();
        if(orgClient.deleteOrg(orgCode)){
            envelop.setSuccessFlg(true);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("删除机构失败");
        }
        return envelop;
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
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData ) throws Exception{
        Envelop envelop  = new Envelop();
        ObjectMapper objectMapper = new ObjectMapper();
        String locationId = addressClient.saveAddress(geographyModelJsonData);
        //TODO 返回地址id非空
        MOrganization mOrganization = objectMapper.readValue(mOrganizationJsonData,MOrganization.class);
        mOrganization.setLocation(locationId);
        String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
        Object object = orgClient.create(mOrganizationJson);
        if(object==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("机构创建失败");
        }else{
            envelop.setSuccessFlg(true);
            envelop.setObj(object);
        }
        return envelop;
    }

    @RequestMapping(value = "organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    public Envelop update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构信息Json", defaultValue = "")
            @RequestParam(value = "mOrganizationJsonData", required = false) String mOrganizationJsonData,
            @ApiParam(name = "geography_model_json_data",value = "地址信息Json",defaultValue = "")
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData  ) throws Exception{
        Envelop envelop = new Envelop();
        ObjectMapper objectMapper = new ObjectMapper();
        String locationId = addressClient.saveAddress(geographyModelJsonData);
        MOrganization mOrganization = objectMapper.readValue(mOrganizationJsonData,MOrganization.class);
        mOrganization.setLocation(locationId);
        String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
        Object object = orgClient.create(mOrganizationJson);
        if("true".equals(object.toString())){
            envelop.setSuccessFlg(true);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新失败");
        }
        return envelop;
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
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        MOrganization mOrg = new MOrganization();
        mOrg = orgClient.getOrg(orgCode);
        // MOrganization mOrg = orgClient.getOrg(orgCode);
        if (mOrg == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("机构获取失败");
            return envelop;
        }

        OrgDetailModel org = new OrgDetailModel();
        org.setOrgCode(mOrg.getOrgCode());
        org.setFullName(mOrg.getFullName());
        org.setShortName(mOrg.getShortName());
        //获取机构类别字典值
        String orgTypeCode = mOrg.getOrgType();
        org.setOrgTypeCode(orgTypeCode);
        org.setOrgTypeName(conDictEntryClient.getOrgType(orgTypeCode).getValue());
        //获取接入方式字典字典值
        String settledWayCode = mOrg.getSettledWay();
        org.setSettledWayCode(settledWayCode);
        org.setSettledWayName(conDictEntryClient.getSettledWay(settledWayCode).getValue());

        org.setAdmin(mOrg.getAdmin());
        org.setTel(mOrg.getTel());
        org.setTags(mOrg.getTags());
        //获取地址字典值明细
        String locationId = mOrg.getLocation();
        MGeography addr = addressClient.getAddressById(locationId);
        org.setProvince(addr.getProvince());
        org.setCity(addr.getCity());
        org.setDistrict(addr.getDistrict());
        org.setTown(addr.getTown());
        org.setStreet(addr.getStreet());
        org.setExtra(addr.getExtra());
        //获取公钥信息（公钥、有效区间、开始时间）
        MUserSecurity security = securityClient.getUserSecurityByOrgCode(mOrg.getOrgCode());
        org.setPublicKey(security.getPublicKey());
        org.setValidTime(DateUtil.toString(security.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                + "~" + DateUtil.toString(security.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
        org.setStartTime(DateUtil.toString(security.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));

        envelop.setSuccessFlg(true);
        envelop.setObj(org);
        return envelop;
    }


    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/organizations/{name}", method = RequestMethod.GET)
    public Envelop getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @PathVariable(value = "name") String name) {
        Envelop envelop = new Envelop();
        envelop.setDetailModelList(orgClient.getIdsByName(name));
        return envelop;
    }

    public String back(@ApiParam(name = "name", value = "机构名称", defaultValue = "")
                       @PathVariable(value = "name") String name){
        return name;
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
            @PathVariable(value = "activity_flag") int activityFlag) throws Exception{
        return orgClient.activity(orgCode,activityFlag);
    }


    /**
     * 根据地址获取机构下拉列表
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "organizations/{province}/{city}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    public Envelop getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @PathVariable(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @PathVariable(value = "city") String city) {
        Envelop envelop = new Envelop();
        Collection<MOrganization> mOrganizations = orgClient.getOrgsByAddress(province, city);
        envelop.setObj(mOrganizations);
        return envelop;
    }

    @RequestMapping( value = "organizations/key" , method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    public Envelop distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        Envelop envelop = new Envelop();
        Map<String,String> key = orgClient.distributeKey(orgCode);
        envelop.setObj(key);
        return envelop;
    }
}
