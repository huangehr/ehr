package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsReportCategoryAppDao;
import com.yihu.ehr.resource.model.ReportCategoryAppRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxw on 2017/11/24.
 */
@Transactional
@Service
public class RsReportCategoryAppService extends BaseJpaService<ReportCategoryAppRelation, RsReportCategoryAppDao> {

    @Autowired
    private RsReportCategoryAppDao rsReportCategoryAppDao;

    public void deleteByCategoryIdAndAppId(String reportCategoryId, String appId) {
        rsReportCategoryAppDao.deleteByReportCategoryIdAndAppId(Integer.parseInt(reportCategoryId), appId);
    }

    public ReportCategoryAppRelation saveInfo(String reportCategoryId, String appId) {
        ReportCategoryAppRelation reportCategoryAppRelation = new ReportCategoryAppRelation();
        reportCategoryAppRelation.setReportCategoryId(Integer.parseInt(reportCategoryId));
        reportCategoryAppRelation.setAppId(appId);
        reportCategoryAppRelation = rsReportCategoryAppDao.save(reportCategoryAppRelation);
        return reportCategoryAppRelation;
    }

    public String getAppIdByCategory(String reportCategoryId) {
        String result = "";
        List<String> apps = rsReportCategoryAppDao.findAppIdByCategory(Integer.parseInt(reportCategoryId));
        if (null != apps & apps.size() > 0) {
            result = String.join(",", apps);
        }
        return result;
    }
}
