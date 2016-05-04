package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.profile.*;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.ProfileIndicesService;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.profile.service.Template;
import com.yihu.ehr.util.DateTimeUtils;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private ProfileIndicesService indicesService;

    @Autowired
    private ProfileUtil profileUtil;

    @ApiOperation(value = "搜索档案", notes = "返回符合条件的档案列表")
    @RequestMapping(value = ServiceApi.HealthProfile.Profiles, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public Collection<MProfile> searchProfile(
            @ApiParam(value = "搜索参数", defaultValue = SampleQuery)
            @RequestBody MProfileSearch query,
            @ApiParam(value = "起始日期", defaultValue = "2015-10-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束日期", defaultValue = "2016-10-01")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws Exception {
        String demographicId = query.getDemographicId();
        String orgCode = query.getOrganizationCode();
        String patientId = query.getPatientId();
        String eventNo = query.getEventNo();
        String name = query.getName();
        String telephone = query.getTelephone();
        String gender = query.getGender();
        Date birthday = DateTimeUtils.simpleDateParse(query.getBirthday());

        Page<ProfileIndices> profileIndices = indicesService.findByIndices(orgCode, patientId, eventNo, since, to, null);
        if (profileIndices == null || profileIndices.getContent().isEmpty()) {
            profileIndices = indicesService.findByDemographic(demographicId, orgCode, name, telephone, gender, birthday, since, to, null);
        }

        if (profileIndices == null || profileIndices.getContent().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No profile found with this query!");
        }

        return profileUtil.loadAndConvertProfiles(profileIndices, false, false);
    }

    @ApiOperation(value = "删除档案", notes = "删除一份档案，包括数据集")
    @RequestMapping(value = ServiceApi.HealthProfile.Profile, method = RequestMethod.DELETE)
    public void deleteProfile(@ApiParam(value = "档案ID", defaultValue = "")
                              @PathVariable("profile_id") String profileId) throws Exception {
        profileService.deleteProfile(profileId);
    }

    @ApiOperation(value = "获取档案", notes = "读取一份档案")
    @RequestMapping(value = ServiceApi.HealthProfile.Profile, method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws Exception {
        StdProfile profile = profileService.getProfile(profileId, loadStdDataSet, loadOriginDataSet);

        return profileUtil.convertProfile(profile, loadStdDataSet || loadOriginDataSet);
    }

    @ApiOperation(value = "获取档案文档", notes = "获取档案文档，按数据集整理")
    @RequestMapping(value = ServiceApi.HealthProfile.ProfileDocument, method = RequestMethod.GET)
    public MProfileDocument getProfileDocument(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "文档ID", defaultValue = "0dae00065684e6570dc35654490aacb3")
            @PathVariable("document_id") String documentId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws Exception {
        StdProfile profile = profileService.getProfile(profileId, loadStdDataSet, loadOriginDataSet);
        Map<Template, MCDADocument> cdaDocuments = profileUtil.getCustomizedCDADocuments(
                profile.getCdaVersion(),
                profile.getOrgCode(),
                profile.getEventType());

        Integer templateId = null;
        MCDADocument cdaDocument = null;
        for (Template template : cdaDocuments.keySet()){
            cdaDocument = cdaDocuments.get(template);
            if (cdaDocument.getId().equals(documentId)){
                templateId = template.getId();
                break;
            }
        }

        if (templateId == null || cdaDocument == null) throw new ApiException(HttpStatus.NOT_FOUND, "Document not found.");

        return profileUtil.convertDocument(profile, cdaDocument, templateId, loadStdDataSet || loadStdDataSet);
    }
}
