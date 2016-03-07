//package com.yihu.ehr.pack.thrift;
//
//import com.yihu.ehr.data.domain.TPageable;
//import com.yihu.ehr.pack.TJsonPackage;
//import com.yihu.ehr.pack.TJsonPackageService;
//import org.apache.thrift.TException;
//
//import java.nio.ByteBuffer;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Sand
// * @version 1.0
// * @created 2016.01.14 11:23
// */
//public class PackageService implements TJsonPackageService.Iface {
//
//    @Override
//    public boolean receive(ByteBuffer data, String pwd) throws TException {
//        return false;
//    }
//
//    @Override
//    public int getPackageCount(int since, int to) throws TException {
//        return 0;
//    }
//
//    @Override
//    public List<TJsonPackage> search(Map<String, String> criteria, TPageable pageable) throws TException {
//        return null;
//    }
//
//    @Override
//    public TJsonPackage getPackage(String id) throws TException {
//        return null;
//    }
//
//    @Override
//    public List<TJsonPackage> getUnresolvedPackages(int count) throws TException {
//        return null;
//    }
//}
