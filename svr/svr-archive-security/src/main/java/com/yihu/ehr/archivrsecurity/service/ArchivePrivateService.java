package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.ScArchivePrivate;
import com.yihu.ehr.archivrsecurity.dao.repository.ArchivePrivateRespository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lyr on 2016/7/11.
 */
@Service
@Transactional
public class ArchivePrivateService extends BaseJpaService<ScArchivePrivate,ArchivePrivateRespository>{

    @Autowired
    ArchivePrivateRespository archivePrivateRep;

    /**
     * 保存档案公开隐藏状态
     * @param archivePrivates
     * @return
     */
    public List<ScArchivePrivate> saveArchivePrivate(List<ScArchivePrivate> archivePrivates)
    {
        for(ScArchivePrivate archivePrivate : archivePrivates)
        {
            archivePrivateRep.save(archivePrivate);
        }

        return archivePrivates;
    }

    /**
     * 更新档案状态
     * @param archivePrivates
     * @return
     */
    public List<ScArchivePrivate> updateArchivePrivate(List<ScArchivePrivate> archivePrivates) throws Exception
    {
        for(ScArchivePrivate archivePrivate : archivePrivates)
        {
             ScArchivePrivate archivePriv = archivePrivateRep.findByUserIdAndRowKey(archivePrivate.getUserId()
                     ,archivePrivate.getRowKey());
             if(archivePriv != null)
             {
                 archivePriv.setStatus(archivePrivate.getStatus());
                 archivePrivate.setId(archivePriv.getId());
             }
             else
             {
                 throw new Exception(String.format("不存在账号%s的档案状态数据%s", new String[]{archivePriv.getUserId()
                         ,archivePriv.getRowKey()}));
             }
        }

        return archivePrivates;
    }

    /**
     * 删除档案公开隐藏状态
     * @param userId
     * @param rowKey
     */
    public void deleteArchivePrivate(String userId,String rowKey)
    {
        String[] rowkeys = rowKey.split(",");

        for(String rowkey : rowkeys)
        {
            archivePrivateRep.deleteByUserIdAndRowKey(userId,rowkey);
        }
    }


    /**
     * 查找某个用户的档案公开状态
     * @param userId
     * @param rowKey
     * @param status
     * @return
     */
    public List<ScArchivePrivate> findByUserIdAndStatus(String userId,int status)
    {
        if(status > 0)
        {
            return archivePrivateRep.findByUserIdAndStatus(userId,status);
        }
        else
        {
            return archivePrivateRep.findByUserId(userId);
        }
    }

    /**
     * 查找某个用户的某个档案公开状态
     * @param userId
     * @param rowKey
     * @return
     */
    public ScArchivePrivate findByUserIdAndRowKey(String userId,String rowKey)
    {
        return archivePrivateRep.findByUserIdAndRowKey(userId,rowKey);
    }
}
