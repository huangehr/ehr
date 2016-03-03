package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardsource.StdSourceDetailModel;
import com.yihu.ehr.ha.std.controller.StandardSourceController;
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

import static org.junit.Assert.assertTrue;
/**
 * Created by yww on 2016/3/1.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StandardSourceControllerTests {
    @Autowired
    private StandardSourceController stdSourceController;

    ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void atestVersion() throws Exception {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();
        Envelop envelop = new Envelop();
        //分页查询----------------------------1ok
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 15;
        int page = 1;
        envelop = stdSourceController.searchSources(fields, filters, sorts, size, page);

        //新增标准来源----------------------------3
        StdSourceDetailModel sourceDetailModel = new StdSourceDetailModel();
        sourceDetailModel.setCode("wwcs");
        sourceDetailModel.setName("api改造测试新增");
        sourceDetailModel.setSourceType("1");
        sourceDetailModel.setDescription("only a test");
        String jSonData = objectMapper.writeValueAsString(sourceDetailModel);
        envelop = stdSourceController.addStdSource(jSonData);
        StdSourceDetailModel detailModel = (StdSourceDetailModel) envelop.getObj();


        //更新标准来源----------------------------4
        String idForTest = detailModel.getId();

        detailModel.setCode("wwcsjkzl");
        detailModel.setName("api改造测试修改");
        detailModel.setSourceType("2");
        detailModel.setDescription("again");
        String jSonDataNew = objectMapper.writeValueAsString(detailModel);
        envelop = stdSourceController.updateStdSource(jSonDataNew);

        //根据id获取标准信息来源----------------------------ok
        envelop = stdSourceController.getStdSource(idForTest);

        //批量删除标准来源（ids）----------------------------5ok
//        String ids = "0000000656d6513be60f8d1780e16198,0000000656d65155e60f8d1780e1619b";
//        boolean flag1 = stdSourceController.delStdSources(ids);
//        assertTrue("批量删除失败!",flag1);
        //删除标准来源（id）----------------------------6ok
        boolean flag2 = stdSourceController.delStdSource(idForTest);
        assertTrue("标准来源删除失败！", flag2);
    }
}
