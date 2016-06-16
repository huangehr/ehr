package com.yihu.ehr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.org.feign.SecurityClient;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.org.service.Organization;
import com.yihu.ehr.util.phonics.PinyinUtil;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "org", description = "组织机构管理服务", tags = {"机构管理"})
public class OrgEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgService orgService;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private FastDFSUtil fastDFSUtil;

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
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询机构列表")
    public List<MOrganization> searchOrgs(
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
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<Organization> organizationList = orgService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgService.getCount(filters), page, size);
        return (List<MOrganization>) convertToModels(organizationList, new ArrayList<MOrganization>(organizationList.size()), MOrganization.class, fields);
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
        orgService.save(org);
        return convertToModel(org, MOrganization.class);
    }

    @RequestMapping(value = "organizations", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改机构")
    public MOrganization update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgJsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Organization org = objectMapper.readValue(orgJsonData, Organization.class);
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
    @RequestMapping(value = "organizations/geography", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址获取机构下拉列表")
    public List<MOrganization> getOrgsByAddress(
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city", required = false) String city,
            @ApiParam(name = "district", value = "县")
            @RequestParam(value = "district", required = false) String district) {
        List<Organization> orgList = orgService.searchByAddress(province, city, district);
        return (List<MOrganization>) convertToModels(orgList, new ArrayList<MOrganization>(orgList.size()), MOrganization.class, null);
    }

    @RequestMapping(value = "organizations/key", method = RequestMethod.POST)
    @ApiOperation(value = "机构分发密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        MKey key = securityClient.getOrgKey(orgCode);
        Map<String, String> keyMap = new HashMap<>();
        if (key != null) {
            securityClient.deleteKeyByOrgCode(orgCode);
        }

        key = securityClient.createOrgKey(orgCode);

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
}
