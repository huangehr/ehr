package com.yihu.ehr.oauth2.oauth2.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by progr1mmer on 2017/9/19.
 */
public class EhrJdbcAccessUrlService {

    private JdbcTemplate jdbcTemplate;

    public EhrJdbcAccessUrlService(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getValidUrl(String clientId, String redirectUriParameter) {
        String cleanUrl;
        if (redirectUriParameter.indexOf("?") != -1) {
            cleanUrl = redirectUriParameter.substring(0, redirectUriParameter.indexOf("?"));
        } else {
            cleanUrl = redirectUriParameter;
        }
        String sql = "SELECT url, out_url FROM apps WHERE id = '" + clientId + "'";
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
        if (cleanUrl.equals(resultMap.get("url").toString()) || cleanUrl.equals(resultMap.get("out_url").toString())) {
            return resultMap.get("url").toString();
        } else {
            return redirectUriParameter;
        }
    }

    public String getRealUrl(String clientId, String redirectUriParameter) {
        String cleanUrl;
        String queryParam;
        if (redirectUriParameter.indexOf("?") != -1) {
            queryParam = "&clientId=" + clientId;
            cleanUrl = redirectUriParameter.substring(0, redirectUriParameter.indexOf("?"));
        } else {
            queryParam = "?clientId=" + clientId;
            cleanUrl = redirectUriParameter;
        }
        String sql = "SELECT url, out_url FROM apps WHERE id = '" + clientId + "'";
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
        if (cleanUrl.equals(resultMap.get("url").toString()) || cleanUrl.equals(resultMap.get("out_url").toString())) {
            return redirectUriParameter + queryParam;
        } else {
            return resultMap.get("url").toString() + queryParam;
        }
    }
}
