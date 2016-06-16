package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.specialdict.Icd10DictModel;
import com.yihu.ehr.agModel.specialdict.IndicatorsDictModel;
import com.yihu.ehr.specialdict.controller.Icd10DictController;
import com.yihu.ehr.specialdict.controller.IndicatorDictController;
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
public class IndicatorDictControllerTests {

    private static String version = "v1.0";

    ApplicationContext applicationContext;

    @Autowired
    private IndicatorDictController indicatorDictController;
    @Autowired
    private Icd10DictController icd10DictController;
    @Autowired
    private ObjectMapper objectMapper;


    public static String icd10Id;
    public static String indicatorId;
    public static Envelop envelop = new Envelop();

    @Test
    public void atestCreateIndicatorDict() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        String code = "IndicatorDictTest_01";
        String name = "指标字典测试_01";
        String type = "1";
        String unit = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";
        String description = "指标字典新增测试。";

        Map<String,String> retrieveMap_1 = new HashMap<>();
        retrieveMap_1.put("code",code);
        retrieveMap_1.put("name",name);
        retrieveMap_1.put("type",type);
        retrieveMap_1.put("unit",unit);
        retrieveMap_1.put("upperLimit",upperLimit);
        retrieveMap_1.put("lowerLimit",lowerLimit);
        retrieveMap_1.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json_1 = objectMapper.writeValueAsString(retrieveMap_1);

        envelop = indicatorDictController.createIndicatorsDict(json_1);
        indicatorId = ((IndicatorsDictModel)envelop.getObj()).getId().toString();
        assertTrue("字典新增失败", envelop.isSuccessFlg());

        envelop = indicatorDictController.getIndicatorsDict(indicatorId);
        assertTrue("ID查字典失败", envelop.isSuccessFlg());
    }

    @Test
    public void btestUpdateIndicatorDict() throws Exception{
        String code = "IndicatorDictTest_01";
        String name = "指标字典测试_01";
        String type = "1";
        String unit = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";
        String description = "指标字典新增测试。";

        Map<String,String> retrieveMap_2 = new HashMap<>();
        retrieveMap_2.put("id",indicatorId);
        retrieveMap_2.put("code",code);
        retrieveMap_2.put("name",name);
        retrieveMap_2.put("type",type);
        retrieveMap_2.put("unit",unit);
        retrieveMap_2.put("upperLimit",upperLimit);
        retrieveMap_2.put("lowerLimit",lowerLimit);
        retrieveMap_2.put("description",description);

        String json_2 = objectMapper.writeValueAsString(retrieveMap_2);

        envelop = indicatorDictController.updateIndicatorsDict(json_2);
        assertTrue("字典更新失败", envelop.isSuccessFlg());
    }

    @Test
    public void dtestGetIcd10DictList() throws Exception{
        //---------- 无条件查询 ----------------
        String fields = "";
        String filters = "";
        String sorts = "";
        int page = 1;
        int size = 15;
        envelop = indicatorDictController.getIndicatorsDictList(fields, filters, sorts, size, page);
        assertTrue("无条件查询失败", envelop.isSuccessFlg());

        //---------- 全条件查询 ----------------
        String code = "IndicatorDictTest_02";
        String name = "指标字典测试_02";
        String type = "1";
        String unit = "度";
        String upperLimit = "14度";
        String lowerLimit = "1度";

        filters = "code=" + code;
        filters += ";name=" + name;
        filters += ";type=" + type;
        filters += ";unit=" + unit;
        filters += ";upperLimit=" + upperLimit;
        filters += ";lowerLimit=" + lowerLimit;

        fields = "id,code,name,type,typeName,flag,flagName,tradeName,unit,specifications,description";
        sorts = "+code,+name";

        envelop = indicatorDictController.getIndicatorsDictList(fields, filters, sorts, size, page);
        assertTrue("全条件查询失败", envelop.isSuccessFlg());
    }

    @Test
    public void etestIndicatorIsUsageDict() throws Exception{
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
        retrieveMap_3.put("description", description_icd10);

        String json_icd10 = objectMapper.writeValueAsString(retrieveMap_3);

        Envelop envelop_icd10 = icd10DictController.createIcd10Dict(json_icd10);
        Icd10DictModel icd10DictModel = (Icd10DictModel)envelop_icd10.getObj();
        icd10Id = icd10DictModel.getId();

        Map<String,String> retrieveMap_4 = new HashMap<>();
        retrieveMap_4.put("icd10Id",icd10Id);
        retrieveMap_4.put("indicatorId", indicatorId);

        String relaJson = objectMapper.writeValueAsString(retrieveMap_4);
        icd10DictController.createIcd10IndicatorRelation(relaJson);

        envelop = indicatorDictController.indicatorIsUsage(indicatorId);

        assertTrue("验证失败！", envelop.isSuccessFlg());
    }

    @Test
    public void ztestDeleteIndicator() throws Exception{
        envelop = indicatorDictController.deleteIndicatorsDict(indicatorId);
        assertTrue("删除失败", envelop.isSuccessFlg());

        icd10DictController.deleteIcd10Dict(icd10Id);
    }
}
