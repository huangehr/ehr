package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.OauthClientDetailsDao;
import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;

/**
 * Created by progr1mmer on 2018/1/23.
 */
@Service
public class OauthClientDetailsService extends BaseJpaService<OauthClientDetails, OauthClientDetailsDao> {

}
