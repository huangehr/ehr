package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.ProfileMotherhoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by progr1mmer on 2018/5/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileMotherhoodEndPoint", description = "孕产情况", tags = {"档案影像服务 - 孕产情况"})
public class ProfileMotherhoodEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileMotherhoodService profileMotherhoodService;

    @ApiOperation("概况")
    @RequestMapping(value = ServiceApi.Profiles.MotherhoodOverview, method = RequestMethod.GET)
    public Map<String, Object> HealthProblem(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "version", value = "版本号", defaultValue = "59083976eebd")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return profileMotherhoodService.overview(demographic_id, version);
    }

}
