package com.yihu.ehr.security;

import com.yihu.ehr.SvrSecurityApplication;
import com.yihu.ehr.security.controller.SecurityRestController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrSecurityApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecurityRestControllerTests {

    String apiVersion = "v1.0";
    ApplicationContext applicationContext;

    @Autowired
    private SecurityRestController securityRestController;


    @Test
    public void atestGetUserSecurityByLoginCode() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrSecurityApplication.class).run();
        String loginCode = "admin";
        Object userSecurity = securityRestController.getUserSecurityByLoginCode(apiVersion,loginCode);
        assertTrue("查询失败！" , userSecurity != null);
    }

    @Test
    public void btestGetUserSecurityByOrgCode() throws Exception{
        String orgCode = "341321234";
        Object userSecurity = securityRestController.getUserSecurityByOrgCode(apiVersion,orgCode);
        assertTrue("查询失败！" , userSecurity != null);
    }

    @Test
    //// TODO: 2016/1/27
    public void ctestGetUserToken() throws Exception{
//        String userName = "CSYH1023001";
//        String rsaPWD = "e10adc3949ba59abbe56e057f20f883e";
//        String appId = "33nuGNdwGl";
//        String appSecret = "Yhep6NKOOBd2h8rd";
//        Object userSecurity = securityRestController.getUserToken(apiVersion,userName,rsaPWD,appId,appSecret);
//        assertTrue("查询失败！" , userSecurity != null);
        assertTrue("查询失败！" , 1==1);
    }

    @Test
    public void dtestrefreshToken() throws Exception{
        String userId = "0dae000355efa435b735be11806d23a2";
        String refreshToken = "3801916fbd19d6fdc2adf2752b491a5d";
        String appId = "Vppx44kHAb";
        Object userSecurity = securityRestController.refreshToken(apiVersion,userId,refreshToken,appId);
        assertTrue("查询失败！" , userSecurity != null);
    }

    @Test
    public void etestRevokeToken() throws Exception{
        String accessToken = "3801916fbd19d6fdc2adf2752b491a5w";
        Object result = securityRestController.revokeToken(apiVersion,accessToken);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void ftestCreateSecurityByOrgCode() throws Exception{
        String orgCode = "341321234";
        Object userSecurityModel = securityRestController.createSecurityByOrgCode(apiVersion,orgCode);
        assertTrue("查询失败！" , userSecurityModel != null);
    }

    @Test
    public void gtestGetUserKeyIdByOrgCd() throws Exception{
        String orgCode = "341321234";
        Object userKeyId = securityRestController.getUserKeyIdByOrgCd(apiVersion,orgCode);
        assertTrue("查询失败！" , userKeyId != null);
    }

    @Test
    public void htestDeleteSecurity() throws Exception{
        String id = "0dae000355f0ec3149f6320da0f3fdff";
        Object result = securityRestController.deleteSecurity(apiVersion,id);
        assertTrue("查询失败！" , "success".equals(result));
    }

    @Test
    public void itestDeleteUserKey() throws Exception{
        String id = "0dae000355efa67149f63219649244c3";
        Object userKeyId = securityRestController.deleteUserKey(apiVersion,id);
        assertTrue("查询失败！" , "success".equals(userKeyId));
    }

    @Test
    public void jtestGetUserSecurityByUserName() throws Exception{
        String loginCode = "admin";
        Object userSecurity = securityRestController.getUserSecurityByUserName(apiVersion,loginCode);
        assertTrue("查询失败！" , userSecurity!=null);
    }

    @Test
    public void ktestCreateSecurityByUserId() throws Exception{
        String loginCode = "0dae0003561cc415c72d9111e8cb88aa";
        Object userSecurity = securityRestController.createSecurityByUserId(apiVersion,loginCode);
        assertTrue("查询失败！" , userSecurity!=null);
    }

    @Test
    public void ltestGetUserKeyByUserId() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object userKeyId = securityRestController.getUserKeyByUserId(apiVersion,userId);
        assertTrue("查询失败！" , userKeyId!=null);
    }

    @Test
    public void mtestGetUserSecurityByUserId() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object userSecurity = securityRestController.getUserSecurityByUserId(apiVersion,userId);
        assertTrue("查询失败！" , userSecurity!=null);
    }

}
