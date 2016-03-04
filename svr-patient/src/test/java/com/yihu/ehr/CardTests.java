//package com.yihu.ehr;
//
//import com.yihu.ehr.patient.controller.CardController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SvrPatientApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class CardTests {
//
//	ApplicationContext applicationContext;
//
//	@Autowired
//	private CardController cardController;
//
//	@Test
//	public void aSearchCardBinding() throws Exception{
//		applicationContext = new SpringApplicationBuilder()
//				.web(false).sources(SvrPatientApplication.class).run();
//		String idCardNo = "131125196306273424";
//		String number = "350123196602021234";
//		String cardType = "MediCard";
//		int size = 1;
//		int page = 10;
//		Object result = cardController.searchCardBinding(idCardNo,number,cardType,size,page,null,null);
//		assertTrue("查询失败！" , result != null);
//	}
//
//	@Test
//	public void bSearchCardUnBinding() throws Exception{
//		String number = "";
//		String cardType = "MediCard";
//		int size = 1;
//		int page = 10;
//		Object result = cardController.searchCardUnBinding(number,cardType,size,page,null,null);
//		assertTrue("查询失败！" , result != null);
//	}
//
//	@Test
//	public void bGetCard() throws Exception{
//		String id = "0dae000455efe9bb49f632070cc46d17";
//		String cardType = "OfficersCard";
//		Object result = cardController.getCard(id,cardType);
//		assertTrue("查询失败！" , result != null);
//	}
//
//	@Test
//	public void bDetachCard() throws Exception{
//		String id = "0dae000455efe9bb49f632070cc46d17";
//		String cardType = "OfficersCard";
//		Object result = cardController.detachCard(id,cardType);
//		assertTrue("查询失败！" , result != null);
//	}
//
//	@Test
//	public void battachCard() throws Exception{
//		String id = "0dae000455efe9bb49f632070cc46d17";
//		String idCardNo = "131125196306273424";
//		String cardType = "OfficersCard";
//		Object result = cardController.attachCard(id,idCardNo,cardType);
//		assertTrue("查询失败！" , result != null);
//	}
//
//
//}
