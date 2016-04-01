package com.yihu.ehr.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.MDataSet;
import com.yihu.ehr.model.MProfile;
import com.yihu.ehr.persist.ProfileService;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.util.encode.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "健康档案服务", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, description = "健康档案获取及查询服务")
public class ProfileEndPoint {
    @Autowired
    private ProfileService profileService;

    @ApiOperation(value = "时间轴", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取患者的就诊档案列表")
    @RequestMapping(value = RestApi.HealthProfile.Profiles, method = RequestMethod.GET)
    public List<MProfile> getProfiles(
            @ApiParam(value = "身份证号,使用Base64编码")
            @PathVariable("demographic_id") String demographicId,
            @ApiParam(value = "起始时间")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束时间")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @ApiParam(value = "是否加载标准数据集")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        demographicId = new String(Base64.decode(demographicId));

        List<Profile> profiles = profileService.getProfiles(demographicId, since, to, loadStdDataSet, loadOriginDataSet);
        return null;
    }

    @ApiOperation(value = "获取档案", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "读取一份档案")
    @RequestMapping(value = RestApi.HealthProfile.Profile, method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID")
            @PathVariable("id") String id) throws IOException, ParseException {
        Profile profile = profileService.getProfile(id, true, true);

        return null;
    }

    @ApiOperation(value = "获取档案数据集", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取档案数据集列表")
    @RequestMapping(value = RestApi.HealthProfile.ProfileDateSets, method = RequestMethod.GET)
    public List<MDataSet> getProfileDataSets(
            @ApiParam(value = "档案ID")
            @PathVariable("id") String id,
            @ApiParam(value = "数据集ID列表")
            @RequestParam("data_sets") String[] dataSetIds) {
        return null;
    }

    @ApiOperation(value = "搜索档案", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "读取一份档案")
    @RequestMapping(value = RestApi.HealthProfile.ProfileSearch, method = RequestMethod.POST)
    public List<MProfile> searchProfile(
            @ApiParam(value = "搜索条件")
            @RequestParam("q") String query) {

        return null;
    }
}
