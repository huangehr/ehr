package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.model.InspectionInfo;
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
import java.util.Map;

/**
 * EndPoint - 检查检验
 * Created by progr1mmer on 2018/3/13.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileInspectionEndPoint", description = "检查检验", tags = {"档案影像服务 - 检查检验"})
public class ProfileInspectionEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileInspectionService profileInspectionService;

    @ApiOperation("检查检验记录 - 上饶APP")
    @RequestMapping(value = ServiceApi.Profiles.InspectionRecords, method = RequestMethod.GET)
    public List inspectionRecords(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "健康问题health_problem?hpCode（不传默认查找所有）")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "date", value = "时间 {'start':'2018-03-12T12:00:00Z','end':'2018-03-15T12:00:00Z'}")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "searchParam", value = "搜索条件")
            @RequestParam(value = "searchParam", required = false) String searchParam) throws Exception {
        return profileInspectionService.inspectionRecords(demographic_id, filter, date, searchParam);
    }

    @ApiOperation("检查检验记录统计 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.InspectionStatistics, method = RequestMethod.GET)
    public List<InspectionInfo> inspectionStatistics(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "table", required = true, value = "HDSD00_79 - 检查， HDSD00_77 - 检验")
            @RequestParam(value = "table") String table) throws Exception {
        return profileInspectionService.inspectionStatistics(demographic_id, table);
    }

    @ApiOperation("检查检验记录子项详情（某次检验记录） - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.InspectionStatisticsOneSub, method = RequestMethod.GET)
    public Map<String, String> inspectionStatisticsOneSub(
            @ApiParam(name = "profile_id", value = "档案ID", required = true, defaultValue = "49236052X_17022473_1503229954000$HDSD00_77$3")
            @RequestParam(value = "profile_id") String profile_id) throws Exception {
        return profileInspectionService.inspectionStatisticsOneSub(profile_id);
    }

    @ApiOperation("检查检验记录子项详情（相关检验全部记录） - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.InspectionStatisticsAllSub, method = RequestMethod.GET)
    public Map<String, Map<String, Integer>> inspectionStatisticsAllSub(
            @ApiParam(name = "demographic_id", value = "身份证号码", required = true, defaultValue = "9d2970b8-dac5-40bb-816d-d3e841a29afc")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "name", required = true, value = "检验名称")
            @RequestParam(value = "name") String name) throws Exception {
        return profileInspectionService.inspectionStatisticsAllSub(demographic_id, name);
    }
}
