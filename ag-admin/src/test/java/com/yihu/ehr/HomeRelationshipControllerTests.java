package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.patient.HomeGroupModel;
import com.yihu.ehr.patient.controller.HomeRelationshipController;
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
 * Created by AndyCai on 2016/4/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HomeRelationshipControllerTests {

    @Autowired
    public HomeRelationshipController relationshipController;

    @Autowired
    public ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTest(){
        try {
            applicationContext = new SpringApplicationBuilder()
                    .web(false).sources(AgAdminApplication.class).run();

            String fields = "";
            String filter = "householderIdCardNo=350521199004296559";
            int page = 1;
            int rows = 15;
            Envelop envelop = relationshipController.getHomeGroup(fields, filter, "", rows, page);
            assertTrue("列表获取失败", envelop.isSuccessFlg() && envelop.getDetailModelList() != null);

            HomeGroupModel homeGroupModel = objectMapper.readValue(objectMapper.writeValueAsString(envelop.getDetailModelList().get(0)), HomeGroupModel.class);
            filter = "familyId="+homeGroupModel.getId();
            envelop = relationshipController.getHomeRelationship(fields, filter, "", rows, page);
        }
        catch (Exception ex)
        {
            int i=0;
        }
    }
}
