package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.family.ResourceCells;
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
}