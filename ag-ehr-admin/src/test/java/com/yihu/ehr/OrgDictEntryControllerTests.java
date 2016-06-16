package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.controller.AdapterOrgController;
import com.yihu.ehr.adapter.controller.OrgDictEntryController;
import com.yihu.ehr.agModel.thirdpartystandard.*;
import com.yihu.ehr.adapter.controller.OrgDictController;
import com.yihu.ehr.util.rest.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/3/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrgDictEntryControllerTests {
    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgDictController orgDictController;

    @Autowired
    private OrgDictEntryController entryController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTestOrgDictEntry() throws Exception{

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

        OrgDictDetailModel dictModel=new OrgDictDetailModel();
        dictModel.setCode("test_cms_code");
        dictModel.setName("test_cms_name");
        dictModel.setOrganization(detailModel.getCode());
        dictModel.setDescription("这是测试字典");

        envelop = orgDictController.saveOrgDict(objectMapper.writeValueAsString(dictModel));
        assertTrue("新增字典失败!",envelop.isSuccessFlg());

        String fields = "";
        String filter = "code=test_cms_code";
        int page = 1;
        int rows = 15;
        envelop = orgDictController.searchOrgDicts(fields,filter,"",rows,page);
        assertTrue("列表信息获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDictModel orgDictModel = objectMapper.readValue(jsonData,OrgDictModel.class);

        envelop = orgDictController.getOrgDict(orgDictModel.getId());
        OrgDictDetailModel orgDictDetailModel = (OrgDictDetailModel)envelop.getObj();

        OrgDictEntryDetailModel entryDetailModel = new OrgDictEntryDetailModel();
//        entryDetailModel.setCode("test_cms_code");
//        entryDetailModel.setName("test_cms_name");
//        entryDetailModel.setOrgDict(orgDictModel.getId());
//        entryDetailModel.setOrganization(detailModel.getCode());
        entryDetailModel.setDescription("这是测试字典项");

        envelop = entryController.saveOrgDictItem(objectMapper.writeValueAsString(entryDetailModel));
        assertTrue("非空验证失败",!envelop.isSuccessFlg());

        entryDetailModel.setCode("test_cms_code");
        entryDetailModel.setName("test_cms_name");
        entryDetailModel.setOrgDict(orgDictModel.getSequence());
        entryDetailModel.setOrganization(detailModel.getCode());
        envelop = entryController.saveOrgDictItem(objectMapper.writeValueAsString(entryDetailModel));
        assertTrue("新增字典项失败",envelop.isSuccessFlg());

        envelop = entryController.searchOrgDictItems(fields, filter, "", rows, page);
        assertTrue("列表信息获取失败!",envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()==1);

        jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        OrgDictEntryModel entryModel = objectMapper.readValue(jsonData,OrgDictEntryModel.class);
        envelop = entryController.getOrgDictItem(entryModel.getId());
        assertTrue("字典项详细信息获取失败!",envelop.isSuccessFlg()&&envelop.getObj()!=null);

        entryDetailModel = (OrgDictEntryDetailModel)envelop.getObj();

        entryDetailModel.setName("test_cms_name_c");
        envelop = entryController.saveOrgDictItem(objectMapper.writeValueAsString(entryDetailModel));
        assertTrue("修改失败",envelop.isSuccessFlg());

        envelop = entryController.getOrgDictEntryBySequence(detailModel.getCode(),entryDetailModel.getSequence());
        assertTrue("数据获取失败!",envelop.isSuccessFlg()&& envelop.getObj()!=null);

        List<String> listDictEntry = entryController.getOrgDictEntry(orgDictDetailModel.getSequence(),detailModel.getCode());
        assertTrue("获取字典全部字典项失败",listDictEntry.size()>0);

        envelop = entryController.deleteOrgDictItem(String.valueOf(entryDetailModel.getId()));
        assertTrue("删除失败",envelop.isSuccessFlg());

        envelop = orgDictController.deleteOrgDict(dictModel.getId());
        assertTrue("字典删除失败!",envelop.isSuccessFlg());

        envelop=adapterOrgController.delAdapterOrg(detailModel.getCode());
        assertTrue("机构删除失败!",envelop.isSuccessFlg());
    }
}
