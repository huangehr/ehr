package com.yihu.ehr.std;

import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdDictEntry;
import com.yihu.ehr.standard.dict.controller.DictController;
import com.yihu.ehr.standard.dict.controller.DictEntryController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DictEntryTests {

    @Autowired
    private DictEntryController dictEntryController;

    String version = "000000000000";
    long id = 1;
    long dictId = 1;
    @Test
    public void testDeleteDictEntry() throws Exception{
        boolean rs = dictEntryController.deleteDictEntry(version, dictId, id);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testDeleteDictEntrys() throws Exception{
        String ids = "1,2";
        boolean rs = dictEntryController.deleteDictEntrys(version, dictId, ids);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testDeleteDictEntryList() throws Exception{
        boolean rs = dictEntryController.deleteDictEntryList(version, dictId);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testGetDictEntry() throws Exception{
        MStdDictEntry mStdDictEntry = dictEntryController.getDictEntry(id, version);
        assertTrue("查询数据为空！", mStdDictEntry != null);
    }

    @Test
    public void testSaveDataSet() throws Exception{
        String code = "test1";
        String value = "test1";
        String desc = "test1";
        boolean rs = dictEntryController.addDictEntry(version, dictId, code, value, desc);
        assertTrue("新增失败！", rs);
    }

    @Test
    public void testUpdateDataSet() throws Exception{
        String code = "test1";
        String value = "test1";
        String desc = "test1";
        boolean rs = dictEntryController.updateDictEntry(id, version, dictId, code, value, desc);
        assertTrue("修改失败！", rs);
    }
}
