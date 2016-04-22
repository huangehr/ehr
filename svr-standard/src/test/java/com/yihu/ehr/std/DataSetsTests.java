//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MStdDataSet;
//import com.yihu.ehr.standard.datasets.controller.BaseRestEndPoint;
//import com.yihu.ehr.standard.datasets.service.BaseDataSet;
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
//public class DataSetsTests {
//
//    @Autowired
//    private BaseRestEndPoint dataSetsController;
//
//
//    @Test
//    public void test() throws Exception{
//        String  version = "000000000000";
//        BaseDataSet dataSet = new BaseDataSet();
//        dataSet.setCode("TESTSOUR");
//        dataSet.setName("TESTSOUR");
//        dataSet.setStdVersion("v1.0");
//        dataSet.setReference("ref");
//        dataSet.setPublisher(1);
//        dataSet.setCatalog(2);
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        MStdDataSet rs = dataSetsController.saveDataSet(version, objectMapper.writeValueAsString(dataSet));
//        assertTrue("新增失败！", rs!=null);
//
//        List<MStdDataSet> ls = (List) dataSetsController.searchDataSets("", "code=TESTSOUR", "", 15, 1, version, null, null);
//        if(ls.size()>0){
//            rs = ls.get(0);
//            rs.setCode("TEST_UPDATE");
//            rs = dataSetsController.updateDataSet(version, rs.getId(), objectMapper.writeValueAsString(rs));
//            assertTrue("修改失败！", rs!=null);
//        }
//
//        rs = dataSetsController.getDataSet(rs.getId(), version);
//        assertTrue("查询数据为空！", rs != null);
//
//        boolean b = dataSetsController.deleteDataSet(rs.getId(), version);
//        assertTrue("删除数量 0！", b);
//
//    }
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
//}
