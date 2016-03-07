package com.yihu.ehr.api.org;

import com.yihu.ehr.api.model.OrganizationModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.04 9:03
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/organizations")
@Api(protocols = "https", value = "organizations", description = "组织机构服务")
public class OrganizationsEndPoint extends BaseController{

    @Autowired
    private OrganizationClient organizationClient;
    @Autowired
    private UserClient userClient;

    @ApiOperation(value = "根据机构代码获取机构")
    @RequestMapping(value = "/organizations/{org_code}", method = RequestMethod.GET)
    public OrganizationModel getOrg (
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception{
        return organizationClient.getOrg(orgCode);
    }

    @ApiOperation(value = "验证用户名密码得到所属机构")
    @RequestMapping(value = "/organizations/user_name/{user_name}/password/{password}", method = RequestMethod.GET)
    public OrganizationModel getOrgByUser (
            @ApiParam(name = "user_name", value = "账户", defaultValue = "")
            @PathVariable(value = "user_name") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @PathVariable(value = "password") String password) throws Exception{
        MUser mUser = userClient.getUserByNameAndPassword(userName,password);
        return organizationClient.getOrg(mUser.getOrganization());
    }


    @RequestMapping(value = "/organizations/admin/{admin_login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构代码获取机构")
    public OrganizationModel getOrgByAdminLoginCode(
            @ApiParam(name = "admin_login_code", value = "管理员登录帐号", defaultValue = "")
            @PathVariable(value = "admin_login_code") String adminLoginCode) throws Exception{
        return organizationClient.getOrgByAdminLoginCode(adminLoginCode);
    }


//    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
//    @ApiOperation(value = "根据条件查询机构列表")
//    public List<OrganizationModel> searchOrgs(
//            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
//            @RequestParam(value = "fields", required = false) String fields,
//            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
//            @RequestParam(value = "filters", required = false) String filters,
//            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
//            @RequestParam(value = "sorts", required = false) String sorts,
//            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
//            @RequestParam(value = "size", required = false) int size,
//            @ApiParam(name = "page", value = "页码", defaultValue = "1")
//            @RequestParam(value = "page", required = false) int page) throws Exception{
//        List<OrganizationModel> organizationList = organizationClient.search(fields, filters, sorts, page, size);
//        return organizationList;
//    }
}
