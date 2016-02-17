package com.yihu.ehr.ha;

/**
 * Created by AndyCai on 2016/1/19.
 */

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AgAdminApplication.class)
//@WebAppConfiguration
//@EnableDiscoveryClient
//@EnableFeignClients
public class SystemDictControllerTests {

//    @Autowired
//    private ConventionalDictEntryController systemDictController;
//
//    @Autowired
//    private SystemDictController sysDict;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private String version = "v1.0";
//
//    ApplicationContext applicationContext;
//
//    @Test
//    public void atestSystemDict() throws Exception{
//
//        applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(AgAdminApplication.class).run();
//
//        String name = "test_dict_cms";
//        String ref="";
//        String userId = "0dae0003567a0d909b10c56a3220efdf";
//        Object object =null;// sysDict.createDict(version, name, ref, userId);
//        assertNotEquals("字典新增失败", object, null);
//
//        String userJson = objectMapper.writeValueAsString(object);
//
//        //object = sysDict.getDictsByName(version,"","",1,15);
//        assertNotEquals("字典列表获取失败", object, null);
//
//        String dictId = "";
//        name = "test_dict_cms_c";
//
//        //object = sysDict.updateDict(version,Long.parseLong(dictId),name);
//        assertNotEquals("字典修改失败", object, null);
//
//        String itemCode = "test_item_cms";
//        String itemValue = "test_value_cms";
//        Integer sort = 1;
//        String catalog = "";
//        //object = sysDict.createDictEntry(version,Long.parseLong(dictId),itemCode,itemValue,sort,catalog);
//        assertNotEquals("字典项新增失败", object, null);
//
//        itemValue = "test_value_cms_c";
//       // object = sysDict.updateDictEntry(version, Long.parseLong(dictId), itemCode, itemValue, sort, catalog);
//        assertNotEquals("字典项修改失败", object, null);
//
//        //object = sysDict.getDictEntrysByDictId(version,Long.parseLong(dictId),1,15);
//        assertNotEquals("字典项列表获取失败", object, null);
//
//        //object = sysDict.getDictEntrysByDictId(version,Long.parseLong(dictId));
//        assertNotEquals("获取全部字典项失败", object, null);
//
//        //object = sysDict.deleteDictEntry(version,Long.parseLong(dictId),itemCode);
//        assertNotEquals("字典项删除失败", object, "true");
//
//        //object = sysDict.deleteDict(version,Long.parseLong(dictId));
//        assertNotEquals("字典删除失败", object, "true");
//    }
//
//    @Test
//    public void getOrgType() throws Exception {
//
//        Object baseDict = systemDictController.getOrgType("Govement");
//        assertNotEquals("机构类别字典获取失败", baseDict, null);
//
//    }
//
//    @Test
//    public void getSettledWay() throws Exception {
//
//        Object baseDict = systemDictController.getSettledWay("Direct");
//        assertNotEquals("结算方式字典获取失败", baseDict, null);
//
//    }
//
//    @Test
//    public void getAppCatalog() throws Exception {
//
//        Object baseDict = systemDictController.getAppCatalog("ChildHealth");
//        assertNotEquals("APP类别字典获取失败", baseDict, null);
//
//    }
//
//    @Test
//    public void getAppStatus() throws Exception {
//
//        Object baseDict = systemDictController.getAppStatus("Approved");
//        assertNotEquals("APP状态字典获取失败", baseDict, null);
//
//    }
}
