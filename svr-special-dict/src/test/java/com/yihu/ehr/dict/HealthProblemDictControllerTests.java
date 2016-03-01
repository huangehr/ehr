package com.yihu.ehr.dict;

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

import java.util.List;

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

        hpDictController.createHpDict(code,name, description);
        HealthProblemDict healthProblemDict = hpDictRepo.findByCode(code);

        assertTrue("健康问题字典新增失败！" , healthProblemDict != null);
    }

    @Test
    public void btestCreateIcd10DictCodeRepeat() throws Exception{
        try{
            String code = "HealthProblemDictTest_01";
            String name = "健康问题字典测试_02";
            String description = "健康问题字典新增Code重复测试。";

            Object result = hpDictController.createHpDict(code, name, description);
        }
        catch (ApiException e){
            assertTrue("健康问题字典新增Code重复测试失败！",true);
        }
    }

    @Test
    public void ctestCreateHpDictNameRepeat() throws Exception{
        try{
            String code = "HealthProblemDictTest_02";
            String name = "健康问题字典测试_01";
            String description = "健康问题字典新增Name重复测试。";

            Object result = hpDictController.createHpDict(code, name, description);
        }
        catch (ApiException e){
            assertTrue("健康问题字典新增Name重复测试失败！", true);
        }
    }

    @Test
    public void dtestUpdateHpDict() throws Exception{
        String code = "HealthProblemDictTest_02";
        String name = "健康问题字典测试_02";
        String description = "健康问题字典修改测试。Only a test。";

        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_01");
        String id = healthProblemDict.getId().toString();

        hpDictController.updateHpDict(id, code, name, description);

        healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_02");
        boolean result = healthProblemDict.getId().toString().equals(id);
        result = result&&healthProblemDict.getCode().toString().equals(code);
        result = result&&healthProblemDict.getName().toString().equals(name);
        result = result&&healthProblemDict.getDescription().toString().equals(description);

        assertTrue("健康问题字典更新失败！", result);
    }

    @Test
    public void etestGetHpDictById() throws Exception{

        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_02");
        String id = healthProblemDict.getId().toString();

        MHealthProblemDict result = (MHealthProblemDict)hpDictController.getHpDict(id);

        assertTrue("健康问题字典获取失败！", result != null);
    }

    @Test
    public void ftestGetHpDictList() throws Exception{
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
        hpDictController.createHpDict(code, name, description);
        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_03");
        String hpId = healthProblemDict.getId().toString();

        String code_icd10 = "Icd10DictTest_03";
        String name_icd10 = "ICD10字典测试_03";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_03。";
        icd10DictController.createIcd10Dict(code_icd10, name_icd10, chronicFlag, infectiousFlag, description_icd10);
        Icd10Dict icd10Dict = icd10DictRepo.findByCode("Icd10DictTest_03");
        String icd10Id = icd10Dict.getId().toString();

        boolean result = (boolean)hpDictController.createHpIcd10Relation(hpId,icd10Id);
        assertTrue("新增失败！", result);
    }

    @Test
    public void htestUpdateHpIcd10Relation() throws Exception{
        String code = "HealthProblemDictTest_04";
        String name = "健康问题字典测试_04";
        String description = "健康问题字典新增测试_04。";
        hpDictController.createHpDict(code, name, description);
        HealthProblemDict hpDict = hpDictRepo.findByCode("HealthProblemDictTest_04");
        String hpId = hpDict.getId().toString();

        String code_icd10 = "Icd10DictTest_04";
        String name_icd10 = "ICD10字典测试_04";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_04。";
        icd10DictController.createIcd10Dict(code_icd10, name_icd10, chronicFlag, infectiousFlag, description_icd10);
        Icd10Dict icd10Dict = icd10DictRepo.findByCode("Icd10DictTest_03");
        String icd10Id = icd10Dict.getId().toString();

        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_03");
        String id = healthProblemDict.getId().toString();

        List<HpIcd10Relation> hpIcd10Relation_old = (List<HpIcd10Relation>)hpIcd10RelaRepo.findByHpId(id);
        String relaId = hpIcd10Relation_old.get(0).getId();

        boolean result = (boolean)hpDictController.updateHpIcd10Relation(relaId,hpId,icd10Id);

        assertTrue("修改关联失败！", result);
    }

    @Test
    public void ztestDeleteHpDict() throws Exception{
        // 存在关联的删除
        HealthProblemDict healthProblemDict = hpDictRepo.findByCode("HealthProblemDictTest_04");
        String id = healthProblemDict.getId().toString();

        List<HpIcd10Relation> hpIcd10Relation = (List<HpIcd10Relation>)hpIcd10RelaRepo.findByHpId(id);
        String relaId = hpIcd10Relation.get(0).getId();
        boolean result = hpDictController.deleteHpDict(id);

        hpIcd10Relation = (List<HpIcd10Relation>)hpIcd10RelaRepo.findByHpId(id);
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
