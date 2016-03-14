package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgMetaDataDetailModel;
import com.yihu.ehr.ha.adapter.controller.*;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/3/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdapterDataSetControllerTests {

    @Autowired
    private AdapterDataSetController dataSetController;

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgDataSetController orgDataSetController;

    @Autowired
    private OrgMetaDataController metaDataController;

    @Autowired
    private OrgAdapterPlanController planController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    public void aTestAdapterDataSet() throws Exception{

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

        OrgDataSetDetailModel dataSetModel = new OrgDataSetDetailModel();
        dataSetModel.setCode("test_cms_code");
        dataSetModel.setName("test_cms_name");
        dataSetModel.setOrganization(orgDetailModel.getCode());
        dataSetModel.setCreateDate("2016-03-01");
        dataSetModel.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");

        envelop = orgDataSetController.saveOrgDataSet(objectMapper.writeValueAsString(dataSetModel));
        assertTrue("机构数据集新增失败!",!envelop.isSuccessFlg());

        String fields = "";
        String filter = "name=test_cms_name";
        int page = 1;
        int rows = 15;
        envelop = orgDataSetController.searchAdapterOrg(fields,filter,"",rows,page);
        assertTrue("列表获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDataSetModel orgDataSetModel = objectMapper.readValue(jsonData,OrgDataSetModel.class);


        OrgMetaDataDetailModel metaDataModel = new OrgMetaDataDetailModel();
        metaDataModel.setCode("test_cms_code");
        metaDataModel.setName("test_cms_name");
        metaDataModel.setOrganization(orgDetailModel.getCode());
        metaDataModel.setOrgDataSet(dataSetModel.getSequence());
        metaDataModel.setDescription("这是测试数据元!");
        metaDataModel.setCreateDate("2016-03-01");
        metaDataModel.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");

        envelop = metaDataController.saveOrgMetaData(objectMapper.writeValueAsString(metaDataModel));
        assertTrue("机构数据元新增失败!",!envelop.isSuccessFlg());


        fields = "";
        filter = "name=test_cms_name";
        page = 1;
        rows = 15;
        envelop = metaDataController.searchOrgMetaData(fields,filter,"",rows,page);
        assertTrue("列表信息获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        metaDataModel = objectMapper.readValue(jsonData,OrgMetaDataDetailModel.class);
        /**************************新增机构标准信息 End ********************************/

        /******************************适配方案新增 Start ***************************************/

        AdapterPlanModel adapterPlanModel = new AdapterPlanModel();
        adapterPlanModel.setCode("test_code_cms");
        adapterPlanModel.setName("test_name_cms");
        adapterPlanModel.setVersion("000000000000");
        adapterPlanModel.setOrg("CSJG1019002");
        adapterPlanModel.setDescription("这是测试方案!");
        adapterPlanModel.setType("1");
        envelop = planController.addAdapterPlan(objectMapper.writeValueAsString(adapterPlanModel),"true");
        assertTrue("新增失败", envelop.isSuccessFlg());

        fields = "";
        filter = "name=test_name_cms";
        page = 1;
        rows = 15;
        envelop = planController.searchAdapterPlan(fields,filter,"",rows,page);
        assertTrue("列表获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        adapterPlanModel=objectMapper.readValue(jsonData,AdapterPlanModel.class);

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
        /******************************适配方案新增 End ***************************************/
    }
}
