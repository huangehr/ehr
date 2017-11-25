package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Location;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - 待命地点
 * Created by progr1mmer on 2017/11/22.
 */
public interface LocationDao extends PagingAndSortingRepository<Location, Integer> {

    Location findById(Integer id);
}
