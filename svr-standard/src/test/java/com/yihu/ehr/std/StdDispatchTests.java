package com.yihu.ehr.std;

import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.standard.dispatch.controller.StandardDispatchRestController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StdDispatchTests {

    @Autowired
    private StandardDispatchRestController standardDispatchRestController;

    @Test
    public void testGetSchemeInfo() throws Exception{
        String userPrivateKey = "";
        String updateVersion = "";
        String curVersion = "";
//        List ls = standardDispatchRestController.getSchemeInfo(userPrivateKey, updateVersion, curVersion);
//        assertTrue("获取数据为0！", ls != null);
    }

}
