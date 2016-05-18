package com.yihu.ehr.service.resource.stage2;

import com.yihu.ehr.profile.core.commons.ProfileType;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.ResourceAdaptionKeySchema;
import com.yihu.ehr.service.resource.StdDataSet;
import com.yihu.ehr.service.resource.stage1.FilePackModel;
import com.yihu.ehr.service.resource.stage1.StdPackModel;
import com.yihu.ehr.service.resource.stage1.MetaDataRecord;
import com.yihu.ehr.service.util.DataSetUtil;
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
    private ResourceAdaptionKeySchema resAdaptionKeySchema;

    /**
     * 将解析好的档案拆解成资源。
     *
     * @param packModel
     * @return
     */
    public ResourceBucket grindingPackModel(StdPackModel packModel){
        ResourceBucket resourceBucket = new ResourceBucket();
        BeanUtils.copyProperties(packModel, resourceBucket);

        Collection<StdDataSet> stdDataSets = packModel.getDataSets();
        for (StdDataSet dataSet : stdDataSets){
            if(DataSetUtil.isOriginDataSet(dataSet.getCode())) continue;

            Set<String> keys = dataSet.getRecordKeys();
            if (dataSet.isMainRecord()){
                MasterRecord masterRecord = resourceBucket.getMasterRecord();

                for (String key : keys){
                    MetaDataRecord metaDataRecord = dataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        String resourceMetaData = ResourceMetaData(packModel.getCdaVersion(), dataSet.getCode(), metaDataCode);
                        if (StringUtils.isEmpty(resourceMetaData)) continue;

                        masterRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                    }

                    // 仅一条记录
                    break;
                }
            } else {
                SubRecords subRecords = resourceBucket.getSubRecords();

                int index = 0;
                for (String key : keys){
                    SubRecord subRecord = new SubRecord();
                    subRecord.setRowkey(packModel.getId(), dataSet.getCode(), index++);

                    subRecords.addRecord(subRecord);

                    MetaDataRecord metaDataRecord = dataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        String resourceMetaData = ResourceMetaData(packModel.getCdaVersion(), dataSet.getCode(), metaDataCode);
                        if (StringUtils.isEmpty(resourceMetaData)) continue;

                        subRecord.addResource(resourceMetaData, metaDataRecord.getMetaData(metaDataCode));
                    }
                }
            }
        }

        // files that unable to get structured data, store as they are
        if (packModel.getProfileType() == ProfileType.File){
            resourceBucket.setCdaDocuments(((FilePackModel)packModel).getCdaDocuments());
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
     protected String ResourceMetaData(String cdaVersion, String dataSetCode, String metaDataCode){
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
}
