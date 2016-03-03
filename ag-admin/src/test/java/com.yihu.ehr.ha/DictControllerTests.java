package com.yihu.ehr.ha;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yihu.ehr.agModel.standard.dict.DictEntryModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.ha.std.controller.DictController;
import com.yihu.ehr.util.Envelop;
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
import static org.junit.Assert.assertTrue;
/**
 * Created by wq on 2016/3/2.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DictControllerTests {

    private static String version = "000000000000";

    @Autowired
    private DictController dictController;

    ApplicationContext applicationContext;

    Envelop envelop = new Envelop();

    @Test
    public void atestDict() throws Exception{

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();


        envelop = dictController.createDict("450","code_test","name_test","32156412","source_test",version,"test","0dae0003561e059a49f6321ee017ef88");
        assertNotEquals("字典新增失败",envelop,null);

        envelop = dictController.updateDict("423", "code_test_copy", "name_test_copy", "2222222222", "source_test_copy", version, "test", "0dae0003561e059a49f6321ee017ef88");
        assertNotEquals("字典修改失败",envelop,null);

        envelop = dictController.getDictById(version, 423);
        assertNotEquals("获取字典信息失败",envelop,null);

        String ids = "451,452";
        envelop = dictController.deleteDict(version, "427,428");
        assertNotEquals("删除字典失败",envelop,null);

        envelop = dictController.searchDataSets("","","",15,1,version);
        assertNotEquals("查询字典失败",envelop,null);



    }

    @Test
    public void btestDict() throws JsonProcessingException {

        envelop = dictController.createDictEntry(version,"428","","entry_code_test","value_test","test");
        assertNotEquals("字典项新增失败",envelop,null);

        envelop = dictController.updateDictEntry(version,"428","41148","entry_code_test_copy","value_test_copy","test_copy");
        assertNotEquals("字典项修改失败",envelop,null);

        envelop = dictController.getDictEntryById(version,((DictEntryModel)envelop.getObj()).getId());
        assertNotEquals("获取字典项失败",envelop,null);

        envelop = dictController.deleteDictEntry(version,428,String.valueOf(((DictEntryModel) envelop.getObj()).getId()));
        assertNotEquals("删除字典项失败",envelop,null);

        envelop = dictController.searchDictEntry("","","",15,1,version);
        assertNotEquals("查询字典项失败",envelop,null);



    }

}
