package com.yihu.ehr.ha;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.ha.std.controller.DataSetController;
import com.yihu.ehr.ha.std.service.DataSetClient;
import com.yihu.ehr.util.Envelop;
import net.minidev.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.security.pkcs11.P11Util;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by wq on 2016/2/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataSetControllerTests {

    private static String apiVersion = "v1.0";
    private static String version = "000000000000";
    static String datasetId;

    @Autowired
    private DataSetController dataSetController;

    ApplicationContext applicationContext;

    Envelop envelop = new Envelop();
    Map<String,Object> map = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();


    @Test
    public void atestDataSet() throws JsonProcessingException {

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        String id = "";
        String fields = "";
        String filters = "name=name_test";
        String sorts = "";

        envelop = dataSetController.saveDataSet(id,"code_test","name_test","refStandard_test","summary_test",version);
        envelop = dataSetController.saveDataSet(id,"code_test1","name_test","refStandard_test","summary_test",version);
        envelop = dataSetController.saveDataSet(id,"code_test2","name_test","refStandard_test","summary_test",version);
        envelop = dataSetController.saveDataSet(id,"code_test3","name_test","refStandard_test","summary_test",version);
        envelop = dataSetController.saveDataSet(id,"code_test4","name_test","refStandard_test","summary_test",version);
        envelop = dataSetController.saveDataSet(id,"code_test5","name_test","refStandard_test","summary_test",version);
        assertTrue("新增数据集信息失败", envelop.isSuccessFlg());

        envelop = dataSetController.getDataSetByCodeName(fields, filters, sorts, 15, 1, version);
        assertNotEquals("查询数据集列表失败", envelop,null);

        StringBuffer ids = new StringBuffer();
        for (int i = 1;i<(envelop.getDetailModelList().size());i++){
            id = String.valueOf(((DataSetModel) envelop.getDetailModelList().get(i)).getId());
            ids.append(id+",");
        }
        Envelop envelop1 = dataSetController.deleteDataSet(ids.toString(),version);
        assertTrue("批量删除数据集信息失败", envelop1.isSuccessFlg());

        envelop = dataSetController.getDataSet(((DataSetModel)envelop.getDetailModelList().get(0)).getId(),version);
        assertNotEquals("获取数据集信息失败", envelop, null);

        envelop = dataSetController.saveDataSet(String.valueOf(((DataSetModel) envelop.getObj()).getId()),"code_copy","name_copy","refStandard_test","summary_test",version);
        assertTrue("修改数据集信息失败", envelop.isSuccessFlg());

        datasetId = String.valueOf(((DataSetModel) envelop.getObj()).getId());

    }

    @Test
    public void btestMetaData()throws Exception{

        map.put("id","");
        map.put("code","metadata_code_test");
        map.put("name","metadata_name_test");
        map.put("dataSetId",datasetId);
        map.put("innerCode","metadata_innerCode_test");
        map.put("columnName","metadata_column_test");

        String metadataJsonData = mapper.writeValueAsString(map);
        envelop = dataSetController.saveMetaData(version, metadataJsonData);
        assertNotEquals("数据元新增失败",envelop,null);

        envelop = dataSetController.getMetaDataByCodeOrName("","name=metadata_name_test","",15,1,version);
        assertNotEquals("查询数据元失败",envelop,null);

        envelop = dataSetController.getMetaData(((MetaDataModel)envelop.getDetailModelList().get(0)).getId(),version);
        assertNotEquals("获取数据元失败",envelop,null);

        map.put("id",((MetaDataModel)envelop.getObj()).getId());
        map.put("code","metadata_name_copy");
        String updataMetadataJsonData = mapper.writeValueAsString(map);
        envelop = dataSetController.saveMetaData(version, updataMetadataJsonData);
        assertNotEquals("更新数据元失败",envelop,null);

        String metadataId = String.valueOf(((MetaDataModel) envelop.getObj()).getId());

        envelop = dataSetController.validatorMetadataCode(version, Long.valueOf(datasetId), "EVENT_NO");
        assertTrue("数据元代码重复", !envelop.isSuccessFlg());

        envelop = dataSetController.validatorMetadataName(version, Long.valueOf(datasetId), "name_test");
        assertTrue("数据元名称重复", !envelop.isSuccessFlg());

        envelop = dataSetController.deleteMetaData(metadataId,version);
        assertNotEquals("删除数据元失败",envelop,null);

    }
    @Test
    public void ztestdelete(){
        envelop = dataSetController.deleteDataSet(datasetId,version);
        assertTrue("删除数据集信息失败", envelop.isSuccessFlg());
    }
}
