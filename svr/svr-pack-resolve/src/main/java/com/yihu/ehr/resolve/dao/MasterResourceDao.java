package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.MasterRecord;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.util.ResourceStorageUtil;
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

    public void saveOrUpdate(ResourceBucket resBucket, StandardPackage standardPackage) throws Exception {
        String rowKey = resBucket.getId();
        TableBundle bundle = new TableBundle();
        if(resBucket.isReUploadFlg()) { //补传处理
            Map<String, String> originResult = hbaseDao.get(ResourceCore.MasterTable, rowKey, MasterResourceFamily.Data);
            if(!originResult.isEmpty()) {
                MasterRecord masterRecord = resBucket.getMasterRecord();
                Map<String, String> supplement = masterRecord.getDataGroup();
                for(String key : supplement.keySet()) {
                    if(!originResult.containsKey(key)) {
                        originResult.put(key, supplement.get(key));
                    }
                }
                hbaseDao.deleteFamily(ResourceCore.MasterTable, rowKey, MasterResourceFamily.Data);
                bundle.addValues(rowKey, MasterResourceFamily.Data, originResult);
                hbaseDao.save(ResourceCore.MasterTable, bundle);
                Map<String, String> basicResult = hbaseDao.get(ResourceCore.MasterTable, rowKey, MasterResourceFamily.Basic);
                if(basicResult.get("event_type") != null) {
                    EventType eventType = EventType.create(basicResult.get("event_type"));
                    standardPackage.setEventType(eventType);
                }
                standardPackage.setDemographicId(basicResult.get("demographic_id"));
                resBucket.setDemographicId(basicResult.get("demographic_id"));
            }else {
                throw new RuntimeException("Please upload the complete package first !");
            }
        }else {
            // delete legacy data if they are exist
            /**
            String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.MasterTable, "^" + rowKey);
            if (legacyRowKeys != null && legacyRowKeys.length > 0) {
                bundle.addRows(legacyRowKeys);
                hbaseDao.delete(ResourceCore.MasterTable, bundle);
            }
            */
            //主表直接GET
            String legacy = hbaseDao.get(ResourceCore.MasterTable, rowKey);
            if(StringUtils.isNotEmpty(legacy)) {
                hbaseDao.delete(ResourceCore.MasterTable, rowKey);
            }
            // now save the data to hbase
            bundle.clear();
            bundle.addValues(
                    rowKey,
                    MasterResourceFamily.Basic,
                    ResourceStorageUtil.getMasterResCells(MasterResourceFamily.Basic, resBucket)
            );
            bundle.addValues(
                    rowKey,
                    MasterResourceFamily.Data,
                    ResourceStorageUtil.getMasterResCells(MasterResourceFamily.Data, resBucket)
            );
            hbaseDao.save(ResourceCore.MasterTable, bundle);
        }
    }
}
