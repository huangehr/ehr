package com.yihu.ehr.thrift;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接池管理器。使用Eureka客户端获取应用及实例配置信息。当前未集成Ribbon等负载均衡功能。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.09 17:17
 */
class ConnectionPoolManager {
    @Autowired
    private EurekaClient discoveryClient;

    Map<EurekaInstanceInfo, XConnectionProvider> connectionProviderMap;

    public ConnectionPoolManager(){
        connectionProviderMap = new HashMap<EurekaInstanceInfo, XConnectionProvider>();
    }

    public getServiceConnectionProvider(){

    }

    public Object listInstances(){
        Applications applications = discoveryClient.getApplications();
        for (Application application : applications.getRegisteredApplications()){
            String name = application.getName();
            System.out.println("service: " + name);

            List<InstanceInfo> instanceInfoList = application.getInstances();
            for (InstanceInfo instanceInfo : instanceInfoList){
                EurekaInstanceInfo eurekaInstanceInfo = new EurekaInstanceInfo();

                eurekaInstanceInfo.serviceHost = instanceInfo.getHostName();
                eurekaInstanceInfo.serviceIP = instanceInfo.getIPAddr();
                eurekaInstanceInfo.port = instanceInfo.getPort();


            }
        }

        return null;
    }
}
