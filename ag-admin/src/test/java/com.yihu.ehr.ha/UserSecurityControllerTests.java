package com.yihu.ehr.ha;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.agModel.security.UserSecurityModel;
import com.yihu.ehr.ha.security.controller.SecurityController;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by wq on 2016/2/24.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserSecurityControllerTests {

    private static String apiVersion = "v1.0";

    @Autowired
    private SecurityController securityController;

    ApplicationContext applicationContext;

//    @Autowired
//    private ObjectMapper objectMapper;

    Envelop envelop = new Envelop();

    @Test
    public void atestcreateSecurityByUserId() throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        envelop = securityController.createSecurityByUserId(userId);
        assertNotEquals("用户创建Security失败",envelop,null);

        envelop = securityController.getUserKeyByUserId(((UserSecurityModel)envelop.getObj()).getId());
        assertNotEquals("查询用户Security失败",envelop,null);




    }

}
