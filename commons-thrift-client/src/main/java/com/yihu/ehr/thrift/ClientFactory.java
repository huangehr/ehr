package com.yihu.ehr.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Constructor;

/**
 * Thrift 客户端构造器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 11:30
 */
public class ClientFactory {
    ConnectionManager connectionManager = new ConnectionManager();

    ////
    public enum TransportType {
        Simple,
        SSL
    }

    String host;
    int port;

    int sslPort;
    String keyStore;
    String keyPass;
    String keyManagerType;
    String keyStoreType;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    public void setKeyManagerType(String keyManagerType) {
        this.keyManagerType = keyManagerType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public Object getService(String serviceName, Class<?> serviceInterface, TransportType type) throws Exception {
        TProtocol protocol = createProtocol(createTransport(type));

        Class<?> clientClass = Class.forName(serviceInterface.getName().replace("$Iface", "$Client"));
        Constructor<?> clientConstructor = clientClass.getConstructor(TProtocol.class);

        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, serviceName);
        return clientConstructor.newInstance(multiplexedProtocol);
    }

    TTransport createTransport(TransportType transportType) throws TTransportException {
        TTransport transport;
        if (transportType == TransportType.Simple) {
            transport = new TSocket(host, port);
            transport.open();
        } else if (transportType == TransportType.SSL) {
            /*
             * Similar to the server, you can use the parameters to setup client parameters or
             * use the default settings. On the client side, you will need a TrustStore which
             * contains the trusted certificate along with the public key.
             * For this example it's a self-signed cert.
             */
            TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            params.setTrustStore(keyStore, keyPass, keyManagerType, keyStoreType);

            /*
             * Get a client transport instead of a server transport. The connection is opened on
             * invocation of the factory method, no need to specifically call open()
             */
            transport = TSSLTransportFactory.getClientSocket(host, sslPort, 0, params);
        } else {
            transport = null;
        }

        return transport;
    }

    TProtocol createProtocol(TTransport transport) {
        TProtocol protocol = new TBinaryProtocol(transport);

        return protocol;
    }
}
