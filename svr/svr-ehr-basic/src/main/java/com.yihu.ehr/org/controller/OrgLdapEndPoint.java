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

/**
 * @author hzp
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "/org/ldap/", description = "组织Ldap服务")
public class OrgLdapEndPoint {
  /*  @Autowired
    OrgLdapService orgLdapService;

    @ApiOperation("缓存机构数据")
    @RequestMapping(value = "init", method = RequestMethod.POST)
    public void inti() throws Exception {
         orgLdapService.importLdapData();
    }

    @ApiOperation("查询所有子节点")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    public List<Map<String,Object>> queryAll(@ApiParam(value = "节点DN", defaultValue = "dc=ehr,dc=jkzl")
                        @RequestParam String dn,
                      @ApiParam(value = "节点类型，1用户 2科室 3机构 为空全部", defaultValue = "1")
                      @RequestParam(required = false) Integer type) throws Exception {
        return orgLdapService.queryAllByObjectClass(dn,type);
    }

    @ApiOperation("查询一级子节点")
    @RequestMapping(value = "queryChildren", method = RequestMethod.GET)
    public List<Map<String,Object>> queryChildren(@ApiParam(value = "节点DN", defaultValue = "ou=test1111,o=1111111,dc=ehr,dc=jkzl")
                                             @RequestParam String dn,
                                             @ApiParam(value = "节点类型，1用户 2科室 3机构 为空全部", defaultValue = "")
                                             @RequestParam(required = false) Integer type) throws Exception {
        return orgLdapService.queryChildren(dn,type);
    }

    @ApiOperation("查询节点")
    @RequestMapping(value = "queryBase", method = RequestMethod.GET)
    public Map<String,Object> queryBase(@ApiParam(value = "节点DN", defaultValue = "ou=test1111,o=1111111,dc=ehr,dc=jkzl")
                                                  @RequestParam String dn,
                                                  @ApiParam(value = "节点类型，1用户 2科室 3机构 为空全部", defaultValue = "")
                                                  @RequestParam(required = false) Integer type) throws Exception {
        return orgLdapService.queryBase(dn,type);
    }

    @ApiOperation("查询所有子节点(不包含自己)")
    @RequestMapping(value = "queryAllWithoutSelf", method = RequestMethod.GET)
    public List<Map<String,Object>> queryAllWithoutSelf(@ApiParam(value = "节点DN", defaultValue = "ou=test1111,o=1111111,dc=ehr,dc=jkzl")
                                                  @RequestParam String dn,
                                                  @ApiParam(value = "节点类型，1用户 2科室 3机构 为空全部", defaultValue = "")
                                                  @RequestParam(required = false) Integer type) throws Exception {
        return orgLdapService.queryAllWithoutSelf(dn,type);
    }*/
}
