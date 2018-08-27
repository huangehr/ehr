package com.yihu.ehr.zuul.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by progr1mmer on 2018/8/24.
 */
@RestController
@RequestMapping("/route")
public class ComplexRouteEndPoint {

    @Autowired
    private ComplexRouteLocator complexRouteLocator;
    @Autowired
    private JdbcZuulRouteService jdbcZuulRouteService;

    @PostMapping
    public String save (
            //主键
            @RequestParam(name = "id") String id,
            //网关暴露出去的路径   例:/v1/simple2/**"
            @RequestParam(name = "path") String path,
            //注册到微服务的Id
            @RequestParam(name = "serviceId", required = false) String serviceId,
            //跳转的路径   例:http://localhost:10010/
            @RequestParam(name = "url", required = false) String url,
            //失败是否重试
            @RequestParam(name = "retryable", required = false, defaultValue = "true") Boolean retryable) {
        ZuulProperties.ZuulRoute ruleRoute = new ZuulProperties.ZuulRoute();
        ruleRoute.setId(id);
        // Prepend with slash if not already present.
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (StringUtils.hasText(complexRouteLocator.getProperties().getPrefix())) {
            path = complexRouteLocator.getProperties().getPrefix() + path;
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
        }
        ruleRoute.setPath(path);
        ruleRoute.setServiceId(serviceId);
        ruleRoute.setRetryable(retryable);
        ruleRoute.setUrl(url);
        jdbcZuulRouteService.addZuulRoute(ruleRoute);
        complexRouteLocator.refresh();
        return "success";
    }

    @DeleteMapping
    public String remove (
            @RequestParam(name = "id") String id) {
        jdbcZuulRouteService.removeZuulRoute(id);
        complexRouteLocator.getProperties().getRoutes().remove(id);
        complexRouteLocator.refresh();
        return "success";
    }

    @PutMapping
    public String enable(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "enabled") Integer enabled) {
        jdbcZuulRouteService.updateZuulRouteEnable(id, enabled);
        complexRouteLocator.getProperties().getRoutes().remove(id);
        complexRouteLocator.refresh();
        return "success";
    }

    @GetMapping
    public List<ZuulProperties.ZuulRoute> getRules() {
        List<ZuulProperties.ZuulRoute> zuulRoutes = new ArrayList<>();
        complexRouteLocator.getProperties().getRoutes().forEach((key, val) -> zuulRoutes.add(val));
        return zuulRoutes;
    }

}
