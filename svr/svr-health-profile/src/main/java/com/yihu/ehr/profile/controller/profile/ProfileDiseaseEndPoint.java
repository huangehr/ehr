package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.ProfileDiseaseService;
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
 * Created by progr1mmer on 2017/12/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileDiseaseEndPoint", description = "健康问题", tags = {"档案影像服务 - 健康问题"})
public class ProfileDiseaseEndPoint extends EnvelopRestEndPoint{

    @Autowired
    private ProfileDiseaseService profileDiseaseService;

    @ApiOperation("主要健康问题OK")
    @RequestMapping(value = ServiceApi.Profiles.HealthProblem, method = RequestMethod.GET)
    public List<Map<String, Object>> HealthProblem(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id) {
        return profileDiseaseService.getHealthProblem(demographic_id);
    }

    @ApiOperation("主要健康问题诊断详情OK")
    @RequestMapping(value = ServiceApi.Profiles.HealthProblemSub, method = RequestMethod.GET)
    public List<Map<String, Object>> HealthProblemSub(
            @ApiParam(name = "last_visit_record", value = "最后一次诊断记录", required = true, defaultValue = "49229004X_000406450000000UX0_1485608518000")
            @RequestParam(value = "last_visit_record") String last_visit_record,
            @ApiParam(name = "event_type", value = "事件类型", required = true, defaultValue = "0")
            @RequestParam(value = "event_type") String event_type) {
        return profileDiseaseService.getHealthProblemSub(last_visit_record, event_type);
    }


}
