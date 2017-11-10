package com.yihu.ehr.emergency.service;

import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class AmbulanceService extends BaseJpaService<Ambulance, String> {


}
