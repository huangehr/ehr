package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.specialdict.*;
import com.yihu.ehr.specialdict.controller.DrugDictController;
import com.yihu.ehr.specialdict.controller.HealthProblemDictController;
import com.yihu.ehr.specialdict.controller.Icd10DictController;
import com.yihu.ehr.specialdict.controller.IndicatorDictController;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
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
public class Icd10DictControllerTests {

    private static String version = "v1.0";
    public MIcd10Dict mIcd10Dict = null;

    ApplicationContext applicationContext;

    @Autowired
    private HealthProblemDictController hpDictController;
    @Autowired
    private Icd10DictController icd10DictController;
    @Autowired
    private DrugDictController drugDictController;
    @Autowired
    private IndicatorDictController indicatorDictController;

    @Autowired
    private ObjectMapper objectMapper;

    public static Long hpId;
    public static Long drugId;
    public static Long indectorId;
    public static Long icd10Id;
    public static Long hpIcd10RelaId;
    public static Long icd10DrugRelaId;
    public static Long icd10IndeRelaId;
    public static Envelop envelop = new Envelop();

    public static Long hpIdDel;
    public static Long drugIdDel;
    public static Long indectorIdDel;
    public static Long icd10IdDel;

    @Test
    public void atestCreateIcd10Dict() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        String code = "Icd10DictTest_01";
        String name = "ICD10字典测试_01";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试。";

        Map<String,String> retrieveMap_1 = new HashMap<>();
        retrieveMap_1.put("code",code);
        retrieveMap_1.put("name",name);
        retrieveMap_1.put("chronicFlag",chronicFlag);
        retrieveMap_1.put("infectiousFlag",infectiousFlag);
        retrieveMap_1.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json_1 = objectMapper.writeValueAsString(retrieveMap_1);

        envelop = icd10DictController.createIcd10Dict(json_1);
        icd10Id = ((Icd10DictModel)envelop.getObj()).getId();
        assertTrue("字典新增失败", envelop.isSuccessFlg());

        envelop = icd10DictController.getIcd10Dict(icd10Id);
        assertTrue("ID查字典失败", envelop.isSuccessFlg());
    }

    @Test
    public void btestUpdateIcd10Dict() throws Exception{
        String code = "Icd10DictTest_02";
        String name = "ICD10字典测试_02";
        String chronicFlag = "0";
        String infectiousFlag = "0";
        String description = "ICD10字典新增测试。";

        Map<String,Object> retrieveMap_2 = new HashMap<>();
        retrieveMap_2.put("id", icd10Id);
        retrieveMap_2.put("code",code);
        retrieveMap_2.put("name",name);
        retrieveMap_2.put("chronicFlag",chronicFlag);
        retrieveMap_2.put("infectiousFlag",infectiousFlag);
        retrieveMap_2.put("description",description);

        String json_2 = objectMapper.writeValueAsString(retrieveMap_2);

        envelop = icd10DictController.updateIcd10Dict(json_2);
        assertTrue("字典更新失败", envelop.isSuccessFlg());
    }

    @Test
    public void ftestGetIcd10DictList() throws Exception{
        //----------  无条件查询 ----------------
        String fields = "";
        String filters = "";
        String sorts = "";
        int page = 1;
        int size = 15;
        envelop = icd10DictController.getIcd10DictList(fields, filters, sorts, size, page);
        assertTrue("无条件查询失败", envelop.isSuccessFlg());

        //---------- 全条件查询 ----------------
        String code = "Icd10DictTest_02";
        String name = "ICD10字典测试_02";
        String chronicFlag = "0";
        String infectiousFlag = "0";

        filters = "code=" + code;
        filters += ";name=" + name;
        filters += ";chronicFlag=" + chronicFlag;
        filters += ";infectiousFlag=" + infectiousFlag;

        fields = "id,code,name,chronicFlag,infectiousFlag,description";
        sorts = "+code,+name";

        envelop = icd10DictController.getIcd10DictList(fields, filters, sorts, size, page);
        assertTrue("全条件查询失败", envelop.isSuccessFlg());
    }

    //-------------------------------ICD10与药品关联关系管理---------------------------------------------------------------
    @Test
    public void gtestCreateIcd10DrugRelation() throws Exception{
        String code = "Icd10DictTest_06";
        String name = "ICD10字典测试_06";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试_06。";

        Map<String,String> retrieveMap_6 = new HashMap<>();
        retrieveMap_6.put("code",code);
        retrieveMap_6.put("name",name);
        retrieveMap_6.put("chronicFlag",chronicFlag);
        retrieveMap_6.put("infectiousFlag",infectiousFlag);
        retrieveMap_6.put("description",description);

        String json_6 = objectMapper.writeValueAsString(retrieveMap_6);
        Envelop envelop_6 = icd10DictController.createIcd10Dict(json_6);
        Icd10DictModel icd10DictModel_6 = (Icd10DictModel)envelop_6.getObj();
        Long icd10Id_6 = icd10DictModel_6.getId();

        icd10IdDel = icd10Id_6;

        String code_drug = "DrugDictTest_06";
        String name_drug = "Drug字典测试_06";
        String type = "1";
        String flag = "1";
        String tradeName = "商品名称_06";
        String unit = "盒";
        String specifications = "12包";
        String description_drug = "Drug字典新增测试。";

        Map<String,String> retrieveMap_4 = new HashMap<>();
        retrieveMap_4.put("code",code_drug);
        retrieveMap_4.put("name",name_drug);
        retrieveMap_4.put("type",type);
        retrieveMap_4.put("flag",flag);
        retrieveMap_4.put("tradeName",tradeName);
        retrieveMap_4.put("unit",unit);
        retrieveMap_4.put("specifications",specifications);
        retrieveMap_4.put("description",description_drug);

        String json_4 = objectMapper.writeValueAsString(retrieveMap_4);
        Envelop envelop_4 = drugDictController.createDrugDict(json_4);
        Long drugId_4 = ((DrugDictModel)envelop_4.getObj()).getId();

        drugIdDel = drugId_4;

        Map<String,Object> retrieveMap_5 = new HashMap<>();
        retrieveMap_5.put("icd10Id",icd10Id_6);
        retrieveMap_5.put("drugId",drugId_4);

        String relaJson = objectMapper.writeValueAsString(retrieveMap_5);
        envelop = icd10DictController.createIcd10DrugRelation(relaJson);
        icd10DrugRelaId = ((Icd10DrugRelationModel)envelop.getObj()).getId();
        assertTrue("新增失败！", envelop.isSuccessFlg());

        envelop = icd10DictController.isIcd10DrugRelaExist(drugId_4,icd10Id_6);
        assertTrue("验证失败！", envelop.isSuccessFlg());
    }

    @Test
    public void htestUpdateIcd10DrugRelation() throws Exception{
        String code = "Icd10DictTest_07";
        String name = "ICD10字典测试_07";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试_07。";

        Map<String,String> retrieveMap_7 = new HashMap<>();
        retrieveMap_7.put("code",code);
        retrieveMap_7.put("name",name);
        retrieveMap_7.put("chronicFlag",chronicFlag);
        retrieveMap_7.put("infectiousFlag",infectiousFlag);
        retrieveMap_7.put("description",description);

        String json_7 = objectMapper.writeValueAsString(retrieveMap_7);
        Envelop envelop_7 = icd10DictController.createIcd10Dict(json_7);
        Icd10DictModel icd10DictModel_7 = (Icd10DictModel)envelop_7.getObj();
        Long icd10Id_7 = icd10DictModel_7.getId();

        String code_drug = "DrugDictTest_07";
        String name_drug = "Drug字典测试_07";
        String type = "1";
        String flag = "1";
        String tradeName = "商品名称_07";
        String unit = "盒";
        String specifications = "12包";
        String description_drug = "Drug字典新增测试。";

        Map<String,String> retrieveMap_8 = new HashMap<>();
        retrieveMap_8.put("code",code_drug);
        retrieveMap_8.put("name",name_drug);
        retrieveMap_8.put("type",type);
        retrieveMap_8.put("flag",flag);
        retrieveMap_8.put("tradeName",tradeName);
        retrieveMap_8.put("unit",unit);
        retrieveMap_8.put("specifications",specifications);
        retrieveMap_8.put("description",description_drug);

        String json_8 = objectMapper.writeValueAsString(retrieveMap_8);
        Envelop envelop_8 = drugDictController.createDrugDict(json_8);
        Long drugId_8 = ((DrugDictModel)envelop_8.getObj()).getId();

        Map<String,Object> retrieveMap_9 = new HashMap<>();
        retrieveMap_9.put("id", icd10DrugRelaId);
        retrieveMap_9.put("drugId", drugId_8);
        retrieveMap_9.put("icd10Id",icd10Id_7);

        String json_9 = objectMapper.writeValueAsString(retrieveMap_9);
        envelop = icd10DictController.updateIcd10DrugRelation(json_9);
        assertTrue("更新失败！", envelop.isSuccessFlg());

        icd10DictController.deleteIcd10Dict(icd10IdDel);
        drugDictController.deleteDrugDict(drugIdDel);

        icd10DictController.deleteIcd10Dict(icd10Id_7);
        drugDictController.deleteDrugDict(drugId_8);
    }

    //-------------------------------ICD10与指标关联关系管理---------------------------------------------------------------
    @Test
    public void itestCreateIcd10IndicatorRelation() throws Exception{
        String code = "Icd10DictTest_08";
        String name = "ICD10字典测试_08";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试_08。";

        Map<String,String> retrieveMap_10 = new HashMap<>();
        retrieveMap_10.put("code",code);
        retrieveMap_10.put("name",name);
        retrieveMap_10.put("chronicFlag",chronicFlag);
        retrieveMap_10.put("infectiousFlag",infectiousFlag);
        retrieveMap_10.put("description",description);

        String json_10 = objectMapper.writeValueAsString(retrieveMap_10);
        Envelop envelop_10 = icd10DictController.createIcd10Dict(json_10);
        Icd10DictModel icd10DictModel_10 = (Icd10DictModel)envelop_10.getObj();
        Long icd10Id_10 = icd10DictModel_10.getId();

        icd10IdDel = icd10Id_10;

        String code_in = "IndicatorDictTest_08";
        String name_in = "指标字典测试_08";
        String type_in = "1";
        String unit_in = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";
        String description_in = "指标字典新增测试_08。";

        Map<String,String> retrieveMap_11 = new HashMap<>();
        retrieveMap_11.put("code",code_in);
        retrieveMap_11.put("name",name_in);
        retrieveMap_11.put("type",type_in);
        retrieveMap_11.put("unit",unit_in);
        retrieveMap_11.put("upperLimit",upperLimit);
        retrieveMap_11.put("lowerLimit",lowerLimit);
        retrieveMap_11.put("description", description_in);

        String json_11 = objectMapper.writeValueAsString(retrieveMap_11);
        Envelop envelop_11 = indicatorDictController.createIndicatorsDict(json_11);
        Long indicatorId_11 = ((IndicatorsDictModel)envelop_11.getObj()).getId();

        indectorIdDel = indicatorId_11;

        Map<String,Object> retrieveMap_12 = new HashMap<>();
        retrieveMap_12.put("icd10Id",icd10Id_10);
        retrieveMap_12.put("indicatorId",indicatorId_11);

        String relaJson_12 = objectMapper.writeValueAsString(retrieveMap_12);
        envelop = icd10DictController.createIcd10IndicatorRelation(relaJson_12);
        icd10IndeRelaId = ((Icd10IndicatorRelationModel)envelop.getObj()).getId();
        assertTrue("新增关联失败！", envelop.isSuccessFlg());

        envelop = icd10DictController.isIcd10IndicatorsRelaExist(indicatorId_11,icd10Id_10);
        assertTrue("验证失败！", envelop.isSuccessFlg());
    }

    @Test
    public void jtestUpdateIcd10IndicatorRelation() throws Exception{
        String code = "Icd10DictTest_09";
        String name = "ICD10字典测试_09";
        String chronicFlag = "1";
        String infectiousFlag = "1";
        String description = "ICD10字典新增测试_09。";

        Map<String,String> retrieveMap_13 = new HashMap<>();
        retrieveMap_13.put("code",code);
        retrieveMap_13.put("name",name);
        retrieveMap_13.put("chronicFlag",chronicFlag);
        retrieveMap_13.put("infectiousFlag",infectiousFlag);
        retrieveMap_13.put("description",description);

        String json_13 = objectMapper.writeValueAsString(retrieveMap_13);
        Envelop envelop_13 = icd10DictController.createIcd10Dict(json_13);
        Icd10DictModel icd10DictModel_13 = (Icd10DictModel)envelop_13.getObj();
        Long icd10Id_13 = icd10DictModel_13.getId();

        String code_in = "IndicatorDictTest_09";
        String name_in = "指标字典测试_09";
        String type_in = "1";
        String unit_in = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";
        String description_in = "指标字典新增测试_09。";

        Map<String,String> retrieveMap_14 = new HashMap<>();
        retrieveMap_14.put("code",code_in);
        retrieveMap_14.put("name",name_in);
        retrieveMap_14.put("type",type_in);
        retrieveMap_14.put("unit",unit_in);
        retrieveMap_14.put("upperLimit",upperLimit);
        retrieveMap_14.put("lowerLimit",lowerLimit);
        retrieveMap_14.put("description",description_in);

        String json_14 = objectMapper.writeValueAsString(retrieveMap_14);
        Envelop envelop_14 = indicatorDictController.createIndicatorsDict(json_14);
        Long indicatorId_14 = ((IndicatorsDictModel)envelop_14.getObj()).getId();

        Map<String,Object> retrieveMap_15 = new HashMap<>();
        retrieveMap_15.put("id",icd10IndeRelaId);
        retrieveMap_15.put("icd10Id", icd10Id_13);
        retrieveMap_15.put("indicatorId", indicatorId_14);

        String relaJson_15 = objectMapper.writeValueAsString(retrieveMap_15);

        envelop = icd10DictController.updateIcd10IndicatorRelation(relaJson_15);
        assertTrue("更新关联失败！", envelop.isSuccessFlg());

        icd10DictController.deleteIcd10Dict(icd10IdDel);
        indicatorDictController.deleteIndicatorsDict(indectorIdDel);

        icd10DictController.deleteIcd10Dict(icd10Id_13);
        indicatorDictController.deleteIndicatorsDict(indicatorId_14);
    }

    @Test
    public void ztestDeleteIcd10Dict() throws Exception{
        envelop = icd10DictController.deleteIcd10Dict(icd10Id);
        assertTrue("删除失败", envelop.isSuccessFlg());
    }
}
