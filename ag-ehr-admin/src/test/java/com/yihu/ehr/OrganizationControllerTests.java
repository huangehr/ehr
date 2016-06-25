package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.organization.controller.OrganizationController;
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

import static org.junit.Assert.assertNotEquals;


/**
 * Created by AndyCai on 2016/2/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganizationControllerTests {

    @Autowired
    private OrganizationController orgController;

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void atestOrg() throws Exception {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();
        Envelop envelop = new Envelop();
        //新增机构-------------------------1
        if (orgController.getOrg("CSJG10200FJ").isSuccessFlg()) {
            orgController.deleteOrg("CSJG10200FJ");
        }
        OrgDetailModel orgDetailModel = new OrgDetailModel();
        orgDetailModel.setOrgCode("CSJG10200FJ");
        orgDetailModel.setFullName("健康之路1");
        orgDetailModel.setShortName("健康之路1");
        orgDetailModel.setSettledWay("ThirdParty");
        orgDetailModel.setAdmin("陈");
        orgDetailModel.setTel("15959208182");
        orgDetailModel.setOrgType("ThirdPartyPlatform");
        orgDetailModel.setTel("15959208182");

        //机构地址
        GeographyModel addrCreate = new GeographyModel();
        addrCreate.setCountry("中国");
        addrCreate.setProvince("福建省");
        addrCreate.setCity("厦门市");
        addrCreate.setDistrict("思明区");
        addrCreate.setTown("金山街道");
        addrCreate.setStreet("金山");
        addrCreate.setExtra("金山西里110号");
        addrCreate.setPostalCode("364110");

        String orgCreateJson = objectMapper.writeValueAsString(orgDetailModel);
        String addrCreateJson = objectMapper.writeValueAsString(addrCreate);
        envelop = orgController.create(orgCreateJson, addrCreateJson,"","");
        assertNotEquals("机构新增失败！", envelop.isSuccessFlg(), false);

        // 新创建的orgDetailModel对象，用于下面的操作
        OrgDetailModel orgNew = (OrgDetailModel) envelop.getObj();

        //修改刚新建的机构 ----------------------2ok
        GeographyModel addrUpdate = new GeographyModel();
        addrUpdate.setCountry("中国");
        addrUpdate.setProvince("福建省");
        addrUpdate.setCity("福州市");
        addrUpdate.setDistrict("仓山区");
        addrUpdate.setTown("滨江街道");
        addrUpdate.setStreet("滨江");
        addrUpdate.setExtra("滨江西路110");
        addrUpdate.setPostalCode("364110");

        orgNew.setAdmin("程海");
        String orgUpdateJson = objectMapper.writeValueAsString(orgNew);
        String addrUpdateJson = objectMapper.writeValueAsString(addrUpdate);
        envelop = orgController.update(orgUpdateJson, addrUpdateJson,"","");
        assertNotEquals("机构更新失败！", envelop.getObj(), null);

        //列表查询（page、size）---------------------3ok
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 2;
        int page = 1;
        envelop = orgController.searchOrgs(fields,filters,sorts,size,page,"","","");
        assertNotEquals("机构列表数据获取失败！", envelop.isSuccessFlg(), false);

        //根据机构的code查询机构------------------------4ok
        envelop = orgController.getOrg("CSJG10200FJ");
        assertNotEquals("未查询到该机构！",envelop.isSuccessFlg(),false);

        //根据机构名称search机构编号列表ids（like fullName/shortName）-------------------5ok
        envelop = orgController.getIdsByName("健康之路");
        assertNotEquals("未查询到对应的ids！",envelop.isSuccessFlg(),false);


        //根据地址获取机构下拉列表 ---------------------6ok

        envelop = orgController.getOrgsByAddress("福建省", "厦门市", "海沧区");
        assertNotEquals("未查询到对应的机构列表！", envelop.isSuccessFlg(), false);


        //更新机构激活状态-----------------7ok
        //微服务需要提供2个参数。实际第二个参数没有使用

        boolean flag1 = orgController.activity("CS1113001",0);
        assertNotEquals("更新机构状态失败！",flag1,false);

        //分配机构密钥-------------------8ok

        envelop = orgController.distributeKey("CSJG10200FJ");
        assertNotEquals("密钥分配失败！",envelop.isSuccessFlg(),false);

        //判断提交的机构代码是否已经存在-----------------------9ok
        boolean flag2 = orgController.isOrgCodeExists("CSJG10200FJ");
        assertNotEquals("机构代码不存在！",envelop.isSuccessFlg(),false);

        //根据机构的code删除刚新建的机构-----------------10ok
        envelop = orgController.deleteOrg("CSJG10200FJ");
        assertNotEquals("删除机构失败！",envelop.isSuccessFlg(),false);
    }
}
