package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Schedule;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - 排版历史
 * Created by progr1mmer on 2017/11/8.
 */
public interface ScheduleDao extends PagingAndSortingRepository<Schedule, Integer> {

    Schedule findById(int id);

}
