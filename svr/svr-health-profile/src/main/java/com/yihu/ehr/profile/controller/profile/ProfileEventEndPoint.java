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
    public List<Map<String, Object>> visits (
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "过滤条件(key1=val1;key2=val2)")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "blurry_type", value = "针对需要对特殊档案类型进行查询的参数" +
                    "(0-门诊 1-住院 2-体检 3-影像 4-检查 5-检验 6-妇幼 7-免疫)；" +
                    "此处有值的话filter参数中不能再包含event_type")
            @RequestParam(value = "blurry_type", required = false) String blurry_type,
            @ApiParam(name = "date", value = "时间，格式如：{\"start\":\"2018-01-01T00:00:00Z\",\"end\":\"2018-02-01T00:00:00Z\",\"month\":\"2018-03\"}")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "searchParam", value = "搜索条件（此参数只针对机构和诊断）")
            @RequestParam(value = "searchParam", required = false) String searchParam) throws Exception {
        return patientEvent.visits(demographic_id, filter, blurry_type, date, searchParam);
    }

    @ApiOperation("最近的一条就诊记录 - 上饶APP")
    @RequestMapping(value = ServiceApi.Profiles.RecentMedicalEvents, method = RequestMethod.GET)
    public Map<String, Object> recentVisit (
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id) throws Exception {
        return patientEvent.recentVisit(demographic_id, -30);
    }

    @ApiOperation("近期就诊列表 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.RecentVisits, method = RequestMethod.GET)
    public List<Map<String, Object>> recentVisits (
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id) throws Exception {
        return patientEvent.recentVisits(demographic_id, -180);
    }

    @ApiOperation("近期就诊详情 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.RecentVisitsSub, method = RequestMethod.GET)
    public Map<String, Object> recentVisitsSub (
            @ApiParam(name = "profile_id", value = "档案ID", required = true, defaultValue = "49229004X_000000481520_1513758586000")
            @RequestParam(value = "profile_id") String profile_id) throws Exception {
        return patientEvent.recentVisitsSub(profile_id);
    }
}
