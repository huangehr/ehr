package com.yihu.ehr;

import com.yihu.ehr.dict.controller.ConventionalDictController;
import com.yihu.ehr.dict.controller.SystemDictController;
import com.yihu.ehr.dict.controller.SystemDictEntryController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrDictApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvrDictApplicationTests {

    String apiVersion = "v1.0";
    ApplicationContext applicationContext;

    @Autowired
    private SystemDictController dictController;

    @Autowired
    private SystemDictEntryController entryController;

    @Autowired
    private ConventionalDictController conventionalDictController;

    @Test
    public void atestGetUserModel() throws Exception {
        String userId = "0dae0003561cc415c72d9111e8cb88aa";
        String name = "应用app";
        String reference = "应用app";
        Object dict = dictController.createDictionary("");
        assertTrue("查询失败！", dict != null);
    }


    @Test
    public void btestDeleteDict() throws Exception {
        long dictId = 1;
        dictController.deleteDictionary(dictId);
    }

    @Test
    public void ctestUpdateDict() throws Exception {
        long dictId = 1;
        String name = "App1";
        Object systemDict = dictController.updateDictionary("");
        assertTrue("修改失败！", systemDict != null);
    }

    @Test
    public void dtestSearchSysDicts() throws Exception {
        long dictId = 1;
        String name = "APP";
        String phoneticCode = "APP";
        int page = 1;
        int rows = 10;
        Object result = dictController.getDictionary(dictId);
        assertTrue("修改失败！", result != null);
    }

    @Test
    public void etestCreateDictEntry() throws Exception {
        long dictId = 1;
        String code = "APP";
        String value = "APP";
        int sort = 10;
        String catalog = "";
        Object result = entryController.createDictEntry("");
        assertTrue("创建失败！", result != null);
    }

    @Test
    public void ftestDeleteDictEntry() throws Exception {
        long dictId = 1;
        String code = "ChildHealth";
        entryController.deleteDictEntry(dictId, code);
        assertTrue("删除失败！", true);
    }

    @Test
    public void gtestUpdateDictEntry() throws Exception {
        long dictId = 1;
        String code = "APP";
        String value = "APP32";
        int sort = 101;
        String catalog = "APP32";
        Object result = entryController.updateDictEntry("");
        assertTrue("修改失败！", result != null);
    }

    @Test
    public void htestSearchDictEntryList() throws Exception {
        long dictId = 1;
        int page = 1;
        int rows = 10;
        Object result = entryController.getDictEntries(dictId, rows, page, null, null);
        assertTrue("查询失败！", result != null);
    }

    @Test
    public void itestelectTags() throws Exception {
        Object result = conventionalDictController.getTagsList();
        assertTrue("查询失败！", result != null);
    }

    @Test
    public void jtestSearchDictEntryListForDDL() throws Exception {
        long dictId = 1;
        Object result = entryController.getDictEntries(dictId, 0, 0, null, null);
        assertTrue("查询失败！", result != null);
    }

    @Test
    public void ltestAutoSearchDictEntryList() throws Exception {
        long dictId = 1;
        String value = "儿童保健";
        Object result = entryController.getDictEntries(dictId, 0, 0, null, null);
        assertTrue("查询失败！", result != null);
    }


}
