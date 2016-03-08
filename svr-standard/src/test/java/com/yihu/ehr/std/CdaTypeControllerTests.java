package com.yihu.ehr.std;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.standard.cdatype.controller.CdaTypeController;
import com.yihu.ehr.standard.cdatype.service.CDAType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
public class CdaTypeControllerTests {

	ApplicationContext applicationContext;

	@Autowired
	private CdaTypeController cdaTypeController;


    @Test
    public void aGetParents() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(StandardServiceApp.class).run();
        String parentId = "";
        Object result = cdaTypeController.getChildrenByPatientId(parentId);
        assertTrue("查询失败！" , result != null);
        parentId = "0dae000656720d02269e0319806c0ef0";
        result = cdaTypeController.getChildrenByPatientId(parentId);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void bgGetChildIncludeSelfByParentTypesAndKey() throws Exception{
        String[] patientIds = new String[]{"0dae000656720d12269e0319806c0ef1","0dae000656720d25269e0319806c0ef2"}; //儿童保健，妇女保健
        String key = "";
        Object result = cdaTypeController.getChildIncludeSelfByParentIdsAndKey(patientIds,key);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void cGetCdaTypeByCodeOrName() throws Exception{
        String code = "";
        String name = "";
        Object result = cdaTypeController.getCdaTypeByCodeOrName(code,name);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void dGetCdaTypeById() throws Exception{
        String id = "0dae000656720d25269e0319806c0ef2";
        Object result = cdaTypeController.getCdaTypeById(id);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void gGetCdaTypeByIds() throws Exception{
        String[] ids = new String[]{"0dae000656720d12269e0319806c0ef1","0dae000656720d25269e0319806c0ef2"};
        Object result = cdaTypeController.getCdaTypeByIds(ids);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void jSaveCDAType() throws Exception{
        String id = "0dae000656720d25269e0319806c0ef2";
        MCDAType cdaType = cdaTypeController.getCdaTypeById(id);
        cdaType.setName("test");
        String jsonData = new ObjectMapper().writeValueAsString(cdaType);
        Object result = cdaTypeController.saveCDAType(jsonData);
        assertTrue("保存失败！" , result != null);
    }

    @Test
    public void kIsCDATypeExists() throws Exception{
        String code = "HDSB01";
        Object result = cdaTypeController.isCDATypeExists(code);
        assertTrue("保存失败！" , result != null);
        code = "000000";
        result = cdaTypeController.isCDATypeExists(code);
        assertTrue("保存失败！" , result != null);
    }

    @Test
    public void mDeleteCDATypeByPatientIds() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(StandardServiceApp.class).run();
        String[] ids = new String[]{"0dae000656720d12269e0319806c0ef1","0dae000656720d25269e0319806c0ef2"};
        Object result = cdaTypeController.deleteCDATypeByPatientIds(ids);
        assertTrue("删除失败！" , result != null);
    }



}
