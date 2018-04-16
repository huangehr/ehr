package com.yihu.ehr.basic.appointment.service;

import com.yihu.ehr.basic.appointment.dao.RegistrationDao;
import com.yihu.ehr.basic.appointment.entity.Registration;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 挂号单 Service
 *
 * @author 张进军
 * @date 2018/4/16 19:18
 */
@Service
@Transactional
public class RegistrationService extends BaseJpaService<Registration, RegistrationDao> {

    @Autowired
    RegistrationDao registrationDao;

    public Registration getById(Integer id) {
        return registrationDao.findOne(id);
    }

    @Transactional(readOnly = false)
    public Registration save(Registration redisMqChannel) {
        return registrationDao.save(redisMqChannel);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        registrationDao.delete(id);
    }

}