package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.ProfileDiseaseService;
import com.yihu.ehr.profile.service.ProfileInfoBaseService;
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
 * Created by progr1mmer on 2018/1/7.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfilePatientEndPoint ", description = "患者基本信息", tags = {"档案影像服务 - 患者基本信息"})
public class ProfilePatientEndPoint extends EnvelopRestEndPoint{

    @Autowired
    private ProfileInfoBaseService patientInfoBaseService;

    @ApiOperation("患者基本信息OK")
    @RequestMapping(value = ServiceApi.Profiles.ProfileInfo, method = RequestMethod.GET)
    public Map<String, Object> profileInfo(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "version", value = "版本号", defaultValue = "59083976eebd")
            @RequestParam(value = "version", required = false) String version) {
        return patientInfoBaseService.getPatientInfo(demographic_id, version);
    }

    @ApiOperation("既往史")
    @RequestMapping(value = ServiceApi.Profiles.ProfileHistory, method = RequestMethod.GET)
    public List<Map<String, Object>> profileHistory(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id) {
        return patientInfoBaseService.profileHistory(demographic_id);
    }

    @ApiOperation("个人史")
    @RequestMapping(value = ServiceApi.Profiles.PersonHistory, method = RequestMethod.GET)
    public Map<String, Object> personHistory(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id) {
        return patientInfoBaseService.personHistory(demographic_id);
    }
}
