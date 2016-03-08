//package com.yihu.ehr.std;
//
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MCDAVersion;
//import com.yihu.ehr.model.standard.MStdDataSet;
//import com.yihu.ehr.standard.cdaversion.controller.CdaVersionController;
//import com.yihu.ehr.standard.datasets.controller.DataSetsController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = StandardServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class DataSetsTests {
//
//    @Autowired
//    private DataSetsController dataSetsController;
//
//    String version = "000000000000";
//    long id = 1;
//
//    @Test
//    public void testDeleteDataSet() throws Exception{
//        boolean rs = dataSetsController.deleteDataSet(id, version);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testDeleteDataSets() throws Exception{
//        String ids = "1,2";
//        boolean rs = dataSetsController.deleteDataSet(ids, version);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testGetDataSet() throws Exception{
//        MStdDataSet mStdDataSet = dataSetsController.getDataSet(id, version);
//        assertTrue("获取信息失败！", mStdDataSet!=null);
//    }
//
//    @Test
//    public void testSaveDataSet() throws Exception{
//        String code = "test1";
//        String name = "test1";
//        String refStandard = "test1";
//        String summary = "test";
////        boolean rs = dataSetsController.saveDataSet(code, name, refStandard, summary, version);
////        assertTrue("新增失败！", rs);
//    }
//
//    @Test
//    public void testUpdateDataSet() throws Exception{
//        String code = "test1";
//        String name = "test1";
//        String refStandard = "test1";
//        String summary = "test";
////        boolean rs = dataSetsController.updateDataSet(id, code, name, refStandard, summary, version);
////        assertTrue("更新失败！", rs);
//    }
//}
