package com.yihu.ehr;

import com.yihu.ehr.address.controller.AddressController;
import com.yihu.ehr.address.service.Address;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrAddressApplication.class)
public class SvrAddressApplicationTests {

	String apiVersion = "v1.0";
	Object addessId = "";


    @Autowired
    private AddressController addressController;

	//新增一条记录
	@Test
	//@Rollback(false)
	public void aSaveAddress(){
		String country = "中国";
		String province = "福建省T";
		String city = "宁德市T";
		String district = "霞浦县T";
		String town = "";
		String street = "";
		String extra = "";
		String postalCode = "072150";
		addessId = addressController.saveAddress(apiVersion,country,province,city,district,town,street,extra,postalCode);
		//Object obj = addressController.getAddressById("v1.0", addessId.toString());
		assertTrue("查询失败！" , addessId != null);
	}


	//根据level查询
	@Test
	public void bGetAddressByLevel(){
		Integer level=3;
		 Object addressDictList = addressController.getAddressByLevel(apiVersion,level);
        assertTrue("查询失败！" , addressDictList != null);
	}

	//根据pid查询 厦门市
	@Test
	public void cgetAddressDictByPid(){
		Integer pid = 350000;
		Object addressDictList = addressController.getAddressDictByPid(apiVersion,pid);
		assertTrue("查询失败！" , addressDictList != null);
	}

	//根pid查询
	@Test
	public void egetAddressById(){
		Object address = addressController.getAddressById(apiVersion,addessId.toString());
		assertTrue("查询失败！" , address != null);
	}

	//根据id获取地址名称
	@Test
	public void fCanonicalAddress(){
		Object addressDictList = addressController.getCanonicalAddress(apiVersion,addessId.toString());
		assertTrue("查询失败！" , addressDictList != null);
	}


	//根据省市县查询地址
	@Test
	public void hsearch(){
		String province = "福建省T";
		String city = "宁德市T";
		String district = "霞浦县T";
		Object addressDictList = addressController.search(apiVersion,province,city,district);
		assertTrue("查询失败！" , addressDictList != null);
	}

	//根据id删除地址
	@Test
	public void jdelete(){
		addressController.delete(apiVersion,addessId.toString());
		Object address = addressController.getAddressById(apiVersion,addessId.toString());
		assertTrue("查询失败！" , address == null);
	}



}
