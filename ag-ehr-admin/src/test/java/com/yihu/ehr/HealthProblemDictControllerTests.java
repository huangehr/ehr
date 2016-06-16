package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.specialdict.HealthProblemDictModel;
import com.yihu.ehr.agModel.specialdict.HpIcd10RelationModel;
import com.yihu.ehr.agModel.specialdict.Icd10DictModel;
import com.yihu.ehr.specialdict.controller.HealthProblemDictController;
import com.yihu.ehr.specialdict.controller.Icd10DictController;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.util.rest.Envelop;
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

import static org.junit.Assert.assertTrue;

/**
 * Created by CWS on 2016/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HealthProblemDictControllerTests {

    private static String version = "v1.0";
    public MDrugDict mDrugDict = null;

    ApplicationContext applicationContext;

    @Autowired
    private HealthProblemDictController hpDictController;
    @Autowired
    private Icd10DictController icd10DictController;
    @Autowired
    private ObjectMapper objectMapper;

    public static Long hpId;
    public static Long icd10Id;
    public static Long relaId;
    public static Envelop envelop = new Envelop();

    @Test
    public void atestCreateHealthProblemDict() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        String code = "HealthProblemDictTest_01";
        String name = "健康问题字典测试_01";
        String description = "健康问题字典新增测试。";

        Map<String,String> retrieveMap_1 = new HashMap<>();
        retrieveMap_1.put("code",code);
        retrieveMap_1.put("name",name);
        retrieveMap_1.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json_1 = objectMapper.writeValueAsString(retrieveMap_1);
        envelop = hpDictController.createHpDict(json_1);

        hpId = ((HealthProblemDictModel)envelop.getObj()).getId();
        assertTrue("字典新增失败", envelop.isSuccessFlg());

        envelop = hpDictController.getHpDict(hpId);
        assertTrue("ID查字典失败", envelop.isSuccessFlg());
    }

    @Test
    public void btestUpdateHpDict() throws Exception{
        String code = "HealthProblemDictTest_02";
        String name = "健康问题字典测试_02";
        String description = "健康问题字典修改测试_02.";

        Map<String,Object> retrieveMap_2 = new HashMap<>();
        retrieveMap_2.put("id", hpId);
        retrieveMap_2.put("code",code);
        retrieveMap_2.put("name",name);
        retrieveMap_2.put("description",description);

        String json_2 = objectMapper.writeValueAsString(retrieveMap_2);

        envelop = hpDictController.updateHpDict(json_2);
        assertTrue("字典更新失败", envelop.isSuccessFlg());
    }

    @Test
    public void dtestGetHpDictList() throws Exception{
        //---------- 无条件查询 ----------------
        String fields = "";
        String filters = "";
        String sorts = "";
        int page = 1;
        int size = 15;
        envelop = hpDictController.getHpDictList(fields, filters, sorts, size, page);
        assertTrue("无条件查询失败", envelop.isSuccessFlg());

        //---------- 全条件查询 ----------------
        String code = "HealthProblemDictTest_02";
        String name = "健康问题字典测试_02";

        filters = "code=" +code;
        filters += ";name=" + name;

        fields = "id,code,name,description";
        sorts = "+code,+name";

        envelop = hpDictController.getHpDictList(fields, filters, sorts, size, page);
        assertTrue("全条件查询失败", envelop.isSuccessFlg());
    }

    //-------------------------------健康问题与ICD10关联关系---------------------------------------------------------------
    @Test
    public void gtestCreateHpIcd10Relation() throws Exception{
        String code_icd10 = "Icd10DictTest_05";
        String name_icd10 = "ICD10字典测试_05";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_05。";

        Map<String,String> retrieveMap_3 = new HashMap<>();
        retrieveMap_3.put("code",code_icd10);
        retrieveMap_3.put("name",name_icd10);
        retrieveMap_3.put("chronicFlag",chronicFlag);
        retrieveMap_3.put("infectiousFlag",infectiousFlag);
        retrieveMap_3.put("description",description_icd10);

        String json_icd10 = objectMapper.writeValueAsString(retrieveMap_3);

        Envelop envelop_icd10 = icd10DictController.createIcd10Dict(json_icd10);
        Icd10DictModel icd10DictModel = (Icd10DictModel)envelop_icd10.getObj();
        icd10Id = icd10DictModel.getId();

        Map<String,Object> retrieveMap_4 = new HashMap<>();
        retrieveMap_4.put("icd10Id",icd10Id);
        retrieveMap_4.put("hpId",hpId);

        String relaJson = objectMapper.writeValueAsString(retrieveMap_4);
        envelop = hpDictController.createHpIcd10Relation(relaJson);
        relaId = ((HpIcd10RelationModel)envelop.getObj()).getId();
        assertTrue("验证失败！", envelop.isSuccessFlg());

        //envelop = hpDictController.isHpIcd10RelaExist(hpId,icd10Id);
        assertTrue("验证失败！", envelop.isSuccessFlg());
    }

    @Test
    public void htestUpdateHpIcd10Relation() throws Exception{
        String code = "HealthProblemDictTest_04";
        String name = "健康问题字典测试_04";
        String description = "健康问题字典新增测试_04。";

        Map<String,String> retrieveMap01 = new HashMap<>();
        retrieveMap01.put("code",code);
        retrieveMap01.put("name",name);
        retrieveMap01.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json01 = objectMapper.writeValueAsString(retrieveMap01);

        Envelop envelop_01 = hpDictController.createHpDict(json01);
        Long hpId_01 = ((HealthProblemDictModel)envelop_01.getObj()).getId();

        String code_icd10 = "Icd10DictTest_04";
        String name_icd10 = "ICD10字典测试_04";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_04。";

        Map<String,String> retrieveMap02 = new HashMap<>();
        retrieveMap02.put("code",code_icd10);
        retrieveMap02.put("name",name_icd10);
        retrieveMap02.put("chronicFlag",chronicFlag);
        retrieveMap02.put("infectiousFlag",infectiousFlag);
        retrieveMap02.put("description",description_icd10);

        String json02 = objectMapper.writeValueAsString(retrieveMap02);

        Envelop envelop_02 = icd10DictController.createIcd10Dict(json02);
        Long icd10_02 = ((Icd10DictModel)envelop_02.getObj()).getId();

        Map<String,Object> retrieveMap03 = new HashMap<>();
        retrieveMap03.put("id",relaId);
        retrieveMap03.put("hpId",hpId_01);
        retrieveMap03.put("icd10Id",icd10_02);

        String json03 = objectMapper.writeValueAsString(retrieveMap03);
        assertTrue("修改关联失败！", hpDictController.updateHpIcd10Relation(json03) != null);

        icd10DictController.deleteIcd10Dict(icd10_02);
        hpDictController.deleteHpDict(hpId_01);
    }

    @Test
    public void ztestDeleteHpDict() throws Exception{
        //envelop = hpDictController.deleteHpDict(hpId);
        assertTrue("删除失败", envelop.isSuccessFlg());

        icd10DictController.deleteIcd10Dict(icd10Id);
    }
}
