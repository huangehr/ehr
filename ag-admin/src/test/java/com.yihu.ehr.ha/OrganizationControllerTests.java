package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.organization.controller.OrganizationController;
import com.yihu.ehr.ha.organization.model.OrgModel;
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

    private static String version = "v1.0";

    @Autowired
    private static OrganizationController orgController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void atestOrg() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        OrgModel orgModel = new OrgModel();
        orgModel.setOrgCode("jinshida-weining");
        orgModel.setAdmin("shanghai");
        orgModel.setCity("上海");
        orgModel.setDistrict("黄埔");
        orgModel.setFullName("上海金仕达卫宁");
        orgModel.setOrgType("ThirdPartyPlatform");
        orgModel.setSettledWay("ThirdParty");
        orgModel.setProvince("上海");
        orgModel.setShortName("卫宁");
        orgModel.setTags("电子病历");
        orgModel.setTel("15959208182");
        String orgModelJson = objectMapper.writeValueAsString(orgModel);
        Object object = orgController.updateOrg(version,orgModelJson);
        assertNotEquals("机构5新增失败！", object, null);

        String id = "";

        String orgCode = "";
        String fullName="";
        String settledWay="";
        String orgType="";
        String province="";
        String city="";
        String district="";
        int page=1;
        int rows =15;

        object = orgController.searchOrgs(orgCode,fullName,settledWay,orgType,province,city,district,page,rows);
        assertNotEquals("机构列表数据获取失败！", object, null);

        object = orgController.getOrgByCode(version,id);
        assertNotEquals("机构信息获取失败！", object, null);

        object = orgController.getOrgModel(version,id);
        assertNotEquals("机构明细获取失败！", object, null);

        object = orgController.updateOrg(version,orgModelJson);
        assertNotEquals("机构修改失败！", object, null);

        String status = "1";
        object = orgController.activity(version,id,status);
        assertNotEquals("机构激活失败！", object, "true");

        object=orgController.getIdsByName(version,fullName);
        assertNotEquals("机构ID获取失败！", object, null);

        object = orgController.getOrgsByAddress(version,province,city);
        assertNotEquals("根据地址获取机构信息失败！", object, null);

        object = orgController.distributeKey(version,id);
        assertNotEquals("秘钥分发失败！", object, null);

        object = orgController.validationOrg(version,id);
        assertNotEquals("机构验证失败！", object, null);

        object = orgController.deleteOrg(version,id);
        assertNotEquals("机构上传失败！", object, "false");
    }

}
