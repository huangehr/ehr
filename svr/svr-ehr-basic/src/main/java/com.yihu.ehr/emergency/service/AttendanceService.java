package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.AttendanceDao;
import com.yihu.ehr.entity.emergency.Attendance;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Service - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class AttendanceService extends BaseJpaService<Attendance, AttendanceDao> {

    @Autowired
    private AttendanceDao attendanceDao;

    public Attendance findByCarIdAndStatus(String carId, List<Attendance.Status> statuses) {
        return attendanceDao.findByCarIdAndStatus(carId, statuses);
    }

    public Attendance findByCreateDateAndCarId(Date startTime, String carId) {
        return attendanceDao.findByCreateDateAndCarId(startTime, carId);
    }

}
