package com.yihu.ehr.pack.thrift;

import com.yihu.ehr.pack.JsonPackage;
import com.yihu.ehr.pack.JsonPackageManager;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.14 11:23
 */
public class PackageService implements JsonPackageManager.Iface {
    @Override
    public boolean receive(ByteBuffer byteBuffer, String s) throws TException {

        return false;
    }

    @Override
    public List<JsonPackage> getArchiveList(String s, String s1) throws TException {
        return null;
    }

    @Override
    public List<JsonPackage> searchArchives(Map<String, String> map) throws TException {
        return null;
    }

    @Override
    public JsonPackage getJsonPackage(String s) throws TException {
        return null;
    }

    @Override
    public int getArchiveCount(String s, String s1) throws TException {
        return 0;
    }

    @Override
    public JsonPackage acquireArchive() throws TException {
        return null;
    }

    @Override
    public void reportArchiveFinished(String s, String s1) throws TException {

    }

    @Override
    public void reportArchiveFailed(String s, String s1) throws TException {

    }
}
