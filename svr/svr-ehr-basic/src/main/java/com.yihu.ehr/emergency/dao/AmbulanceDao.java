package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Ambulance;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Dao - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
public interface AmbulanceDao extends PagingAndSortingRepository<Ambulance, String> {

    Ambulance findById(String id);

    Ambulance findByPhone(String phone);
}
