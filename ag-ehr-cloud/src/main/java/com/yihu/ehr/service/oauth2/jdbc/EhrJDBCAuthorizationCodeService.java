package com.yihu.ehr.service.oauth2.jdbc;

import org.springframework.asm.Type;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2验证码服务 jdbc 版本
 * code 的服务类
 * @author cwd
 * @version 1.0
 * @created 2017.02.21 11:03
 */
public class EhrJDBCAuthorizationCodeService extends RandomValueAuthorizationCodeServices {
    private static final String DEFAULT_SELECT_STATEMENT = "select code, authentication from oauth_code where code = ?";
    private static final String DEFAULT_INSERT_STATEMENT = "insert into oauth_code (code, authentication,create_time) values (?, ?,?)";
    private static final String DEFAULT_DELETE_STATEMENT = "delete from oauth_code where code = ?";

    private String selectAuthenticationSql = DEFAULT_SELECT_STATEMENT;
    private String insertAuthenticationSql = DEFAULT_INSERT_STATEMENT;
    private String deleteAuthenticationSql = DEFAULT_DELETE_STATEMENT;

    private final JdbcTemplate jdbcTemplate;

    public EhrJDBCAuthorizationCodeService(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        jdbcTemplate.update(insertAuthenticationSql,
                new Object[]{code, new SqlLobValue(SerializationUtils.serialize(authentication)), new Date()}, new int[]{
                        Types.VARCHAR, Types.BLOB, Types.TIMESTAMP});
    }

    public OAuth2Authentication remove(String code) {
        OAuth2Authentication authentication;

        try {
            authentication = jdbcTemplate.queryForObject(selectAuthenticationSql,
                    new RowMapper<OAuth2Authentication>() {
                        public OAuth2Authentication mapRow(ResultSet rs, int rowNum)
                                throws SQLException {
                            return SerializationUtils.deserialize(rs.getBytes("authentication"));
                        }
                    }, code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        if (authentication != null) {
            jdbcTemplate.update(deleteAuthenticationSql, code);
        }

        return authentication;
    }

    public void setSelectAuthenticationSql(String selectAuthenticationSql) {
        this.selectAuthenticationSql = selectAuthenticationSql;
    }

    public void setInsertAuthenticationSql(String insertAuthenticationSql) {
        this.insertAuthenticationSql = insertAuthenticationSql;
    }

    public void setDeleteAuthenticationSql(String deleteAuthenticationSql) {
        this.deleteAuthenticationSql = deleteAuthenticationSql;
    }
}
