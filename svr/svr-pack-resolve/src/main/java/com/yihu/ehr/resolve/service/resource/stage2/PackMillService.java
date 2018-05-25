package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.ResolveException;
import com.yihu.ehr.resolve.model.stage1.FilePackage;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.*;
import com.yihu.ehr.resolve.log.PackResolveLogger;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

    /**
     * 将解析好的档案包拆解成资源。
     * @param stdPack
     * @return
     */
    public ResourceBucket grindingPackModel(StandardPackage stdPack, EsSimplePackage esSimplePackage) throws  Exception{
        ResourceBucket resourceBucket = new ResourceBucket();
        BeanUtils.copyProperties(stdPack, resourceBucket);
        //获取机构名称
        String orgName = redisService.getOrgName(resourceBucket.getOrgCode());
        if (!StringUtils.isBlank(orgName)) {
            resourceBucket.setOrgName(orgName);
        }
        //获取机构区域
        String orgArea = redisService.getOrgArea(resourceBucket.getOrgCode());
        if (!StringUtils.isBlank(orgArea)) {
            resourceBucket.setOrgArea(orgArea);
        }
        //门诊/住院诊断、健康问题
        if (stdPack.getDiagnosisList() != null && stdPack.getDiagnosisList().size() > 0) {
            Set<String> healthProblemList = new HashSet<>();
            Set<String> healthProblemNameList = new HashSet<>();
            for (String diagnosis : stdPack.getDiagnosisList()) {
                String healthProblem = redisService.getHpCodeByIcd10(diagnosis);//通过ICD10获取健康问题
                if (!StringUtils.isEmpty(healthProblem)) {
                    String[] hpCodeArr = healthProblem.split(";");
                    for (String hpCode : hpCodeArr) {
                        String hpName = redisService.getHealthProblem(hpCode);
                        healthProblemList.add(hpCode);
                        healthProblemNameList.add(hpName);
                    }
                }
            }
            resourceBucket.setDiagnosis(StringUtils.join(stdPack.getDiagnosisList().toArray(),";"));//ICD10
            resourceBucket.setDiagnosisName(StringUtils.join(stdPack.getDiagnosisNameList().toArray(),";"));//ICD10名称
            resourceBucket.setHealthProblem(StringUtils.join(healthProblemList.toArray(),";"));//健康问题
            resourceBucket.setHealthProblemName(StringUtils.join(healthProblemNameList.toArray(),";"));//健康问题名称
        }

        //获取数据集的集合
        Collection<PackageDataSet> packageDataSets = stdPack.getDataSets();
        //遍历数据集
        for (PackageDataSet srcDataSet : packageDataSets){
            //如果为原始数据集，则跳过
            if (DataSetUtil.isOriginDataSet(srcDataSet.getCode())){
                continue;
            }
            Boolean isMultiRecord = redisService.getDataSetMultiRecord(srcDataSet.getCdaVersion(), srcDataSet.getCode());
            if (null == isMultiRecord) {
                throw new ResolveException(srcDataSet.getCode() + " is_multi_record can not be null for std version " + srcDataSet.getCdaVersion());
            }
            Set<String> keys = srcDataSet.getRecordKeys();
            if (!isMultiRecord){
                MasterRecord masterRecord = resourceBucket.getMasterRecord();
                for (String key : keys){
                    MetaDataRecord metaDataRecord = srcDataSet.getRecord(key);
                    for (String srcMetadataCode : metaDataRecord.getMetaDataCodes()){
                        //通过标准数据元编码(例如HDSA00_01_012)获取资源化数据元ID(EHR_XXXXXX)
                        String resMetadata = getResMetadata(stdPack.getCdaVersion(), srcDataSet.getCode(), srcMetadataCode, resourceBucket, metaDataRecord.getMetaData(srcMetadataCode), esSimplePackage);
                        if (StringUtils.isEmpty(resMetadata)){
                            continue;
                        }
                        //masterRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(masterRecord, stdPack.getCdaVersion(), resMetadata, metaDataRecord.getMetaData(srcMetadataCode), srcDataSet.getCode(), srcMetadataCode, resourceBucket, esSimplePackage);
                    }
                    //仅一条记录
                    break;
                }
            } else {
                SubRecords subRecords = resourceBucket.getSubRecords();
                Integer index = 0;
                char cIndex = 'a';
                for (String key : keys){
                    SubRecord subRecord = new SubRecord();
                    if (stdPack.getProfileType() == ProfileType.DataSet){
                        subRecord.setRowkey(stdPack.getId(), srcDataSet.getCode(), srcDataSet.getPk());
                    } else {
                        if (resourceBucket.isReUploadFlg()) {
                            subRecord.setRowkey(stdPack.getId(), srcDataSet.getCode(), ("") + (cIndex ++));
                        } else {
                            subRecord.setRowkey(stdPack.getId(), srcDataSet.getCode(), index ++);
                        }
                    }
                    MetaDataRecord metaDataRecord = srcDataSet.getRecord(key);
                    for (String srcMetadataCode : metaDataRecord.getMetaDataCodes()){
                        String resMetadata = getResMetadata(stdPack.getCdaVersion(), srcDataSet.getCode(), srcMetadataCode, resourceBucket, metaDataRecord.getMetaData(srcMetadataCode), esSimplePackage);
                        if (StringUtils.isEmpty(resMetadata)) {
                            continue;
                        }
                        //subRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(subRecord, stdPack.getCdaVersion(), resMetadata, metaDataRecord.getMetaData(srcMetadataCode), srcDataSet.getCode(), srcMetadataCode, resourceBucket, esSimplePackage);
                    }
                    if (subRecord.getDataGroup().size() > 0) {
                        subRecords.addRecord(subRecord);
                    }
                }
            }
        }
        // files that unable to get structured data, store as they are
        if (stdPack.getProfileType() == ProfileType.File){
            resourceBucket.setCdaDocuments(((FilePackage) stdPack).getCdaDocuments());
        } else if (stdPack.getProfileType() == ProfileType.Link){
            LinkPackage linkPackage = (LinkPackage)stdPack;
            resourceBucket.setLinkFiles(linkPackage.getLinkFiles());
        }
        return resourceBucket;
    }

    /**
     * 对数据元资源化处理。其原理是根据映射关系，将数据元映射到资源中。
     * @param cdaVersion 版本号
     * @param srcDataSetCode 标准数据集编码
     * @param srcMetadataCode 标准数据元编码
     * @param resourceBucket 数据包
     * @param value 值
     * @return
     */
     protected String getResMetadata(String cdaVersion, String srcDataSetCode, String srcMetadataCode, ResourceBucket resourceBucket, String value, EsSimplePackage esSimplePackage){
         // TODO: 翻译时需要的内容：对CODE与VALUE处理后再翻译
         if ("rBUSINESS_DATE".equals(srcMetadataCode)) {
             return null;
         }
         String resMetadata = redisService.getRsAdapterMetaData(cdaVersion, srcDataSetCode, srcMetadataCode);
         if (StringUtils.isEmpty(resMetadata)){
             //质控数据
             Map<String, Object> qcRecord = new HashMap<>();
             qcRecord.put("pack_id", esSimplePackage.get_id());
             qcRecord.put("org_code", resourceBucket.getOrgCode());
             qcRecord.put("org_name", resourceBucket.getOrgName());
             qcRecord.put("org_area", resourceBucket.getOrgArea());
             qcRecord.put("dept", resourceBucket.getDeptCode());
             qcRecord.put("diagnosis_name", resourceBucket.getDiagnosisName());
             qcRecord.put("receive_date", DATE_FORMAT.format(esSimplePackage.getReceive_date()));
             qcRecord.put("event_date", DateUtil.toStringLong(resourceBucket.getEventDate()));
             qcRecord.put("event_type", resourceBucket.getEventType() == null ? null : resourceBucket.getEventType().getType());
             qcRecord.put("event_no", resourceBucket.getEventNo());
             qcRecord.put("version", cdaVersion);
             qcRecord.put("dataset", srcDataSetCode);
             qcRecord.put("metadata", srcMetadataCode);
             qcRecord.put("value", value);
             qcRecord.put("qc_step", 2); //资源化质控环节
             qcRecord.put("qc_error_type", 1); //资源适配错误
             qcRecord.put("qc_error_message", String.format("Unable to get resource meta data code for ehr meta data %s of %s in %s", srcMetadataCode, srcDataSetCode, cdaVersion));
             resourceBucket.getQcRecords().addRecord(qcRecord);
             //日志
             PackResolveLogger.warn(String.format("Unable to get resource meta data code for ehr meta data %s of %s in %s", srcMetadataCode, srcDataSetCode, cdaVersion));
             return null;
        }
        return resMetadata;
     }

    /**
     * STD字典转内部EHR字典
     * @param dataRecord 数据
     * @param cdaVersion 版本号
     * @param metadataId 资源化编码
     * @param value 值
     * @param srcDataSetCode 标准数据集编码
     * @param srcMetadataCode 标准数据元编码
     * @param resourceBucket 数据包
     * @throws Exception
     */
    protected void dictTransform(ResourceRecord dataRecord, String cdaVersion, String metadataId, String value, String srcDataSetCode, String srcMetadataCode, ResourceBucket resourceBucket, EsSimplePackage esSimplePackage) throws Exception {
        //查询对应内部EHR字段是否有对应字典
        String dictCode = getMetadataDict(metadataId);
        //内部EHR数据元字典不为空情况
        if (StringUtils.isNotBlank(dictCode) && StringUtils.isNotBlank(value)) {
            //判断是否为时间格式
            if (dictCode.equals("DATECONDITION")) {
                if (!value.contains("T") && !value.contains("Z")) {
                    StringBuilder error = new StringBuilder();
                    error.append("Invalid date time format ")
                            .append(srcDataSetCode)
                            .append(" ")
                            .append(srcMetadataCode)
                            .append(" ")
                            .append(value)
                            .append(" for std version ")
                            .append(cdaVersion)
                            .append(".");
                    throw new IllegalJsonDataException(error.toString());
                }
                dataRecord.addResource(metadataId, value);
            } else {
                //查找对应的字典数据
                String[] dict = getDict(cdaVersion, dictCode, value, resourceBucket, srcDataSetCode, srcMetadataCode, esSimplePackage);
                //对应字典不为空情况下，转换EHR内部字典，并保存字典对应值，为空则不处理
                if (dict.length > 1) {
                    //保存标准字典值编码(code)
                    dataRecord.addResource(metadataId, dict[0]);
                    //保存标准字典值名称(value)
                    dataRecord.addResource(metadataId + "_VALUE", dict[1]);
                } else {
                    dataRecord.addResource(metadataId, value);
                }
            }
        } else {   //内部EHR数据元字典为空不处理
            dataRecord.addResource(metadataId, value);
        }
    }

    /**
     * 获取数据元对应字典缓存
     * @param metadataId 数据元ID
     * @return
     */
    public String getMetadataDict(String metadataId) {
        return redisService.getRsMetaData(metadataId);
    }

    /**
     * 获取对应字典缓存信息
     * @param version cda版本
     * @param dictCode EHR内部字典代码
     * @param srcDictEntryCode STD字典项代码
     * @return
     */
    public String[] getDict(String version, String dictCode, String srcDictEntryCode, ResourceBucket resourceBucket, String srcDataSetCode, String srcMetadataCode, EsSimplePackage esSimplePackage) {
        String dict = redisService.getRsAdapterDict(version, dictCode, srcDictEntryCode);
        if (dict != null) {
            return dict.split("&");
        }
        //质控数据
        Map<String, Object> qcRecord = new HashMap<>();
        qcRecord.put("pack_id", esSimplePackage.get_id());
        qcRecord.put("org_code", resourceBucket.getOrgCode());
        qcRecord.put("org_name", resourceBucket.getOrgName());
        qcRecord.put("org_area", resourceBucket.getOrgArea());
        qcRecord.put("dept", resourceBucket.getDeptCode());
        qcRecord.put("diagnosis_name", resourceBucket.getDiagnosisName());
        qcRecord.put("receive_date", DATE_FORMAT.format(esSimplePackage.getReceive_date()));
        qcRecord.put("event_date", DateUtil.toStringLong(resourceBucket.getEventDate()));
        qcRecord.put("event_type", resourceBucket.getEventType() == null ? null : resourceBucket.getEventType().getType());
        qcRecord.put("event_no", resourceBucket.getEventNo());
        qcRecord.put("version", version);
        qcRecord.put("dataset", srcDataSetCode);
        qcRecord.put("metadata", srcMetadataCode);
        qcRecord.put("value", srcDictEntryCode);
        qcRecord.put("qc_step", 2); //资源化质控环节
        qcRecord.put("qc_error_type", 2); //字典转换错误
        qcRecord.put("qc_error_message", String.format("Unable to get dict value for meta data %s of %s in %s", srcMetadataCode, srcDataSetCode, version));
        resourceBucket.getQcRecords().addRecord(qcRecord);
        //日志
        PackResolveLogger.warn(String.format("Unable to get dict value for meta data %s of %s in %s", srcMetadataCode, srcDataSetCode, version));
        return "".split("&");
    }
}
