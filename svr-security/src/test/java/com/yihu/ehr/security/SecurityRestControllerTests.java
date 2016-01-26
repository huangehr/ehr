package com.yihu.ehr.security;

import com.yihu.ehr.SvrSecurityApplication;
import com.yihu.ehr.security.controller.SecurityRestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/1/25.
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrSecurityApplication.class)
public class SecurityRestControllerTests {

    @Autowired
    private SecurityRestController security;

    @Test
    public void getUserSecurityByUserId()
    {
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object object = security.getUserSecurityByUserId("v1.0", userId);

        assertTrue("安全信息获取失败",object!=null);
    }
}
