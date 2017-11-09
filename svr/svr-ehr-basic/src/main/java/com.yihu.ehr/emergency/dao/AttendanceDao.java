package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Attendance;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
public interface AttendanceDao extends PagingAndSortingRepository<Attendance, Integer> {
}
