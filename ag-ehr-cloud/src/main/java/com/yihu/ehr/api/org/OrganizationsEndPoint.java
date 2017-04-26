package com.yihu.ehr.api.org;

import com.yihu.ehr.api.model.KeyModel;
import com.yihu.ehr.api.model.UserModel;
import com.yihu.ehr.api.model.OrgModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
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
@Api(value = "organizations", description = "组织机构服务")
public class OrganizationsEndPoint extends BaseController {

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private SecurityClient securityClient;

    @ApiOperation(value = "获取机构")
    @RequestMapping(value = "/{org_code}", method = RequestMethod.GET)
    public OrgModel getOrg(
            @ApiParam(name = "org_code", value = "机构代码", defaultValue = "")
            @PathVariable(value = "org_code")
            String orgCode) throws Exception {
        MOrganization orgModel = organizationClient.getOrg(orgCode);

        OrgModel model = new OrgModel();
        BeanUtils.copyProperties(orgModel, model);

        return model;
    }


    @ApiOperation(value = "获取机构列表")
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    List<MOrganization> search(
            @RequestParam(value = "fields",required = false) String fields,
            @RequestParam(value = "filters",required = false) String filters,
            @RequestParam(value = "sorts",required = false) String sorts,
            @RequestParam(value = "page",required = false) int page,
            @RequestParam(value = "size",required = false) int size) {
        return organizationClient.search(fields,filters,sorts,page,size);
    }

    @RequestMapping(value = "/{org_code}/administrator", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构管理员信息")
    public UserModel getOrgAdministrator(
            @ApiParam(name = "org_code", value = "管理员登录帐号", defaultValue = "")
            @PathVariable(value = "org_code")
            String orgCode) throws Exception {
        return null;
    }

    @RequestMapping(value = "/{org_code}/key", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构公钥", notes = "机构公钥")
    public KeyModel getPublicKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code")
            String orgCode) {

        MKey key = securityClient.getOrgKey(orgCode);
        KeyModel model = new KeyModel();
        BeanUtils.copyProperties(key, model, "privateKey","fromDate","expiryDate");
        return model;
    }
}
