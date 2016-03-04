//package com.yihu.ehr.security;
//
//import com.yihu.ehr.SvrSecurityApplication;
//import com.yihu.ehr.security.controller.SecurityRestController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by AndyCai on 2016/1/25.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SvrSecurityApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class SecurityRestControllerTests {
//
//    ApplicationContext applicationContext;
//
//    @Autowired
//    private SecurityRestController securityRestController;
//
//
//    @Test
//    public void atestGetUserSecurityByLoginCode() throws Exception{
//        applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(SvrSecurityApplication.class).run();
//        String loginCode = "admin";
//        Object userSecurity = securityRestController.getUserSecurityByLoginCode(loginCode);
//        assertTrue("查询失败！" , userSecurity != null);
//    }
//
//    @Test
//    public void btestGetUserSecurityByOrgCode() throws Exception{
//        String orgCode = "341321234";
//        Object userSecurity = securityRestController.getUserSecurityByOrgCode(orgCode);
//        assertTrue("查询失败！" , userSecurity != null);
//    }
//
//
//    @Test
//    public void ftestCreateSecurityByOrgCode() throws Exception{
//        String orgCode = "341321234";
//        Object userSecurityModel = securityRestController.createSecurityByOrgCode(orgCode);
//        assertTrue("查询失败！" , userSecurityModel != null);
//    }
//
//    @Test
//    public void gtestGetUserKeyIdByOrgCd() throws Exception{
//        String orgCode = "341321234";
//        Object userKeyId = securityRestController.getUserKeyIdByOrgCd(orgCode);
//        assertTrue("查询失败！" , userKeyId != null);
//    }
//
//    @Test
//    public void htestDeleteSecurity() throws Exception{
//        String id = "0dae000355f0ec3149f6320da0f3fdff";
//        boolean result = securityRestController.deleteSecurity(id);
//        assertTrue("删除失败！" , result==true);
//    }
//
//    @Test
//    public void itestDeleteUserKey() throws Exception{
//        String id = "0dae000355efa67149f63219649244c3";
//        boolean result = securityRestController.deleteUserKey(id);
//        assertTrue("删除失败！" , result==true);
//    }
//
//
//    @Test
//    public void ktestCreateSecurityByUserId() throws Exception{
//        String loginCode = "0dae0003561cc415c72d9111e8cb88aa";
//        Object userSecurity = securityRestController.createSecurityByUserId(loginCode);
//        assertTrue("查询失败！" , userSecurity!=null);
//    }
//
//    @Test
//    public void ltestGetUserKeyByUserId() throws Exception{
//        String userId = "0dae0003561cc415c72d9111e8cb88aa";
//        Object userKeyId = securityRestController.getUserKeyByUserId(userId);
//        assertTrue("查询失败！" , userKeyId!=null);
//    }
//
//    @Test
//    public void mtestGetUserSecurityByUserId() throws Exception{
//        String userId = "0dae0003561cc415c72d9111e8cb88aa";
//        Object userSecurity = securityRestController.getUserSecurityByUserId(userId);
//        assertTrue("查询失败！" , userSecurity!=null);
//    }
//
//}
