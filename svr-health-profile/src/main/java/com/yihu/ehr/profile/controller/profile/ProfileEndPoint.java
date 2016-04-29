package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.profile.*;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.profile.config.CdaDocumentOptions;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.core.StdDataSet;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.ProfileIndicesService;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.profile.service.Template;
import com.yihu.ehr.profile.service.TemplateService;
import com.yihu.ehr.schema.OrgKeySchema;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.DateTimeUtils;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.htrace.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
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
    private TemplateService templateService;

    @Autowired
    private XCDADocumentClient cdaDocumentClient;

    @Autowired
    CdaDocumentOptions cdaDocumentOptions;

    @Autowired
    CacheReader cacheReader;

    @Autowired
    OrgKeySchema orgKeySchema;

    @Autowired
    StdKeySchema stdKeySchema;

    @ApiOperation(value = "搜索档案", notes = "返回符合条件的档案列表")
    @RequestMapping(value = ServiceApi.HealthProfile.Profiles, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public Collection<MProfile> searchProfile(
            @ApiParam(value = "搜索参数", defaultValue = SampleQuery)
            @RequestBody MProfileSearch query,
            @ApiParam(value = "起始日期", defaultValue = "2015-10-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束日期", defaultValue = "2016-10-01")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws IOException, ParseException {
        String demographicId = query.getDemographicId();
        String orgCode = query.getOrganizationCode();
        String patientId = query.getPatientId();
        String eventNo = query.getEventNo();
        String name = query.getName();
        String telephone = query.getTelephone();
        String gender = query.getGender();
        Date birthday = DateTimeUtils.simpleDateParse(query.getBirthday());

        Page<ProfileIndices> profileIndices = indicesService.findByIndices(orgCode, patientId, eventNo, since, to, null);
        if (profileIndices.getContent().size() == 0) {
            profileIndices = indicesService.findByDemographic(demographicId, orgCode, name, telephone, gender, birthday, since, to, null);
        }

        return loadAndConvertProfiles(profileIndices, false, false);
    }

    @ApiOperation(value = "删除档案", notes = "删除一份档案，包括数据集")
    @RequestMapping(value = ServiceApi.HealthProfile.Profile, method = RequestMethod.DELETE)
    public void deleteProfile(@ApiParam(value = "档案ID", defaultValue = "")
                              @PathVariable("profile_id") String profileId) throws IOException {
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
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        StdProfile profile = profileService.getProfile(profileId, loadStdDataSet, loadOriginDataSet);

        return convertProfile(profile);
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
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        StdProfile profile = profileService.getProfile(profileId, loadStdDataSet, loadOriginDataSet);
        Map<Template, MCDADocument> cdaDocuments = getCustomizedCDADocuments(profile);

        return convertDocument(profile, cdaDocuments, documentId);
    }

    private Collection<MProfile> loadAndConvertProfiles(Page<ProfileIndices> profileIndices,
                                                        boolean loadStdDataSet,
                                                        boolean loadOriginDataSet) throws IOException, ParseException {
        if (profileIndices.getContent().size() == 0) return null;

        List<StdProfile> profiles = new ArrayList<>();
        for (ProfileIndices indices : profileIndices) {
            StdProfile profile = profileService.getProfile(indices.getProfileId(), loadStdDataSet, loadOriginDataSet);
            profiles.add(profile);
        }

        List<MProfile> profileList = new ArrayList<>();
        for (StdProfile profile : profiles) {
            MProfile mProfile = convertProfile(profile);

            profileList.add(mProfile);
        }

        return profileList;
    }

    /**
     * 卫生机构定制的CDA文档列表
     */
    private Map<Template, MCDADocument> getCustomizedCDADocuments(StdProfile profile) {
        // 使用CDA类别关键数据元映射，取得与此档案相关联的CDA类别ID
        String cdaType = null;
        for (StdDataSet dataSet : profile.getDataSets()) {
            if (cdaDocumentOptions.isPrimaryDataSet(dataSet.getCode())) {
                cdaType = cdaDocumentOptions.getCdaDocumentTypeId(dataSet.getCode());
                break;
            }
        }

        if (cdaType == null) {
            throw new RuntimeException("Cannot find cda document type by data set code, forget primary data set & cda document mapping?");
        }

        // 此类目下卫生机构定制的CDA文档列表
        Map<Template, MCDADocument> cdaDocuments = templateService.getOrganizationTemplates(profile.getOrgCode(), profile.getCdaVersion(), cdaType);
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error("Unable to get cda document of version " + profile.getCdaVersion()
                    + " for organization " + profile.getOrgCode() + ", template not uploaded?");

            return null;
        }

        return cdaDocuments;
    }

    private MProfile convertProfile(StdProfile profile) {
        MProfile mProfile = new MProfile();
        mProfile.setId(profile.getId());
        mProfile.setCdaVersion(profile.getCdaVersion());
        mProfile.setOrgCode(profile.getOrgCode());
        mProfile.setOrgName(cacheReader.read(orgKeySchema.name(profile.getOrgCode())));
        mProfile.setEventDate(profile.getEventDate());
        mProfile.setDemographicId(profile.getDemographicId());
        mProfile.setProfileType(profile.getProfileType());
        mProfile.setEventType(profile.getEventType());

        Map<Template, MCDADocument> cdaDocuments = getCustomizedCDADocuments(profile);
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error("Unable to get cda document of version " + profile.getCdaVersion()
                    + " for organization " + profile.getOrgCode() + ", template not uploaded?");

            return null;
        }

        for (Template template : cdaDocuments.keySet()) {
            MCDADocument cdaDocument = cdaDocuments.get(template);
            MProfileDocument document = new MProfileDocument();
            document.setId(cdaDocument.getId());
            document.setName(cdaDocument.getName());
            document.setTemplateId(template.getId());

            // CDA文档裁剪，根据从医院中实际采集到的数据集，对CDA进行裁剪
            boolean validDocument = false;
            List<MCdaDataSetRelationship> datasetRelationships = cdaDocumentClient.getCDADataSetRelationshipByCDAId(
                    profile.getCdaVersion(),
                    cdaDocument.getId());

            for (MCdaDataSetRelationship datasetRelationship : datasetRelationships) {
                String dataSetId = datasetRelationship.getDataSetId();
                String dataSetCode = cacheReader.read(stdKeySchema.dataSetCode(profile.getCdaVersion(), dataSetId));
                if (StringUtils.isEmpty(dataSetCode)) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Data set of version " + profile.getCdaVersion() + " not cached in redis.");
                }

                convertDataSet(profile.getCdaVersion(), document, profile.getDataSet(dataSetCode));

                convertDataSet(profile.getCdaVersion(), document, profile.getDataSet(DataSetUtil.originDataSetCode(dataSetCode)));

                if (!validDocument) validDocument = !dataSetCode.startsWith("HDSA00_01");
            }

            if (validDocument) mProfile.getDocuments().add(document);
        }

        return mProfile;
    }

    private MProfileDocument convertDocument(StdProfile profile, Map<Template, MCDADocument> cdaDocuments, String documentId) {
        for (Template template : cdaDocuments.keySet()) {
            MCDADocument cdaDocument = cdaDocuments.get(template);
            if (!cdaDocument.getId().equals(documentId)) continue;

            MProfileDocument document = new MProfileDocument();
            document.setId(cdaDocument.getId());
            document.setName(cdaDocument.getName());
            document.setTemplateId(template.getId());

            // CDA文档裁剪，根据从医院中实际采集到的数据集，对CDA进行裁剪
            List<MCdaDataSetRelationship> datasetRelationships = cdaDocumentClient.getCDADataSetRelationshipByCDAId(
                    profile.getCdaVersion(),
                    cdaDocument.getId());

            for (MCdaDataSetRelationship datasetRelationship : datasetRelationships) {
                String dataSetId = datasetRelationship.getDataSetId();
                String dataSetCode = cacheReader.read(stdKeySchema.dataSetCode(profile.getCdaVersion(), dataSetId));
                if (StringUtils.isEmpty(dataSetCode)) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Data set of version " + profile.getCdaVersion() + " not cached in redis.");
                }

                convertDataSet(profile.getCdaVersion(), document, profile.getDataSet(dataSetCode));

                convertDataSet(profile.getCdaVersion(), document, profile.getDataSet(DataSetUtil.originDataSetCode(dataSetCode)));
            }

            return document;
        }

        return null;
    }

    private void convertDataSet(String version, MProfileDocument document, StdDataSet dataSet) {
        if (dataSet != null) {
            String dataSetCode = DataSetUtil.standardDataSetCode(dataSet.getCode());

            MDataSet mDataSet = new MDataSet();
            mDataSet.setName(cacheReader.read(stdKeySchema.dataSetNameByCode(version, dataSetCode)));
            mDataSet.setCode(dataSet.getCode());

            for (String key : dataSet.getRecordKeys()) {
                if(dataSet.getRecord(key) != null) {
                    mDataSet.getRecords().put(key, new MRecord(dataSet.getRecord(key).getMetaDataGroup()));
                } else {
                    mDataSet.getRecords().put(key, null);
                }
            }

            document.getDataSets().add(mDataSet);
        }
    }
}
