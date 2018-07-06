package com.yihu.ehr.resolve.service.resource.stage2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.ErrorType;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.ResolveException;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.family.ResourceFamily;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.resolve.log.PackResolveLogger;
import com.yihu.ehr.resolve.model.stage1.FilePackage;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage1.details.CdaDocument;
import com.yihu.ehr.resolve.model.stage1.details.LinkFile;
import com.yihu.ehr.resolve.model.stage1.details.OriginFile;
import com.yihu.ehr.resolve.model.stage2.*;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 档案包压碎机，将档案数据包压碎成资源点。
 * @author Sand
 * @created 2016.05.16 13:51
 */
@Service
public class PackMillService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RedisService redisService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     *
     * @param originalPackage
     * @return
     * @throws Exception
     */
    public ResourceBucket grindingPackModel(OriginalPackage originalPackage) throws  Exception{
        ResourceBucket resourceBucket = initBasicData(originalPackage);
        //获取数据集的集合
        Map<String, PackageDataSet> packageDataSets = originalPackage.getDataSets();
        for (String dataSetCode : packageDataSets.keySet()) {
            //如果为原始数据集，则跳过
            if (DataSetUtil.isOriginDataSet(dataSetCode)){
                continue;
            }
            //初始化基本字段
            PackageDataSet srcDataSet = packageDataSets.get(dataSetCode);
            Boolean isMultiRecord = redisService.getDataSetMultiRecord(srcDataSet.getCdaVersion(), srcDataSet.getCode());
            if (null == isMultiRecord) {
                throw new ResolveException(srcDataSet.getCode() + " is_multi_record can not be null for std version " + srcDataSet.getCdaVersion());
            }
            Set<String> keys = srcDataSet.getRecordKeys();
            Set<String> existSet = new HashSet<>();
            if (!isMultiRecord){
                MasterRecord masterRecord = resourceBucket.getMasterRecord();
                for (String key : keys){
                    MetaDataRecord metaDataRecord = srcDataSet.getRecord(key);
                    for (String srcMetadataCode : metaDataRecord.getMetaDataCodes()){
                        //通过标准数据元编码(例如HDSA00_01_012)获取资源化数据元ID(EHR_XXXXXX)
                        String resMetadata = getResMetadata(
                                originalPackage.getCdaVersion(),
                                srcDataSet.getCode(),
                                srcMetadataCode,
                                resourceBucket,
                                metaDataRecord.getMetaData(srcMetadataCode),
                                originalPackage.getProfileType(),
                                existSet
                        );
                        if (StringUtils.isEmpty(resMetadata)){
                            continue;
                        }
                        //masterRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(
                                masterRecord,
                                originalPackage.getCdaVersion(),
                                resMetadata,
                                metaDataRecord.getMetaData(srcMetadataCode),
                                srcDataSet.getCode(),
                                srcMetadataCode
                        );
                    }
                    //仅一条记录
                    break;
                }
            } else {
                Integer index = 0;
                char cIndex = 'a';
                for (String key : keys){
                    SubRecord subRecord = new SubRecord();
                    if (originalPackage.getProfileType() == ProfileType.Simple){
                        subRecord.setRowkey(originalPackage.getId(), srcDataSet.getCode(), srcDataSet.getPk());
                    } else {
                        if (originalPackage.isReUploadFlg()) {
                            subRecord.setRowkey(originalPackage.getId(), srcDataSet.getCode(), ("") + (cIndex ++));
                        } else {
                            subRecord.setRowkey(originalPackage.getId(), srcDataSet.getCode(), index ++);
                        }
                    }
                    MetaDataRecord metaDataRecord = srcDataSet.getRecord(key);
                    for (String srcMetadataCode : metaDataRecord.getMetaDataCodes()){
                        String resMetadata = getResMetadata(
                                originalPackage.getCdaVersion(),
                                srcDataSet.getCode(),
                                srcMetadataCode,
                                resourceBucket,
                                metaDataRecord.getMetaData(srcMetadataCode),
                                originalPackage.getProfileType(),
                                existSet
                        );
                        if (StringUtils.isEmpty(resMetadata)) {
                            continue;
                        }
                        //subRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(
                                subRecord,
                                originalPackage.getCdaVersion(),
                                resMetadata,
                                metaDataRecord.getMetaData(srcMetadataCode),
                                srcDataSet.getCode(),
                                srcMetadataCode
                        );
                    }
                    if (subRecord.getDataGroup().size() > 0) {
                        resourceBucket.getSubRecords().add(subRecord);
                    }
                }
            }
        }
        if (originalPackage.getProfileType() == ProfileType.File || originalPackage.getProfileType() == ProfileType.Link) {
            resourceBucket.insertBasicRecord(ResourceCells.SUB_ROWKEYS, objectMapper.writeValueAsString(resourceBucket.getSubRowkeys()));
        }
        return resourceBucket;
    }

    /**
     * 生成基本资源包
     * @param originalPackage
     * @return
     */
    private ResourceBucket initBasicData (OriginalPackage originalPackage) throws Exception {
        ResourceBucket resourceBucket;
        if (originalPackage.getProfileType() == ProfileType.Standard) {
            resourceBucket = new ResourceBucket(
                    originalPackage.getId(),
                    originalPackage.getPackId(),
                    originalPackage.getReceiveDate(),
                    ResourceCore.MasterTable,
                    ResourceCore.SubTable,
                    ResourceFamily.Basic,
                    ResourceFamily.Data
            );
            //基本字段
            StandardPackage standardPackage = (StandardPackage) originalPackage;
            resourceBucket.insertBasicRecord(ResourceCells.PROFILE_TYPE, standardPackage.getProfileType().toString());
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_NO, standardPackage.getEventNo());
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_DATE, DateTimeUtil.utcDateTimeFormat(standardPackage.getEventTime()));
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_TYPE, standardPackage.getEventType() == null ? "" : Integer.toString(standardPackage.getEventType().ordinal()));
            resourceBucket.insertBasicRecord(ResourceCells.CARD_ID, standardPackage.getCardId());
            resourceBucket.insertBasicRecord(ResourceCells.CARD_TYPE, standardPackage.getCardType());
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_ID, standardPackage.getPatientId());
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_NAME, standardPackage.getPatientName());
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_AGE, standardPackage.getPatientAge());
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_SEX, standardPackage.getPatientSex());
            resourceBucket.insertBasicRecord(ResourceCells.DEMOGRAPHIC_ID, standardPackage.getDemographicId());
            resourceBucket.insertBasicRecord(ResourceCells.ORG_CODE, standardPackage.getOrgCode());
            String orgName = redisService.getOrgName(standardPackage.getOrgCode());
            if (StringUtils.isEmpty(orgName)) {
                throw new ResolveException("can not get org name for code " + standardPackage.getOrgCode());
            }
            resourceBucket.insertBasicRecord(ResourceCells.ORG_NAME, orgName);
            String orgArea = redisService.getOrgArea(standardPackage.getOrgCode());
            if (StringUtils.isEmpty(orgName)) {
                throw new ResolveException("can not get org area for code " + standardPackage.getOrgCode());
            }
            resourceBucket.insertBasicRecord(ResourceCells.ORG_AREA, orgArea);
            resourceBucket.insertBasicRecord(ResourceCells.CDA_VERSION, standardPackage.getCdaVersion());
            resourceBucket.insertBasicRecord(ResourceCells.CREATE_DATE, DateTimeUtil.utcDateTimeFormat(new Date()));
            resourceBucket.insertBasicRecord(ResourceCells.DEPT_CODE, standardPackage.getDeptCode());
            //门诊/住院健康问题
            if (!standardPackage.getDiagnosisCode().isEmpty()) {
                Set<String> healthProblem = new HashSet<>();
                Set<String> healthProblemName = new HashSet<>();
                standardPackage.getDiagnosisCode().forEach(item -> {
                    String _healthProblem = redisService.getHpCodeByIcd10(item);//通过ICD10获取健康问题
                    if (!StringUtils.isEmpty(_healthProblem)) {
                        String [] hpCodes = _healthProblem.split(";");
                        for (String hpCode : hpCodes) {
                            healthProblem.add(hpCode);
                            healthProblemName.add(redisService.getHealthProblem(hpCode));
                        }
                    }
                });
                resourceBucket.insertBasicRecord(ResourceCells.DIAGNOSIS, StringUtils.join(standardPackage.getDiagnosisCode(),";"));
                resourceBucket.insertBasicRecord(ResourceCells.DIAGNOSIS_NAME, StringUtils.join(standardPackage.getDiagnosisName(),";"));
                resourceBucket.insertBasicRecord(ResourceCells.HEALTH_PROBLEM, StringUtils.join(healthProblem,";"));//健康问题
                resourceBucket.insertBasicRecord(ResourceCells.HEALTH_PROBLEM_NAME, StringUtils.join(healthProblemName,";"));//健康问题名称
            }
            return resourceBucket;
        } else if (originalPackage.getProfileType() == ProfileType.File) {
            resourceBucket = new ResourceBucket(
                    originalPackage.getId(),
                    originalPackage.getPackId(),
                    originalPackage.getReceiveDate(),
                    ResourceCore.FileMasterTable,
                    ResourceCore.FileSubTable,
                    ResourceFamily.Basic,
                    ResourceFamily.Data
            );
            //基本字段
            FilePackage filePackage = (FilePackage) originalPackage;
            resourceBucket.insertBasicRecord(ResourceCells.PROFILE_TYPE, filePackage.getProfileType().toString());
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_NO, filePackage.getEventNo());
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_DATE, DateTimeUtil.utcDateTimeFormat(filePackage.getEventTime()));
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_TYPE, filePackage.getEventType() == null ? "" : Integer.toString(filePackage.getEventType().ordinal()));
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_ID, filePackage.getPatientId());
            resourceBucket.insertBasicRecord(ResourceCells.DEMOGRAPHIC_ID, filePackage.getDemographicId());
            resourceBucket.insertBasicRecord(ResourceCells.ORG_CODE, filePackage.getOrgCode());
            resourceBucket.insertBasicRecord(ResourceCells.ORG_NAME, redisService.getOrgName(filePackage.getOrgCode()));
            resourceBucket.insertBasicRecord(ResourceCells.ORG_AREA, redisService.getOrgArea(filePackage.getOrgCode()));
            resourceBucket.insertBasicRecord(ResourceCells.CDA_VERSION, filePackage.getCdaVersion());
            resourceBucket.insertBasicRecord(ResourceCells.CREATE_DATE, DateTimeUtil.utcDateTimeFormat(new Date()));
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_NAME, filePackage.getPatientName());
            resourceBucket.insertBasicRecord(ResourceCells.DEMOGRAPHIC_ID, filePackage.getDemographicId());
            ArrayNode root = objectMapper.createArrayNode();
            Map<String, CdaDocument> cdaDocuments = filePackage.getCdaDocuments();
            cdaDocuments.keySet().forEach(item -> {
                CdaDocument cdaDocument = cdaDocuments.get(item);
                for (OriginFile originFile : cdaDocument.getOriginFiles()) {
                    ObjectNode subNode = root.addObject();
                    subNode.put("mime", originFile.getMime());
                    subNode.put("url", originFile.getUrlsStr());
                    String name = originFile.getUrlScope()==null ? "":originFile.getUrlScope().name();
                    subNode.put("url_score", name);
                    subNode.put("emr_id", originFile.getEmrId());
                    subNode.put("emr_name", originFile.getEmrName());
                    subNode.put("expire_date", originFile.getExpireDate()== null ? "" : DateTimeUtil.utcDateTimeFormat(originFile.getExpireDate()));
                    subNode.put("note", originFile.getNote());
                    StringBuilder builder = new StringBuilder();
                    for (String fileName : originFile.getFileUrls().keySet()){
                        builder.append(fileName).append(":").append(originFile.getFileUrls().get(fileName)).append(";");
                    }
                    subNode.put("files", builder.toString());
                    subNode.put("cda_document_id", cdaDocument.getId());
                    subNode.put("cda_document_name", cdaDocument.getName());
                }
            });
            resourceBucket.insertBasicRecord(ResourceCells.FILE_LIST, root.toString());
            return resourceBucket;
        } else if (originalPackage.getProfileType() == ProfileType.Link) {
            resourceBucket = new ResourceBucket(
                    originalPackage.getId(),
                    originalPackage.getPackId(),
                    originalPackage.getReceiveDate(),
                    ResourceCore.FileMasterTable,
                    ResourceCore.FileSubTable,
                    ResourceFamily.Basic,
                    ResourceFamily.Data
            );
            //基本字段
            LinkPackage linkPackage = (LinkPackage) originalPackage;
            resourceBucket.insertBasicRecord(ResourceCells.PROFILE_TYPE, linkPackage.getProfileType().toString());
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_NO, linkPackage.getEventNo());
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_DATE, DateTimeUtil.utcDateTimeFormat(linkPackage.getEventTime()));
            resourceBucket.insertBasicRecord(ResourceCells.EVENT_TYPE, linkPackage.getEventType() == null ? "" : Integer.toString(linkPackage.getEventType().ordinal()));
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_ID, linkPackage.getPatientId());
            resourceBucket.insertBasicRecord(ResourceCells.ORG_CODE, linkPackage.getOrgCode());
            resourceBucket.insertBasicRecord(ResourceCells.ORG_NAME, redisService.getOrgName(linkPackage.getOrgCode()));
            resourceBucket.insertBasicRecord(ResourceCells.ORG_AREA, redisService.getOrgArea(linkPackage.getOrgCode()));
            resourceBucket.insertBasicRecord(ResourceCells.CDA_VERSION, linkPackage.getCdaVersion());
            resourceBucket.insertBasicRecord(ResourceCells.CREATE_DATE, DateTimeUtil.utcDateTimeFormat(new Date()));
            resourceBucket.insertBasicRecord(ResourceCells.PATIENT_NAME, linkPackage.getPatientName());
            resourceBucket.insertBasicRecord(ResourceCells.DEMOGRAPHIC_ID, linkPackage.getDemographicId());
            ArrayNode root = objectMapper.createArrayNode();
            linkPackage.getLinkFiles().forEach(item -> {
                ObjectNode subNode = root.addObject();
                subNode.put("file_extension", item.getFileExtension());
                subNode.put("origin_name", item.getOriginName());
                subNode.put("report_form_no", item.getReportFormNo());
                subNode.put("serial_no", item.getSerialNo());
                subNode.put("file_size", item.getFileSize());
                subNode.put("url", item.getUrl());
            });
            resourceBucket.insertBasicRecord(ResourceCells.FILE_LIST, root.toString());
            return resourceBucket;
        } else if (originalPackage.getProfileType() == ProfileType.Simple) {
            resourceBucket = new ResourceBucket(
                    originalPackage.getId(),
                    originalPackage.getPackId(),
                    originalPackage.getReceiveDate(),
                    ResourceCore.FileMasterTable,
                    ResourceCore.FileSubTable,
                    ResourceFamily.Basic,
                    ResourceFamily.Data
            );
            return resourceBucket;
        }
        throw new ZipException("Invalid zip file structure");
    }

    /**
     * 对数据元资源化处理。其原理是根据映射关系，将数据元映射到资源中。
     * @param cdaVersion 版本号
     * @param srcDataSetCode 标准数据集编码
     * @param srcMetadataCode 标准数据元编码
     * @param resourceBucket 数据包
     * @param value 值
     * @param profileType 档案类型
     * @param existSet 已质控数据记录
     * @return
     */
     protected String getResMetadata(String cdaVersion,
                                     String srcDataSetCode,
                                     String srcMetadataCode,
                                     ResourceBucket resourceBucket,
                                     String value,
                                     ProfileType profileType,
                                     Set<String> existSet){
         // TODO: 翻译时需要的内容：对CODE与VALUE处理后再翻译
         if ("rBUSINESS_DATE".equals(srcMetadataCode)) {
             return null;
         }
         String resMetadata = redisService.getRsAdapterMetaData(cdaVersion, srcDataSetCode, srcMetadataCode);
         if (!StringUtils.isEmpty(resMetadata)) {
             return resMetadata;
         }
         //日志
         PackResolveLogger.warn(String.format("Unable to get resource meta data code for ehr meta data %s of %s in %s", srcMetadataCode, srcDataSetCode, cdaVersion));
         if (profileType == ProfileType.Standard && !existSet.contains(srcDataSetCode + "$" + srcMetadataCode)) {
             //质控数据
             Map<String, Object> qcMetadataRecord = new HashMap<>();
             StringBuilder _id = new StringBuilder();
             _id.append(resourceBucket.getPackId())
                     .append("$")
                     .append(srcDataSetCode)
                     .append("$")
                     .append(srcMetadataCode);
             qcMetadataRecord.put("_id", _id.toString());
             qcMetadataRecord.put("pack_id", resourceBucket.getPackId());
             qcMetadataRecord.put("patient_id", resourceBucket.getBasicRecord(ResourceCells.PATIENT_ID));
             qcMetadataRecord.put("org_code", resourceBucket.getBasicRecord(ResourceCells.ORG_CODE));
             qcMetadataRecord.put("org_name", resourceBucket.getBasicRecord(ResourceCells.ORG_NAME));
             qcMetadataRecord.put("org_area", resourceBucket.getBasicRecord(ResourceCells.ORG_AREA));
             qcMetadataRecord.put("dept", resourceBucket.getBasicRecord(ResourceCells.DEPT_CODE));
             qcMetadataRecord.put("diagnosis_name", resourceBucket.getBasicRecord(ResourceCells.DIAGNOSIS_NAME));
             qcMetadataRecord.put("event_date", DateUtil.toStringLong(DateUtil.strToDate(resourceBucket.getBasicRecord(ResourceCells.EVENT_DATE))));
             qcMetadataRecord.put("event_type", resourceBucket.getBasicRecord(ResourceCells.EVENT_TYPE) == "" ? -1 : new Integer(resourceBucket.getBasicRecord(ResourceCells.EVENT_TYPE)));
             qcMetadataRecord.put("event_no", resourceBucket.getBasicRecord(ResourceCells.EVENT_NO));
             qcMetadataRecord.put("receive_date", DATE_FORMAT.format(resourceBucket.getReceiveDate()));
             qcMetadataRecord.put("version", cdaVersion);
             qcMetadataRecord.put("dataset", srcDataSetCode);
             qcMetadataRecord.put("metadata", srcMetadataCode);
             qcMetadataRecord.put("value", value);
             qcMetadataRecord.put("qc_step", 2); //资源化质控环节
             qcMetadataRecord.put("qc_error_type", ErrorType.FieldAdaptationError.getType()); //资源适配错误
             qcMetadataRecord.put("qc_error_name", ErrorType.FieldAdaptationError.getName()); //资源适配错误
             qcMetadataRecord.put("qc_error_message", String.format("Unable to get resource meta data code for ehr meta data %s of %s in %s", srcMetadataCode, srcDataSetCode, cdaVersion));
             qcMetadataRecord.put("create_date", DATE_FORMAT.format(new Date()));
             resourceBucket.getQcMetadataRecords().addRecord(qcMetadataRecord);
             existSet.add(srcDataSetCode + "$" + srcMetadataCode);
         }
         return null;
     }

    /**
     *
     * @param dataRecord 数据
     * @param cdaVersion 版本号
     * @param metadataId 资源化编码
     * @param value 值
     * @param srcDataSetCode 标准数据集编码
     * @param srcMetadataCode 标准数据元编码
     * @throws Exception
     */
    protected void dictTransform(ResourceRecord dataRecord,
                                 String cdaVersion,
                                 String metadataId,
                                 String value,
                                 String srcDataSetCode,
                                 String srcMetadataCode) throws Exception {
        //查询是否有对应字典ID
        String dictId = redisService.getMetaDataDict(cdaVersion, srcDataSetCode, srcMetadataCode);
        //字典ID不为空且原始值不为空的情况下
        if (StringUtils.isNotBlank(dictId) && StringUtils.isNotBlank(value)) {
            //查找对应的字典值
            String _value = redisService.getDictEntryValue(cdaVersion, dictId, value);
            //对应字典值不为空的情况下，保存原始值和字典值
            if (StringUtils.isNotBlank(_value)) {
                //保存标准字典值编码(code)
                dataRecord.addResource(metadataId, value);
                //保存标准字典值名称(value)
                dataRecord.addResource(metadataId + "_VALUE", _value);
            } else {
                dataRecord.addResource(metadataId, value);
            }
        } else {
            dataRecord.addResource(metadataId, value);
        }
    }
}
