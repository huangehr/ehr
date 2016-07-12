package com.yihu.ehr.service;

import com.yihu.ehr.dao.model.AuthorizeAppApply;
import com.yihu.ehr.dao.repository.XAuthorizeAppApplyRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Service
@Transactional
public class AuthorizeAppApplyService extends BaseJpaService<AuthorizeAppApply, XAuthorizeAppApplyRepository> {


}
