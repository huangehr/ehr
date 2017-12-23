package com.yihu.ehr.org;

import com.yihu.ehr.SvrEhrBasic;
import com.yihu.ehr.org.model.OrgHealthCategory;
import com.yihu.ehr.org.service.OrgHealthCategoryService;
import com.yihu.ehr.solr.SolrAdmin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卫生机构类别相关测试
 *
 * @author 张进军
 * @date 2017/12/22 17:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SvrEhrBasic.class)
@WebAppConfiguration
public class OrgHealthCategoryTest {

    @Autowired
    private OrgHealthCategoryService orgHealthCategoryService;
    @Autowired
    private SolrAdmin solrAdmin;

    /**
     * 将卫生机构类别导入到solr中
     */
    @Test
    public void importToSolr() throws Exception {
        List<OrgHealthCategory> list = orgHealthCategoryService.findAll();
        Map<String, Object> map;
        for (OrgHealthCategory item : list) {
            map = new HashMap<>();
            map.put("id", item.getId());
            map.put("pid", item.getPid());
            map.put("code", item.getCode());
            map.put("name", item.getName());
            solrAdmin.create("OrgHealthCategory", map);
        }
    }

}
