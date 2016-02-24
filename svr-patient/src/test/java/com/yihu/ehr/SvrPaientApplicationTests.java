package com.yihu.ehr;

import com.yihu.ehr.patient.controller.PatientController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrPatientApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvrPaientApplicationTests {

	ApplicationContext applicationContext;

	@Autowired
	private PatientController patientController;

	@Test
	public void aSearchOrgs() throws Exception{
		applicationContext = new SpringApplicationBuilder()
				.web(false).sources(SvrPatientApplication.class).run();
		String name = "";
		String idCardNo = "";
		String province = "河北省";
		String city = "唐山市";
		String district = "遵化市";
		int size = 10;
		int page = 1;
		Object org = patientController.searchPatient(name,idCardNo,province,city,district,size,page);
		assertTrue("查询失败！" , org != null);
	}

}
