package com.yihu.ehr.analyze.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: zhengwei
 * @Date: 2018/7/17 11:47
 * @Description:
 */
@Component
public class QcDataSetDetailJob {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    protected ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 18 * * ?")
    public void dataSetTask() throws Exception {
        String date = redisClient.get("start_date");
        if (StringUtils.isEmpty(date)) {
            date = "2018-07-18";
            redisClient.set("start_date",date);
        }
        if("2018-04-14".equals(date)){
            return;
        }
        List<String> field = new ArrayList<>();
        field.add("org_code");
        String sqlOrg = "SELECT org_code FROM json_archives/info where receive_date>= '"+date+" 00:00:00' AND receive_date<='" +  date + " 23:59:59' group by org_code";
        List<Map<String, Object>> orgList = elasticSearchUtil.findBySql(field,sqlOrg);
        for(Map<String,Object> orgMap : orgList) {
            String orgCode = orgMap.get("org_code")+"";
            List<Map<String, Object>> res = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("qc_step=1;");
            stringBuilder.append("receive_date>=" + date + " 00:00:00;");
            stringBuilder.append("receive_date<" + date + " 23:59:59;");
            stringBuilder.append("org_code=" + orgCode);
            long starttime = System.currentTimeMillis();
            int count = (int) elasticSearchUtil.count("json_archives_qc", "qc_dataset_info", stringBuilder.toString());
            double pageNum = count % 1000 > 0 ? count / 1000 + 1 : count / 1000;
            for (int i = 0; i < pageNum; i++) {
                Page<Map<String, Object>> result = elasticSearchUtil.page("json_archives_qc", "qc_dataset_info", stringBuilder.toString(), i + 1, 1000);
                System.out.println("查询耗时：" + (System.currentTimeMillis() - starttime) + "ms");
                for (Map<String, Object> map : result) {
                    List<Map<String, Object>> dataSets = objectMapper.readValue(map.get("details").toString(), List.class);
                    String eventType = map.get("event_type").toString();
                    for (Map<String, Object> dataSet : dataSets) {
                        for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
                            getDataSets(map.get("version") + "", entry.getKey(), (int) entry.getValue(), res, date, orgCode,eventType);
                        }
                    }
                }
            }
            elasticSearchUtil.bulkIndex("json_archives_qc","qc_dataset_detail",res);
            System.out.println("统计耗时：" + (System.currentTimeMillis() - starttime) + "ms");
        }
        Date beginDate = DateUtil.strToDate(date);
        Date addDate = DateUtil.addDate(-1, beginDate);
        String endDate = DateUtil.toString(addDate);
        redisClient.set("start_date",endDate);
    }

    public void getDataSets(String version, String dataSet, int row, List<Map<String, Object>> res,String date,String orgCode,String eventType){
        boolean flag = true;
        for(Map<String, Object> map : res){
            if(dataSet.equals(map.get("dataset"))&&eventType.equals(map.get("event_type"))){
                flag = false;
                map.put("row", (int)map.get("row") + row);
                map.put("count", (int)map.get("count") + 1);
                break;
            }
        }
        if(flag){
            Map<String, Object> map = new HashMap<>();
            map.put("org_code", orgCode);
            map.put("event_type", eventType);
            map.put("receive_date", date+" 00:00:00");
            map.put("dataset", dataSet);
            map.put("dataset_name", redisClient.get("std_data_set_" + version + ":" + dataSet + ":name"));
            map.put("row", row);
            map.put("count", 1);

            res.add(map);
        }
    }
}
