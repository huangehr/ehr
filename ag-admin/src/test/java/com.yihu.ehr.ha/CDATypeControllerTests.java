package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeDetailModel;
import com.yihu.ehr.ha.std.controller.CDATypeController;
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
 * Created by yww on 2016/3/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CDATypeControllerTests {

    static ApplicationContext applicationContext = new SpringApplicationBuilder()
            .web(false).sources(AgAdminApplication.class).run();

    Envelop envelop = new Envelop();

    @Autowired
    CDATypeController cdaTypeController;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testChildrenByPatientId() throws Exception {
        //根据父级cdaType的id获取下一级cdaType----------------ok
        String parentId = "0dae000656720d02269e0319806c0ef0";
        envelop = cdaTypeController.getChildrenByPatientId(parentId);
        assertNotEquals("根据父级cdaType的id获取下级！", envelop.getDetailModelList(), 0);
    }

    @Test
    public void testChildIncludeSelfByParentIdsAndKey() throws Exception {
        //根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）---------(微服务取得not in结果）
        String parentIds = "0dae000656720d12269e0319806c0ef1";
        String key = "";
        envelop = cdaTypeController.getChildIncludeSelfByParentIdsAndKey(parentIds, key);
        assertNotEquals("根据parentIds、key查询失败！", envelop.getDetailModelList(), 0);
    }

    @Test
    public void testCdaTypeByCodeOrName() throws Exception {
        // 根据code或者name获取CDAType列表------------------------------ok
        String code = "HDSB01_";
        String name = "儿童";
        envelop = cdaTypeController.getCdaTypeByCodeOrName(code, name);
        assertNotEquals("没有查询到匹配条件的cdaType！", envelop.getDetailModelList().size(), 0);
    }

    @Test
    public void testCdaTypeByIds() throws Exception {
        //根据ids获取CDAType列表------------------------------------------ok
        String ids = "0dae000656720bd5269e0319806c0eed,0dae000656720d25269e0319806c0ef2";
        envelop = cdaTypeController.getCdaTypeByIds(ids);
        assertNotEquals("没有查询到对应的cdaType！", envelop.getDetailModelList().size(), 0);
    }

    @Test
    public void testSaveCDAType() throws Exception {
        //新增CDAType----------ok

        CdaTypeDetailModel detailModel = new CdaTypeDetailModel();
        detailModel.setCode("wwcs");
        detailModel.setName("api网关改造测试");
        detailModel.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");
        detailModel.setParentId("0dae000656720d02269e0319806c0ef0");
        detailModel.setDescription("wwcscscscscsscs");
        String jsonDataCreate = objectMapper.writeValueAsString(detailModel);
        envelop = cdaTypeController.saveCDAType(jsonDataCreate);
        assertNotEquals("新增cdaType失败！", envelop.getObj(), null);

        //获取刚新建的cdaType 便于后面测试
        CdaTypeDetailModel cdaTypeDetailModel = (CdaTypeDetailModel) envelop.getObj();

        //根据id获取cdaType）-----------ok

        envelop = cdaTypeController.getCdaTypeById(cdaTypeDetailModel.getId());
        assertNotEquals("没有查询到对应的cdaType！", envelop.getObj(), null);

        //修改cdaType--------------ok

        cdaTypeDetailModel.setCode("wwcsUpdate");
        cdaTypeDetailModel.setName("api网关改造测试Update");
        cdaTypeDetailModel.setUpdateUser("0dae0003561cc415c72d9111e8cb88aa");
        String jsonDataUpdate = objectMapper.writeValueAsString(cdaTypeDetailModel);
        envelop = cdaTypeController.updateCDAType(jsonDataUpdate);
        assertNotEquals("更新cdaType失败！", envelop.getObj(), null);

        //删除刚新建的cdaType------------ok

        boolean rs = cdaTypeController.deleteCDATypeByPatientIds(cdaTypeDetailModel.getId());
        assertNotEquals("删除cdaType失败！", rs, false);

    }

    @Test
    public void testisCDATypeExists() throws Exception {
        //判断提交的机构代码是否已经存在-----------ok
        String code = "HDSD14";
        boolean flag1 = cdaTypeController.isCDATypeExists(code);
        assertNotEquals("该code对应的cdaType不存在！", flag1, false);
        String code2 = "HDSD14aa";
        boolean flag2 = cdaTypeController.isCDATypeExists(code2);
        assertNotEquals("该code对应的cdaType不存在！*********", flag2, false);
    }

    @Test
    public void testDeleteByParentIds() throws Exception {
        //删除CDA类别，若该类别存在子类别，将一并删除子类别---------------------------------------ok
        String ids = "85be292860a5488c9ada65afaf2735e0,cd4b8e189b9d4cdea2e823fb2a027f34";
        boolean rs = cdaTypeController.deleteCDATypeByPatientIds(ids);
        assertNotEquals("删除cdaType失败！", rs, false);
    }
}
