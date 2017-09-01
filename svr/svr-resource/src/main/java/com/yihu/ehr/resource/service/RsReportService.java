package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsReportDao;
import com.yihu.ehr.resource.model.RsReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源报表 Service
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
@Service
@Transactional(readOnly = true)
public class RsReportService extends BaseJpaService<RsReport, RsReportDao> {

    @Autowired
    private RsReportDao rsReportDao;

    /**
     * 根据ID，获取资源报表
     *
     * @param id 资源报表ID
     * @return RsReport
     */
    public RsReport getById(Integer id) {
        return rsReportDao.findOne(id);
    }

    /**
     * 根据Code，获取资源报表
     *
     * @param code 资源报表Code
     * @return RsReport
     */
    public RsReport getByCode(String code) {
        return rsReportDao.findByCode(code);
    }

    /**
     * 保存资源报表
     *
     * @param rsReport 资源报表
     * @return RsReport 资源报表
     */
    @Transactional(readOnly = false)
    public RsReport save(RsReport rsReport) {
        return rsReportDao.save(rsReport);
    }

    /**
     * 根据ID删除资源报表
     *
     * @param id 资源报表ID
     */
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        rsReportDao.delete(id);
    }

    /**
     * 验证资源报表编码是否唯一
     *
     * @param id   主键
     * @param code 编码
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueCode(Integer id, String code) {
        RsReport rsReport = rsReportDao.isUniqueCode(id, code);
        if (rsReport == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证资源报表编码是否唯一
     *
     * @param id   主键
     * @param name 名称
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueName(Integer id, String name) {
        RsReport rsReport = rsReportDao.isUniqueName(id, name);
        if (rsReport == null) {
            return true;
        } else {
            return false;
        }
    }

}
