package com.yihu.ehr;

import com.yihu.ehr.apps.controller.AppController;
import com.yihu.ehr.model.app.MApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrAppApplication.class)
public class SvrAppsApplicationTests {

	String apiVersion = "v1.0";
	String appId = "";
	String secret = "";

    @Autowired
    private AppController appController;



	//新增一条记录
	@Test
	public void aSaveAddress() throws Exception {
		String name = "ehr测试应用";
		String catalog = "ChildHealth";
		String url = "103";
		String description = "hr测试应用";
		String tags = "app";
		String userId = "0dae0003561cc415c72d9111e8cb88aa";  //admin
		Object app = appController.createApp(apiVersion,name,catalog,url,description,tags,userId);
		appId = ((MApp) app).getId();
		secret= ((MApp) app).getSecret();
		//Object obj = appController.getAddressById("v1.0", addessId.toString());
		assertTrue("查询失败！" , app != null);
	}

	//查询列表
	@Test
	public void cGetApp() throws Exception {
		Object app =  appController.getApp(apiVersion,"dmQShirMpV");
		assertTrue("查询失败！" , app != null);
	}


	//查询列表
	@Test
	public void dGetAppList() throws Exception {
		String appName = "";
		String catalog = "ChildHealth";
		String status = "";
		String page = "1";
		String rows = "10";  //admin
		Object result = appController.getAppList(apiVersion,appId,appName,catalog,status,page,rows);
		assertTrue("查询失败！" , result != null);
	}

	//查询列表
	@Test
	public void fGetAppDetail() throws Exception {
		Object appDetail =  appController.getAppDetail(apiVersion,appId);
		assertTrue("查询失败！" , appDetail != null);
	}



	//新增一条记录
	@Test
	public void hUpdateApp() throws Exception {

		String appId = "ehr测试应用1";
		String name = "ehr测试应用1";
		String catalog = "ChildHealth";
		String status = "Forbidden";
		String url = "104";
		String description = "hr测试应用";
		String tags = "apps";
		Object result = appController.updateApp(apiVersion,appId,name,catalog,status,url,description,tags);
		assertTrue("修改失败！" , !"success".equals(result));
	}


	//新增一条记录
	@Test
	public void jCheck() throws Exception {
		String status = "Approved";
		Object result = appController.check(apiVersion,appId,status);
		assertTrue("修改失败！" , !"success".equals(result));
	}


	//新增一条记录
	@Test
	public void kValidationApp() throws Exception {
		Object result = appController.check(apiVersion,appId,secret);
		assertTrue("validationApp失败！" , !"true".equals(result));
	}


	//查询列表
	@Test
	public void lDeleteApp() throws Exception {
		appController.deleteApp(apiVersion,appId);
		Object app = appController.getApp(apiVersion,appId);
		assertTrue("查询失败！" , app == null);
	}





}
