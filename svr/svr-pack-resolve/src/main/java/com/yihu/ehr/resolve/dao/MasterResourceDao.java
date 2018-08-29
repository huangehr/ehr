package com.yihu.ehr.resolve.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.resolve.model.stage1.FilePackage;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage2.MasterRecord;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 档案资源主库。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:20
 */
@Repository
public class MasterResourceDao {

    @Autowired
    private HBaseDao hbaseDao;

    public void saveOrUpdate(ResourceBucket resourceBucket, OriginalPackage originalPackage) throws Exception {
        //如果是非结构化档案, 或者是 影像档案, 通过rowkey 判断结构化档案 是否有数据
        if(originalPackage instanceof FilePackage || originalPackage instanceof LinkPackage){
            String profileId = originalPackage.getProfileId().toString();
            String rowkey = profileId.substring(2,profileId.length());
            Map<String, String> originResult = hbaseDao.get(ResourceCore.MasterTable, rowkey, resourceBucket.getdFamily());
            /*if (!originResult.isEmpty()) {
                throw new IllegalJsonFileException("Please upload the struct package(" + rowkey + ") first !");
            }*/
        }
        String rowKey = resourceBucket.getId();
        TableBundle bundle = new TableBundle();
        if (originalPackage.isReUploadFlg()) { //补传处理
            Map<String, String> originResult = hbaseDao.get(resourceBucket.getMaster(), rowKey, resourceBucket.getdFamily());
            if (!originResult.isEmpty()) {
                MasterRecord masterRecord = resourceBucket.getMasterRecord();
                Map<String, String> supplement = masterRecord.getDataGroup();
                originResult.putAll(supplement);
                bundle.addValues(rowKey, resourceBucket.getdFamily(), originResult);
                hbaseDao.save(resourceBucket.getMaster(), bundle);
                Map<String, String> basicResult = hbaseDao.get(resourceBucket.getMaster(), rowKey, resourceBucket.getBasicFamily());
                updateFile(resourceBucket,originalPackage,basicResult);
                if (StringUtils.isNotEmpty(basicResult.get(ResourceCells.EVENT_TYPE))) {
                    EventType eventType = EventType.create(basicResult.get(ResourceCells.EVENT_TYPE));
                    originalPackage.setEventType(eventType);
                }
                resourceBucket.insertBasicRecord(ResourceCells.DEMOGRAPHIC_ID, basicResult.get(ResourceCells.DEMOGRAPHIC_ID));
            } else {
                throw new IllegalJsonFileException("Please upload the complete package(" + rowKey + ") first !");
            }
        } else {
            // delete legacy data if they are exist
            //主表直接GET
            String legacy = hbaseDao.get(resourceBucket.getMaster(), rowKey);
            if (StringUtils.isNotEmpty(legacy)) {
                hbaseDao.delete(resourceBucket.getMaster(), rowKey);
            }
            // now save the data to hbase
            bundle.clear();
            bundle.addValues(
                    rowKey,
                    resourceBucket.getBasicFamily(),
                    resourceBucket.getMasterBasicRecords(originalPackage.getProfileType())
            );
            bundle.addValues(
                    rowKey,
                    resourceBucket.getdFamily(),
                    resourceBucket.getMasterRecord().getDataGroup()
            );
            hbaseDao.save(resourceBucket.getMaster(), bundle);
        }
    }

    /**
     * 此处方法主要是非结构化档案补传,文件的更新
     * @param resourceBucket
     * @param originalPackage
     * @param basicResult
     */
    private void updateFile(ResourceBucket resourceBucket, OriginalPackage originalPackage,Map<String, String> basicResult){
        if(originalPackage instanceof FilePackage){
            String file_list = basicResult.get("file_list");
            JsonArray oldFileArray = new JsonParser().parse(file_list).getAsJsonArray();
            JsonArray newFileArray = new JsonArray();
            newFileArray.addAll(oldFileArray);
            //新上报的数据
            String file_list1 = resourceBucket.getBasicRecord("file_list");
            JsonArray waitAddFileArray = new JsonParser().parse(file_list1).getAsJsonArray();

            for(JsonElement waitAdd :waitAddFileArray){
                String cdaId = ((JsonObject) waitAdd).get("cda_document_id").getAsString();
                for(JsonElement oldFile :oldFileArray){
                    String oldCdaId = ((JsonObject) oldFile).get("cda_document_id").getAsString();
                    if(cdaId.equalsIgnoreCase(oldCdaId)){
                        newFileArray.remove(oldFile);
                    }
                }
            }
            newFileArray.addAll(waitAddFileArray);
            basicResult.put("file_list",newFileArray.toString());
            TableBundle bundle = new TableBundle();
            bundle.addValues(resourceBucket.getId(), resourceBucket.getBasicFamily(), basicResult);
            hbaseDao.save(resourceBucket.getMaster(), bundle);
        }else if (originalPackage instanceof LinkPackage){
            String file_list = basicResult.get("file_list");
            JsonArray oldFileArray = new JsonParser().parse(file_list).getAsJsonArray();
            //新上报的数据
            String file_list1 = resourceBucket.getBasicRecord("file_list");
            JsonArray waitAddFileArray = new JsonParser().parse(file_list1).getAsJsonArray();
            oldFileArray.addAll(waitAddFileArray);
            basicResult.put("file_list",oldFileArray.toString());
            TableBundle bundle = new TableBundle();
            bundle.addValues(resourceBucket.getId(), resourceBucket.getBasicFamily(), basicResult);
            hbaseDao.save(resourceBucket.getMaster(), bundle);
        }

    }
}