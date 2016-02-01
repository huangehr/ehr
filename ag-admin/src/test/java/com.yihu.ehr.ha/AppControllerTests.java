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

    MApp mApp = null;

    ApplicationContext applicationContext;

    @Autowired
    private AppController appController;


    @Test
    public void atestCreateApp() {

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();
        //新增测试
        Object object = appController.createApp(version, "测试APP", "ChildHealth", "fsdadfs", "这是用于测试的数据", "1", "0dae0003561cc415c72d9111e8cb88aa");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String aaa = objectMapper.writeValueAsString(object);
            mApp = objectMapper.readValue(aaa, MApp.class);

        } catch (Exception ex) {
        }
        assertNotEquals("APP新增失败", mApp, null);

    }

    @Test
    public void btestgetAppList() {

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();
        String appId = "";
        String appName = "";
        String catalog = "";
        String status = "";
        int page = 1;
        int rows = 15;
        Object object = appController.getAppList(version, appId, appName, catalog, status, page, rows);
        assertNotEquals("机构类别字典获取失败", object, null);
    }

    @Test
    public void ctestUpdateApp() {
        //修改测试
        String id = mApp.getId();
        String name = mApp.getName();
        String secret = mApp.getSecret();
        String url = mApp.getUrl();
        String catalog = mApp.getCatalog();
        String status = mApp.getStatus();
        String description = mApp.getDescription();
        String tags = mApp.getTags();
        Object object = appController.updateApp(version, id, name, catalog, status, url, description, tags);
        //success
        assertTrue("APP修改失败", !object.toString().equals("true"));
    }

    @Test
    public void dtestGetAppDetailById()
    {

        String id = mApp.getId();
        //获取明细
        Object object = appController.getAppDetail(version, id);
        assertNotEquals("APP明细获取失败", object, null);

    }

    @Test
    public void etestUpdateStatus(){

        String id=mApp.getId();
        //修改状态
        Object object = appController.checkStatus(version, id, "WaitingForApprove");
        assertTrue("APP状态修改失败", !object.toString().equals("success"));
    }

    @Test
    public void ftestDeleteAppById(){
        String id = mApp.getId();
        //根据查询条件获取列表
        Object object = appController.deleteApp(version, id);
        assertTrue("APP删除失败", !object.toString().equals("success"));
    }
}
