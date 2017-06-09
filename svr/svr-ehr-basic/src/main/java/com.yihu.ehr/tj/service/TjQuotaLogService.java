package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjQuotaLog;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjQuotaLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class TjQuotaLogService extends BaseJpaService<TjQuotaLog, XTjQuotaLogRepository> {


}
