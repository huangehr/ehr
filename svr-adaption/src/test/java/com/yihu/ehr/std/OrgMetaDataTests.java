package com.yihu.ehr.std;

import com.yihu.ehr.adaption.orgmetaset.controller.OrgMetaDataController;
import com.yihu.ehr.model.adaption.MOrgMetaData;
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
@SpringApplicationConfiguration(classes = OrgMetaDataTests.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
public class OrgMetaDataTests {

    long id = 1;
    @Autowired
    private OrgMetaDataController orgMetaDataController;

    @Test
    public void createOrgMetaData() throws Exception {
        String code = "CODE";
        String name = "NAME";
        String orgCode = "ORGCODE";
        String userId = "TEST";
        String description = "desc";
        int orgDataSetSeq = 1;
        MOrgMetaData rs = orgMetaDataController.createOrgMetaData("");
        assertTrue("新增失败", rs!=null);
    }

    @Test
    public void updateDictItem() throws Exception {
        String code = "CODE";
        String name = "NAME";
        String orgCode = "ORGCODE";
        String userId = "TEST";
        int orgDataSetSeq = 1;
        String description = "desc";
        MOrgMetaData rs = orgMetaDataController.updateOrgMetaData("");
        assertTrue("修改失败！", rs!=null);
    }

    @Test
    public void deleteOrgMetaData() throws Exception {
        String ids = "1,2";
        boolean rs = orgMetaDataController.deleteOrgMetaData(id);
        assertTrue("删除失败！", rs);
    }

    @Test
    public void deleteOrgMetaDataList() throws Exception {
        String ids = "1,2";
        boolean rs = orgMetaDataController.deleteOrgMetaDataList(ids);
        assertTrue("删除失败！", rs);
    }

    @Test
    public void getOrgMetaData() throws Exception {

        MOrgMetaData metaData = orgMetaDataController.getOrgMetaData(id);
        assertTrue("获取机构数据集失败", metaData != null);
    }


}
