package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardversion.StdVersionModel;
import com.yihu.ehr.ha.std.controller.CDAVersionController;
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

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by yww on 2016/2/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CDAVersionTests {

    static ApplicationContext applicationContext = new SpringApplicationBuilder()
            .web(false).sources(AgAdminApplication.class).run();

    Envelop envelop = new Envelop();

    @Autowired
    CDAVersionController cdaVersionController;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void testAddVersion() throws Exception {
        //因新增无法返回值问题，增删改查无法连起来。
    }

    @Test
    public void testCreateVersion() throws Exception {
        String filters = "author=wwcs";
        envelop = cdaVersionController.searchCDAVersions("", filters, "", 30, 1);
        if(envelop.getDetailModelList().size()==0){
            //新增cda版本---------能新增，结果无法返回（微服务重启）
            String userCode = "wwcs";
            envelop = cdaVersionController.addVersion(userCode);
            assertTrue("新增失败！", envelop.getObj() != null);
        }else{
            List<StdVersionModel> list = envelop.getDetailModelList();
            StdVersionModel detailModel = list.get(0);
            String versionNew = detailModel.getVersion();

//            //修改版本信息--正常情况不允许修改------------
//            envelop = cdaVersionController.updateVersion(detailModel.getVersion(), "V5.11", "wwcs", 1, detailModel.getBaseVersion());
//            assertTrue("更新失败！", envelop.getObj() != null);
//
//            //发布刚新增的版本-------微服务去生成版本相关的XML文件，文件有生成，测试超时，版本发布状态未更改
//            boolean rs2 = cdaVersionController.commitVersion(versionNew);
//            assertTrue("发布版本失败！", rs2);
//
//            //讲刚新建的已被发布的版本，修改为编辑状态--------------
//            boolean rs = cdaVersionController.rollbackToStage(versionNew);
//            assertTrue("版本回滚失败！", rs);
//
//            //用于测试检验是否是最新已发布版本
//            boolean rs22 = cdaVersionController.commitVersion(versionNew);
//            assertTrue("发布版本失败！", rs22);
//
//            //测试是否为最新已发布版本-----------ok
//            boolean rs3 = cdaVersionController.isLatestVersion(versionNew);
//            assertTrue("不是最新已发布的版本！", rs3);

            //删除刚发布的cda版本（drop方法可以删除编辑/非编辑状态版本）-----------ok
            boolean rs4 = cdaVersionController.dropCDAVersion(versionNew);
            assertTrue("删除失败！", rs4);
        }
    }

//    @Test
//    public void testRevertVersion() throws Exception {//----------ok
//        String versionNew = "";
//        boolean rs = cdaVersionController.revertVersion(versionNew);
//        assertTrue("删除编辑中的版本失败！", rs);
//    }

    @Test
    public void testCheckVersionName() throws Exception {//-----------ok
        String versionName = "0.1";
        boolean rs = cdaVersionController.checkVersionName(versionName);
        assertTrue("版本名称存在", rs);
    }

    @Test
    public void testGetVersion() throws Exception {//-----------ok
        String version = "56395d75b854";
        envelop = cdaVersionController.getVersion(version);
        assertTrue("获取版本失败", envelop.getObj() != null);
    }

    @Test
    public void testVersion() throws Exception {
        // 分页查询------------------------------ok
        String fields = "";
        String filters = "author=wwcs";
        String sorts = "";
        int size = 15;
        int page = 1;
        envelop = cdaVersionController.searchCDAVersions(fields, filters, sorts, size, page);
        assertNotEquals("没有匹配条件的cda版本！", envelop.isSuccessFlg(), false);
    }

    @Test
    public void cTestCommitVersion() throws  Exception{
        String version = "56395d75b854";
        Envelop envelop = cdaVersionController.commitVersion(version);
        assertTrue("发布失败!",envelop.isSuccessFlg());
    }

}
