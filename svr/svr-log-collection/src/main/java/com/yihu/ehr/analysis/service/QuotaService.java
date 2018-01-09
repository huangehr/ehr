package com.yihu.ehr.analysis.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihu.ehr.analysis.util.ElasticsearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chenweida on 2018/1/9.
 */
@Service
public class QuotaService {
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    public String apiNum() {
        String sql = "SELECT count(*) num,api FROM cloud_operator_log group by api ";
        JSONArray jsonObject=new JSONArray();
        jsonObject.addAll(elasticsearchUtil.excuteDataModel(sql));
        return jsonObject.toString();
    }

    public String apiSuccessFlagNum() {
        String sql = "SELECT count(*) num,api,responseCode FROM cloud_operator_log group by api,responseCode ";

        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> maps = elasticsearchUtil.excuteDataModel(sql);
        maps.forEach(one -> {
            String key = one.get("api").toString();
            JSONArray ja = null;
            if (jsonObject.containsKey(key)) {
                ja = jsonObject.getJSONArray(key);
            } else {
                ja = new JSONArray();
            }
            JSONObject jo = new JSONObject();
            jo.put("responseCode", one.get("responseCode"));
            jo.put("num", one.get("num"));
            ja.add(jo);
            jsonObject.put(key, ja);
        });
        return jsonObject.toString();
    }
}
