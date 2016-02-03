package com.yihu.ehr;

import com.yihu.ehr.apps.controller.AppController;
import com.yihu.ehr.apps.service.App;
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
@SpringApplicationConfiguration(classes = SvrAppApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvrAppsApplicationTests {

	String apiVersion = "v1.0";
	String appId = "";
	ApplicationContext applicationContext;


    @Autowired
	private AppController appController;


	//新增一条记录
	@Test
	public void atestSaveApp() throws Exception {
		applicationContext = new SpringApplicationBuilder()
				.web(false).sources(SvrAppApplication.class).run();
		String name = "ehrTest";
		String catalog = "ChildHealth";
		String url = "103";
		String description = "ehrTest";
		String tags = "app";
		String userId = "0dae0003561cc415c72d9111e8cb88aa";  //admin
		Object app =  appController.createApp(apiVersion,name,catalog,url,description,tags,userId);
		appId = ((App) app).getId();
		assertTrue("查询失败！" , appId != null);
	}

	@Test
	public void btestGetAppList() throws Exception{
		applicationContext = new SpringApplicationBuilder()
				.web(false).sources(SvrAppApplication.class).run();
		String appId = "";
		String appName = "";
		String catalog = "";
		String status = "";
		int page = 1;
		int rows = 10;
		Object appList = appController.getAppList(apiVersion,appId,appName,catalog,status,page,rows);
		assertTrue("查询失败！" , appList != null);
	}



	@Test
	public void ctestGetApp() throws Exception{
		String appId = "yYkKALLwUO";
		Object app = appController.getApp(apiVersion,appId);
		assertTrue("查询失败！" , app != null);
	}



//	@Test
//	public void dtestGetAppDetail() throws Exception{
//		String appId = "yYkKALLwUO";
//		Object appDetail = appController.getAppDetail(apiVersion,appId);
//		assertTrue("查询失败！" , appDetail != null);
//	}




	@Test
	public void etestCheck() throws Exception {
		String appId = "yYkKALLwUO";
		String status = "Approved";
		Object result = appController.check(apiVersion,appId,status);
		assertTrue("faild" , "success".equals(result));
	}


	@Test
	public void ftestValidationApp() throws Exception {
		String appId = "yYkKALLwUO";
		String secret = "RgfyNkytS2PK3D3Xs";
		Object result = appController.validationApp(apiVersion,appId,secret);
		assertTrue("faild" , result!=null);
	}

	@Test
	public void gtestUpdateApp() throws Exception {
		String appId = "yYkKALLwUO";
		String name = "健康档案浏览器";
		String catalog = "ChildHealth";
		String status = "Forbidden";
		String url = "104";
		String description = "yYkKALLwUO";
		String tags = "appss";
		Object result = appController.updateApp(apiVersion,appId,name,catalog,status,url,description,tags);
		assertTrue("修改失败！" , "success".equals(result));
	}


	@Test
	public void htestDeleteApp() throws Exception{
		String appId = "yYkKALLwUO";
		Object result = appController.deleteApp(apiVersion,appId);
		assertTrue("删除失败！" , "success".equals(result));
	}


}
