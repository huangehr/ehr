package com.yihu.ehr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdadocument.CDAModel;
import com.yihu.ehr.agModel.standard.cdadocument.CdaDataSetRelationshipModel;
import com.yihu.ehr.std.controller.CDAController;
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

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by wq on 2016/3/4.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CDAControllerTests {

    private static String version = "000000000000";
    private static String typeId = "0dae0006568242560dc356006a770344";

    @Autowired
    private CDAController cdaController;

    ApplicationContext applicationContext;

    Envelop envelop = new Envelop();
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void atestDict() throws JsonProcessingException {

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        Map<String,Object> map = new HashMap<>();

        map.put("code","code_test");
        map.put("name","name_test");
        map.put("sourceId","sourceId");
        map.put("typeId",typeId);
        map.put("description","description_test");
        map.put("createUser","createUser");
        map.put("versionCode",version);

        String CDAJsonDataModel = mapper.writeValueAsString(map);

        envelop = cdaController.SaveCdaInfo("",CDAJsonDataModel);
        assertNotEquals("新增cda文档失败",envelop,null);

        map.put("name","name_copy");
        CDAJsonDataModel = mapper.writeValueAsString(map);
        envelop = cdaController.updateCDADocuments(CDAJsonDataModel);
        assertNotEquals("修改cda文档失败",envelop,null);

        envelop = cdaController.getCDAInfoById(((CDAModel)envelop.getObj()).getId(),version);
        assertNotEquals("获取cda文档失败",envelop,null);

        envelop = cdaController.GetCdaListByKey("code_test", "", version,typeId,1,15);
        assertNotEquals("获取cda文档列表失败",envelop,null);

        StringBuffer sb = new StringBuffer();
        for (int i = 1;i<envelop.getDetailModelList().size();i++){
            sb.append(((CDAModel)envelop.getDetailModelList().get(i)).getId()+",");
        }
        Envelop envelopl = cdaController.deleteCdaInfo(sb.toString(),version);
        assertNotEquals("删除cda文档列表失败",envelop,null);


        Envelop relationshipEnvelop = cdaController.SaveRelationship("57",((CDAModel)envelop.getDetailModelList().get(0)).getId(),version,"xmlinfo");
        assertNotEquals("新增CDADataSetRelationship失败",envelop,null);

        String cdaId = ((CDAModel)envelop.getDetailModelList().get(0)).getId();
        envelop = cdaController.getRelationByCdaId(cdaId,version);
        assertNotEquals("获取CDADataSetRelationship失败",envelop,null);

//        envelop = cdaController.createCDASchemaFile(version,cdaId);
//        assertNotEquals("生成CDA文档失败",envelop,null);
//
//        Envelop isEnvelop = cdaController.FileExists(version,((CdaDataSetRelationshipModel)envelop.getDetailModelList().get(0)).getCdaId());
//        assertNotEquals("判断文档是否存在失败",envelop,null);

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0;i<envelop.getDetailModelList().size();i++){
            stringBuffer.append(((CdaDataSetRelationshipModel)envelop.getDetailModelList().get(i)).getId()+",");
        }

        envelop = cdaController.deleteCDADataSetRelationship(version,stringBuffer.toString());
        assertNotEquals("删除CDADataSetRelationship失败",envelop,null);

        envelop = cdaController.deleteCdaInfo(cdaId,version);
        assertNotEquals("删除cda文档列表失败",envelop,null);

    }
}
