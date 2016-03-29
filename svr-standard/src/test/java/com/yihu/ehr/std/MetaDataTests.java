//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MStdMetaData;
//import com.yihu.ehr.standard.datasets.controller.MetaDataController;
//import com.yihu.ehr.standard.datasets.service.IMetaData;
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
//public class MetaDataTests {
//
//    @Autowired
//    private MetaDataController metaDataController;
//
//    String version = "000000000000";
//    long id = 1;
//    long dataSetId = 1;
//
//    @Test
//    public void test() throws Exception{
//        String  version = "000000000000";
//        IMetaData metaData = new IMetaData();
//        metaData.setCode("TESTSOUR");
//        metaData.setDictId(1l);
//        metaData.setDataSetId(1l);
//        metaData.setInnerCode("testinner");
//        metaData.setName("testnaem");
//        metaData.setColumnName("testcom");
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        MStdMetaData rs = metaDataController.saveMetaSet(version, objectMapper.writeValueAsString(metaData));
//        assertTrue("新增失败！", rs!=null);
//
//        List<MStdMetaData> ls = (List) metaDataController.searchDataSets("", "code=TESTSOUR", "", 15, 1, version, null, null);
//        if(ls.size()>0){
//            rs = ls.get(0);
//            rs.setCode("TEST_UPDATE");
//            rs = metaDataController.updataMetaSet(version, rs.getId(), objectMapper.writeValueAsString(rs));
//            assertTrue("修改失败！", rs!=null);
//        }
//
//        rs = metaDataController.getMetaData(rs.getId(), version);
//        assertTrue("查询数据为空！", rs != null);
//
//        boolean b = metaDataController.deleteMetaData(rs.getId(), version);
//        assertTrue("删除数量 0！", b);
//
//    }
//
//
//    @Test
//    public void testDeleteMetaData() throws Exception{
//
//        boolean rs = metaDataController.deleteMetaData(id, version);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testDeleteMetaDatas() throws Exception{
//        String ids = "1,2";
//        boolean rs = metaDataController.deleteMetaDatas(ids, version);
//        assertTrue("删除数量 0！", rs);
//    }
//
//    @Test
//    public void testDeleteMetaDataByDataSet() throws Exception{
//        boolean rs = metaDataController.deleteMetaDataByDataSet(dataSetId, version);
//        assertTrue("删除数量 0！", rs);
//    }
//
//
//    @Test
//    public void testGetMetaData() throws Exception{
//        MStdMetaData mStdMetaData = metaDataController.getMetaData(id, version);
//        assertTrue("获取信息失败！", mStdMetaData != null);
//    }
//
//    @Test
//    public void testValidateCode() throws Exception{
//        String code = "";
//        boolean rs = metaDataController.validateInnerCode(version, dataSetId, code);
//        assertTrue("code不重复！", rs);
//    }
//
//    @Test
//    public void testValidatorName() throws Exception{
//        String name = "";
//        boolean rs = metaDataController.validatorName(version, dataSetId, name);
//        assertTrue("name不重复！", rs);
//    }
//}
