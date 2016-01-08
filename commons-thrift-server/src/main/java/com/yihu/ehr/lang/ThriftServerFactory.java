package com.yihu.ehr.lang;

import org.apache.thrift.TBaseProcessor;
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
public class ThriftServerFactory {

    private Integer port = 6010+1;

    private Integer priority = 1;// default

    private Object service;// service实现类

    private ServerThread serverThread;

    private String configPath;

    public ThriftServerFactory() {
        this.service = null;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
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
                //if (pclass instanceof TBaseProcessor) {
                //    continue;
                //}
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