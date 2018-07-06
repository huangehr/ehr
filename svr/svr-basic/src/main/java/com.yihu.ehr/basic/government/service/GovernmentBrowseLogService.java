package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.basic.government.dao.GovernmentBrowseLogRepository;
import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import com.yihu.ehr.model.resource.MRsReport;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
@Transactional
@Service
public class GovernmentBrowseLogService extends BaseJpaService<GovernmentBrowseLog, GovernmentBrowseLogRepository> {
    @Autowired
    private GovernmentBrowseLogRepository governmentBrowseLogRepository;
    @Autowired
    private JdbcTemplate jdbcTmeplate;

    public GovernmentBrowseLog saveGovernmentBrowseLog(GovernmentBrowseLog governmentBrowseLog) {
        governmentBrowseLog = governmentBrowseLogRepository.save(governmentBrowseLog);
        return governmentBrowseLog;
    }

    public List<GovernmentBrowseLog> getBrowseName(String userId) {
        String sql = "select show_type from rs_report where code = ?";
        List<GovernmentBrowseLog> list = governmentBrowseLogRepository.findByUserId(userId);
        for (GovernmentBrowseLog governmentBrowseLog : list) {
            List<MRsReport> query = jdbcTmeplate.query(sql, new BeanPropertyRowMapper(MRsReport.class), governmentBrowseLog.getResourceId());
            if (null != query && query.size() > 0) {
                governmentBrowseLog.setShowType(query.get(0).getShowType());
            }

        }
        return list;
    }

    public List getHotBrowseLog() {
        String sql = "select gbl.*,show_type from government_browse_log gbl join rs_report rp on gbl.resource_id=rp.code" +
                " WHERE gbl.resource_id = ? order by gbl.create_time desc limit 1";
        List<GovernmentBrowseLog> hotList = new ArrayList<>();
        List<String> codeList = governmentBrowseLogRepository.getSortByCode();
        for (String code : codeList) {
            List<GovernmentBrowseLog> query = jdbcTmeplate.query(sql, new BeanPropertyRowMapper(GovernmentBrowseLog.class), code);
            if (null != query && query.size() > 0) {
                hotList.add(query.get(0));
            }
        }
        return hotList;
    }
}
