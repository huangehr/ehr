package com.yihu.ehr.util.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author Air
 * @version 1.0
 * @created 2015.07.02 15:48
 */
public class HopHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
//        if("localhost".equals(hostname)){
//            return true;
//        } else {
//            return false;
//        }

        return true;
    }
}
