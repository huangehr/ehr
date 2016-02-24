package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.geography.controller.AddressController;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.agModel.geogrephy.GeographyDictModel;
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

        mGeography.setCountry("中国test");
        mGeography.setProvince("福建省");
        mGeography.setCity("厦门市");
        mGeography.setDistrict("test_district");
        mGeography.setTown("test_town");
        mGeography.setStreet("test_street");
        mGeography.setExtra("test_extra");
        mGeography.setPostalCode("363000");

        envelop = addressController.saveAddress(objectMapper.writeValueAsString(mGeography));
        assertNotEquals("地址新增失败", envelop, null);

        String id = ((GeographyDictModel)envelop.getObj()).getId();
        envelop = addressController.getAddressById(id);
        assertNotEquals("地址明细获取失败", envelop, null);

        envelop = addressController.search("福建省","厦门市","test_district");
        assertNotEquals("地址搜索失败", envelop, null);

        envelop = addressController.delete(id);
        assertNotEquals("地址删除失败", envelop,null);

    }

    @Test
    public void btestGetAddressByLevel()
    {
//        Integer level = 1;
//        envelop = addressController.getAddressByLevel(level);
//        assertNotEquals("地址等级查询地址字典失败",envelop,null);
    }

    @Test
    public void ctestgetAddressDictByPid()
    {
//        Integer pid = 156;
//        envelop = addressController.getAddressDictByPid(pid);
//        assertNotEquals("父id查询地址字典失败",envelop,null);
    }

    @Test
    public void dtestgetCanonicalAddress(){
        String id = "0dae000155fb8a553c5d6125d8610b86";
        envelop = addressController.getCanonicalAddress(id);
        assertNotEquals("根据地址编号获取地址中文字符串全拼失败",envelop,null);
    }

    @Test
    public void ztestisNullAddress() throws Exception {
//        "{'id':'1231232434345wef','province':'河北省','city':'保定市','district':'安国市'}"
        String address = "{\"id\": \"1231232434345wef\", \"province\": \"河北省\", \"city\": \"保定市\",\"district\": \"安国市\"}";

        envelop = addressController.isNullAddress(address);
        assertNotEquals("判断是否是个地址 失败",envelop,null);
    }
}
