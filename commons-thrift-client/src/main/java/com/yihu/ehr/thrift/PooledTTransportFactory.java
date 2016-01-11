package com.yihu.ehr.thrift;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 16:00
 */
public class PooledTTransportFactory implements PooledObjectFactory<TTransport> {
    public enum TransportType {
        Simple,
        SSL
    }

    String serviceIP;
    int servicePort;
    int sslPort;
    int connTimeOut;
    String keyStore;
    String keyPass;
    String keyManagerType;
    String keyStoreType;

    /**
     * @param serviceIP
     * @param servicePort
     * @param connTimeOut
     */
    public PooledTTransportFactory(String serviceIP, int servicePort, int connTimeOut) {
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.connTimeOut = connTimeOut;
    }

    public PooledObject<TTransport> makeObject() throws Exception {
        try {
            DefaultPooledObject<TTransport> transport = new DefaultPooledObject<TTransport>(
                    new TSocket(this.serviceIP,this.servicePort, this.connTimeOut));

            transport.getObject().open();
            return transport;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroyObject(PooledObject<TTransport> transport) throws Exception {
        if (transport instanceof TSocket) {
            TSocket socket = (TSocket) transport;
            if (socket.isOpen()) {
                socket.close();
            }
        }
    }

    public boolean validateObject(PooledObject<TTransport> transport) {
        try {
            if (transport instanceof TSocket) {
                TSocket thriftSocket = (TSocket) transport;
                if (thriftSocket.isOpen()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void passivateObject(PooledObject<TTransport> transport) throws Exception {
    }

    public void activateObject(PooledObject<TTransport> transport) throws Exception {
    }

    TTransport createTransport(TransportType transportType) throws TTransportException {
        TTransport transport;
        if (transportType == TransportType.Simple) {
            transport = new TSocket(serviceIP, servicePort);
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
            transport = TSSLTransportFactory.getClientSocket(serviceIP, sslPort, 0, params);
        } else {
            transport = null;
        }

        return transport;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public void setConnTimeOut(int connTimeOut) {
        this.connTimeOut = connTimeOut;
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
}
