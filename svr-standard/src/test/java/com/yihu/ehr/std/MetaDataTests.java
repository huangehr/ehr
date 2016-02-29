package com.yihu.ehr.std;

import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MStdMetaData;
import com.yihu.ehr.standard.datasets.controller.MetaDataController;
import com.yihu.ehr.standard.datasets.service.IMetaData;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetaDataTests {

    @Autowired
    private MetaDataController metaDataController;

    String version = "000000000000";
    long id = 1;
    long dataSetId = 1;
    @Test
    public void testDeleteMetaData() throws Exception{

        boolean rs = metaDataController.deleteMetaData(id, version);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testDeleteMetaDatas() throws Exception{
        String ids = "1,2";
        boolean rs = metaDataController.deleteMetaDatas(ids, version);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testDeleteMetaDataByDataSet() throws Exception{
        boolean rs = metaDataController.deleteMetaDataByDataSet(dataSetId, version);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testSaveMetaSet() throws Exception{
        IMetaData model = new IMetaData();
        model.setCode("");
        model.setName("");
        model.setColumnLength("");
        model.setColumnName("");
        model.setColumnType("");
        model.setDataSetId(1);
        model.setDefinition("test");
        model.setFormat("");
        model.setInnerCode("");
        model.setNullable(true);
        model.setPrimaryKey(true);
        model.setType("");
        model.setDictId(1);
        boolean rs = metaDataController.saveMetaSet(version, model);
        assertTrue("新增失败！", rs);
    }

    @Test
    public void testUpdataMetaSet() throws Exception{
        IMetaData model = new IMetaData();
        model.setId(id);
        model.setCode("");
        model.setName("");
        model.setColumnLength("");
        model.setColumnName("");
        model.setColumnType("");
        model.setDataSetId(1);
        model.setDefinition("test");
        model.setFormat("");
        model.setInnerCode("");
        model.setNullable(true);
        model.setPrimaryKey(true);
        model.setType("");
        model.setDictId(1);
        boolean rs = metaDataController.updataMetaSet(version, model);
        assertTrue("保存失败！", rs);
    }

    @Test
    public void testGetMetaData() throws Exception{
        MStdMetaData mStdMetaData = metaDataController.getMetaData(id, version);
        assertTrue("获取信息失败！", mStdMetaData != null);
    }

    @Test
    public void testValidateCode() throws Exception{
        String code = "";
        boolean rs = metaDataController.validateCode(version, dataSetId, code);
        assertTrue("code不重复！", rs);
    }

    @Test
    public void testValidatorName() throws Exception{
        String name = "";
        boolean rs = metaDataController.validatorName(version, dataSetId, name);
        assertTrue("name不重复！", rs);
    }
}
