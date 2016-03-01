package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.AgAdminConstants;
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

/**
 * Created by Administrator on 2016/3/1.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminConstants.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StandardSourceControllerTests {
    @Autowired
    StandardSourceController stdSourceController;

    @Autowired
    ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void atestVersion() throws Exception {
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();
        Envelop envelop = new Envelop();
        //分页查询----------------------------1
        String fields = "";
        String filters = "";
        String sorts = "";
        int size = 15;
        int page = 1;
        envelop = stdSourceController.searchAdapterOrg(fields, filters, sorts, size, page);

        //根据id获取标准信息来源----------------------------2
        String sourceId = "";
        envelop = stdSourceController.getStdSource(sourceId);

//        //新增标准来源----------------------------3
//        String codeOld = "";
//        String nameOld = "";
//        String sourctTypeOld = "";
//        String descriptionOld = "";
//        envelop = stdSourceController.addStdSource(codeOld,nameOld,sourctTypeOld,descriptionOld);
//
//
//        //更新标准来源----------------------------4
//        String id = "";
//        String codeNew = "";
//        String nameNew = "";
//        String sourctTypeNew = "";
//        String descriptionNew = "";
//        envelop = stdSourceController.updateStdSource(id,codeNew,nameNew,sourctTypeNew,descriptionNew);
//
//
//        //批量删除标准来源（ids）----------------------------5
//        String ids = "";
//        boolean flag1 = stdSourceController.delStdSources(ids);
//        //删除标准来源（id）----------------------------6
//        String idForDel = "";
//        boolean flag2 = stdSourceController.delStdSource(idForDel);
    }
}
