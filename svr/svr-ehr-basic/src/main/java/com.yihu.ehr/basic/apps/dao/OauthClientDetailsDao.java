package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by progr1mmer on 2018/1/23.
 */
public interface OauthClientDetailsDao extends JpaRepository<OauthClientDetails, String> {

}
