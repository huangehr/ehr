package com.yihu.ehr.org.controller;

import com.netflix.discovery.converters.Auto;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.org.OrgCache;
import com.yihu.ehr.org.service.OrgLdapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hzp
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "/org/ldap/", description = "组织Ldap服务")
public class OrgLdapEndPoint {
    @Autowired
    OrgLdapService orgLdapService;

    @ApiOperation("缓存机构数据")
    @RequestMapping(value = "init", method = RequestMethod.POST)
    public void inti() throws Exception {
         orgLdapService.importLdapData();
    }

}
