//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.adaption.adapterplan.controller.OrgAdapterPlanController;
//import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
//import com.yihu.ehr.model.adaption.MAdapterPlan;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.Map;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AdapterPlanTests.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
//public class AdapterPlanTests {
//
//	long id = 1;
//	@Autowired
//	private OrgAdapterPlanController orgAdapterPlanController;
//
//	@Test
//	public void testSaveAdapterPlan() throws Exception{
//		OrgAdapterPlan plan = new OrgAdapterPlan();
//		plan.setOrg("testOrg");
//		plan.setName("testName");
//		plan.setDescription("test");
//		plan.setCode("testCode");
//		plan.setParentId(1l);
//		plan.setStatus(1);
//		plan.setType("1");
//		plan.setVersion("00000000000");
//		String json = new ObjectMapper().writeValueAsString(plan);
//		MAdapterPlan rs = orgAdapterPlanController.saveAdapterPlan(new ObjectMapper().writeValueAsString(plan), "true");
//		assertTrue("新增失败", rs!=null);
//	}
//
//	@Test
//	public void testUpdateAdapterOrg() throws Exception{
//		OrgAdapterPlan plan = new OrgAdapterPlan();
//		plan.setOrg("testOrg");
//		plan.setName("testName");
//		plan.setDescription("test");
//		plan.setCode("testCode");
//		plan.setParentId(1l);
//		plan.setStatus(1);
//		plan.setType("1");
//		plan.setVersion("00000000000");
//		String json = new ObjectMapper().writeValueAsString(plan);
//		MAdapterPlan rs = orgAdapterPlanController.updateAdapterPlan(2L, json);
//		assertTrue("修改失败！", rs!=null);
//	}
//
//	@Test
//	public void delAdapterPlan() throws Exception{
//		String ids = "1,2";
//		boolean rs = orgAdapterPlanController.delAdapterPlan(ids);
//		assertTrue("删除失败！", rs);
//	}
//
//	@Test
//	public void getAdapterCustomize() throws Exception{
//		Map map = orgAdapterPlanController.getAdapterCustomize(id, "000000000000");
//
//	}
//
//	@Test
//	public void testGetVersion() throws Exception{
//
//		MAdapterPlan adapterPlan = orgAdapterPlanController.getAdapterPlanById(id);
//		assertTrue("获采集方案失败", adapterPlan != null);
//	}
//
//}
