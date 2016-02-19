package com.yihu.ehr;

import com.yihu.ehr.geography.controller.GeographyDictController;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrGeographyApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional(transactionManager="transactionManager")

public class GeographyDictTests {

    @Autowired
    private GeographyDictController geographyDictController;

	//根据level查询
	@Test
	public void cGetAddressByLevel(){
		Integer level=3;
        Object addressDictList = geographyDictController.getAddressByLevel(level);
        assertTrue("查询失败！" , addressDictList != null);
	}

	@Test
	public void fGetAddressDictByPid(){
        Object addressDictList = geographyDictController.getAddressDictByPid(0);
		assertTrue("查询失败！" , addressDictList != null);
	}


}
