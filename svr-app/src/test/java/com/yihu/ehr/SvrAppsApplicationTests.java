package com.yihu.ehr;

import com.yihu.ehr.apps.controller.AppController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrAppApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvrAppsApplicationTests {
    String appId = "";
    ApplicationContext applicationContext;

    //@Autowired
    private AppController appController;

    //新增一条记录
    @Test
    public void atestSaveApp() throws Exception {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrAppApplication.class).run();
        String name = "ehrTest";
        String catalog = "ChildHealth";
        String url = "103";
        String description = "ehrTest";
        String tags = "app";
        String userId = "0dae0003561cc415c72d9111e8cb88aa";  //admin
//        Object app = appController.createApp(name, catalog, url, description, tags, userId);
//        appId = ((App) app).getId();
//        assertTrue("查询失败！", appId != null);
    }

    @Test
    public void btestGetAppList() throws Exception {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrAppApplication.class).run();
        String appId = "";
        String appName = "";
        String catalog = "";
        String status = "";
        int page = 1;
        int rows = 10;
        //Object appList = appController.getApps(appId, appName, catalog, status, page, rows);
        //assertTrue("查询失败！", appList != null);
    }


    @Test
    public void ctestGetApp() throws Exception {
        String appId = "yYkKALLwUO";
        Object app = appController.getApp(appId);
        assertTrue("查询失败！", app != null);
    }


    @Test
    public void dtestGetAppDetail() throws Exception {
        String appId = "yYkKALLwUO";
        Object appDetail = appController.getApp(appId);
        assertTrue("查询失败！", appDetail != null);
    }


    @Test
    public void etestCheck() throws Exception {
        String appId = "yYkKALLwUO";
        String status = "Approved";
        //Object result = appController.updateApp(appId, status);
        //assertTrue("faild", "success".equals(result));
    }

    @Test
    public void gtestUpdateApp() throws Exception {
        String appId = "yYkKALLwUO";
        String name = "健康档案浏览器";
        String catalog = "ChildHealth";
        String status = "Forbidden";
        String url = "104";
        String description = "yYkKALLwUO";
        String tags = "appss";
//        Object result = appController.updateApp(appId, name, catalog, status, url, description, tags);
//        assertTrue("修改失败！", "success".equals(result));
    }


    @Test
    public void htestDeleteApp() throws Exception {
        String appId = "yYkKALLwUO";
        appController.deleteApp(appId);
    }
}
