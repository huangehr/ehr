package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.dict.SystemDictEntryModel;
import com.yihu.ehr.agModel.dict.SystemDictModel;
import com.yihu.ehr.systemDict.controller.ConventionalDictEntryController;
import com.yihu.ehr.systemDict.controller.SystemDictController;
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
 * Created by AndyCai on 2016/1/19.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SystemDictControllerTests {

    @Autowired
    private ConventionalDictEntryController systemDictController;

    @Autowired
    private SystemDictController sysDict;

    @Autowired
    private ObjectMapper objectMapper;

    private String version = "v1.0";

    ApplicationContext applicationContext= new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

    Envelop envelop = new Envelop();

    @Test
    public void atestSystemDict() throws Exception{

        SystemDictModel systemDictModel = new SystemDictModel();
        systemDictModel.setName("test_dict_cms");
        systemDictModel.setReference("");
        systemDictModel.setAuthorId("0dae0003567a0d909b10c56a3220efdf");
        envelop = sysDict.createDictionary(objectMapper.writeValueAsString(systemDictModel));
        assertNotEquals("字典新增失败", envelop, null);

        //  ((MSystemDict) envelop.getObj()).getId()
        envelop = sysDict.getDictionary(1);
        assertNotEquals("字典明细获取失败", envelop, null);

        String fields = "";//"id,name,phonetic_code,reference,author,create_date";
        String filter = "name=11";
        String sorts = "";
        int page = 1;
        int rows = 15;

        envelop = sysDict.getDictionaries(fields, filter, sorts,rows,page);
        assertNotEquals("字典列表获取失败", envelop, null);

        systemDictModel.setName("test_dict_cms_c");
        systemDictModel.setId(230);
        systemDictModel.setPhoneticCode("TEST_DICT_CMS_C");
        systemDictModel.setCreateDate("2016-03-22");

        envelop = sysDict.updateDictionary(objectMapper.writeValueAsString(systemDictModel));
        assertTrue("字典修改失败", ((SystemDictModel) envelop.getObj()).getName().equals("test_dict_cms_c"));

        SystemDictEntryModel systemDictEntryModel = new SystemDictEntryModel();
        systemDictEntryModel.setDictId(systemDictModel.getId());
        systemDictEntryModel.setCode( "test_item_cms");
        systemDictEntryModel.setValue( "test_value_cms");
        systemDictEntryModel.setSort(1);
        systemDictEntryModel.setCatalog("");
        envelop = sysDict.createDictEntry(objectMapper.writeValueAsString(systemDictEntryModel));
        assertNotEquals("字典项新增失败", envelop, null);

        systemDictEntryModel.setValue("test_value_cms_c");
        envelop = sysDict.updateDictEntry(objectMapper.writeValueAsString(systemDictEntryModel));
        assertTrue("字典项修改失败", ((SystemDictEntryModel) envelop.getObj()).getValue().equals("test_value_cms_c"));

        envelop = sysDict.getDictEntry(46,"3");
        assertNotEquals("字典项获取失败",envelop,null);

//        envelop = sysDict.getDictEntries(systemDictModel.getId(),"", 1, 15);
//        assertNotEquals("字典项列表获取失败", envelop, null);

        envelop = sysDict.deleteDictEntry(systemDictModel.getId(),systemDictEntryModel.getCode());
        assertTrue("字典项删除失败", envelop.isSuccessFlg());

        envelop = sysDict.deleteDictionary(systemDictModel.getId());
        assertTrue("字典删除失败", envelop.isSuccessFlg());
    }

    @Test
    public void btestgetOrgType() throws Exception {

        envelop = systemDictController.getAppCatalog("ChildHealth");
        assertNotEquals("APP类别字典获取失败", envelop, null);

        envelop = systemDictController.getAppStatus("Approved");
        assertNotEquals("APP状态字典获取失败", envelop, null);

        /************************************/
        envelop = systemDictController.getGender("Male");
        assertNotEquals("性别字典项获取失败", envelop, null);

        envelop = systemDictController.getMartialStatus("10");
        assertNotEquals("婚姻状态字典项获取失败", envelop, null);

        envelop = systemDictController.getNation("1");
        assertNotEquals("国家字典项获取失败", envelop, null);

        envelop = systemDictController.getResidenceType("permanent");
        assertNotEquals("人口居住类型字典项获取失败", envelop, null);

        envelop = systemDictController.getOrgType("Govement");
        assertNotEquals("机构类别字典获取失败", envelop, null);

        envelop = systemDictController.getSettledWay("Direct");
        assertNotEquals("结算方式字典获取失败", envelop, null);

        envelop = systemDictController.getCardStatus("Cancelled");
        assertNotEquals("卡状态字典项获取失败", envelop, null);

        envelop = systemDictController.getCardType("AICard");
        assertNotEquals("卡类别字典项获取失败", envelop, null);

//        envelop = systemDictController.getRequestState("10");
//        assertNotEquals("家庭成员请求消息状态字典项获取失败", envelop, null);

        envelop = systemDictController.getKeyType("Org");
        assertNotEquals("密钥类型字典项获取失败", envelop, null);

        envelop = systemDictController.getMedicalRole("Doctor");
        assertNotEquals("医疗角色字典项获取失败", envelop, null);

        envelop = systemDictController.getUserRole("Admin");
        assertNotEquals("用户角色字典项获取失败", envelop, null);

        envelop = systemDictController.getUserType("Heater");
        assertNotEquals("用户类型字典项获取失败", envelop, null);

        envelop = systemDictController.getLoginAddress("baidu_apikey");
        assertNotEquals("连接的第三方应用字典项获取失败", envelop, null);

        envelop = systemDictController.getYesNo(false);
        assertNotEquals("是否字典项获取失败", envelop, null);

        envelop = systemDictController.getAdapterType("1");
        assertNotEquals("适配类型字典项获取失败", envelop, null);

        envelop = systemDictController.getStdSourceType("1");
        assertNotEquals("标准来源字典项获取失败", envelop, null);

        String[] strings = new String[]{"1","2"};

        envelop = systemDictController.getStdSourceTypeList(strings);
        assertNotEquals("获取标准来源类型字典项失败", envelop, null);

        envelop = systemDictController.getUserTypeList();
        assertNotEquals("获取用户类型字典项失败", envelop, null);

        envelop = systemDictController.getTagsList();
        assertNotEquals("获取标签字典项失败", envelop, null);

//        envelop = sysDict.isAppNameExists("ChildHealth");
//        assertTrue("获取标签字典项失败", envelop.isSuccessFlg());

    }

    @Test
    public void cTestSearchEntry() throws Exception{

        SystemDictModel systemDictModel = new SystemDictModel();
        systemDictModel.setName("test_cms_dict");
        systemDictModel.setAuthorId("0dae0003561cc415c72d9111e8cb88aa");
        systemDictModel.setCreateDate("2016-03-22");

        envelop = sysDict.createDictionary(objectMapper.writeValueAsString(systemDictModel));
        assertTrue("新增失败!",envelop.isSuccessFlg()&&envelop.getObj()!=null);

        systemDictModel = (SystemDictModel)envelop.getObj();
        envelop = sysDict.deleteDictionary(systemDictModel.getId());
        assertTrue("删除失败!",envelop.isSuccessFlg());

        String fields = "";
        String filter = "";
        int page = 1;
        int rows = 15;
        Envelop envelop = sysDict.getDictionaries(fields,filter,"",15,1);
        assertTrue("数据获取失败!",envelop.getDetailModelList().size()>0);

        String jsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(0));
        systemDictModel = objectMapper.readValue(jsonData,SystemDictModel.class);

        long dictId = systemDictModel.getId();
        filter="dictId="+dictId;
        envelop = sysDict.getDictEntries(fields,filter,"",15,1);
        assertTrue("字典项数据获取失败!",envelop.getDetailModelList().size()>0);
    }

}
