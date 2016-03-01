package com.yihu.ehr.ha;

import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.ha.std.controller.DataSetController;
import com.yihu.ehr.ha.std.service.DataSetClient;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Autowired
    private DataSetController dataSetController;

    Envelop envelop = new Envelop();

    @Test
    public void atestDataSet(){

        long id = 0;
        String code = "code_test";
        String name = "name_test";
        String type = "type_test";
        String refStandard = "refStandard_test";
        String summary = "summary_test";
        String version = "000000000000";

        String fields = "";
        String filters = "";
        String sorts = "";

        envelop = dataSetController.saveDataSet(apiVersion,id,code,name,type,refStandard,summary,version);
        assertTrue("新增数据集信息失败", envelop.isSuccessFlg());

        envelop = dataSetController.getDataSetByCodeName(fields, filters, sorts, 15, 1, version);
        assertNotEquals("查询数据集列表失败", envelop,null);

        id = 60;
        envelop = dataSetController.getDataSet(id,version);
        assertNotEquals("获取数据集信息失败", envelop, null);

        envelop = dataSetController.saveDataSet(apiVersion,id,code,name,type,refStandard,summary,version);
        assertTrue("修改数据集信息失败", envelop.isSuccessFlg());

        envelop = dataSetController.deleteDataSet(id,version);
        assertTrue("删除数据集信息失败", envelop.isSuccessFlg());

        String ids = "60,61";
        envelop = dataSetController.deleteDataSet(ids,version);
        assertTrue("删除数据集信息失败", envelop.isSuccessFlg());

    }

    @Test
    public void btestMetaData(){

    }
}
