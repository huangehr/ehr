package com.yihu.ehr.organization.controller;

import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.redis.client.RedisUpdateClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.adapter.service.AdapterOrgClient;
import com.yihu.ehr.adapter.service.PlanClient;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import com.yihu.ehr.model.profile.MTemplate;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.agModel.org.OrgModel;
import com.yihu.ehr.security.service.SecurityClient;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.template.service.TemplateClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
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
@Api(value = "organization", description = "机构信息管理", tags = {"机构管理"})
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
    @Autowired
    private UserClient userClient;
    @Autowired
    private AdapterOrgClient adapterOrgClient;
    @Autowired
    private PlanClient planClient;
    @Autowired
    private TemplateClient templateClient;
    @Autowired
    private FileResourceClient fileResourceClient;
    @Autowired
    private AppClient appClient;
    @Autowired
    private RedisUpdateClient redisUpdateClient;

    @ApiOperation(value = "获取所有部门列表")
    @RequestMapping(value = "/organizations/getAllOrgs", method = RequestMethod.GET)
    public Envelop getAllOrgDepts() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(orgClient.getAllOrgs() );
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    public Envelop searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province",required=false) String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city",required=false) String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district",required=false) String district) {
        try {
            String address = "";
            if(StringUtils.isNotBlank(province)){
              List<String> addressList = addressClient.search(province, city, district);
                String[] addrIdsArrays = addressList.toArray(new String[addressList.size()]);
                address = String.join(",", addrIdsArrays);
            }
            if(StringUtils.isNotBlank(address)){
                filters = StringUtils.isNotBlank(filters)?(filters+"location="+address):"location="+address;
            }
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

    @RequestMapping(value = "/organizations/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构下拉列表")
    public Envelop searchOrgsForCombo(
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
            ResponseEntity<List<MOrganization>> responseEntity = orgClient.searchOrgs(fields, filters, sorts, size, page);
            int totalCount = getTotalCount(responseEntity);
            return getResult(responseEntity.getBody(), totalCount, page, size);
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
        if(StringUtils.isNotEmpty(mOrg.getLocation())){
            // 获取机构地址信息
            String locationStrName = addressClient.getCanonicalAddress(mOrg.getLocation());
            if(StringUtils.isNotEmpty(locationStrName)){
                orgModel.setLocationStrName(locationStrName);
            }
        }
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
        try {
            orgModel.setCreateDate(DateUtil.formatDate(mOrg.getCreateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
        }catch (Exception e){
            orgModel.setCreateDate("");
        }
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
            //用户
            ResponseEntity<List<MUser>> userEntity = userClient.searchUsers("", "organization="+orgCode, "", 1, 1);
            List<MUser> users = userEntity.getBody();
            if(users.size()>0){
                return failed("删除失败!该组织机构下面存在用户，请先删除用户！");
            }
            //第三方标准
            ResponseEntity<Collection<MAdapterOrg>> mAdapterEntity = adapterOrgClient.searchAdapterOrg("", "org="+orgCode, "", 1, 1);
            List<MAdapterOrg> mAdapterOrgs = (List<MAdapterOrg>) mAdapterEntity.getBody();
            if(mAdapterOrgs.size()>0){
                return failed("删除失败!该组织机构下面存在第三方标准，请先删除第三方标准！");
            }
            //标准适配
            ResponseEntity<Collection<MAdapterPlan>> mAdapterPlanEntity = planClient.searchAdapterPlan("", "org="+orgCode, "", 1, 1);
            List<MAdapterPlan> mAdapterPlans = (List<MAdapterPlan>) mAdapterPlanEntity.getBody();
            if(mAdapterPlans.size()>0){
                return failed("删除失败!该组织机构下面存在标准适配，请先删除标准适配！");
            }
            //模板管理
            ResponseEntity<Collection<MTemplate>> mTemplateEntity = templateClient.getTemplates("", "organizationCode="+orgCode, "", 1, 1);
            List<MTemplate> mTemplates = (List<MTemplate>) mTemplateEntity.getBody();
            if(mTemplates.size()>0){
                return failed("删除失败!该组织机构下面存在模板，请先删除模板！");
            }
            //应用
            ResponseEntity<List<MApp>> mAppEntity = appClient.getApps("", "org=" + orgCode, "", 1, 1);
            List<MApp> mApps = mAppEntity.getBody();
            if(mApps.size()>0){
                return failed("删除失败!该组织机构下面存在应用，请先删除应用！");
            }
            if (!securityClient.deleteKeyByOrgCode(orgCode)) {
                return failed("删除失败!");
            }

            if (!orgClient.deleteOrg(orgCode)) {
                return failed("删除失败!");
            }
            try {
               fileResourceClient.filesDelete(orgCode);
            }catch (Exception e){
                return success("数据删除成功！图片删除失败！");
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
            @RequestParam(value = "geography_model_json_data", required = false) String geographyModelJsonData,
            @ApiParam(name = "inputStream", value = "转换后的输入流", defaultValue = "")
            @RequestParam(value = "inputStream") String inputStream,
            @ApiParam(name = "imageName", value = "图片全名", defaultValue = "")
            @RequestParam(value = "imageName") String imageName){
        try {
            String errorMsg = "";

            //头像上传,接收头像保存的远程路径  path
            String path = null;
            if (!StringUtils.isEmpty(inputStream)) {
                String jsonData = inputStream + "," + imageName;
                path = orgClient.uploadPicture(jsonData);
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
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            String locationId = null;
            if (!StringUtils.isEmpty(geographyModelJsonData)) {
                GeographyModel geographyModel = objectMapper.readValue(geographyModelJsonData, GeographyModel.class);
                if (geographyModel.nullAddress()) {
                    errorMsg+="机构地址不能为空！";
                }
                locationId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
                if (StringUtils.isEmpty(locationId)) {
                    return failed("保存地址失败！");
                }
                if(StringUtils.isNotEmpty(errorMsg))
                {
                    return failed(errorMsg);
                }
            }
            mOrganization.setLocation(locationId);
            String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
            MOrganization mOrgNew = orgClient.create(mOrganizationJson);
            if (mOrgNew == null) {
                return failed("保存失败!");
            }
            //新增机构名称缓存（原则上如果缓存新增失败不能影响实际新增结果）
            try {
                redisUpdateClient.updateOrgName(mOrgNew.getOrgCode());
                redisUpdateClient.updateOrgArea(mOrgNew.getOrgCode());
            }catch (Exception e) {
                e.printStackTrace();
            }
            return success(convertToOrgDetailModel(mOrgNew));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/organizations/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改机构")
    public Envelop update(
            @ApiParam(name = "mOrganizationJsonDatas", value = "机构信息Json", defaultValue = "")
            @RequestParam(value = "mOrganizationJsonDatas", required = true) String mOrganizationJsonData,
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
              path = orgClient.uploadPicture(jsonData);
          }
          String locationId = null;
          if (!StringUtils.isEmpty(geographyModelJsonData)) {
              GeographyModel geographyModel = objectMapper.readValue(geographyModelJsonData, GeographyModel.class);
              if (geographyModel.nullAddress()) {
                  errorMsg+="机构地址不能为空！";
              }
              locationId = addressClient.saveAddress(objectMapper.writeValueAsString(geographyModel));
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
          if(StringUtils.isNotEmpty(errorMsg)) {
              return failed(errorMsg);
          }
          mOrganization.setLocation(locationId);
          String mOrganizationJson = objectMapper.writeValueAsString(mOrganization);
          MOrganization mOrgNew = orgClient.update(mOrganizationJson);
          if (mOrgNew == null) {
             return  failed("更新失败");
          }
          //更新机构名称缓存（原则上如果缓存更新失败不能影响实际更新结果）
          try {
              redisUpdateClient.updateOrgName(mOrgNew.getOrgCode());
              redisUpdateClient.updateOrgArea(mOrgNew.getOrgCode());
          }catch (Exception e) {
              e.printStackTrace();
          }
          return success(convertToOrgDetailModel(mOrgNew));
      } catch (Exception e) {
          e.printStackTrace();
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
            if (!StringUtils.isEmpty(mOrg.getImgRemotePath())) {
                try {
                    String imagePath[] = mOrg.getImgRemotePath().split(":");
                    String localPath = orgClient.downloadPicture(imagePath[0], imagePath[1]);
                    mOrg.setImgLocalPath(localPath);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (mOrg.getParentHosId() != null) {
                MOrganization org =  orgClient.getOrgById(String.valueOf(mOrg.getParentHosId()));
                if (org != null) {
                    mOrg.setParentHosName(org.getFullName());
                }
            }
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
     * 根据机构Id获取机构
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/organizations/getOrgById/{org_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    public Envelop getOrgById(
            @ApiParam(name = "org_id", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_id") String orgId) {
        try {
            if (StringUtils.isEmpty(orgId)) {
                return failed("机构ID不能为空！");
            }
            MOrganization mOrg = orgClient.getOrgById(orgId);
            if (!StringUtils.isEmpty(mOrg.getImgRemotePath())) {
                try {
                    String imagePath[] = mOrg.getImgRemotePath().split(":");
                    String localPath = orgClient.downloadPicture(imagePath[0], imagePath[1]);
                    mOrg.setImgLocalPath(localPath);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
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
            MGeography addr = addressClient.getAddressById(mOrg.getLocation());
            if(addr != null) {
                org.setProvince(addr.getProvince());
                org.setCity(addr.getCity());
                org.setDistrict(addr.getDistrict());
                org.setTown(addr.getTown());
                org.setStreet(addr.getStreet());
                org.setExtra(addr.getExtra());
                if(StringUtils.isNotEmpty(addr.getProvince() )){
                    org.setProvinceId(getGeographyIdByName(addr.getProvince().toString()));
                }
                if(StringUtils.isNotEmpty(addr.getCity() )){
                    org.setCityId(getGeographyIdByName(addr.getCity().toString()));
                }
                if(StringUtils.isNotEmpty(addr.getDistrict() )) {
                    org.setDistrictId(getGeographyIdByName(addr.getDistrict().toString()));
                }
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

    public int getGeographyIdByName(String name){
        if(StringUtils.isEmpty(name)){
            return 0;
        }

        ObjectResult result =  addressClient.getAddressNameByCode(name);
        if(result != null){
            Map<String,Object> info = (HashMap)result.getData();
            int id = Integer.parseInt(info.get("id").toString());
            return id;
        }
        return 0;
    }

    public int geographyToCode(String name,int code){
        String[] fields = {"name","pid"};
        String[] values = {name,String.valueOf(code)};
        List<MGeographyDict> geographyDictList = (List<MGeographyDict>) addressClient.getAddressDict(fields,values);
        return geographyDictList.get(0).getId();
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
            @ApiParam(name = "district", value = "县")
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

    @RequestMapping(value = "organizations/checkSunOrg", method = RequestMethod.PUT)
    @ApiOperation(value = "判断机构是否已经是子机构")
    public boolean checkSunOrg(
            @ApiParam(name = "org_pId", value = "org_pId", defaultValue = "")
            @RequestParam(value = "org_pId") String orgPid,
            @ApiParam(name = "org_id", value = "org_id", defaultValue = "")
            @RequestParam(value = "org_id") String orgId){
        try {
            return orgClient.checkSunOrg(orgPid, orgId);
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
