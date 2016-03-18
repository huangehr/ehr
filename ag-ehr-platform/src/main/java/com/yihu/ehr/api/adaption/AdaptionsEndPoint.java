package com.yihu.ehr.api.adaption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.AdapterDispatchClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 标准适配。标准适配分为两个方面：机构数据标准与适配方案。前者用于定义医疗机构信息化系统的数据视图，
 * 后者根据此视图与平台的健康档案数据标准进行匹配与映射。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/adaptions")
@Api(protocols = "https", value = "adaptions", description = "标准适配服务")
public class AdaptionsEndPoint {
    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private AdapterDispatchClient adapterDispatchClient;

    @RequestMapping(value = "/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构最新适配", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, notes = "此包内容包含：平台标准，机构标准与二者适配")
    public ResponseEntity<String> getOrgAdaption(
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(name = "org_code", value = "机构编码")
            @PathVariable(value = "org_code") String orgCode) throws Exception {

        String keyId = securityClient.getOrgKey(orgCode);
        MKey mKey = securityClient.getKey(keyId);
        Object o = adapterDispatchClient.downAdaptions(mKey.getPrivateKey(), versionCode, orgCode).toString();
        return new ResponseEntity<>(
                o.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{org_code}/source", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构最新适配的文件路径", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "此包内容包含：平台标准，机构标准与二者适配")
    public ResponseEntity<String> getOrgAdaptionUrl(
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(name = "org_code", value = "机构编码")
            @PathVariable(value = "org_code") String orgCode) throws Exception {

        MKey mKey = securityClient.getOrgKey(orgCode);
        Map rs = adapterDispatchClient.getAdaptionUrl(mKey.getPrivateKey(), versionCode, orgCode);
        if(rs==null){
            rs = new HashMap<>();
            rs.put("msg", "该机构适配版本未发布！");
        }
        ObjectMapper mapper = new ObjectMapper();
        return new ResponseEntity<>(mapper.writeValueAsString(rs), HttpStatus.OK);
    }


    @ApiOperation(value = "获取标准版本", produces = MediaType.ALL_VALUE)
    @RequestMapping(value = "/org_plan/version",  method = RequestMethod.GET)
    public ResponseEntity<String> getVersion(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Map rs = new HashMap<>();
        if (org.apache.commons.lang.StringUtils.isEmpty(orgCode)) {
            rs.put("msg", "缺失参数:org_code!");
            return new ResponseEntity<>(mapper.writeValueAsString(rs), HttpStatus.BAD_REQUEST);
        }
        Object object = adapterDispatchClient.getCDAVersionInfoByOrgCode(orgCode);
        if(object!=null){
            Map map = (Map) object;
            if(map.get("result")!=null){
                return new ResponseEntity<>(
                        ((Map) map.get("result")).get("version").toString(), HttpStatus.OK);
            }
            return new ResponseEntity<>(mapper.writeValueAsString(object), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);
    }
}
