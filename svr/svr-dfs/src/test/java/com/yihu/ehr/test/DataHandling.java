package com.yihu.ehr.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.http.HttpResponse;
import com.yihu.ehr.util.http.HttpUtils;
import com.yihu.ehr.util.rest.Envelop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by progr1mmer on 2018/8/3.
 */
public class DataHandling {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataHandling.class);

    private static final int size = 200;
    private static final int thread = 10;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String [] args) {
        start();
    }

    public static void start() {
        Envelop envelop = getEsData(1, size);
        if (null == envelop) {
            throw new RuntimeException("获取总数据量失败");
        }
        int totalPage = envelop.getTotalPage();
        int singleDeal = totalPage / thread;
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        for (int i = 1; i <= thread; i ++) {
            final int start = ((i - 1) * singleDeal) + 1;
            final int end = i * singleDeal;
            executorService.execute(() -> hand(start, end));
        }
    }

    public static void hand(int start, int end) {
        for (int i = start; i <= end; i ++) {
            Envelop envelop = getEsData(i, size);
            if (envelop != null && envelop.isSuccessFlg()) {
                List<Map<String, Object>> dataList = envelop.getDetailModelList();
                for (Map<String, Object> data : dataList) {
                    File file = null;
                    try {
                        Map<String, Object> sourceMap = new HashMap<>();
                        String filePath = data.get("remote_path").toString().replaceAll(":", "/");
                        /*file = HttpUtils.download("http://jksr.srswjw.gov.cn:1235/gateway/file/" + filePath,
                            System.getProperty("java.io.tmpdir") + java.io.File.separator + filePath.substring(filePath.lastIndexOf("/") + 1),
                            null, null);*/
                        Map<String, String> header = new HashMap<>();
                        header.put("token", "ae416ecb-c944-4e01-a8fa-98f1d7c9535f");
                        String _id = data.get("_id").toString();
                        file = HttpUtils.download("http://jksr.srswjw.gov.cn:1235/gateway/pack-mgr/api/v1.0/packages/download/" + _id,
                                System.getProperty("java.io.tmpdir") + File.separator + filePath.substring(filePath.lastIndexOf("/") + 1),
                                null, header);
                        String remotePath = upload(file);
                        if (remotePath != null) {
                            sourceMap.put("remote_path", remotePath);
                            sourceMap.put("pwd", data.get("pwd"));
                            sourceMap.put("receive_date", data.get("receive_date"));
                            sourceMap.put("org_code", data.get("org_code"));
                            sourceMap.put("org_name", data.get("org_name"));
                            sourceMap.put("org_area", data.get("org_area"));
                            sourceMap.put("client_id", data.get("client_id"));
                            sourceMap.put("resourced", 0);
                            sourceMap.put("md5_value", data.get("md5_value"));
                            sourceMap.put("archive_status", 0);
                            sourceMap.put("fail_count", 0);
                            sourceMap.put("analyze_status", 0);
                            sourceMap.put("analyze_fail_count", 0);
                            sourceMap.put("pack_type", data.get("pack_type"));
                            Map<String, Object> params = new HashMap<>();
                            params.put("index", "json_archives");
                            params.put("type", "info");
                            params.put("source", objectMapper.writeValueAsString(sourceMap));
                            HttpResponse httpResponse = HttpUtils.doPost("http://172.17.110.227:10770/api/v1.0/elasticSearch/index", params, null);
                            LOGGER.info(httpResponse.getContent());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (file != null) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public static Envelop getEsData(int page, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("index", "json_archives");
        params.put("type", "info");
        params.put("filter", "event_date>=2018-07-01 00:00:00;event_date<2018-08-01 00:00:00;pack_type=1;archive_status=3");
        params.put("sorts", "+event_date");
        params.put("page", page);
        params.put("size", size);
        Map<String, String> header = new HashMap<>();
        header.put("token", "ae416ecb-c944-4e01-a8fa-98f1d7c9535f");
        try {
            HttpResponse response = HttpUtils.doGet("http://jksr.srswjw.gov.cn:1235/gateway/dfs/api/v1.0/elasticSearch/pageSort", params, header);
            Envelop envelop = objectMapper.readValue(response.getContent(), Envelop.class);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String upload (File file){
        Map<String, Object> params = new HashMap<>();
        params.put("objectId", "profile");
        params.put("creator", "Progr1mmer");
        try {
            HttpResponse httpResponse = HttpUtils.doUpload("http://172.17.110.227:10770/api/v1.0/fastDfs/upload", params, null, "file", file);
            if (httpResponse.isSuccessFlg()) {
                Envelop envelop = objectMapper.readValue(httpResponse.getContent(), Envelop.class);
                Map<String, Object> data = (Map<String, Object>) envelop.getObj();
                return data.get("path").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
