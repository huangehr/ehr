package com.yihu.ehr.api.esb.service;

import com.yihu.ehr.api.esb.model.HosAcqTask;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.12 17:56
 */
@Service
@Transactional
public class HosAcqTaskService extends BaseJpaService<HosAcqTask, XHosAcqTaskRepository> {

}