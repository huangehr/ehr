package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.dict.controller.AdapterDictController;
import com.yihu.ehr.adaption.dict.service.AdapterDict;
import com.yihu.ehr.adaption.orgdataset.controller.OrgDataSetController;
import com.yihu.ehr.model.adaption.MAdapterDict;
import com.yihu.ehr.model.adaption.MDataSet;
import com.yihu.ehr.model.adaption.MOrgDataSet;
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
@SpringApplicationConfiguration(classes = OrgDataSetTests.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
public class OrgDataSetTests {

	long id = 1;
	@Autowired
	private OrgDataSetController orgDataSetController;

	@Test
	public void createOrgDataSet() throws Exception{

		String code = "CODE";
		String name = "NAME";
		String description = "DESC";
		String orgCode = "ORGCODE";
		String userId = "TEST";
		boolean rs = orgDataSetController.createOrgDataSet(code, name, description, orgCode, userId);
		assertTrue("新增失败", rs);
	}

	@Test
	public void updateAdapterMetaData() throws Exception{
		String code = "CODE";
		String name = "NAME";
		String description = "DESC";
		String orgCode = "ORGCODE";
		String userId = "TEST";
		boolean rs = orgDataSetController.updateOrgDataSet(orgCode, id, code, name, description, userId);
		assertTrue("修改失败！", rs);
	}

	@Test
	public void deleteOrgDataSet() throws Exception{
		String ids = "1,2";
		boolean rs = orgDataSetController.deleteOrgDataSet(id);
		assertTrue("删除失败！", rs);
	}


	@Test
	public void getOrgDataSet() throws Exception{

		MOrgDataSet dataSet = orgDataSetController.getOrgDataSet(id);
		assertTrue("获取机构数据集失败", dataSet != null);
	}

}
