package com.yihu.ehr.profile.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.profile.core.Profile;
import com.yihu.ehr.profile.model.MDataSet;
import com.yihu.ehr.profile.model.MDocument;
import com.yihu.ehr.profile.model.MProfile;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import com.yihu.ehr.profile.persist.repo.XProfileIndicesRepo;
import com.yihu.ehr.util.controller.BaseEndPoint;
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
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "健康档案服务", description = "健康档案获取及查询服务")
public class ProfileEndPoint extends BaseEndPoint {
    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private XProfileIndicesRepo profileIndicesRepo;

    // 数据集代码与CDA类别ID之间的映射
    Map<String, String> cdaTypeMapper;

    @ApiOperation(value = "按时间获取档案列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取患者的就诊档案列表")
    @RequestMapping(value = RestApi.HealthProfile.Profiles, method = RequestMethod.GET)
    public Collection<MProfile> getProfiles(
            @ApiParam(value = "身份证号,使用Base64编码", defaultValue = "NDUxMzAwMTk5MzA4MjgxMjk0")
            @PathVariable("demographic_id") String demographicId,
            @ApiParam(value = "起始时间", defaultValue = "2016-01-01")
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

        List<Profile> profiles = new ArrayList<>();
        for (ProfileIndices indices : profileIndices) {
            Profile profile = profileRepo.findOne(indices.getRowkey(), loadStdDataSet, loadOriginDataSet);
            profiles.add(profile);
        }

        for (Profile profile : profiles) {
            MProfile mProfile = new MProfile();
            mProfile.setSummary(profile.getSummary());
            mProfile.setOrgCode(profile.getOrgCode());
            mProfile.setOrgName(profile.getOrgName());
            mProfile.setDate(profile.getEventDate());

            // 取得与此档案相关联的CDA类目
            String cdaType = null;
            for (String dataSetCode : cdaTypeMapper.keySet()) {
                if (profile.getDataSet(dataSetCode) != null) {
                    cdaType = cdaTypeMapper.get(dataSetCode);
                    break;
                }
            }

            if (cdaType == null) throw new RuntimeException("Cannot find cda document type by data set code, forget primary data set & cda document mapping?");

            // 此类目下卫生机构定制的CDA文档列表
            MCDADocument[] cdaDocuments = archiveTplManager.getAdaptedDocumentsList(profile.getOrgCode(), profile.getCdaVersion(), cdaType);
            if (cdaDocuments == null || cdaDocuments.length == 0)
                throw new RuntimeException("Unable to get cda document of version " + profile.getCdaVersion()
                        + " for organization " + profile.getOrgCode() + ", template not uploaded?");

            for (MCDADocument cdaDocument : cdaDocuments) {
                MDocument document = new MDocument();
                document.cdaVersion = profile.getCdaVersion();
                document.orgCode = profile.getOrgCode();
                document.cdaDocumentId = cdaDocument.getId();
                document.cdaDocumentName = cdaDocument.getName();

                // CDA文档裁剪，根据从医院中实际采集到的数据集，对CDA进行裁剪
                boolean validDocument = false;
                XCdaDatasetRelationship[] datasetRelationships = cdaDocument.getRelationship();
                for (XCdaDatasetRelationship datasetRelationship : datasetRelationships) {
                    String stdDataSetCode = datasetRelationship.getDataSetCode();
                    String originDataSetCode = StdObjectQualifierTranslator.makeOriginDataSetTable(datasetRelationship.getDataSetCode());

                    XEhrDataSet ehrStdDataSet = ehrArchive.getDataSet(stdDataSetCode);
                    if (ehrStdDataSet != null) {
                        DataSetModel stdDataSet = createDateSetModel(stdDataSetCode, ehrStdDataSet);
                        document.dataSets.add(stdDataSet);

                        if (!validDocument) validDocument = !ehrStdDataSet.getCode().startsWith("HDSA00_01");
                    }

                    XEhrDataSet ehrOriDataSet = ehrArchive.getDataSet(originDataSetCode);
                    if (ehrOriDataSet != null) {
                        DataSetModel oriDataSet = createDateSetModel(originDataSetCode, ehrOriDataSet);
                        document.dataSets.add(oriDataSet);

                        if (!validDocument) validDocument = !ehrStdDataSet.getCode().startsWith("HDSA00_01");
                    }
                }

                // TODO 仅对有关键数据集的CDA文档展示，若只有病人基本信息数据集则不用展示
                if (validDocument) mProfile.documents.add(document);
            }
        }

        return convertToModels(profiles, new ArrayList<>(profiles.size()), MProfile.class, null);
    }

    @ApiOperation(value = "获取档案", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "读取一份档案")
    @RequestMapping(value = RestApi.HealthProfile.Profile, method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID")
            @PathVariable("id") String id,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        Profile profile = profileRepo.findOne(id, loadStdDataSet, loadOriginDataSet);

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
