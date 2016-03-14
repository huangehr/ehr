//
//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.AdaptionServiceApp;
//import com.yihu.ehr.adaption.dataset.controller.AdapterDataSetController;
//import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
//import com.yihu.ehr.model.adaption.MAdapterDataSet;
//import com.yihu.ehr.model.adaption.MAdapterDataVo;
//import com.yihu.ehr.model.adaption.MDataSet;
//import com.yihu.ehr.model.adaption.MOrgMetaData;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.List;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AdaptionServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
////@WebAppConfiguration
//public class AdapterDataSetTests {
//	ApplicationContext applicationContext;
//	long id = 1;
//	@Autowired
//	private AdapterDataSetController adapterDataSetController;
//	ObjectMapper objectMapper = new ObjectMapper();
//
//	@Test
//	public void testSaveAdapterPlan() throws Exception{
//		applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(AdaptionServiceApp.class).run();
//		AdapterDataSet dataSet = new AdapterDataSet();
//		dataSet.setDescription("desc");
//		dataSet.setAdapterPlanId(17l);
//		dataSet.setDataSetId(1l);
//		dataSet.setDataType("1");
//		dataSet.setMetaDataId(1l);
//		dataSet.setOrgDataSetSeq(1l);
//		dataSet.setOrgMetaDataSeq(2l);
//		dataSet.setStdDict(1l);
//
//
//		MAdapterDataSet rs = adapterDataSetController.createAdapterMetaData(
//				objectMapper.writeValueAsString(dataSet)
//		);
//		assertTrue("新增失败", rs != null);
//
//		List<MDataSet> ls = (List) adapterDataSetController
//				.searchAdapterDataSet(17l, "", "", "", 15, 1, null, null);
//		assertTrue("获取列表失败", ls != null);
//
//		List<MAdapterDataVo> adapterDataSets = (List) adapterDataSetController
//				.searchAdapterMetaData(17l, 1l, "", "", "", 15, 1, null, null);
//		assertTrue("获取列表失败", adapterDataSets != null);
//
////		MAdapterDataVo vo ;
////		if(ls!=null && ls.size()>0){
////			vo = adapterDataSets.get(0);
////			vo.setDescription("updatedesc");
////			rs = adapterDataSetController.updateAdapterMetaData(
////					rs.getId(),
////					objectMapper.writeValueAsString(rs));
////			assertTrue("修改失败", rs != null);
////		}
//
//		rs = adapterDataSetController.getAdapterMetaData(56753l);
//		assertTrue("没查找到数据", rs != null);
//
//		rs.setDescription("updateTest");
//		rs = adapterDataSetController.updateAdapterMetaData(
//				rs.getId(),
//				objectMapper.writeValueAsString(rs));
//
////		boolean b = adapterDataSetController.delMetaData(rs.getId() + "");
////		assertTrue("删除失败", rs != null);
//
//		rs = adapterDataSetController.createAdapterMetaData(
//				objectMapper.writeValueAsString(dataSet));
//
//
//		try {
//			rs = adapterDataSetController.createAdapterMetaData(
//					objectMapper.writeValueAsString(dataSet));
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//		dataSet.setDescription("TESTCODE2");
//		rs = adapterDataSetController.createAdapterMetaData(
//				objectMapper.writeValueAsString(dataSet));
//
//		adapterDataSets = (List) adapterDataSetController
//				.searchAdapterMetaData(17l, 15l, "", "", "", 15, 1, null, null);
//
//		if(ls!=null && ls.size()>0){
//			String ids = "";
//			for(MAdapterDataVo adapterDataSet: adapterDataSets){
//				ids += "," + adapterDataSet.getId();
//			}
//			boolean b = adapterDataSetController.delMetaData(ids.substring(1));
//			assertTrue("删除列表失败", b);
//		}
//
//	}
//
//
//}
