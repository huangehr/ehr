package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Ambulance;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
public interface AmbulanceDao extends PagingAndSortingRepository<Ambulance, String> {

}
