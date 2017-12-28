package com.yihu.quota.service.org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2017/12/29.
 */
@Service
public class OrgService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getLevel(String code) {
        String sql =  "SELECT level_id FROM organizations WHERE org_code = '" + code + "'";
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);
        if(dataList.isEmpty()) {
            return "";
        }else {
            return dataList.get(0).get("level_id").toString();
        }
    }
}
