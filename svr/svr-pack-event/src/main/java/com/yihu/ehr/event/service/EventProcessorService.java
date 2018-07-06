package com.yihu.ehr.event.service;

import com.yihu.ehr.entity.event.EventProcessor;
import com.yihu.ehr.event.dao.EventProcessorDao;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 档案解析事件处理器
 *
 * Created by progr1mmer on 2018/7/5.
 */
@Service
@Transactional
public class EventProcessorService extends BaseJpaService<EventProcessor, EventProcessorDao> {

    @Autowired
    private EventProcessorDao eventProcessorDao;

    public EventProcessor findByName(String name) {
        return eventProcessorDao.findByName(name);
    }
}
