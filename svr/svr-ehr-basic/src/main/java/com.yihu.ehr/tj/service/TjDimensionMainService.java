package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjDimensionMain;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjDimensionMainRepository;
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


}
