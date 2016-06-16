package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.controller.AdapterOrgController;
import com.yihu.ehr.adapter.controller.OrgDictController;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictModel;
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
public class OrgDictControllerTests {

    @Autowired
    private AdapterOrgController adapterOrgController;

    @Autowired
    private OrgDictController orgDictController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void aTestOrgDict() throws Exception{

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
//        dictModel.setCode("test_cms_code");
//        dictModel.setName("test_cms_name");
   //     dictModel.setOrganization(detailModel.getCode());
        dictModel.setDescription("这是测试字典");


        envelop = orgDictController.saveOrgDict(objectMapper.writeValueAsString(dictModel));
        assertTrue("非空校验失败!",!envelop.isSuccessFlg());

        dictModel.setCode("test_cms_code");
        dictModel.setName("test_cms_name");
        dictModel.setOrganization(detailModel.getCode());

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

        envelop = orgDictController.getOrgDict(orgDictModel.getId());
        assertTrue("明细获取失败",envelop.isSuccessFlg() && envelop.getObj()!=null);

        dictModel = (OrgDictDetailModel)envelop.getObj();
        dictModel.setName("test_cms_name_c");
        envelop = orgDictController.saveOrgDict(objectMapper.writeValueAsString(dictModel));
        assertTrue("修改失败!",envelop.isSuccessFlg());

        envelop = orgDictController.getOrgDictBySequence(detailModel.getCode(),dictModel.getSequence());
        assertTrue("数据获取失败!",envelop.isSuccessFlg()&&envelop.getObj()!=null);

        List<String> listDict = orgDictController.getOrgDict(detailModel.getCode());
        assertTrue("获取机构全部字典失败!",listDict.size()>0);

        envelop = orgDictController.deleteOrgDict(dictModel.getId());
        assertTrue("删除失败!",envelop.isSuccessFlg());

        envelop=adapterOrgController.delAdapterOrg(detailModel.getCode());
        assertTrue("机构删除失败!",envelop.isSuccessFlg());
    }
}
