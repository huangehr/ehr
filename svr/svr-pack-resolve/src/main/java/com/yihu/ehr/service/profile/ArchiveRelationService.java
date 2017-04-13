package com.yihu.ehr.service.profile;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.family.ResourceFamily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


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
        //判断记录是否存在
        String re =hbaseDao.get(ResourceCore.MasterTable,profileId);

        if(!StringUtils.isEmpty(re))
        {
            hbaseDao.put(ResourceCore.MasterTable,profileId,ResourceFamily.Basic,MasterResourceFamily.BasicColumns.DemographicId,idCardNo);
        }
        else{
            throw new Exception("不存在改条记录");
        }
    }


}
