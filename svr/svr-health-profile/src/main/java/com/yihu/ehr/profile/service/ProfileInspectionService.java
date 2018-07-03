package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.util.SimpleSolrQueryUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/3/13.
 */
@Service
public class ProfileInspectionService extends ProfileBasicService {

    public List inspectionRecords(String demographicId, String filter, String date, String searchParam) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("HDSD00_79", "inspect");
        typeMap.put("HDSD00_77", "examine");
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("HDSD00_79", "EHR_002883"); //检查名称
        nameMap.put("HDSD00_77", "EHR_000352"); //检验项目
        Map<String, String> numMap = new HashMap<>();
        numMap.put("HDSD00_79", "EHR_000316"); //检查报告单号
        numMap.put("HDSD00_77", "EHR_000363"); //检验报告单号
        for (String dataset : typeMap.keySet()) {
            String query = "{\"q\":\"demographic_id:" + demographicId + "\"}";
            query = SimpleSolrQueryUtil.getQuery(filter, date, query);
            Envelop masterEnvelop = resource.getMasterData(query, 1, 1000, null);
            if (masterEnvelop.isSuccessFlg()) {
                List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
                //循环获取结果集
                for (Map<String, Object> temp : masterList) {
                    String subQ = "{\"q\":\"rowkey:" + temp.get("rowkey") + "$" + dataset + "$*\"}";
                    Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                    List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                    subList.forEach(item -> {
                        Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                        if (resultMap != null) {
                            String healthProblemName = (String) resultMap.get("healthProblemName");
                            String itemName = (String) item.get(nameMap.get(dataset));
                            if (itemName != null) {
                                resultMap.put("projectName", itemName);
                            } else {
                                if (dataset.equals("HDSD00_79")) {
                                    resultMap.put("projectName", "检查报告-" + healthProblemName);
                                } else {
                                    resultMap.put("projectName", "检验报告-" + healthProblemName);
                                }
                            }
                            resultMap.put("type", typeMap.get(dataset));
                            resultMap.put("mark", item.get(numMap.get(dataset)));
                            resultList.add(resultMap);
                        }
                    });
                }
            }
        }
        return resultList;
    }

}
