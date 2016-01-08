package com.yihu.ehr;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.boot.SpringApplication;

import java.net.URLDecoder;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 17:10
 */
public class App {
    public static void main(String[] args) throws Exception {
        /*ServiceClientFactory clientFactory = new ServiceClientFactory();
        clientFactory.setHost("localhost");
        clientFactory.setPort(6011);

        DataSetManagerService.Client client = (DataSetManagerService.Client)clientFactory.create("DataSetManagerService", DataSetManagerService.Iface.class);
        int result = client.add(12, 11);

        System.out.println(result);*/

        TTransport transport;
        try {
            transport = new TSocket("localhost", 6011);
            TProtocol protocol = new TBinaryProtocol(transport);
            DataSetManagerService.Client client = new DataSetManagerService.Client(protocol);
            transport.open();
            System.out.println("result: " + client.add(20, 12));
            transport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
