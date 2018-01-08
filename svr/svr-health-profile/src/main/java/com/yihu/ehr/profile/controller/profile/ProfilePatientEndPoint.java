package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
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

    @ApiOperation("患者患病史JSON")
    @RequestMapping(value = ServiceApi.Profiles.ProfileHistory, method = RequestMethod.GET)
    public String ProfileHistory(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id) {

        return "[{\"pastHistoryType\":\"家族病史\",\"pastHistoryContents\":\"也就是医学中常常提到的家族史，也指某一种病的患者的家族成员（较大范围的家族成员，不仅限于祖孙等直系亲属）中发病情况。家族病史分为阴性跟阳性。 1)阴性（即没有发现同样病的患者）。临床上无家族史 2)阳性（即发现有同样病的患者）。比如：临床上讲糖尿病家族史、高血压病家族史、遗传型疾病家族史等。\"},\n" +
                "  {\"pastHistoryType\":\"传染史\",\"pastHistoryContents\":\"传染史..\"},\n" +
                "  {\"pastHistoryType\":\"家族史\",\"pastHistoryContents\":\"家族史..\"},\n" +
                "  {\"pastHistoryType\":\"手术史\",\"pastHistoryContents\":\"手术史..\"}]";
    }

}
