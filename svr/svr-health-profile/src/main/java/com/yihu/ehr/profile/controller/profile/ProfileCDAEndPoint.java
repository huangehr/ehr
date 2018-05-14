package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * EndPoint - 档案CDA接口（兼容 pc & mobile）
 * 档案CDA接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.22
 * @modifier progr1mmer
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileCDAEndPoint", description = "档案CDA接口", tags = "档案影像服务 - 档案CDA接口")
public class ProfileCDAEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileCDAService profileCDAService;

    @ApiOperation("CDA分类")
    @RequestMapping(value = ServiceApi.Profiles.CDAClass, method = RequestMethod.GET)
    public List<Map<String, Object>> CDAClass(
            @ApiParam(name = "profile_id", value = "档案ID", required = true, defaultValue = "49229004X_000406450000000UX0_1485608518000")
            @RequestParam(value = "profile_id") String profile_id,
            @ApiParam(name = "template_name", value = "cda文档标题", defaultValue = "门诊摘要")
            @RequestParam(value = "template_name", required = false) String template_name) throws Exception {
        return profileCDAService.getCDAClass(profile_id, template_name);
    }

    @ApiOperation("CDA数据")
    @RequestMapping(value = ServiceApi.Profiles.CDAData, method = RequestMethod.GET)
    public Map<String, Object> CDAData(
            @ApiParam(name = "profile_id", value = "档案ID", required =  true, defaultValue = "449229004X_000001186960_1513419334000")
            @RequestParam(value = "profile_id") String profile_id,
            @ApiParam(name = "cda_document_id", value = "模板ID", required = true, defaultValue = "82e8a0b5317a11e7b186a1ae879a6c51")
            @RequestParam(value = "cda_document_id") String cda_document_id,
            @ApiParam(name = "transform", value = "是否转换成标准数据", defaultValue = "false")
            @RequestParam(value = "transform", required = false) boolean transform) throws Exception {
        return profileCDAService.getCDAData(profile_id, cda_document_id, true);
    }

}
