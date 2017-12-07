package com.yihu.ehr.dfs.es.service;

import com.yihu.ehr.dfs.es.dao.ElasticSearchDao;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Service - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@Service
public class ElasticSearchService {

    @Autowired
    private ElasticSearchDao elasticSearchDao;

    public void create(String index, String type, List<Map<String, String>> source) throws IOException{
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        for(Map<String , String> map : source) {
            String field = map.get("field");
            String fType = map.get("type");
            xContentBuilder.startObject(field);
            if(fType.equals("date")){
                xContentBuilder.field("type", fType);
                xContentBuilder.field("format", "yyyy-MM-dd HH:mm:ss");
            }else {
                xContentBuilder.field("type", fType);
            }
            xContentBuilder.endObject();
        }
        xContentBuilder.endObject().endObject();
        elasticSearchDao.create(index, type, xContentBuilder);
    }

    public void remove(String index){
        elasticSearchDao.remove(index);
    }

    public Map<String, Object> index(String index, String type, Map<String, Object> source) throws ParseException{
        return elasticSearchDao.index(index, type, source);
    }

    public void delete(String index, String type, String [] idArr) {
        elasticSearchDao.delete(index, type, idArr);
    }

    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        if(source.containsKey("_id")) {
            source.remove("_id");
        }
        return elasticSearchDao.update(index, type, id, source);
    }

    public Map<String, Object> findById(String index, String type, String id) {
        return  elasticSearchDao.findById(index, type, id);
    }

    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(field + ":" + value);
        boolQueryBuilder.must(queryStringQueryBuilder);
        return elasticSearchDao.findByField(index, type, boolQueryBuilder);
    }

    public List<Map<String, Object>> page(String index, String type, List<Map<String, String>> filter, int page, int size) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(Map<String, String> param : filter) {
            String andOr = param.get("andOr");
            String condition = param.get("condition");
            String field = param.get("field");
            String value = param.get("value");
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
                    rangeQueryBuilder.lt(value);
                }else if(condition.equals("<")) {
                    rangeQueryBuilder.lte(value);
                }
                if("and".equals(andOr)) {
                    boolQueryBuilder.must(rangeQueryBuilder);
                }else if("or".equals(andOr)) {
                    boolQueryBuilder.should(rangeQueryBuilder);
                }
            }
        }
        return elasticSearchDao.page(index, type, boolQueryBuilder, page, size);
    }
}
