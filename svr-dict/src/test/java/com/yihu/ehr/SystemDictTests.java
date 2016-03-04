//package com.yihu.ehr;
//
//import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
//import com.yihu.ehr.dict.controller.SystemDictController;
//import com.yihu.ehr.dict.service.SystemDict;
//import com.yihu.ehr.model.dict.MSystemDict;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//import static org.springframework.test.util.AssertionErrors.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SvrDictApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class SystemDictTests {
//
//    @Autowired
//    private SystemDictController systemDictController;
//
//    @Test
//    public void atestGetDictionaries() throws Exception {
//        String fields = "";
//        String filters = "";
//        String sorts = "";
//        int page = 1;
//        int size = 10;
//        Object result = systemDictController.getDictionaries(fields,filters,sorts,size,page,null,null);
//        assertTrue("查询失败！", result != null);
//    }
//
//    @Test
//    public void btestCreateDictionary() throws Exception {
//        SystemDict systemDict = new SystemDict();
//        systemDict.setName("test");
//        systemDict.setAuthorId("testUser");
//        systemDict.setCreateDate(new Date());
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonDate = objectMapper.writeValueAsString(systemDict);
//        Object dict = systemDictController.createDictionary(jsonDate);
//        assertTrue("创建失败！", dict != null);
//    }
//
//
//    @Test
//    public void btestGetDictionary() throws Exception {
//        int id = 184;
//        Object result = systemDictController.getDictionary(id);
//        assertTrue("查询失败！", result != null);
//    }
//
//
//    @Test
//    public void ctestUpdateDict() throws Exception {
//        int id = 184;
//        MSystemDict systemDict = systemDictController.getDictionary(id);
//        systemDict.setName("test1");
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonData = objectMapper.writeValueAsString(systemDict);
//        Object result = systemDictController.updateDictionary(jsonData);
//        assertTrue("修改失败！", result != null);
//    }
//
//    @Test
//    public void dtestDeleteDictionary() throws Exception {
//        long id = 184;
//        try {
//            systemDictController.deleteDictionary(id);
//        }catch (Exception e){
//            assertTrue("删除失败！",true);
//        }
//    }
//
//}
