package com.yihu.ehr.std;

import com.yihu.ehr.adaption.adapterorg.controller.AdapterOrgController;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrg;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapterOrgTests.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
public class AdapterOrgTests {

	@Autowired
	private AdapterOrgController adapterOrgController;

	@Test
	public void testAddAdapterOrg() throws Exception{
		String model = "";
		MAdapterOrg rs = adapterOrgController.addAdapterOrg(model);
		assertTrue("新增失败", rs!=null);
	}

	@Test
	public void testUpdateAdapterOrg() throws Exception{
		String code = "test1";
		String name = "test1";
		String desc = "test2";
		MAdapterOrg rs = adapterOrgController.updateAdapterOrg(code, name, desc);
		assertTrue("修改失败！", rs!=null);
	}

	@Test
	public void testDelAdapterOrg() throws Exception{
		String codes = "test1";
		boolean rs = adapterOrgController.delAdapterOrg(codes);
		assertTrue("删除失败！", rs);
	}

	@Test
	public void testOrgIsExistData() throws Exception{
		String org = "org";
		boolean rs = adapterOrgController.orgIsExistData(org);
		assertTrue("采集机构不存在采集数据！", rs);
	}

	@Test
	public void testGetVersion() throws Exception{
		String code = "000000000000";
		MAdapterOrg adapterOrg = adapterOrgController.getAdapterOrg(code);
		assertTrue("获采集机构失败", adapterOrg != null);
	}

}
