package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.PositionDao;
import com.yihu.ehr.entity.emergency.Position;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - 定位数据表
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class PositionService extends BaseJpaService<Position, PositionDao> {

}
