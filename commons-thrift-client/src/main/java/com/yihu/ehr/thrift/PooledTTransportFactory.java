package com.yihu.ehr.thrift;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 16:00
 */
public class PooledTTransportFactory implements PooledObjectFactory<TTransport> {
    private String serviceIP;
    private int servicePort;
    private int timeOut;

    /**
     * @param serviceIP
     * @param servicePort
     * @param timeOut
     */
    public PooledTTransportFactory(String serviceIP, int servicePort, int timeOut) {
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.timeOut = timeOut;
    }

    public PooledObject<TTransport> makeObject() throws Exception {
        try {
            DefaultPooledObject<TTransport> transport = new DefaultPooledObject<TTransport>(
                    new TSocket(this.serviceIP,this.servicePort, this.timeOut));

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

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
