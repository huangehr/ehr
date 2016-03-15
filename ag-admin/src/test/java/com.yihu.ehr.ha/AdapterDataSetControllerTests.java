package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.adapter.AdapterDataSetModel;
import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
import com.yihu.ehr.agModel.adapter.AdapterRelationshipModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgMetaDataDetailModel;
import com.yihu.ehr.ha.adapter.controller.*;
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

    @Test
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
        assertTrue("机构数据集新增失败!",envelop.isSuccessFlg());

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
        assertTrue("机构数据元新增失败!",envelop.isSuccessFlg());


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

        /******************************适配方案新增 End ***************************************/

        try {

            AdapterDataSetModel adapterDataSetModel = new AdapterDataSetModel();
            adapterDataSetModel.setAdapterPlanId(adapterPlanModel.getId());
            adapterDataSetModel.setDataSetId(Long.parseLong("2"));
            adapterDataSetModel.setMetaDataId(Long.parseLong("11"));
            envelop = dataSetController.addAdapterMetaData(objectMapper.writeValueAsString(adapterDataSetModel));
            assertTrue("数据元适配新增失败!", envelop.isSuccessFlg());

            envelop = dataSetController.searchAdapterDataSet(adapterPlanModel.getId(), "", "", "", 15, 1);
            assertTrue("适配数据集查询失败!", envelop.isSuccessFlg() && envelop.getDetailModelList().size() == 1);

            jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
            AdapterRelationshipModel adapterRelationshipModel = objectMapper.readValue(jsonData, AdapterRelationshipModel.class);

            envelop = dataSetController.searchAdapterMetaData(adapterPlanModel.getId(), adapterRelationshipModel.getId(), "", "", "", 15, 1);
            assertTrue("适配数据元查询失败!", envelop.isSuccessFlg() && envelop.getDetailModelList().size() == 1);

            jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
            adapterDataSetModel = objectMapper.readValue(jsonData, AdapterDataSetModel.class);

            envelop = dataSetController.getAdapterMetaDataById(adapterDataSetModel.getId());
            assertTrue("适配明细获取失败!", envelop.isSuccessFlg() && envelop.getObj() != null);

            adapterDataSetModel = (AdapterDataSetModel) envelop.getObj();
            adapterDataSetModel.setOrgDataSetSeq(Long.parseLong(String.valueOf(dataSetModel.getSequence())));
            adapterDataSetModel.setOrgMetaDataSeq(Long.parseLong(String.valueOf(metaDataModel.getSequence())));
            envelop = dataSetController.updateAdapterMetaData(objectMapper.writeValueAsString(adapterDataSetModel));
            assertTrue("适配信息修改失败!", envelop.isSuccessFlg());

            envelop = dataSetController.deleteAdapterMetaData(String.valueOf(adapterDataSetModel.getId()));
            assertTrue("适配信息删除失败!", envelop.isSuccessFlg());

        }
        catch (Exception ex)
        {
            /*********************************删除初始化数据 Start **************************************/
            bTestDeleteData(String.valueOf(adapterPlanModel.getId()), String.valueOf(metaDataModel.getId()), Long.parseLong(orgDataSetModel.getId()), orgDetailModel.getCode());
            /*********************************删除初始化数据 End ****************************************/
        }
        /*********************************删除初始化数据 Start **************************************/
        bTestDeleteData(String.valueOf(adapterPlanModel.getId()), String.valueOf(metaDataModel.getId()),  Long.parseLong(orgDataSetModel.getId()), orgDetailModel.getCode());
        /*********************************删除初始化数据 End ****************************************/
    }

    public void bTestDeleteData(String planId,String metaId,long dataSetId,String orgCode) throws Exception
    {
        /******************************适配方案删除 Start ***************************************/
        Envelop envelop = planController.delAdapterPlan(planId);
        assertTrue("删除失败!",envelop.isSuccessFlg());
        /******************************适配方案删除 End ***************************************/

        /**************************删除机构标准信息 Start ********************************/
        envelop = metaDataController.deleteOrgMetaDataList( metaId);
        assertTrue("删除失败!",envelop.isSuccessFlg());

        envelop = orgDataSetController.deleteOrgDataSet(dataSetId);
        assertTrue("数据集删除失败!",envelop.isSuccessFlg());

        envelop = adapterOrgController.delAdapterOrg(orgCode);
        assertTrue("机构删除失败!",envelop.isSuccessFlg());
        /**************************删除机构标准信息 End ********************************/
    }
}
