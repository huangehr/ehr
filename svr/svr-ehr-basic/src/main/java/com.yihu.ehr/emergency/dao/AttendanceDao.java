package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Dao - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
public interface AttendanceDao extends PagingAndSortingRepository<Attendance, Integer> {

    @Query("SELECT attendance FROM Attendance attendance WHERE attendance.carId = :carId AND (attendance.status = :status1 OR  attendance.status  = :status2)")
    Attendance findByCarIdAndStatus(@Param("carId") String carId, @Param("status1") Attendance.Status status1, @Param("status2") Attendance.Status status2);
}
