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
        String cleanUrl = "";
        if(redirectUriParameter.indexOf("?") != -1) {
            cleanUrl = redirectUriParameter.substring(0, redirectUriParameter.indexOf("?"));
        }else {
            cleanUrl = redirectUriParameter;
        }
        String sql = "SELECT url, out_url FROM apps WHERE id = '" + clientId + "'";
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
        if(cleanUrl.equals(resultMap.get("url").toString()) || cleanUrl.equals(resultMap.get("out_url").toString())) {
            return resultMap.get("url").toString();
        }else {
            return redirectUriParameter;
        }
    }

    public String getRealUrl(String clientId, String redirectUriParameter) {
        String cleanUrl = "";
        String queryParam = "";
        if(redirectUriParameter.indexOf("?") != -1) {
            queryParam = "&clientId=" + clientId;
            cleanUrl = redirectUriParameter.substring(0, redirectUriParameter.indexOf("?"));
        }else {
            queryParam = "?clientId=" + clientId;
            cleanUrl = redirectUriParameter;
        }
        String sql = "SELECT url, out_url FROM apps WHERE id = '" + clientId + "'";
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);
        if(cleanUrl.equals(resultMap.get("url").toString()) || cleanUrl.equals(resultMap.get("out_url").toString())) {
            return redirectUriParameter + queryParam;
        }else {
            return resultMap.get("url").toString() + queryParam;
        }
    }
}
