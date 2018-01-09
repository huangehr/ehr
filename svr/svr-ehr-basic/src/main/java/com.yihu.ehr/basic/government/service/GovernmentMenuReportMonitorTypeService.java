package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.basic.government.dao.GovernmentMenuReportMonitorTypeDao;
import com.yihu.ehr.entity.government.GovernmentMenuReportMonitorType;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 政府平台菜单资源报表监测分类 Service
 *
 * @author jansney
 * @created 2017年11月7日15:23:14
 */
@Service
@Transactional(readOnly = true)
public class GovernmentMenuReportMonitorTypeService extends BaseJpaService<GovernmentMenuReportMonitorType, GovernmentMenuReportMonitorTypeDao> {

    @Autowired
    private GovernmentMenuReportMonitorTypeDao governmentMenuReportMonitorTypeDao;

    /**
     * 保存政府平台菜单资源报表监测分类
     *
     * @param govergnmentMenuReportMonitorType 政府平台菜单资源报表监测分类
     * @return GovernmentMenuReportMonitorType 政府平台菜单资源报表监测分类
     */
    @Transactional(readOnly = false)
    public GovernmentMenuReportMonitorType save(GovernmentMenuReportMonitorType govergnmentMenuReportMonitorType) {
        return governmentMenuReportMonitorTypeDao.save(govergnmentMenuReportMonitorType);
    }

    /**
     * 根据ID删除政府平台菜单资源报表监测分类及其所有后代
     *
     * @param id 资源报表监测分类ID
     */
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        governmentMenuReportMonitorTypeDao.delete(id);
    }

    public List<Integer> getMonitorTypeIdByGovernmentMenuId(String menuId) {
        return governmentMenuReportMonitorTypeDao.findMonitorTypeIdByGovernmentMenuId(Integer.parseInt(menuId));
    }
}
