package com.yihu.ehr.oauth2.oauth2.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by progr1mmer on 2017/9/19.
 */
@Component
public class EhrAppsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getValidUrl(String clientId, String redirectUriParameter) {
        String sql = "SELECT url, out_url FROM apps WHERE id = '" + clientId + "'";
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
        if(redirectUriParameter.equals(resultMap.get("url").toString()) || redirectUriParameter.equals(resultMap.get("out_url").toString())) {
            return resultMap.get("url").toString();
        }else {
            return redirectUriParameter;
        }
    }

    public String getRealUrl(String clientId, String redirectUriParameter) {
        String sql = "SELECT url, out_url FROM apps WHERE id = '" + clientId + "'";
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
        if(redirectUriParameter.equals(resultMap.get("url").toString()) || redirectUriParameter.equals(resultMap.get("out_url").toString())) {
            return redirectUriParameter;
        }else {
            return resultMap.get("url").toString();
        }
    }
}
