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
    public void testAddVersion() throws Exception {//------------ok
        //新增标准版本--------ok
        String v = "56d6ab1fc32b";
        envelop = cdaVersionController.getVersion(v);
        if (envelop.isSuccessFlg()) {
            boolean flag8 = cdaVersionController.dropCDAVersion(v);
            assertTrue("删除失败！", flag8);
        }
        String userCode = "wwcs";
        envelop = cdaVersionController.addVersion(userCode);
        assertTrue("新增失败！", envelop.getObj() != null);

        StdVersionDetailModel stdVersionDetailModel = (StdVersionDetailModel) envelop.getObj();
        String versionForTest = stdVersionDetailModel.getBaseVersion();
        //String versionForTest = "56d6a26b4360";
        //修改刚新建的标准版本--------
        String versionName = "V4.1";
        String userCodeNew = "wwcsUpdate";
        int inStage = 1;//-----发布
        //String baseVersion = stdVersionDetailModel.getBaseVersion();
        envelop = cdaVersionController.updateVersion(versionForTest, versionName, userCodeNew, inStage, "56d3f3b33656");
        assertTrue("更新失败！", envelop.isSuccessFlg());

        //发布刚新建的标准版本-------
        boolean flag2 = cdaVersionController.commitVersion(versionForTest);
        assertTrue("发布版本失败！", flag2);

        //删除刚新建的标准版本------
        boolean flag3 = cdaVersionController.dropCDAVersion(versionForTest);
        assertTrue("删除失败！", flag3);

    }

    @Test
    public void testCreateVersion() throws Exception {//ok
        String userCode = "user1";
        envelop = cdaVersionController.addVersion(userCode);
        assertTrue("新增失败！", envelop.getObj() != null);
    }

    //    @Test
    public void testIsLatestVersions() throws Exception {//-----------ok
        String version = "56d3f3b33656";
        boolean rs = cdaVersionController.isLatestVersion(version);
        assertTrue("不是最新已发布的版本！", rs);
    }

    @Test
    public void testDropCDAVersion() throws Exception {//-----------ok
        String version = "56cc4441886d";
        boolean rs = cdaVersionController.dropCDAVersion(version);
        assertTrue("删除失败！", rs);
    }

    @Test
    public void testRevertVersion() throws Exception {//----------ok
        String version = "56d6abc5c32d";
        boolean rs = cdaVersionController.revertVersion(version);
        assertTrue("删除编辑中的版本失败！", rs);
    }

    @Test
    public void testCommitVersion() throws Exception {//-------ok
        String version = "56d690273179";
        boolean rs = cdaVersionController.commitVersion(version);
        assertTrue("发布版本失败！", rs);
    }

    @Test
    public void testRollbackToStage() throws Exception {//-------ok
        String version = "56d690273179";
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
        String version = "56cc0d31886d";
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
