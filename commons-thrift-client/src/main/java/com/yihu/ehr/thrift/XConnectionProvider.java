package com.yihu.ehr.thrift;

import org.apache.thrift.transport.TSocket;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 15:50
 */
public interface XConnectionProvider {
    /**
     * 取链接池中的一个链接
     *
     * @return
     */
    public TSocket getConnection();

    /**
     * 返还链接
     *
     * @param socket
     */
    public void returnCon(TSocket socket);
}
