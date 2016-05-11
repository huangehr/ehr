package com.yihu.ehr.profile.controller.profile.converter;

import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.profile.MDataSet;
import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.model.profile.MRecord;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.profile.config.CdaDocumentTypeOptions;
import com.yihu.ehr.profile.core.EventType;
import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.StdDataSet;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.profile.service.Template;
import com.yihu.ehr.profile.service.TemplateService;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.schema.OrgKeySchema;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 健康档案工具。
 *
 * @author Sand
 * @created 2016.05.03 14:08
 */
@Service
public class StdProfileConverter {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private XCDADocumentClient cdaDocumentClient;

    @Autowired
    private CdaDocumentTypeOptions cdaDocumentTypeOptions;

    @Autowired
    private CacheReader cacheReader;

    @Autowired
    private OrgKeySchema orgKeySchema;

    @Autowired
    private StdKeySchema stdKeySchema;

    /*public Collection<MProfile> loadProfiles(Page<ProfileIndices> profileIndices,
                                             boolean loadStdDataSet,
                                             boolean loadOriginDataSet) throws Exception {
        if (profileIndices.getContent().size() == 0) return null;

        List<StdProfile> profiles = new ArrayList<>();
        for (ProfileIndices indices : profileIndices) {
            StdProfile profile = profileService.getProfile(indices.getProfileId(), loadStdDataSet, loadOriginDataSet);
            profiles.add(profile);
        }

        List<MProfile> profileList = new ArrayList<>();
        for (StdProfile profile : profiles) {
            MProfile mProfile = convertProfile(profile, loadStdDataSet || loadOriginDataSet);

            profileList.add(mProfile);
        }

        return profileList;
    }*/

    public MProfile convertProfile(StdProfile profile, boolean containDataSet) {
        MProfile mProfile = new MProfile();
        mProfile.setId(profile.getId());
        mProfile.setCdaVersion(profile.getCdaVersion());
        mProfile.setOrgCode(profile.getOrgCode());
        mProfile.setOrgName(cacheReader.read(orgKeySchema.name(profile.getOrgCode())));
        mProfile.setEventDate(profile.getEventDate());
        mProfile.setDemographicId(profile.getDemographicId());
        mProfile.setClientId(profile.getClientId());
        mProfile.setProfileType(profile.getProfileType());
        mProfile.setEventType(profile.getEventType());

        convertDocuments(profile, mProfile, containDataSet);

        return mProfile;
    }

    protected void convertDocuments(StdProfile profile, MProfile mProfile, boolean containDataSet){
        Map<Template, MCDADocument> cdaDocuments = getCustomizedCDADocuments(profile.getCdaVersion(), profile.getOrgCode(), profile.getEventType());
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error("Unable to get cda document of version " + profile.getCdaVersion()
                    + " for organization " + profile.getOrgCode() + ", template not uploaded?");

            return;
        }

        for (Template template :cdaDocuments.keySet()){
            MProfileDocument document = convertDocument(profile, cdaDocuments.get(template), template.getId(), containDataSet);
            if(document != null) mProfile.getDocuments().add(document);
        }
    }

    public MProfileDocument convertDocument(StdProfile profile, MCDADocument cdaDocument, Integer templateId, boolean containDataSet) {
        MProfileDocument document = new MProfileDocument();
        document.setId(cdaDocument.getId());
        document.setName(cdaDocument.getName());
        document.setTemplateId(templateId);

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

            if (containDataSet) {
                convertDataSet(profile.getCdaVersion(), document, profile.getDataSet(dataSetCode));
                convertDataSet(profile.getCdaVersion(), document, profile.getDataSet(DataSetUtil.originDataSetCode(dataSetCode)));
            }

            // 判断是否是一个正常的文档: 不只含有人口学信息部分
            if (!validDocument && profile.getDataSet(dataSetCode) != null) validDocument = !dataSetCode.startsWith("HDSA00_01");

            dataSetCode = DataSetUtil.originDataSetCode(dataSetCode);
            if (!validDocument && profile.getDataSet(dataSetCode) != null) validDocument = !dataSetCode.startsWith("HDSA00_01");
        }

        return validDocument ? document : null;
    }

    public void convertDataSet(String version, MProfileDocument document, StdDataSet dataSet) {
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

    /**
     * 卫生机构定制的CDA文档列表
     *
     * 定制的CDA文档列表根据档案中的数据集列表，从而获取这份档案的CDA类别。此CDA类别包含与文档相关的模板，CDA文档。
     */
    public Map<Template, MCDADocument> getCustomizedCDADocuments(String cdaVersion, String orgCode, EventType eventType) {
        // 使用CDA类别关键数据元映射，取得与此档案相关联的CDA类别ID
        String cdaType = cdaDocumentTypeOptions.getCdaDocumentTypeId(Integer.toString(eventType.getType()));

        if (StringUtils.isEmpty(cdaType)) {
            throw new RuntimeException("Cannot find cda document type by health event, forget event & cda document type mapping?");
        }

        // 此类目下卫生机构定制的CDA文档列表
        Map<Template, MCDADocument> cdaDocuments = templateService.getOrganizationTemplates(orgCode, cdaVersion, cdaType);
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error("Unable to get cda document of version " + cdaVersion
                    + " for organization " + orgCode + ", template not prepared?");

            return null;
        }

        return cdaDocuments;
    }
}
