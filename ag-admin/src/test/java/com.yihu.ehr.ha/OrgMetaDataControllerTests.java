package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgMetaDataDetailModel;
import com.yihu.ehr.ha.adapter.controller.AdapterOrgController;
import com.yihu.ehr.ha.adapter.controller.OrgDataSetController;
import com.yihu.ehr.ha.adapter.controller.OrgMetaDataController;
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
public class OrgMetaDataControllerTests {

    @Autowired
    private OrgMetaDataController metaDataController;

    @Autowired
    private OrgDataSetController orgDataSetController;

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTestMateData() throws Exception {
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

        detailModel = (AdapterOrgDetailModel) envelop.getObj();

        OrgDataSetDetailModel dataSetModel = new OrgDataSetDetailModel();
        dataSetModel.setCode("test_cms_code");
        dataSetModel.setName("test_cms_name");
        dataSetModel.setOrganization(detailModel.getCode());
        dataSetModel.setCreateDate("2016-03-01");
        dataSetModel.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");

        envelop = orgDataSetController.saveOrgDataSet(objectMapper.writeValueAsString(dataSetModel));
        assertTrue("数据集新增失败!", envelop.isSuccessFlg());

        dataSetModel = (OrgDataSetDetailModel)envelop.getObj();

        String fields = "";
        String filter = "name=test_cms_name";
        int page = 1;
        int rows = 15;
        envelop = orgDataSetController.searchAdapterOrg(fields,filter,"",rows,page);
        assertTrue("列表获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDataSetModel orgDataSetModel = objectMapper.readValue(jsonData,OrgDataSetModel.class);

        OrgMetaDataDetailModel metaDataModel = new OrgMetaDataDetailModel();
//        metaDataModel.setCode("test_cms_code");
//        metaDataModel.setName("test_cms_name");
//        metaDataModel.setOrganization(detailModel.getCode());
//        metaDataModel.setOrgDataSet(dataSetModel.getSequence());
        metaDataModel.setDescription("这是测试数据元!");
        metaDataModel.setCreateDate("2016-03-01");
        metaDataModel.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");

        envelop = metaDataController.saveOrgMetaData(objectMapper.writeValueAsString(metaDataModel));
        assertTrue("非空验证失败!",!envelop.isSuccessFlg());

        metaDataModel.setCode("test_cms_code");
        metaDataModel.setName("test_cms_name");
        metaDataModel.setOrganization(detailModel.getCode());
        metaDataModel.setOrgDataSet(dataSetModel.getSequence());

        envelop = metaDataController.saveOrgMetaData(objectMapper.writeValueAsString(metaDataModel));
        assertTrue("新增失败!",envelop.isSuccessFlg());

        fields = "";
        filter = "name=test_cms_name";
        page = 1;
        rows = 15;
        envelop = metaDataController.searchOrgMetaData(fields,filter,"",rows,page);
        assertTrue("列表信息获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        metaDataModel = objectMapper.readValue(jsonData,OrgMetaDataDetailModel.class);

        envelop = metaDataController.getOrgMetaData(metaDataModel.getId());
        assertTrue("列表信息获取失败!",envelop.isSuccessFlg() &&  envelop.getObj()!=null);

        metaDataModel = (OrgMetaDataDetailModel)envelop.getObj();
        metaDataModel.setName("test_cms_name_c");
        envelop = metaDataController.saveOrgMetaData(objectMapper.writeValueAsString(metaDataModel));
        assertTrue("修改失败!",envelop.isSuccessFlg());

        envelop = metaDataController.getMetaDataBySequence(detailModel.getCode(),metaDataModel.getSequence());
        assertTrue("数据获取失败!",envelop.isSuccessFlg()&& envelop.getObj()!=null);

        envelop = metaDataController.deleteOrgMetaDataList( String.valueOf(metaDataModel.getId()));
        assertTrue("删除失败!",envelop.isSuccessFlg());

        envelop = orgDataSetController.deleteOrgDataSet(Long.parseLong(orgDataSetModel.getId()));
        assertTrue("数据集删除失败!",envelop.isSuccessFlg());

        envelop = adapterOrgController.delAdapterOrg(detailModel.getCode());
        assertTrue("机构删除失败!",envelop.isSuccessFlg());
    }
}
