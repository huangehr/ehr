package com.yihu.ehr.analysis.listener.save.impl;

import com.yihu.ehr.analysis.config.es.ElasticFactory;
import com.yihu.ehr.analysis.listener.save.LogSaver;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by chenweida on 2018/1/11.
 */
@Component
public class ESLogSaver implements LogSaver {
    @Autowired
    private ElasticFactory elasticFactory;

    public void save(Object data, String tableName) throws IOException {
        Index index = new Index.Builder(data).index(tableName).type(tableName).build();
        JestResult jestResult = elasticFactory.getJestClient().execute(index);
        System.out.println(jestResult.isSucceeded());
        // mongoTemplate.insert( data, tableName);
    }
}
