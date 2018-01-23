package com.yihu.ehr.query.services;

import com.yihu.ehr.query.common.enums.Logical;
import com.yihu.ehr.query.common.enums.Operation;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.solr.SolrUtil;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Solr统计查询服务
 * add by hzp at 2016-04-26
 */
@Service
public class SolrQuery {

    private String startTime;
    private String endTime;

    @Autowired
    SolrUtil solrUtil;

    public void initParams(String startTime, String endTime) {
        // 初始执行指标，起止日期没有值，默认分组聚合前50年的。
        this.startTime = startTime == null ? "NOW/YEAR-50YEAR" : startTime;
        this.endTime = endTime == null ? "NOW" : endTime;
    }

    /**
     * solr语法转换
     */
    public String conditionToString(QueryCondition condition) {
        String s = "";
        String operation = condition.getOperation();
        String field = condition.getField();
        Object keyword = condition.getKeyword();
        Object[] keywords = condition.getKeywords();
        switch (operation) {
            case Operation.LIKE:
                s = field + ":*\"" + keyword + "\"*";
                break;
            case Operation.LEFTLIKE:
                s = field + ":*\"" + keyword + "\"";
                break;
            case Operation.RIGHTLIKE:
                s = field + ":\"" + keyword + "\"*";
                break;
            case Operation.RANGE: {
                if (keywords.length >= 2) {
                    s = field + ":[\"" + keywords[0] + "\" TO \"" + keywords[1] + "\"]";
                }
                break;
            }
            case Operation.NOTRANGE: {
                if (keywords.length >= 2) {
                    s = "NOT " + field + ":[\"" + keywords[0] + "\" TO \"" + keywords[1] + "\"]";
                }
                break;
            }
            case Operation.NE: {
                s = "NOT(" + field + ":\"" + keyword + "\")";
                break;
            }
            case Operation.IN: {
                String in = "";
                if (keywords != null && keywords.length > 0) {
                    for (Object key : keywords) {
                        if (in != null && in.length() > 0) {
                            in += " OR " + field + ":\"" + key + "\"";
                        } else {
                            in = field + ":\"" + key + "\"";
                        }
                    }
                } else if (keyword != null) {
                    in = field + ":\"" + keyword + "\"";
                }
                s = "( " + in + " )";
                break;
            }
            case Operation.NIN: {
                String in = "";
                if (keywords != null && keywords.length > 0) {
                    for (Object key : keywords) {
                        if (in != null && in.length() > 0) {
                            in += " OR " + field + ":\"" + key + "\"";
                        } else {
                            in = field + ":\"" + key + "\"";
                        }
                    }
                } else if (keyword != null) {
                    in = field + ":\"" + keyword + "\"";
                }
                s = "NOT (" + in + ")";
                break;
            }
            case Operation.GT: {
                s = field + ":{\"" + keyword + "\" TO *}";
                break;
            }
            case Operation.GTE: {
                s = field + ":[\"" + keyword + "\" TO * ]";
                break;
            }
            case Operation.LT: {
                s = field + ":" + "{* TO \"" + keyword + "\" }";
                break;
            }
            case Operation.LTE: {
                s = field + ":" + "[* TO \"" + keyword + "\" ]";
                break;
            }
            case Operation.EQ: {
                s = field + ":\"" + keyword + "\"";
                break;
            }
            default:
                s = "unknown operation";
        }

        return s;
    }

    /**
     * solr语法转换
     */
    public String conditionToString(List<QueryCondition> conditions) {
        String re = "";
        String NOT = "";
        if (conditions != null && conditions.size() > 0) {
            for (QueryCondition condition : conditions) {
                if (!re.equals("") || !NOT.equals("")) {
                    switch (condition.getLogical()) {
                        case Logical.AND:
                            re += " AND ";
                            break;
                        case Logical.OR:
                            re += " OR  ";
                            break;
                        case Logical.NOT:
                            re += " NOT ";
                            break;
                    }
                    if (conditionToString(condition).indexOf("NOT") == 0) {
                        if (NOT.equals("")) {
                            NOT = " NOT(" + conditionToString(condition).substring(3);
                            re = re.substring(0, re.length() - 5);
                        } else {
                            NOT += re.substring(re.length() - 5) + conditionToString(condition).substring(3, conditionToString(condition).length());
                            re = re.substring(0, re.length() - 5);
                        }
                    } else {
                        re += conditionToString(condition);
                        re = "(" + re + ")";
                    }
                } else {
                    re += conditionToString(condition);
                    if (conditionToString(condition).indexOf("NOT") == 0) {
                        NOT = "NOT(" + re.substring(3);
                        re = "";

                    }
                }
            }
        } else {
            re = "*:* ";
        }
        if (NOT.equals(""))
            return re;
        else
            return re + NOT + ")";
    }

    /******************************** Count方法 ******************************************************/
    /**
     * 获取总条数
     *
     * @param queryString
     * @return
     */
    public long count(String table, String queryString) throws Exception {
        return solrUtil.count(table, queryString);
    }

    /**
     * 单分组统计Count
     *
     * @return
     */
    public Page<Map<String, Object>> getGroupCount(String table, String groupField) throws Exception {
        return getGroupCount(table, groupField, "", "", 1, 1000);
    }

    /**
     * 单分组统计Count(分页)
     */
    public Page<Map<String, Object>> getGroupCount(String table,
                                                   String groupField,
                                                   String q,
                                                   String fq,
                                                   int page,
                                                   int rows) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();

        if (rows < 0) rows = 10;
        if (rows > 100) rows = 100;
        if (page < 0) page = 1;
        int start = (page - 1) * rows;

        /***** Solr查询 ********/
        Map<String, Long> list = solrUtil.groupCount(table, q, fq, groupField, start, rows);

        if (list != null && list.size() > 0) {
            for (Map.Entry<String, Long> item : list.entrySet()) {
                Map<String, Object> obj = new HashMap<>();
                obj.put(groupField, item.getKey());
                obj.put("$count", item.getValue());
                data.add(obj);
            }
        }

        return new PageImpl<Map<String, Object>>(data, new PageRequest(page - 1, rows), data.size());
    }

    /**
     * 递归转换统计数据
     */
    private List<Map<String, Object>> pivotToMapList(List<PivotField> pivotList,
                                                     List<Map<String, Object>> data,
                                                     Map<String, Object> pre) {
        if (data == null) {
            data = new ArrayList<>();
        }

        if (pivotList != null) {
            for (PivotField pivot : pivotList) {
                String field = pivot.getField();
                String value = pivot.getValue().toString();
                Map<String, Object> newRow = new HashMap<>();
                if (pre != null) {
                    newRow.putAll(pre); //深度复制
                }

                newRow.put(field, value);

                //递归获取子数据
                if (pivot.getPivot() != null && pivot.getPivot().size() > 0) {
                    data = pivotToMapList(pivot.getPivot(), data, newRow);
                } else {
                    int count = pivot.getCount();
                    newRow.put("$count", count);
                    data.add(newRow);
                }
            }
        }

        return data;
    }

    /**
     * 多级分组统计Count(不包含自定义分组)
     */
    public Page<Map<String, Object>> getGroupMult(String table,
                                                  String groupFields,
                                                  String q,
                                                  String fq,
                                                  int page,
                                                  int rows) throws Exception {
        List<PivotField> listPivot = solrUtil.groupCountMult(table, q, fq, groupFields, page, rows);
        return new PageImpl<Map<String, Object>>(pivotToMapList(listPivot, null, null), new PageRequest(page - 1, rows), listPivot.size());
    }

    /**
     * 纯自定义分组递归
     */
    private List<Map<String, Object>> recGroupCount(String table,
                                                    List<SolrGroupEntity> grouplist,
                                                    int num,
                                                    List<Map<String, Object>> data,
                                                    Map<String, Object> pre,
                                                    String q,
                                                    String fq) throws Exception {
        if (data == null) {
            data = new ArrayList<>();
        }
        String groupField = grouplist.get(num).getGroupField();
        Map<String, String> list = grouplist.get(num).getGroupCondition();

        for (String key : list.keySet()) {
            String condition = list.get(key);
            String query = fq;
            if (query != null && query.length() > 0) {
                query += " AND " + condition;
            } else {
                query = condition;
            }

            Map<String, Object> newRow = new HashMap<>();
            if (pre != null) {
                newRow.putAll(pre); //深度复制
            }
            newRow.put(groupField, key);

            //最后一级查询
            if (num == grouplist.size() - 1) {
                long count = solrUtil.count(table, query);
                newRow.put("$count", count);
                data.add(newRow);
            } else {
                data = recGroupCount(table, grouplist, num + 1, data, newRow, q, query);
            }
        }

        return data;
    }

    /**
     * 多级分组统计Count（包含自定义分组）
     */
    public Page<Map<String, Object>> getGroupMult(String table,
                                                  String groupFields,
                                                  List<SolrGroupEntity> customGroup,
                                                  String q,
                                                  String fq) throws Exception {

        List<Map<String, Object>> data = null;
        if (groupFields != null && groupFields.length() > 0) {
            String[] groups = groupFields.split(",");
            List<SolrGroupEntity> grouplist = new ArrayList<SolrGroupEntity>();
            if (customGroup != null && customGroup.size() > 0) {
                grouplist = customGroup;
            }

            //遍历字段分组
            List<FacetField> facets = solrUtil.groupCount(table, q, fq, groups);
            for (FacetField facet : facets) {
                String groupName = facet.getName();
                SolrGroupEntity group = new SolrGroupEntity(groupName);

                List<FacetField.Count> counts = facet.getValues();
                for (FacetField.Count count : counts) {
                    String value = count.getName();
                    group.putGroupCondition(value, groupName + ":" + value);
                }

                grouplist.add(group);
            }

            data = recGroupCount(table, grouplist, 0, null, q, fq);
        }
        //纯自定义分组
        else {
            if (customGroup != null && customGroup.size() > 0) {
                data = recGroupCount(table, customGroup, 0, null, null, null);
            }
        }
        return new PageImpl<Map<String, Object>>(data);
    }

    /**
     * 多级分组统计Count（包含自定义分组）
     */
    public List<Map<String, Object>> getGroupMultList(String core,
                                                      List<SolrGroupEntity> dimensionGroupList,
                                                      List<SolrGroupEntity> customGroups,
                                                      String q,
                                                      String fq) throws Exception {
        // 维度字段及最后一个维度基于其他维度组合作为条件的统计结果的集合
        List<Map<String, Object>> resultCounts = new ArrayList<>();

        if (dimensionGroupList.size() > 0) {
            // 收集将前 N-1 维度的统计，拼接成筛选条件。
            // 注意：其中指标维度code出现的顺序要与 dimensionGroupList 中的一致。
            List<SolrGroupEntity> groupList = new ArrayList<>();
            if (customGroups != null && customGroups.size() > 0) {
                groupList = customGroups;
            }

            for (SolrGroupEntity dimensionGroup : dimensionGroupList) {
                if (dimensionGroup.getType().equals(SolrGroupEntity.GroupType.DATE_RANGE)) {
                    // 按每天范围统计
                    List<RangeFacet> rangeFacets = solrUtil.getFacetDateRange(core, dimensionGroup.getGroupField(), startTime, endTime, "+1DAY", fq, q);
                    for (RangeFacet rangeFacet : rangeFacets) {
                        String groupName = rangeFacet.getName();
                        List<RangeFacet.Count> countList = rangeFacet.getCounts();
                        SolrGroupEntity group = new SolrGroupEntity(groupName);
                        group.setType(SolrGroupEntity.GroupType.DATE_RANGE);
                        for (RangeFacet.Count count : countList) {
                            if (count.getCount() > 0) {
                                String day = count.getValue().substring(0, 10);
                                group.putGroupCondition(day, String.format("%s:[%sT00:00:00Z TO %sT23:59:59Z]", groupName, day, day));
                            }
                        }
                        groupList.add(group);
                    }
                } else {
                    // 按分组字段值统计
                    String[] groupField = {dimensionGroup.getGroupField()};
                    List<FacetField> facets = solrUtil.groupCount(core, q, fq, groupField);
                    for (FacetField facet : facets) {
                        String groupName = facet.getName();
                        List<FacetField.Count> counts = facet.getValues();
                        SolrGroupEntity group = new SolrGroupEntity(groupName);
                        for (FacetField.Count count : counts) {
                            if (count.getCount() > 0) {
                                String value = count.getName();
                                group.putGroupCondition(value, groupName + ":" + value);
                            }
                        }
                        groupList.add(group);
                    }
                }
            }

            resultCounts = recGroupCount(core, groupList, 0, null, q, fq);
        }
        //纯自定义分组
        else {
            if (customGroups != null && customGroups.size() > 0) {
                resultCounts = recGroupCount(core, customGroups, 0, null, null, null);
            }
        }

        return resultCounts;
    }

    /**
     * 递归获取分组数据(混合)
     */
    private List<Map<String, Object>> recGroupCount(String core,
                                                    List<SolrGroupEntity> grouplist,
                                                    int num,
                                                    List<Map<String, Object>> preList,
                                                    String q,
                                                    String fq) throws Exception {
        String conditionName = "$condition"; // 拼接最后一个维度分组聚合统计的过滤条件
        String countKeyName = "$countKey"; // 拼接最后一个维度分组聚合统计值对应的唯一健
        if (num == grouplist.size() - 1) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // 维度字段、维度组合Key及统计结果
            SolrGroupEntity group = grouplist.get(num);  // 最后一个维度
            String groupField = group.getGroupField();
            SolrGroupEntity.GroupType groupType = group.getType();

            for (Map<String, Object> preObj : preList) {
                // 遍历前 N-1 维度组合作为筛选条件
                String query = preObj.get(conditionName).toString();
                if (fq != null && !fq.equals("") && !fq.equals("*:*")) {
                    query += " AND " + fq;
                }

                // 统计最后一个维度
                Map<String, Long> countMap = new HashMap<>();
                if (groupType.equals(SolrGroupEntity.GroupType.DATE_RANGE)) {
                    // 按每天范围统计
                    List<RangeFacet> rangeFacets = solrUtil.getFacetDateRange(core, groupField, startTime, endTime, "+1DAY", query, q);
                    for (RangeFacet rangeFacet : rangeFacets) {
                        List<RangeFacet.Count> countList = rangeFacet.getCounts();
                        for (RangeFacet.Count count : countList) {
                            if (count.getCount() > 0) {
                                String key = count.getValue().substring(0, 10);
                                countMap.put(key, (long) count.getCount());
                            }
                        }
                    }
                } else {
                    // 按字段值统计
                    countMap = solrUtil.groupCount(core, q, query, groupField, 0, 1000);
                }

                if (countMap.size() > 0) {
                    for (String key : countMap.keySet()) {
                        Map<String, Object> obj = new LinkedHashMap<>();
                        obj.putAll(preObj); //深拷贝
                        obj.put(groupField, key);
                        String countKey = preObj.get(countKeyName).toString() + "-" + key;
                        obj.put(countKeyName, countKey);
                        obj.put("$count", countMap.get(key));
                        obj.remove(conditionName);
                        list.add(obj);
                    }
                }
            }

            return list;
        } else {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//返回集合
            SolrGroupEntity group = grouplist.get(num); //当前分组
            Map<String, String> groupMap = group.getGroupCondition(); //当前分组项
            String groupName = group.getGroupField();
            if (preList != null) {
                //遍历上级递归数据
                for (Map<String, Object> preObj : preList) {
                    //遍历当前分组数据
                    for (Map.Entry<String, String> item : groupMap.entrySet()) {
                        Map<String, Object> obj = new LinkedHashMap<>();
                        obj.putAll(preObj); //深拷贝
                        obj.put(groupName, item.getKey());
                        String condition = preObj.get(conditionName).toString() + " AND " + item.getValue();
                        obj.put(conditionName, condition);
                        String key = preObj.get(countKeyName).toString() + "-" + item.getKey();
                        obj.put(countKeyName, key);
                        list.add(obj);
                    }
                }
            } else { //第一次遍历
                for (Map.Entry<String, String> item : groupMap.entrySet()) {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put(groupName, item.getKey());
                    obj.put(conditionName, item.getValue());
                    obj.put(countKeyName, item.getKey());
                    list.add(obj);
                }
            }
            return recGroupCount(core, grouplist, num + 1, list, q, fq);
        }

    }

    /************************* 数值统计 **********************************************/

    /**
     * 多级数值统计
     */
    public Page<Map<String, Object>> getStats(String table,
                                              String groupFields,
                                              String statsFields) throws Exception {
        return getStats(table, groupFields, statsFields, "", "", null);
    }

    /**
     * 多级数值统计
     */
    public Page<Map<String, Object>> getStats(String table,
                                              String groupFields,
                                              String statsFields,
                                              String q,
                                              String fq) throws Exception {
        return getStats(table, groupFields, statsFields, q, fq, null);
    }

    /**
     * 多级数值统计
     *
     * @param core        core名
     * @param groupFields 统计字段
     * @param statsFields 分组字段
     * @param q           查询条件
     * @param fq          筛选条件
     * @param customGroup 额外自定义分组
     */
    public Page<Map<String, Object>> getStats(String core,
                                              String groupFields,
                                              String statsFields,
                                              String q,
                                              String fq,
                                              List<SolrGroupEntity> customGroup) throws Exception {
        String[] groups = groupFields.split(",");
        String[] stats = statsFields.split(",");

        List<Map<String, Object>> data = null;
        if (groups != null && groups.length > 0) {
            List<SolrGroupEntity> grouplist = new ArrayList<>();
            if (customGroup != null && customGroup.size() > 0) {
                grouplist = customGroup;
            }

            //遍历字段分组
            List<FacetField> facets = solrUtil.groupCount(core, q, fq, groups);
            for (FacetField facet : facets) {
                String groupName = facet.getName();
                SolrGroupEntity group = new SolrGroupEntity(groupName);

                List<FacetField.Count> counts = facet.getValues();
                for (FacetField.Count count : counts) {
                    String value = count.getName();
                    group.putGroupCondition(value, groupName + ":" + value);
                }

                grouplist.add(group);
            }

            data = recStats(core, stats, grouplist, q, fq, 0, null);
        } else {
            //全部自定义条件
            System.out.print("All custom condition!");
        }

        return new PageImpl<Map<String, Object>>(data);
    }

    /**
     * 递归数值统计
     */
    private List<Map<String, Object>> recStats(String core,
                                               String[] statsFields,
                                               List<SolrGroupEntity> grouplist,
                                               String q,
                                               String fq,
                                               int num,
                                               List<Map<String, Object>> preList) throws Exception {
        String conditionName = "$condition";
        if (num == grouplist.size() - 1) {
            List<Map<String, Object>> list = new ArrayList<>();//返回集合
            SolrGroupEntity group = grouplist.get(num);
            Map<String, String> groupMap = group.getGroupCondition(); //当前分组项
            String groupName = group.getGroupField();
            DecimalFormat df = new DecimalFormat("#.00");

            if (preList != null && preList.size() > 0) {
                for (Map<String, Object> preObj : preList) {
                    String query = preObj.get(conditionName).toString();

                    if ((fq != null && !fq.equals("")) && (query != null && !query.equals(""))) {
                        query = fq + " AND " + query;
                    } else {
                        query = fq + query;
                    }

                    //根据条件最后一级数值统计
                    Map<String, List<FieldStatsInfo>> statsMap = new HashMap<>();
                    //所有统计字段
                    for (String field : statsFields) {
                        List<FieldStatsInfo> statsList = solrUtil.getStats(core, q, query, field, groupName);
                        statsMap.put(field, statsList);
                    }

                    if (statsMap != null && statsMap.size() > 0) {
                        List<FieldStatsInfo> statsFirst = statsMap.get(statsFields[0]);
                        if (statsFirst != null) {
                            for (int i = 0; i < statsFirst.size(); i++) {
                                String groupItem = statsFirst.get(i).getName();
                                Map<String, Object> obj = new HashMap<>();
                                obj.putAll(preObj); //深拷贝
                                obj.put(groupName, groupItem == null ? "" : groupItem);
                                obj.remove(conditionName);

                                for (String field : statsFields) {
                                    List<FieldStatsInfo> statsList = statsMap.get(field);
                                    FieldStatsInfo item = statsList.get(i);
                                    obj.put("$count_" + field, item.getCount());
                                    obj.put("$sum_" + field, df.format(item.getSum()));
                                    obj.put("$avg_" + field, df.format(item.getMean()));
                                    obj.put("$max_" + field, df.format(item.getMax()));
                                    obj.put("$min_" + field, df.format(item.getMin()));
                                }

                                list.add(obj);
                            }
                        }
                    }
                }
            } else {
                //最后一级数值统计
                Map<String, List<FieldStatsInfo>> statsMap = new HashMap<>();
                //所有统计字段
                for (String field : statsFields) {
                    List<FieldStatsInfo> statsList = solrUtil.getStats(core, q, fq, field, groupName);
                    statsMap.put(field, statsList);
                }

                if (statsMap != null && statsMap.size() > 0) {
                    List<FieldStatsInfo> statsFirst = statsMap.get(statsFields[0]);
                    for (int i = 0; i < statsFirst.size(); i++) {
                        String groupItem = statsFirst.get(i).getName();
                        Map<String, Object> obj = new HashMap<>();
                        obj.put(groupName, groupItem == null ? "" : groupItem);
                        obj.remove(conditionName);

                        for (String field : statsFields) {
                            List<FieldStatsInfo> statsList = statsMap.get(field);
                            FieldStatsInfo item = statsList.get(i);
                            obj.put("$count_" + field, item.getCount());
                            obj.put("$sum_" + field, df.format(item.getSum()));
                            obj.put("$avg_" + field, df.format(item.getMean()));
                            obj.put("$max_" + field, df.format(item.getMax()));
                            obj.put("$min_" + field, df.format(item.getMin()));
                        }

                        list.add(obj);
                    }
                }
            }

            return list;
        } else {
            List<Map<String, Object>> list = new ArrayList<>();//返回集合
            SolrGroupEntity group = grouplist.get(num); //当前分组
            Map<String, String> groupMap = group.getGroupCondition(); //当前分组项
            String groupName = group.getGroupField();

            if (preList != null) {
                //遍历上级递归数据
                for (Map<String, Object> preObj : preList) {
                    //遍历当前分组数据
                    for (Map.Entry<String, String> item : groupMap.entrySet()) {
                        Map<String, Object> obj = new HashMap<>();
                        obj.putAll(preObj); //深拷贝
                        obj.put(groupName, item.getKey());
                        String condition = obj.get(conditionName).toString() + " AND " + item.getValue();
                        obj.put(conditionName, condition);
                        list.add(obj);
                    }
                }
            } else { //第一次遍历
                for (Map.Entry<String, String> item : groupMap.entrySet()) {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put(groupName, item.getKey());
                    obj.put(conditionName, item.getValue());
                    list.add(obj);
                }
            }

            return recStats(core, statsFields, grouplist, q, fq, num + 1, list);
        }
    }

}
