package com.yihu.ehr.quota.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.quota.TjDimensionMain;
import com.yihu.ehr.query.BaseJpaService;
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
public class TjDimensionMainService extends BaseJpaService<TjDimensionMain, XTjDimensionMainRepository> {


    @Autowired
    XTjDimensionMainRepository tjDimensionMainRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据ID获取.
     * @param
     */
    public TjDimensionMain getTjDimensionMain(Integer id) {
        TjDimensionMain tjDimensionMain = tjDimensionMainRepository.findOne(Long.valueOf(id));
        return tjDimensionMain;
    }
}
