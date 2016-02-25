package com.yihu.ehr.ha;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.agModel.security.UserSecurityModel;
import com.yihu.ehr.ha.security.controller.SecurityController;
import com.yihu.ehr.model.security.MUserSecurity;
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

    private static String userId = "0dae0003568ceaf286faa653cc70399f";
    private static String orgCode = "ceshi3";
    private static String loginCode = "ceshi3";


    @Autowired
    private SecurityController securityController;

    ApplicationContext applicationContext;

    Envelop envelop = new Envelop();

    @Test
    public void atestSecurity() throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        //用户Security

        envelop = securityController.createSecurityByUserId(userId);
        assertNotEquals("用户创建Security失败",envelop,null);

        envelop = securityController.getUserSecurityByUserId(userId);
        assertNotEquals("查询用户Security失败",envelop,null);

        String userKeyId = securityController.getUserKeyByUserId(userId);
        assertNotEquals("获取userkey失败",userKeyId,null);

        envelop = securityController.deleteSecurity(((MUserSecurity) envelop.getObj()).getId());
        assertNotEquals("删除用户Security失败",envelop,null);

        envelop = securityController.deleteUserKey(userKeyId);
        assertNotEquals("删除userKey失败",envelop,null);


        //机构Security

        envelop = securityController.createSecurityByOrgCode(orgCode);
        assertNotEquals("机构创建Security失败",envelop,null);

        String orgKeyId = securityController.getUserKeyIdByOrgCd(orgCode);
        assertNotEquals("获取userkey失败",orgKeyId,null);

        envelop = securityController.deleteSecurity(((MUserSecurity) envelop.getObj()).getId());
        assertNotEquals("删除机构Security失败",envelop,null);

        envelop = securityController.deleteUserKey(orgKeyId);
        assertNotEquals("删除userKey失败",envelop,null);

    }


    @Test
    public void ctestgetUserSecurity() throws Exception{

        envelop = securityController.getUserSecurityByLoginCode(loginCode);
        assertNotEquals("获取用户公钥失败",envelop,null);

        envelop = securityController.getUserSecurityByOrgCode(orgCode);
        assertNotEquals("获取企业公钥失败",envelop,null);

    }

}
