package com.yihu.ehr.ha;

import com.eureka2.shading.codehaus.jackson.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.geography.controller.AddressController;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/1/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@EnableFeignClients
public class AddressControllerTests {

    private static String apiVersion = "v1.0";

    @Autowired
    private AddressController addressController;

    ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    Envelop envelop = new Envelop();


    @Test
    public void atestAddress() throws Exception {
        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        MGeography mGeography = new MGeography();

        mGeography.setId("sdfasdfasdfe32132123a");
        mGeography.setCountry("test_country1");
        mGeography.setProvince("test_province1");
        mGeography.setCity("test_cit1y");
        mGeography.setDistrict("test_d1istrict");
        mGeography.setTown("test_tow1n");
        mGeography.setStreet("test_str1eet");
        mGeography.setExtra("test_ext1ra");
        mGeography.setPostalCode("3630100");

        String address = addressController.saveAddress(objectMapper.writeValueAsString(mGeography));
        assertNotEquals("地址新增失败", address, null);

        String id = "0dae000155fb8a553c5d6125d8610b86";
        envelop = addressController.getAddressById(id);
        assertNotEquals("地址明细获取失败", envelop, null);

        envelop = addressController.search("test_province","test_city","test_district");
        assertNotEquals("地址搜索失败", envelop, null);

        Boolean bo = addressController.delete(id);
        assertTrue("地址删除失败",bo);

    }

    @Test
    public void btestGetAddressByLevel()
    {
//        Integer level = 1;
//        Object object = addressController.getAddressByLevel(apiVersion,level);
//        assertNotEquals("地址信息查询失败",object,null);
    }

    @Test
    public void cgetAddressDictByPid()
    {
//        Integer level = 2;
//        Object object = addressController.getAddressDictByPid(apiVersion, level);
//        assertNotEquals("地址信息查询失败",object,null);
    }

}
