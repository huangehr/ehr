package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.model.stage2.SubRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 档案资源子库。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Repository
public class SubResourceDao {

    @Autowired
    private HBaseDao hbaseDao;

    public void saveOrUpdate(ResourceBucket resourceBucket, OriginalPackage originalPackage) throws Exception {
        String rowKey = resourceBucket.getId();
        TableBundle bundle = new TableBundle();
        if (originalPackage.isReUploadFlg()) { //补传处理
            List<SubRecord> subRecordList = resourceBucket.getSubRecords();
            if (subRecordList.size() > 0) {
                //先删除
                String legacyRowKeys[] = new String[subRecordList.size()];
                for (int i = 0; i < subRecordList.size(); i++) {
                    legacyRowKeys[i] = subRecordList.get(i).getRowkey();
                }
                bundle.addRows(legacyRowKeys);
                hbaseDao.delete(resourceBucket.getSlave(), bundle);
                bundle.clear();
                //保存
                subRecordList.forEach(item -> {
                    bundle.addValues(
                            item.getRowkey(),
                            resourceBucket.getBasicFamily(),
                            resourceBucket.getSubBasicRecords(originalPackage.getProfileType())
                    );
                    bundle.addValues(
                            item.getRowkey(),
                            resourceBucket.getdFamily(),
                            item.getDataGroup()
                    );
                });
                hbaseDao.save(resourceBucket.getSlave(), bundle);
            }
        } else {
            // delete legacy data if they are exist
            String legacyRowKeys[] = hbaseDao.findRowKeys(resourceBucket.getSlave(), rowKey, rowKey.substring(0, rowKey.length() - 1) + "1", "^" + rowKey);
            if (legacyRowKeys != null && legacyRowKeys.length > 0) {
                bundle.addRows(legacyRowKeys);
                hbaseDao.delete(resourceBucket.getSlave(), bundle);
            }
            bundle.clear();
            // now save the data to hbase
            List<SubRecord> subRecordList = resourceBucket.getSubRecords();
            subRecordList.forEach(item -> {
                bundle.addValues(
                        item.getRowkey(),
                        resourceBucket.getBasicFamily(),
                        resourceBucket.getSubBasicRecords(originalPackage.getProfileType()));
                bundle.addValues(
                        item.getRowkey(),
                        resourceBucket.getdFamily(),
                        item.getDataGroup());
            });
            hbaseDao.save(resourceBucket.getSlave(), bundle);
        }
    }
}
