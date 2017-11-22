package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.LocationDao;
import com.yihu.ehr.entity.emergency.Location;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - 待命地点
 * Created by progr1mmer on 2017/11/22.
 */
@Service
@Transactional
public class LocationService extends BaseJpaService<Location, LocationDao> {

    @Autowired
    private LocationDao locationDao;

    public Location findById(Integer id) {
        return locationDao.findById(id);
    }
}
