package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.basic.government.dao.GovernmentBrowseLogRepository;
import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
@Transactional
@Service
public class GovernmentBrowseLogService extends BaseJpaService<GovernmentBrowseLog, GovernmentBrowseLogRepository> {
    @Autowired
    private GovernmentBrowseLogRepository governmentBrowseLogRepository;

    public GovernmentBrowseLog saveGovernmentBrowseLog(GovernmentBrowseLog governmentBrowseLog) {
        governmentBrowseLog = governmentBrowseLogRepository.save(governmentBrowseLog);
        return governmentBrowseLog;
    }

    public List<GovernmentBrowseLog> getBrowseName(String userId) {
        List<GovernmentBrowseLog> list = governmentBrowseLogRepository.findByUserId(userId);
        return list;
    }
}
