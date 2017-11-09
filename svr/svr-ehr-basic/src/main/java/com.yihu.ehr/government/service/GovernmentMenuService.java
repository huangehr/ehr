package com.yihu.ehr.government.service;

import com.yihu.ehr.entity.government.GovernmentMenu;
import com.yihu.ehr.entity.government.GovernmentMenuReportMonitorType;
import com.yihu.ehr.government.dao.GovernmentMenuReportMonitorTypeDao;
import com.yihu.ehr.government.dao.XGovernmentMenuRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by wxw on 2017/11/2.
 */
@Transactional
@Service
public class GovernmentMenuService extends BaseJpaService<GovernmentMenu,XGovernmentMenuRepository> {
    @Autowired
    private XGovernmentMenuRepository governmentMenuRepository;
    @Autowired
    private GovernmentMenuReportMonitorTypeDao governmentMenuReportMonitorTypeDao;

    public GovernmentMenu getById(Integer id) {
        return governmentMenuRepository.findOne(id);
    }

    public int checkCode(String code) throws Exception{
        String filters = "status=1;code=" + code;
        List<GovernmentMenu> governmentMenu = this.search(filters);
        if (null != governmentMenu && governmentMenu.size() > 0) {
            return governmentMenu.size();
        }
        return 0;
    }

    public int checkName(String name) throws Exception{
        String filters = "status=1;name=" + name;
        List<GovernmentMenu> governmentMenu = this.search(filters);
        if (null != governmentMenu && governmentMenu.size() > 0) {
            return governmentMenu.size();
        }
        return 0;
    }

    public GovernmentMenu saveGovernmentMenu(GovernmentMenu governmentMenu, String ids) {
        governmentMenu.setCreateTime(new Date());
        governmentMenu = governmentMenuRepository.save(governmentMenu);
        if (null != governmentMenu && governmentMenu.getId() != 0) {
            //往政府服务菜单报表监测配置表插入数据
            saveGovernmentMenuReportMonitorType(governmentMenu, ids);
        }
        return governmentMenu;
    }

    public GovernmentMenu updateGovernmentMenu(GovernmentMenu governmentMenu, String ids) {
        governmentMenu.setUpdateTime(new Date());
        governmentMenu = governmentMenuRepository.save(governmentMenu);
        if (null != governmentMenu) {
            //先删掉该菜单下的检测类型
            governmentMenuReportMonitorTypeDao.deleteByGovernmentMenuId(governmentMenu.getId());
            //往政府服务菜单报表监测配置表插入数据
            saveGovernmentMenuReportMonitorType(governmentMenu, ids);
        }
        return governmentMenu;
    }

    private void saveGovernmentMenuReportMonitorType(GovernmentMenu governmentMenu, String ids) {
        String[] array = ids.split(";");
        for (int i = 0; i < array.length; i++) {
            GovernmentMenuReportMonitorType monitorType = new GovernmentMenuReportMonitorType();
            monitorType.setGovernmentMenuId(governmentMenu.getId());
            monitorType.setRsReoportMonitorTypeId(Integer.parseInt(array[i]));
            governmentMenuReportMonitorTypeDao.save(monitorType);
        }
    }
}
