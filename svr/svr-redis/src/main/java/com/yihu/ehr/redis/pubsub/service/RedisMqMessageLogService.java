package com.yihu.ehr.redis.pubsub.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.pubsub.dao.RedisMqMessageLogDao;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * redis消息记录 Service
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@Service
@Transactional
public class RedisMqMessageLogService extends BaseJpaService<RedisMqMessageLog, RedisMqMessageLogDao> {

    @Autowired
    RedisMqMessageLogDao redisMqMessageLogDao;

    public RedisMqMessageLog getById(String id) {
        return redisMqMessageLogDao.findOne(id);
    }

    @Transactional(readOnly = false)
    public RedisMqMessageLog save(RedisMqMessageLog redisMqMessageLog) {
        return redisMqMessageLogDao.save(redisMqMessageLog);
    }

    @Transactional(readOnly = false)
    public void saveAndDeleteOld(RedisMqMessageLog newMessageLog, String oldMessageId) {
        redisMqMessageLogDao.save(newMessageLog);
        redisMqMessageLogDao.delete(oldMessageId);
    }


}