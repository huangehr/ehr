package com.yihu.ehr.analysis.listener.save;

import java.io.IOException;

/**
 * Created by chenweida on 2018/1/11.
 */
public interface LogSaver {
    public void save(Object data, String tableName) throws IOException;
}
