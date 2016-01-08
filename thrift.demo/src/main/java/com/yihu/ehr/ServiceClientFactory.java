package com.yihu.ehr;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;

import java.lang.reflect.Constructor;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.04 14:29
 */
public class ServiceClientFactory {
    public enum TransportType{
        Simple,
        SSL
    }

    String host = "localhost";
    int port = 6011;
    int sslPort = 9090;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSslPort(int sslPort){
        this.sslPort = sslPort;
    }

    Object create(String serviceName, Class<?> serviceInterface) throws Exception {
        TTransport transport = createTransport(TransportType.Simple);
        TProtocol protocol = createProtocol(transport);

        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, serviceName);

        Class<?> clientClass = Class.forName(serviceInterface.getName().replace("$Iface", "$Client"));
        Constructor<?> clientConstructor = clientClass.getConstructor(TProtocol.class);

        return clientConstructor.newInstance(multiplexedProtocol);
    }

    public static <T> T getService(String serviceName, Class<?> serviceInterface) {
        return null;
    }

    TProtocol createProtocol(TTransport transport) {
        TProtocol protocol = new TBinaryProtocol(transport);

        return protocol;
    }

    TTransport createTransport(TransportType transportType) throws TTransportException {
        TTransport transport;
        if (transportType == TransportType.Simple) {
            transport = new TSocket(host, port);
            transport.open();
        } else if (transportType == TransportType.SSL){
                /*
                 * Similar to the server, you can use the parameters to setup client parameters or
                 * use the default settings. On the client side, you will need a TrustStore which
                 * contains the trusted certificate along with the public key.
                 * For this example it's a self-signed cert.
                 */
            TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            params.setTrustStore("../../lib/java/test/.truststore", "thrift", "SunX509", "JKS");

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
}
