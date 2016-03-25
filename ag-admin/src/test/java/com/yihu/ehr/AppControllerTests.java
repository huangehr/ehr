package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.app.AppDetailModel;
import com.yihu.ehr.apps.controller.AppController;
import com.yihu.ehr.model.app.MApp;
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
 * Created by AndyCai on 2016/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppControllerTests {

    private static String version = "v1.0";

    public MApp mApp = null;

    ApplicationContext applicationContext;

    @Autowired
    private AppController appController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void atestCreateApp() throws Exception{

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        Envelop envelop = new Envelop();
        //列表查询（size、page）---------1 ok

        String fields = "";
        String filter = "";
        String sorts = "";
        int page = 1;
        int size = 15;
        envelop = appController.getApps(fields, filter, sorts, size, page);
        assertTrue("app列表获取失败", envelop.isSuccessFlg());

        //创建app----------2

        String appJsonCreate = "{\"name\":\"wwcs\",\"url\":\"www.baidu.com\",\"catalog\":\"ChildHealth\",\"description\":\"firstTest\",\"creator\":\"0dae0003561cc415c72d9111e8cb88aa\"}";
        envelop = appController.createApp(appJsonCreate);
        assertTrue("app列表创建失败！", envelop.isSuccessFlg());
        AppDetailModel appDetailModel = (AppDetailModel)envelop.getObj();
        String appIdForTest = appDetailModel.getId();
        String appSecretForTest = appDetailModel.getSecret();
        String appCreateTime = appDetailModel.getCreateTime();

        //根据id获取app------------3 ok

        envelop = appController.getApp(appIdForTest);
        assertTrue("app获取失败！", envelop.isSuccessFlg());


        //更新app-----------------4

        String appJsonUpdate = "{\"id\":\""+appIdForTest+"\",\"name\":\"wwcs111\",\"secret\":\""+appSecretForTest+"\",\"catalog\":\"ChildHealth\",\"status\":\"WaitingForApprove\",\"url\":\"www.baidu.com\",\"creator\":\"0dae0003561cc415c72d9111e8cb88aa\"}";
        AppDetailModel app = objectMapper.readValue(appJsonUpdate,AppDetailModel.class);
        app.setCreateTime(appCreateTime);
        String appp = objectMapper.writeValueAsString(app);
        envelop = appController.updateApp(appp);
        assertTrue("app更新失败！", envelop.isSuccessFlg());


        //更新app状态-------------------5 ok

        Boolean flag = appController.updateStatus(appIdForTest,"Approved");
        assertTrue("app状态更新失败！", flag);


        //判断app是否存在（id、secret）-----------6 ok

        Boolean flag2 = appController.isAppExistence(appIdForTest,appSecretForTest);
        assertTrue("app不存在！", flag2);

        //判断指定name的app是否存在-----------7 ok

        Boolean flag3 = appController.isAppNameExists("wwcs111");
        assertTrue("该名称已存在！", flag3);

        //删除刚创建的app--------------8

        envelop = appController.deleteApp(appIdForTest);
        assertTrue("app删除失败！", envelop.isSuccessFlg());


    }

}
