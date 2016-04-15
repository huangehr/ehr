package com.yihu.ehr.profile.controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.profile.MDataSet;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MRecord;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.profile.config.CdaDocumentOptions;
import com.yihu.ehr.profile.core.commons.DataSetTableOption;
import com.yihu.ehr.profile.core.structured.StructuredDataSet;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import com.yihu.ehr.profile.persist.repo.XProfileIndicesRepo;
import com.yihu.ehr.profile.service.TemplateService;
import com.yihu.ehr.schema.OrgKeySchema;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "健康档案服务", description = "健康档案服务")
public class ProfileEndPoint extends BaseRestEndPoint {
    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private XProfileIndicesRepo profileIndicesRepo;

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

    @ApiOperation(value = "搜索档案", notes = "读取一份档案")
    @RequestMapping(value = RestApi.HealthProfile.ProfileSearch, method = RequestMethod.POST)
    public List<MProfile> searchProfile(
            @ApiParam(value = "搜索条件")
            @RequestParam("q") String query) {
        return null;
    }

    @ApiOperation(value = "按时间获取档案列表", notes = "获取患者的就诊档案列表")
    @RequestMapping(value = RestApi.HealthProfile.Profiles, method = RequestMethod.GET)
    public Collection<MProfile> getProfiles(
            @ApiParam(value = "身份证号,使用Base64编码", defaultValue = "NDEyNzI2MTk1MTExMzA2MjY4")
            @RequestParam("demographic_id") String demographicId,
            @ApiParam(value = "起始时间", defaultValue = "2015-01-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束时间", defaultValue = "2016-12-31")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        demographicId = new String(Base64.decode(demographicId));

        List<ProfileIndices> profileIndices = profileIndicesRepo.findByDemographicIdAndEventDateBetween(
                demographicId, since, to);

        List<StructuredProfile> profiles = new ArrayList<>();
        for (ProfileIndices indices : profileIndices) {
            StructuredProfile profile = profileRepo.findOne(indices.getRowkey(), loadStdDataSet, loadOriginDataSet);
            profiles.add(profile);
        }

        List<MProfile> profileList = new ArrayList<>();
        for (StructuredProfile profile : profiles) {
            MProfile mProfile = convertProfile(profile);

            profileList.add(mProfile);
        }

        return profileList;
    }

    @ApiOperation(value = "获取档案", notes = "读取一份档案")
    @RequestMapping(value = RestApi.HealthProfile.Profile, method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        StructuredProfile profile = profileRepo.findOne(profileId, loadStdDataSet, loadOriginDataSet);

        return convertProfile(profile);
    }

    @ApiOperation(value = "获取档案文档", notes = "获取档案文档，按数据集整理")
    @RequestMapping(value = RestApi.HealthProfile.ProfileDocument, method = RequestMethod.GET)
    public MProfileDocument getProfileDocument(
            @ApiParam(value = "档案ID")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "文档ID")
            @PathVariable("document_id") String documentId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        StructuredProfile profile = profileRepo.findOne(profileId, loadStdDataSet, loadOriginDataSet);
        Collection<MCDADocument> cdaDocuments = getCustomizedCDADocuments(profile);

        return convertDocument(profile, cdaDocuments, documentId);
    }

    /**
     * 此类目下卫生机构定制的CDA文档列表
     */
    private Collection<MCDADocument> getCustomizedCDADocuments(StructuredProfile profile){
        // 使用CDA类别关键数据元映射，取得与此档案相关联的CDA类别ID
        String cdaType = null;
        for (StructuredDataSet dataSet : profile.getDataSets()){
            if(cdaDocumentOptions.isPrimaryDataSet(dataSet.getCode())){
                cdaType = cdaDocumentOptions.getCdaDocumentTypeId(dataSet.getCode());
                break;
            }
        }

        if (cdaType == null) {
            throw new RuntimeException("Cannot find cda document type by data set code, forget primary data set & cda document mapping?");
        }

        // 此类目下卫生机构定制的CDA文档列表
        Collection<MCDADocument> cdaDocuments = templateService.getOrganizationTemplates(profile.getOrgCode(), profile.getCdaVersion(), cdaType);
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error("Unable to get cda document of version " + profile.getCdaVersion()
                    + " for organization " + profile.getOrgCode() + ", template not uploaded?");

            return null;
        }

        return cdaDocuments;
    }

    private MProfile convertProfile(StructuredProfile profile){
        MProfile mProfile = new MProfile();
        mProfile.setId(profile.getId());
        mProfile.setCdaVersion(profile.getCdaVersion());
        mProfile.setOrgCode(profile.getOrgCode());
        mProfile.setOrgName(cacheReader.read(orgKeySchema.name(profile.getOrgCode())));
        mProfile.setEventDate(profile.getEventDate());
        mProfile.setSummary(profile.getSummary());
        mProfile.setDemographicId(profile.getDemographicId());

        Collection<MCDADocument> cdaDocuments = getCustomizedCDADocuments(profile);
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error("Unable to get cda document of version " + profile.getCdaVersion()
                    + " for organization " + profile.getOrgCode() + ", template not uploaded?");

            return null;
        }

        for (MCDADocument cdaDocument : cdaDocuments) {
            MProfileDocument document = new MProfileDocument();
            document.setId(cdaDocument.getId());
            document.setName(cdaDocument.getName());

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

                addDataset(profile.getCdaVersion(), document, profile.getDataSet(dataSetCode));

                addDataset(profile.getCdaVersion(), document, profile.getDataSet(DataSetTableOption.originDataSetCode(dataSetCode)));

                if (!validDocument) validDocument = !dataSetCode.startsWith("HDSA00_01");
            }

            if (validDocument) mProfile.getDocuments().add(document);
        }

        return mProfile;
    }

    private MProfileDocument convertDocument(StructuredProfile profile, Collection<MCDADocument> cdaDocuments, String documentId){
        for (MCDADocument cdaDocument : cdaDocuments) {
            if (!cdaDocument.getId().equals(documentId)) continue;

            MProfileDocument document = new MProfileDocument();
            document.setId(cdaDocument.getId());
            document.setName(cdaDocument.getName());

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

                addDataset(profile.getCdaVersion(), document, profile.getDataSet(dataSetCode));

                addDataset(profile.getCdaVersion(), document, profile.getDataSet(DataSetTableOption.originDataSetCode(dataSetCode)));
            }

            return document;
        }

        return null;
    }

    private void addDataset(String version, MProfileDocument document, StructuredDataSet dataSet) {
        if (dataSet != null) {
            String dataSetCode = DataSetTableOption.standardDataSetCode(dataSet.getCode());

            MDataSet mDataSet = new MDataSet();
            mDataSet.setName(cacheReader.read(stdKeySchema.dataSetNameByCode(version, dataSetCode)));
            mDataSet.setCode(dataSet.getCode());

            Map<String, MRecord> records = new HashMap<>();
            mDataSet.setRecords(records);

            for (String key : dataSet.getRecordKeys()){
                records.put(key, new MRecord(dataSet.getRecord(key)));
            }

            document.getDataSets().add(mDataSet);
        }
    }
}
