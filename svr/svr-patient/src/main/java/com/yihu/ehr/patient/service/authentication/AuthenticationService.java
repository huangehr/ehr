package com.yihu.ehr.patient.service.authentication;

import com.yihu.ehr.patient.dao.XAuthenticationRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@Service
@Transactional
public class AuthenticationService extends BaseJpaService<Authentication, XAuthenticationRepository> {


}
