package com.yihu.ehr.basic.appointment.service;

import com.yihu.ehr.basic.appointment.dao.RegistrationDao;
import com.yihu.ehr.basic.appointment.entity.Registration;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    public Registration getById(String id) {
        return registrationDao.findOne(id);
    }

    @Transactional(readOnly = false)
    public Registration save(Registration registration) {
        return registrationDao.save(registration);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        registrationDao.delete(id);
    }

    /**
     * 根据就诊日期判断，更新过期的未就诊挂号单改为已就诊状态
     *
     * @param list 挂号单集合
     */
    @Transactional
    public void updateStateByRegisterDate(List<Registration> list) {
        Date currDate = DateUtil.getSysDate();
        for (int i = 0, size = list.size(); i < size; i++) {
            Registration registration = list.get(i);
            Integer state = registration.getState();
            if (state != null && (state == 1 || state == 2 || state == 11 || state == 22)) {
                Date registerDate = DateUtil.parseDate(registration.getRegisterDate(),
                        DateUtil.DEFAULT_DATE_YMD_FORMAT);
                if (DateUtil.compareDateTime(currDate, registerDate) > 0) {
                    registration.setState(3);
                    registration.setStateDesc("已就诊");
                    save(registration);
                }
            }
        }
    }

}