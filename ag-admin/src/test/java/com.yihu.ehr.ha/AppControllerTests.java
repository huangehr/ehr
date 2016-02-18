package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.apps.controller.AppController;
import com.yihu.ehr.model.app.MApp;
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

            mApp = new MApp();
            mApp.setName("测试APP");
            mApp.setSecret("");
            mApp.setUrl("dfadfasf");
            mApp.setCatalog("ChildHealth");

            mApp.setDescription("这是用于测试的数据");
            mApp.setCreator("0dae0003561cc415c72d9111e8cb88aa");
            String tags = "1";
            //新增测试
            mApp = appController.createApp(objectMapper.writeValueAsString(mApp));

            assertNotEquals("APP新增失败", mApp, null);

            String fields = "id,code,name,status,catalog,url,description,tags";
            String filter = "";
            String sorts = "code";
            int page = 1;
            int rows = 15;
            List<MApp> mApps = appController.getApps(fields, "", sorts, page, rows);
            assertNotEquals("机构列表获取失败", mApps, null);


           // String tags = "";//mApp.getTags().toString();

            mApp.setName("测试APP1");
            mApp = appController.updateApp(objectMapper.writeValueAsString(mApp));
            assertTrue("APP修改失败", mApp.getName().equals("测试APP1"));

            mApp = appController.getApp(mApp.getId());
            assertNotEquals("APP明细获取失败", mApp, null);

//        object = appController.checkStatus(version, id, "WaitingForApprove");
//        assertTrue("APP状态修改失败", object.toString().equals("true"));

            Object object = appController.deleteApp(mApp.getId());
            assertTrue("APP删除失败", object.toString().equals("true"));

    }

}
