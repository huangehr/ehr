package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.TransformClient;
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
 * EndPoint - 档案事件接口（兼容 pc & mobile）
 * 档案事件接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.22
 * @modifier progr1mmer
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileEventEndPoint", description = "档案事件接口", tags = {"档案影像服务 - 档案事件接口"})
public class ProfileEventEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileEventService patientEvent;

    @ApiOperation("门诊/住院事件(时间轴)")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvents, method = RequestMethod.GET)
    public List<Map<String, Object>> medicalEvents(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321199703137824")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "blurry_type", value = "针对需要对特殊档案类型进行查询的参数(0-门诊 2-影像 1-住院 3-体检 4-检验 6-免疫), 此处有值的话filter中就不能再包含event_type")
            @RequestParam(value = "blurry_type", required = false) String blurry_type,
            @ApiParam(name = "date", value = "时间")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "searchParam", value = "搜索条件")
            @RequestParam(value = "searchParam", required = false) String searchParam) throws Exception {
        return patientEvent.getPatientEvents(demographic_id, filter, blurry_type, date, searchParam);
    }

    @ApiOperation("最近的一条就诊记录 - 上饶APP")
    @RequestMapping(value = ServiceApi.Profiles.RecentMedicalEvents, method = RequestMethod.GET)
    public Map<String, Object> recentMedicalEvents(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321199703137824")
            @RequestParam(value = "demographic_id") String demographic_id) throws Exception {
        return patientEvent.recentMedicalEvents(demographic_id);
    }

    @ApiOperation("近期就诊 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.RecentVisits, method = RequestMethod.GET)
    public Map<String, Object> recentVisits(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321199703137824")
            @RequestParam(value = "demographic_id") String demographic_id) throws Exception {
        return patientEvent.recentVisits(demographic_id);
    }

    @ApiOperation("近期就诊详情 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.RecentVisitsSub, method = RequestMethod.GET)
    public Map<String, Object> recentVisitsSub(
            @ApiParam(name = "profile_id", value = "档案ID", required = true, defaultValue = "362321199703137824")
            @RequestParam(value = "profile_id") String profile_id) throws Exception {
        return patientEvent.recentVisitsSub(profile_id);
    }
}
