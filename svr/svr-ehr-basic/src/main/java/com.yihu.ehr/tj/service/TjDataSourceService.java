package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjDataSource;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjDataSourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/6/9.
 */
@Service
@Transactional
public class TjDataSourceService extends BaseJpaService<TjDataSource, XTjDataSourceRepository> {
}
