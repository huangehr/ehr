package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.ProfileMedicationService;
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
 * EndPoint - 用药记录（兼容 pc & mobile）
 * Created by progr1mmer on 2018/3/13.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileMedicationEndPoint", description = "用药记录", tags = {"档案影像服务 - 用药记录"})
public class ProfileMedicationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileMedicationService profileMedicationService;

    @ApiOperation("用药记录")
    @RequestMapping(value = ServiceApi.Profiles.MedicationRecords, method = RequestMethod.GET)
    public List medicationRecords(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "健康问题health_problem=hpCode（不传默认查找所有）")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "date", value = "时间")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "key_word", value = "关键字")
            @RequestParam(value = "key_word", required = false) String key_word) throws Exception {
        return profileMedicationService.medicationRecords(demographic_id, filter, date, key_word);
    }

    @ApiOperation("用药排行")
    @RequestMapping(value = ServiceApi.Profiles.MedicationRanking, method = RequestMethod.GET)
    public Map<String, Integer> medicationRanking(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "hp_code", value = "健康问题代码（不传默认查找所有）")
            @RequestParam(value = "hp_code", required = false) String hp_code) throws Exception {
        return profileMedicationService.medicationRanking(demographic_id, hp_code);
    }


    @ApiOperation("用药详情")
    @RequestMapping(value = ServiceApi.Profiles.MedicationSub, method = RequestMethod.GET)
    public Map<String, Object> medicationSub(
            @ApiParam(name = "profile_id", value = "主表事件索引", required = true, defaultValue = "49229004X_000406450000000UX0_1485608518000")
            @RequestParam(value = "profile_id") String profile_id) throws Exception {
        return profileMedicationService.medicationSub(profile_id);
    }

}
