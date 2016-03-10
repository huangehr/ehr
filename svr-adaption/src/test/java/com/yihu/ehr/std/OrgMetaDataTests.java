package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.AdaptionServiceApp;
import com.yihu.ehr.adaption.orgmetaset.controller.OrgMetaDataController;
import com.yihu.ehr.model.adaption.MOrgDictItem;
import com.yihu.ehr.model.adaption.MOrgMetaData;
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
public class OrgMetaDataTests {

    long id = 1;
    @Autowired
    private OrgMetaDataController orgMetaDataController;
    ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void createOrgMetaData() throws Exception {
        MOrgMetaData metaData = new MOrgMetaData();
        metaData.setCode("testcode");
        metaData.setName("testname");
        metaData.setDescription("testdesc");
        metaData.setOrganization("testorg");
        metaData.setCreateUser("testuser");
        metaData.setColumnLength(1);
        metaData.setColumnType("add");
        metaData.setOrgDataSet(1);
        metaData.setSequence(5);


        MOrgMetaData rs = orgMetaDataController.createOrgMetaData(
                objectMapper.writeValueAsString(metaData));
        assertTrue("新增失败", rs != null);

        List<MOrgMetaData> ls = (List) orgMetaDataController.searchOrgMetaDatas("", "code=testcode;name=testname", "+name", 15, 1, null, null);
        assertTrue("获取列表失败", ls != null);

        if(ls!=null && ls.size()>0){
            rs = ls.get(0);
            rs.setCode("updatecode");
            rs = orgMetaDataController.updateOrgMetaData(
                    objectMapper.writeValueAsString(rs));
            assertTrue("修改失败", rs != null);
        }


        rs = orgMetaDataController.getOrgMetaData(rs.getId());
        assertTrue("获取失败", rs != null);

        boolean b = orgMetaDataController.deleteOrgMetaData(rs.getId());
        assertTrue("删除失败", rs != null);

        rs = orgMetaDataController.createOrgMetaData(
                objectMapper.writeValueAsString(metaData));


        try {
            rs = orgMetaDataController.createOrgMetaData(
                    objectMapper.writeValueAsString(metaData));
        }catch (Exception e){
            e.printStackTrace();
        }

        metaData.setCode("TESTCODE2");
        rs = orgMetaDataController.createOrgMetaData(
                objectMapper.writeValueAsString(metaData));

        ls = (List) orgMetaDataController.searchOrgMetaDatas("", "name=testname", "+name", 15, 1, null, null);

        if(ls!=null && ls.size()>0){String ids = "";
            for(MOrgMetaData dictItem1: ls){
                ids += "," + dictItem1.getId();
            }
            b = orgMetaDataController.deleteOrgMetaDataList(ids.substring(1));
            assertTrue("删除列表失败", b);
        }

    }


}
