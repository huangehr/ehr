package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.ha.adapter.controller.AdapterOrgController;
import com.yihu.ehr.ha.adapter.controller.OrgAdapterPlanController;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/3/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdapterDictControllerTests {

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgAdapterPlanController planController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTestDict()throws Exception{
        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        /**************************新增机构标准信息 Start ********************************/
        AdapterOrgDetailModel orgDetailModel = new AdapterOrgDetailModel();
        orgDetailModel.setCode("CSJG1019002");
        orgDetailModel.setName("test_org_cms");
        orgDetailModel.setType("2");
        orgDetailModel.setDescription("这是测试机构");
        orgDetailModel.setOrg("CSJG1019002");
        Envelop envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(orgDetailModel));
        assertTrue("机构新增失败", envelop.isSuccessFlg());
    }
}
