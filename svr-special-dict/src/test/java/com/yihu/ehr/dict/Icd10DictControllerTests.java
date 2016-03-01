package com.yihu.ehr.dict;

import com.yihu.ehr.SvrSpecialDictApplication;
import com.yihu.ehr.dict.controller.DrugDictController;
import com.yihu.ehr.dict.controller.Icd10DictController;
import com.yihu.ehr.dict.controller.IndicatorsDictController;
import com.yihu.ehr.dict.service.*;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
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
public class Icd10DictControllerTests {

    ApplicationContext applicationContext;

    @Autowired
    private Icd10DictController icd10DictController;
    @Autowired
    private XIcd10DictRepository icd10DictRepository;
    @Autowired
    private DrugDictController drugDictController;
    @Autowired
    private XDrugDictRepository drugDictRepo;
    @Autowired
    private XIcd10DrugRelationRepository icd10DrugRelationRepository;
    @Autowired
    private IndicatorsDictController indicatorsDictController;
    @Autowired
    private XIndicatorsDictRepository indicatorsDictRepo;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorRelationRepository;

    @Test
    public void atestCreateIcd10Dict() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(SvrSpecialDictApplication.class).run();

        String code = "Icd10DictTest_01";
        String name = "ICD10字典测试_01";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试。";

        icd10DictController.createIcd10Dict(code,name,chronicFlag,infectiousFlag,description);
        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code);

        assertTrue("ICD10字典新增失败！" , icd10Dict != null);
    }

    @Test
    public void btestCreateIcd10DictCodeRepeat() throws Exception{
        try{
            String code = "Icd10DictTest_01";
            String name = "ICD10字典测试_02";
            String chronicFlag = "0";
            String infectiousFlag = "1";
            String description = "ICD10字典新增测试。";

            Object result = icd10DictController.createIcd10Dict(code,name,chronicFlag,infectiousFlag,description);
        }
        catch (ApiException e){
            assertTrue("ICD10字典code重复验证失败！", true);
        }
    }

    @Test
    public void ctestCreateIcd10DictNameRepeat() throws Exception{
        try{
            String code = "Icd10DictTest_02";
            String name = "ICD10字典测试_01";
            String chronicFlag = "0";
            String infectiousFlag = "1";
            String description = "ICD10字典新增测试。";

            Object result = icd10DictController.createIcd10Dict(code,name,chronicFlag,infectiousFlag,description);
        }
        catch (ApiException e){
            assertTrue("ICD10字典name重复验证失败！", true);
        }
    }

    @Test
    public void dtestUpdateIcd10Dict() throws Exception{
        String code = "Icd10DictTest_02";
        String name = "ICD10字典测试_02";
        String chronicFlag = "0";
        String infectiousFlag = "0";
        String description = "ICD10字典新增测试。Only a test。";

        Icd10Dict icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_01");
        String id = icd10Dict.getId().toString();

        icd10DictController.updateIcd10Dict(id,code,name,chronicFlag,infectiousFlag,description);

        icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_02");
        boolean result = icd10Dict.getId().toString().equals(id);
        result = result&&icd10Dict.getCode().toString().equals(code);
        result = result&&icd10Dict.getName().toString().equals(name);
        result = result&&icd10Dict.getChronicFlag().toString().equals(chronicFlag);
        result = result&&icd10Dict.getInfectiousFlag().toString().equals(infectiousFlag);
        result = result&&icd10Dict.getDescription().toString().equals(description);

        assertTrue("ICD10字典更新失败！", result);
    }

    @Test
    public void etestGetIcd10DictById() throws Exception{

        Icd10Dict icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_02");
        String id = icd10Dict.getId().toString();

        MIcd10Dict result = (MIcd10Dict)icd10DictController.getIcd10Dict(id);

        assertTrue("ICD10字典更新失败！", result != null);
    }

    @Test
    public void ftestGetIcd10DictList() throws Exception{
        String code = "Icd10DictTest_02";
        String name = "ICD10字典测试_02";
        String chronicFlag = "0";
        String infectiousFlag = "0";

        String filters = "code=" + code;
        filters += ";name=" + name;
        filters += ";chronicFlag=" + chronicFlag;
        filters += ";infectiousFlag=" + infectiousFlag;

        String fields = "id,code,name,chronicFlag,infectiousFlag,description";
        String sorts = "+code,+name";

        List<MIcd10Dict> icd10DictList =  (List<MIcd10Dict>)icd10DictController.getIcd10DictList(fields, filters, sorts, 15, 1, null, null);
        assertTrue("查询失败！" , icd10DictList.size() != 0);
    }

    //-------------------------------ICD10与药品关联关系管理---------------------------------------------------------------
    @Test
    public void gtestCreateIcd10DrugRelation() throws Exception{
        String code_icd10 = "Icd10DictTest_06";
        String name_icd10 = "ICD10字典测试_06";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_06。";
        icd10DictController.createIcd10Dict(code_icd10, name_icd10, chronicFlag, infectiousFlag, description_icd10);
        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code_icd10);
        String icd10Id = icd10Dict.getId().toString();

        String code = "DrugDictTest_06";
        String name = "Drug字典测试_06";
        String type = "1";
        String flag = "0";
        String tradeName = "商品名称_06";
        String unit = "盒";
        String specifications = "12包";
        String description = "Drug字典新增测试。";
        drugDictController.createDrugDict(code, name, type, flag,tradeName,unit, specifications,description);
        DrugDict drugDict = drugDictRepo.findByCode(code);
        String drugId = drugDict.getId();

        boolean result = (boolean)icd10DictController.createIcd10DrugRelation(icd10Id, drugId);
        assertTrue("新增失败！", result);
    }

    @Test
    public void htestUpdateIcd10DrugRelation() throws Exception{
        String code_icd10 = "Icd10DictTest_07";
        String name_icd10 = "ICD10字典测试_07";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_07。";
        icd10DictController.createIcd10Dict(code_icd10, name_icd10, chronicFlag, infectiousFlag, description_icd10);
        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code_icd10);
        String icd10Id = icd10Dict.getId().toString();

        String code = "DrugDictTest_07";
        String name = "Drug字典测试_07";
        String type = "1";
        String flag = "0";
        String tradeName = "商品名称_07";
        String unit = "盒";
        String specifications = "12包";
        String description = "Drug字典新增测试。";
        drugDictController.createDrugDict(code, name, type, flag,tradeName,unit, specifications,description);
        DrugDict drugDict = drugDictRepo.findByCode(code);
        String drugId = drugDict.getId();

        Icd10Dict icd10Dict1 = icd10DictRepository.findByCode("Icd10DictTest_06");
        String id = icd10Dict1.getId().toString();
        List<Icd10DrugRelation> icd10DrugRelations = (List<Icd10DrugRelation>)icd10DrugRelationRepository.findByIcd10Id(id);
        String relaId = icd10DrugRelations.get(0).getId();

        boolean result = (boolean)icd10DictController.updateIcd10DrugRelation(relaId, icd10Id, drugId);

        assertTrue("修改关联失败！", result);

        drugDictController.deleteDrugDict(drugId);

        drugDict = drugDictRepo.findByCode("DrugDictTest_06");
        drugId = drugDict.getId();
        drugDictController.deleteDrugDict(drugId);
    }

    //-------------------------------ICD10与指标关联关系管理---------------------------------------------------------------
    @Test
    public void itestCreateIcd10IndicatorRelation() throws Exception{
        String code_icd10 = "Icd10DictTest_08";
        String name_icd10 = "ICD10字典测试_08";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_08。";
        icd10DictController.createIcd10Dict(code_icd10, name_icd10, chronicFlag, infectiousFlag, description_icd10);
        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code_icd10);
        String icd10Id = icd10Dict.getId().toString();

        String code = "IndicatorDictTest_08";
        String name = "指标字典测试_08";
        String type = "1";
        String unit = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";
        String description = "指标字典新增测试_08。";
        indicatorsDictController.createIndicatorsDict(code, name, type, unit, upperLimit, lowerLimit, description);
        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode(code);
        String indicatorsDictId = indicatorsDict.getId();

        boolean result = (boolean)icd10DictController.createIcd10IndicatorRelation(icd10Id, indicatorsDictId);
        assertTrue("新增失败！", result);
    }

    @Test
    public void jtestUpdateIcd10IndicatorRelation() throws Exception{
        String code_icd10 = "Icd10DictTest_09";
        String name_icd10 = "ICD10字典测试_09";
        String chronicFlag = "0";
        String infectiousFlag = "1";
        String description_icd10 = "ICD10字典新增测试_09。";
        icd10DictController.createIcd10Dict(code_icd10, name_icd10, chronicFlag, infectiousFlag, description_icd10);
        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code_icd10);
        String icd10Id = icd10Dict.getId().toString();

        String code = "IndicatorDictTest_09";
        String name = "指标字典测试_09";
        String type = "1";
        String unit = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";
        String description = "指标字典新增测试_09。";
        indicatorsDictController.createIndicatorsDict(code, name, type, unit, upperLimit, lowerLimit, description);
        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode(code);
        String indicatorsDictId = indicatorsDict.getId();

        Icd10Dict icd10Dict1 = icd10DictRepository.findByCode("Icd10DictTest_08");
        String id = icd10Dict1.getId().toString();
        List<Icd10IndicatorRelation> icd10IndicatorRelations = (List<Icd10IndicatorRelation>)icd10IndicatorRelationRepository.findByIcd10Id(id);
        String relaId = icd10IndicatorRelations.get(0).getId();

        boolean result = (boolean)icd10DictController.updateIcd10IndicatorRelation(relaId, icd10Id, indicatorsDictId);
        assertTrue("修改关联失败！", result);

        indicatorsDictController.deleteIndicatorsDict(indicatorsDictId);

        indicatorsDict = indicatorsDictRepo.findByCode("IndicatorDictTest_08");
        indicatorsDictId = indicatorsDict.getId();
        indicatorsDictController.deleteIndicatorsDict(indicatorsDictId);
    }

    @Test
    public void ztestDeleteIcd10Dict() throws Exception{
        //无关联删除
        Icd10Dict icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_02");
        String id = icd10Dict.getId().toString();
        boolean result = icd10DictController.deleteIcd10Dict(id);
        assertTrue("删除失败！", result);

        //有关联删除
        icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_06");
        id = icd10Dict.getId().toString();
        icd10DictController.deleteIcd10Dict(id);

        icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_07");
        id = icd10Dict.getId().toString();
        icd10DictController.deleteIcd10Dict(id);

        icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_08");
        id = icd10Dict.getId().toString();
        icd10DictController.deleteIcd10Dict(id);

        icd10Dict = icd10DictRepository.findByCode("Icd10DictTest_09");
        id = icd10Dict.getId().toString();
        icd10DictController.deleteIcd10Dict(id);

    }

}
