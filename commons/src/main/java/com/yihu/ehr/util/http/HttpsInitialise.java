package com.yihu.ehr.util.http;

import com.yihu.ehr.util.log.LogService;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;

/**
 * Https相关环境初始化
 *
 * @author Air
 * @version 1.0
 * @created 2015.07.07 9:21
 */
public class HttpsInitialise {

    private static volatile HttpsInitialise instance = null;
    private SSLContext sslcontext;
    private SSLConnectionSocketFactory sslConnectionSocketFactory;

    private HttpsInitialise() {
    }

    public void finalize() throws Throwable {
    }

    public static HttpsInitialise getInstance() {
        if (instance == null) {
            synchronized (HttpsInitialise.class) {
                if (instance == null) {
                    try {
                        instance = new HttpsInitialise();
                    } catch (Exception ex) {
                        LogService.getLogger(HttpsClient.class).error(ex.getMessage());
                    }
                }
            }
        }

        return instance;
    }

    /**
     * 初始化SSLConnectionSocketFactory.
     *
     * @param password       密码
     * @param trustStorePath 信任库路径
     * @throws Exception
     */
    public void  init(String trustStorePath, String password) throws Exception {
        sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new File(trustStorePath), password.toCharArray(),
                        new TrustSelfSignedStrategy())
                .build();

        // Allow TLSv1 protocol only
        sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                new HopHostnameVerifier());
    }

    public SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        return sslConnectionSocketFactory;
    }

}
