package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.AdaptionServiceApp;
import com.yihu.ehr.adaption.orgdictitem.controller.OrgDictItemController;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItem;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import com.yihu.ehr.model.adaption.MOrgDictItem;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdaptionServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@WebAppConfiguration
public class OrgDictItemTests {

	long id = 1;
	@Autowired
	private OrgDictItemController orgDictItemController;

	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void createOrgDictItem() throws Exception{
		MOrgDictItem dictItem = new MOrgDictItem();
		dictItem.setCode("testcode");
		dictItem.setName("testname");
		dictItem.setDescription("testdesc");
		dictItem.setOrganization("testorg");
		dictItem.setCreateUser("testuser");
		dictItem.setOrgDict(4);
//		dictItem.setSequence(5);
		dictItem.setSort(5);


		MOrgDictItem rs = orgDictItemController.createOrgDictItem(
				objectMapper.writeValueAsString(dictItem));
		assertTrue("新增失败", rs != null);

		List<MOrgDictItem> ls = (List) orgDictItemController.searchOrgDictItems("", "code=testcode;name=testname", "+name", 15, 1, null, null);
		assertTrue("获取列表失败", ls != null);

		if(ls!=null && ls.size()>0){
			rs = ls.get(0);
			rs.setCode("updatecode");
			rs = orgDictItemController.updateDictItem(
					objectMapper.writeValueAsString(rs));
			assertTrue("修改失败", rs != null);
		}


		rs = orgDictItemController.getOrgDictItem(rs.getId());
		assertTrue("获取失败", rs != null);

		boolean b = orgDictItemController.deleteOrgDictItem(rs.getId());
		assertTrue("删除失败", rs != null);

		rs = orgDictItemController.createOrgDictItem(
				objectMapper.writeValueAsString(dictItem));


		try {
			rs = orgDictItemController.createOrgDictItem(
					objectMapper.writeValueAsString(dictItem));
		}catch (Exception e){
			e.printStackTrace();
		}

		dictItem.setCode("TESTCODE2");
		rs = orgDictItemController.createOrgDictItem(
				objectMapper.writeValueAsString(dictItem));

		ls = (List) orgDictItemController.searchOrgDictItems("", "name=testname", "+name", 15, 1, null, null);

		if(ls!=null && ls.size()>0){String ids = "";
			for(MOrgDictItem dictItem1: ls){
				ids += "," + dictItem1.getId();
			}
			b = orgDictItemController.deleteOrgDictItemList(ids.substring(1));
			assertTrue("删除列表失败", b);
		}


	}


	@Test
	public void getOrgDictEntry() throws Exception{

		Integer orgDictSeq = 1;
		String orgCode = "CSJG1019009";
		List<String> ls = (List) orgDictItemController.getOrgDictEntry(orgDictSeq, orgCode);
		assertTrue("获取列表失败", ls != null);

	}
}
