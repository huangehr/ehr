package com.yihu.ehr.org;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.SvrOrgApplication;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.org.controller.OrganizationController;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrOrgApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SvrOrgApplicationTests {

	ApplicationContext applicationContext;

    @Autowired
    private OrganizationController organizationController;

    @Test
    public void aSearchOrgs() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrOrgApplication.class).run();
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 10;
        int page = 1;
        Object org = organizationController.searchOrgs(fields,filters,sorts,size,page,null,null);
        assertTrue("查询失败！" , org != null);
    }

	@Test
	public void bDeleteOrg() throws Exception{
		String orgCode = "341321234";
		Object result = organizationController.deleteOrg(orgCode);
		assertTrue("查询失败！" , result != null);
	}


    @Test
    public void cCreate() throws Exception{
        MOrganization org = organizationController.getOrg("341321234");
        String jsonData = new ObjectMapper().writeValueAsString(org);
        Object result = organizationController.create(jsonData);
        assertTrue("查询失败！" , result != null);
    }



	@Test
	public void dUpdate() throws Exception{
        MOrganization org = organizationController.getOrg("341321234");
        org.setFullName("test");
        String jsonData = new ObjectMapper().writeValueAsString(org);
		Object result = organizationController.update(jsonData);
		assertTrue("修改失败！" , result != null);
	}

    @Test
    public void eGetIdsByName() throws Exception{
        try {
            organizationController.getIdsByName("医院");
        }catch (Exception e){
            assertTrue("修改失败！" ,true);
        }


    }

    @Test
    public void fActivity() throws Exception{
        Object result = organizationController.activity("341321234",1);
        assertTrue("修改失败！" , result != null);
    }

    @Test
	public void gGetOrgsByAddress() throws Exception{
        String province = "福建省";
        String city = "厦门市";
        String district = "集美区";
		Object org = organizationController.getOrgsByAddress(province,city,district);
		assertTrue("查询失败！" , org != null);
	}


	@Test
	public void hDistributeKey() throws Exception{
		String orgCode = "341321234";
		Object result = organizationController.distributeKey(orgCode);
		assertTrue("操作失败！" , result != null);
	}

}
