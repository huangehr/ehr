package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.resolve.feign.RedisServiceClient;
import com.yihu.ehr.resolve.model.stage1.FilePackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.*;
import com.yihu.ehr.resolve.util.PackResolveLogger;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 档案包压碎机，将档案数据包压碎成资源点。
 * @author Sand
 * @created 2016.05.16 13:51
 */
@Service
public class PackMillService {

    @Autowired
    private RedisServiceClient redisServiceClient;

    /**
     * 将解析好的档案包拆解成资源。
     * @param stdPack
     * @return
     */
    public ResourceBucket grindingPackModel(StandardPackage stdPack) throws  Exception{
        ResourceBucket resourceBucket = new ResourceBucket();
        BeanUtils.copyProperties(stdPack, resourceBucket);
        //机构名称、机构区域
        if(!StringUtils.isBlank(resourceBucket.getOrgCode())) {
            //获取机构名称
            String orgName = redisServiceClient.getOrgRedis(resourceBucket.getOrgCode());
            if(!StringUtils.isBlank(orgName)) {
                resourceBucket.setOrgName(orgName);
            }
            //获取机构区域
            String orgArea = redisServiceClient.getOrgAreaRedis(resourceBucket.getOrgCode());
            if(!StringUtils.isBlank(orgArea)) {
                resourceBucket.setOrgArea(orgArea);
            }
        }

        //门诊/住院诊断、健康问题
        if(stdPack.getDiagnosisList()!=null && stdPack.getDiagnosisList().size()>0) {
            List<String> healthProblemList = new ArrayList<>();
            for(String diagnosis : stdPack.getDiagnosisList()) {
                String healthProblem = redisServiceClient.getIcd10HpCodeRedis(diagnosis);//通过ICD10获取健康问题
                if(!StringUtils.isEmpty(healthProblem)) {
                    String[] hpCodeList = healthProblem.split(";");
                    for(String hpCode:hpCodeList) {
                        if(!healthProblemList.contains(hpCode)) {
                            healthProblemList.add(hpCode);
                        }
                    }
                }
            }
            resourceBucket.setDiagnosis(StringUtils.join(stdPack.getDiagnosisList().toArray(),";"));          //ICD10
            resourceBucket.setHealthProblem(StringUtils.join(healthProblemList.toArray(),";"));    //健康问题
        }

        //获取数据集的集合
        Collection<PackageDataSet> packageDataSets = stdPack.getDataSets();
        //遍历数据集
        for(PackageDataSet srcDataSet : packageDataSets){
            //如果为原始数据集，则跳过
            if(DataSetUtil.isOriginDataSet(srcDataSet.getCode())){
                continue;
            }
            Boolean isMultiRecord = Boolean.valueOf(redisServiceClient.getDataSetMultiRecord(srcDataSet.getCdaVersion(), srcDataSet.getCode()));
            if(null == isMultiRecord) {
                throw new RuntimeException("IsMultiRecord Can not be null");
            }
            Set<String> keys = srcDataSet.getRecordKeys();
            if (!isMultiRecord){
                MasterRecord masterRecord = resourceBucket.getMasterRecord();
                for (String key : keys){
                    MetaDataRecord metaDataRecord = srcDataSet.getRecord(key);
                    for (String srcMetadataCode : metaDataRecord.getMetaDataCodes()){
                        //通过标准数据元编码(例如HDSA00_01_012)获取资源化数据元ID(EHR_XXXXXX)
                        String resMetadata = getResMetadata(stdPack.getCdaVersion(), srcDataSet.getCode(), srcMetadataCode);
                        if (StringUtils.isEmpty(resMetadata)){
                            continue;
                        }
                        //masterRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(masterRecord, stdPack.getCdaVersion(), resMetadata, metaDataRecord.getMetaData(srcMetadataCode));
                    }
                    //仅一条记录
                    break;
                }
            } else {
                SubRecords subRecords = resourceBucket.getSubRecords();
                int index = 0;
                for (String key : keys){
                    SubRecord subRecord = new SubRecord();
                    if (stdPack.getProfileType() == ProfileType.DataSet){
                        subRecord.setRowkey(stdPack.getId(), srcDataSet.getCode(), srcDataSet.getPk());
                    }else {
                        subRecord.setRowkey(stdPack.getId(), srcDataSet.getCode(), index++ );
                    }
                    MetaDataRecord metaDataRecord = srcDataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        String resourceMetaData = getResMetadata(stdPack.getCdaVersion(), srcDataSet.getCode(), metaDataCode);
                        if (StringUtils.isEmpty(resourceMetaData)) {
                            continue;
                        }
                        //subRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(subRecord, stdPack.getCdaVersion(), resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                    }
                    if(subRecord.getDataGroup().size() > 0) {
                        subRecords.addRecord(subRecord);
                    }
                }
            }
        }
        // files that unable to get structured data, store as they are
        if (stdPack.getProfileType() == ProfileType.File){
            resourceBucket.setCdaDocuments(((FilePackage) stdPack).getCdaDocuments());
        }
        return resourceBucket;
    }

    /**
     * 对数据元资源化处理。其原理是根据映射关系，将数据元映射到资源中。
     *
     * @param cdaVersion
     * @param srcDataSetCode
     * @param srcMetadataCode
     * @return
     */
     protected String getResMetadata(String cdaVersion, String srcDataSetCode, String srcMetadataCode){
         // TODO: 翻译时需要的内容：对CODE与VALUE处理后再翻译
        String resMetadata = redisServiceClient.getRsAdaptionMetaData(cdaVersion, srcDataSetCode, srcMetadataCode);
        if (StringUtils.isEmpty(resMetadata)){
            //调试的时候可将其改为LoggerService的日志输出
            PackResolveLogger.warn(
                     String.format("Unable to get resource meta data code for ehr meta data %s of %s in %s, forget to cache them?",
                             srcMetadataCode, srcDataSetCode, cdaVersion));
             return null;
        }
        return resMetadata;
     }

    /**
     * STD字典转内部EHR字典
     * @param dataRecord 数据
     * @param cdaVersion 版本号
     * @param metadataId EHR数据源ID
     * @param value 原值
     * @throws Exception
     */
    protected  void dictTransform(ResourceRecord dataRecord, String cdaVersion, String metadataId, String value) throws Exception {
        //查询对应内部EHR字段是否有对应字典
        String dictCode = getMetadataDict(metadataId);
        //内部EHR数据元字典不为空情况
        if(StringUtils.isNotBlank(dictCode) && StringUtils.isNotBlank(value)) {
            //查找对应的字典数据
            String[] dict = getDict(cdaVersion, dictCode, value);
            //对应字典不为空情况下，转换EHR内部字典，并保存字典对应值，为空则不处理
            if(dict != null && dict.length > 1) {
                //保存标准字典值编码(code)
                dataRecord.addResource(metadataId, dict[0]);
                //保存标准字典值名称(value)
                dataRecord.addResource(metadataId + "_VALUE", dict[1]);
            } else {
                dataRecord.addResource(metadataId, value);
            }
        } else {   //内部EHR数据元为空不处理
            dataRecord.addResource(metadataId, value);
        }
    }

    /**
     * 获取数据元对应字典缓存
     * @param metadataId 数据元ID
     * @return
     */
    public String getMetadataDict(String metadataId) {
        String dictCode = redisServiceClient.getRsMetaData(metadataId);
        return dictCode;
    }

    /**
     * 获取对应字典缓存信息
     * @param version cda版本
     * @param dictCode EHR内部字典代码
     * @param srcDictEntryCode STD字典项代码
     * @return
     */
    public String[] getDict(String version, String dictCode, String srcDictEntryCode) {
        String dict = redisServiceClient.getRsAdaptionDict(version, dictCode, srcDictEntryCode);
        if(dict != null) {
            return dict.split("&");
        }
        else {
            return null;
        }
    }
}
