package com.yihu.ehr.std;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

import javax.annotation.PostConstruct;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 11:30
 */
public class ThriftServiceServerFactory {

    private Integer port;

    private Integer priority = 1;// default

    private Object service;// serice实现类

    private ThriftServerIpTransfer ipTransfer;

    private ThriftServerAddressReporter addressReporter;

    private ServerThread serverThread;

    private String configPath;

    public ThriftServiceServerFactory() {
        this.service = null;
        this.ipTransfer = ipTransfer;
        this.addressReporter = addressReporter;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        if (ipTransfer == null) {
            ipTransfer = new LocalNetworkIpTransfer();
        }

        String ip = ipTransfer.getIp();
        if (ip == null) {
            throw new NullPointerException("cant find server ip...");
        }
        String hostname = ip + ":" + port + ":" + priority;
        Class serviceClass = service.getClass();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = serviceClass.getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalClassFormatException("service-class should implements Iface");
        }

        // reflect,load "Processor";
        TProcessor processor = null;
        for (Class clazz : interfaces) {
            String cname = clazz.getSimpleName();
            if (!cname.equals("Iface")) {
                continue;
            }
            String pname = clazz.getEnclosingClass().getName() + "$Processor";
            try {
                Class pclass = classLoader.loadClass(pname);
                if (!pclass.isAssignableFrom(TProcessor.class)) {
                    continue;
                }
                Constructor constructor = pclass.getConstructor(clazz);
                processor = (TProcessor) constructor.newInstance(service);
                break;
            } catch (Exception e) {
                //
            }
        }

        if (processor == null) {
            throw new IllegalClassFormatException("service-class should implements Iface");
        }

        //需要单独的线程,因为serve方法是阻塞的.
        serverThread = new ServerThread(processor, port);
        serverThread.start();

        // report
        if (addressReporter != null) {
            addressReporter.report(configPath, hostname);
        }
    }

    class ServerThread extends Thread {
        private TServer server;

        ServerThread(TProcessor processor, int port) throws Exception {
            TServerSocket serverTransport = new TServerSocket(port);
            TBinaryProtocol.Factory portFactory = new TBinaryProtocol.Factory(true, true);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.processor(processor);
            args.protocolFactory(portFactory);
            server = new TThreadPoolServer(args);
        }

        @Override
        public void run() {
            try {
                server.serve();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public void stopServer() {
            server.stop();
        }
    }

    public void close() {
        serverThread.stopServer();
    }
}