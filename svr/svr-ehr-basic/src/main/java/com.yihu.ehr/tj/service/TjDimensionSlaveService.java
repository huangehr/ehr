package com.yihu.ehr.tj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.tj.TjDimensionSlave;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjDimensionSlaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class TjDimensionSlaveService extends BaseJpaService<TjDimensionSlave, XTjDimensionSlaveRepository> {


    @Autowired
    XTjDimensionSlaveRepository tjDimensionSlaveRepository;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * 根据ID获取.
     * @param
     */
    public TjDimensionSlave getTjDimensionSlave(Integer id) {
        TjDimensionSlave tjDimensionSlave = tjDimensionSlaveRepository.findOne(Long.valueOf(id));
        return tjDimensionSlave;
    }
}
