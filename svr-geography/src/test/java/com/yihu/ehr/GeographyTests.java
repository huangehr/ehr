package com.yihu.ehr;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.geography.controller.GeographyController;
import com.yihu.ehr.model.geogrephy.MGeography;
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

public class GeographyTests {

    @Autowired
    private GeographyController addressController;


	@Test
	public void bGetAddressById(){
        Object address = addressController.getAddressById("0dae000155fb8a5a3c5d6125d861119a");
		assertTrue("查询失败！" , address != null);
	}

	@Test
	public void bGetCanonicalAddress(){
		Object address = addressController.getCanonicalAddress("0dae000155fb8a5a3c5d6125d861119a");
		assertTrue("查询失败！" , address != null);
	}

	@Test
	public void bSaveAddress() throws Exception {
		Object address = addressController.getAddressById("0dae000155fb8a5a3c5d6125d861119a");
		String jsonData = new ObjectMapper().writeValueAsString(address);
		Object result = addressController.saveAddress(jsonData);
		assertTrue("查询失败！" , result != null);
	}

	@Test
	public void bSearch() throws Exception {
		String province = "福建省";
		String city = "厦门市";
		String district = "集美区";
		Object result = addressController.search(province,city,district);
		assertTrue("查询失败！" , result != null);
	}



	//根据id删除地址
	@Test
	public void jelete(){
        boolean result = addressController.delete("0dae000155fb8a533c5d6125d861096e");
		assertTrue("删除失败！" , result);
	}

	@Test
	public void jIsNullAddress() throws Exception {
		MGeography geography = addressController.getAddressById("0dae000155fb8a533c5d6125d861096e");
		String jsonData = new ObjectMapper().writeValueAsString(geography);
		Object result = addressController.isNullAddress(jsonData);
		assertTrue("操作失败！" ,result!=null);
	}



}
