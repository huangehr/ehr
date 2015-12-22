package com.yihu.ehr.util.http;

import com.yihu.ehr.util.encrypt.MD5;
import com.yihu.ehr.util.file.FileUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Air
 * @version 1.0
 * @created 2015.07.08 14:12
 */
public class HOPClient {
    private final String version = "1.0";
    private final String signMethod = "md5";
    private String method;
    private String timestamp;
    private String format;
    private String appKey="hos";
    private String accessToken;
    private String secretKey = "hos";
    private String url;
    private TreeMap<String, String> paramMap;
    private HttpsClient httpsClient;

    public HOPClient() {
        paramMap = new TreeMap<>();
        httpsClient = new HttpsClient();
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setParam(String key, String value) {
        paramMap.put(key, value);
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Response get() {
        String completeUrl = completeUrl();
        if (completeUrl == null){
            return null;
        }

        return httpsClient.get(completeUrl);
    }

    public boolean downLoad(String path) {
        String completeUrl = completeUrl();
        if (completeUrl == null){
            return false;
        }

        Response response = httpsClient.get(completeUrl);
        if (response.statusCode != 200) {
            return false;
        }

        try {
            boolean result = FileUtil.writeFile(path, response.body, "UTF-8");
            if (!result) {
                LogService.getLogger(HOPClient.class).error("下载文件失败.");
            }

            return result;
        } catch (IOException e) {
            LogService.getLogger(HOPClient.class).error("下载文件失败.");
        }
        return false;
    }


    public Response postForm(List<NameValuePair> formParams) {
        String completeUrl = completeUrl();
        return httpsClient.postForm(completeUrl, formParams);
    }

    public Response postFile(String filePath) {
        String completeUrl = completeUrl();
        return httpsClient.postFile(completeUrl, filePath);
    }

    private String completeUrl() {
        try {
            addParam("method", method, true);
            addParam("format", format, false);
            addParam("app_key", appKey, true);
            addParam("timestamp", getTimestamp(), true);
            addParam("v", version, true);
            addParam("sign_method", signMethod, true);
            addParam("access_token", accessToken, false);
            addParam("sign", signParam(), true);

            return url + genParam();
        } catch (Exception e) {
            LogService.getLogger(HOPClient.class).error(e.getMessage());
        }

        return null;
    }

    private void addParam(String paramName, String paramValue, boolean bMust) throws Exception {
        if (StringUtils.isEmpty(paramValue)) {
            if (bMust) {
                throw new Exception(paramName + "参数不能为空.");
            } else {
                return;
            }
        }

        String encodeValue = URLEncoder.encode(paramValue, "UTF-8");
        paramMap.put(paramName, encodeValue);
    }

    private String signParam() {
        Iterator<Map.Entry<String, String>> iterator = paramMap.entrySet().iterator();

        StringBuilder builder = new StringBuilder(secretKey);
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            builder.append(key);
            builder.append(value);
        }

        try {
            return MD5.hash(builder.toString());
        } catch (Exception e) {
            LogService.getLogger(HOPClient.class).error(e.getMessage());
        }

        return null;
    }

    private String genParam() {
        Iterator<Map.Entry<String, String>> iterator = paramMap.entrySet().iterator();

        StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            if (builder.length() == 0) {
                builder.append("?");
            } else {
                builder.append("&");
            }
            builder.append(key);
            builder.append("=");
            builder.append(value);
        }

        return builder.toString();
    }

    private String getTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = format.format(new Date());

        return timestamp;
    }
}
