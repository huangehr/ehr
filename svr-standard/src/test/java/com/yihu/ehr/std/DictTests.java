//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MStdDict;
//import com.yihu.ehr.standard.dict.controller.DictController;
//import com.yihu.ehr.standard.dict.service.IDict;
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
////@Transactional
//public class DictTests {
//
//    @Autowired
//    private DictController dictController;
//
//    String version = "000000000000";
//    long id = 1;
//
//
//
//    @Test
//    public void test() throws Exception{
//        String  version = "000000000000";
//        IDict dict = new IDict();
//        dict.setCode("TESTSOUR");
//        dict.setName("TESTSOUR");
//        dict.setStdVersion("v1.0");
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        MStdDict rs = dictController.addDict(version, objectMapper.writeValueAsString(dict));
//        assertTrue("新增失败！", rs!=null);
//
//        List<MStdDict> ls = (List) dictController.searchDataSets("", "code=TESTSOUR", "", 15, 1, version, null, null);
//        if(ls.size()>0){
//            rs = ls.get(0);
//            rs.setCode("TEST_UPDATE");
//            rs = dictController.updateDict(version, rs.getId(), objectMapper.writeValueAsString(rs));
//            assertTrue("修改失败！", rs!=null);
//        }
//
//        rs = dictController.getCdaDictInfo(rs.getId(), version);
//        assertTrue("查询数据为空！", rs != null);
//
//        boolean b = dictController.deleteDict(version, rs.getId());
//        assertTrue("删除数量 0！", b);
//
//    }
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
//}
