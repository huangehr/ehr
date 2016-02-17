package com.yihu.ehr.ha;

/**
 * Created by AndyCai on 2016/1/19.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AgAdminApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppControllerTests {

//    private static String version = "v1.0";
//
//    public MApp mApp = null;
//
//    ApplicationContext applicationContext;
//
//    @Autowired
//    private AppController appController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void atestCreateApp() throws Exception{
//
//        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();
//        //新增测试
//        Object object = appController.createApp("测试APP", "ChildHealth", "fsdadfs", "这是用于测试的数据", "1", "0dae0003561cc415c72d9111e8cb88aa");
//        try {
//
//            String aaa = objectMapper.writeValueAsString(object);
//            mApp = objectMapper.readValue(aaa, MApp.class);
//
//        } catch (Exception ex) {
//        }
//        assertNotEquals("APP新增失败", mApp, null);
//
//        String appId = "";
//        String appName = "";
//        String catalog = "";
//        String status = "";
//        int page = 1;
//        int rows = 15;
//      //  object = appController.getApps( "", appName, catalog, status, page, rows);
//      //  assertNotEquals("机构类别字典获取失败", object, null);
//
//        String id = mApp.getId();
//        String name = mApp.getName();
//        String secret = mApp.getSecret();
//        String url = mApp.getUrl();
//        catalog = mApp.getCatalog().getCode();
//        status = mApp.getStatus().getCode();
//        String description = mApp.getDescription();
//        String tags = mApp.getTags().toString();
//        object = appController.updateApp( id, name, catalog, status, url, description, tags);
//        //success
//        assertNotEquals("APP修改失败", object,null);
//
//        object = appController.getApp( id);
//        assertNotEquals("APP明细获取失败", object, null);
//
////        object = appController.checkStatus(version, id, "WaitingForApprove");
////        assertTrue("APP状态修改失败", object.toString().equals("true"));
//
//        object = appController.deleteApp( id);
//        assertTrue("APP删除失败", object.toString().equals("true"));
//    }

}
