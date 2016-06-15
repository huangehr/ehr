package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.organization.controller.OrganizationController;
import com.yihu.ehr.security.controller.SecurityController;
import com.yihu.ehr.users.controller.UserController;
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
import static org.junit.Assert.assertTrue;

/**
 * Created by wq on 2016/2/24.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserSecurityControllerTests {


    @Autowired
    private OrganizationController orgController;

    @Autowired
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityController securityController;

    ApplicationContext applicationContext;

    @Test
    public void atestSecurity() throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        /*******************************用户秘钥操作 Start **********************************/
        UserDetailModel userModel = new UserDetailModel();
        userModel.setGender("Male");
        userModel.setMartialStatus("10");
        userModel.setUserType("Nurse");
        userModel.setOrganization("341321234");
        userModel.setMajor("");
        userModel.setLoginCode("test_cms");
        userModel.setRealName("test_cms");
        userModel.setIdCardNo("111111111111111111");
        userModel.setEmail("555@qq.com");
        userModel.setTelephone("11111111111");
        String userModelJson = objectMapper.writeValueAsString(userModel);
        Envelop envelop = userController.createUser(userModelJson);
        assertTrue("用户新增失败", envelop.isSuccessFlg());

        userModel = (UserDetailModel)envelop.getObj();

        //用户Security
        envelop = securityController.createSecurityByUserId(userModel.getId());
        assertNotEquals("用户创建Security失败",envelop,null);

        envelop = securityController.getUserSecurityByUserId(userModel.getId());
        assertNotEquals("查询用户Security失败", envelop, null);

        envelop = securityController.deleteUserKeyByUserId(userModel.getId());
        assertNotEquals("删除用户Security失败",envelop,null);


        envelop = userController.deleteUser(userModel.getId());
        assertTrue("用户删除失败",envelop.isSuccessFlg());
        /*******************************用户秘钥操作 End ************************************/

        /*******************************机构秘钥操作 Start **********************************/
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

        //机构Security
        envelop = securityController.createSecurityByOrgCode(orgDetailModel.getOrgCode());
        assertNotEquals("机构创建Security失败",envelop,null);

        envelop = securityController.getUserSecurityByOrgCode(orgDetailModel.getOrgCode());
        assertTrue("公私钥信息获取失败!",envelop.isSuccessFlg() && envelop.getObj()!=null);

        envelop = securityController.deleteKeyByOrgCode(orgDetailModel.getOrgCode());
        assertTrue("机构秘钥信息删除失败!",envelop.isSuccessFlg());

        envelop = orgController.deleteOrg(orgDetailModel.getOrgCode());
        assertTrue("机构删除失败!",envelop.isSuccessFlg());
        /*******************************机构秘钥操作 End ************************************/


    }

}
