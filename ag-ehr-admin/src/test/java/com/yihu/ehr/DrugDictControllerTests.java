package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.specialdict.DrugDictModel;
import com.yihu.ehr.agModel.specialdict.Icd10DictModel;
import com.yihu.ehr.specialdict.controller.DrugDictController;
import com.yihu.ehr.specialdict.controller.Icd10DictController;
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
public class DrugDictControllerTests {
    ApplicationContext applicationContext;
    @Autowired
    private DrugDictController drugDictController;
    @Autowired
    private Icd10DictController icd10DictController;
    @Autowired
    private ObjectMapper objectMapper;

    private static String version = "v1.0";
    public static long drugId;
    public static long icd10Id;
    public static Envelop envelop = new Envelop();

    @Test
    public void atestCreateDrugDict() throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        String code = "DrugDictTest_01";
        String name = "Drug字典测试_01";
        String type = "1";
        String flag = "1";
        String tradeName = "商品名称_01";
        String unit = "盒";
        String specifications = "12包";
        String description = "Drug字典新增测试。";

        Map<String,String> retrieveMap_1 = new HashMap<>();
        retrieveMap_1.put("code",code);
        retrieveMap_1.put("name",name);
        retrieveMap_1.put("type",type);
        retrieveMap_1.put("flag",flag);
        retrieveMap_1.put("tradeName",tradeName);
        retrieveMap_1.put("unit",unit);
        retrieveMap_1.put("specifications",specifications);
        retrieveMap_1.put("description",description);

        ObjectMapper objectMapper = new ObjectMapper();
        String json_1 = objectMapper.writeValueAsString(retrieveMap_1);
        envelop = drugDictController.createDrugDict(json_1);

        drugId = ((DrugDictModel)envelop.getObj()).getId();
        assertTrue("字典新增失败", envelop.isSuccessFlg());

        envelop = drugDictController.getDrugDict(drugId);
        assertTrue("ID查字典失败", envelop.isSuccessFlg());
    }

    @Test
    public void btestUpdateDrugDict() throws Exception{
        String code = "DrugDictTest_02";
        String name = "Drug字典测试_02";
        String type = "2";
        String flag = "2";
        String tradeName = "商品名称_02";
        String unit = "瓶";
        String specifications = "500ml";
        String description = "Drug字典更新测试。";

        Map<String,Object> retrieveMap_2 = new HashMap<>();
        retrieveMap_2.put("id",drugId);
        retrieveMap_2.put("code",code);
        retrieveMap_2.put("name",name);
        retrieveMap_2.put("type",type);
        retrieveMap_2.put("flag",flag);
        retrieveMap_2.put("tradeName",tradeName);
        retrieveMap_2.put("unit",unit);
        retrieveMap_2.put("specifications",specifications);
        retrieveMap_2.put("description",description);

        String json_2 = objectMapper.writeValueAsString(retrieveMap_2);
        envelop = drugDictController.updateDrugDict(json_2);

        assertTrue("字典更新失败", envelop.isSuccessFlg());
    }

    @Test
    public void dtestGetDrugDictList() throws Exception{
        String fields = "";
        String filters = "";
        String sorts = "";
        int page = 1;
        int size = 15;
        envelop = drugDictController.getDrugDictList(fields, filters, sorts, size, page);
        assertTrue("无条件查询失败", envelop.isSuccessFlg());

        String code = "DrugDictTest_02";
        String name = "Drug字典测试_02";
        String type = "2";
        String flag = "2";
        String tradeName = "商品名称_02";
        String unit = "瓶";
        String specifications = "500ml";
        String description = "Drug字典更新测试。";

        filters = "code=" + code;
        filters += ";name=" + name;
        filters += ";type=" + type;
        filters += ";flag=" + flag;
        filters += ";tradeName=" + tradeName;
        filters += ";unit=" + unit;
        filters += ";specifications=" + specifications;
        filters += ";description=" + description;

        fields = "id,code,name,type,typeName,flag,flagName,tradeName,unit,specifications,description";
        sorts = "+code,+name";

        envelop = drugDictController.getDrugDictList(fields, filters, sorts, size, page);
        assertTrue("全条件查询失败", envelop.isSuccessFlg());
    }

    @Test
    public void etestDrugIsUsageDict() throws Exception{
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
        retrieveMap_4.put("drugId",drugId);

        String relaJson = objectMapper.writeValueAsString(retrieveMap_4);
        icd10DictController.createIcd10DrugRelation(relaJson);

        envelop = drugDictController.isUsage(drugId);

        assertTrue("验证失败！", envelop.isSuccessFlg());
    }

    @Test
    public void ztestDeleteDrugDict() throws Exception{
        envelop = drugDictController.deleteDrugDict(drugId);
        assertTrue("删除失败", envelop.isSuccessFlg());

        icd10DictController.deleteIcd10Dict(icd10Id);
    }
}
