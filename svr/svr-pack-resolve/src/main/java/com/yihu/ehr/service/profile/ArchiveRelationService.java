package com.yihu.ehr.service.profile;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.family.ResourceFamily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 档案关联
 * Created by hzp on 2017/4/11.
*/
@Service
public class ArchiveRelationService {

    @Autowired
    HBaseDao hbaseDao;

    /**
     * 档案关联
     */
    public void archiveRelation(String profileId,String idCardNo) throws Exception
    {
        hbaseDao.put(ResourceCore.MasterTable,profileId,ResourceFamily.Basic,"name",idCardNo);
    }


}
