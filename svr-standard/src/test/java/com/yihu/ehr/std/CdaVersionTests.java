//package com.yihu.ehr.std;
//
//import com.yihu.ehr.StandardServiceApp;
//import com.yihu.ehr.model.standard.MCDAVersion;
//import com.yihu.ehr.standard.cdaversion.controller.CdaVersionController;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.assertTrue;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = StandardServiceApp.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class CdaVersionTests {
//
//    @Autowired
//    private CdaVersionController cdaVersionController;
//
//    @Test
//    public void testIsLatestVersions() throws Exception{
//        String version = "56d3f3b33656";
//        boolean rs = cdaVersionController.isLatestVersion(version);
//        assertTrue("不是最后一个版本", rs);
//    }
//
//    @Test
//    public void testAddVersion() throws Exception{
//        String userCode = "user1";
//        MCDAVersion rs = cdaVersionController.addVersion(userCode);
//        assertTrue("新增失败！", rs!=null);
//    }
//
//    @Test
//    public void testDropCDAVersion() throws Exception{
//        String version = "56d790a561ea";
//        boolean rs = cdaVersionController.dropCDAVersion(version);
//        assertTrue("删除失败！", rs);
//    }
//
//    @Test
//    public void testRevertVersion() throws Exception{
//        String version = "56d7e2d384c1";
//        boolean rs = cdaVersionController.revertVersion(version);
//        assertTrue("删除编辑中的版本失败！", rs);
//    }
//
//    @Test
//    public void testCommitVersion() throws Exception{
//        String version = "56d3f3b33656";
//        boolean rs = cdaVersionController.commitVersion(version);
//        assertTrue("发布版本失败！", rs);
//    }
//
//    @Test
//    public void testRollbackToStage() throws Exception{
//        String version = "568f110cf330";
//        boolean rs = cdaVersionController.rollbackToStage(version);
//        assertTrue("版本回滚失败！", rs);
//    }
//
//    @Test
//    public void testUpdateVersion() throws Exception{
//        String version = "568f110cf330";
//        String versionName = "v3.1";
//        String userCode = "lincl";
//        int inStage = 1;
//        String baseVersion = "568ce002559f";
//        MCDAVersion rs = cdaVersionController.updateVersion(version, versionName, userCode, inStage, baseVersion);
//        assertTrue("更新失败！", rs!=null);
//    }
//
//    @Test
//    public void testCheckVersionName() throws Exception{
//        String versionName = "V3.0";
//        boolean rs = cdaVersionController.checkVersionName(versionName);
//        assertTrue("版本名称重复" , rs);
//    }
//
//    @Test
//    public void testExistInStage() throws Exception{
//        boolean rs = cdaVersionController.existInStage();
//        assertTrue("不存在处于编辑状态的版本：", rs);
//    }
//
//    @Test
//    public void testGetVersion() throws Exception{
//        String version = "000000000000";
//        MCDAVersion mcdaVersion = cdaVersionController.getVersion(version);
//        assertTrue("获取版本失败", mcdaVersion != null);
//    }
//}
