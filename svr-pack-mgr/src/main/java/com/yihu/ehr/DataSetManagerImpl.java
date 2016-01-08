package com.yihu.ehr;

import org.apache.thrift.TException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.08 16:45
 */
public class DataSetManagerImpl implements DataSetManagerService.Iface {
    @Override
    public int add(int num1, int num2) throws TException {
        return num1 + num2;
    }
}
