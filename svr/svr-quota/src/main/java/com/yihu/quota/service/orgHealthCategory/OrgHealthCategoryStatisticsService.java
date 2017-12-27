package com.yihu.quota.service.orgHealthCategory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.client.EsClient;
import com.yihu.quota.vo.SaveModelOrgHealthCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卫生机构类别统计 Service
 *
 * @author 张进军
 * @date 2017/12/26 13:42
 */
@Service
public class OrgHealthCategoryStatisticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EsClient esClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将卫生机构类别末节点统计结果，填充到卫生机构类别集合中，
     * 然后合计父节点统计结果，再将卫生机构类别集合统计结果保存到ES库中。
     *
     * @param endpointsStatisticList 卫生机构类别末节点统计结果
     * @return 保存成功的状态
     */
    public boolean countResultsAndSaveToEs(List<Map<String, Object>> endpointsStatisticList) {
        // 获取所有卫生机构类别的集合
        String sql = "SELECT id, pid, code, name, 0 AS result, 'false' AS isEndpoint FROM org_health_category ORDER BY code ";
        List<Map<String, Object>> allOrgHealthCategoryList = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> endpoint : endpointsStatisticList) {
            for (int i = 0, len = allOrgHealthCategoryList.size(); i < len; i++) {
                Map<String, Object> item = allOrgHealthCategoryList.get(i);
                if (item.get("code").toString().equals(endpoint.get("code").toString())) {
                    // 填充末节点统计结果
                    item.put("result", endpoint.get("result"));
                    item.put("isEndpoint", "true");
                }
            }
        }

        // 合计父节点统计结果
        for (int j = 0, jLen = allOrgHealthCategoryList.size(); j < jLen; j++) {
            Map<String, Object> item = allOrgHealthCategoryList.get(j);
            int id = (int) item.get("id");
            List<Map<String, Object>> itemEndpointList = findEndpointsForOneParent(allOrgHealthCategoryList, id);
            item.put("result", countResult(itemEndpointList));
        }

        // 添加【合计】到卫生机构类别集合
        int totalResult = countResult(endpointsStatisticList);
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("id", "-1");
        totalMap.put("name", "合计");
        totalMap.put("result", totalResult);
        allOrgHealthCategoryList.add(totalMap);

        boolean finalFlag = true;
        try {
            String quotaCode = null;
            String quotaName = null;
            String quotaDate = null;
            String town = null;
            String year = null;
            String slaveKey1 = null;
            String slaveKey2 = null;
            String slaveKey3 = null;
            String slaveKey4 = null;
            if (endpointsStatisticList.size() > 0) {
                Map<String, Object> endpoint = endpointsStatisticList.get(0);
                quotaCode = endpoint.get("quotaCode").toString();
                quotaName = endpoint.get("quotaName").toString();
                quotaDate = endpoint.get("quotaDate").toString();
                town = endpoint.get("town") == null ? null : endpoint.get("town").toString();
                year = endpoint.get("year") == null ? null : endpoint.get("year").toString();
                slaveKey1 = endpoint.get("slaveKey1") == null ? null : endpoint.get("slaveKey1").toString();
                slaveKey2 = endpoint.get("slaveKey2") == null ? null : endpoint.get("slaveKey2").toString();
                slaveKey3 = endpoint.get("slaveKey3") == null ? null : endpoint.get("slaveKey3").toString();
                slaveKey4 = endpoint.get("slaveKey4") == null ? null : endpoint.get("slaveKey4").toString();
            }

            // 保存到ES库中
            SaveModelOrgHealthCategory model;
            for (Map<String, Object> item : allOrgHealthCategoryList) {
                model = new SaveModelOrgHealthCategory();
                model.setQuotaCode(quotaCode);
                model.setQuotaName(quotaName);
                model.setQuotaDate(quotaDate);
                model.setTown(town);
                model.setYear(year);
                model.setSlaveKey1(slaveKey1);
                model.setSlaveKey2(slaveKey2);
                model.setSlaveKey3(slaveKey3);
                model.setSlaveKey4(slaveKey4);
                model.setOrgHealthCategoryId(item.get("id").toString());
                String pid = item.get("pid") == null ? null : item.get("pid").toString();
                model.setOrgHealthCategoryPid(pid);
                String code = item.get("code") == null ? null : item.get("pid").toString();
                model.setOrgHealthCategoryCode(code);
                model.setOrgHealthCategoryName(item.get("name").toString());
                esClient.index("quota_index", "quota", objectMapper.writeValueAsString(model));
            }
        } catch (Exception e) {
            e.printStackTrace();
            finalFlag = false;
        }

        return finalFlag;
    }

    /**
     * 查找指定父节点的所有末节点
     *
     * @param allOrgHealthCategoryList 所有卫生机构类集合
     * @param pid                      指定的父节点ID
     * @return 指定父节点的所有末节点集合
     */
    private List<Map<String, Object>> findEndpointsForOneParent(List<Map<String, Object>> allOrgHealthCategoryList, int pid) {
        List<Map<String, Object>> endpointList = new ArrayList<>();
        for (Map<String, Object> item : allOrgHealthCategoryList) {
            int id = (int) item.get("id");
            Object itemPid = item.get("pid");
            boolean isEndpoint = (boolean) item.get("isEndpoint");
            if (itemPid != null && (int) itemPid == pid) {
                if (isEndpoint) {
                    endpointList.add(item);
                } else {
                    findEndpointsForOneParent(allOrgHealthCategoryList, id);
                }
            }
        }
        return endpointList;
    }

    /**
     * 合计指定卫生机构类别末节点集合中的统计结果
     *
     * @param endpointList 卫生机构类别末节点集合
     * @return 合计的统计值
     */
    private int countResult(List<Map<String, Object>> endpointList) {
        return endpointList.stream()
                .mapToInt(item -> item.get("result") != null ? Integer.parseInt(item.get("result").toString()) : 0)
                .sum();
    }

}
