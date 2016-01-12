package com.yihu.ehr.thrift;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 17:17
 */
public class ConnectionManager {
    Map<String, XConnectionProvider> connectionProviderMap;

    ConnectionManager(){
        connectionProviderMap = new HashMap<String, XConnectionProvider>();
    }
}
