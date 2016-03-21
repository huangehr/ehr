package com.yihu.ehr.std;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.StandardServiceApp;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.standard.stdsrc.controller.StandardSourceController;
import com.yihu.ehr.standard.stdsrc.service.StandardSource;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;

//@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StandardServiceApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StdSourceTests {

    @Autowired
    private StandardSourceController standardSourceController;

    @Test
    public void test() throws Exception{
        //测试时先把表中添加一条数据 id为0000000656ea58569225f91710c022e2
        StandardSource standardSource = new StandardSource();
        standardSource.setUpdateDate(new Date());
        standardSource.setCode("TESTSOUR");
        standardSource.setName("TESTSOUR");
        ObjectMapper objectMapper = new ObjectMapper();

        MStdSource rs = standardSourceController.addStdSource(objectMapper.writeValueAsString(standardSource));
        assertTrue("新增失败！", rs!=null);

        rs.setId("0000000656ea58569225f91710c022e2");
        rs.setCode("TEST_UPDATE");
        rs = standardSourceController.updateStdSource(rs.getId(), objectMapper.writeValueAsString(rs));

        rs = standardSourceController.getStdSource(rs.getId());
        assertTrue("查询数据为空！", rs != null);

        boolean b = standardSourceController.delStdSource(rs.getId());
        assertTrue("删除数量 0！", b);

        String ids = "";
        standardSource.setCode("test2223");
        rs = standardSourceController.addStdSource(objectMapper.writeValueAsString(standardSource));
        assertTrue("新增失败！", rs!=null);

        ids += "," + rs.getId();
        standardSource.setCode("test2224");
        rs = standardSourceController.addStdSource(objectMapper.writeValueAsString(standardSource));
        assertTrue("新增失败！", rs!=null);
        ids += "," + rs.getId();

        b = standardSourceController.delStdSources(ids.substring(1));
        assertTrue("删除数量 0！", b);
    }
}
