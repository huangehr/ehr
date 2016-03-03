package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.ha.adapter.controller.AdapterOrgController;
import com.yihu.ehr.ha.adapter.controller.OrgDataSetController;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/3/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdapterOrgTests {

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgDataSetController dataSetController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    public void atestAdapterOrg() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        AdapterOrgDetailModel detailModel = new AdapterOrgDetailModel();
       // detailModel.setName("test_org_cms");
        detailModel.setType("2");
        detailModel.setDescription("这是测试机构");
        detailModel.setOrg("CSJG1019002");

        Envelop envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(detailModel));
        assertTrue("名称为空", !envelop.isSuccessFlg());

        detailModel.setName("test_org_cms");
        detailModel.setOrg("");
        envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(detailModel));
        assertTrue("机构为空", !envelop.isSuccessFlg());

        detailModel.setOrg("CSJG1019002");
        envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(detailModel));
        assertTrue("新增失败", envelop.isSuccessFlg());

        detailModel = (AdapterOrgDetailModel)envelop.getObj();
        envelop = adapterOrgController.getAdapterOrg(detailModel.getCode());
        assertTrue("第三方机构获取失败", envelop.isSuccessFlg()&&envelop.getObj()!=null);

        detailModel = (AdapterOrgDetailModel)envelop.getObj();
        detailModel.setName("test_org_cms_c");
        envelop = adapterOrgController.updateAdapterOrg(detailModel.getCode(),detailModel.getName(),detailModel.getDescription());
        assertTrue("修改失败", envelop.isSuccessFlg());

        envelop = adapterOrgController.delAdapterOrg(detailModel.getCode());
        assertTrue("删除失败", envelop.isSuccessFlg());

    }

    public void bTestStandardCopy() throws Exception{
        AdapterOrgDetailModel detailModel = new AdapterOrgDetailModel();
        detailModel.setName("test_org_cms");
        detailModel.setType("2");
        detailModel.setDescription("这是测试机构");
        detailModel.setOrg("CSJG1019002");
        detailModel.setParent("ceshi3");

        Envelop envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(detailModel));

        String fields = "";
        String filter = "organization=CSJG1019002";
        int page = 1;
        int rows = 15;
        Envelop envelop1 = dataSetController.searchAdapterOrg(fields,filter,"",rows,page);
        assertTrue("新增成功!", envelop.isSuccessFlg() && envelop1.getDetailModelList().size()>0);

        detailModel = (AdapterOrgDetailModel)envelop.getObj();
        envelop = adapterOrgController.delAdapterOrg(detailModel.getCode());

        envelop1 = dataSetController.searchAdapterOrg(fields,filter,"",rows,page);
        assertTrue("删除成功!", envelop.isSuccessFlg() && envelop1.getDetailModelList().size()==0);
    }
}
