package com.yihu.ehr.dfs.es.service;

import com.yihu.ehr.dfs.es.dao.ElasticSearchDao;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2017/12/2.
 */
@Service
public class ElasticSearchService {

    @Autowired
    private ElasticSearchDao elasticSearchDao;

    public void create(String index, List<Map<String, String>> source) throws Exception{
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        for(Map<String , String> map : source ) {
            String field = map.get("field");
            String type = map.get("type");
            xContentBuilder.startObject(field);
            if(type.equals("date")){
                xContentBuilder.field("type", type);
                xContentBuilder.field("format", "yyyy-MM-dd HH:mm:ss");
            }else {
                xContentBuilder.field("type", type);
            }
            xContentBuilder.endObject();
        }
        xContentBuilder.endObject().endObject();
        elasticSearchDao.create(index, xContentBuilder);
    }

    public void remove(String index){
        elasticSearchDao.remove(index);
    }

    public Map<String, Object> index(String index, Map<String, Object> source) throws ParseException{
        return elasticSearchDao.index(index, source);
    }

    public void delete(String index, String [] idArr) {
        elasticSearchDao.delete(index, idArr);
    }

    public Map<String, Object> update(String index, String id, Map<String, Object> source) throws DocumentMissingException {
        return elasticSearchDao.update(index, id, source);
    }

    public Map<String, Object> findById(String index, String id) {
        return  elasticSearchDao.findById(index, id);
    }

    public List<Map<String, Object>> findByField(String index, String field, Object value) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(field, value);
        boolQueryBuilder.must(matchQueryBuilder);
        return elasticSearchDao.findByField(index, boolQueryBuilder);
    }

    public List<Map<String, Object>> page(String index, List<Map<String, String>> filter, int page, int size) throws Exception{
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
        return elasticSearchDao.page(index, boolQueryBuilder, page, size);
    }
}
