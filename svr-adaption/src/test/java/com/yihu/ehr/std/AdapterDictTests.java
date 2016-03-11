//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.adaption.dataset.controller.AdapterDataSetController;
//import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
//import com.yihu.ehr.adaption.dict.controller.AdapterDictController;
//import com.yihu.ehr.adaption.dict.service.AdapterDict;
//import com.yihu.ehr.model.adaption.MAdapterDataSet;
//import com.yihu.ehr.model.adaption.MAdapterDict;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AdapterDictTests.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
//public class AdapterDictTests {
//
//	long id = 1;
//	@Autowired
//	private AdapterDictController adapterDictController;
//
//	@Test
//	public void testSaveAdapterPlan() throws Exception{
//		AdapterDict dict = new AdapterDict();
//		dict.setAdapterPlanId(1l);
//		dict.setDescription("ddd");
//		dict.setDictEntryId(1l);
//		dict.setDictId(1l);
//		dict.setOrgDictEntrySeq(1l);
//		dict.setOrgDictSeq(1l);
//		String json = new ObjectMapper().writeValueAsString(dict);
//		MAdapterDict rs = adapterDictController.createAdapterDictEntry(json);
//		assertTrue("新增失败", rs!=null);
//	}
//
//	@Test
//	public void updateAdapterMetaData() throws Exception{
//		AdapterDict dict = new AdapterDict();
//		dict.setAdapterPlanId(1l);
//		dict.setDescription("ddd");
//		dict.setDictEntryId(1l);
//		dict.setDictId(1l);
//		dict.setOrgDictEntrySeq(1l);
//		dict.setOrgDictSeq(1l);
//		String json = new ObjectMapper().writeValueAsString(dict);
//		MAdapterDict rs = adapterDictController.updateAdapterDictEntry(id, json);
//		assertTrue("修改失败！", rs!=null);
//	}
//
//	@Test
//	public void delAdapterPlan() throws Exception{
//		String ids = "1,2";
//		boolean rs = adapterDictController.delDictEntry(ids);
//		assertTrue("删除失败！", rs);
//	}
//
//
//	@Test
//	public void getAdapterDictEntry() throws Exception{
//
//		MAdapterDict dict = adapterDictController.getAdapterDictEntry(id);
//		assertTrue("获取适配字典项失败", dict != null);
//	}
//
//}
