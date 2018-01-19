package com.yihu.quota.service;

import com.yihu.quota.SvrQuotaApplication;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

/**
 * 卫生机构类别统计 Test
 *
 * @author 张进军
 * @date 2017/12/28 19:47
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SvrQuotaApplication.class)
@WebAppConfiguration
public class OrgHealthCategoryStatisticsTest {

    @Autowired
    OrgHealthCategoryStatisticsService orgHealthCategoryStatisticsService;

    /**
     * 获取卫生机构类别树
     */
    @Test
    public void findTreeTest() {
        List<Map<String, Object>> list = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
        System.out.println("11");
    }

}
