//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.adaption.dataset.controller.AdapterDataSetController;
//import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
//import com.yihu.ehr.model.adaption.MAdapterDataSet;
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
//@SpringApplicationConfiguration(classes = AdapterDataSetTests.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
//public class AdapterDataSetTests {
//
//	long id = 1;
//	@Autowired
//	private AdapterDataSetController adapterDataSetController;
//
//	@Test
//	public void testSaveAdapterPlan() throws Exception{
//		AdapterDataSet dataSet = new AdapterDataSet();
//		dataSet.setDescription("desc");
//		dataSet.setAdapterPlanId(1l);
//		dataSet.setDataSetId(1l);
//		dataSet.setDataType("1");
//		dataSet.setMetaDataId(1l);
//		dataSet.setOrgDataSetSeq(1l);
//		dataSet.setOrgMetaDataSeq(2l);
//		dataSet.setStdDict(1l);
//		String json = new ObjectMapper().writeValueAsString(dataSet);
//		MAdapterDataSet rs = adapterDataSetController.createAdapterMetaData(json);
//		assertTrue("新增失败", rs!=null);
//	}
//
//	@Test
//	public void updateAdapterMetaData() throws Exception{
//		AdapterDataSet dataSet = new AdapterDataSet();
//		dataSet.setDescription("desc");
//		dataSet.setAdapterPlanId(1l);
//		dataSet.setDataSetId(1l);
//		dataSet.setDataType("1");
//		dataSet.setMetaDataId(1l);
//		dataSet.setOrgDataSetSeq(1l);
//		dataSet.setOrgMetaDataSeq(2l);
//		dataSet.setStdDict(1l);
//		String json = new ObjectMapper().writeValueAsString(dataSet);
//		MAdapterDataSet rs = adapterDataSetController.updateAdapterMetaData(id, json);
//		assertTrue("修改失败！", rs!=null);
//	}
//
//	@Test
//	public void delAdapterPlan() throws Exception{
//		String ids = "1,2";
//		boolean rs = adapterDataSetController.delMetaData(ids);
//		assertTrue("删除失败！", rs);
//	}
//
//
//	@Test
//	public void getAdapterMetaData() throws Exception{
//
//		MAdapterDataSet dataSet = adapterDataSetController.getAdapterMetaData(id);
//		assertTrue("获取适配数据元失败", dataSet != null);
//	}
//
//}
