package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.app.AppModel;
import com.yihu.ehr.ha.apps.controller.AppController;
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

import java.util.List;

import static org.junit.Assert.assertNotEquals;
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

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        Object object = new Object();
        Envelop envelop = new Envelop();
        //列表查询（size、page）---------1 ok

//        String fields = "id,name,status,catalog,url,description";
//        String filter = null;
//        String sorts = "create_time";
//        int page = 2;
//        int size = 15;
//        envelop = appController.getApps(fields, filter, sorts, size, page);
//        assertTrue("app列表获取失败", envelop.isSuccessFlg());

        //创建app----------2

//        String appJsonCreate = "{\"name\":\"wwcs\",\"url\":\"www.baidu.com\",\"catalog\":\"ChildHealth\",\"description\":\"firstTest\",\"creator\":\"0dae0003561cc415c72d9111e8cb88aa\"}";
//        ObjectMapper objectMapper = new ObjectMapper();
//        MApp mApp = objectMapper.readValue(appJsonCreate,MApp.class);
//        String appJsonUpdated = objectMapper.writeValueAsString(mApp);
//        envelop = appController.createApp(appJsonUpdated);
//        assertTrue("app列表创建失败！", envelop.isSuccessFlg());


        //根据id获取app------------3 ok

//        String appId = "33nuGNdwGl";
//        envelop = appController.getApp(appId);
//        assertTrue("app获取失败！", envelop.isSuccessFlg());


        //更新app-----------------4

//        String appJsonUpdate = "{\"id\":\"mDGeumvkF5\",\"name\":\"wwcs111\",\"secret\":\"clKGlGwcV3fEUg7h\",\"catalog\":\"ChildHealth\",\"status\":\"WaitingForApprove\",\"url\":\"www.baidu.com\",\"creator\":\"0dae0003561cc415c72d9111e8cb88aa\"}";
//        ObjectMapper objectMapper = new ObjectMapper();
//        MApp app = objectMapper.readValue(appJsonUpdate,MApp.class);
//        String appp = objectMapper.writeValueAsString(app);
//        envelop = appController.updateApp(appp);
//        assertTrue("app更新失败！", envelop.isSuccessFlg());


        //更新app状态-------------------5 ok

//        Boolean flag = appController.updateStatus("QzHmvMDFVd","Approved");
//        assertTrue("app状态更新失败！", flag);


        //判断app是否存在（id、secret）-----------6 ok

//        Boolean flag2 = appController.isAppExistence("QzHmvMDFVd","XEYbrGmWWvIl5xFG");
//        assertTrue("app不存在！", flag2);


        //判断指定name的app是否存在-----------7 ok

//        Boolean flag3 = appController.isAppNameExists("测试应用1112001");
//        assertTrue("该名称已存在！", !flag3);

        //删除刚创建的app

//        envelop = appController.getApps("","name=wwcs",null,1,1);
//        String appId ="mDGeumvkF5";
//        for (MApp app:(List<MApp>)envelop.getDetailModelList()){
//            appId = app.getId();
//        }
//        envelop = appController.deleteApp(appId);
//        assertTrue("app删除失败！", envelop.isSuccessFlg());

//            mApp = new MApp();
//            mApp.setName("测试APP");
//            mApp.setSecret("");
//            mApp.setUrl("dfadfasf");
//            mApp.setCatalog("ChildHealth");
//
//            mApp.setDescription("这是用于测试的数据");
//            mApp.setCreator("0dae0003561cc415c72d9111e8cb88aa");
//            String tags = "1";
//            //新增测试
//            object = appController.createApp(objectMapper.writeValueAsString(mApp));
//            assertNotEquals("APP新增失败", mApp, null);
           // String tags = "";//mApp.getTags().toString();

//            mApp.setName("测试APP1");
//            mApp = appController.updateApp(objectMapper.writeValueAsString(mApp));
//            assertTrue("APP修改失败", mApp.getName().equals("测试APP1"));
//
//            mApp = appController.getApp(mApp.getId());
//            assertNotEquals("APP明细获取失败", mApp, null);

//        object = appController.checkStatus(version, id, "WaitingForApprove");
//        assertTrue("APP状态修改失败", object.toString().equals("true"));

//            Object object = appController.deleteApp(mApp.getId());
//            assertTrue("APP删除失败", object.toString().equals("true"));

    }

}
