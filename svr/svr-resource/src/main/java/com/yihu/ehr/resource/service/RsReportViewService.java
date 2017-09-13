package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsReportViewDao;
import com.yihu.ehr.resource.model.RsReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 资源报表视图配置 Service
 *
 * @author 张进军
 * @created 2017.8.22 14:05
 */
@Service
@Transactional(readOnly = true)
public class RsReportViewService extends BaseJpaService<RsReportView, RsReportViewDao> {

    @Autowired
    private RsReportViewDao rsReportViewDao;

    /**
     * 根据资源报表ID，获取资源报表视图配置
     *
     * @param reportId 资源报表ID
     * @return RsReportView
     */
    public List<RsReportView> findByReportId(Integer reportId) {
        return rsReportViewDao.findByReportId(reportId);
    }

    /**
     * 根据资源报表ID、视图ID，获取资源报表视图配置
     *
     * @param reportId   资源报表ID
     * @param resourceId 视图ID
     * @return RsReportView
     */
    public RsReportView findByReportIdAndResourceId(Integer reportId, String resourceId) {
        return rsReportViewDao.findByReportIdAndResourceId(reportId, resourceId);
    }

    /**
     * 保存资源报表视图配置
     *
     * @param reportId 资源报表ID
     * @param list 资源报表视图配置集合
     * @return RsReportView 资源报表视图配置
     */
    @Transactional(readOnly = false)
    public void save(Integer reportId, List<RsReportView> list) {
        rsReportViewDao.deleteByReportId(reportId);
        rsReportViewDao.save(list);
    }

}
