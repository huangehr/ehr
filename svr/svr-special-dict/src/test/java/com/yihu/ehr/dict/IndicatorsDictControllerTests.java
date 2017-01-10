//package com.yihu.ehr.dict;
//
//import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
//import com.yihu.ehr.SvrSpecialDictApplication;
//import com.yihu.ehr.dict.controller.Icd10DictController;
//import com.yihu.ehr.dict.controller.IndicatorsDictController;
//import com.yihu.ehr.dict.service.*;
//import com.yihu.ehr.exception.ApiException;
//import com.yihu.ehr.model.specialdict.MIcd10Dict;
//import com.yihu.ehr.model.specialdict.MIndicatorsDict;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by Cws on 2016/1/25.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SvrSpecialDictApplication.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class IndicatorsDictControllerTests {
//
//    ApplicationContext applicationContext;
//
//    @Autowired
//    private IndicatorsDictController indicatorsDictController;
//    @Autowired
//    private XIndicatorsDictRepository indicatorsDictRepo;
//    @Autowired
//    private Icd10DictController icd10DictController;
//    @Autowired
//    private XIcd10DictRepository icd10DictRepository;
//
//    @Test
//    public void atestCreateIndicatorDict() throws Exception{
//        applicationContext = new SpringApplicationBuilder()
//                .web(false).sources(SvrSpecialDictApplication.class).run();
//
//        String code = "IndicatorDictTest_01";
//        String name = "指标字典测试_01";
//        String type = "1";
//        String unit = "度";
//        String upperLimit = "14度";
//        String lowerLimit = "1度";
//        String description = "指标字典新增测试。";
//
//        Map<String,String> retrieveMap = new HashMap<>();
//        retrieveMap.put("code",code);
//        retrieveMap.put("name",name);
//        retrieveMap.put("type",type);
//        retrieveMap.put("unit",unit);
//        retrieveMap.put("upperLimit",upperLimit);
//        retrieveMap.put("lowerLimit",lowerLimit);
//        retrieveMap.put("description",description);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(retrieveMap);
//
//        assertTrue("指标字典新增失败！" , indicatorsDictController.createIndicatorsDict(json) != null);
//
//        assertTrue("存在性验证失败！", indicatorsDictController.isCodeExists("IndicatorDictTest_01"));
//        assertTrue("存在性验证失败！", indicatorsDictController.isNameExists("指标字典测试_01"));
//    }
//
//    @Test
//    public void btestUpdateIndicatorDict() throws Exception{
//        String code = "IndicatorDictTest_02";
//        String name = "指标字典测试_02";
//        String type = "1";
//        String unit = "度";
//        String upperLimit = "14度";
//        String lowerLimit = "1度";
//        String description = "指标字典更新测试。Only a test";
//
//        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode("IndicatorDictTest_01");
//        String id = indicatorsDict.getId().toString();
//
//        Map<String,String> retrieveMap = new HashMap<>();
//        retrieveMap.put("id",id);
//        retrieveMap.put("code",code);
//        retrieveMap.put("name",name);
//        retrieveMap.put("type",type);
//        retrieveMap.put("unit",unit);
//        retrieveMap.put("upperLimit",upperLimit);
//        retrieveMap.put("lowerLimit",lowerLimit);
//        retrieveMap.put("description",description);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(retrieveMap);
//
//        indicatorsDictController.updateIndicatorsDict(json);
//
//        indicatorsDict = indicatorsDictRepo.findByCode("IndicatorDictTest_02");
//        boolean result = indicatorsDict.getId().toString().equals(id);
//        result = result&&indicatorsDict.getCode().toString().equals(code);
//        result = result&&indicatorsDict.getName().toString().equals(name);
//        result = result&&indicatorsDict.getType().toString().equals(type);
//        result = result&&indicatorsDict.getUnit().toString().equals(unit);
//        result = result&&indicatorsDict.getUpperLimit().toString().equals(upperLimit);
//        result = result&&indicatorsDict.getLowerLimit().toString().equals(lowerLimit);
//        result = result&&indicatorsDict.getDescription().toString().equals(description);
//
//        assertTrue("指标字典更新失败！", result);
//    }
//
//    @Test
//    public void ctestGetIndicatorDictById() throws Exception{
//
//        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode("IndicatorDictTest_02");
//        String id = indicatorsDict.getId().toString();
//
//        MIndicatorsDict result = (MIndicatorsDict)indicatorsDictController.getIndicatorsDict(id);
//
//        assertTrue("指标字典查询失败！", result != null);
//    }
//
//    @Test
//    public void dtestGetIcd10DictList() throws Exception{
//        String code = "IndicatorDictTest_02";
//        String name = "指标字典测试_02";
//        String type = "1";
//        String unit = "度";
//        String upperLimit = "14度";
//        String lowerLimit = "1度";
//
//        String filters = "code=" + code;
//        filters += ";name=" + name;
//        filters += ";type=" + type;
//        filters += ";unit=" + unit;
//        filters += ";upperLimit=" + upperLimit;
//        filters += ";lowerLimit=" + lowerLimit;
//
//        String fields = "id,code,name,type,unit,upperLimit,lowerLimit,description";
//        String sorts = "+code,+name";
//
//        List<MIndicatorsDict> indicatorsDictList =  (List<MIndicatorsDict>)indicatorsDictController.getIndicatorsDictList(fields, filters, sorts, 15, 1, null, null);
//        assertTrue("查询失败！" , indicatorsDictList.size() != 0);
//    }
//
//    @Test
//    public void etestIndicatorIsUsageDict() throws Exception{
//        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode("IndicatorDictTest_02");
//        String id = indicatorsDict.getId().toString();
//
//        String code = "Icd10DictTest_05";
//        String name = "ICD10字典测试_05";
//        String chronicFlag = "1";
//        String infectiousFlag = "1";
//        String description = "ICD10字典新增测试_05。";
//
//        Map<String,String> retrieveMap = new HashMap<>();
//        retrieveMap.put("code",code);
//        retrieveMap.put("name",name);
//        retrieveMap.put("chronicFlag",chronicFlag);
//        retrieveMap.put("infectiousFlag",infectiousFlag);
//        retrieveMap.put("description",description);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String icd10Json = objectMapper.writeValueAsString(retrieveMap);
//
//        icd10DictController.createIcd10Dict(icd10Json);
//        Icd10Dict icd10Dict = icd10DictRepository.findByCode(code);
//        String icd10Id = icd10Dict.getId();
//
//        Map<String,String> retrieveMap02 = new HashMap<>();
//        retrieveMap02.put("icd10Id",icd10Id);
//        retrieveMap02.put("indicatorId",id);
//
//        String relaJson = objectMapper.writeValueAsString(retrieveMap02);
//
//        icd10DictController.createIcd10IndicatorRelation(relaJson);
//        boolean result = indicatorsDictController.indicatorIsUsage(id);
//
//        assertTrue("验证失败！", result);
//
//        icd10DictController.deleteIcd10Dict(icd10Id);
//    }
//
//    @Test
//    public void ztestDeleteIndicator() throws Exception{
//        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode("IndicatorDictTest_02");
//        String id = indicatorsDict.getId().toString();
//        boolean result = indicatorsDictController.deleteIndicatorsDict(id);
//
//        assertTrue("删除失败！", result);
//    }
//
//}
