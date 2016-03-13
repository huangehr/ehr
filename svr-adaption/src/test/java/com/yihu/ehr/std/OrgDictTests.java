package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.AdaptionServiceApp;
import com.yihu.ehr.adaption.orgdict.controller.OrgDictController;
import com.yihu.ehr.model.adaption.MOrgDict;
import com.yihu.ehr.model.adaption.MOrgDictItem;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdaptionServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
public class OrgDictTests {

	long id = 1;
	@Autowired
	private OrgDictController orgDictController;
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void createOrgDict() throws Exception{
		MOrgDict dict = new MOrgDict();
		dict.setCode("testcode");
		dict.setName("testname");
		dict.setDescription("testdesc");
		dict.setOrganization("testorg");
		dict.setCreateUser("testuser");
		dict.setSequence(5);

		MOrgDict rs = orgDictController.createOrgDict(
				objectMapper.writeValueAsString(dict));
		assertTrue("新增失败", rs != null);

		List<MOrgDict> ls = (List) orgDictController.searchOrgDicts("", "code=testcode;name=testname", "+name", 15, 1, null, null);
		assertTrue("获取列表失败", ls != null);

		if(ls!=null && ls.size()>0){
			rs = ls.get(0);
			rs.setCode("updatecode");
			rs = orgDictController.updateOrgDict(
					objectMapper.writeValueAsString(rs));
			assertTrue("修改失败", rs != null);
		}


		rs = orgDictController.getOrgDict(rs.getId());
		assertTrue("获取失败", rs != null);

		boolean b = orgDictController.deleteOrgDict(rs.getId());
		assertTrue("删除失败", rs != null);

//		rs = orgDictController.createOrgDict(
//				objectMapper.writeValueAsString(dict));
//
//
//		try {
//			rs = orgDictController.createOrgDict(
//					objectMapper.writeValueAsString(dict));
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//		dict.setCode("TESTCODE2");
//		rs = orgDictController.createOrgDict(
//				objectMapper.writeValueAsString(dict));
//
//		ls = (List) orgDictController.searchOrgDicts("", "name=testname", "+name", 15, 1, null, null);
//
//		if(ls!=null && ls.size()>0){String ids = "";
//			for(MOrgDict dictItem1: ls){
//				ids += "," + dictItem1.getId();
//			}
//			b = orgDictController.d(ids.substring(1));
//			assertTrue("删除列表失败", b);
//		}

	}

	@Test
	public void getOrgDict() throws Exception{
		String orgCode = "CSJG1019009";
		List<String> ls = (List) orgDictController.getOrgDict(orgCode);
		assertTrue("获取列表失败", ls != null);

	}

}
