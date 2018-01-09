package com.yihu.quota.service.orgHealthCategory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.client.EsClient;
import com.yihu.quota.vo.SaveModel;
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
                item.put("town", endpoint.get("town"));
                item.put("year", endpoint.get("year"));
                item.put("slaveKey1", endpoint.get("slaveKey1"));
                item.put("slaveKey2", endpoint.get("slaveKey2"));
                item.put("slaveKey3", endpoint.get("slaveKey3"));
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
        return saveToEs(endpointsStatisticList, allOrgHealthCategoryList);
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
            boolean isEndpoint = Boolean.parseBoolean(item.get("isEndpoint").toString());
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

    /**
     * 将卫生机构类别统计结果保存到ES库中
     *
     * @param endpointsStatisticList   卫生机构类别末节点统计结果
     * @param allOrgHealthCategoryList 所有卫生机构类集合
     * @return
     */
    private boolean saveToEs(List<Map<String, Object>> endpointsStatisticList, List<Map<String, Object>> allOrgHealthCategoryList) {
        boolean result = false;
        try {
            String quotaCode = null;
            String quotaName = null;
            String quotaDate = null;
            String town = null;
            String year = null;
            String slaveKey1 = null;
            String slaveKey2 = null;
            String slaveKey3 = null;
            if (endpointsStatisticList.size() > 0) {
                Map<String, Object> endpoint = endpointsStatisticList.get(0);
                quotaCode = endpoint.get("quotaCode").toString().replaceAll("_", "");
                quotaName = endpoint.get("quotaName").toString();
                quotaDate = endpoint.get("quotaDate").toString();
            }

            SaveModel model;
            for (Map<String, Object> item : allOrgHealthCategoryList) {
                model = new SaveModel();
                town = item.get("town") == null ? null : item.get("town").toString();
                year = item.get("year") == null ? null : item.get("year").toString();
                slaveKey1 = item.get("slaveKey1") == null ? null : item.get("slaveKey1").toString();
                slaveKey2 = item.get("slaveKey2") == null ? null : item.get("slaveKey2").toString();
                slaveKey3 = item.get("slaveKey3") == null ? null : item.get("slaveKey3").toString();
                model.setQuotaCode(quotaCode);
                model.setQuotaName(quotaName);
                model.setQuotaDate(quotaDate);
                model.setTown(town);
                model.setTownName(town);
                model.setYear(year);
                model.setYearName(year);
                model.setSlaveKey1(slaveKey1);
                model.setSlaveKey2(slaveKey2);
                model.setSlaveKey3(slaveKey3);
                model.setOrgHealthCategoryId(item.get("id").toString());
                String pid = item.get("pid") == null ? null : item.get("pid").toString();
                model.setOrgHealthCategoryPid(pid);
                String code = item.get("code") == null ? null : item.get("code").toString();
                model.setOrgHealthCategoryCode(code);
                model.setOrgHealthCategoryName(item.get("name").toString());
                model.setResult(item.get("result").toString());
                esClient.index("quota_index", "quota", objectMapper.writeValueAsString(model));
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据父级ID，递归获取卫生机构类别的父级及其子级集合，形成树形结构。
     *
     * @param pid 父级ID，为 -1 时获取整棵树。
     * @return 父级及其子集的树形结构数据
     */
    public List<Map<String, Object>> getOrgHealthCategoryTreeByPid(int pid) {
        List<Map<String, Object>> treeList = new ArrayList<>();

        List<Map<String, Object>> childList = new ArrayList<>();
        if (pid == -1) {
            childList = getOrgHealthCategoryTopNodes();
        } else {
            childList = getOrgHealthCategoryChildrenByPid(pid);
        }
        for (Map<String, Object> child : childList) {
            child.put("children", getOrgHealthCategoryTreeByPid((int) child.get("id")));
            treeList.add(child);
        }

        return treeList;
    }

    /**
     * 根据ID，获取卫生机构类别信息
     */
    private Map<String, Object> getOrgHealthCategoryById(int id) {
        String sql = "SELECT o.id, o.pid, o.code, o.name AS text, NULL AS children FROM org_health_category o WHERE o.id = " + id;
        return (Map<String, Object>) jdbcTemplate.queryForMap(sql);
    }

    /**
     * 获取卫生机构类别顶级节点
     */
    private List<Map<String, Object>> getOrgHealthCategoryTopNodes() {
        String sql = "SELECT o.id, o.pid, o.code, o.name AS text, NULL AS children FROM org_health_category o WHERE o.pid IS NULL";
        return (List<Map<String, Object>>) jdbcTemplate.queryForList(sql);
    }

    /**
     * 根据卫生机构类别父级点ID，获取其子节点集合
     */
    private List<Map<String, Object>> getOrgHealthCategoryChildrenByPid(int pid) {
        String sql = "SELECT o.id, o.pid, o.code, o.name AS text, NULL AS children FROM org_health_category o WHERE o.pid = " + pid;
        return (List<Map<String, Object>>) jdbcTemplate.queryForList(sql);
    }

}
