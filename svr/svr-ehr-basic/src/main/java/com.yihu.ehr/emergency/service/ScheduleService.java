package com.yihu.ehr.emergency.service;

import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class ScheduleService extends BaseJpaService<Schedule, Integer> {

}
