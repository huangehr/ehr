package com.yihu.ehr.basic.appointment.dao;

import com.yihu.ehr.basic.appointment.entity.Registration;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 挂号单 DAO
 *
 * @author 张进军
 * @date 2018/4/16 19:18
 */
public interface RegistrationDao extends PagingAndSortingRepository<Registration, String> {


}
