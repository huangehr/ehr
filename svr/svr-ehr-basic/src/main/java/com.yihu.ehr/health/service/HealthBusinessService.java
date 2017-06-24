package com.yihu.ehr.health.service;

import com.yihu.ehr.entity.health.HealthBusiness;
import com.yihu.ehr.health.dao.XHealthBusinessRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */
@Service
@Transactional
public class HealthBusinessService extends BaseJpaService<HealthBusiness, XHealthBusinessRepository> {
    @Autowired
    XHealthBusinessRepository healthBusinessRepository;

    public List<HealthBusiness> getAllHealthBusiness(){
        return healthBusinessRepository.getAllHealthBusiness();
    }

    public List<HealthBusiness> searchByParentId(Integer parentId) {
        return healthBusinessRepository.searchByParentId(parentId);
    }

    public HealthBusiness getById(Integer id) {
        return healthBusinessRepository.findOne(id);
    }

    public void deleteHealthBusiness(Integer id) {
        healthBusinessRepository.delete(id);
    }

    public int getCountByName(String name) {
        List<HealthBusiness> list = healthBusinessRepository.searchByName(name);
        if (list != null && !list.isEmpty()){
            return list.size();
        }else {
            return 0;
        }
    }

    public int getCountByCode(String code) {
        List<HealthBusiness> list = healthBusinessRepository.searchByCode(code);
        if (list != null && !list.isEmpty()){
            return list.size();
        }else {
            return 0;
        }
    }

    public HealthBusiness saveHealthBusinesst(HealthBusiness healthBusinesst) {
        healthBusinessRepository.save(healthBusinesst);
        return healthBusinesst;
    }

    public HealthBusiness updateHealthBusiness(HealthBusiness healthBusiness) {
        healthBusiness = healthBusinessRepository.save(healthBusiness);
        return healthBusiness;
    }
}
