package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.SystemDict.controller.ConventionalDictEntryController;
import com.yihu.ehr.ha.SystemDict.controller.SystemDictController;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
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

        systemDict = sysDict.getDictionary(systemDict.getId());
        assertNotEquals("字典明细获取失败", systemDict, null);

        String fields = "";//"id,name,phonetic_code,reference,author,create_date";
        String filter = "name=test_dict_cms";
        String sorts = "";
        int page = 1;
        int rows = 15;

        List<MSystemDict> dicts = sysDict.getDictionaries(fields, filter, sorts,rows,page);
        assertNotEquals("字典列表获取失败", dicts, null);

        systemDict.setName("test_dict_cms_c");

        systemDict = sysDict.updateDictionary(objectMapper.writeValueAsString(systemDict));
        assertTrue("字典修改失败", systemDict.getName().equals("test_dict_cms_c"));

        MConventionalDict conventionalDict = new MConventionalDict();
        conventionalDict.setDictId(systemDict.getId());
        conventionalDict.setCode( "test_item_cms");
        conventionalDict.setValue( "test_value_cms");
        conventionalDict.setSort(1);
        conventionalDict.setCatalog("");
        conventionalDict = sysDict.createDictEntry(objectMapper.writeValueAsString(conventionalDict));
        assertNotEquals("字典项新增失败", conventionalDict, null);

        conventionalDict.setValue("test_value_cms_c");
        conventionalDict = sysDict.updateDictEntry(objectMapper.writeValueAsString(conventionalDict));
        assertTrue("字典项修改失败", conventionalDict.getValue().equals("test_value_cms_c"));


        List<MDictionaryEntry> entries = sysDict.getDictEntries(systemDict.getId(),"", 1, 15);
        assertNotEquals("字典项列表获取失败", entries, null);


       sysDict.deleteDictEntry(systemDict.getId(),conventionalDict.getCode());
       // assertTrue("字典项删除失败", object1.toString().equals("true"));

        sysDict.deleteDictionary(systemDict.getId());
        //assertTrue("字典删除失败", object1.toString().equals("true"));
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
