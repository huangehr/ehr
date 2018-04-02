package com.yihu.ehr.analysis.listener.save.impl;

import com.yihu.ehr.analysis.listener.save.LogSaver;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by chenweida on 2018/1/11.
 */
@Component
public class MongoLogSaver implements LogSaver {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(Object data, String tableName) throws IOException {
        mongoTemplate.insert( data, tableName);
    }
}
