//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MStdDict;
//import com.yihu.ehr.model.standard.MStdDictEntry;
//import com.yihu.ehr.standard.dict.controller.DictController;
//import com.yihu.ehr.standard.dict.controller.DictEntryController;
//import com.yihu.ehr.standard.dict.service.IDict;
//import com.yihu.ehr.standard.dict.service.IDictEntry;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.List;
//
//import static org.junit.Assert.assertTrue;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = StandardServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class DictEntryTests {
//
//    @Autowired
//    private DictEntryController dictEntryController;
//
//    String version = "000000000000";
//    long id = 1;
//    long dictId = 1;
//
//
//    @Test
//    public void test() throws Exception{
//        String  version = "000000000000";
//        IDictEntry dictEntry = new IDictEntry();
//        dictEntry.setCode("TESTSOUR");
//        dictEntry.setDictId(1l);
//        dictEntry.setValue("TESTvaa.0");
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        MStdDictEntry rs = dictEntryController.addDictEntry(version, objectMapper.writeValueAsString(dictEntry));
//        assertTrue("新增失败！", rs!=null);
//
//        List<MStdDictEntry> ls = (List) dictEntryController.searchDictEntry("", "code=TESTSOUR", "", 15, 1, version, null, null);
//        if(ls.size()>0){
//            rs = ls.get(0);
//            rs.setCode("TEST_UPDATE");
//            rs = dictEntryController.updateDictEntry(version, rs.getId(), objectMapper.writeValueAsString(rs));
//            assertTrue("修改失败！", rs!=null);
//        }
//
//        rs = dictEntryController.getDictEntry(rs.getId(), version);
//        assertTrue("查询数据为空！", rs != null);
//
//        boolean b = dictEntryController.deleteDictEntry(version, rs.getId());
//        assertTrue("删除数量 0！", b);
//
//    }
//
//
//    @Test
//    public void testDeleteDictEntryList() throws Exception{
//        boolean rs = dictEntryController.deleteDictEntryList(version, dictId);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testGetDictEntry() throws Exception{
//        MStdDictEntry mStdDictEntry = dictEntryController.getDictEntry(id, version);
//        assertTrue("查询数据为空！", mStdDictEntry != null);
//    }
//
//}
