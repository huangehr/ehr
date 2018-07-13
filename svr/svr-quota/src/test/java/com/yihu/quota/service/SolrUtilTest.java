package com.yihu.quota.service;

import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.SvrQuotaApplication;
import org.apache.solr.client.solrj.response.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
        String fq = "demographic_id:*";
        try {
            List<Group> groupList = solrUtil.queryDistinctOneField("HealthProfile", null, fq, null, 0, 1000, fields, "demographic_id", "create_date desc");
            for (Group group : groupList) {
                System.out.println("done");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
