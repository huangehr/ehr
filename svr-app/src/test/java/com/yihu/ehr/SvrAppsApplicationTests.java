package com.yihu.ehr;

import com.yihu.ehr.apps.controller.AppController;
import com.yihu.ehr.apps.feignClient.dict.ConventionalDictClient;
import com.yihu.ehr.apps.feignClient.user.UserClient;
import com.yihu.ehr.model.app.MApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrAppApplication.class)
@EnableDiscoveryClient
@EnableFeignClients
public class SvrAppsApplicationTests {

	String apiVersion = "v1.0";
	String appId = "";
	String secret = "";




    @Autowired
    private AppController appController;


	@Before
	public void setUp() throws Exception {


	}



	//新增一条记录
	@Test
	public void aSaveApp() throws Exception {

		ApplicationContext context = new SpringApplicationBuilder()
				.web(false).sources(SvrAppApplication.class).run();
		ConventionalDictClient conventionalDictClien1t = context.getBean(ConventionalDictClient.class);

		UserClient userClient = context.getBean(UserClient.class);



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


	@Test
	public void atestCreateApp() {
		try {
			//新增测试
			Object object = appController.createApp("v1.0", "测试APP", "ChildHealth", "http://test", "这是用于测试的数据", "1", "0dae0003561cc415c72d9111e8cb88aa");
			assertNotEquals("APP新增失败", object, null);

			MApp mApp = (MApp) object;
			//修改测试
			String id = mApp.getId();
			String name = mApp.getName();
			String secret = mApp.getSecret();
			String url = mApp.getUrl();
			String catalog = mApp.getCatalog();
			String status = mApp.getStatus();
			String description = mApp.getDescription();
			String tags = mApp.getTags();
			object = appController.updateApp("v1.0",id, name, catalog, status, url, description, tags);
			//success
			assertTrue("APP修改失败", !object.toString().equals("success"));

			//获取明细
			object = appController.getAppDetail("v1.0",id);
			assertNotEquals("APP明细获取失败", object, null);

			//修改状态
			object = appController.check("v1.0",id, "WaitingForApprove");
			assertTrue("APP状态修改失败", !object.toString().equals("success"));

			//根据查询条件获取列表
			// object = appController.getAppList(1, "ChildHealth", "WaitingForApprove", 1, 20);
			object = appController.deleteApp("v1.0",id);
			assertTrue("APP删除失败", !object.toString().equals("success"));
		}
		catch (Exception ex){
			int i=0;
		}

	}


}
