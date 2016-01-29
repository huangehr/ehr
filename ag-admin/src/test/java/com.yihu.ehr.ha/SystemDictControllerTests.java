package com.yihu.ehr.ha;

import com.yihu.ehr.ha.SystemDict.controller.ConventionalDictEntryController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotEquals;


/**
 * Created by AndyCai on 2016/1/19.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@WebAppConfiguration
@EnableDiscoveryClient
@EnableFeignClients
public class SystemDictControllerTests {

    @Autowired
    private ConventionalDictEntryController systemDictController;

    @Test
    public void getOrgType() {
        try {
            Object baseDict = systemDictController.getOrgType("Govement");
            assertNotEquals("机构类别字典获取失败", baseDict, null);
        }
        catch (Exception ex){}
    }

    @Test
    public void getSettledWay() {
        try {
            Object baseDict = systemDictController.getSettledWay("Direct");
            assertNotEquals("结算方式字典获取失败", baseDict, null);
        }
        catch (Exception ex){}
    }

    @Test
    public void getAppCatalog() {
        try {
            Object baseDict = systemDictController.getAppCatalog("ChildHealth");
            assertNotEquals("APP类别字典获取失败", baseDict, null);
        }
        catch (Exception ex){}
    }

    @Test
    public void getAppStatus() {
        try {
            Object baseDict = systemDictController.getAppStatus("Approved");
            assertNotEquals("APP状态字典获取失败", baseDict, null);
        }
        catch (Exception ex){}
    }
}
