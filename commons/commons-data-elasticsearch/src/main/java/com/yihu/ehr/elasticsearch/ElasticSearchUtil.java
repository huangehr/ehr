package com.yihu.ehr.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Util - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticSearchUtil {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    /**
     * 创建映射
     *  注意：保存数据之前如果没有创建相应的字
     *  段映射会导致搜索结果不准确
     * @param index
     * @param type
     * @param source
     * @param setting - 该设置根据需要进行配置
     * @throws IOException
     */
    public void mapping (String index, String type, Map<String, Map<String, String>> source, Map<String, Object> setting) throws IOException{
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        for (String field : source.keySet()) {
            xContentBuilder.startObject(field);
            Map<String, String> propsMap = source.get(field);
            for (String prop : propsMap.keySet()) {
                xContentBuilder.field(prop, propsMap.get(prop));
            }
            xContentBuilder.endObject();
        }
        xContentBuilder.endObject().endObject();
        elasticSearchClient.mapping(index, type, xContentBuilder, setting);
    }

    /**
     * 移除索引 - 整个移除
     * @param index
     */
    public void remove (String index){
        elasticSearchClient.remove(index);
    }

    /**
     * 添加数据
     * @param index
     * @param type
     * @param source
     * @return
     * @throws ParseException
     */
    public Map<String, Object> index (String index, String type, Map<String, Object> source) throws ParseException{
        return elasticSearchClient.index(index, type, source);
    }

    /**
     * 批量添加数据 - 效率高
     * @param index
     * @param type
     * @param source
     * @throws ParseException
     */
    public void bulkIndex (String index, String type, List<Map<String, Object>> source) throws ParseException{
        if (source.size() > 0) {
            elasticSearchClient.bulkIndex(index, type, source);
        }
    }

    /**
     * 删除数据
     * @param index
     * @param type
     * @param id
     */
    public void delete (String index, String type, String id) {
        elasticSearchClient.delete(index, type, id);
    }

    /**
     * 批量删除数据
     * @param index
     * @param type
     * @param idArr
     */
    public void bulkDelete (String index, String type, String [] idArr) {
        if (idArr.length > 0) {
            elasticSearchClient.bulkDelete(index, type, idArr);
        }
    }

    /**
     * 根据字段批量删除数据
     * @param index
     * @param type
     * @param field
     * @param value
     */
    public void deleteByField(String index, String type, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(field, value);
        boolQueryBuilder.must(termsQueryBuilder);
        List<String> idList = elasticSearchClient.getIds(index, type, boolQueryBuilder);
        if (idList.size() > 0) {
            String [] idArr = new String[idList.size()];
            idArr = idList.toArray(idArr);
            elasticSearchClient.bulkDelete(index, type, idArr);
        }
    }

    public void deleteByFilter(String index, String type, BoolQueryBuilder boolQueryBuilder) {
        List<String> idList = elasticSearchClient.getIds(index, type, boolQueryBuilder);
        if (idList.size() > 0) {
            String [] idArr = new String[idList.size()];
            idArr = idList.toArray(idArr);
            elasticSearchClient.bulkDelete(index, type, idArr);
        }
    }

    /**
     * 更新数据 - 返回最新文档
     * @param index
     * @param type
     * @param id
     * @param source
     * @return
     * @throws DocumentMissingException
     */
    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        source.remove("_id");
        return elasticSearchClient.update(index, type, id, source);
    }

    /**
     * 更新数据 - 不返回文档
     * @param index
     * @param type
     * @param id
     * @param source
     * @throws DocumentMissingException
     */
    public void voidUpdate (String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        source.remove("_id");
        elasticSearchClient.voidUpdate(index, type, id, source);
    }

    /**
     * 批量更新数据
     * @param index
     * @param type
     * @param source
     * @throws DocumentMissingException
     */
    public void bulkUpdate(String index, String type, List<Map<String, Object>> source) throws DocumentMissingException {
        if (source.size() > 0) {
            elasticSearchClient.bulkUpdate(index, type, source);
        }
    }

    /**
     * 根据ID查找数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    public Map<String, Object> findById(String index, String type, String id) {
        return elasticSearchClient.findById(index, type, id);
    }

    /**
     * 根据字段查找数据
     * @param index
     * @param type
     * @param field
     * @param value
     * @return
     */
    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(field, value);
        boolQueryBuilder.must(termQueryBuilder);
        return elasticSearchClient.findByField(index, type, boolQueryBuilder);
    }

    /**
     * 获取文档列表
     * @param index
     * @param type
     * @param filters
     * @return
     */
    public List<Map<String, Object>> list(String index, String type, String filters) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.findByField(index, type, boolQueryBuilder);
    }

    /**
     * 获取文档分页
     * @param index
     * @param type
     * @param filters
     * @param page
     * @param size
     * @return
     */
    public List<Map<String, Object>> page(String index, String type, String filters, int page, int size) {
        return this.page(index, type, filters, null, page, size);
    }

    /**
     * 获取分档分页 - 带分页功能
     * @param index
     * @param type
     * @param filters
     * @param sorts
     * @param page
     * @param size
     * @return
     */
    public List<Map<String, Object>> page(String index, String type, String filters, String sorts, int page, int size) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        List<SortBuilder> sortBuilderList = getSortBuilder(sorts);
        return elasticSearchClient.page(index, type, boolQueryBuilder, sortBuilderList, page, size);
    }

    /**
     * 获取文档数
     * @param index
     * @param type
     * @param filters
     * @return
     */
    public long count(String index, String type, String filters) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.count(index, type, boolQueryBuilder);
    }

    //分组统计
    public Map<String,Long> countByGroup(String index, String type, String filters,String groupField) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.countByGroup(index, type, boolQueryBuilder,groupField);
    }

    //分组求和
    public Map<String,Double> sumtByGroup(String index, String type, String filters,String sumField,String groupField) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.sumByGroup(index, type, boolQueryBuilder,sumField,groupField);
    }

    /**
     * 根据SQL查找数据
     * @param field
     * @param sql
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> findBySql(List<String> field, String sql) throws Exception {
        return elasticSearchClient.findBySql(field, sql);
    }

    /**
     * 根据SQL查找数据
     * @param sql
     * @return
     * @throws Exception
     */
    public ResultSet findBySql(String sql) throws Exception {
        return elasticSearchClient.findBySql(sql);
    }

    /**
     * 根据日期分组
     * @param index
     * @param type
     * @param filters
     * @param start
     * @param end
     * @param field
     * @param interval
     * @param format
     * @return
     */
    public Map<String, Long> dateHistogram(String index, String type, String filters, Date start, Date end, String field, DateHistogramInterval interval, String format) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.dateHistogram(index, type, boolQueryBuilder, start, end, field, interval, format);
    }

    /**
     * 查询去重数量
     * @param index
     * @param type
     * @param filters
     * @param filed
     * @return
     */
    public int cardinality(String index, String type, String filters, String filed){
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.cardinality(index, type, boolQueryBuilder, filed);
    }

    /**
     * 排序语句转换
     * @param sorts
     * @return
     */
    private List<SortBuilder> getSortBuilder(String sorts) {
        List<SortBuilder> sortBuilderList = new ArrayList<>();
        if (StringUtils.isEmpty(sorts)) {
            return sortBuilderList;
        }
        String [] sortArr = sorts.split(";");
        for (String sort : sortArr) {
            String operator = sort.substring(0, 1);
            SortBuilder sortBuilder = new FieldSortBuilder(sort.substring(1));
            if ("-".equalsIgnoreCase(operator.trim())) {
                sortBuilder.order(SortOrder.DESC);
            } else if ("+".equalsIgnoreCase(operator.trim())) {
                sortBuilder.order(SortOrder.ASC);
            } else {
                sortBuilder.order(SortOrder.DESC);
            }
            sortBuilderList.add(sortBuilder);
        }
        return sortBuilderList;
    }

    /**
     * 查询语句转换
     * @param filters
     * @return
     */
    public QueryBuilder getQueryBuilder(String filters) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isEmpty(filters)) {
            return boolQueryBuilder;
        }
        String [] filterArr = filters.split(";");
        for (String filter : filterArr) {
            if (filter.contains("||")){
                String [] fields = filter.split("\\|\\|");
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                for (String filed : fields) {
                    String [] condition = filed.split("=");
                    queryBuilder.should(QueryBuilders.termQuery(condition[0], condition[1]));
                }
                boolQueryBuilder.must(queryBuilder);
            } else if (filter.contains("?")) {
                String [] condition = filter.split("\\?");
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(condition[0], condition[1]);
                boolQueryBuilder.must(matchQueryBuilder);
            } else if (filter.contains("<>")) {
                String [] condition = filter.split("<>");
                if (condition[1].contains(",")) {
                    String [] inCondition = condition[1].split(",");
                    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(condition[0], inCondition);
                    boolQueryBuilder.mustNot(termsQueryBuilder);
                } else {
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(condition[0], condition[1]);
                    boolQueryBuilder.mustNot(termQueryBuilder);
                }
            } else if (filter.contains(">=")) {
                String [] condition = filter.split(">=");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.gte(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains(">")) {
                String [] condition = filter.split(">");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.gt(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("<=")) {
                String [] condition = filter.split("<=");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.lte(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("<")) {
                String [] condition = filter.split("<");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.lt(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("=")) {
                String [] condition = filter.split("=");
                if (condition[1].contains(",")) {
                    String [] inCondition = condition[1].split(",");
                    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(condition[0], inCondition);
                    boolQueryBuilder.must(termsQueryBuilder);
                } else {
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(condition[0], condition[1]);
                    boolQueryBuilder.must(termQueryBuilder);
                }
            }
        }
        return boolQueryBuilder;
    }

}
