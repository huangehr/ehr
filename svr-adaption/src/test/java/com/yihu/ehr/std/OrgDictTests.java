//package com.yihu.ehr.std;
//
//import com.yihu.ehr.adaption.orgdict.controller.OrgDictController;
//import com.yihu.ehr.model.adaption.MOrgDict;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.List;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = OrgDictTests.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
//public class OrgDictTests {
//
//	long id = 1;
//	@Autowired
//	private OrgDictController orgDictController;
//
//	@Test
//	public void createOrgDict() throws Exception{
//
//		String code = "CODE";
//		String name = "NAME";
//		String description = "DESC";
//		String orgCode = "ORGCODE";
//		String userId = "TEST";
//		MOrgDict rs = orgDictController.createOrgDict("");
//		assertTrue("新增失败", rs!=null);
//	}
//
//	@Test
//	public void updateOrgDict() throws Exception{
//		String code = "CODE";
//		String name = "NAME";
//		String description = "DESC";
//		String orgCode = "ORGCODE";
//		String userId = "TEST";
//		MOrgDict rs = orgDictController.updateOrgDict("");
//		assertTrue("修改失败！", rs!=null);
//	}
//
//	@Test
//	public void deleteOrgDict() throws Exception{
//		String ids = "1,2";
//		boolean rs = orgDictController.deleteOrgDict(id);
//		assertTrue("删除失败！", rs);
//	}
//
//
//	@Test
//	public void getOrgDict() throws Exception{
//
//		MOrgDict dict = orgDictController.getOrgDict(id);
//		assertTrue("获取机构数据集失败", dict != null);
//	}
//
//
//	@Test
//	public void getOrgDictList() throws Exception{
//
//		String orgCode = "orgCode";
//		List ls = orgDictController.getOrgDict(orgCode);
//		assertTrue("获取机构数据集失败", ls != null);
//	}
//
//}
