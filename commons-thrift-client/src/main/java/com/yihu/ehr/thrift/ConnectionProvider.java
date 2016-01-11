package com.yihu.ehr.thrift;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.transport.TSocket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Thrift 连接池管理器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 15:49
 */
public class ConnectionProvider implements XConnectionProvider {
    private String serviceIP;   // 服务的IP地址
    private int servicePort;    // 服务的端口
    private int connTimeOut;    // 连接超时配置
    private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;               // 可以从缓存池中分配对象的最大数量
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;                 // 缓存池中最大空闲对象数量
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;                 // 缓存池中最小空闲对象数量
    private long maxWait = GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;         // 阻塞的最大时间

    // 从缓存池中分配对象，是否执行PooledObjectFactory.validateObject方法
    private boolean testOnBorrow = GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW;
    private boolean testOnReturn = GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN;
    private boolean testWhileIdle = GenericObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE;

    private ObjectPool objectPool = null;   // 对象缓存池

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        PooledTTransportFactory pooledTTransportFactory = new PooledTTransportFactory(serviceIP, servicePort, connTimeOut);
        objectPool = new GenericObjectPool(pooledTTransportFactory);

        ((GenericObjectPool)objectPool).setMaxTotal(maxTotal);
        ((GenericObjectPool)objectPool).setMaxIdle(maxIdle);
        ((GenericObjectPool)objectPool).setMinIdle(minIdle);
        ((GenericObjectPool)objectPool).setMaxWaitMillis(maxWait);
        ((GenericObjectPool)objectPool).setTestOnBorrow(testOnBorrow);
        ((GenericObjectPool)objectPool).setTestOnReturn(testOnReturn);
        ((GenericObjectPool)objectPool).setTestWhileIdle(testWhileIdle);
        ((GenericObjectPool)objectPool).setBlockWhenExhausted(GenericObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED);
    }

    @PreDestroy
    public void destroy() {
        try {
            objectPool.close();
        } catch (Exception e) {
            throw new RuntimeException("error destroy()", e);
        }
    }

    public TSocket getConnection(String serviceName, PooledTTransportFactory.TransportType type) {
        try {
            TSocket socket = (TSocket) objectPool.borrowObject();
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("error getConnection()", e);
        }
    }

    public void returnCon(TSocket socket) {
        try {
            objectPool.returnObject(socket);
        } catch (Exception e) {
            throw new RuntimeException("error returnCon()", e);
        }
    }

    public String getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public int getConnTimeOut() {
        return connTimeOut;
    }

    public void setConnTimeOut(int connTimeOut) {
        this.connTimeOut = connTimeOut;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public ObjectPool getObjectPool() {
        return objectPool;
    }

    public void setObjectPool(ObjectPool objectPool) {
        this.objectPool = objectPool;
    }
}
