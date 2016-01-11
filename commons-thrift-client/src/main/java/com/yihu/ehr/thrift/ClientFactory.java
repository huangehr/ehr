package com.yihu.ehr.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Constructor;

/**
 * Thrift 客户端构造器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 11:30
 */
public class ClientFactory {
    ConnectionPoolManager connectionPoolManager = new ConnectionPoolManager();

    public Object getService(String serviceName, Class<?> serviceInterface, PooledTTransportFactory.TransportType type) throws Exception {
        TProtocol protocol = createProtocol(connectionProvider.getConnection(serviceName, type));

        Class<?> clientClass = Class.forName(serviceInterface.getName().replace("$Iface", "$Client"));
        Constructor<?> clientConstructor = clientClass.getConstructor(TProtocol.class);

        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, serviceName);
        return clientConstructor.newInstance(multiplexedProtocol);
    }

    TProtocol createProtocol(TTransport transport) {
        TProtocol protocol = new TBinaryProtocol(transport);

        return protocol;
    }
}
