package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.AmbulanceDao;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.PublicKey;

/**
 * Service - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class AmbulanceService extends BaseJpaService<Ambulance, AmbulanceDao> {

    @Autowired
    private AmbulanceDao ambulanceDao;

    public Ambulance findById(String carId){
        return ambulanceDao.findById(carId);
    }

    public Ambulance findByPhone(String phone) {
        return ambulanceDao.findByPhone(phone);
    }
}
