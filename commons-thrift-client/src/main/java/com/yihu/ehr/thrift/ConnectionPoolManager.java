package com.yihu.ehr.thrift;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.yihu.ehr.lang.TServiceProvider;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接池管理器。使用Eureka客户端获取应用及实例配置信息。当前未集成Ribbon等负载均衡功能。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 17:17
 */
@Component
class ConnectionPoolManager {
    @Autowired
    private EurekaClient discoveryClient;

    Map<String, XConnectionProvider> connectionProviderMap;

    public ConnectionPoolManager() {
        connectionProviderMap = new HashMap<String, XConnectionProvider>();
    }

    public synchronized TTransport getTransport(String serviceName, PooledTTransportFactory.TransportType type) throws InvocationTargetException, NoSuchMethodException, TException, InstantiationException, IllegalAccessException {
        XConnectionProvider connectionProvider = connectionProviderMap.get(serviceName);
        if (connectionProvider == null) {
            cacheService();
        }

        connectionProvider = connectionProviderMap.get(serviceName);
        return connectionProvider.getConnection(type);
    }

    /**
     * 执行服务缓存，方便快速创建应用。
     */
    // TODO: 连接池待优化，例如：多个相同主机的，是否需要放在一块？
    // TODO: Eureka服务器更新后，客户端缓存刷新机制，例如：服务何时失效
    // TODO: 服务有多个实例如何处理？使用负载均衡可以处理此问题，但目前没有配置。
    private void cacheService() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, TException {
        Applications applications = discoveryClient.getApplications();
        for (Application application : applications.getRegisteredApplications()) {
            List<InstanceInfo> instanceInfoList = application.getInstances();
            for (InstanceInfo instanceInfo : instanceInfoList) {
                String serviceIP = instanceInfo.getIPAddr();
                int port = instanceInfo.getPort() + 1;
                int connTimeout = 10000;

                PooledTTransportFactory transportFactory = new PooledTTransportFactory(serviceIP, port, connTimeout);
                ConnectionProvider connectionProvider = new ConnectionProvider();
                connectionProvider.init(transportFactory);

                TTransport transport = PooledTTransportFactory.makeTransport(serviceIP, port, connTimeout);
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);

                Constructor<?> clientConstructor = TServiceProvider.Client.class.getConstructor(TProtocol.class);
                TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, TServiceProvider.class.getCanonicalName());
                TServiceProvider.Client client = (TServiceProvider.Client) clientConstructor.newInstance(multiplexedProtocol);

                List<String> supportedServiceNames = client.getSupportedServiceNames();
                for (String serviceName : supportedServiceNames){
                    connectionProviderMap.put(serviceName, connectionProvider);
                }

                transport.close();
            }
        }
    }
}
