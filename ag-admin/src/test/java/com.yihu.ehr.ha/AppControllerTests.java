package com.yihu.ehr.ha;

import com.yihu.ehr.ha.apps.controller.AppController;
import com.yihu.ehr.model.app.MApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@WebAppConfiguration
@EnableDiscoveryClient
@EnableFeignClients
public class AppControllerTests {
    @Autowired
    private AppController appController;

    @Test
    public void getAppList() {
        Object object = appController.getAppList(1, 1, 1, 1, 5);
        assertNotEquals("机构类别字典获取失败", object, null);
    }

    @Test
    public void atestCreateApp() {
        //新增测试
        Object object = appController.createApp("测试APP", "ChildHealth", "www.baidu.com", "这是用于测试的数据", "1", "0dae0003561cc415c72d9111e8cb88aa");
        assertNotEquals("APP新增失败", object, null);

        MApp mApp = (MApp) object;
        //修改测试
        String id = mApp.getId();
        String name = mApp.getName();
        String secret = mApp.getSecret();
        String url = mApp.getUrl();
        String catalog = mApp.getCatalog();
        String status = mApp.getStatus();
        String description = mApp.getDescription();
        String tags = mApp.getTags();
        object = appController.updateApp(id, name, catalog, status, url, description, tags);
        //success
        assertTrue("APP修改失败", !object.toString().equals("success"));

        //获取明细
        object = appController.getAppDetail(id);
        assertNotEquals("APP明细获取失败", object, null);

        //修改状态
        object = appController.check(id,"WaitingForApprove");
        assertTrue("APP状态修改失败", !object.toString().equals("success"));

        //根据查询条件获取列表
       // object = appController.getAppList(1, "ChildHealth", "WaitingForApprove", 1, 20);
        object = appController.deleteApp(id);
        assertTrue("APP删除失败", !object.toString().equals("success"));


    }

    public void btestUpdateApp() {
        //Object
    }
}
