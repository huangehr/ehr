package com.yihu.ehr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yihu.ehr.agModel.standard.dict.DictEntryModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.std.controller.DictController;
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

import static org.junit.Assert.assertNotEquals;
/**
 * Created by wq on 2016/3/2.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DictControllerTests {

    private static String version = "000000000000";
    static String dictId;

    @Autowired
    private DictController dictController;

    ApplicationContext applicationContext;

    Envelop envelop = new Envelop();

    @Test
    public void atestDict() throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

//
//        envelop = dictController.saveDict("0", "code_test1", "name_test", "32156412", "source_test", version, "test", "0dae0003561e059a49f6321ee017ef88");
//        envelop = dictController.saveDict("0", "code_test2", "name_test", "32156412", "source_test", version, "test", "0dae0003561e059a49f6321ee017ef88");
//        envelop = dictController.saveDict("0", "code_test3", "name_test", "32156412", "source_test", version, "test", "0dae0003561e059a49f6321ee017ef88");
//        envelop = dictController.saveDict("0", "code_test4", "name_test", "32156412", "source_test", version, "test", "0dae0003561e059a49f6321ee017ef88");
//        envelop = dictController.saveDict("0", "code_test5", "name_test", "32156412", "source_test", version, "test", "0dae0003561e059a49f6321ee017ef88");
//        assertNotEquals("字典新增失败",envelop,null);

        envelop = dictController.searchDicts("", "name=name_test", "", 15, 1, version);
        assertNotEquals("查询字典失败",envelop,null);

        StringBuffer ids = new StringBuffer();
        for (int i = 1;i<(envelop.getDetailModelList().size());i++){
            String id = String.valueOf(((DictModel) envelop.getDetailModelList().get(i)).getId());
            ids.append(id+",");
        }
        Envelop envelop1 = dictController.deleteDict(version, ids.toString());
        assertNotEquals("批量删除字典失败",envelop,null);

//        envelop = dictController.saveDict(String.valueOf(((DictModel) envelop.getDetailModelList().get(0)).getId()), "code_test_copy", "name_test_copy", "22", "source_test_copy", version, "test", "0dae0003561e059a49f6321ee017ef88");
//        assertNotEquals("字典修改失败",envelop,null);

        envelop = dictController.getDictById(version, ((DictModel)envelop.getObj()).getId());
        assertNotEquals("获取字典信息失败",envelop,null);

        dictId = String.valueOf(((DictModel) envelop.getObj()).getId());

    }

    @Test
    public void btestDict() throws JsonProcessingException {
//
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test1", "value_test", "test1");
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test2", "value_test", "test2");
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test3", "value_test", "test3");
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test4", "value_test", "test4");
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test5", "value_test", "test5");
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test6", "value_test", "test6");
//        envelop = dictController.saveDictEntry(version, dictId, "", "entry_code_test7", "value_test", "test7");
        assertNotEquals("字典项新增失败",envelop,null);

        envelop = dictController.searchDictEntry("","dictId=2","",15,1,version);
        assertNotEquals("查询字典项失败",envelop,null);

        StringBuffer ids = new StringBuffer();
        for (int i = 1;i<(envelop.getDetailModelList().size());i++){
            String id = String.valueOf(((DictEntryModel) envelop.getDetailModelList().get(i)).getId());
            ids.append(id+",");
        }
        //Envelop envelop1 = dictController.deleteDictEntry(version,Long.valueOf(dictId),ids.toString());
        Envelop envelop1 = dictController.deleteDictEntry(version,ids.toString());
        assertNotEquals("批量删除字典项失败",envelop1,null);

//        envelop = dictController.saveDictEntry(version,dictId,String.valueOf(((DictEntryModel) envelop.getDetailModelList().get(0)).getId()),"entry_code_test_copy","value_test_copy","test_copy");
//        assertNotEquals("字典项修改失败",envelop,null);

        envelop = dictController.getDictEntryById(version,((DictEntryModel)envelop.getObj()).getId());
        assertNotEquals("获取字典项失败",envelop,null);

        //envelop = dictController.deleteDictEntry(version,Long.valueOf(dictId),String.valueOf(((DictEntryModel) envelop.getObj()).getId()));
        envelop = dictController.deleteDictEntry(version,String.valueOf(((DictEntryModel) envelop.getObj()).getId()));

        assertNotEquals("删除字典项失败",envelop,null);

    }

    @Test
    public void ztestDelete(){
        envelop = dictController.deleteDict(version, dictId);
        assertNotEquals("删除字典失败",envelop,null);
    }

}
