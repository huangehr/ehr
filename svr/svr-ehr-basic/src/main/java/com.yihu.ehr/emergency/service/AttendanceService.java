package com.yihu.ehr.emergency.service;

import com.yihu.ehr.entity.emergency.Attendance;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class AttendanceService extends BaseJpaService<Attendance, Integer> {

}
