package com.yihu.ehr.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Thrift 客户端构造器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 11:30
 */
@Component
public class ClientFactory {
    @Autowired
    ConnectionPoolManager connectionPoolManager = new ConnectionPoolManager();

    public <T> Object getService(String serviceName, Class<?> serviceInterface, PooledTTransportFactory.TransportType type) {
        try{
            TProtocol protocol = createProtocol(connectionPoolManager.getTransport(serviceName, type));

            Class<?> clientClass = Class.forName(serviceInterface.getName().replace("$Iface", "$Client"));
            Constructor<?> clientConstructor = clientClass.getConstructor(TProtocol.class);

            TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, serviceName);
            return (T) clientConstructor.newInstance(multiplexedProtocol);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

        return null;
    }

    TProtocol createProtocol(TTransport transport) {
        TProtocol protocol = new TBinaryProtocol(transport);

        return protocol;
    }
}
