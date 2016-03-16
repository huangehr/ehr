package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetModel;
import com.yihu.ehr.ha.adapter.controller.AdapterOrgController;
import com.yihu.ehr.ha.adapter.controller.OrgDataSetController;
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
 * Created by AndyCai on 2016/3/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrgDataSetControllerTests {

    @Autowired
    private OrgDataSetController orgDataSetController;

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTestOrgDataSet() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        AdapterOrgDetailModel detailModel = new AdapterOrgDetailModel();
        detailModel.setName("test_org_cms");
        detailModel.setType("2");
        detailModel.setDescription("这是测试机构");
        detailModel.setOrg("CSJG1019002");
        detailModel.setCode("CSJG1019002");

        Envelop envelop = adapterOrgController.addAdapterOrg(objectMapper.writeValueAsString(detailModel));
        assertTrue("适配机构新增失败!", envelop.isSuccessFlg());

        detailModel = (AdapterOrgDetailModel)envelop.getObj();

        OrgDataSetDetailModel dataSetModel = new OrgDataSetDetailModel();
      //  dataSetModel.setCode("test_cms_code");
      //  dataSetModel.setName("test_cms_name");
       // dataSetModel.setOrganization(detailModel.getCode());
        dataSetModel.setCreateDate("2016-03-01");
        dataSetModel.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");

        envelop = orgDataSetController.saveOrgDataSet(objectMapper.writeValueAsString(dataSetModel));
        assertTrue("非空验证失败!",!envelop.isSuccessFlg());

        dataSetModel.setCode("test_cms_code");
        dataSetModel.setName("test_cms_name");
        dataSetModel.setOrganization(detailModel.getCode());
        envelop = orgDataSetController.saveOrgDataSet(objectMapper.writeValueAsString(dataSetModel));
        assertTrue("新增失败!",envelop.isSuccessFlg());

        String fields = "";
        String filter = "name=test_cms_name";
        int page = 1;
        int rows = 15;
        envelop = orgDataSetController.searchAdapterOrg(fields,filter,"",rows,page);
        assertTrue("列表获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDataSetModel orgDataSetModel = objectMapper.readValue(jsonData,OrgDataSetModel.class);

        envelop = orgDataSetController.getOrgDataSet( Integer.parseInt(orgDataSetModel.getId()));
        assertTrue("数据集明细获取失败!",envelop.isSuccessFlg()&&envelop.getObj()!=null);

        dataSetModel = (OrgDataSetDetailModel)envelop.getObj();
        dataSetModel.setName("test_cms_name_c");
        envelop = orgDataSetController.saveOrgDataSet(objectMapper.writeValueAsString(dataSetModel));
        assertTrue("修改失败!", envelop.isSuccessFlg() && envelop.getObj() != null && ((OrgDataSetDetailModel) envelop.getObj()).getName().equals("test_cms_name_c"));

        envelop = orgDataSetController.getDataSetBySequence(detailModel.getCode(),dataSetModel.getSequence());
        assertTrue("数据获取失败!",envelop.isSuccessFlg()&&envelop.getObj()!=null);

        envelop = orgDataSetController.deleteOrgDataSet(dataSetModel.getId());
        assertTrue("数据集删除失败!",envelop.isSuccessFlg());

        envelop=adapterOrgController.delAdapterOrg(detailModel.getCode());
        assertTrue("机构删除失败!",envelop.isSuccessFlg());
    }
}
