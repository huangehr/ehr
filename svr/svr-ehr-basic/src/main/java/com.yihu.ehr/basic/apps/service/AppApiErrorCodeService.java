package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.AppApiErrorCodeDao;
import com.yihu.ehr.entity.api.AppApiErrorCode;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - Api错误码
 * Created by progr1mmer on 2018/3/15.
 */
@Service
@Transactional
public class AppApiErrorCodeService extends BaseJpaService<AppApiErrorCode, AppApiErrorCodeDao> {

}
