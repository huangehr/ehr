package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.profile.controller.profile.converter.ProfileUtil;
import com.yihu.ehr.profile.memory.intermediate.MemoryProfile;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 档案接口。提供就诊数据的原始档案，以CDA文档配置作为数据内容架构。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "健康档案服务", description = "提供档案搜索及完整档案下载")
public class ProfileEndPoint extends BaseRestEndPoint {
    private final static String SampleQuery = "{\n" +
            "\"demographicId\": \"412726195111306268\",\n" +
            "\"organizationCode\": \"41872607-9\",\n" +
            "\"patientId\": \"10295435\",\n" +
            "\"eventNo\": \"000622450\",\n" +
            "\"name\": \"段廷兰\",\n" +
            "\"telephone\": \"11\",\n" +
            "\"gender\": \"女\",\n" +
            "\"birthday\": \"1951-11-30\"\n" +
            "}";

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileUtil profileUtil;

    @ApiOperation(value = "获取档案", notes = "读取一份档案")
    @RequestMapping(value = ServiceApi.HealthProfile.Profile, method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws Exception {

        MemoryProfile profile = profileService.getProfile(profileId, loadStdDataSet, loadOriginDataSet);
        return profileUtil.convertProfile(profile, loadStdDataSet || loadOriginDataSet);
    }

    @ApiOperation(value = "获取档案文档", notes = "获取档案文档，按数据集整理")
    @RequestMapping(value = ServiceApi.HealthProfile.ProfileDocument, method = RequestMethod.GET)
    public MProfileDocument getProfileDocument(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "CDA文档ID", defaultValue = "0dae0006568b67720dc35654490ac6da")
            @PathVariable("document_id") String documentId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws Exception {

        MemoryProfile profile = profileService.getProfile(profileId, loadStdDataSet, loadOriginDataSet);
        MProfileDocument profileDocument =  profileUtil.convertDocument(profile, documentId, loadStdDataSet || loadOriginDataSet);

        return profileDocument;
    }

    @ApiOperation(value = "删除档案", notes = "删除一份档案，包括数据集")
    @RequestMapping(value = ServiceApi.HealthProfile.Profile, method = RequestMethod.DELETE)
    public void deleteProfile(@ApiParam(value = "档案ID", defaultValue = "")
                              @PathVariable("profile_id") String profileId) throws Exception {
        profileService.deleteProfile(profileId);
    }
}
