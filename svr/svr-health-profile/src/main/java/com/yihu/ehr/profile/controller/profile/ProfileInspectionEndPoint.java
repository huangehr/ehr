package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.ProfileInspectionService;
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

/**
 * EndPoint - 检查检验（兼容 pc & mobile）
 * Created by progr1mmer on 2018/3/13.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileInspectionEndPoint", description = "检查检验", tags = {"档案影像服务 - 检查检验"})
public class ProfileInspectionEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileInspectionService profileInspectionService;

    @ApiOperation("检查检验记录")
    @RequestMapping(value = ServiceApi.Profiles.InspectionRecords, method = RequestMethod.GET)
    public List inspectionRecords(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "hp_code", value = "健康问题代码（不传默认查找所有）")
            @RequestParam(value = "hp_code", required = false) String hp_code,
            @ApiParam(name = "date", value = "时间 {'start':'2018-03-12T12:00:00Z','end':'2018-03-15T12:00:00Z'}")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "event_type", value = "事件类型")
            @RequestParam(value = "event_type", required = false) String event_type) throws Exception {
        return profileInspectionService.inspectionRecords(demographic_id, hp_code, date, event_type);
    }

}
