package com.yihu.ehr.org;

import com.yihu.ehr.SvrOrgApplication;
import com.yihu.ehr.org.controller.OrganizationController;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrOrgApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvrOrgApplicationTests {

	String apiVersion = "v1.0";
	String appId = "";
	ApplicationContext applicationContext;


    @Autowired
    private OrganizationController organizationController;


//	@Test
//	public void atestGetOrgModel() throws Exception{
//		applicationContext = new SpringApplicationBuilder()
//				.web(false).sources(SvrOrgApplication.class).run();
//		String orgCode = "341321234";
//		Object org = organizationController.getOrg(apiVersion,orgCode);
//		assertTrue("查询失败！" , org != null);
//	}

//	@Test
//	public void btestActivity() throws Exception{
//		String orgCode = "341321234";
//		String activityFlag = "1";
//		Object result = organizationController.activity(apiVersion,orgCode,activityFlag);
//		assertTrue("操作失败！" , "success".equals(result));
//	}

//	@Test
//	public void ctestupdateOrg() throws Exception{
//		String orgCode = "341321234";
//		OrgModel model = (OrgModel) organizationController.getOrgModel(apiVersion,orgCode);
//		model.setUpdateFlg("0");
//        model.setAdmin("admin");
//        ObjectMapper objectMapper = new ObjectMapper();
//        String orgStr = objectMapper.writeValueAsString(model);
//		Object result = organizationController.updateOrg(apiVersion,orgStr);
//		assertTrue("修改失败！" , result != null);
//	}


//	@Test
//	public void dtestgetOrgModel() throws Exception{
//		String orgCode = "341321234";
//		Object org = organizationController.getOrgModel(apiVersion,orgCode);
//		assertTrue("查询失败！" , org != null);
//	}
//
//	@Test
//	public void etestgetOrgByCode() throws Exception{
//		String orgCode = "341321234";
//		Object org = organizationController.getOrgByCode(apiVersion,orgCode);
//		assertTrue("查询失败！" , org != null);
//	}
//
//	@Test
//	public void ftestGetIdsByName() throws Exception{
//		String orgName = "医院";
//		Object org = organizationController.getIdsByName(apiVersion,orgName);
//		assertTrue("查询失败！" , org != null);
//	}

//	@Test
//	public void gtestGetOrgsByAddress() throws Exception{
//		String province = "广东省";
//		String city = "广州市";
//		Object org = organizationController.getOrgsByAddress(apiVersion,province,city);
//		assertTrue("查询失败！" , org != null);
//	}
//
//	@Test
//	public void htestDistributeKey() throws Exception{
//		String orgCode = "341321234";
//		Object org = organizationController.distributeKey(apiVersion,orgCode);
//		assertTrue("操作失败！" , org != null);
//	}

//	@Test
//	public void itestValidationOrg() throws Exception{
//		String orgCode = "341321234";
//		Object result = organizationController.validationOrg(apiVersion,orgCode);
//		assertTrue("查询失败！" , result!=null);
//	}


}
