package com.yihu.ehr.resource.service;

import com.yihu.ehr.entity.quota.TjQuotaCategory;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsReportUsersDao;
import com.yihu.ehr.resource.model.RsReport;
import com.yihu.ehr.resource.model.RsReportUsers;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxw on 2018/7/31.
 */
@Service
@Transactional(readOnly = true)
public class RsReportUsersService extends BaseJpaService<RsReportUsers, RsReportUsersDao> {

    @Autowired
    private RsReportUsersDao rsReportUsersDao;

    /**
     * 根据用户编码获取报表列表
     * @param userId
     * @return
     */
    public List<RsReportUsers> getRsReportUsersList(String userId) {
        List<RsReportUsers> reportUsersList = rsReportUsersDao.findByUserId(userId);
        return  reportUsersList;
    }

    public List<RsReport> getOtherReportListByUserId(String userId) {
        String sql = "select rs_report.* from rs_report where status = 1 and code not in (SELECT report_code from rs_report_users where user_id = '" + userId +"') and rs_report.REPORT_CATEGORY_ID in (SELECT ID from rs_report_category where pid in (SELECT id FROM rs_report_category where CODE in ('businessMonitoringSystem','healthMonitorSystem')))";

        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsReport.class);
        List query = jdbcTemplate.query(sql, rowMapper);
        return query;
    }

    /**
     * 根据用户编码删除用户报表
     * @param userId
     */
    public void deleteByUserId(String userId) {
        rsReportUsersDao.deleteByUserId(userId);
    }

    /**
     * 保存用户与报表关系
     * @param userId
     * @param list
     */
    @Transactional(readOnly = false)
    public void saveRsReportUser(String userId, List<RsReportUsers> list) {
        rsReportUsersDao.findByUserId(userId);
        rsReportUsersDao.save(list);
    }
}
