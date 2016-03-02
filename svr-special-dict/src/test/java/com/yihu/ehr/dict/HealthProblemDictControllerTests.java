package com.yihu.ehr.dict;

import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.yihu.ehr.SvrSpecialDictApplication;
import com.yihu.ehr.dict.controller.HealthProblemDictController;
import com.yihu.ehr.dict.controller.Icd10DictController;
import com.yihu.ehr.dict.service.*;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
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
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Cws on 2016/1/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrSpecialDictApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HealthProblemDictControllerTests {

    ApplicationContext applicationContext;

    @Autowired
    private HealthProblemDictController hpDictController;
    @Autowired
    private XHealthProblemDictRepository hpDictRepo;
    @Autowired
    private XHpIcd10RelationRepository hpIcd10RelaRepo;
    @Autowired
    private XIcd10DictRepository icd10DictRepo;
    @Autowired
    private Icd10DictController icd10DictController;

    @Test
    public void atestCreateHealthProblemDict() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrSpecialDictApplication.class).run();

        String code = "HealthProblemDictTest_01";
        String name = "健康问题字典测试_01";
        String description = "健康问题字典新增测试。";

        Map<String,String> retrieveMap = new HashMap<>();
        retrieveMap.put("code",code);
        retrieveMap.put("name",name);
        retrieveMap.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(retrieveMap);

        assertTrue("健康问题字典新增失败！" , hpDictController.createHpDict(json) != null);

        assertTrue("存在性验证失败！", hpDictController.isCodeExists("HealthProblemDictTest_01"));
        assertTrue("存在性验证失败！", hpDictController.isNameExists("健康问题字典测试_01"));
    }

    @Test
    public void btestUpdateHpDict() throws Exception{
        String code = "HealthProblemDictTest_02";
        String name = "健康问题字典测试_02";
        String description = "健康问题字典修改测试。Only a test。";

        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_01");
        String id = healthProblemDict.getId().toString();

        Map<String,String> retrieveMap = new HashMap<>();
        retrieveMap.put("id",id);
        retrieveMap.put("code",code);
        retrieveMap.put("name",name);
        retrieveMap.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(retrieveMap);

        hpDictController.updateHpDict(json);

        healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_02");
        boolean result = healthProblemDict.getId().toString().equals(id);
        result = result&&healthProblemDict.getCode().toString().equals(code);
        result = result&&healthProblemDict.getName().toString().equals(name);
        result = result&&healthProblemDict.getDescription().toString().equals(description);

        assertTrue("健康问题字典更新失败！", result);
    }

    @Test
    public void ctestGetHpDictById() throws Exception{

        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_02");
        String id = healthProblemDict.getId().toString();

        MHealthProblemDict result = hpDictController.getHpDict(id);

        assertTrue("健康问题字典获取失败！", result != null);
    }

    @Test
    public void dtestGetHpDictList() throws Exception{
        String code = "HealthProblemDictTest_02";
        String name = "健康问题字典测试_02";

        String filters = "code=" +code;
        filters += ";name=" + name;

        String fields = "id,code,name,description";
        String sorts = "+code,+name";

        List<MHealthProblemDict> hpDictList =  (List<MHealthProblemDict>)hpDictController.getHpDictList(fields, filters, sorts, 15, 1, null, null);
        assertTrue("查询失败！" , hpDictList.size() != 0);
    }

    //-------------------------------健康问题与ICD10关联关系---------------------------------------------------------------
    @Test
    public void gtestCreateHpIcd10Relation() throws Exception{
        String code = "HealthProblemDictTest_03";
        String name = "健康问题字典测试_03";
        String description = "健康问题字典新增测试_03。";

        Map<String,String> retrieveMap01 = new HashMap<>();
        retrieveMap01.put("code",code);
        retrieveMap01.put("name",name);
        retrieveMap01.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json01 = objectMapper.writeValueAsString(retrieveMap01);

        hpDictController.createHpDict(json01);
        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_03");
        String hpId = healthProblemDict.getId().toString();

        String code_icd10 = "Icd10DictTest_03";
        String name_icd10 = "ICD10字典测试_03";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_03。";

        Map<String,String> retrieveMap02 = new HashMap<>();
        retrieveMap02.put("code",code_icd10);
        retrieveMap02.put("name",name_icd10);
        retrieveMap02.put("chronicFlag",chronicFlag);
        retrieveMap02.put("infectiousFlag",infectiousFlag);
        retrieveMap02.put("description",description_icd10);

        String json02 = objectMapper.writeValueAsString(retrieveMap02);

        icd10DictController.createIcd10Dict(json02);
        Icd10Dict icd10Dict = icd10DictRepo.findByCode("Icd10DictTest_03");
        String icd10Id = icd10Dict.getId().toString();

        Map<String,String> retrieveMap03 = new HashMap<>();
        retrieveMap03.put("hpId",hpId);
        retrieveMap03.put("icd10Id",icd10Id);

        String json03 = objectMapper.writeValueAsString(retrieveMap03);

        assertTrue("新增失败！", hpDictController.createHpIcd10Relation(json03) != null);

        assertTrue("存在性验证失败！", hpDictController.isHpIcd10RelaExist(icd10Id,hpId));


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

        hpDictController.createHpDict(json01);
        HealthProblemDict hpDict = hpDictRepo.findByCode("HealthProblemDictTest_04");
        String hpId = hpDict.getId().toString();

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

        icd10DictController.createIcd10Dict(json02);
        Icd10Dict icd10Dict = icd10DictRepo.findByCode("Icd10DictTest_03");
        String icd10Id = icd10Dict.getId().toString();

        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_03");
        String id = healthProblemDict.getId().toString();

        List<HpIcd10Relation> hpIcd10Relation_old = hpIcd10RelaRepo.findByHpId(id);
        String relaId = hpIcd10Relation_old.get(0).getId();

        Map<String,String> retrieveMap03 = new HashMap<>();
        retrieveMap03.put("id",relaId);
        retrieveMap03.put("hpId",hpId);
        retrieveMap03.put("icd10Id",icd10Id);

        String json03 = objectMapper.writeValueAsString(retrieveMap03);

        assertTrue("修改关联失败！", hpDictController.updateHpIcd10Relation(json03) != null);
    }

    @Test
    public void ztestDeleteHpDict() throws Exception{
        // 存在关联的删除
        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_04");
        String id = healthProblemDict.getId().toString();

        List<HpIcd10Relation> hpIcd10Relation = hpIcd10RelaRepo.findByHpId(id);
        String relaId = hpIcd10Relation.get(0).getId();
        boolean result = hpDictController.deleteHpDict(id);

        hpIcd10Relation = hpIcd10RelaRepo.findByHpId(id);
        result = result&&(hpIcd10Relation.size() == 0);

        assertTrue("删除失败！", result);

        //无关联的删除
        healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_02");
        id = healthProblemDict.getId().toString();
        result = hpDictController.deleteHpDict(id);

        assertTrue("删除失败！", result);

        //无关联的删除
        healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_03");
        id = healthProblemDict.getId().toString();
        hpDictController.deleteHpDict(id);

        Icd10Dict icd10Dict = icd10DictRepo.findByCode("Icd10DictTest_03");
        String icd10Id = icd10Dict.getId();
        icd10DictController.deleteIcd10Dict(icd10Id);

        icd10Dict = icd10DictRepo.findByCode("Icd10DictTest_04");
        icd10Id = icd10Dict.getId();
        icd10DictController.deleteIcd10Dict(icd10Id);
    }
}
