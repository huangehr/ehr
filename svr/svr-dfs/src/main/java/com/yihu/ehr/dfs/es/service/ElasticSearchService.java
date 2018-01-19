package com.yihu.ehr.dfs.es.service;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/1/4.
 */
@Service
public class ElasticSearchService {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public void mapping(String index, String type, Map<String, Map<String, String>> source) throws IOException {
        elasticSearchUtil.mapping(index, type, source);
    }

    public void remove(String index){
        elasticSearchUtil.remove(index);
    }

    public Map<String, Object> index(String index, String type, Map<String, Object> source) throws ParseException {
        return elasticSearchUtil.index(index, type, source);
    }

    public void delete(String index, String type, String [] idArr) {
        elasticSearchUtil.delete(index, type, idArr);
    }

    public void deleteByField(String index, String type, String field, Object value) {
        elasticSearchUtil.deleteByField(index, type, field, value);
    }

    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        return elasticSearchUtil.update(index, type, id, source);
    }

    public Map<String, Object> findById(String index, String type, String id) {
        return elasticSearchUtil.findById(index, type, id);
    }

    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        return elasticSearchUtil.findByField(index, type, field, value);
    }

    public List<Map<String, Object>> page(String index, String type, List<Map<String, Object>> filter, int page, int size) {
        return elasticSearchUtil.page(index, type, filter, page, size);
    }

    public long count(String index, String type, List<Map<String, Object>> filter) {
        return elasticSearchUtil.count(index, type, filter);
    }

}
