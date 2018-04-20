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
import java.util.*;

/**
 * Util - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticSearchUtil {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    public void mapping(String index, String type, Map<String, Map<String, String>> source) throws IOException{
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
        elasticSearchClient.mapping(index, type, xContentBuilder);
    }

    public void remove(String index){
        elasticSearchClient.remove(index);
    }

    public Map<String, Object> index(String index, String type, Map<String, Object> source) throws ParseException{
        return elasticSearchClient.index(index, type, source);
    }

    public void delete(String index, String type, String [] idArr) {
        elasticSearchClient.delete(index, type, idArr);
    }

    public void deleteByField(String index, String type, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
        boolQueryBuilder.must(matchQueryBuilder);
        List<String> idList = elasticSearchClient.getIds(index, type, boolQueryBuilder);
        String [] idArr = new String[idList.size()];
        idArr = idList.toArray(idArr);
        elasticSearchClient.delete(index, type, idArr);
    }

    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        if (source.containsKey("_id")) {
            source.remove("_id");
        }
        return elasticSearchClient.update(index, type, id, source);
    }

    public Map<String, Object> findById(String index, String type, String id) {
        return elasticSearchClient.findById(index, type, id);
    }

    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(field, value);
        boolQueryBuilder.must(termQueryBuilder);
        return elasticSearchClient.findByField(index, type, boolQueryBuilder);
    }

    public List<Map<String, Object>> list(String index, String type, String filters) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.findByField(index, type, boolQueryBuilder);
    }

    public List<Map<String, Object>> page(String index, String type, String filters, int page, int size) {
        return this.page(index, type, filters, null, page, size);
    }

    public List<Map<String, Object>> page(String index, String type, String filters, String sorts, int page, int size) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        List<SortBuilder> sortBuilderList = getSortBuilder(sorts);
        return elasticSearchClient.page(index, type, boolQueryBuilder, sortBuilderList, page, size);
    }

    public long count(String index, String type, String filters) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filters);
        return elasticSearchClient.count(index, type, boolQueryBuilder);
    }

    public List<Map<String, Object>> findBySql(List<String> field, String sql) throws Exception {
        return elasticSearchClient.findBySql(field, sql);
    }

    public ResultSet findBySql(String sql) throws Exception {
        return elasticSearchClient.findBySql(sql);
    }

    public List<Map<String, Long>> dateHistogram(String index, String type,  List<Map<String, Object>> filter, Date start, Date end, String field, DateHistogramInterval interval, String format) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filter);
        return elasticSearchClient.dateHistogram(index, type, boolQueryBuilder, start, end, field, interval, format);
    }

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

    private QueryBuilder getQueryBuilder(String filters) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isEmpty(filters)) {
            return boolQueryBuilder;
        }
        String [] filterArr = filters.split(";");
        for (String filter : filterArr) {
            if (filter.contains("?")) {
                String [] condition = filter.split("\\?");
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(condition[0], condition[1]);
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
                if (condition[0].toLowerCase().endsWith("date")) {
                    rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
                }
                rangeQueryBuilder.gte(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains(">")) {
                String [] condition = filter.split(">");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                if (condition[0].toLowerCase().endsWith("date")) {
                    rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
                }
                rangeQueryBuilder.gt(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("<=")) {
                String [] condition = filter.split("<=");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                if (condition[0].toLowerCase().endsWith("date")) {
                    rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
                }
                rangeQueryBuilder.lte(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("<")) {
                String [] condition = filter.split("<");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                if (condition[0].toLowerCase().endsWith("date")) {
                    rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
                }
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

    private QueryBuilder getQueryBuilder(List<Map<String, Object>> filter) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map<String, Object> param : filter) {
            String andOr = String.valueOf(param.get("andOr"));
            String condition = String.valueOf(param.get("condition"));
            String field = String.valueOf(param.get("field"));
            Object value = param.get("value");
            if (condition.equals("=")) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
                if ("and".equals(andOr)) {
                    boolQueryBuilder.must(matchQueryBuilder);
                } else if ("or".equals(andOr)) {
                    boolQueryBuilder.should(matchQueryBuilder);
                }
            } else if (condition.equals("?")) {
                QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(field + ":" + value);
                if ("and".equals(andOr)) {
                    boolQueryBuilder.must(queryStringQueryBuilder);
                } else if("or".equals(andOr)) {
                    boolQueryBuilder.should(queryStringQueryBuilder);
                }
            } else {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);;
                if (field.endsWith("Date")) {
                    rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
                }
                if (condition.equals(">")) {
                    rangeQueryBuilder.gt(value);
                } else if (condition.equals(">=")) {
                    rangeQueryBuilder.gte(value);
                } else if (condition.equals("<=")) {
                    rangeQueryBuilder.lte(value);
                } else if (condition.equals("<")) {
                    rangeQueryBuilder.lt(value);
                }
                if ("and".equals(andOr)) {
                    boolQueryBuilder.must(rangeQueryBuilder);
                } else if ("or".equals(andOr)) {
                    boolQueryBuilder.should(rangeQueryBuilder);
                }
            }
        }
        return boolQueryBuilder;
    }

}
