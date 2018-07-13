package com.yihu.ehr.solr;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Solr底层查询类
 *
 * @author hzp
 * @version 1.0
 * @created 2016.04.26
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SolrUtil {

    private final static String ASC = "asc";

    @Autowired
    private SolrPool pool;

    /**
     * 简单查询方法
     */
    public SolrDocumentList query(String tableName, String q, Map<String, String> sort, long start, long rows) throws Exception {
        return query(tableName, q, null, sort, start, rows, null);
    }

    /**
     * 简单查询返回字段
     */
    public SolrDocumentList queryReturnFieldList(String tableName, String q, String fq, Map<String, String> sort, long start, long rows, String... fields) throws Exception {
        return query(tableName, q, fq, sort, start, rows, fields);
    }

    /**
     * Solr查询方法
     *
     * @param q      查询字符串
     * @param fq     过滤查询
     * @param sort   排序
     * @param start  查询起始行
     * @param rows   查询行数
     * @param fields 返回字段
     * @return
     */
    public SolrDocumentList query(String core, String q, String fq, Map<String, String> sort, long start, long rows, String... fields) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) { //设置查询条件
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) { //设置过滤条件
            query.setFilterQueries(fq);
        }
        query.setFields(fields);
        query.setStart(Integer.parseInt(String.valueOf(start)));//设置查询起始行
        query.setRows(Integer.parseInt(String.valueOf(rows)));//设置查询行数

        //设置排序
        if (sort != null) {
            for (Object co : sort.keySet()) {
                if (ASC == sort.get(co).toLowerCase() || ASC.equals(sort.get(co).toLowerCase())) {
                    query.addSort(co.toString(), SolrQuery.ORDER.asc);
                } else {
                    query.addSort(co.toString(), SolrQuery.ORDER.desc);
                }
            }
        }
        QueryResponse rsp = conn.query(query);
        SolrDocumentList docs = rsp.getResults();
        return docs;

    }

    /**
     * Solr单个字段去重查询
     *
     * @param q          可选，查询字符串
     * @param fq         可选，过滤查询
     * @param sort       可选，排序
     * @param start      必填，查询起始行
     * @param rows       必填，查询行数
     * @param fields     必填，返回字段
     * @param groupField 必填，分组去重字段。针对一个字段去重。
     * @param groupSort  可选，组内排序字段，如："event_date asc"
     * @return
     */
    public List<Group> queryDistinctOneField(String core, String q, String fq, Map<String, String> sort, long start, long rows,
                                                  String[] fields, String groupField, String groupSort) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (StringUtils.isNotEmpty(q)) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (StringUtils.isNotEmpty(fq)) {
            query.setFilterQueries(fq);
        }
        if (sort != null) {
            for (Object co : sort.keySet()) {
                if (ASC.equals(sort.get(co).toLowerCase())) {
                    query.addSort(co.toString(), SolrQuery.ORDER.asc);
                } else {
                    query.addSort(co.toString(), SolrQuery.ORDER.desc);
                }
            }
        }
        query.setFields(fields);
        query.setStart(Integer.parseInt(String.valueOf(start)));
        query.setRows(Integer.parseInt(String.valueOf(rows)));
        query.setParam(GroupParams.GROUP, true);
        query.setParam(GroupParams.GROUP_FORMAT, "grouped");
        query.setParam(GroupParams.GROUP_FIELD, groupField);
        if (StringUtils.isNotEmpty(groupSort)) {
            query.setParam(GroupParams.GROUP_SORT, groupSort);
        }

        List<Group> groups = new ArrayList<>();
        QueryResponse response = conn.query(query);
        GroupResponse groupResponse = response.getGroupResponse();
        if (groupResponse != null) {
            List<GroupCommand> groupList = groupResponse.getValues();
            for (GroupCommand groupCommand : groupList) {
                groups = groupCommand.getValues();
            }
        }

        return groups;
    }

    /**
     * Solr单个字段去重查询
     *
     * @param q          可选，查询字符串
     * @param fq         可选，过滤查询
     * @param sort       可选，排序
     * @param start      必填，查询起始行
     * @param rows       必填，查询行数
     * @param fields     必填，返回字段
     * @param groupField 必填，分组去重字段。针对一个字段去重。
     * @param groupSort  可选，组内排序字段，如："event_date asc"
     * @return
     */
    public SolrDocumentList queryDistinctOneFieldForDocList(String core, String q, String fq, Map<String, String> sort, long start, long rows,
                                             String[] fields, String groupField, String groupSort) throws Exception {
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();

        List<Group> groups = queryDistinctOneField(core, q, fq, sort, start, rows, fields, groupField, groupSort);

        QueryResponse response = conn.query(query);
        GroupResponse groupResponse = response.getGroupResponse();
        if (groupResponse != null) {
            List<GroupCommand> groupList = groupResponse.getValues();
            for (GroupCommand groupCommand : groupList) {
                groups = groupCommand.getValues();
                for (Group group : groups) {
                    if (group.getResult().size() > 0) {
                        solrDocumentList.add(group.getResult().get(0));
                    }
                }
            }
        }

        return solrDocumentList;
    }


    /**
     * Solr查询方法 多个过滤条件
     *
     * @param q     查询字符串
     * @param fq    过滤查询  多个过滤条件
     * @param sort  排序
     * @param start 查询起始行
     * @param rows  查询行数
     * @return
     */
    public SolrDocumentList queryByfqs(String core, String q, String[] fq, Map<String, String> sort, long start, long rows) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && fq.length > 0) {
            query.setFilterQueries(fq);
        }

        query.setStart(Integer.parseInt(String.valueOf(start)));//设置查询起始行
        query.setRows(Integer.parseInt(String.valueOf(rows)));//设置查询行数

        //设置排序
        if (sort != null) {
            for (Object co : sort.keySet()) {
                if (ASC == sort.get(co).toLowerCase() || ASC.equals(sort.get(co).toLowerCase())) {
                    query.addSort(co.toString(), SolrQuery.ORDER.asc);
                } else {
                    query.addSort(co.toString(), SolrQuery.ORDER.desc);
                }
            }
        }

        QueryResponse rsp = conn.query(query);
        return rsp.getResults();

    }

    /**
     * 总数查询方法
     */
    public long count(String core, String q) throws Exception {
        return count(core, q, null);
    }

    /**
     * 总数查询方法
     */
    public long count(String core, String q, String fq) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) { //设置查询条件
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) {
            query.setFilterQueries(fq);
        }
        query.setStart(0);
        query.setRows(0);

        QueryResponse rsp = conn.query(query);
        long count = rsp.getResults().getNumFound();
        //query.setStart(start);
        //rsp = conn.query(query);
        //SolrDocumentList docs = rsp.getResults();
        return count;

    }

    /**
     * 单组分组Count统计（start从0开始）
     *
     * @param core       core名
     * @param q          查询条件
     * @param fq         筛选条件
     * @param groupField 分组字段名
     * @param start      起始偏移位
     * @param limit      结果条数，为负数则不限制
     */
    public Map<String, Long> groupCount(String core, String q, String fq, String groupField, int start, int limit) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) {
            query.setFilterQueries(fq);
        }
        query.setFacet(true);//设置facet=on
        query.setRows(0);
        query.addFacetField(groupField);
        query.setFacetLimit(limit);//限制每次返回结果数
        query.set(FacetParams.FACET_OFFSET, start);
        query.setFacetMissing(false);//不统计null的值
        query.setFacetMinCount(0);// 设置返回的数据中每个分组的数据最小值，比如设置为0，则统计数量最小为0，不然不显示
        QueryResponse rsp = conn.query(query);
        List<FacetField.Count> countList = rsp.getFacetField(groupField).getValues();
        Map<String, Long> rmap = new HashMap<>();
        for (FacetField.Count count : countList) {
            if (count.getCount() > 0)
                rmap.put(count.getName(), (long) count.getCount());
        }
        return rmap;

    }

    /**
     * 多组分组Count(独立计算)
     *
     * @param core        core名
     * @param q           查询条件
     * @param fq          筛选条件
     * @param groupFields 分组字段名
     */
    public List<FacetField> groupCount(String core, String q, String fq, String[] groupFields) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) {
            query.setFilterQueries(fq);
        }

        query.setFacet(true);//设置facet=on
        query.setRows(0);
        query.addFacetField(groupFields);
        query.setFacetLimit(-1); // 限制每次返回结果数
        query.set(FacetParams.FACET_OFFSET, 0);
        query.setFacetMissing(false); // 不统计null的值
        query.setFacetMinCount(0); // 设置返回的数据中每个分组的数据最小值，比如设置为0，则统计数量最小为0，不然不显示

        QueryResponse rsp = conn.query(query);
        return rsp.getFacetFields();

    }

    /**
     * 多组分组Count统计（关联计算）
     *
     * @param core        core名
     * @param q           查询条件
     * @param fq          筛选条件
     * @param groupFields 分组字段名
     * @param start       起始偏移位
     * @param limit       结果条数，为负数则不限制
     */
    public List<PivotField> groupCountMult(String core, String q, String fq, String groupFields, int start, int limit) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) {
            query.setFilterQueries(fq);
        }

        query.setFacet(true);//设置facet=on
        query.setRows(0);
        query.addFacetPivotField(groupFields);
        query.set(FacetParams.FACET_OFFSET, start);
        query.setFacetLimit(limit);//限制每次返回结果数
        query.setFacetMissing(false);//不统计null的值
        query.setFacetMinCount(0);// 设置返回的数据中每个分组的数据最小值，比如设置为0，则统计数量最小为0，不然不显示

        QueryResponse rsp = conn.query(query);
        NamedList<List<PivotField>> namedList = rsp.getFacetPivot();

        if (namedList != null && namedList.size() > 0) {
            return namedList.getVal(0);
        } else {
            return null;
        }
    }

    /**
     * 分组数值统计
     *
     * @param core       表名
     * @param q          查询条件
     * @param statsField 统计字段
     * @return
     */
    public FieldStatsInfo getStats(String core, String q, String fq, String statsField) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) {
            query.setFilterQueries(fq);
        }

        query.addGetFieldStatistics(statsField);
        query.setRows(0);

        QueryResponse rsp = conn.query(query);
        Map<String, FieldStatsInfo> stats = rsp.getFieldStatsInfo();
        if (stats != null && stats.size() > 0) {
            return stats.get(statsField);
        }
        return null;
    }

    /**
     * 分组数值统计
     *
     * @param core       表名
     * @param q          查询条件
     * @param statsField 统计字段
     * @param groupField 分组字段
     * @return
     */
    public List<FieldStatsInfo> getStats(String core, String q, String fq, String statsField, String groupField) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) {
            query.setFilterQueries(fq);
        }

        query.addGetFieldStatistics(statsField);
        query.addStatsFieldFacets(statsField, groupField);
        query.setRows(0);

        QueryResponse rsp = conn.query(query);
        Map<String, FieldStatsInfo> stats = rsp.getFieldStatsInfo();
        if (stats != null && stats.size() > 0) {
            Map<String, List<FieldStatsInfo>> map = stats.get(statsField).getFacets();
            if (map != null) {
                return map.get(groupField);
            }
        }
        return null;
    }


    /**
     * 查询统计
     *
     * @param core       core名
     * @param facetQuery 查询条件
     */
    public Map<String, Integer> getFacetQuery(String core, String facetQuery) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFacet(true);
        query.addFacetQuery(facetQuery);
        QueryResponse resp = conn.query(query);
        return resp.getFacetQuery();
    }


    /**
     * 单字段分组统计
     *
     * @param core
     * @param facetField
     * @param fq
     * @param minCount
     * @param start
     * @param limit
     * @param missing
     */
    public FacetField getFacetField(String core, String facetField, String fq, int minCount, int start, int limit, boolean missing) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");

        if (!StringUtils.isBlank(fq)) {
            query.setFilterQueries(fq);
        }

        query.setStart(start)
                .setRows(0)
                .setFacet(true)
                .addFacetField(facetField)
                .setFacetMinCount(minCount)
                .setFacetLimit(limit)
                .setFacetMissing(missing);

        QueryResponse resp = conn.query(query);

        return resp.getFacetField(facetField);

    }

    /**
     * 日期范围分组统计
     */
    public List<RangeFacet> getFacetDateRange(String core, String dateField, Date startTime, Date endTime, String gap, String fq) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        if (!StringUtils.isBlank(fq)) {
            query.setFilterQueries(fq);
        }
        query.setRows(0)
                .setFacet(true)
                .addDateRangeFacet(dateField, startTime, endTime, gap);
        QueryResponse resp = conn.query(query);
        return resp.getFacetRanges();
    }

    /**
     * 日期范围分组统计
     */
    public List<RangeFacet> getFacetDateRange(String core, String field, String start, String end, String gap, String fq, String q) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (StringUtils.isEmpty(q)) {
            query.setQuery("*:*");
        } else {
            query.setQuery(q);
        }
        if (!StringUtils.isEmpty(fq)) {
            query.setFilterQueries(fq);
        }
        query.setRows(0)
                .setFacet(true)
                .setFacetMissing(false)
                .add("facet.range", new String[]{field})
                .add(String.format(Locale.ROOT, "f.%s.%s", new Object[]{field, "facet.range.start"}), new String[]{start})
                .add(String.format(Locale.ROOT, "f.%s.%s", new Object[]{field, "facet.range.end"}), new String[]{end})
                .add(String.format(Locale.ROOT, "f.%s.%s", new Object[]{field, "facet.range.gap"}), new String[]{gap});
        QueryResponse resp = conn.query(query);
        return resp.getFacetRanges();

    }

    /**
     * 数值型字段范围统计
     */
    public List<RangeFacet> getFacetNumRange(String core, String field, int start, int end, int gap, String fq) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        if (!StringUtils.isBlank(fq)) {
            query.setFilterQueries(fq);
        }
        query.setRows(0)
                .setFacet(true)
                .addNumericRangeFacet(field, start, end, gap);
        QueryResponse resp = conn.query(query);
        return resp.getFacetRanges();
    }

}
