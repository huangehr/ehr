//package com.yihu.ehr;
//
//import com.yihu.ehr.geography.controller.GeographyController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.orm.hibernate4.HibernateTransactionManager;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SvrGeographyApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional(transactionManager="transactionManager")
//
//public class SvrGeographyApplicationTests {
//
//	String apiVersion = "v1.0";
//	static Object addessId = "";
//
//	@Autowired
//	private HibernateTransactionManager transactionManager;
//
//    @Autowired
//    private GeographyController addressController;
//
//	//新增一条记录
//	@Test
//	public void aSaveAddress()  {
//		String country = "中国";
//		String province = "北京市";
//		String city = "北京市";
//		String district = "东城区";
//		String town = "";
//		String street = "";
//		String extra = "";
//		String postalCode = "352000";
//		addessId = addressController.saveAddress(apiVersion,country,province,city,district,town,street,extra,postalCode);
//		assertTrue("查询失败！" , addessId != null);
//	}
//
//	//根pid查询
//	@Test
//	public void bgetAddressById(){
//		//Object address = addressController.getAddressById(apiVersion, addessId.toString());
//        Object address = addressController.getAddressById(apiVersion, "0dae000155fb8a513c5d6125d8610792");
//		assertTrue("查询失败！" , address != null);
//	}
//
//	//根据pid查询 厦门市
//	@Test
//	public void cgetAddressDictByPid(){
//		Integer pid = 350000;
//		Object addressDictList = addressController.getAddressDictByPid(apiVersion,pid);
//		assertTrue("查询失败！" , addressDictList != null);
//	}
//
//
//	//根据level查询
//	@Test
//	public void cGetAddressByLevel(){
//		Integer level=3;
//        Object addressDictList = addressController.getAddressByLevel(apiVersion,level);
//        assertTrue("查询失败！" , addressDictList != null);
//	}
//
//
//	//根据id获取地址名称
//	@Test
//	public void fCanonicalAddress(){
//		//Object addressDictList = addressController.getCanonicalAddress(apiVersion,addessId.toString());
//        Object addressDictList = addressController.getCanonicalAddress(apiVersion,"0dae000155fb8a533c5d6125d861096e");
//		assertTrue("查询失败！" , addressDictList != null);
//	}
//
//
//	//根据省市县查询地址
//	@Test
//	public void hsearch(){
//		String province = "北京市";
//		String city = "北京市";
//		String district = "东城区";
//		Object addressDictList = addressController.search(apiVersion,province,city,district);
//		assertTrue("查询失败！" , addressDictList != null);
//	}
//
//	//根据id删除地址
//	@Test
//	public void jdelete(){
//		//addressController.delete(apiVersion,addessId.toString());
//        String result = addressController.delete(apiVersion,"0dae000155fb8a533c5d6125d861096e").toString();
//		assertTrue("删除失败！" , "success".equals(result));
//	}
//
//}
