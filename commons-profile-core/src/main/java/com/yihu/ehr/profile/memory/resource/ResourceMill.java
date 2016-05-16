package com.yihu.ehr.profile.memory.resource;

import com.yihu.ehr.profile.memory.intermediate.MetaDataRecord;
import com.yihu.ehr.profile.memory.intermediate.StdDataSet;
import com.yihu.ehr.profile.memory.intermediate.MemoryProfile;
import com.yihu.ehr.profile.memory.util.DataSetUtil;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.Set;

/**
 * 档案资源化压碎机，将档案数据压碎成资源点。
 *
 * @author Sand
 * @created 2016.05.16 13:51
 */
public class ResourceMill {

    /**
     * 将解析好的档案拆解成资源。
     *
     * @param profile
     * @return
     */
    public ResourceBucket grindingProfile(MemoryProfile profile){
        ResourceBucket resourceBucket = new ResourceBucket();
        BeanUtils.copyProperties(profile, resourceBucket);

        Collection<StdDataSet> stdDataSets = profile.getDataSets();
        for (StdDataSet dataSet : stdDataSets){
            if(DataSetUtil.isOriginDataSet(dataSet.getCode())) continue;

            Set<String> keys = dataSet.getRecordKeys();

            if (dataSet.isMainRecord()){
                MainRecord mainRecord = resourceBucket.getMainRecord();

                for (String key : keys){
                    MetaDataRecord metaDataRecord = dataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        String resourceCode = ResourceMetaData(profile.getCdaVersion(), dataSet.getCode(), metaDataCode);

                        mainRecord.addResource(resourceCode, metaDataRecord.getMetaData(metaDataCode));
                    }

                    // 仅一条记录
                    break;
                }
            } else {
                SubRecords subRecords = resourceBucket.getSubRecords();

                for (String key : keys){
                    SubRecord subRecord = new SubRecord();
                    subRecords.addRecord(subRecord);

                    MetaDataRecord metaDataRecord = dataSet.getRecord(key);
                    for (String metaDataCode : metaDataRecord.getMetaDataCodes()){
                        String resourceCode = ResourceMetaData(profile.getCdaVersion(), dataSet.getCode(), metaDataCode);

                        subRecord.addResource(resourceCode, metaDataRecord.getMetaData(metaDataCode));
                    }
                }
            }
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
         // 翻译时需要的内容：对CODE与VALUE处理后再翻译

         return "";
     }
}
