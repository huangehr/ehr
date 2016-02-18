package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.SystemDict.controller.ConventionalDictEntryController;
import com.yihu.ehr.ha.SystemDict.controller.SystemDictController;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.Envelop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
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
public class SystemDictControllerTests {

    @Autowired
    private ConventionalDictEntryController systemDictController;

    @Autowired
    private SystemDictController sysDict;

    @Autowired
    private ObjectMapper objectMapper;

    private String version = "v1.0";

    ApplicationContext applicationContext;

    @Test
    public void atestSystemDict() throws Exception{

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        MSystemDict systemDict = new MSystemDict();
        systemDict.setName("test_dict_cms");
        systemDict.setReference("");
        systemDict.setAuthorId("0dae0003567a0d909b10c56a3220efdf");
        systemDict =sysDict.createDictionary(objectMapper.writeValueAsString(systemDict));
        assertNotEquals("字典新增失败", systemDict, null);

        String fields = "id,name,phonetic_code,reference,author,create_date";
        String filter = "name=test_dict_cms";
        String sorts = "name";
        int page = 1;
        int rows = 15;

        Envelop object = sysDict.getDictionaries(fields, filter, sorts,page,rows);
        assertNotEquals("字典列表获取失败", object, null);

        long dictId = systemDict.getId();
        String name = "test_dict_cms_c";

        systemDict = sysDict.updateDictionary(dictId, name);
        assertTrue("字典修改失败", systemDict.getName().equals("test_dict_cms_c"));

        MConventionalDict conventionalDict = new MConventionalDict();
        conventionalDict.setCode( "test_item_cms");
        conventionalDict.setValue( "test_value_cms");
        conventionalDict.setSort(1);
        conventionalDict.setCatalog("");
        conventionalDict = sysDict.createDictEntry(objectMapper.writeValueAsString(conventionalDict));
        assertNotEquals("字典项新增失败", conventionalDict, null);

        conventionalDict.setValue("test_value_cms_c");
        conventionalDict = sysDict.updateDictEntry(objectMapper.writeValueAsString(conventionalDict));
        assertTrue("字典项修改失败", conventionalDict.getValue().equals("test_value_cms_c"));


        object = sysDict.getDictEntries(systemDict.getId(),"", 1, 15);
        assertNotEquals("字典项列表获取失败", object, null);


        Object object1 = sysDict.deleteDictEntry(systemDict.getId(),conventionalDict.getCode());
        assertNotEquals("字典项删除失败", object1.toString(), "true");

        object1 = sysDict.deleteDictionary(systemDict.getId());
        assertNotEquals("字典删除失败", object1, "true");
    }

    @Test
    public void getOrgType() throws Exception {

        Object baseDict = systemDictController.getOrgType("Govement");
        assertNotEquals("机构类别字典获取失败", baseDict, null);

    }

    @Test
    public void getSettledWay() throws Exception {

        Object baseDict = systemDictController.getSettledWay("Direct");
        assertNotEquals("结算方式字典获取失败", baseDict, null);

    }

    @Test
    public void getAppCatalog() throws Exception {

        Object baseDict = systemDictController.getAppCatalog("ChildHealth");
        assertNotEquals("APP类别字典获取失败", baseDict, null);

    }

    @Test
    public void getAppStatus() throws Exception {

        Object baseDict = systemDictController.getAppStatus("Approved");
        assertNotEquals("APP状态字典获取失败", baseDict, null);

    }
}
