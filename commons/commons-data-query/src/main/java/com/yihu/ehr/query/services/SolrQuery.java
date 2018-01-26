package com.yihu.ehr.query.services;

import com.yihu.ehr.query.common.enums.Logical;
import com.yihu.ehr.query.common.enums.Operation;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.solr.SolrUtil;
import org.apache.commons.lang3.StringUtils;
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

    // 基础指标抽取时用
    private String startTime; // 查找的开始日期
    private String endTime; // 查找的截止日期

    @Autowired
    SolrUtil solrUtil;

    public void initParams(String startTime, String endTime) {
        // 初始执行指标，起止日期没有值，默认分组聚合前50年的。
        this.startTime = startTime == null ? "NOW/YEAR-50YEAR" : startTime;
        this.endTime = endTime == null ? "NOW" : endTime;
    }

    //region solr语法转换

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

    //endregion solr语法转换

    //region Count 统计

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
            List<SolrGroupEntity> grouplist = new ArrayList<>();
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
        } else { // 纯自定义分组
            if (customGroup != null && customGroup.size() > 0) {
                data = recGroupCount(table, customGroup, 0, null, null, null);
            }
        }
        return new PageImpl<Map<String, Object>>(data);
    }

    /**
     * 多级分组 Count 统计（包含自定义分组）
     * <p>
     * TODO
     * 涉及时间维度聚合统计，目前是按天间隔统计的写法，需要扩展按年、月等间隔统计时，需要定制分支。
     * 具体需要扩展的地方查看 joinAggregationCondition()、finalCount() 方法中备注。
     * -- 张进军 2018.1.26
     *
     * @param core               core名
     * @param q                  查询条件
     * @param fq                 筛选条件
     * @param dimensionGroupList 分组字段
     * @param customGroups       额外自定义分组
     */
    public List<Map<String, Object>> getCountMultList(String core,
                                                      String q,
                                                      String fq,
                                                      List<SolrGroupEntity> dimensionGroupList,
                                                      List<SolrGroupEntity> customGroups) throws Exception {
        // 维度字段及最后一个维度基于其他维度组合作为条件的统计结果的集合
        List<Map<String, Object>> resultCounts = new ArrayList<>();

        if (dimensionGroupList.size() > 0) {
            List<SolrGroupEntity> groupList = new ArrayList<>();
            if (customGroups != null && customGroups.size() > 0) {
                groupList = customGroups;
            }

            // 收集根据指标维度分组聚合的条件
            groupList.addAll(joinAggregationCondition(core, q, fq, dimensionGroupList));

            resultCounts = recGroupCount(core, groupList, 0, null, q, fq);
        } else { // 纯自定义分组
            if (customGroups != null && customGroups.size() > 0) {
                resultCounts = recGroupCount(core, customGroups, 0, null, null, null);
            }
        }

        return resultCounts;
    }

    /**
     * 递归 Count 统计(混合)
     */
    private List<Map<String, Object>> recGroupCount(String core,
                                                    List<SolrGroupEntity> groupList,
                                                    int num,
                                                    List<Map<String, Object>> preList,
                                                    String q,
                                                    String fq) throws Exception {
        // 维度字段、维度组合Key及统计结果
        List<Map<String, Object>> resultList = new ArrayList<>();

        String conditionName = "$condition"; // 拼接最后一个维度分组聚合统计的过滤条件
        String statisticsKeyName = "$statisticsKey"; // 拼接最后一个维度分组聚合统计值对应的唯一健
        if (num == groupList.size() - 1) {
            SolrGroupEntity groupEntity = groupList.get(num);  // 最后一个维度
            String groupField = groupEntity.getGroupField();
            Map<String, String> groupConditionMap = groupEntity.getGroupCondition();

            if (preList != null && preList.size() > 0) {
                // 遍历前 N-1 维度组合作为筛选条件
                for (Map<String, Object> preObj : preList) {
                    String currFq = preObj.get(conditionName).toString();
                    if (StringUtils.isNotEmpty(fq) && !fq.equals("*:*")) {
                        currFq += " AND " + fq;
                    }

                    // 对最后一个维度统计
                    Map<String, Long> countMap = finalCount(core, q, currFq, groupEntity);

                    if (countMap.size() > 0) {
                        for (String key : countMap.keySet()) {
                            Map<String, Object> obj = new LinkedHashMap<>();
                            obj.putAll(preObj); // 深拷贝
                            obj.put(groupField, key);
                            String statisticsKey = preObj.get(statisticsKeyName).toString() + "-" + key;
                            obj.put(statisticsKeyName, statisticsKey);
                            obj.put("$result", countMap.get(key)); // 统计值
                            obj.remove(conditionName);
                            resultList.add(obj);
                        }
                    }
                }
            } else {  // 只有一个维度分组统计场合
                for (Map.Entry<String, String> item : groupConditionMap.entrySet()) {
                    String currFq = item.getValue();
                    if (StringUtils.isNotEmpty(fq) && !fq.equals("*:*")) {
                        currFq += " AND " + fq;
                    }

                    Map<String, Long> countMap = finalCount(core, q, currFq, groupEntity);

                    if (countMap.size() > 0) {
                        for (String key : countMap.keySet()) {
                            Map<String, Object> obj = new LinkedHashMap<>();
                            obj.put(groupField, key);
                            obj.put(statisticsKeyName, key);
                            obj.put("$result", countMap.get(key)); // 统计值
                            resultList.add(obj);
                        }
                    }
                }
            }

            return resultList;
        } else {
            List<Map<String, Object>> list = new ArrayList<>();//返回集合
            SolrGroupEntity group = groupList.get(num); //当前分组
            Map<String, String> groupMap = group.getGroupCondition(); //当前分组项
            String groupField = group.getGroupField();
            if (preList != null) {
                //遍历上级递归数据
                for (Map<String, Object> preObj : preList) {
                    //遍历当前分组数据
                    for (Map.Entry<String, String> item : groupMap.entrySet()) {
                        Map<String, Object> obj = new LinkedHashMap<>();
                        obj.putAll(preObj); // 深拷贝
                        obj.put(groupField, item.getKey());
                        String condition = preObj.get(conditionName).toString() + " AND " + item.getValue();
                        obj.put(conditionName, condition);
                        String statisticsKey = preObj.get(statisticsKeyName).toString() + "-" + item.getKey();
                        obj.put(statisticsKeyName, statisticsKey);
                        list.add(obj);
                    }
                }
            } else { //第一次遍历
                for (Map.Entry<String, String> item : groupMap.entrySet()) {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put(groupField, item.getKey());
                    obj.put(conditionName, item.getValue());
                    obj.put(statisticsKeyName, item.getKey());
                    list.add(obj);
                }
            }
            return recGroupCount(core, groupList, num + 1, list, q, fq);
        }

    }

    /**
     * 对最后一个维度进行 count 统计
     *
     * @param core        core名
     * @param q           查询条件
     * @param fq          筛选条件
     * @param groupEntity 分组信息
     */
    private Map<String, Long> finalCount(String core,
                                         String q,
                                         String fq,
                                         SolrGroupEntity groupEntity) throws Exception {
        Map<String, Long> countMap = new HashMap<>();
        SolrGroupEntity.GroupType groupType = groupEntity.getType();
        String groupField = groupEntity.getGroupField();
        Object gap = groupEntity.getGap();

        if (groupType.equals(SolrGroupEntity.GroupType.DATE_RANGE)) {
            // 按日期范围统计
            List<RangeFacet> rangeFacets = solrUtil.getFacetDateRange(core, groupField, startTime, endTime, gap.toString(), fq, q);
            for (RangeFacet rangeFacet : rangeFacets) {
                List<RangeFacet.Count> countList = rangeFacet.getCounts();
                for (RangeFacet.Count count : countList) {
                    if (count.getCount() > 0) {
                        // TODO 目前是按天间隔统计的写法，需要扩展按年、月等间隔统计时，需要定制分支。 -- 张进军 2018.1.26
                        String key = count.getValue().substring(0, 10);
                        countMap.put(key, (long) count.getCount());
                    }
                }
            }
        } else {
            // 按字段值统计
            countMap = solrUtil.groupCount(core, q, fq, groupField, 0, -1);
        }

        return countMap;
    }

    //endregion Count 统计

    //region 多级数值统计

    /**
     * 多级数值统计
     *
     * @param core        core名
     * @param groupFields 分组字段
     * @param statsFields 统计字段，多个则逗号分隔
     */
    public Page<Map<String, Object>> getStats(String core,
                                              String groupFields,
                                              String statsFields) throws Exception {
        return getStats(core, groupFields, statsFields, "", "", null);
    }

    /**
     * 多级数值统计
     *
     * @param core        core名
     * @param groupFields 分组字段
     * @param statsFields 统计字段，多个则逗号分隔
     * @param q           查询条件
     * @param fq          筛选条件
     */
    public Page<Map<String, Object>> getStats(String core,
                                              String groupFields,
                                              String statsFields,
                                              String q,
                                              String fq) throws Exception {
        return getStats(core, groupFields, statsFields, q, fq, null);
    }

    /**
     * 多级数值统计
     *
     * @param core        core名
     * @param groupFields 分组字段
     * @param statsFields 统计字段，多个则逗号分隔
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

    //endregion 多级数值统计

    //region 指标多维度求和统计

    /**
     * 多维度求和统计（包含自定义分组）
     * <p>
     * TODO
     * 涉及时间维度聚合统计，目前是按天间隔统计的写法，需要扩展按年、月等间隔统计时，需要定制分支。
     * 具体需要扩展的地方查看 joinAggregationCondition()、finalCount() 方法中备注。
     * -- 张进军 2018.1.26
     *
     * @param core               core名
     * @param q                  查询条件
     * @param fq                 筛选条件
     * @param statsField         统计字段
     * @param dimensionGroupList 分组字段
     * @param customGroups       额外自定义分组
     */
    public List<Map<String, Object>> getSumMultList(String core,
                                                    String q,
                                                    String fq,
                                                    String statsField,
                                                    List<SolrGroupEntity> dimensionGroupList,
                                                    List<SolrGroupEntity> customGroups) throws Exception {
        // 维度字段及最后一个维度基于其他维度组合作为条件的统计结果的集合
        List<Map<String, Object>> resultList = new ArrayList<>();

        if (dimensionGroupList != null && dimensionGroupList.size() > 0) {
            List<SolrGroupEntity> groupList = new ArrayList<>();
            if (customGroups != null && customGroups.size() > 0) {
                groupList = customGroups;
            }

            // 收集根据指标维度分组聚合的条件
            groupList.addAll(joinAggregationCondition(core, q, fq, dimensionGroupList));

            resultList = recGroupSum(core, statsField, groupList, q, fq, 0, null);
        } else { // 纯自定义分组
            if (customGroups != null && customGroups.size() > 0) {
                resultList = recGroupSum(core, statsField, customGroups, q, fq, 0, null);
            }
        }

        return resultList;
    }

    /**
     * 多维度递归求和统计
     */
    private List<Map<String, Object>> recGroupSum(String core,
                                                  String statsField,
                                                  List<SolrGroupEntity> groupList,
                                                  String q,
                                                  String fq,
                                                  int num,
                                                  List<Map<String, Object>> preList) throws Exception {
        // 维度字段、维度组合Key及统计结果
        List<Map<String, Object>> resultList = new ArrayList<>();

        String conditionName = "$condition"; // 拼接最后一个维度分组聚合统计的过滤条件
        String statisticsKeyName = "$statisticsKey"; // 拼接最后一个维度分组聚合统计值对应的唯一健
        if (num == groupList.size() - 1) {
            SolrGroupEntity group = groupList.get(num); // 最后一个分组维度
            String groupField = group.getGroupField();
            Map<String, String> groupConditionMap = group.getGroupCondition();
            DecimalFormat df = new DecimalFormat("#.00");

            if (preList != null && preList.size() > 0) {
                for (Map<String, Object> preObj : preList) {
                    // 遍历前 N-1 维度组合作为筛选条件
                    String query = preObj.get(conditionName).toString();
                    if (StringUtils.isNotEmpty(fq) && !fq.equals("*:*")) {
                        query += " AND " + fq;
                    }

                    // 收集最后一个维度的统计结果
                    for (Map.Entry<String, String> item : groupConditionMap.entrySet()) {
                        String currFq = query + " AND " + item.getValue();

                        Map<String, FieldStatsInfo> statsMap = new HashMap<>();
                        FieldStatsInfo statsInfo = solrUtil.getStats(core, q, currFq, statsField);
                        if (statsInfo.getSum() != null && !statsInfo.getSum().equals(0d)) {
                            Map<String, Object> obj = new HashMap<>();
                            obj.putAll(preObj); // 深拷贝
                            obj.put(groupField, item.getKey());
                            String statisticsKey = preObj.get(statisticsKeyName).toString() + "-" + item.getKey();
                            obj.put(statisticsKeyName, statisticsKey);
                            obj.put("$result", df.format(statsInfo.getSum()));  // 统计值
                            obj.remove(conditionName);
                            resultList.add(obj);
                        }
                    }
                }
            } else { // 只有一个维度分组统计场合
                for (Map.Entry<String, String> item : groupConditionMap.entrySet()) {
                    String currFq = item.getValue();
                    if (StringUtils.isNotEmpty(fq) && !fq.equals("*:*")) {
                        currFq += " AND " + fq;
                    }

                    Map<String, FieldStatsInfo> statsMap = new HashMap<>();
                    FieldStatsInfo statsInfo = solrUtil.getStats(core, q, currFq, statsField);
                    if (statsInfo.getSum() != null && !statsInfo.getSum().equals(0d)) {
                        Map<String, Object> obj = new HashMap<>();
                        obj.put(groupField, item.getKey());
                        obj.put(statisticsKeyName, item.getKey());
                        obj.put("$result", df.format(statsInfo.getSum()));  // 统计值
                        resultList.add(obj);
                    }
                }
            }

            return resultList;
        } else {
            List<Map<String, Object>> list = new ArrayList<>(); // 返回集合
            SolrGroupEntity group = groupList.get(num); // 当前分组项
            Map<String, String> groupConditionMap = group.getGroupCondition();
            String groupField = group.getGroupField();

            if (preList != null) {
                //遍历上级递归数据
                for (Map<String, Object> preObj : preList) {
                    //遍历当前分组数据
                    for (Map.Entry<String, String> item : groupConditionMap.entrySet()) {
                        Map<String, Object> obj = new HashMap<>();
                        obj.putAll(preObj); // 深拷贝
                        obj.put(groupField, item.getKey());
                        String condition = preObj.get(conditionName).toString() + " AND " + item.getValue();
                        obj.put(conditionName, condition);
                        String statisticsKey = preObj.get(statisticsKeyName).toString() + "-" + item.getKey();
                        obj.put(statisticsKeyName, statisticsKey);
                        list.add(obj);
                    }
                }
            } else { //第一次遍历
                for (Map.Entry<String, String> item : groupConditionMap.entrySet()) {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put(groupField, item.getKey());
                    obj.put(conditionName, item.getValue());
                    obj.put(statisticsKeyName, item.getKey());
                    list.add(obj);
                }
            }

            return recGroupSum(core, statsField, groupList, q, fq, num + 1, list);
        }
    }

    //endregion 多维度求和统计

    //region 公共私有方法

    /**
     * 收集多分组聚合的条件
     * 注意：其中指标维度code出现的顺序要与 dimensionGroupList 中的一致。
     *
     * @param core               core名
     * @param q                  查询条件
     * @param fq                 筛选条件
     * @param dimensionGroupList 分组字段
     */
    private List<SolrGroupEntity> joinAggregationCondition(String core,
                                                           String q,
                                                           String fq,
                                                           List<SolrGroupEntity> dimensionGroupList) throws Exception {
        List<SolrGroupEntity> groupList = new ArrayList<>();

        for (SolrGroupEntity dimensionGroup : dimensionGroupList) {
            Object gap = dimensionGroup.getGap();
            if (dimensionGroup.getType().equals(SolrGroupEntity.GroupType.DATE_RANGE)) {
                // 按日期范围统计
                List<RangeFacet> rangeFacets = solrUtil.getFacetDateRange(core, dimensionGroup.getGroupField(), startTime, endTime, gap.toString(), fq, q);
                for (RangeFacet rangeFacet : rangeFacets) {
                    String groupName = rangeFacet.getName();
                    List<RangeFacet.Count> countList = rangeFacet.getCounts();
                    SolrGroupEntity groupEntity = new SolrGroupEntity(groupName);
                    groupEntity.setType(SolrGroupEntity.GroupType.DATE_RANGE);
                    groupEntity.setGap(gap);
                    for (RangeFacet.Count count : countList) {
                        if (count.getCount() > 0) {
                            // TODO 目前是按天间隔统计的写法，需要扩展按年、月等间隔统计时，需要定制分支。 -- 张进军 2018.1.26
                            String day = count.getValue().substring(0, 10);
                            groupEntity.putGroupCondition(day, String.format("%s:[%sT00:00:00Z TO %sT23:59:59Z]", groupName, day, day));
                        }
                    }
                    if (groupEntity.getGroupCondition().size() > 0) {
                        groupList.add(groupEntity);
                    }
                }
            } else {
                // 按分组字段值统计
                String[] groupField = {dimensionGroup.getGroupField()};
                List<FacetField> facets = solrUtil.groupCount(core, q, fq, groupField);
                for (FacetField facet : facets) {
                    String groupName = facet.getName();
                    List<FacetField.Count> counts = facet.getValues();
                    SolrGroupEntity groupEntity = new SolrGroupEntity(groupName);
                    for (FacetField.Count count : counts) {
                        if (count.getCount() > 0) {
                            String value = count.getName();
                            groupEntity.putGroupCondition(value, groupName + ":" + value);
                        }
                    }
                    if (groupEntity.getGroupCondition().size() > 0) {
                        groupList.add(groupEntity);
                    }
                }
            }
        }

        return groupList;
    }

    //endregion 公共私有方法

}
