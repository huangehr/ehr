package com.yihu.ehr.resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.01 10:02
 */
@RestController
@RequestMapping("/")
public class ResourceController {

    @RequestMapping("")
    public String home() {
        return "Hello World from OAuth2 app.";
    }
}
