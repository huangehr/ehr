package com.yihu.ehr.thrift;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务提供者实现类。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 15:48
 */
class ServiceProviderImpl implements ServiceProvider.Iface {
    Map<String, String> supportedServices = new HashMap<>();

    @Override
    public String getImplementationName(String serviceName) throws TException {
        return supportedServices.get(serviceName);
    }

    @Override
    public boolean supportsService(String serviceName) throws TException {
        return supportedServices.keySet().contains(serviceName);
    }

    @Override
    public List<String> getSupportedServiceNames() throws TException {
        return new ArrayList<>(supportedServices.keySet());
    }

    public void registerService(String serviceName, String implementationName){
        supportedServices.put(serviceName, implementationName);
    }
}
