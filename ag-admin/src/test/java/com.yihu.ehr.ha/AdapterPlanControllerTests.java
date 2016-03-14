package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/3/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdapterPlanControllerTests {

//    @Autowired
//    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgAdapterPlanController planController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTestPlan()throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();
//        AdapterOrgDetailModel orgDetailModel = new AdapterOrgDetailModel();
//        orgDetailModel.setCode("CSJG1019002");
//        orgDetailModel.setName("test_org_cms");
//        orgDetailModel.setType("2");
//        orgDetailModel.setDescription("这是测试机构");
//        orgDetailModel.setOrg("CSJG1019002");
//        Envelop envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(orgDetailModel));
//        assertTrue("机构新增失败", envelop.isSuccessFlg());

        AdapterPlanModel adapterPlanModel = new AdapterPlanModel();
        adapterPlanModel.setCode("test_code_cms");
        adapterPlanModel.setName("test_name_cms");
        adapterPlanModel.setVersion("000000000000");
        adapterPlanModel.setOrg("CSJG1019002");
        adapterPlanModel.setDescription("这是测试方案!");
        adapterPlanModel.setType("1");
        Envelop envelop = planController.addAdapterPlan(objectMapper.writeValueAsString(adapterPlanModel),"true");
        assertTrue("新增失败", envelop.isSuccessFlg());

        String fields = "";
        String filter = "name=test_name_cms";
        int page = 1;
        int rows = 15;
        envelop = planController.searchAdapterPlan(fields,filter,"",rows,page);
        assertTrue("列表获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        adapterPlanModel=objectMapper.readValue(jsonData,AdapterPlanModel.class);

        adapterPlanModel.setName("test_name_cms_c");
        envelop = planController.updateAdapterPlan(objectMapper.writeValueAsString(adapterPlanModel));
        assertTrue("修改失败!",envelop.isSuccessFlg());

        envelop=planController.getAdapterPlanById(adapterPlanModel.getId());
        assertTrue("方案明细获取失败!",envelop.isSuccessFlg());

        List<Map<String, String>> plans = planController.getAdapterPlanList("1","000000000000");
        assertTrue("根据标准版本获取方案列表失败!",plans!=null && plans.size()>0);


        List<CustomModel> customModels = new ArrayList<>();
        CustomModel customModel = new CustomModel();
        customModel.setId("11");
        customModel.setPid("2");
        customModel.setText("本人姓名");
        customModels.add(customModel);

        CustomModel customModel1 = new CustomModel();
        customModel1.setId("62");
        customModel1.setPid("3");
        customModel1.setText("机构名称");
        customModels.add(customModel1);

        boolean result = planController.adapterDataSet(adapterPlanModel.getId(),objectMapper.writeValueAsString( customModels));
        assertTrue("定制失败!",result);

        envelop = planController.delAdapterPlan(String.valueOf(adapterPlanModel.getId()));
        assertTrue("删除失败!",envelop.isSuccessFlg());
    }
}
