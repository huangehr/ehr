package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.ha.organization.controller.OrganizationController;
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
import static org.junit.Assert.assertNotEquals;


/**
 * Created by AndyCai on 2016/2/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganizationControllerTests {

    @Autowired
    private static OrganizationController orgController;

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void atestOrg() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();
        Envelop envelop = new Envelop();

        //新增机构-------------------------1
        OrgDetailModel orgDetailModel = new OrgDetailModel();
        orgDetailModel.setOrgCode("jinshida-weining");
        orgDetailModel.setAdmin("shanghai");
        orgDetailModel.setCity("上海");
        orgDetailModel.setDistrict("黄埔");
        orgDetailModel.setFullName("上海金仕达卫宁");
        orgDetailModel.setOrgType("ThirdPartyPlatform");
        orgDetailModel.setSettledWay("ThirdParty");
        orgDetailModel.setProvince("上海");
        orgDetailModel.setShortName("卫宁");
        orgDetailModel.setTags("电子病历");
        orgDetailModel.setTel("15959208182");

        //机构地址
//        GeographyModel addr = new GeographyModel();
//
//
//        String orgModelJson = objectMapper.writeValueAsString(orgDetailModel);
//        envelop = orgController.update(addrModelJson, orgModelJson);
//        assertNotEquals("机构5新增失败！", envelop.getObj(), null);

        // 新创建的orgDetailModel对象，用于下面的操作
        OrgDetailModel orgNew = (OrgDetailModel)envelop.getObj();


        //列表查询（page、size）
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 30;
        int page = 1;
        envelop = orgController.searchOrgs(fields,filters,sorts,size,page);
        assertNotEquals("机构列表数据获取失败！", envelop.isSuccessFlg(), false);

//        object = orgController.getOrgByCode(version,id);
//        assertNotEquals("机构信息获取失败！", object, null);
//
//        object = orgController.getOrgModel(version,id);
//        assertNotEquals("机构明细获取失败！", object, null);
//
//        object = orgController.updateOrg(version,orgModelJson);
//        assertNotEquals("机构修改失败！", object, null);
//
//        String status = "1";
//        object = orgController.activity(version,id,status);
//        assertNotEquals("机构激活失败！", object, "true");
//
//        object=orgController.getIdsByName(version,fullName);
//        assertNotEquals("机构ID获取失败！", object, null);
//
//        object = orgController.getOrgsByAddress(version,province,city);
//        assertNotEquals("根据地址获取机构信息失败！", object, null);
//
//        object = orgController.distributeKey(version,id);
//        assertNotEquals("秘钥分发失败！", object, null);
//
//        object = orgController.validationOrg(version,id);
//        assertNotEquals("机构验证失败！", object, null);
//
//        object = orgController.deleteOrg(version,id);
//        assertNotEquals("机构上传失败！", object, "false");
    }

}
