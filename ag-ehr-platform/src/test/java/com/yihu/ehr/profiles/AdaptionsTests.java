package com.yihu.ehr.profiles;

import com.yihu.ehr.api.adaption.AdaptionsEndPoint;
import com.yihu.ehr.util.RestEcho;
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
@SpringApplicationConfiguration(classes = AdaptionsTests.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
public class AdaptionsTests {

	@Autowired
	private AdaptionsEndPoint adaptions;
	String orgCode = "ceshi3";
	String userName = "1019008";
	String versionCode = "000000000000";
	String updateVersion = "56395d75b854";

	@Test
	public void getCDAVersionInfoByOrgCode() throws Exception{
		Object rs = adaptions.getCDAVersionInfoByOrgCode(orgCode);
		assertTrue("新增失败", rs!=null);
	}

	@Test
	public void getOrgAdaption() throws Exception{

		Object rs = adaptions.getOrgAdaption(userName, versionCode, orgCode);
		assertTrue("新增失败", rs!=null);
	}

	@Test
	public void getOrgAdaptions() throws Exception{
		Object rs = adaptions.getOrgAdaptions(userName,versionCode, orgCode);

//		String pwd = rs.getResult().get("cryptograph").asText();
//		rs.getResult().get("zipfile").;

		assertTrue("新增失败", rs!=null);
	}

	@Test
	public void getOrgSchema() throws Exception{
		Object rs = adaptions.getOrgSchema(userName, updateVersion, versionCode);
		assertTrue("新增失败", rs!=null);
	}
}
