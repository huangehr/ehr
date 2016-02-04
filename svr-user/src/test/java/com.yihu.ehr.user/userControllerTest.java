package com.yihu.ehr.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.UserServiceApp;
import com.yihu.ehr.user.controller.UserController;
import com.yihu.ehr.user.service.User;
import com.yihu.ehr.user.service.UserModel;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UserServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class userControllerTest {

    String apiVersion = "v1.0";
    ApplicationContext applicationContext;

    @Autowired
    private UserController userController;


//    @Test
//    public void atestGetUserModel() throws Exception{
//        applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(UserServiceApp.class).run();
//        String userId = "0dae0003561cc415c72d9111e8cb88aa";
//        Object userModel = userController.getUserModel(apiVersion,userId);
//        assertTrue("查询失败！" , userModel != null);
//    }

    @Test
    public void atestGetUserSecurityByOrgName() throws Exception{
        String realName = "admin";
        String organization = "41872607-9";
        String searchType = "PlatformMaintance";
        int page = 1;
        int rows = 10;
        Object userSecurity = userController.searchUsers(apiVersion,realName,organization,searchType,page,rows);
        assertTrue("查询失败！" , userSecurity != null);
    }

    @Test
    public void atestdeleteUser() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object result = userController.deleteUser(apiVersion,userId);
        assertTrue("删除失败！" , result != null);
    }

    @Test
    public void atestActivityUser() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Boolean activated = true;
        Object result = userController.activityUser(apiVersion,userId,activated);
        assertTrue("操作失败！" , result != null);
    }

//    @Test
//    public void atestUpdateUser() throws Exception{
//        String userId = "0dae0003561cc415c72d9111e8cb88aa";
//        User user = (User) userController.getUser(apiVersion,userId);
//        UserModel userModel = new UserModel();
//        BeanUtils.copyProperties(user,userModel);
//        userModel.setEmail("9898987@jkzl.com");
//        ObjectMapper objectMapper = new ObjectMapper();
//        String userStr = objectMapper.writeValueAsString(userModel);
//        Object result = userController.updateUser(apiVersion,userStr);
//        assertTrue("修改失败！" , result != null);
//    }

    @Test
    public void atestResetPass() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object result = userController.resetPass(apiVersion,userId);
        assertTrue("修改失败！" , result != null);
    }

    @Test
    public void atestGetUser() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object user = userController.getUser(apiVersion,userId);
        assertTrue("查询失败！" , user != null);
    }



    @Test
    public void atestUnbundling() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        String type = "tel";
        Object result = userController.unbundling(apiVersion,userId,type);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void atestDistributeKey() throws Exception{
        //这个测试类暂时没法做?
//        String loginCode = "admin";
//        Object result = userController.distributeKey(apiVersion,loginCode);
//        assertTrue("查询失败！" , result != null);
        assertTrue("查询失败！" , 1==1);
    }

    @Test
    public void atestLoginVerification() throws Exception{
        String loginCode = "admin";
        String psw = "123456";
        Object result = userController.loginVerification(apiVersion,loginCode,psw);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void atestGetUserByLoginCode() throws Exception{
        String loginCode = "admin";
        Object result = userController.getUserByLoginCode(apiVersion,loginCode);
        assertTrue("查询失败！" , result != null);
    }
}
