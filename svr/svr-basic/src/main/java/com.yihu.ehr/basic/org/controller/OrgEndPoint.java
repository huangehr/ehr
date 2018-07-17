package com.yihu.ehr.basic.org.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.basic.address.service.AddressDictService;
import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.basic.security.service.UserSecurityService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.address.AddressDict;
import com.yihu.ehr.entity.security.UserKey;
import com.yihu.ehr.entity.security.UserSecurity;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.basic.org.model.OrgDept;
import com.yihu.ehr.basic.org.service.OrgDeptService;
import com.yihu.ehr.basic.org.service.OrgService;
import com.yihu.ehr.util.phonics.PinyinUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "org", description = "组织机构管理服务", tags = {"机构管理-机构管理服务"})
public class OrgEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgService orgService;
    @Autowired
    private UserSecurityService userSecurityService;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private AddressDictService addressDictService;


    @RequestMapping(value = "/organizations/getAllOrgs", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有机构列表")
    public List<MOrganization> getAllOrgs() throws Exception {
        List<MOrganization> orgs = orgService.search(null);
        return orgs;
    }


    /**
     * 机构列表查询
     *
     * @param fields
     * @param filters
     * @param sorts
     * @param size
     * @param page
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations/list", method = RequestMethod.POST)
    @ApiOperation(value = "根据条件查询机构列表")
    public List<MOrganization> searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<Organization> organizationList = orgService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgService.getCount(filters), page, size);
        return (List<MOrganization>) convertToModels(organizationList, new ArrayList<MOrganization>(organizationList.size()), MOrganization.class, fields);
    }

    /**
     * 机构列表查询
     *
     * @param fields
     * @param filters
     * @param sorts
     * @param size
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations/searchForCombo", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "searchParm", value = "关键字搜索")
            @RequestParam(value = "searchParm", required = false) String searchParm,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        filters = filters==null? "": filters;
        if (StringUtils.isNotEmpty(searchParm)){
            filters += "orgCode?"+searchParm+" g1;fullName?"+searchParm+" g1;";
        }
        List<Organization> list = orgService.search(fields, filters, sorts, page, size);
        int count = (int)orgService.getCount(filters);
        Envelop envelop = getPageResult(list, count, page, size);
        return envelop;
    }
    /**
     * 删除机构
     *
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据机构代码删除机构")
    public boolean deleteOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        orgService.delete(orgCode);
        return true;
    }

    /**
     * 创建机构
     *
     * @param orgJsonData
     * @throws Exception
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建机构")
    public MOrganization create(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgJsonData) throws Exception {
        Organization org = toEntity(orgJsonData, Organization.class);
        org.setCreateDate(new Date());
        org.setActivityFlag(1);
        org.setPyCode(PinyinUtil.getPinYinHeadChar(org.getFullName(), false));
        Organization organization=orgService.save(org);
        String orgId= orgService.getOrgIdByOrgCode(organization.getOrgCode());
        //添加默认部门
        OrgDept dept=new OrgDept();
        dept.setOrgId(String.valueOf(orgId));
        dept.setCode(String.valueOf(orgId)+"1");
        dept.setName("未分配");
        orgDeptService.saveOrgDept(dept);
        return convertToModel(org, MOrganization.class);
    }

    @RequestMapping(value = "organizations", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改机构")
    public MOrganization update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgJsonData) throws Exception {
        Organization org = toEntity(orgJsonData, Organization.class);
        org.setPyCode(PinyinUtil.getPinYinHeadChar(org.getFullName(), false));
        orgService.save(org);
        return convertToModel(org, MOrganization.class);
    }

    /**
     * 根据机构代码获取机构
     *
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    public MOrganization getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        Organization org = orgService.getOrg(orgCode);
        MOrganization orgModel = convertToModel(org, MOrganization.class);
        return orgModel;
    }

    /**
     * 根据机构ID获取机构
     *
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/organizations/getOrgById/{org_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构ID获取机构")
    public MOrganization getOrgById(
            @ApiParam(name = "org_id", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_id") String orgId) throws Exception {
        Organization org = orgService.getOrgById(orgId);
        MOrganization orgModel = convertToModel(org, MOrganization.class);
        return orgModel;
    }


    /**
     * 根据机构代码列表批量查询机构
     *
     * @param orgCodes
     * @return
     */
    @RequestMapping(value = "/organizations/org_codes", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码列表批量查询机构")
    public List<MOrganization> getOrgs(
            @ApiParam(name = "org_codes", value = "机构代码", defaultValue = "")
            @RequestParam(value = "org_codes") String[] orgCodes) throws Exception {
        List<String> orgCodeList = Arrays.asList(orgCodes);
        List<Organization> organizationList = orgService.findByOrgCodes(orgCodeList);
        return (List<MOrganization>) convertToModels(organizationList, new ArrayList<MOrganization>(organizationList.size()), MOrganization.class, "");
    }

    /**
     * 根据管理员登录帐号获取机构
     *
     * @param adminLoginCode
     * @return
     */
    @RequestMapping(value = "/organizations/{org_code}/admin/{admin_login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据管理员登录帐号获取机构")
    public MOrganization getOrgByAdminLoginCode(
            @ApiParam(name = "org_code", value = "管理员登录帐号", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "admin_login_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "admin_login_code") String adminLoginCode) throws Exception {
        Organization org = orgService.getOrgByAdminLoginCode(orgCode,adminLoginCode);
        MOrganization orgModel = convertToModel(org, MOrganization.class);
        return orgModel;
    }

    /**
     * 根据name获取机构orgCodes
     *
     * @param name
     * @return
     */
    @ApiOperation(value = "根据名称获取机构编号列表")
    @RequestMapping(value = "/organizations/name", method = RequestMethod.GET)
    public List<String> getIdsByName(
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        List<String> orgCodes = orgService.getCodesByName(name);
        return orgCodes;
    }

    @ApiOperation(value = "根据地区代码获取机构列表")
    @RequestMapping(value = "/organizations/areas/{area}", method = RequestMethod.GET)
    public List<MOrganization> getOrganizationByAreaCode(
            @ApiParam(name = "area", value = "地区代码", defaultValue = "")
            @PathVariable(value = "area") String area)
    {
        List<Organization> organizationList = orgService.findByOrgArea(area);
        return (List<MOrganization>) convertToModels(organizationList, new ArrayList<MOrganization>(organizationList.size()), MOrganization.class, "");
    }

    /**
     * 跟新机构激活状态
     *
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "organizations/{org_code}/{activity_flag}", method = RequestMethod.PUT)
    @ApiOperation(value = "跟新机构激活状态")
    public boolean activity(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode,
            @ApiParam(name = "activity_flag", value = "状态", defaultValue = "")
            @PathVariable(value = "activity_flag") int activityFlag) throws Exception {
        Organization org = orgService.getOrg(orgCode);
        if (org.getActivityFlag() == 1) {
            org.setActivityFlag(0);
        } else {
            org.setActivityFlag(1);
        }
        orgService.save(org);
        return true;
    }

    /**
     * 根据地址获取机构下拉列表
     *
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "/organizations/geography", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    public List<MOrganization> getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city", required = false) String city,
            @ApiParam(name = "district", value = "县")
            @RequestParam(value = "district", required = false) String district) {
        List<Organization> orgList = orgService.searchByAddress(province, city, district);
        if(orgList != null && orgList.size() > 0 ){
            return (List<MOrganization>) convertToModels(orgList, new ArrayList<MOrganization>(orgList.size()), MOrganization.class, null);
        }else {
            return null;
        }
    }

    @RequestMapping(value = "/organizations/key", method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) throws Exception {
        UserSecurity key = userSecurityService.getKeyByOrgCode(orgCode);
        Map<String, String> keyMap = new HashMap<>();
        if (key != null) {
            List<UserKey> keyMaps =  userSecurityService.getKeyMapByOrgCode(orgCode);
            userSecurityService.deleteKey(keyMaps);
        }

        key = userSecurityService.createKeyByOrgCode(orgCode);

        String validTime = DateFormatUtils.format(key.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(key.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", key.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(key.getFromDate(), "yyyy-MM-dd"));
        return keyMap;
    }

    @RequestMapping(value = "/organizations/existence/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    boolean isOrgCodeExists(
            @ApiParam(name = "org_code", value = "org_code", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) {
        return orgService.isExistOrg(orgCode);
    }

    @RequestMapping(value = "/organizations/checkSunOrg" , method = RequestMethod.PUT)
    @ApiOperation(value = "判断机构是否已经是子机构")
    boolean checkSunOrg(
            @ApiParam(name = "org_pId", value = "org_pId", defaultValue = "")
            @RequestParam(value = "org_pId") String orgPid,
            @ApiParam(name = "org_id", value = "org_id", defaultValue = "")
            @RequestParam(value = "org_id") String orgId) {
        return orgService.checkSunOrg(orgPid,orgId);
    }


    /**
     * 机构资质上传
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/organizations/images",method = RequestMethod.POST)
    @ApiOperation(value = "上传头像,把图片转成流的方式发送")
    public String uploadImages(
            @ApiParam(name = "jsonData", value = "头像转化后的输入流")
            @RequestBody String jsonData ) throws Exception {
        if(jsonData == null){
            return null;
        }
        String date = URLDecoder.decode(jsonData,"UTF-8");

        String[] fileStreams = date.split(",");
        String is = URLDecoder.decode(fileStreams[0],"UTF-8").replace(" ","+");
        byte[] in = Base64.getDecoder().decode(is);

        String pictureName = fileStreams[1].substring(0,fileStreams[1].length()-1);
        String fileExtension = pictureName.substring(pictureName.lastIndexOf(".") + 1).toLowerCase();
        String description = null;
        if ((pictureName != null) && (pictureName.length() > 0)) {
            int dot = pictureName.lastIndexOf('.');
            if ((dot > -1) && (dot < (pictureName.length()))) {
                description = pictureName.substring(0, dot);
            }
        }
        InputStream inputStream = new ByteArrayInputStream(in);
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
//        String path = "{\"groupName\":" + groupName + ",\"remoteFileName\":" + remoteFileName + "}";
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        //返回文件路径
        return path;
    }


    /**
     * 机构资质下载
     * @return
     */
    @RequestMapping(value = "/organizations/images",method = RequestMethod.GET)
    @ApiOperation(value = "下载头像")
    public String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName) throws Exception {
        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);

        String fileStream = new String(Base64.getEncoder().encode(bytes));
        String imageStream = URLEncoder.encode(fileStream, "UTF-8");
        return imageStream;
    }

    @RequestMapping(value = "/organizations/getAllSaasOrgs", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有机构列表")
    public List<Organization> getAllSaasOrgs(
            @ApiParam(name = "saasName", value = "名称", defaultValue = "")
            @RequestParam(value = "saasName", required = false) String saasName) throws Exception {
                List<Organization> orgs = orgService.getAllSaasOrgs(saasName);
                return orgs;
            }
    /**
     * 根据机构ID获取机构
     *
     * @param userOrgCode
     * @return
     */
    @RequestMapping(value = "/organizations/getOrgListById", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构ID获取机构")
    public List<String> getOrgListById(
            @ApiParam(name = "userOrgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "userOrgCode") List<Long> userOrgCode) throws Exception {
        List<String> organizationList = orgService.getOrgListById(userOrgCode);
        return organizationList;
    }

    @RequestMapping(value = "/organizations/getAllOrgsNoPaging", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有机构列表不分页")
    public List<MOrganization> getAllOrgsNoPaging() throws Exception {
        List<MOrganization> orgs = orgService.search(null);
        return orgs;
    }

    @RequestMapping(value = "/organizations/batch", method = RequestMethod.POST)
    @ApiOperation("批量导入机构")
    public boolean createOrgBatch(
            @ApiParam(name = "orgs", value = "JSON", defaultValue = "")
            @RequestBody String orgs) throws Exception{
        List models = objectMapper.readValue(orgs, new TypeReference<List>() {});
        orgService.addOrgBatch(models);
        return true;
    }

    @RequestMapping(value = ServiceApi.Org.getseaOrgsByOrgCode, method = RequestMethod.POST)
    @ApiOperation("根据机构code获取机构code和name")
    public Map<String,String> seaOrgsByOrgCode(
            @ApiParam(name = "org_codes", value = "机构org_codes", defaultValue = "")
            @RequestBody String org_codes)throws Exception{
        Map<String, String> map = new HashMap<>();
        List<Object> list = (List<Object>)orgService.orgExist(toEntity(org_codes, String[].class));
            for(int i = 0 ;i < list.size() ; i++){
                Object[] objectList=(Object[])list.get(i);
                if(null!=objectList[0]&&null!=objectList[1]){
                    map.put(objectList[0].toString(), objectList[1].toString());
                }
            }
        return  map;
    };

    @RequestMapping(value = "/organizations/getHospital", method = RequestMethod.GET)
    @ApiOperation(value = "查询所有经纬度医院列表")
    public Envelop getHospital() throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<Organization> orgs = orgService.getHospital();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(orgs);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = "/organizations/getOrgListByAddressPid", method = RequestMethod.GET)
    @ApiOperation(value = "根据区域查询机构列表")
    public Envelop getOrgListByAddressPid(
            @ApiParam(name = "pid", value = "区域id", defaultValue = "")
            @RequestParam(value = "pid") Integer pid,
            @ApiParam(name = "fullName", value = "机构名称", defaultValue = "")
            @RequestParam(value = "fullName", required = false) String fullName) {
        Envelop envelop = new Envelop();
        try {
            List<Organization> orgList;
            if (StringUtils.isEmpty(fullName)) {
                orgList = orgService.getOrgListByAddressPid(pid);
            } else {
                orgList = orgService.getOrgListByAddressPidAndParam(pid, fullName);
            }
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(orgList);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = "/organizations/getOrgListTreeByAddressPid", method = RequestMethod.GET)
    @ApiOperation(value = "根据区域、机构区县查询机构列表")
    public List<Map<String, Object>> getOrgListTreeByAddressPid(
            @ApiParam(name = "pid", value = "区域id", defaultValue = "")
            @RequestParam(value = "pid") Integer pid) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        try {
            List<AddressDict> addList = addressDictService.getPidToAddr(pid);
            List<Organization> orgList;
            for (AddressDict addressDict : addList) {
                List<Map<String, Object>> childListMap = new ArrayList<>();
                Map<String, Object> allMap = new HashMap<>();
                allMap.put("text", "全部机构");
                childListMap.add(allMap);
                Map<String, Object> map = new HashMap<>();
                map.put("text", addressDict.getName());
                map.put("value", addressDict.getId());
                orgList = orgService.getOrgListByAddressPidAndOrgArea(pid, addressDict.getId() + "");
                if (null != orgList && orgList.size() > 0) {
                    orgList.forEach(one -> {
                        Map<String, Object> childMap = new HashMap<>();
                        childMap.put("text", one.getFullName());
                        childMap.put("value", one.getOrgCode());
                        childListMap.add(childMap);
                    });
                }
                map.put("children", childListMap);
                listMap.add(map);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }
}
