package com.yihu.ehr.service.resource.stage2.repo;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.util.ResourceStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 档案资源主库。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:20
 */
@Service
public class MasterResourceRepository {
    @Autowired
    HBaseDao hbaseDao;

    public void saveOrUpdate(ResourceBucket resBucket) throws Throwable {
        TableBundle bundle = new TableBundle();

        // delete legacy data if they are exist
        String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.MasterTable, "^" + resBucket.getId());
        if (legacyRowKeys != null && legacyRowKeys.length > 0){
            bundle.addRows(legacyRowKeys);
            hbaseDao.delete(ResourceCore.MasterTable, bundle);
        }

        // now save the data to hbase
        bundle.clear();
        bundle.addValues(resBucket.getId(),
                MasterResourceFamily.Basic,
                ResourceStorageUtil.getMasterResCells(MasterResourceFamily.Basic, resBucket));
        bundle.addValues(resBucket.getId(),
                MasterResourceFamily.Data,
                ResourceStorageUtil.getMasterResCells(MasterResourceFamily.Data, resBucket));

        hbaseDao.save(ResourceCore.MasterTable, bundle);
    }
}
