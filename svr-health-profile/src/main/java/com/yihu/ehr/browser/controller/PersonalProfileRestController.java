//package com.yihu.ehr.browser.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.yihu.ehr.browser.feign.OrganizationClient;
//import com.yihu.ehr.browser.service.Event;
//import com.yihu.ehr.browser.service.TimeLineModel;
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.model.profile.MProfile;
//import com.yihu.ehr.model.standard.MCDADocument;
//import com.yihu.ehr.util.DateFormatter;
//import com.yihu.ehr.util.RestEcho;
//import com.yihu.ehr.util.controller.BaseRestController;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.*;
//
///**
// * 健康档案个人版接口。实现时间轴与档案的数据获取。
// *
// * @author Sand
// * @version 1.0
// * @created 2015.12.26 16:02
// */
//@RestController
//@RequestMapping(ApiVersion.Version1_0 + "/personal-profile")
//@Api(protocols = "https", value = "personal-profile", description = "健康档案个人版接口")
//public class PersonalProfileRestController extends BaseRestController {
//    @Resource(name = Services.EhrArchiveManager)
//    private XEhrArchiveManager ehrArchiveManager;
//
//
//    @Resource(name = Services.EnvironmentOption)
//    XEnvironmentOption environmentOption;
//
//    @Resource(name = Services.ArchiveTemplateManager)
//    private XArchiveTplManager archiveTplManager;
//
//    @Autowired
//    private OrganizationClient organizationClient;
//
//    // 数据集代码与CDA类别ID之间的映射
//    Map<String, String> cdaTypeMapper;
//
//    @PostConstruct
//    void initCatalogMapper() throws IOException {
//        cdaTypeMapper = new HashMap<>();
//
//        JsonNode cdaCatalogMapper = new ObjectMapper().readTree(environmentOption.getOption(EnvironmentOptions.EhrCdaCatalogMap));
//        Iterator<String> nodeNames = cdaCatalogMapper.fieldNames();
//        while (nodeNames.hasNext()) {
//            String nodeName = nodeNames.next();
//            cdaTypeMapper.put(nodeName, cdaCatalogMapper.get(nodeName).asText());
//        }
//    }
//
//    /**
//     * 时间轴序列请求接口。
//     *
//     * @param demographicId 病人身份证号
//     * @param from          起始时间
//     * @param to            结束时间
//     * @return
//     */
//    @RequestMapping(value = "/timeline", method = RequestMethod.GET)
//    @ApiOperation(value = "时间轴序列请求接口", response = RestEcho.class, produces = "application/json", notes = "读取病人的就诊时间轴序列")
//    public Object timeline(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "demographic_id", value = "病人身份证号码", defaultValue = "412726198711060437", required = true)
//            @RequestParam(value = "demographic_id") String demographicId,
//            @ApiParam(name = "from", value = "起始时间", defaultValue = "2015-01-01", required = true)
//            @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
//            @ApiParam(name = "to", value = "结束时间", defaultValue = "2015-12-19", required = true)
//            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws JsonProcessingException {
//        List<MProfile> archiveList = ehrArchiveManager.getArchiveList(new DemographicId(demographicId), from, to, false, false);
//
//        TimeLineModel timeLineModel = new TimeLineModel();
//        for (MProfile ehrArchive : archiveList) {
//            Event event = new Event();
//            event.summary = ehrArchive.getSummary();
//            event.orgName = organizationClient.getOrg(ehrArchive.getOrgCode()).getFullName();
//            event.date = DateFormatter.simpleDateFormat(ehrArchive.getEventDate());
//
//            // 取得与此档案相关联的CDA类目
//            String cdaType = null;
//            for (String dataSetCode : cdaTypeMapper.keySet()) {
//                if (ehrArchive.getDataSet(dataSetCode) != null) {
//                    cdaType = cdaTypeMapper.get(dataSetCode);
//                    break;
//                }
//            }
//
//            if (cdaType == null) throw new RuntimeException("未找到健康档案类别，CDA类别未配置？");
//
//            // 此类目下卫生机构定制的CDA文档列表
//            MCDADocument[] cdaDocuments = archiveTplManager.getAdaptedDocumentsList(ehrArchive.getOrgCode(), ehrArchive.getCdaVersion(), cdaType);
//            if (cdaDocuments == null || cdaDocuments.length == 0)
//                throw new RuntimeException("未找到CDA文档，机构CDA展示模板未配置？");
//
//            for (MCDADocument cdaDocument : cdaDocuments) {
//                MCDADocument document = new MCDADocument();
//                document.cdaVersion = ehrArchive.getCdaVersion();
//                document.orgCode = ehrArchive.getOrgCode();
//                document.cdaDocumentId = cdaDocument.getId();
//                document.cdaDocumentName = cdaDocument.getName();
//
//                // CDA文档裁剪，根据从医院中实际采集到的数据集，变相对CDA进行裁剪
//                XCdaDatasetRelationship[] datasetRelationships = cdaDocument.getRelationship();
//                for (XCdaDatasetRelationship datasetRelationship : datasetRelationships) {
//                    String stdDataSetCode = datasetRelationship.getDataSetCode();
//                    String originDataSetCode = StdObjectQualifierTranslator.makeOriginDataSetTable(datasetRelationship.getDataSetCode());
//
//                    XEhrDataSet ehrStdDataSet = ehrArchive.getDataSet(stdDataSetCode);
//                    if (ehrStdDataSet != null) {
//                        DataSetModel stdDataSet = createDateSetModel(stdDataSetCode, ehrStdDataSet);
//                        document.dataSets.add(stdDataSet);
//                    }
//
//                    MProfile ehrOriDataSet = ehrArchive.getDataSet(stdDataSetCode);
//                    if (ehrOriDataSet != null) {
//                        DataSetModel oriDataSet = createDateSetModel(originDataSetCode, ehrOriDataSet);
//                        document.dataSets.add(oriDataSet);
//                    }
//                }
//
//                // TODO 仅对有关键数据集的CDA文档展示，若只有病人基本信息数据集则不用展示. 但目前的过滤判断过于简单了
//                if (document.dataSets.size() > 1) event.documents.add(document);
//            }
//
//            timeLineModel.events.add(event);
//        }
//
//        return new ObjectMapper().writeValueAsString(timeLineModel);
//    }
//
//    /**
//     * 时间轴序列请求接口。
//     *
//     * @param demographicId 病人身份证号
//     * @param orgs          机构列表
//     * @param archivetype   文档类型
//     * @param resource      数据源
//     * @param from          起始时间
//     * @param to            结束时间
//     * @return
//     */
//    @RequestMapping(value = "/timeline2", method = RequestMethod.GET)
//    @ApiOperation(value = "时间轴序列请求接口（增加机构列表、文档类型、数据源等查询条件）", response = RestEcho.class, produces = "application/json", notes = "读取病人的就诊时间轴序列")
//    public RestEcho timeline2(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "demographic_id", value = "病人身份证号码", defaultValue = "412726198711060437", required = true)
//            @RequestParam(value = "demographic_id") String demographicId,
//            @ApiParam(name = "orgs", value = "机构列表",  defaultValue = "", required = false)
//            @RequestParam(value = "orgs", required = false)  String[] orgs,
//            @ApiParam(name = "archivetype", value = "文档类型", defaultValue = "", required = false)
//            @RequestParam(value = "archivetype", required = false)  String archivetype,
//            @ApiParam(name = "resource", value = "数据源", defaultValue = "" , required = false)
//            @RequestParam(value = "resource", required = false)  String resource,
//            @ApiParam(name = "from", value = "起始时间", defaultValue = "2015-01-01", required = true)
//            @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
//            @ApiParam(name = "to", value = "结束时间", defaultValue = "2015-12-19", required = true)
//            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
//        try {
//            List<XEhrArchive> archiveList = ehrArchiveManager.getArchiveList(new DemographicId(demographicId),orgs,archivetype,resource, from, to, false, false);
//
//            TimeLineModel timeLineModel = new TimeLineModel();
//            for (XEhrArchive ehrArchive : archiveList) {
//                Event event = new Event();
//                event.summary = ehrArchive.getSummary();
//                event.orgName = orgManager.getOrg(ehrArchive.getOrgCode()).getFullName();
//                event.date = DateFormatter.simpleDateFormat(ehrArchive.getEventDate());
//                event.orgCode = ehrArchive.getOrgCode();
//                // 取得与此档案相关联的CDA类目
//                String cdaType = null;
//                for (String dataSetCode : cdaTypeMapper.keySet()) {
//                    if (ehrArchive.getDataSet(dataSetCode) != null) {
//                        cdaType = cdaTypeMapper.get(dataSetCode);
//                        break;
//                    }
//                }
//
//                if (cdaType == null) throw new RuntimeException("未找到健康档案类别，CDA类别未配置？");
//
//                // 此类目下卫生机构定制的CDA文档列表
//                XCDADocument[] cdaDocuments = archiveTplManager.getAdaptedDocumentsList(ehrArchive.getOrgCode(), ehrArchive.getCdaVersion(), cdaType);
//                if (cdaDocuments == null || cdaDocuments.length == 0)
//                    throw new RuntimeException("未找到CDA文档，机构CDA展示模板未配置？");
//
//                for (XCDADocument cdaDocument : cdaDocuments) {
//                    DocumentModel document = new DocumentModel();
//                    document.cdaVersion = ehrArchive.getCdaVersion();
//                    document.orgCode = ehrArchive.getOrgCode();
//                    document.cdaDocumentId = cdaDocument.getId();
//                    document.cdaDocumentName = cdaDocument.getName();
//
//                    // CDA文档裁剪，根据从医院中实际采集到的数据集，变相对CDA进行裁剪
//                    XCdaDatasetRelationship[] datasetRelationships = cdaDocument.getRelationship();
//                    for (XCdaDatasetRelationship datasetRelationship : datasetRelationships) {
//                        String stdDataSetCode = datasetRelationship.getDataSetCode();
//                        String originDataSetCode = StdObjectQualifierTranslator.makeOriginDataSetTable(datasetRelationship.getDataSetCode());
//
//                        XEhrDataSet ehrStdDataSet = ehrArchive.getDataSet(stdDataSetCode);
//                        if (ehrStdDataSet != null) {
//                            DataSetModel stdDataSet = createDateSetModel(stdDataSetCode, ehrStdDataSet);
//                            document.dataSets.add(stdDataSet);
//                        }
//
//                        XEhrDataSet ehrOriDataSet = ehrArchive.getDataSet(stdDataSetCode);
//                        if (ehrOriDataSet != null) {
//                            DataSetModel oriDataSet = createDateSetModel(originDataSetCode, ehrOriDataSet);
//                            document.dataSets.add(oriDataSet);
//                        }
//                    }
//
//                    // TODO 仅对有关键数据集的CDA文档展示，若只有病人基本信息数据集则不用展示. 但目前的过滤判断过于简单了
//                    if (document.dataSets.size() > 1) event.documents.add(document);
//                }
//
//                timeLineModel.events.add(event);
//            }
//
//            return (RestEcho) succeed(objectMapper.writeValueAsString(timeLineModel));
//        } catch (Exception e) {
//            LogService.getLogger().error(e.getMessage());
//
//            return (RestEcho) failed(ErrorCode.SystemError, e.getMessage());
//        }
//    }
//
//    /**
//     * 根据身份证号查询关联的医院机构详细信息（含省市县信息等等）。
//     *
//     * @param demographicId 病人身份证号
//     * @return
//     */
//    @RequestMapping(value = "/getPersonalArchiveOrg", method = RequestMethod.GET)
//    @ApiOperation(value = "根据身份证号查询关联的医院机构详细信息", response = RestEcho.class, produces = "application/json", notes = "读取病人的就诊机构列表信息")
//    public RestEcho getPersonalArchiveOrg(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "demographic_id", value = "病人身份证号码", defaultValue = "412726198711060437", required = true)
//            @RequestParam(value = "demographic_id") String demographicId,
//            @ApiParam(name = "from", value = "起始时间", defaultValue = "1990-01-01", required = true)
//            @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
//            @ApiParam(name = "to", value = "结束时间", defaultValue = "2099-12-31", required = true)
//            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to ) {
//        try {
//            List<XEhrArchive> archiveList = ehrArchiveManager.getArchiveList(new DemographicId(demographicId), from, to, false, false);
//
//            List<OrgModel>  orgList = new ArrayList<>();
//            XOrganization xOrg;
//            OrgModel orgModel = new OrgModel();
//
//            Set<String> orgSetList = new HashSet<>();
//
//            for (XEhrArchive ehrArchive : archiveList) {
//                orgSetList.add(ehrArchive.getOrgCode());
//            }
//
//            for(String orgCode : orgSetList){
//                xOrg = orgManager.getOrg(orgCode);
//                orgModel = orgManager.getOrgModel(xOrg);
//                orgList.add(orgModel);
//            }
//
//            Map<String,List<OrgModel>> resultMap = new HashMap<>();
//            resultMap.put("orgList",orgList);
//
//            return (RestEcho) succeed(objectMapper.writeValueAsString(resultMap));
//
//        } catch (Exception e) {
//            LogService.getLogger().error(e.getMessage());
//
//            return (RestEcho) failed(ErrorCode.SystemError, e.getMessage());
//        }
//    }
//
//    /**
//     * 创建数据集模型。
//     *
//     * @param dataSetCode
//     * @param ehrDataSet
//     * @return
//     */
//    private DataSetModel createDateSetModel(String dataSetCode, XEhrDataSet ehrDataSet) {
//        DataSetModel dataSet = new DataSetModel();
//        dataSet.dataSetCode = dataSetCode;
//
//        for (String key : ehrDataSet.getRecordKeys()) {
//            dataSet.dataKeys += key + ",";
//        }
//
//        return dataSet;
//    }
//
//    /**
//     * 档案请求。在知道数据集代码及记录KEY的情况下，提取档案数据。与 timeline() 接口配合性能更高。
//     *
//     * @param cdaVersion  CDA版本号
//     * @param dataSetList 数据集列表，JSON结构，包含一次档案请求中包含的数据集及相应的记录ID。
//     * @return
//     */
//    @ApiOperation(value = "档案请求接口", response = RestEcho.class, produces = "application/json", notes = "根据数据集直接加载健康档案，速度快")
//    @RequestMapping(value = "/document_by_ds", method = RequestMethod.GET)
//    public RestEcho loadDocumentByDataSet(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0", required = true)
//            @PathVariable(value = "api_version") String apiVersion,
//            @ApiParam(name = "cda_version", value = "CDA版本", defaultValue = "000000000000", required = true)
//            @RequestParam(value = "cda_version") String cdaVersion,
//            @ApiParam(name = "data_set_list", value = "数据集列表", required = true)
//            @RequestParam(value = "data_set_list") String dataSetList,
//            @ApiParam(name = "origin_data", value = "原始数据", required = true)
//            @RequestParam(value = "origin_data") boolean originData) {
//        try {
//            ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(dataSetList);
//
//            ObjectNode profileDataSets = objectMapper.createObjectNode();
//            for (int i = 0; i < arrayNode.size(); ++i) {
//                JsonNode dataSetNode = arrayNode.get(i);
//                String dataSetCode = dataSetNode.get("dataSetCode").asText();
//
//                if ((originData && StdObjectQualifierTranslator.isOriginDataSet(dataSetCode)) ||
//                        (!originData && !StdObjectQualifierTranslator.isOriginDataSet(dataSetCode))) {
//                    String[] keys = dataSetNode.get("dataKeys").asText().split(",");
//
//                    XEhrDataSet ehrDataSet = ehrArchiveManager.getDataSet(cdaVersion, dataSetCode, keys);
//
//                    // 需要去掉后台表的 _ORIGIN 结尾
//                    if (originData)
//                        dataSetCode = dataSetCode.substring(0, dataSetCode.indexOf(StdObjectQualifierTranslator.OriginDataSetFlag));
//
//                    profileDataSets.set(dataSetCode, ehrDataSet.toJson(true).get(ehrDataSet.getCode()));
//                }
//            }
//
//            return (RestEcho) succeed(objectMapper.writeValueAsString(profileDataSets));
//        } catch (Exception e) {
//            LogService.getLogger().error(e.getMessage());
//
//            return (RestEcho) failed(ErrorCode.SystemError, e.getMessage());
//        }
//    }
//}
