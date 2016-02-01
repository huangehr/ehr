package com.yihu.ehr.ha;

import com.yihu.ehr.ha.geography.controller.AddressController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by AndyCai on 2016/1/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@WebAppConfiguration
@EnableDiscoveryClient
@EnableFeignClients
public class AddressControllerTests {

    private static String apiVersion = "v1.0";

    @Autowired
    private AddressController addressController;

    @Test
    public void atestGetAddressByLevel()
    {
        Integer level = 1;
        Object object = addressController.getAddressByLevel(apiVersion,level);
        assertNotEquals("地址信息查询失败",object,null);
    }

    @Test
    public void bgetAddressDictByPid()
    {
        Integer level = 2;
        Object object = addressController.getAddressDictByPid(apiVersion, level);
        assertNotEquals("地址信息查询失败",object,null);
    }

    @Test
    public void csaveAddress(){
        String country = "中国";
        String province = "福建省T";
        String city = "宁德市T";
        String district = "霞浦县T";
        String town = "";
        String street = "";
        String extra = "";
        String postalCode = "072150";
        Object addessId = addressController.saveAddress(apiVersion,country,province,city,district,town,street,extra,postalCode);
        //Object obj = addressController.getAddressById("v1.0", addessId.toString());
        assertNotEquals("新增失败！", addessId , null);

        Object object = addressController.getAddressById(apiVersion,addessId.toString());

      //  assertTrue("查询失败！" , object != null);

        assertNotEquals("查询失败！", object , null);
        object = addressController.delete(apiVersion,addessId.toString());

        assertEquals("删除失败！", object,null);
    }
}
