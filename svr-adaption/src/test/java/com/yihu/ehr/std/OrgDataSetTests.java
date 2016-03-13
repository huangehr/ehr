//
//package com.yihu.ehr.std;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yihu.ehr.AdaptionServiceApp;
//import com.yihu.ehr.adaption.dict.controller.AdapterDictController;
//import com.yihu.ehr.adaption.dict.service.AdapterDict;
//import com.yihu.ehr.adaption.orgdataset.controller.OrgDataSetController;
//import com.yihu.ehr.model.adaption.MAdapterDict;
//import com.yihu.ehr.model.adaption.MDataSet;
//import com.yihu.ehr.model.adaption.MOrgDataSet;
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
//@SpringApplicationConfiguration(classes = AdaptionServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class OrgDataSetTests {
//
//	long id = 1;
//	@Autowired
//	private OrgDataSetController orgDataSetController;
//
//	ObjectMapper objectMapper = new ObjectMapper();
//
//	@Test
//	public void createOrgDataSet() throws Exception{
//		MOrgDataSet dataSet = new MOrgDataSet();
//		dataSet.setCode("testcode");
//		dataSet.setName("testname");
//		dataSet.setDescription("testdesc");
//		dataSet.setOrganization("testorg");
//		dataSet.setCreateUser("testuser");
//
//		MOrgDataSet rs = orgDataSetController.createOrgDataSet(
//				objectMapper.writeValueAsString(dataSet));
//
//		List<MOrgDataSet> ls = (List) orgDataSetController.searchAdapterOrg("", "code=testcode;name=testname", "+name", 15, 1, null, null);
//		if(ls!=null && ls.size()>0){
//			rs = ls.get(0);
//			rs.setCode("updatecode");
//			rs = orgDataSetController.updateOrgDataSet(
//					objectMapper.writeValueAsString(rs));
//		}
//
//
//		rs = orgDataSetController.getOrgDataSet(rs.getId());
//
//		boolean b = orgDataSetController.deleteOrgDataSet(rs.getId());
//
//		assertTrue("测试失败", rs != null);
//
//
//	}
//
//
//	@Test
//	public void dataSetIsExist() throws Exception{
//		String orgCode = "dsa";
//		String code = "1";
//		boolean b = orgDataSetController.dataSetIsExist(orgCode, code);
//
//		assertTrue("不存在", b );
//	}
//
//
//}
