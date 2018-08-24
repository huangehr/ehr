package com.yihu.ehr.zuul.config;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by progr1mmer on 2018/8/24.
 */

public class JdbcZuulRouteService {

    private final JdbcTemplate jdbcTemplate;

    public JdbcZuulRouteService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addZuulRoute(ZuulProperties.ZuulRoute zuulRoute) {
        jdbcTemplate.update("insert into zuul_route (id, path, service_id, url, "
                + "retryable, enabled) values (?, ?, ?, ?, ?, ?)", getFieldsForUpdate(zuulRoute));
    }

    public int removeZuulRoute(String id) {
        return jdbcTemplate.update("delete from zuul_route where id = ?", id);
    }

    public int updateZuulRouteEnable(String id, int enabled) {
        return jdbcTemplate.update("update zuul_route set enabled = ? where id = ?", new Object[]{id, enabled});
    }

    public List<ZuulProperties.ZuulRoute> loadZuulRoutes() {
        List<ZuulProperties.ZuulRoute> zuulRoutes = jdbcTemplate.query("select id, path, service_id, url, "
                + "retryable, enabled from zuul_route", new BeanPropertyRowMapper<>(ZuulProperties.ZuulRoute.class));
        return zuulRoutes;
    }

    public List<ZuulProperties.ZuulRoute> loadEnabledZuulRoutes() {
        List<ZuulProperties.ZuulRoute> zuulRoutes = jdbcTemplate.query("select id, path, service_id, url, "
                + "retryable, enabled from zuul_route where enabled = 1", new BeanPropertyRowMapper<>(ZuulProperties.ZuulRoute.class));
        return zuulRoutes;
    }

    private Object [] getFieldsForUpdate(ZuulProperties.ZuulRoute zuulRoute) {
        return new Object[]{
                zuulRoute.getId(),
                zuulRoute.getPath(),
                zuulRoute.getServiceId(),
                zuulRoute.getUrl(),
                zuulRoute.getRetryable(),
                1
        };
    }
}
