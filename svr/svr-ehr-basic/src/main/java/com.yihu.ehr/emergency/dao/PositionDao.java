package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Position;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - 定位数据表
 * Created by progr1mmer on 2017/11/8.
 */
public interface PositionDao extends PagingAndSortingRepository<Position, String> {

}
