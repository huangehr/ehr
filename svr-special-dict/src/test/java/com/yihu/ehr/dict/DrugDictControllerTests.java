package com.yihu.ehr.dict;

import com.yihu.ehr.SvrSpecialDictApplication;
import com.yihu.ehr.dict.controller.DrugDictController;
import com.yihu.ehr.dict.controller.Icd10DictController;
import com.yihu.ehr.dict.service.DrugDict;
import com.yihu.ehr.dict.service.Icd10Dict;
import com.yihu.ehr.dict.service.XDrugDictRepository;
import com.yihu.ehr.dict.service.XIcd10DictRepository;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MDrugDict;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Cws on 2016/1/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrSpecialDictApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DrugDictControllerTests {

    ApplicationContext applicationContext;

    @Autowired
    private DrugDictController drugDictController;
    @Autowired
    private XDrugDictRepository drugDictRepo;
    @Autowired
    private Icd10DictController icd10DictController;
    @Autowired
    private XIcd10DictRepository icd10DictRepository;

    @Test
    public void atestCreateDrugDict() throws Exception{
//        applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(SvrSpecialDictApplication.class).run();

        String code = "DrugDictTest_01";
        String name = "Drug字典测试_01";
        String type = "1";
        String flag = "0";
        String tradeName = "商品名称_01";
        String unit = "盒";
        String specifications = "12包";
        String description = "Drug字典新增测试。";

        drugDictController.createDrugDict(code, name, type, flag,tradeName,unit, specifications,description);
        DrugDict drugDict = drugDictRepo.findByCode(code);

        assertTrue("Drug字典新增失败！" , drugDict != null);
    }

    @Test
    public void btestCreateDrugDictCodeRepeat() throws Exception{
        try{
            String code = "DrugDictTest_01";
            String name = "Drug字典测试_02";
            String type = "1";
            String flag = "0";
            String tradeName = "商品名称_02";
            String unit = "盒";
            String specifications = "12包";
            String description = "Drug字典新增测试。";

            Object result = drugDictController.createDrugDict(code, name, type, flag, tradeName, unit, specifications, description);

        }
        catch (ApiException e){
            assertTrue("Drug字典code重复验证失败！", true);
        }
    }

    @Test
    public void ctestCreateDrugDictNameRepeat() throws Exception{
        try{
            String code = "DrugDictTest_02";
            String name = "Drug字典测试_01";
            String type = "1";
            String flag = "0";
            String tradeName = "商品名称_02";
            String unit = "盒";
            String specifications = "12包";
            String description = "Drug字典新增测试。";

            Object result = drugDictController.createDrugDict(code, name, type, flag,tradeName,unit, specifications,description);
        }
        catch (ApiException e){
            assertTrue("Drug字典name重复验证失败！", true);
        }
    }

    @Test
    public void dtestUpdateDrugDict() throws Exception{
        String code = "DrugDictTest_02";
        String name = "Drug字典测试_02";
        String type = "1";
        String flag = "1";
        String tradeName = "商品名称_02";
        String unit = "瓶";
        String specifications = "500ml";
        String description = "Drug字典更新测试。";

        DrugDict drugDict = drugDictRepo.findByCode("DrugDictTest_01");
        String id = drugDict.getId().toString();

        drugDictController.updateDrugDict(id, code, name, type, flag, tradeName, unit, specifications,description);

        drugDict = drugDictRepo.findByCode("DrugDictTest_02");
        boolean result = drugDict.getId().toString().equals(id);
        result = result&&(drugDict.getCode().toString().equals(code));
        result = result&&(drugDict.getName().toString().equals(name));
        result = result&&(drugDict.getType().toString().equals(type));
        result = result&&(drugDict.getFlag().toString().equals(flag));
        result = result&&(drugDict.getTradeName().toString().equals(tradeName));
        result = result&&(drugDict.getUnit().toString().equals(unit));
        result = result&&(drugDict.getSpecifications().toString().equals(specifications));
        result = result&&(drugDict.getDescription().toString().equals(description));

        assertTrue("Drug字典更新失败！", result);
    }

    @Test
    public void etestGetDrugDictById() throws Exception{

        DrugDict drugDict = drugDictRepo.findByCode("DrugDictTest_02");
        String id = drugDict.getId().toString();

        MDrugDict result = (MDrugDict)drugDictController.getDrugDict(id);

        assertTrue("Drug字典更新失败！", result != null);
    }

    @Test
    public void ftestGetDrugDictList() throws Exception{
        String code = "DrugDictTest_02";
        String name = "Drug字典测试_02";
        String type = "1";
        String flag = "1";
        String tradeName = "商品名称_02";
        String unit = "瓶";
        String specifications = "500ml";
        String description = "Drug字典更新测试。";

        String filters = "code=" + code;
        filters += ";name=" + name;
        filters += ";type=" + type;
        filters += ";flag=" + flag;
        filters += ";tradeName=" + tradeName;
        filters += ";unit=" + unit;
        filters += ";specifications=" + specifications;
        filters += ";description=" + description;

        String fields = "id,code,name,type,flag,tradeName,unit,specifications,description";
        String sorts = "+code,+name";

        List<MDrugDict> drugDictList =  (List<MDrugDict>)drugDictController.getDrugDictList(fields, filters, sorts, 15, 1, null, null);
        assertTrue("查询失败！" , drugDictList.size() != 0);
    }

    @Test
    public void gtestDrugIsUsageDict() throws Exception{
        DrugDict drugDict = drugDictRepo.findByCode("DrugDictTest_02");
        String id = drugDict.getId().toString();

        String code = "Icd10DictTest_05";
        String name = "ICD10字典测试_05";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试_05。";
        icd10DictController.createIcd10Dict(code,name,chronicFlag,infectiousFlag,description);
        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code);
        String icd10Id = icd10Dict.getId();

        icd10DictController.createIcd10DrugRelation(icd10Id,id);
        boolean result = drugDictController.drugIsUsage(id);

        assertTrue("验证失败！", result);

        icd10DictController.deleteIcd10Dict(icd10Id);
    }

    @Test
    public void ztestDeleteDrugDict() throws Exception{
        DrugDict drugDict = drugDictRepo.findByCode("DrugDictTest_02");
        String id = drugDict.getId().toString();
        boolean result = drugDictController.deleteDrugDict(id);

        assertTrue("删除失败！", result);
    }
}
