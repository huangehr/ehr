package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.family.FileResourceFamily;
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
        String tableName = ResourceCore.FileMasterTable;
        String dataColumn = FileResourceFamily.Data;
        String basicColumn = FileResourceFamily.Basic;
        if (resBucket.getProfileType() == ProfileType.Standard){
            tableName = ResourceCore.MasterTable;
            dataColumn = MasterResourceFamily.Data;
            basicColumn = MasterResourceFamily.Basic;
        }
        String rowKey = resBucket.getId();
        TableBundle bundle = new TableBundle();
        if (resBucket.isReUploadFlg()) { //补传处理
            Map<String, String> originResult = hbaseDao.get(tableName, rowKey, dataColumn);
            if (!originResult.isEmpty()) {
                MasterRecord masterRecord = resBucket.getMasterRecord();
                Map<String, String> supplement = masterRecord.getDataGroup();
                originResult.putAll(supplement);
                bundle.addValues(rowKey, dataColumn, originResult);
                hbaseDao.save(tableName, bundle);
                Map<String, String> basicResult = hbaseDao.get(tableName, rowKey, basicColumn);
                if (StringUtils.isNotEmpty(basicResult.get("event_type"))) {
                    EventType eventType = EventType.create(basicResult.get("event_type"));
                    standardPackage.setEventType(eventType);
                }
                standardPackage.setDemographicId(basicResult.get("demographic_id"));
                resBucket.setDemographicId(basicResult.get("demographic_id"));
            } else {
                throw new IllegalJsonFileException("Please upload the complete package(" + rowKey + ") first !");
            }
        } else {
            // delete legacy data if they are exist
            //主表直接GET
            String legacy = hbaseDao.get(tableName, rowKey);
            if (StringUtils.isNotEmpty(legacy)) {
                hbaseDao.delete(tableName, rowKey);
            }
            // now save the data to hbase
            bundle.clear();
            bundle.addValues(
                    rowKey,
                    basicColumn,
                    ResourceStorageUtil.getMasterResCells(basicColumn, resBucket)
            );
            bundle.addValues(
                    rowKey,
                    dataColumn,
                    ResourceStorageUtil.getMasterResCells(dataColumn, resBucket)
            );
            hbaseDao.save(tableName, bundle);
        }
    }
}