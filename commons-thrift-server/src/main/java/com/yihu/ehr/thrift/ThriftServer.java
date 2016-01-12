package com.yihu.ehr.thrift;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Thrift 服务器，实现阻塞与非阻塞两种模式。若一个应用中要同时支持两种模式的服务，
 * 可以声明不同模式的Thrift 服务器对象，并同时运行。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 11:30
 */
public class ThriftServer {
    /**
     * 服务模型：阻塞与非阻塞
     */
    public enum ServerMode {
        BlockingMode,
        NonBlockingMode
    }

    private Integer port;
    private Integer priority = 1;
    private ServerMode serverMode = ServerMode.BlockingMode;

    private ServerThread serverThread;

    public ThriftServer() {
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setServerMode(ServerMode serverMode) {
        this.serverMode = serverMode;
    }

    /**
     * 启动一个含有多个服务的Thrift服务器。ServiceProvider 服务会被默认添加进去。
     *
     * @throws TTransportException
     */
    public void startServer(List<Object> services, ServerMode serverMode) throws TTransportException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, IllegalClassFormatException, ClassNotFoundException {
        serverThread = new ServerThread(serverMode, port);

        ServiceProviderImpl serviceProvider = new ServiceProviderImpl();
        services.add(serviceProvider);

        for (Object service : services) {
            TProcessor processor = loadServiceProcessor(service);

            // 在 ServiceProvider 中注册服务端所支持的服务
            if (service == serviceProvider) {
                serverThread.registerProcessor(ServiceProvider.class.getCanonicalName(), processor);
                serviceProvider.registerService(ServiceProvider.class.getCanonicalName(), service.getClass().getCanonicalName());
            } else {
                serverThread.registerProcessor(service.getClass().getCanonicalName(), processor);
                serviceProvider.registerService(service.getClass().getCanonicalName(), service.getClass().getCanonicalName());
            }
        }

        serverThread.start();
    }

    /**
     * 从Thrift服务中加载此实现的处理器，用于在服务器中注册。
     *
     * @param service 需要加载的处理器。
     * @return
     * @throws IllegalClassFormatException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private TProcessor loadServiceProcessor(Object service) throws IllegalClassFormatException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        // 常规检测
        Class serviceClass = service.getClass();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = serviceClass.getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalClassFormatException(serviceClass.getCanonicalName() + "服务未实现任何接口");
        }

        TProcessor processor = null;
        for (Class inf : interfaces) {
            String interfaceName = inf.getSimpleName();
            if (!interfaceName.equals("Iface")) continue;

            String processClsName = inf.getEnclosingClass().getName() + "$Processor";
            Class processorCls = classLoader.loadClass(processClsName);

            // 构造Processor的时候，使用服务实现对象作为构造参数
            Constructor constructor = processorCls.getConstructor(inf);
            processor = (TProcessor) constructor.newInstance(service);

            break;
        }

        if (processor == null) {
            throw new IllegalClassFormatException("Thrift服务必须实现Iface接口");
        }

        return processor;
    }

    /**
     * Thrift服务线程。
     */
    class ServerThread extends Thread {
        TMultiplexedProcessor multiplexedProcessor;
        TServer server;

        ServerThread(ServerMode serverMode, int port) throws TTransportException {
            multiplexedProcessor = new TMultiplexedProcessor();

            switch (serverMode) {
                case BlockingMode: {
                    TThreadPoolServer.Args args = new TThreadPoolServer.Args(new TServerSocket(port));
                    args.processor(multiplexedProcessor);
                    args.protocolFactory(new TBinaryProtocol.Factory(true, true));

                    server = new TThreadPoolServer(args);
                }
                break;

                case NonBlockingMode: {
                    TNonblockingServer.Args args = new TNonblockingServer.Args(new TNonblockingServerSocket(port));
                    args.processor(multiplexedProcessor);
                    args.protocolFactory(new TBinaryProtocol.Factory(true, true));

                    server = new TNonblockingServer(args);
                }

                default:
                    server = null;
                    break;
            }
        }

        /**
         * 启动Thrift服务线程。
         */
        @Override
        public void run() {
            try {
                server.serve();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        /**
         * 注册Thrift服务处理器，处理器中已包含指定的服务实现。
         *
         * @param processor
         * @throws TTransportException
         */
        public void registerProcessor(String serviceName, TProcessor processor) throws TTransportException {
            multiplexedProcessor.registerProcessor(serviceName, processor);
        }

        /**
         * 结束Thrift服务线程。
         */
        public void stopServer() {
            server.stop();
        }
    }

    /**
     * 停止Thrift服务。
     */
    public void close() {
        serverThread.stopServer();
    }
}