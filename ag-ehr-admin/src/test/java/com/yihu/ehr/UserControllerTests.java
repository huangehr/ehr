package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.users.controller.UserController;
import com.yihu.ehr.util.rest.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/2/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTests {

    @Autowired
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    private MultipartHttpServletRequest image;

    private static String version ="v1.0";

    ApplicationContext applicationContext;

    @Test
    public void atestUser() throws Exception{

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        UserDetailModel userModel = new UserDetailModel();
//        userModel.setLoginCode("test_cms");
//        userModel.setRealName("test_cms");
//        userModel.setIdCardNo("111111111111111111");
//        userModel.setEmail("555@qq.com");
//        userModel.setTelephone("11111111111");
        userModel.setGender("Male");
        userModel.setMartialStatus("10");
        userModel.setUserType("Nurse");
        userModel.setOrganization("341321234");
        userModel.setMajor("");

        String userModelJson = objectMapper.writeValueAsString(userModel);
        Envelop envelop = userController.createUser(userModelJson);
        assertTrue("非空验证失败！", !envelop.isSuccessFlg());

        userModel.setLoginCode("test_cms");
        userModel.setRealName("test_cms");
        userModel.setIdCardNo("111111111111111111");
        userModel.setEmail("555@qq.com");
        userModel.setTelephone("11111111111");
        userModelJson = objectMapper.writeValueAsString(userModel);
        envelop = userController.createUser(userModelJson);
        assertTrue("新增失败", envelop.isSuccessFlg());

        userModel = (UserDetailModel)envelop.getObj();

        envelop = userController.createUser(userModelJson);
        assertTrue("账户重复校验失败", !envelop.isSuccessFlg());

        userModel.setLoginCode("test_cms_1");
        userModelJson = objectMapper.writeValueAsString(userModel);
        envelop = userController.createUser(userModelJson);
        assertTrue("身份证号重复校验失败", !envelop.isSuccessFlg());

        userModel.setLoginCode("test_cms");
        Object object1 = userController.distributeKey(userModel.getLoginCode());
        assertTrue("秘钥分发失败", object1!=null);

        String id= userModel.getId();
        envelop = userController.getUser(id);
        assertTrue("用户明细获取失败！",envelop.isSuccessFlg() && envelop.getObj()!=null);

        userModel = (UserDetailModel)envelop.getObj();
        userModel.setRealName("test_cms_1");
        userModelJson = objectMapper.writeValueAsString(userModel);
        envelop = userController.updateUser(userModelJson);
        assertTrue("修改失败", envelop.isSuccessFlg() && ((UserDetailModel)envelop.getObj()).getRealName().equals("test_cms_1"));


        String fields = "";
        String filter = "realName=test_cms_1";
        int page = 1;
        int rows = 15;

        envelop = userController.searchUsers(fields,filter,"",rows,page, null);
        assertTrue("列表获取失败", envelop.isSuccessFlg() && envelop.getDetailModelList()!=null);

        boolean status = true;
        boolean object = userController.activityUser(id,status);
        assertTrue("激活失败", object);

        envelop = userController.loginVerification(userModel.getLoginCode(),"123456");
        assertTrue("登陆失败", envelop.isSuccessFlg() && envelop.getObj()!=null);

        object = userController.resetPass(id);
        assertTrue("密码重置失败", object);


        String bingType ="tel";
        object=userController.unBinding(id,bingType);
        assertTrue("解绑失败："+bingType, object);

        envelop = userController.deleteUser(userModel.getId());
        assertTrue("删除失败", envelop.isSuccessFlg());
    }

    @Test
    public void bTestSearch(){

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        String fields = "";
        String filter = "";
        int page = 1;
        int rows = 15;

        Envelop envelop = userController.searchUsers(fields,filter,"",rows,page, null);
        assertTrue("列表获取失败", envelop.isSuccessFlg() && envelop.getDetailModelList()!=null);

    }

    @Test
    public void cTestBugDate()throws Exception
    {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        Envelop envelop = userController.getUser("0dae0003561cc415c72d9111e8cb88aa");
        assertTrue("数据获取失败", envelop.isSuccessFlg() && envelop.getObj()!=null);


        UserDetailModel userModel = new UserDetailModel();

        userModel.setGender("Male");
        userModel.setMartialStatus("10");
        userModel.setUserType("Nurse");
        userModel.setOrganization("341321234");
        userModel.setMajor("");

        userModel.setLoginCode("test_cms");
        userModel.setRealName("test_cms");
        userModel.setIdCardNo("111111111111111111");
        userModel.setEmail("555@qq.com");
        userModel.setTelephone("11111111111");
        userModel.setCreateDate("2016-03-22 10:31:00");
        userModel.setLastLoginTime("2016-03-22 10:32:00");
        String userModelJson = objectMapper.writeValueAsString(userModel);
        envelop = userController.createUser(userModelJson);
        assertTrue("新增失败", envelop.isSuccessFlg());

    }
}
