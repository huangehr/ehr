package com.yihu.ehr.util.http;

import com.yihu.ehr.util.log.LogService;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Air
 * @version 1.0
 * @created 2015.07.02 15:37
 */
public class HttpsClient {
    public HttpsClient() {

    }

    public Response get(String url) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(HttpsInitialise.getInstance().getSslConnectionSocketFactory())
                .build();
        HttpGet httpget = new HttpGet(url);

        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = httpclient.execute(httpget);
            entity = response.getEntity();

            Response res = new Response(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity));
            return res;
        } catch (IOException ex) {
            LogService.getLogger(HttpsClient.class).error(ex.getMessage());
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException ex) {
                LogService.getLogger(HttpsClient.class).error(ex.getMessage());
            }

            try {
                httpclient.close();
            } catch (IOException ex) {
                LogService.getLogger(HttpsClient.class).error(ex.getMessage());
            }
        }

        return null;
    }

    public Response postForm(String url, List<NameValuePair> formParams) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(HttpsInitialise.getInstance().getSslConnectionSocketFactory())
                .build();

        UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(postEntity);

        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = httpclient.execute(httpPost);
            entity = response.getEntity();

            Response res = new Response(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity));
            return res;

        } catch (IOException ex) {
            LogService.getLogger(HttpsClient.class).error(ex.getMessage());
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException ex) {
                LogService.getLogger(HttpsClient.class).error(ex.getMessage());
            }

            try {
                httpclient.close();
            } catch (IOException ex) {
                LogService.getLogger(HttpsClient.class).error(ex.getMessage());
            }
        }

        return null;
    }

    public Response postFile(String url, String filePath) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(HttpsInitialise.getInstance().getSslConnectionSocketFactory())
                .build();

        File file = new File(filePath);
        FileEntity fileEntity = new FileEntity(file,
                ContentType.create("text/plain", "UTF-8"));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(fileEntity);

        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = httpclient.execute(httpPost);
            entity = response.getEntity();

            Response res = new Response(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity));
            return res;

        } catch (IOException ex) {
            LogService.getLogger(HttpsClient.class).error(ex.getMessage());
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException ex) {
                LogService.getLogger(HttpsClient.class).error(ex.getMessage());
            }

            try {
                httpclient.close();
            } catch (IOException ex) {
                LogService.getLogger(HttpsClient.class).error(ex.getMessage());
            }
        }

        return null;
    }
}
