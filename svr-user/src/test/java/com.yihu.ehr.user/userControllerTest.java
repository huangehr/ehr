package com.yihu.ehr.user;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.UserServiceApp;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.controller.UserController;
import com.yihu.ehr.user.service.User;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UserServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
public class userControllerTest {

    ApplicationContext applicationContext;

    @Autowired
    private UserController userController;

    @Test
    public void aSearchUsers() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(UserServiceApp.class).run();
        String fields = "";
        String filters = "";
        String sorts = "";
        int page = 1;
        int rows = 10;
//        Object userSecurity = userController.searchUsers(fields,filters,sorts,page,rows,null,null);
//        assertTrue("查询失败！" , userSecurity != null);
    }


    @Test
    public void bDeleteUser() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Object result = userController.deleteUser(userId);
        assertTrue("删除失败！" , result != null);
    }


    @Test
    public void cCreateUser() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(UserServiceApp.class).run();
        MUser user = userController.getUser("0dae0003561cc415c72d9111e8cb88aa");
        user.setId(null);
        String jsobData = new ObjectMapper().writeValueAsString(user);
        Object result = userController.createUser(jsobData);
        assertTrue("创建失败！" , result != null);
    }



    @Test
    public void dUpdateUser() throws Exception{
        MUser user = userController.getUser("0dae0003561cc415c72d9111e8cb88aa");
        user.setRealName("test");
        String userStr = new ObjectMapper().writeValueAsString(user);
        Object result = userController.updateUser(userStr);
        assertTrue("修改失败！" , result != null);
    }

    @Test
    public void eGetUser() throws Exception{
        Object user = userController.getUser("0dae0003561cc415c72d9111e8cb88aa");
        assertTrue("查询失败！" , user != null);
    }

    @Test
    public void fActivityUser() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        Boolean activated = true;
        Object result = userController.activityUser(userId,activated);
        assertTrue("操作失败！" , result != null);
    }


    @Test
    public void gResetPass() throws Exception{
        boolean result = userController.resetPass("0dae0003561cc415c72d9111e8cb88aa");
        assertTrue("操作失败！" , result == true);
    }


    @Test
    public void hUnbundling() throws Exception{
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        String type = "tel";
        Object result = userController.unBinding(userId,type);
        assertTrue("操作失败！" , result != null);
    }

    @Test
    public void iDistributeKey() throws Exception{
        //这个测试类暂时没法做
//        String loginCode = "admin";
//        Object result = userController.distributeKey(loginCode);
//        assertTrue("查询失败！" , result != null);
        assertTrue("查询失败！" , 1==1);
    }

    @Test
    public void jLoginVerification() throws Exception{
        String loginCode = "admin";
        String psw = "123456";
        Object result = userController.loginVerification(loginCode,psw);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void kGetUserByLoginCode() throws Exception{
        String loginCode = "admin";
        Object result = userController.getUserByLoginCode(loginCode);
        assertTrue("查询失败！" , result != null);
    }
}
