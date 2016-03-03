package com.yihu.ehr.ha;

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

    @Autowired
    private DataSetController dataSetController;

    ApplicationContext applicationContext;

    Envelop envelop = new Envelop();


    @Test
    public void atestDataSet(){

        applicationContext = new SpringApplicationBuilder().web(false).sources(AgAdminApplication.class).run();

        long id = 0;
        String code = "code_test";
        String name = "name_test";
        String type = "type_test";
        String refStandard = "refStandard_test";
        String summary = "summary_test";

        String fields = "";
        String filters = "";
        String sorts = "";

        envelop = dataSetController.saveDataSet(apiVersion,id,code,name,type,refStandard,summary,version);
        assertTrue("新增数据集信息失败", envelop.isSuccessFlg());

        envelop = dataSetController.getDataSetByCodeName(fields, filters, sorts, 15, 1, version);
        assertNotEquals("查询数据集列表失败", envelop,null);

        //新增数据集没有返回对象，返回的是Boolean值，无法获取id，所以手动给id
        id = 80;
        envelop = dataSetController.getDataSet(id,version);
        assertNotEquals("获取数据集信息失败", envelop, null);

        code = "code_copy";
        name = "name_copy";
        envelop = dataSetController.saveDataSet(apiVersion,id,code,name,type,refStandard,summary,version);
        assertTrue("修改数据集信息失败", envelop.isSuccessFlg());

        envelop = dataSetController.deleteDataSet(id,version);
        assertTrue("删除数据集信息失败", envelop.isSuccessFlg());



    }

    @Test
    public void btestdeleteDataSet(){
        String ids = "58,59";
        envelop = dataSetController.deleteDataSet(ids,version);
        assertTrue("删除数据集信息失败", envelop.isSuccessFlg());
    }


    @Test
    public void ztestMetaData()throws Exception{

        Map<String,Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map.put("code","metadata_code");
        map.put("name","metadata_name");
        map.put("dataSetId",57);
        map.put("innerCode","metadata_innerCode");
        map.put("columnName","metadata_column");

        String metadataJsonData = mapper.writeValueAsString(map);
        envelop = dataSetController.updataMetaData(version, metadataJsonData);
        assertNotEquals("数据元新增失败",envelop,null);

        envelop = dataSetController.getMetaDataByCodeOrName("","code=EVENT_NO","",15,1,version);
        assertNotEquals("查询数据元失败",envelop,null);

        envelop = dataSetController.getMetaData(apiVersion,55,2840,version);
        assertNotEquals("获取数据元失败",envelop,null);

//        map.put("name","metadata_name_copy");
//        envelop = dataSetController.updataMetaData(version,metadataJsonData);
//        assertNotEquals("更新数据元失败",envelop,null);

        String ids = "2858";
        envelop = dataSetController.deleteMetaData(ids,version);
        assertNotEquals("删除数据元失败",envelop,null);

        envelop = dataSetController.validatorMetadataCode(version, 55, "EVENT_NO");
        assertTrue("数据元代码重复", envelop.isSuccessFlg());

        envelop = dataSetController.validatorMetadataName(version, 55, "name_test");
        assertTrue("数据元名称重复", !envelop.isSuccessFlg());


    }
}
