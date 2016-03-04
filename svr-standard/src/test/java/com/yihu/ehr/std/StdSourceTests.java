package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.standard.standardsource.controller.StandardSourceController;
import com.yihu.ehr.standard.standardsource.service.StandardSource;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertTrue;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StdSourceTests {

    @Autowired
    private StandardSourceController standardSourceController;

    String id = "0dae0006568f839386faa62bd4d8ff55";

    @Test
    public void testDelStdSource() throws Exception{
        boolean rs = standardSourceController.delStdSource(id);
        assertTrue("删除数量 0！", rs);
    }

    @Test
    public void testDelStdSources() throws Exception{
        String ids = "0dae0006568f839386faa62bd4d8ff56,2";
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
        MStdSource standardSource = new MStdSource();
        standardSource.setId("0dae0006568f839386faa62bd4d8ff55");
        standardSource.setUpdateDate(new Date());
        standardSource.setCode("dsfa");
        standardSource.setName("fdf");
        ObjectMapper objectMapper = new ObjectMapper();

        MStdSource rs = standardSourceController.updateStdSource(objectMapper.writeValueAsString(standardSource));
        assertTrue("修改失败！", rs!=null);
    }

    @Test
    public void testUpdateDataSet() throws Exception{
        StandardSource standardSource = new StandardSource();
        standardSource.setUpdateDate(new Date());
        standardSource.setCode("dsfa");
        standardSource.setName("fdf");
        ObjectMapper objectMapper = new ObjectMapper();
        MStdSource rs = standardSourceController.addStdSource(objectMapper.writeValueAsString(standardSource));
        assertTrue("新增失败！", rs!=null);
    }
}
