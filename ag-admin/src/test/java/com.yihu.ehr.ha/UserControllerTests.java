package com.yihu.ehr.ha;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.ha.users.controller.UserController;
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

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/2/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTests {

    @Autowired
    private static UserController userController;

    @Autowired
    private static ObjectMapper objectMapper;

    private static String version ="v1.0";

    ApplicationContext applicationContext;

    @Test
    public void atestUser() throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        UserDetailModel userModel = new UserDetailModel();
        userModel.setLoginCode("test_cms");
        userModel.setRealName("test_cms");
        userModel.setIdCardNo("111111111111111111");
        userModel.setGender("Male");
        userModel.setMartialStatus("10");
        userModel.setEmail("555@qq.com");
        userModel.setTelephone("11111111111");
        userModel.setUserType("Nurse");
        userModel.setOrganization("341321234");
        userModel.setMajor("");

        String userModelJson = objectMapper.writeValueAsString(userModel);
        Envelop envelop = userController.createUser(userModelJson);
        assertTrue("新增失败", !envelop.isSuccessFlg());

        userModel = (UserDetailModel)envelop.getObj();
        String id= userModel.getId();
        envelop = userController.getUser(id);
        assertTrue("用户明细获取失败！",!envelop.isSuccessFlg() || envelop.getObj()==null);

        userModel = (UserDetailModel)envelop.getObj();
        userModel.setRealName("test_cms_1");
        userModelJson = objectMapper.writeValueAsString(userModel);
        envelop = userController.updateUser(userModelJson);
        assertTrue("修改失败", !envelop.isSuccessFlg() || !((UserDetailModel)envelop.getObj()).getRealName().equals("test_cms_1"));

        String fields = "";
        String filter = "realName=test_cms_1";
        int page = 1;
        int rows = 15;

        envelop = userController.searchUsers(fields,filter,"",rows,page,null);
        assertTrue("列表获取失败", !envelop.isSuccessFlg() || envelop.getDetailModelList()==null);

        boolean status = true;
        boolean object = userController.activityUser(id,status);
        assertTrue("激活失败", !object);

        object = userController.resetPass(id);
        assertTrue("密码重置失败", !object);


        String bingType ="tel";
        object=userController.unBinding(id,bingType);
        assertTrue("解绑失败："+bingType, !object);

        Object object1 = userController.distributeKey(userModel.getLoginCode());
        assertTrue("秘钥分发失败", object1==null);

         envelop = userController.loginVerification(userModel.getLoginCode(), userModel.getPassword());
        assertTrue("登陆失败", !envelop.isSuccessFlg() || envelop.getObj()==null);

    }
}
