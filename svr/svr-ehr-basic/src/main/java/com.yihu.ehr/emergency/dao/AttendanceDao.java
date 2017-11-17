package com.yihu.ehr.emergency.dao;

import com.yihu.ehr.entity.emergency.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Dao - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
public interface AttendanceDao extends PagingAndSortingRepository<Attendance, Integer> {

    @Query("SELECT attendance FROM Attendance attendance WHERE attendance.carId = :carId AND attendance.status IN (:statuses)")
    Attendance findByCarIdAndStatus(@Param("carId") String carId, @Param("statuses") List<Attendance.Status> statuses);

    @Query("SELECT max(attendance) FROM Attendance attendance WHERE attendance.startTime <= :startTime AND attendance.carId = :carId ORDER BY attendance.startTime DESC")
    Attendance findByStartTimeAndCarId(@Param("startTime") Date startTime, @Param("carId") String carId);
}
