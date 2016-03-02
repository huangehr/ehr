package com.yihu.ehr.standard.dispatch.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
@Transactional
@Service
public class DispatchLogService extends BaseJpaService<DispatchLog, XDispatchLogRepository> {



}
