package com.yihu.ehr.oauth2.oauth2.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * Created by progr1mmer on 2018/1/8.
 */
public class EhrJdbcUserSecurityService {

    private static final String DEFAULT_KEY_ID_SELECT_STATEMENT = "SELECT id FROM user_security WHERE public_key = ?";
    private static final String DEFAULT_USER_ID_BY_KEY_ID_SELECT_STATEMENT = "SELECT user_id FROM user_key WHERE key_id = ?";
    private static final String DEFAULT_USER_NAME_BY_USER_ID = "SELECT login_code FROM users WHERE id = ?";

    private JdbcTemplate jdbcTemplate;

    public EhrJdbcUserSecurityService(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getDefaultKeyIdSelectStatement(String publicKey) {
        return jdbcTemplate.queryForObject(DEFAULT_KEY_ID_SELECT_STATEMENT, new String []{publicKey}, String.class);
    }

    public String getDefaultUserIdByKeyIdSelectStatement(String keyId) {
        return jdbcTemplate.queryForObject(DEFAULT_USER_ID_BY_KEY_ID_SELECT_STATEMENT, new String []{keyId}, String.class);
    }

    public String getDefaultUserNameByUserId(String userId) {
        return jdbcTemplate.queryForObject(DEFAULT_USER_NAME_BY_USER_ID, new String []{userId}, String.class);
    }
}
