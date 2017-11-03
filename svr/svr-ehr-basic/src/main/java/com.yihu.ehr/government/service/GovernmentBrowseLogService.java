package com.yihu.ehr.government.service;

import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import com.yihu.ehr.government.dao.XGovernmentBrowseLogRepository;
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
public class GovernmentBrowseLogService extends BaseJpaService<GovernmentBrowseLog, XGovernmentBrowseLogRepository> {
    @Autowired
    private XGovernmentBrowseLogRepository governmentBrowseLogRepository;

    public GovernmentBrowseLog saveGovernmentBrowseLog(GovernmentBrowseLog governmentBrowseLog) {
        governmentBrowseLog = governmentBrowseLogRepository.save(governmentBrowseLog);
        return governmentBrowseLog;
    }

    public List<String> getBrowseName(String userId) {
        List<String> list = governmentBrowseLogRepository.findByUserId(userId);
        return list;
    }
}
