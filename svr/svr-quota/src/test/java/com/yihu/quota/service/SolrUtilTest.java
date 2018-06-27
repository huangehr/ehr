package com.yihu.quota.service;

import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.SvrQuotaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 张进军
 * @date 2018/6/20 17:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SvrQuotaApplication.class)
public class SolrUtilTest {

    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void queryDistinctOneFieldTest() {
        String[] fields = {"demographic_id", "org_name", "event_date"};
        String fq = "demographic_id:* AND create_date:[2018-06-13T00:00:00Z TO 2018-06-13T14:00:00Z]";
        try {
            solrUtil.queryDistinctOneField("HealthProfile", null, fq, null, 0, 1000, fields, "demographic_id", "create_date desc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
