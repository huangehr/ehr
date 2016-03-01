package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.Assert.assertNotEquals;


/**
 * Created by yww on 2016/2/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CDAVersionTests {
    ApplicationContext applicationContext = new SpringApplicationBuilder()
            .web(false).sources(AgAdminApplication.class).run();

    Envelop envelop = new Envelop();

    @Autowired
    CDAVersionController cdaVersionController;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void atestVersion() throws Exception {
        // 分页查询------------------------------1 ok
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 15;
        int page = 1;
        envelop = cdaVersionController.searchCDAVersions(fields, filters, sorts, size, page);
        assertNotEquals("没有匹配条件的cda版本！", envelop.isSuccessFlg(), false);

        //检查是否存在编辑状态的cda版本-------------2ok
        boolean flag7 = cdaVersionController.existInStage();
        assertNotEquals("不存在编辑状态的cda版本！", flag7, false);

        //判断是否是最新的已发布的版本 -----------------------3ok
        boolean flag1 = cdaVersionController.isLatestVersion("56d3f3b33656");
        //56d4f83b5dc2
        assertNotEquals("该cda版本不是最新的！", flag1, false);

        //判断版本名称是否已存在--------------------4
        String versionName = "V4.1";
        //V1.0
        boolean flag8 = cdaVersionController.checkVersionName(versionName);
        assertNotEquals("该版本名称不存在！", flag8, false);

        //获取cda版本对象--------------------------5
        envelop = cdaVersionController.getVersion("56395d75b854");
        assertNotEquals("未查询到对应的cda版本！", envelop.isSuccessFlg(), false);

    }

    @Test
    public void btestVersion() throws Exception {
        //新增cda版本(只能有一个处于编辑状态的版本）------------------------------6ok
        envelop = cdaVersionController.addVersion("sysyww");
        assertNotEquals("新增cda版本失败！", envelop.isSuccessFlg(), false);

        //更cda版本信息---------------------------7
        envelop = cdaVersionController.updateVersion("", "V4.11", "sys001", 1, "568ce002559f");
        assertNotEquals("更新cda版本失败！", envelop.isSuccessFlg(), false);

        //cda新版本发布----------------------------6ok
        boolean flag4 = cdaVersionController.commitVersion("56d4edb15dc0");
        assertNotEquals("新cda版本发布失败！", flag4, false);

        //将最新的已发布版本修改为编辑状态----------7
        boolean flag5 = cdaVersionController.rollbackToStage("56d4edb15dc0");
        assertNotEquals("已发布版本修改为编辑状态失败！", flag5, false);

        //删除标准版本及对应的数据库表（没有编辑与非编辑状态限制），同时若存在子类版本，则将子类的baseVersion 设置为此删除版本的父级版本（继承版本的父级版本问题，微服务）------------------------------4
//        boolean flag2 = cdaVersionController.dropCDAVersion("");
//        assertNotEquals("删除cda版本失败！",flag2,false);
//
        //删除（编辑状态）标准版本-----------------------5
//        boolean flag3 = cdaVersionController.revertVersion("");
//        assertNotEquals("删除cda版本失败****！",flag3,false);
    }
}
