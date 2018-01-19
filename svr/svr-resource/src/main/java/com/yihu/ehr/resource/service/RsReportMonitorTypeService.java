package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsReportDao;
import com.yihu.ehr.resource.dao.RsReportMonitorTypeDao;
import com.yihu.ehr.resource.model.RsReport;
import com.yihu.ehr.resource.model.RsReportMonitorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表监测分类 Service
 *
 * @author jansney
 * @created 2017年11月7日15:23:14
 */
@Service
@Transactional(readOnly = true)
public class RsReportMonitorTypeService extends BaseJpaService<RsReportMonitorType, RsReportMonitorTypeDao> {

    @Autowired
    private RsReportMonitorTypeDao rsReportMonitorTypeDao;
    @Autowired
    private RsReportDao rsReportDao;

    /**
     * 根据ID，获取资源报表监测分类
     *
     * @param id 资源报表监测分类ID
     * @return RsReportMonitorType
     */
    public RsReportMonitorType getById(Integer id) {
        return rsReportMonitorTypeDao.findOne(id);
    }

    /**
     * 保存资源报表监测分类
     *
     * @param rsReportMonitorType 资源报表监测分类
     * @return RsReportMonitorType 资源报表监测分类
     */
    @Transactional(readOnly = false)
    public RsReportMonitorType save(RsReportMonitorType rsReportMonitorType) {
        return rsReportMonitorTypeDao.save(rsReportMonitorType);
    }

    /**
     * 根据ID删除资源报表监测分类及其所有后代
     *
     * @param id 资源报表监测分类ID
     */
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        rsReportMonitorTypeDao.delete(id);
    }

    /**
     * 验证资源报表监测分类编码是否唯一
     *
     * @param id   主键
     * @param name 名称
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueName(Integer id, String name) {
        RsReportMonitorType rsReportMonitorType = rsReportMonitorTypeDao.isUniqueName(id, name);
        if (rsReportMonitorType == null) {
            return true;
        } else {
            return false;
        }
    }

    public List<RsReportMonitorType> getInfoById(List<Integer> monitorTypeIds) {
        return rsReportMonitorTypeDao.findById(monitorTypeIds);
    }
}
