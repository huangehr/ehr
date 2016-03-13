package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.AdaptionServiceApp;
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

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdaptionServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
public class AdapterOrgTests {

	@Autowired
	private AdapterOrgController adapterOrgController;
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testAddAdapterOrg() throws Exception{
		MAdapterOrg adapterOrg = new MAdapterOrg();
		adapterOrg.setCode("testcode");
		adapterOrg.setName("testname");
		adapterOrg.setDescription("testdesc");
		adapterOrg.setArea("testArea");
		adapterOrg.setOrg("testorg");
		adapterOrg.setParent("CSJG1020002");
		adapterOrg.setType("testtype");

		boolean b = adapterOrgController.isExistAdapterOrg("341321234");
		assertTrue("机构不存在", b );

		b = adapterOrgController.orgIsExistData("341321234");
		assertTrue("该机构不存在数据", b );

		MAdapterOrg rs = adapterOrgController.addAdapterOrg(
				objectMapper.writeValueAsString(adapterOrg));
		assertTrue("新增失败", rs != null);

		List<MAdapterOrg> ls = (List) adapterOrgController.searchAdapterOrg("", "code=testcode;name=testname", "+name", 15, 1, null, null);
		assertTrue("获取列表失败", ls != null);

		if(ls!=null && ls.size()>0){
			rs = ls.get(0);
//			rs.setName("updateName");
			rs = adapterOrgController.updateAdapterOrg(rs.getCode(), rs.getName(), "testUpdate");
			assertTrue("修改失败", rs != null);
		}


		rs = adapterOrgController.getAdapterOrg(rs.getCode());
		assertTrue("获取失败", rs != null);

		b = adapterOrgController.delAdapterOrg(rs.getCode());
		assertTrue("删除失败", rs != null);

		adapterOrg.setCode("TESTCODE3");
		rs = adapterOrgController.addAdapterOrg(
				objectMapper.writeValueAsString(adapterOrg));


		try {
			rs = adapterOrgController.addAdapterOrg(
					objectMapper.writeValueAsString(adapterOrg));
		}catch (Exception e){
			e.printStackTrace();
		}

		adapterOrg.setCode("TESTCODE2");
		rs = adapterOrgController.addAdapterOrg(
				objectMapper.writeValueAsString(adapterOrg));

		ls = (List) adapterOrgController.searchAdapterOrg("", "name=testname", "+name", 15, 1, null, null);

		if(ls!=null && ls.size()>0){String ids = "";
			for(MAdapterOrg dictItem1: ls){
				ids += "," + dictItem1.getCode();
			}
			b = adapterOrgController.delAdapterOrg(ids.substring(1));
			assertTrue("删除列表失败", b);
		}
	}

}
