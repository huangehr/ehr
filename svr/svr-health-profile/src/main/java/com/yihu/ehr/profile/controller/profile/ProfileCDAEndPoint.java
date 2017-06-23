package com.yihu.ehr.profile.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.model.MedicationStat;
import com.yihu.ehr.profile.service.*;
import com.yihu.ehr.util.rest.Envelop;
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
 * 档案CDA接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.22
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "档案CDA接口")
public class ProfileCDAEndPoint extends BaseRestEndPoint {

    @Autowired
    ProfileCDAService profileCDAService;


    /******************************** CDA档案接口 ****************************************************/
    @ApiOperation("CDA分类")
    @RequestMapping(value = ServiceApi.Profiles.CDAClass, method = RequestMethod.GET)
    public List<Map<String,Object>> CDAClass(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="41872607-9_1000000_30000001_1465894742000")
            @RequestParam(value = "profile_id", required = true) String profile_id,
            @ApiParam(name = "event_type", value = "事件类型")
            @RequestParam(value = "event_type", required = false) String event_type) throws Exception {

        return profileCDAService.getCDAClass(profile_id,event_type);
    }

    @ApiOperation("CDA数据")
    @RequestMapping(value = ServiceApi.Profiles.CDAData, method = RequestMethod.GET)
    public Map<String,Object> CDAData(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="41872607-9_1000000_30000001_1465894742000")
            @RequestParam(value = "profile_id", required = true) String profile_id,
            @ApiParam(name = "cda_document_id", value = "模板ID",defaultValue="0dae0006568a12a20dc35654490ab357")
            @RequestParam(value = "cda_document_id", required = true) String cda_document_id) throws Exception {

        return profileCDAService.getCDAData(profile_id, cda_document_id);
    }

    @ApiOperation("完整CDA文档")
    @RequestMapping(value = ServiceApi.Profiles.CDADocument, method = RequestMethod.GET)
    public Map<String,Object> CDADocument(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="41872607-9_1000000_30000001_1465894742000")
            @RequestParam(value = "profile_id", required = true) String profile_id) throws Exception {

        return  profileCDAService.getCDADocument(profile_id);
    }

    @ApiOperation("获取cda_document_id")
    @RequestMapping(value = ServiceApi.Profiles.CDADocumentId, method = RequestMethod.GET)
    public Map<String, Object> CDADocumentId(
            @ApiParam(name = "org_code", value = "机构代码",defaultValue="41872607-9")
            @RequestParam(value = "org_code", required = true) String org_code,
            @ApiParam(name = "event_no", value = "事件号",defaultValue="30000001")
            @RequestParam(value = "event_no", required = true) String event_no,
            @ApiParam(name = "cda_code", value = "模板类别",defaultValue="HSDC01.04")
            @RequestParam(value = "cda_code", required = true) String cda_code) throws Exception {
        return profileCDAService.getCDADocumentId(org_code,event_no, cda_code);
    }

}
