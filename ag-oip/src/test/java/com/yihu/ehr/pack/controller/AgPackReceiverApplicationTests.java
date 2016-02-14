package com.yihu.ehr.pack.controller;

import com.yihu.ehr.pack.PackReceiverGatewayApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PackReceiverGatewayApp.class)
@WebAppConfiguration
public class AgPackReceiverApplicationTests {

    @Test
    public void contextLoads() {
    }

}
