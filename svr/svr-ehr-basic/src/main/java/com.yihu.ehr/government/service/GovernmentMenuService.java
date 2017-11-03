package com.yihu.ehr.government.service;

import com.yihu.ehr.entity.government.GovernmentMenu;
import com.yihu.ehr.government.dao.XGovernmentMenuRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public GovernmentMenu saveGovernmentMenu(GovernmentMenu governmentMenu) {
        governmentMenu.setCreateTime(new Date());
        governmentMenu = governmentMenuRepository.save(governmentMenu);
        return governmentMenu;
    }

    public GovernmentMenu updateGovernmentMenu(GovernmentMenu governmentMenu) {
        governmentMenu.setUpdateTime(new Date());
        governmentMenu = governmentMenuRepository.save(governmentMenu);
        return governmentMenu;
    }
}
