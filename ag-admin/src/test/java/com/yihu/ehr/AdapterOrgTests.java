package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.controller.AdapterOrgController;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgModel;
import com.yihu.ehr.adapter.controller.OrgDataSetController;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
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

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/3/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdapterOrgTests extends BaseController {

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgDataSetController dataSetController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
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

        detailModel.setCode("CSJG1019002");
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

    @Test
    public void bTestStandardCopy() throws Exception{
        //TODO:未测试

        AdapterOrgDetailModel detailModel = new AdapterOrgDetailModel();
        detailModel.setCode("CSJG1019002");
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

    @Test
    public void cTestConvertEnvelopModel() throws Exception{

        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        String fields = "";
        String filter = "";
        int page = 1;
        int rows = 15;
        Envelop envelop = adapterOrgController.searchAdapterOrg(fields,filter,"",rows,page);

        String jsonData = objectMapper.writeValueAsString(envelop);
        List<AdapterOrgModel> adapterOrgModels = (List<AdapterOrgModel>)getEnvelopList(jsonData, new ArrayList<AdapterOrgModel>(), AdapterOrgModel.class);
        assertTrue("列表数据转换失败!",adapterOrgModels!=null);

        String orgCode = adapterOrgModels.get(0).getCode();
        envelop = adapterOrgController.getAdapterOrg(orgCode);
        jsonData = objectMapper.writeValueAsString(envelop);
        AdapterOrgDetailModel adapterOrgDetailModel = getEnvelopModel(jsonData,AdapterOrgDetailModel.class);
        assertTrue("数据转换失败!",adapterOrgDetailModel!=null);
    }
}
