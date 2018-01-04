package com.yihu.ehr.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
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

    public void mapping(String index, String type, Map<String, Map<String, String>> source) throws IOException{
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        for(String field : source.keySet()) {
            xContentBuilder.startObject(field);
            Map<String, String> propsMap = source.get(field);
            for(String prop : propsMap.keySet()) {
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
        if(source.containsKey("_id")) {
            source.remove("_id");
        }
        return elasticSearchClient.update(index, type, id, source);
    }

    public Map<String, Object> findById(String index, String type, String id) {
        return elasticSearchClient.findById(index, type, id);
    }

    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
        boolQueryBuilder.must(matchQueryBuilder);
        return elasticSearchClient.findByField(index, type, boolQueryBuilder);
    }

    public List<Map<String, Object>> page(String index, String type, List<Map<String, Object>> filter, int page, int size) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filter);
        return elasticSearchClient.page(index, type, boolQueryBuilder, page, size);
    }

    public long count(String index, String type, List<Map<String, Object>> filter) {
        QueryBuilder boolQueryBuilder = getQueryBuilder(filter);
        return elasticSearchClient.count(index, type, boolQueryBuilder);
    }

    private QueryBuilder getQueryBuilder(List<Map<String, Object>> filter) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(Map<String, Object> param : filter) {
            String andOr = String.valueOf(param.get("andOr"));
            String condition = String.valueOf(param.get("condition"));
            String field = String.valueOf(param.get("field"));
            Object value = param.get("value");
            if(condition.equals("=")) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
                if("and".equals(andOr)) {
                    boolQueryBuilder.must(matchQueryBuilder);
                }else if("or".equals(andOr)) {
                    boolQueryBuilder.should(matchQueryBuilder);
                }
            }else if (condition.equals("?")) {
                QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(field + ":" + value);
                if("and".equals(andOr)) {
                    boolQueryBuilder.must(queryStringQueryBuilder);
                }else if("or".equals(andOr)) {
                    boolQueryBuilder.should(queryStringQueryBuilder);
                }
            }else {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);;
                if(field.endsWith("Date")) {
                    rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
                }
                if(condition.equals(">")) {
                    rangeQueryBuilder.gt(value);
                }else if(condition.equals(">=")) {
                    rangeQueryBuilder.gte(value);
                }else if(condition.equals("<=")) {
                    rangeQueryBuilder.lte(value);
                }else if(condition.equals("<")) {
                    rangeQueryBuilder.lt(value);
                }
                if("and".equals(andOr)) {
                    boolQueryBuilder.must(rangeQueryBuilder);
                }else if("or".equals(andOr)) {
                    boolQueryBuilder.should(rangeQueryBuilder);
                }
            }
        }
        return boolQueryBuilder;
    }

}
