//
//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.AdaptionServiceApp;
//import com.yihu.ehr.adaption.adapterplan.controller.OrgAdapterPlanController;
//import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
//import com.yihu.ehr.model.adaption.MAdapterOrg;
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
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AdaptionServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
////@WebAppConfiguration
//public class AdapterPlanTests {
//
//	long id = 1;
//	@Autowired
//	private OrgAdapterPlanController orgAdapterPlanController;
//	ObjectMapper objectMapper = new ObjectMapper();
//
//	@Test
//	public void testSaveAdapterPlan() throws Exception{
//		MAdapterPlan adapterPlan = new MAdapterPlan();
//		adapterPlan.setCode("testcode");
//		adapterPlan.setName("testname");
//		adapterPlan.setDescription("testdesc");
//		adapterPlan.setOrg("testorg");
//		adapterPlan.setType("testtype");
////		adapterPlan.setParentId();
////		adapterPlan.setStatus();
//		adapterPlan.setVersion("000000000000");
//
//
//
//		MAdapterPlan rs = orgAdapterPlanController.saveAdapterPlan(
//				objectMapper.writeValueAsString(adapterPlan), "false");
//		assertTrue("新增失败", rs != null);
//
//		List<MAdapterPlan> ls = (List) orgAdapterPlanController.searchAdapterPlan("", "code=testcode;name=testname", "+name", 15, 1, null, null);
//		assertTrue("获取列表失败", ls != null);
//
//		if(ls!=null && ls.size()>0){
//			rs = ls.get(0);
////			rs.setName("updateName");
//			rs.setName("updateName");
//			rs = orgAdapterPlanController.updateAdapterPlan(rs.getId(), objectMapper.writeValueAsString(rs));
//			assertTrue("修改失败", rs != null);
//		}
//
//
//		rs = orgAdapterPlanController.getAdapterPlanById(rs.getId());
//		assertTrue("获取失败", rs != null);
//
//		boolean b = orgAdapterPlanController.delAdapterPlan(rs.getId() + "");
//		assertTrue("删除失败", rs != null);
//
//		adapterPlan.setCode("TESTCODE3");
//		rs = orgAdapterPlanController.saveAdapterPlan(
//				objectMapper.writeValueAsString(adapterPlan), "false");
//
//
//		try {
//			rs = orgAdapterPlanController.saveAdapterPlan(
//					objectMapper.writeValueAsString(adapterPlan), "false");
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//		adapterPlan.setCode("TESTCODE2");
//		adapterPlan.setParentId(111l);
//		rs = orgAdapterPlanController.saveAdapterPlan(
//				objectMapper.writeValueAsString(adapterPlan), "true");
//
//		ls = (List) orgAdapterPlanController.searchAdapterPlan("", "name=testname", "+name", 15, 1, null, null);
//
//		if(ls!=null && ls.size()>0){
//			String ids = "";
//			for(MAdapterPlan dictItem1: ls){
//				ids += "," + dictItem1.getId();
//			}
//			b = orgAdapterPlanController.delAdapterPlan(ids.substring(1));
//			assertTrue("删除列表失败", b);
//		}
//	}
//
//
//	@Test
//	public void testSearch() throws Exception{
//		String version = "000000000000";
//		String type = "1";
//		List<Map<String, String>> rs = orgAdapterPlanController.getAdapterPlanList(type, version);
//		assertTrue("查询为空", rs != null);
//
//		/**
//		 * 这里涉及到svr-standard模块， 先启动svr-standard， 第一次访问时会time-out
//		 */
//		Long planId = 17l;
//		Map map = orgAdapterPlanController.getAdapterCustomize(planId, version);
//		assertTrue("查询为空", rs != null);
//
////		String cusData = "";
////		boolean b = orgAdapterPlanController.adapterDataSet(planId, cusData);
////		assertTrue("查询为空", b);
//
////		boolean b = orgAdapterPlanController.adapterDispatch(planId);
////		assertTrue("生成失败", b);
//	}
//
//}