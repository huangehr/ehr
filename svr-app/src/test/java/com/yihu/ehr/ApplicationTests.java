package com.yihu.ehr;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.apps.controller.AppController;
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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrAppApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
public class ApplicationTests {
    String appId = "";
    ApplicationContext applicationContext;

    @Autowired
    private AppController appController;

    //新增一条记录
    @Test
    public void atestSaveApp() throws Exception {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrAppApplication.class).run();
        MApp mapp = new MApp();
        mapp.setName("ehrTest");
        mapp.setCatalog("ChildHealth");
        mapp.setUrl("http://192.168.1.103");
        mapp.setDescription("ehrTest");
        mapp.setCreator("0dae0003561cc415c72d9111e8cb88aa");
        String jsonData = new ObjectMapper().writeValueAsString(mapp);
        mapp = appController.createApp(jsonData);
        appId = ((MApp) mapp).getId();
        assertTrue("查询失败！", appId != null);
    }

    @Test
    public void ctestGetApp() throws Exception {
        String appId = "33nuGNdwGl";
        Object app = appController.getApp(appId);
        assertTrue("查询失败！", app != null);
    }

    @Test
    public void dtestDeleteApp() throws Exception {
        appController.deleteApp("33nuGNdwGl");
        assertTrue("修改失败！", true);
    }

    @Test
    public void dtestUpdateApp() throws Exception {
        MApp app = appController.getApp("33nuGNdwGl");
        app.setName("test");
        String jsonData = new ObjectMapper().writeValueAsString(app);
        Object result = appController.updateApp(jsonData);
        assertTrue("删除失败！", result != null);
    }

    @Test
    public void ftestSearchApps() throws Exception {
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 10;
        int page = 1;
        Object result = appController.getApps(fields,filters,sorts,size,page,null,null);
        assertTrue("操作失败！", result != null);
    }

    @Test
    public void gtestUpdateSatus() throws Exception {
        String appId = "33nuGNdwGl";
        String status = "Approved";
        boolean result = appController.updateSatus(appId, status);
        assertTrue("操作失败！", result == true);
    }

    @Test
    public void htestIsAppExistence() throws Exception {
        String appId = "33nuGNdwGl";
        String secret = "Yhep6NKOOBd2h8rd";
        boolean result = appController.isAppExistence(appId, secret);
        assertTrue("操作失败！", result == true);
    }



}
