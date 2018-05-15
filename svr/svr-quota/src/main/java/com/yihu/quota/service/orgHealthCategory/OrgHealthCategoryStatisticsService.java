package com.yihu.quota.service.orgHealthCategory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.quota.vo.SaveModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private ElasticSearchClient esClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将卫生机构类别末节点统计结果，填充到卫生机构类别集合中，
     * 然后合计父节点的统计结果。
     *
     * @param endpointsStatisticList 卫生机构类别末节点统计结果
     * @return 所有卫生机构类别的统计结果
     */
    public List<SaveModel> getAllNodesStatistic(List<Map<String, Object>> endpointsStatisticList) {
        // 获取所有卫生机构类别的集合
        String sql = "SELECT id, pid, top_pid AS topPid, code, name, 0 AS result, 'false' AS isEndpoint FROM org_health_category ORDER BY code ";
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
                item.put("townName", endpoint.get("townName"));
                item.put("year", endpoint.get("year"));
                item.put("yearName", endpoint.get("yearName"));
                item.put("slaveKey1", endpoint.get("slaveKey1"));
                item.put("slaveKey1Name", endpoint.get("slaveKey1Name"));
                item.put("slaveKey2", endpoint.get("slaveKey2"));
                item.put("slaveKey2Name", endpoint.get("slaveKey2Name"));
                item.put("slaveKey3", endpoint.get("slaveKey3"));
                item.put("slaveKey3Name", endpoint.get("slaveKey3Name"));
                if(endpoint.get("economic") != null){
                    item.put("economic", endpoint.get("economic"));
                    item.put("economicName", endpoint.get("economicName"));
                }
                if(endpoint.get("level") != null){
                    item.put("level", endpoint.get("level"));
                    item.put("levelName", endpoint.get("levelName"));
                }
            }
        }

        // 合计父节点统计结果
        for (int j = 0, jLen = allOrgHealthCategoryList.size(); j < jLen; j++) {
            Map<String, Object> item = allOrgHealthCategoryList.get(j);
            int id = (int) item.get("id");
            List<Map<String, Object>> itemEndpointList = findEndpointsForOneParent(allOrgHealthCategoryList, id);
            if (itemEndpointList.size() != 0) {
                item.put("result", countResult(itemEndpointList));
            }
        }
        return translateModel(endpointsStatisticList, allOrgHealthCategoryList);
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
            if (itemPid == null && id == pid) { // 顶节点
                for (Map<String, Object> topSubItem : allOrgHealthCategoryList) {
                    Object topPid = topSubItem.get("topPid");
                    Object topSubItemPid = topSubItem.get("pid");
                    boolean isEndpointSubItem = Boolean.parseBoolean(topSubItem.get("isEndpoint").toString());
                    if (topSubItemPid != null && Integer.parseInt(topPid.toString()) == pid && isEndpointSubItem) {
                        endpointList.add(topSubItem);
                    }
                }
                return endpointList;
            } else if (itemPid != null && (int) itemPid == pid) {
                if (isEndpoint) {
                    endpointList.add(item);
                } else {
                    endpointList.addAll(findEndpointsForOneParent(allOrgHealthCategoryList, id));
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
     * 将卫生机构类别统计结果转换成 SaveModel
     *
     * @param endpointsStatisticList   卫生机构类别末节点统计结果
     * @param allOrgHealthCategoryList 所有卫生机构类集合
     * @return
     */
    private List<SaveModel> translateModel(List<Map<String, Object>> endpointsStatisticList,
                                           List<Map<String, Object>> allOrgHealthCategoryList) {
        List<SaveModel> resultList = new ArrayList<>();
        try {
            String quotaCode = null;
            String quotaName = null;
            String quotaDate = null;
            String town = null;
            String year = null;
            String slaveKey1 = null;
            String slaveKey2 = null;
            String slaveKey3 = null;
            String townName = null;
            String yearName = null;
            String slaveKey1Name = null;
            String slaveKey2Name = null;
            String slaveKey3Name = null;
            String economic = null;
            String economicName = null;
            String level = null;
            String levelName = null;
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

                townName = item.get("townName") == null ? null : item.get("townName").toString();
                yearName = item.get("yearName") == null ? null : item.get("yearName").toString();
                slaveKey1Name = item.get("slaveKey1Name") == null ? null : item.get("slaveKey1Name").toString();
                slaveKey2Name = item.get("slaveKey2Name") == null ? null : item.get("slaveKey2Name").toString();
                slaveKey3Name = item.get("slaveKey3Name") == null ? null : item.get("slaveKey3Name").toString();

                economic = item.get("economic") == null ? null : item.get("economic").toString();
                economicName = item.get("economicName") == null ? null : item.get("economicName").toString();
                level = item.get("level") == null ? null : item.get("level").toString();
                levelName = item.get("levelName") == null ? null : item.get("levelName").toString();

                model.setQuotaCode(quotaCode);
                model.setQuotaName(quotaName);
                model.setQuotaDate(quotaDate);
                model.setTown(town);
                model.setTownName(townName);
                model.setYear(year);
                model.setYearName(yearName);
                model.setSlaveKey1(slaveKey1);
                model.setSlaveKey2(slaveKey2);
                model.setSlaveKey3(slaveKey3);
                model.setSlaveKey1Name(slaveKey1Name);
                model.setSlaveKey2Name(slaveKey2Name);
                model.setSlaveKey3Name(slaveKey3Name);
                model.setEconomic(economic);
                model.setEconomicName(economicName);
                model.setLevel(level);
                model.setLevelName(levelName);
                model.setCreateTime(new Date());
                model.setOrgHealthCategoryId(item.get("id").toString());
                String pid = item.get("pid") == null ? null : item.get("pid").toString();
                model.setOrgHealthCategoryPid(pid);
                String code = item.get("code") == null ? null : item.get("code").toString();
                model.setOrgHealthCategoryCode(code);
                model.setOrgHealthCategoryName(item.get("name").toString());
                model.setResult(item.get("result").toString());

                resultList.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
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
