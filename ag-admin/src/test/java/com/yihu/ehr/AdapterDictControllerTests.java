package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.controller.*;
import com.yihu.ehr.agModel.adapter.AdapterDictModel;
import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
import com.yihu.ehr.agModel.adapter.AdapterRelationshipModel;
import com.yihu.ehr.agModel.thirdpartystandard.*;
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
    private OrgDictController orgDictController;

    @Autowired
    private OrgDictEntryController entryController;

    @Autowired
    private OrgAdapterPlanController planController;

    @Autowired
    private AdapterDictController dictController;

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

        OrgDictDetailModel dictModel=new OrgDictDetailModel();
        dictModel.setCode("test_cms_code");
        dictModel.setName("test_cms_name");
        dictModel.setOrganization(orgDetailModel.getCode());
        dictModel.setDescription("这是测试字典");

        envelop = orgDictController.saveOrgDict(objectMapper.writeValueAsString(dictModel));
        assertTrue("新增失败!",envelop.isSuccessFlg());

        String fields = "";
        String filter = "code=test_cms_code";
        int page = 1;
        int rows = 15;
        envelop = orgDictController.searchOrgDicts(fields,filter,"",rows,page);
        assertTrue("列表信息获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDictModel orgDictModel = objectMapper.readValue(jsonData,OrgDictModel.class);

        OrgDictEntryDetailModel entryDetailModel = new OrgDictEntryDetailModel();
        entryDetailModel.setCode("test_cms_code");
        entryDetailModel.setName("test_cms_name");
        entryDetailModel.setOrgDict(orgDictModel.getId());
        entryDetailModel.setOrganization(orgDetailModel.getCode());
        entryDetailModel.setDescription("这是测试字典项");

        envelop = entryController.saveOrgDictItem(objectMapper.writeValueAsString(entryDetailModel));
        assertTrue("新增字典项失败",envelop.isSuccessFlg());

        envelop = entryController.searchOrgDictItems(fields, filter, "", rows, page);
        assertTrue("列表信息获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDictEntryModel entryModel = objectMapper.readValue(jsonData,OrgDictEntryModel.class);

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

        AdapterDictModel adapterDictModel = new AdapterDictModel();
        adapterDictModel.setAdapterPlanId(adapterPlanModel.getId());
        adapterDictModel.setDictId(Long.parseLong("2"));
        adapterDictModel.setDictEntryId(Long.parseLong("21198"));

        envelop = dictController.createAdapterDictEntry(objectMapper.writeValueAsString(adapterDictModel));
        assertTrue("字典适配新增失败!", envelop.isSuccessFlg());

        envelop = dictController.searchDicts(adapterPlanModel.getId(), "", "", "", 15, 1);
        assertTrue("适配字典查询失败!", envelop.isSuccessFlg() && envelop.getDetailModelList().size() == 1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        AdapterRelationshipModel adapterRelationshipModel = objectMapper.readValue(jsonData, AdapterRelationshipModel.class);

        envelop = dictController.getAdapterDictEntryByDictId(adapterPlanModel.getId(),adapterRelationshipModel.getId(),"","","",15,1);
        assertTrue("适配字典项查询失败!", envelop.isSuccessFlg() && envelop.getDetailModelList().size() == 1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        adapterDictModel = objectMapper.readValue(jsonData, AdapterDictModel.class);

        envelop = dictController.getAdapterDictEntry(adapterDictModel.getId());
        assertTrue("适配明细获取失败!", envelop.isSuccessFlg() && envelop.getObj() != null);

        adapterDictModel = (AdapterDictModel)envelop.getObj();
        adapterDictModel.setOrgDictSeq(Long.parseLong(String.valueOf(orgDictModel.getSequence())));
        adapterDictModel.setOrgDictEntrySeq(Long.parseLong(String.valueOf(entryModel.getSequence())));
        envelop = dictController.updateAdapterDictEntry(objectMapper.writeValueAsString(adapterDictModel));
        assertTrue("适配信息修改失败!", envelop.isSuccessFlg());

        envelop = dictController.delAdapterDictEntry(String.valueOf(adapterDictModel.getId()));
        assertTrue("适配信息删除失败!", envelop.isSuccessFlg());

        /*********************************删除测试数据 Start***********************************/
        envelop = entryController.deleteOrgDictItem(String.valueOf(entryModel.getId()));
        assertTrue("字典项删除失败",envelop.isSuccessFlg());

        envelop = orgDictController.deleteOrgDict(orgDictModel.getId());
        assertTrue("字典删除失败!",envelop.isSuccessFlg());

        envelop=adapterOrgController.delAdapterOrg(orgDetailModel.getCode());
        assertTrue("机构删除失败!",envelop.isSuccessFlg());

        envelop = planController.delAdapterPlan(String.valueOf(adapterPlanModel.getId()));
        assertTrue("删除失败!",envelop.isSuccessFlg());
        /*********************************删除测试数据 end ************************************/
    }
}
