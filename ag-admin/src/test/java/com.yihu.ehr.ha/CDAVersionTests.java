package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardversion.StdVersionDetailModel;
import com.yihu.ehr.ha.std.controller.CDAVersionController;
import com.yihu.ehr.model.standard.MCDAVersion;
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
import static org.junit.Assert.assertFalse;
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
        //新增cda版本---------能新增，结果无法返回（微服务重启）
        String userCode = "wwcs";
        envelop = cdaVersionController.addVersion(userCode);
        assertTrue("新增失败！", envelop.getObj() != null);
    }

    @Test
    public void testUpdateVersion() throws Exception {
        //更新刚新增的cda版本---------ok
        String versionNew = "";
        envelop = cdaVersionController.updateVersion(versionNew, "wwcsUpdate", "syss", 1, "111111111111");
        assertTrue("更新失败！", envelop.getObj() != null);
    }

    @Test
    public void testCommitVersion() throws Exception {
        //发布刚新增的版本-------ok
        String versionNew = "";
        boolean rs2 = cdaVersionController.commitVersion(versionNew);
        assertTrue("发布版本失败！", rs2);

        //测试是否为最新已发布版本-----------ok
        boolean rs3 = cdaVersionController.isLatestVersion(versionNew);
        assertTrue("不是最新已发布的版本！", rs3);
    }

    @Test
    public void testDropCDAVersion() throws Exception {
        //删除刚发布的cda版本-----------ok
        String versionNew = "";
        boolean rs = cdaVersionController.dropCDAVersion(versionNew);
        assertTrue("删除失败！", rs);
    }

    @Test
    public void testRevertVersion() throws Exception {//----------ok
        String versionNew = "";
        boolean rs = cdaVersionController.revertVersion(versionNew);
        assertTrue("删除编辑中的版本失败！", rs);
    }

    @Test
    public void testRollbackToStage() throws Exception {//-------ok
        String version = "";
        boolean rs = cdaVersionController.rollbackToStage(version);
        assertTrue("版本回滚失败！", rs);
    }

    @Test
    public void testCheckVersionName() throws Exception {//-----------ok
        String versionName = "0.1";
        boolean rs = cdaVersionController.checkVersionName(versionName);
        assertTrue("版本名称存在", rs);
    }

    @Test
    public void testExistInStage() throws Exception {//-----------ok
        boolean rs = cdaVersionController.existInStage();
        assertTrue("不存在处于编辑状态的版本：", rs);
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
        String filters = "";
        String sorts = "";
        int size = 15;
        int page = 1;
        envelop = cdaVersionController.searchCDAVersions(fields, filters, sorts, size, page);
        assertNotEquals("没有匹配条件的cda版本！", envelop.isSuccessFlg(), false);
    }

}
