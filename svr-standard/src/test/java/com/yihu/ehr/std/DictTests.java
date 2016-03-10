//package com.yihu.ehr.std;
//
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MStdDict;
//import com.yihu.ehr.standard.dict.controller.DictController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = StandardServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class DictTests {
//
//    @Autowired
//    private DictController dictController;
//
//    String version = "000000000000";
//    long id = 1;
//
//    @Test
//    public void testDeleteDict() throws Exception{
//        boolean rs = dictController.deleteDict(version, id);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testDeleteDicts() throws Exception{
//        String ids = "1,2";
//        boolean rs = dictController.deleteDicts(version, ids);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testGetDict() throws Exception{
//        MStdDict mStdDict = dictController.getCdaDictInfo(id, version);
//        assertTrue("查询数据为空！", mStdDict != null);
//    }
//
//    @Test
//    public void testSaveDict() throws Exception{
//        String code = "test1";
//        String name = "test1";
//        String stdSource = "test1";
//        long baseDict = 1;
//        String desc = "test1";
//        String userId = "lincl";
////        boolean rs = dictController.addDict(code, name, stdSource, version, baseDict, desc, userId);
////        assertTrue("新增失败！", rs);
//    }
//
//    @Test
//    public void testUpdateDict() throws Exception{
//        String code = "test1";
//        String name = "test1";
//        String stdSource = "test1";
//        long baseDict = 1;
//        String desc = "test1";
//        String userId = "lincl";
////        boolean rs = dictController.updateDict(id, code, name, stdSource, version, baseDict, desc, userId);
////        assertTrue("修改失败！", rs);
//    }
//}
