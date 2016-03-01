package com.yihu.ehr.std;

import com.yihu.ehr.adaption.orgdictitem.controller.OrgDictItemController;
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

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OrgDictItemTests.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
public class OrgDictItemTests {

	long id = 1;
	@Autowired
	private OrgDictItemController orgDictItemController;

	@Test
	public void createOrgDictItem() throws Exception{
		String code = "CODE";
		String name = "NAME";
		String orgCode = "ORGCODE";
		String userId = "TEST";

		int orgDictSeq = 1;
		String sort = "1";
		String description = "desc";
		boolean rs = orgDictItemController.createOrgDictItem(orgDictSeq, orgCode, code, name, description, sort, userId);
		assertTrue("新增失败", rs);
	}

	@Test
	public void updateDictItem() throws Exception{
		String code = "CODE";
		String name = "NAME";
		String orgCode = "ORGCODE";
		String userId = "TEST";
		int orgDictSeq = 1;
		String sort = "1";
		String description = "desc";
		boolean rs = orgDictItemController.updateDictItem(id, orgDictSeq, orgCode, code, name, description, sort, userId);
		assertTrue("修改失败！", rs);
	}

	@Test
	public void deleteOrgDictItemList() throws Exception{
		String ids = "1,2";
		boolean rs = orgDictItemController.deleteOrgDictItemList(ids);
		assertTrue("删除失败！", rs);
	}


	@Test
	public void getOrgDictItem() throws Exception{

		MOrgDictItem dictItem = orgDictItemController.getOrgDictItem(id);
		assertTrue("获取机构数据集失败", dictItem != null);
	}


	@Test
	public void getOrgDictEntry() throws Exception{

		String orgCode = "orgCode";
		Integer orgDictSeq = 1;
		Collection ls = orgDictItemController.getOrgDictEntry(orgDictSeq, orgCode);
		assertTrue("获取机构数据集失败", ls != null);
	}

}
