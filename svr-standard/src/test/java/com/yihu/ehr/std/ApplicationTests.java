package com.yihu.ehr.std;

import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.standard.cdatype.controller.CdaTypeController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {

	ApplicationContext applicationContext;

	@Autowired
	private CdaTypeController cdaTypeController;


    @Test
    public void atestGetUserModel() throws Exception{
//        applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(UserController.class).run();

        Object result = cdaTypeController.SaveCdaType("");
        assertTrue("查询失败！" , result != null);
    }


}
