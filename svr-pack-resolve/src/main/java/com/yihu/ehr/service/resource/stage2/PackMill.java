package com.yihu.ehr.service.resource.stage2;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.*;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.service.resource.stage1.FilePackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * 档案包压碎机，将档案数据压碎成资源点。
 *
 * @author Sand
 * @created 2016.05.16 13:51
 */
@Service
public class PackMill {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ResourceMetadataSchema metadataSchema;

    @Autowired
    private ResourceAdaptionDictSchema dictSchema;

    @Autowired
    private ResourceAdaptionKeySchema resAdaptionKeySchema;

    @Autowired
    StdDataSetKeySchema dataSetKeySchema;

    @Autowired
    OrgKeySchema orgKeySchema;

    /**
     * 将解析好的档案拆解成资源。
     *
     * @param stdPack
     * @return
     */
    public ResourceBucket grindingPackModel(StandardPackage stdPack) throws  Exception{
        ResourceBucket resourceBucket = new ResourceBucket();
        BeanUtils.copyProperties(stdPack, resourceBucket);

        if(!StringUtils.isBlank(resourceBucket.getOrgCode()))
        {
            String orgName = redisClient.get(orgKeySchema.name(resourceBucket.getOrgCode()));

            if(!StringUtils.isBlank(orgName))
            {
                resourceBucket.setOrgName(orgName);
            }
            else
            {
                resourceBucket.setOrgName("");
            }
        }

        Collection<PackageDataSet> packageDataSets = stdPack.getDataSets();
        for (PackageDataSet dataSet : packageDataSets){
            if(DataSetUtil.isOriginDataSet(dataSet.getCode())) continue;

            Set<String> keys = dataSet.getRecordKeys();
            Boolean isMultiRecord = redisClient.get(dataSetKeySchema.dataSetMultiRecord(dataSet.getCdaVersion(), dataSet.getCode()));
            if (isMultiRecord == null || !isMultiRecord){
                MasterRecord masterRecord = resourceBucket.getMasterRecord();

                for (String key : keys){
                    MetaDataRecord metaDataRecord = dataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        String resourceMetaData = resourceMetaData(stdPack.getCdaVersion(), dataSet.getCode(), metaDataCode);
                        if (StringUtils.isEmpty(resourceMetaData)) continue;

                        //masterRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(masterRecord,stdPack.getCdaVersion(),resourceMetaData,metaDataRecord.getMetaData(metaDataCode));
                    }

                    // 仅一条记录
                    break;
                }
            } else {
                SubRecords subRecords = resourceBucket.getSubRecords();

                int index = 0;
                for (String key : keys){
                    SubRecord subRecord = new SubRecord();
                    subRecord.setRowkey(stdPack.getId(), dataSet.getCode(), index++);

                    MetaDataRecord metaDataRecord = dataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        if(metaDataCode.equals("PATIENT_ID")) continue;;

                        String resourceMetaData = resourceMetaData(stdPack.getCdaVersion(), dataSet.getCode(), metaDataCode);
                        if (StringUtils.isEmpty(resourceMetaData) ) continue;

                        //subRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                        dictTransform(subRecord,stdPack.getCdaVersion(),resourceMetaData,metaDataRecord.getMetaData(metaDataCode));
                    }

                    if(subRecord.getDataGroup().size() > 0) subRecords.addRecord(subRecord);
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
     * @param dataSetCode
     * @param metaDataCode
     * @return
     */
     protected String resourceMetaData(String cdaVersion, String dataSetCode, String metaDataCode){
         // TODO: 翻译时需要的内容：对CODE与VALUE处理后再翻译
        if (metaDataCode.contains("_CODE")){
            metaDataCode = metaDataCode.substring(0, metaDataCode.indexOf("_CODE"));
        } else if (metaDataCode.contains("_VALUE")){
            metaDataCode = metaDataCode.substring(0, metaDataCode.indexOf("_VALUE"));
        }

         String resMetaDataKey = resAdaptionKeySchema.metaData(cdaVersion, dataSetCode, metaDataCode);
         String resMetaData = redisClient.get(resMetaDataKey);
         if (StringUtils.isEmpty(resMetaData)){
             LogService.getLogger().error(
                     String.format("Unable to get resource meta data code for ehr meta data %s of %s in %s, forget to cache them?",
                             metaDataCode, dataSetCode, cdaVersion));

             return null;
         }

         return resMetaData;
     }

    /**
     * STD字典转内部EHR字典
     * @param dataRecord 数据
     * @param cdaVersion 版本号
     * @param metadataId EHR数据源ID
     * @param value 原值
     * @throws Exception
     */
    protected  void dictTransform(ResourceRecord dataRecord,String cdaVersion,String metadataId,String value) throws Exception
    {
        //查询对应内部EHR字段是否有对应字典
        String dictCode = getMetadataDict(metadataId);

        //内部EHR数据元字典不为空情况
        if(!org.apache.commons.lang.StringUtils.isBlank(dictCode) && !org.apache.commons.lang.StringUtils.isBlank(value))
        {
            //查找对应的字典数据
            String[] dict = getDict(cdaVersion,dictCode,value);

            //对应字典不为空情况下，转换EHR内部字典，并保存字典对应值，为空则不处理
            if(dict != null && dict.length > 1)
            {
                //STD字典代码转换为内部EHR字典代码保存
                dataRecord.addResource(metadataId, dict[0]);
                //添加内部EHR字典代码对应中文作为单独字段保存
                dataRecord.addResource(metadataId + "_VALUE", dict[1]);
            }
            else
            {
                dataRecord.addResource(metadataId, value);
            }
        }
        else
        {   //内部EHR数据元为空不处理
            dataRecord.addResource(metadataId, value);
        }
    }

    /**
     * 获取数据元对应字典缓存
     * @param metadataId 数据元ID
     * @return
     */
    public String getMetadataDict(String metadataId)
    {
        String key = metadataSchema.metaData(metadataId);
        String dictCode = redisClient.get(key);

        return dictCode;
    }

    /**
     * 获取对应字典缓存信息
     * @param version cda版本
     * @param dictCode EHR内部字典代码
     * @param srcDictEntryCode STD字典项代码
     * @return
     */
    public String[] getDict(String version,String dictCode,String srcDictEntryCode)
    {
        String key = dictSchema.metaData(version,dictCode,srcDictEntryCode);
        String dict = redisClient.get(key);

        if(dict != null)
        {
            return dict.split("&");
        }
        else
        {
            return null;
        }
    }
}
