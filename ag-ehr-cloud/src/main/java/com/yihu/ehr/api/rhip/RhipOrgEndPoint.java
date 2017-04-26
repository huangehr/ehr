package com.yihu.ehr.api.rhip;

import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.feign.ConventionalDictEntryClient;
import com.yihu.ehr.feign.GeographyClient;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.04 9:03
 */
@EnableFeignClients
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rhip")
@Api(value = "organizations", description = "区域卫生信息平台-组织机构服务", tags = {"区域卫生信息平台-组织机构服务"})
public class RhipOrgEndPoint extends BaseController {

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private GeographyClient geographyClient;

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @ApiOperation(value = "获取机构")
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    public MOrganization getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code")
            String orgCode) throws Exception {
        MOrganization orgModel = organizationClient.getOrg(orgCode);
        return orgModel;
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
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData,
            @ApiParam(name = "inputStream", value = "转换后的输入流", defaultValue = "")
            @RequestParam(value = "inputStream", required = false) String inputStream,
            @ApiParam(name = "imageName", value = "图片全名", defaultValue = "")
            @RequestParam(value = "imageName", required = false) String imageName){
        try {
            String errorMsg = "";

            //头像上传,接收头像保存的远程路径  path
            String path = null;
            if (!StringUtils.isEmpty(inputStream)) {
                String jsonData = inputStream + "," + imageName;
                path = organizationClient.uploadPicture(jsonData);
            }

            String locationId = null;
            if (!StringUtils.isEmpty(geographyModelJsonData)) {
                GeographyModel geographyModel = objectMapper.readValue(geographyModelJsonData, GeographyModel.class);
                if (geographyModel.nullAddress()) {
                    errorMsg+="机构地址不能为空！";
                }
                locationId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                if (StringUtils.isEmpty(locationId)) {
                    return failed("保存地址失败！");
                }
                if(StringUtils.isNotEmpty(errorMsg))
                {
                    return failed(errorMsg);
                }
            }

            OrgDetailModel orgDetailModel = objectMapper.readValue(mOrganizationJsonData, OrgDetailModel.class);

            if (!StringUtils.isEmpty(path)) {
                orgDetailModel.setImgRemotePath(path);
                orgDetailModel.setImgLocalPath("");
            }

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

            mOrganization.setLocation(locationId);
            String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
            MOrganization mOrgNew = organizationClient.create(mOrganizationJson);
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

    @RequestMapping(value = "/organizations" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构")
    public Envelop update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构信息Json", defaultValue = "")
            @RequestParam(value = "mOrganizationJsonData", required = true) String mOrganizationJsonData,
            @ApiParam(name = "geography_model_json_data",value = "地址信息Json",defaultValue = "")
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData,
            @ApiParam(name = "inputStream", value = "转换后的输入流", defaultValue = "")
            @RequestParam(value = "inputStream", required = false) String inputStream,
            @ApiParam(name = "imageName", value = "图片全名", defaultValue = "")
            @RequestParam(value = "imageName", required = false) String imageName) {
        try {
            String errorMsg ="";

            //头像上传,接收头像保存的远程路径  path
            String path = null;
            if (!StringUtils.isEmpty(inputStream)) {
                String jsonData = inputStream + "," + imageName;
                path = organizationClient.uploadPicture(jsonData);
            }
            String locationId = null;
            if (!StringUtils.isEmpty(geographyModelJsonData)) {
                GeographyModel geographyModel = objectMapper.readValue(geographyModelJsonData, GeographyModel.class);
                if (geographyModel.nullAddress()) {
                    errorMsg+="机构地址不能为空！";
                }
                locationId = geographyClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                if (StringUtils.isEmpty(locationId)) {
                    return failed("保存地址失败！");
                }
                if(StringUtils.isNotEmpty(errorMsg))
                {
                    return failed(errorMsg);
                }
            }
            OrgDetailModel orgDetailModel = null;
            if (!StringUtils.isEmpty(mOrganizationJsonData)) {
                orgDetailModel = objectMapper.readValue(mOrganizationJsonData, OrgDetailModel.class);
            }
            if (!StringUtils.isEmpty(path)) {
                orgDetailModel.setImgRemotePath(path);
                orgDetailModel.setImgLocalPath("");
            }

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

            mOrganization.setLocation(locationId);
            String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
            MOrganization mOrgNew = organizationClient.update(mOrganizationJson);
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

    public MOrganization convertToMOrganization(OrgDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MOrganization mOrganization = convertToModel(detailModel, MOrganization.class);
        mOrganization.setCreateDate(StringToDate(detailModel.getCreateDate(), AgAdminConstants.DateTimeFormat));
        return mOrganization;
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
        if(StringUtils.isNotBlank(mOrg.getOrgType())){
            MConventionalDict orgTypeDict = conDictEntryClient.getOrgType(mOrg.getOrgType());
            org.setOrgTypeName(orgTypeDict == null ? "" : orgTypeDict.getValue());
        }
        //获取接入方式字典字典值
        if(StringUtils.isNotBlank(mOrg.getSettledWay())){
            MConventionalDict settledWayDict = conDictEntryClient.getSettledWay(mOrg.getSettledWay());
            org.setSettledWayName(settledWayDict == null ? "" : settledWayDict.getValue());
        }
        //org.setTags(mOrg.getTags());
        //获取地址字典值明细
        if(StringUtils.isNotBlank(mOrg.getLocation())){
            MGeography addr = geographyClient.getAddressById(mOrg.getLocation());
            if(addr != null) {
                org.setProvince(addr.getProvince());
                org.setCity(addr.getCity());
                org.setDistrict(addr.getDistrict());
                org.setTown(addr.getTown());
                org.setStreet(addr.getStreet());
                org.setExtra(addr.getExtra());
                org.setProvinceId(geographyToCode(addr.getProvince(),156));
                org.setCityId(geographyToCode(addr.getCity(),org.getProvinceId()));
                org.setDistrictId(geographyToCode(addr.getDistrict(),org.getCityId()));
            }
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
    public int geographyToCode(String name,int code){
        String[] fields = {"name","pid"};
        String[] values = {name,String.valueOf(code)};
        List<MGeographyDict> geographyDictList = (List<MGeographyDict>) geographyClient.getAddressDict(fields,values);
        return geographyDictList.get(0).getId();
    }

}
