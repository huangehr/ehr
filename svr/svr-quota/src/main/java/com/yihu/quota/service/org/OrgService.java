package com.yihu.quota.service.org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    /**
     * 获取字典项列表.
     *
     * @param dictId
     * @return Map<String,Object>
     */
    public Map<String, Object> getDictEntries(int dictId) {
        String sql =  "SELECT code,value FROM system_dict_entries WHERE dict_id ="  + dictId ;
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);
        Map<String, Object> map = new HashMap<>();
        if(null != dataList) {
          for(int i=0;i<dataList.size();i++){
              if(null != dataList.get(i).get("code") && null != dataList.get(i).get("value")){
                  map.put(dataList.get(i).get("code").toString(),dataList.get(i).get("value").toString());
              }
          }
        }
        return map;
    }
}
