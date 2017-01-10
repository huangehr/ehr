package com.yihu.ehr.solr;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Solr底层操作类
 *
 * @author hzp
 * @version 1.0
 * @created 2016.04.26
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SolrUtil {
    @Autowired
    SolrPool pool;
    private final static String ASC = "asc";

    /**
     * 获取查询耗时
     */
    private int qtime;

    private int getQtime() {
        return qtime;
    }

    /************************* 基础操作 **************************************************/


    /************************** 查询操作 *****************************************************/
    /**
     * 简单查询方法
     */
    public SolrDocumentList query(String tablename, String q, Map<String, String> sort, long start, long rows) throws Exception {
        return query(tablename, q, null, sort, start, rows);
    }

    /**
     * Solr查询方法
     *
     * @param q     查询字符串
     * @param fq    过滤查询
     * @param sort  过滤条件
     * @param start 查询起始行
     * @param rows  查询行数
     * @return
     */
    public SolrDocumentList query(String core, String q, String fq, Map<String, String> sort, long start, long rows) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) //设置查询条件
        {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) //设置过滤条件
        {
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
        qtime = rsp.getQTime();
        System.out.print("Solr Query Time:" + qtime);
        SolrDocumentList docs = rsp.getResults();

        pool.close(core); //释放连接
        return docs;

    }

    /******************************* Count 统计 ***********************************************/
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
        query.setRows(1);
        if (null != q && !q.equals("")) //设置查询条件
        {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) //设置过滤条件
        {
            query.setFilterQueries(fq);
        }

        QueryResponse rsp = conn.query(query);
        qtime = rsp.getQTime();
        System.out.print("Solr Count Time:" + qtime);

        pool.close(core);
        return rsp.getResults().getNumFound();
    }

    /**
     * 单组分组Count统计（start从0开始）
     */
    public Map<String, Long> groupCount(String core, String q, String fq, String groupField, int start, int rows) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) //设置查询条件
        {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) //设置过滤条件
        {
            query.setFilterQueries(fq);
        }

        query.setFacet(true);//设置facet=on
        query.setRows(0);
        query.addFacetField(groupField);
        query.setFacetLimit(rows);//限制每次返回结果数
        query.set(FacetParams.FACET_OFFSET, start);
        query.setFacetMissing(false);//不统计null的值
        query.setFacetMinCount(0);// 设置返回的数据中每个分组的数据最小值，比如设置为0，则统计数量最小为0，不然不显示

        QueryResponse rsp = conn.query(query);
        List<FacetField.Count> countList = rsp.getFacetField(groupField).getValues();
        qtime = rsp.getQTime();
        System.out.print("Solr Group Time:" + qtime);

        Map<String, Long> rmap = new HashMap<String, Long>();
        for (FacetField.Count count : countList) {
            if (count.getCount() > 0)
                rmap.put(count.getName(), (long) count.getCount());
        }

        pool.close(core);
        return rmap;
    }

    /**
     * 多组分组Count(独立计算)
     */
    public List<FacetField> groupCount(String core, String q, String fq, String[] groups) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) //设置查询条件
        {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) //设置过滤条件
        {
            query.setFilterQueries(fq);
        }

        query.setFacet(true);//设置facet=on
        query.setRows(0);
        query.addFacetField(groups);
        query.setFacetLimit(1000);//限制每次返回结果数
        query.set(FacetParams.FACET_OFFSET, 0);
        query.setFacetMissing(true);//不统计null的值
        query.setFacetMinCount(0);// 设置返回的数据中每个分组的数据最小值，比如设置为0，则统计数量最小为0，不然不显示

        QueryResponse rsp = conn.query(query);
        qtime = rsp.getQTime();
        System.out.print("Solr Group Time:" + qtime);
        List<FacetField> facets = rsp.getFacetFields();

        pool.close(core);
        return facets;
    }

    /**
     * 多组分组Count统计（关联计算）
     *
     * @return
     */
    public List<PivotField> groupCountMult(String core, String q, String fq, String groupFields, int start, int rows) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();
        if (null != q && !q.equals("")) //设置查询条件
        {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) //设置过滤条件
        {
            query.setFilterQueries(fq);
        }

        query.setFacet(true);//设置facet=on
        query.setRows(0);
        query.addFacetPivotField(groupFields);
        query.setFacetLimit(rows);//限制每次返回结果数
        query.setFacetMissing(false);//不统计null的值
        query.setFacetMinCount(0);// 设置返回的数据中每个分组的数据最小值，比如设置为0，则统计数量最小为0，不然不显示

        QueryResponse rsp = conn.query(query);
        qtime = rsp.getQTime();
        System.out.print("Solr Group Time:" + qtime);

        NamedList<List<PivotField>> namedList = rsp.getFacetPivot();

        pool.close(core);
        if (namedList != null && namedList.size() > 0) {
            return namedList.getVal(0);
        } else
            return null;
    }


    /**************************** 数值统计 ******************************************/
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
        if (null != q && !q.equals("")) //设置查询条件
        {
            query.setQuery(q);
        } else {
            query.setQuery("*:*");
        }
        if (null != fq && !fq.equals("")) //设置过滤条件
        {
            query.setFilterQueries(fq);
        }

        query.addGetFieldStatistics(statsField);
        query.addStatsFieldFacets(statsField, groupField);
        query.setRows(0);

        QueryResponse rsp = conn.query(query);
        qtime = rsp.getQTime();
        System.out.print("Solr Stats Time:" + qtime);

        Map<String, FieldStatsInfo> stats = rsp.getFieldStatsInfo();
        pool.close(core);
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
     * @param core       表名
     * @param facetQuery 查询条件
     * @return
     * @throws Exception
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
     * @return
     * @throws Exception
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
     *
     * @param core
     * @param dateField
     * @param startTime
     * @param endTime
     * @param grap
     * @param fq
     * @return
     * @throws Exception
     */
    public List<RangeFacet> getFacetDateRange(String core, String dateField, Date startTime, Date endTime, String grap, String fq) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();

        query.setQuery("*:*");

        if (!StringUtils.isBlank(fq)) {

            query.setFilterQueries(fq);
        }

        query.setRows(0)
               .setFacet(true)
               .addDateRangeFacet(dateField, startTime, endTime, grap);
        QueryResponse resp = conn.query(query);

        return resp.getFacetRanges();
    }

    /**
     * 数值型字段范围统计
     *
     * @param core
     * @param numField
     * @param start
     * @param end
     * @param grap
     * @param fq
     * @return
     * @throws Exception
     */
    public List<RangeFacet> getFacetNumRange(String core, String numField, int start, int end, int grap, String fq) throws Exception {
        SolrClient conn = pool.getConnection(core);
        SolrQuery query = new SolrQuery();

        query.setQuery("*:*");

        if (!StringUtils.isBlank(fq)) {
            query.setFilterQueries(fq);
        }

        query.setRows(0)
                .setFacet(true)
                .addNumericRangeFacet(numField, start, end, grap);
        QueryResponse resp = conn.query(query);

        return resp.getFacetRanges();
    }
}
