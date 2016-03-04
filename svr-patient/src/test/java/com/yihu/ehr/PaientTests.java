//package com.yihu.ehr;
//
//import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
//import com.yihu.ehr.model.patient.MDemographicInfo;
//import com.yihu.ehr.patient.controller.PatientController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SvrPatientApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class PaientTests {
//
//	ApplicationContext applicationContext;
//
//	@Autowired
//	private PatientController patientController;
//
//	@Test
//	public void aSearchOrgs() throws Exception{
//		applicationContext = new SpringApplicationBuilder()
//				.web(false).sources(SvrPatientApplication.class).run();
//		String name = "";
//		String idCardNo = "";
//		String province = "河北省";
//		String city = "衡水市";
//		String district = "安平县";
//		int size = 1;
//		int page = 10;
//		Object result = patientController.searchPatient(name,idCardNo,province,city,district,size,page,null,null);
//		assertTrue("查询失败！" , result != null);
//	}
//
//
//
//	@Test
//	public void dGetPatient() throws Exception{
//		String idCardNo = "412726195002150030";
//		Object result = patientController.getPatient(idCardNo);
//		assertTrue("查询失败！" , result != null);
//	}
//
//	@Test
//	public void gCreatePatient() throws Exception{
//		String idCardNo = "412726195002150030";
//		MDemographicInfo info = patientController.getPatient(idCardNo);
//		info.setIdCardNo("3522251991828317218");
//		String jsonData = new ObjectMapper().writeValueAsString(info);
//		MDemographicInfo model = patientController.createPatient(jsonData,null);
//		assertTrue("创建失败！" , model != null);
//	}
//
//	@Test
//	public void hUpdatePatient() throws Exception{
//		String idCardNo = "412726195002150030";
//		MDemographicInfo info = patientController.getPatient(idCardNo);
//		info.setName("test");
//		String jsonData = new ObjectMapper().writeValueAsString(info);
//		MDemographicInfo model = patientController.updatePatient(jsonData,null);
//		assertTrue("修改失败！" , model != null);
//	}
//
//	@Test
//	public void kResetPass() throws Exception{
//		String idCardNo = "412726195002150030";
//		boolean resut = patientController.resetPass(idCardNo);
//		assertTrue("修改失败！" , resut == true);
//	}
//
//	@Test
//	public void lDeletePatient() throws Exception{
//		String idCardNo = "412726195002150030";
//		Object result = patientController.deletePatient(idCardNo);
//		assertTrue("删除失败！" , result != null);
//	}
//
//
//}
