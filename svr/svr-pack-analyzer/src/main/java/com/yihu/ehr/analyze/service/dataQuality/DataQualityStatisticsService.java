package com.yihu.ehr.analyze.service.dataQuality;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yeshijie on 2018/5/31.
 */
@Service
public class DataQualityStatisticsService extends BaseJpaService {
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    private WarningSettingService warningSettingService;
    @Value("&{quality.orgCode}")
    private String defaultOrgCode;

    /**
     * 统计查询
     * @param start
     * @param end
     * @param eventType 0门诊 1住院 2体检,null全部
     * @throws Exception
     */
    public void dataset(String start,String end,Integer eventType) throws Exception{
        String dateStr = DateUtil.toString(new Date());
        if(StringUtils.isBlank(start)){
            start = dateStr;
        }
        if(StringUtils.isBlank(end)){
            end = dateStr;
        }
        //初始化 数据集数据
        Session session = currentSession();
        Query query = session.createSQLQuery("SELECT org_code,COUNT(*) c from dq_dataset_warning WHERE type = 1 GROUP BY org_code");
        List<Map<String,Object>> datasetList = query.list();
        Map<String, Object> datasetMap = new HashedMap(datasetList.size());
        datasetList.forEach(one->{
            String orgCode = one.get("org_code").toString();
            Integer num = Integer.valueOf(one.get("c").toString());
            datasetMap.put(orgCode,num);
        });

        //统计医院数据
        String sql1 = "SELECT HSI07_01_001,HSI07_01_002,HSI07_01_004,HSI07_01_012,org_code FROM qc/daily_report where receive_date>= '"+start+" 00:00:00' AND '" +  end + " 23:59:59' ";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        Map<String, Map<String, Object>> dataMap = new HashMap<>(resultSet1.getFetchSize());
        try {
            while (resultSet1.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet1.getString("org_code");
                Integer HSI07_01_001 = resultSet1.getInt("HSI07_01_001");//总诊疗=门急诊+出院+体检（入院的不算和js开头的暂时没用）
                Integer HSI07_01_002 = resultSet1.getInt("HSI07_01_002");//门急诊
                Integer HSI07_01_004 = resultSet1.getInt("HSI07_01_004");//体检
                Integer HSI07_01_012 = resultSet1.getInt("HSI07_01_012");//出院
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = new HashMap<>();
                    dataMap1.put("org_code",orgCode);
                }
                switch (eventType){
                    case 0:
                        dataMap1.put("hospitalArchives",HSI07_01_002);
                        break;
                    case 1:
                        dataMap1.put("hospitalArchives",HSI07_01_012);
                        break;
                    case 2:
                        dataMap1.put("hospitalArchives",HSI07_01_004);
                        break;
                    default:
                        dataMap1.put("hospitalArchives",HSI07_01_001);
                        break;
                }
                if(datasetMap.containsKey(orgCode)){
                    dataMap1.put("hospitalDataset",datasetMap.get(orgCode));
                }else {
                    dataMap1.put("hospitalDataset",datasetMap.get(defaultOrgCode));
                }
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //统计接收数据
        String sql2 = "SELECT count(*) c,org_code FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND '" +  end + " 23:59:59' ";
        if(eventType!=null){
            sql2 += " and event_type = "+eventType ;
        }
        sql2 += " group by org_code";
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        try {
            while (resultSet1.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet2.getString("org_code");
                Integer total = resultSet2.getInt("c");//接收 档案数
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = new HashMap<>();
                    dataMap1.put("org_code",orgCode);
                    if(datasetMap.containsKey(orgCode)){
                        dataMap1.put("hospitalDataset",datasetMap.get(orgCode));
                    }else {
                        dataMap1.put("hospitalDataset",datasetMap.get(defaultOrgCode));
                    }
                }
                dataMap1.put("receiveArchives",total);
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String sql3 = "SELECT count(*) c,org_code FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND '" +  end + " 23:59:59' and analyze_status=2 ";
        if(eventType!=null){
            sql3 += " and event_type = "+eventType ;
        }
        sql3 += " group by org_code";
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        try {
            while (resultSet1.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet3.getString("org_code");
                Integer total = resultSet3.getInt("c");//接收 质量异常
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = new HashMap<>();
                    dataMap1.put("org_code",orgCode);
                }
                dataMap1.put("receiveException",total);
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String sql4 = "SELECT details,org_code FROM json_archives_qc/qc_dataset_info where receive_date>= '"+start+" 00:00:00' AND '" +  end + " 23:59:59' ";
        if(eventType!=null){
            sql4 += " and event_type = "+eventType ;
        }
        sql4 += " group by org_code";
        ResultSet resultSet4 = elasticSearchUtil.findBySql(sql4);
        try {
            Map<String, Map<String, Object>> datasetMap1 = new HashMap<>(resultSet1.getFetchSize());
            while (resultSet1.next()) {
                Map<String, Object> datasetMap2 = null;
                String orgCode = resultSet4.getString("org_code");
                String details = resultSet4.getString("details");//接收 数据集
                if(datasetMap1.containsKey(orgCode)){
                    datasetMap2 = datasetMap1.get(orgCode);
                }else {
                    datasetMap2 = new HashedMap();
                }

                JSONArray jsonArray = JSON.parseArray(details);
                for(int i=0;i<jsonArray.size();i++){
                    String dataset = jsonArray.get(i).toString();
                    if(!datasetMap2.containsKey(dataset)){
                        datasetMap2.put(dataset,dataMap);
                    }
                }
                datasetMap1.put(orgCode,datasetMap2);
            }

            for (Map.Entry<String, Map<String, Object>> entry : datasetMap1.entrySet()) {
                String orgCode = entry.getKey();
                Map<String, Object> dataMap1 = null;
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = new HashMap<>();
                    dataMap1.put("org_code",orgCode);
                    if(datasetMap.containsKey(orgCode)){
                        dataMap1.put("hospitalDataset",datasetMap.get(orgCode));
                    }else {
                        dataMap1.put("hospitalDataset",datasetMap.get(defaultOrgCode));
                    }
                }
                dataMap1.put("receiveDataset",entry.getValue().size());//数据集个数
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        //资源化数据
        String sql5 = "SELECT count(*) c,org_code,archive_status FROM json_archives/info where archive_status=2 or archive_status=3 group by org_code,archive_status";
        ResultSet resultSet5 = elasticSearchUtil.findBySql(sql5);
        try {
            while (resultSet1.next()) {
                String orgCode = resultSet5.getString("org_code");
                Integer archiveStatus = resultSet5.getInt("archive_status");// 2失败，3成功
                Integer total = resultSet5.getInt("c");//资源化 解析成功和失败


            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String sql6 = "SELECT count(*) c,org_code FROM json_archives/info where defect=1 group by org_code";
        ResultSet resultSet6 = elasticSearchUtil.findBySql(sql6);
        try {
            while (resultSet1.next()) {
                String orgCode = resultSet6.getString("org_code");
                Integer total = resultSet6.getInt("c");//资源化 解析异常


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
