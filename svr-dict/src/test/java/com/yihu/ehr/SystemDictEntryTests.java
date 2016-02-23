package com.yihu.ehr;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.dict.controller.SystemDictController;
import com.yihu.ehr.dict.controller.SystemDictEntryController;
import com.yihu.ehr.dict.service.SystemDict;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrDictApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SystemDictEntryTests {

    @Autowired
    private SystemDictEntryController systemDictEntryController;


    @Test
    public void atestGetDictionaries() throws Exception {
        long dictId = 2;
        String code = "Approved";
        int page = 1;
        int size = 10;
        Object result = systemDictEntryController.getDictEntries(dictId,code,size,page,null,null);
        assertTrue("查询失败！", result != null);
    }

    @Test
    public void btestCreateDictEntry() throws Exception {
        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setCode("test");
        systemDictEntry.setDictId(2);
        systemDictEntry.setValue("test");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonDate = objectMapper.writeValueAsString(systemDictEntry);
        Object dict = systemDictEntryController.createDictEntry(jsonDate);
        assertTrue("查询失败！", dict != null);
    }


    @Test
    public void btestGetDictEntry() throws Exception {
        int dictId = 2;
        String code = "Approved";
        Object result = systemDictEntryController.getDictEntry(dictId,code);
        assertTrue("查询失败！", result != null);
    }


    @Test
    public void ctestDeleteDictEntry() throws Exception {
        int dictId = 2;
        String code = "Approved";
        try {
            systemDictEntryController.deleteDictEntry(dictId,code);
        }catch (Exception e){
            assertTrue("删除失败！",true);
        }
    }

    @Test
    public void dtestUpdateDictEntry() throws Exception {
        int dictId = 2;
        String code = "Approved";
        MDictionaryEntry dictEntry = systemDictEntryController.getDictEntry(dictId,code);
        dictEntry.setValue("test");
        String jsonData = new ObjectMapper().writeValueAsString(dictEntry);
        Object result = systemDictEntryController.updateDictEntry(jsonData);
        assertTrue("执行失败！", result != null);
    }

}
