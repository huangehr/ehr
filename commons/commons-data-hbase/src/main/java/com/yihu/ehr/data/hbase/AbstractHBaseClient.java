package com.yihu.ehr.data.hbase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

/**
 * @author Sand
 * @created 2016.04.27 16:31
 */
public class AbstractHBaseClient {
    @Autowired
    protected HbaseTemplate hbaseTemplate;
}
