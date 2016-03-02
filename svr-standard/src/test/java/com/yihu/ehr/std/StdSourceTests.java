package com.yihu.ehr.std;

import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.standard.standardsource.controller.StandardSourceController;
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
public class StdSourceTests {

    @Autowired
    private StandardSourceController standardSourceController;

    String id = "0dae0006568f806986faa62bd4d8ff44";

    @Test
    public void testDelStdSource() throws Exception{
        boolean rs = standardSourceController.delStdSource(id);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testDelStdSources() throws Exception{
        String ids = "0dae0006568f806986faa62bd4d8ff44,2";
        boolean rs = standardSourceController.delStdSources(ids);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testGetDataSet() throws Exception{
        MStdSource mStdSource = standardSourceController.getStdSource(id);
        assertTrue("查询数据为空！", mStdSource != null);
    }

    @Test
    public void testUpdateStdSource() throws Exception{

        String code = "test1";
        String name = "test1";
        String desc = "test1";
        String type = "1";
//        boolean rs = standardSourceController.updateStdSource(id, code, name, type, desc);
//        assertTrue("修改失败！", rs);
    }

    @Test
    public void testUpdateDataSet() throws Exception{
        String code = "test1";
        String name = "test1";
        String desc = "test1";
        String type = "1";
//        boolean rs = standardSourceController.addStdSource(code, name, type, desc);
//        assertTrue("新增失败！", rs);
    }
}
