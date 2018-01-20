package com.yihu.ehr.oauth2.oauth2.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;

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
        List<String> keyId = jdbcTemplate.queryForList(DEFAULT_KEY_ID_SELECT_STATEMENT, new String []{publicKey}, String.class);
        if(keyId.size() <= 0) {
            throw new InsufficientAuthenticationException("Illegal authorized pk.");
        }
        return keyId.get(0);
    }

    public String getDefaultUserIdByKeyIdSelectStatement(String keyId) {
        List<String> userId =  jdbcTemplate.queryForList(DEFAULT_USER_ID_BY_KEY_ID_SELECT_STATEMENT, new String []{keyId}, String.class);
        if(userId.size() <= 0) {
            throw new InsufficientAuthenticationException("Unauthorized security key. please check you pk.");
        }
        return userId.get(0);
    }

    public String getDefaultUserNameByUserId(String userId) {
        List<String> username = jdbcTemplate.queryForList(DEFAULT_USER_NAME_BY_USER_ID, new String []{userId}, String.class);
        if(username.size() <= 0) {
            throw new InsufficientAuthenticationException("Illegal user id. please check you pk.");
        }
        return username.get(0);
    }
}
