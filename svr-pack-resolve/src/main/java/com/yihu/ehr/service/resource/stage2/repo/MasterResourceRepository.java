package com.yihu.ehr.service.resource.stage2.repo;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.util.ResourceStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    public void save(ResourceBucket resBucket) throws IOException {
        TableBundle bundle = new TableBundle();
        bundle.addValues(resBucket.getId(),
                MasterResourceFamily.Basic,
                ResourceStorageUtil.getMasterResCells(MasterResourceFamily.Basic, resBucket));
        bundle.addValues(resBucket.getId(),
                MasterResourceFamily.Resource,
                ResourceStorageUtil.getMasterResCells(MasterResourceFamily.Resource, resBucket));

        hbaseDao.saveOrUpdate(ResourceStorageUtil.MasterTable, bundle);
    }
}
