package com.yihu.ehr.user;

import com.yihu.ehr.UserServiceApp;
import com.yihu.ehr.user.user.controller.UserController;
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
@SpringApplicationConfiguration(classes = UserServiceApp.class)
public class userControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void getUser()
    {
        String version = "v1.0";
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object object = userController.getUser(version, userId);
        assertTrue("查询失败！" , object != null);
    }
}
