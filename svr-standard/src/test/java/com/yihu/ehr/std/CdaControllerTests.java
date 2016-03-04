package com.yihu.ehr.std;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.standard.cda.controller.CdaController;
import com.yihu.ehr.standard.cda.service.CDADocument;
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

import static org.springframework.test.util.AssertionErrors.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
public class CdaControllerTests {

	ApplicationContext applicationContext;

	@Autowired
	private CdaController cdaController;


    @Test
    public void aGetCDADocuments() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(StandardServiceApp.class).run();
        String strVersion = "000000000000";
        String code = "CDX_01";
        String name = "住院检验";
        String type = "0dae0006567210a6269e0319806c0f24";
        Integer page = 1;
        Integer rows = 10;
        Object result = cdaController.GetCDADocuments(strVersion,code,name,type,page,rows,null,null);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void bGetCDAInfoById() throws Exception{
        String[] ids = new String[]{"0dae00065683df1fb11bbb211c8a9092"};
        String strVersion = "000000000000";
        Object result = cdaController.getCDADocumentById(ids,strVersion);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void cGetCDADataSetRelationships() throws Exception{
        String cda_Id = "0dae0006568244b00dc356006a77036c";
        String strVersion = "000000000000";
        Integer page = 1;
        Integer rows = 10;
        Object result = cdaController.getCDADataSetRelationships(cda_Id,strVersion,page,rows,null,null);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void dSaveCdaInfo() throws Exception{
        String[] ids = new String[]{"0dae00065683df1fb11bbb211c8a9092"};
        String strVersion = "000000000000";
        MCDADocument cdaDocument = cdaController.getCDADocumentById(ids,strVersion).get(0);
        cdaDocument.setCode("test");
        String jsonData = new ObjectMapper().writeValueAsString(cdaDocument);
        Object result = cdaController.saveCDADocuments(jsonData);
        assertTrue("保存失败！" , result != null);
    }

    @Test
    public void eDeleteCdaInfo() throws Exception{
        String[] ids = new String[]{"0dae00065683df1fb11bbb211c8a9092"};
        String strVersion = "000000000000";
        Object result = cdaController.deleteCDADocuments(ids,strVersion);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void fSaveDataSetRelationship() throws Exception{
        String[] dataSetIds = new String[]{"3","4","5","6"};
        String cdaId = "0dae00065682528d0dc356006a77051d";
        String strVersion = "000000000000";
        String xmlInfo = "";
        Object result = cdaController.saveCDADataSetRelationship(dataSetIds,cdaId,strVersion,xmlInfo);
        assertTrue("查询失败！" , result != null);
    }

    @Test
    public void gDeleteDataSetRelationship() throws Exception{
        String[] relationIds = new String[]{"0dae0006568244cc0dc356006a770371"};
        String versionCode = "000000000000";
        Object result = cdaController.deleteCDADataSetRelationship(versionCode,relationIds);
        assertTrue("删除失败！" , result != null);
    }


    @Test
    public void hGetCDADataSetRelationshipByCdaId() throws Exception{
        String cdaId = "0dae0006568244b00dc356006a77036c";
        String versionCode = "000000000000";
        Object result = cdaController.getCDADataSetRelationshipByCDAId(cdaId,versionCode);
        assertTrue("查询失败！" , result != null);
    }


}
