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
import java.util.*;

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
    @Value("${quality.orgCode}")
    private String defaultOrgCode;

    /**
     * 统计查询
     * @param start
     * @param end
     * @param eventType 0门诊 1住院 2体检,null全部
     * @throws Exception
     */
    public List<Map<String,Object>> dataset(String start,String end,Integer eventType) throws Exception{
        List<Map<String,Object>> re = new ArrayList<>();
        String dateStr = DateUtil.toString(new Date());
        if(StringUtils.isBlank(start)){
            start = dateStr;
        }
        if(StringUtils.isBlank(end)){
            end = dateStr;
        }

        Long startTime = System.currentTimeMillis();

        //初始化 数据集数据
        Session session = currentSession();
        Query query = session.createSQLQuery("SELECT org_code,COUNT(*) c from dq_dataset_warning WHERE type = 1 GROUP BY org_code");
        List<Object[]> datasetList = query.list();
        Map<String, Object> datasetMap = new HashedMap(datasetList.size());
        datasetList.forEach(one->{
            String orgCode = one[0].toString();
            Integer num = Integer.valueOf(one[1].toString());
            datasetMap.put(orgCode,num);
        });

        System.out.println("初始化 数据集数据时间："+(System.currentTimeMillis()-startTime));

        //统计医院数据
        String sql1 = "SELECT sum(HSI07_01_001) s1,sum(HSI07_01_002) s2,sum(HSI07_01_004) s3,sum(HSI07_01_012) s4,org_code FROM qc/daily_report where create_date>= '"+start+"T00:00:00' AND create_date <='" +  end + "T23:59:59' group by org_code";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        Map<String, Map<String, Object>> dataMap = new HashMap<>(resultSet1.getRow());
        try {
            while (resultSet1.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet1.getString("org_code");
                double HSI07_01_001 = resultSet1.getDouble("s1");//总诊疗=门急诊+出院+体检（入院的不算和js开头的暂时没用）
                double HSI07_01_002 = resultSet1.getDouble("s2");//门急诊
                double HSI07_01_004 = resultSet1.getDouble("s2");//体检
                double HSI07_01_012 = resultSet1.getDouble("s4");//出院
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgCode);
                }
                if(eventType == null){
                    dataMap1.put("hospitalArchives",HSI07_01_001);
                }else if(eventType == 1){
                    dataMap1.put("hospitalArchives",HSI07_01_012);
                }else if(eventType == 2){
                    dataMap1.put("hospitalArchives",HSI07_01_004);
                }else if(eventType == 0) {
                    dataMap1.put("hospitalArchives",HSI07_01_002);
                }
                if(datasetMap.containsKey(orgCode)){
                    dataMap1.put("hospitalDataset",datasetMap.get(orgCode));
                }else {
                    dataMap1.put("hospitalDataset",datasetMap.get(defaultOrgCode));
                }
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end1 = System.currentTimeMillis();
        System.out.println("统计医院数据时间："+(end1-startTime));

        //统计接收数据
        String sql2 = "SELECT count(*) c,org_code FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' ";
        if(eventType!=null){
            sql2 += " and event_type = "+eventType ;
        }
        sql2 += " group by org_code";
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        try {
            while (resultSet2.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet2.getString("org_code");
                double total = resultSet2.getDouble("c");//接收 档案数
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgCode);
                }
                dataMap1.put("receiveArchives",total);
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end2 = System.currentTimeMillis();
        System.out.println("统计接收数据(档案数)时间："+(end2-end1));

        String sql3 = "SELECT count(*) c,org_code FROM json_archives_qc/qc_metadata_info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' ";
        if(eventType!=null){
            sql3 += " and event_type = "+eventType ;
        }
        sql3 += " group by org_code";
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        try {
            while (resultSet3.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet3.getString("org_code");
                double total = resultSet3.getDouble("c");//接收 质量异常
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgCode);
                }
                dataMap1.put("receiveException",total);
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end3 = System.currentTimeMillis();
        System.out.println("统计接收数据(质量异常)时间："+(end3-end2));

        String sql4 = "SELECT details,org_code FROM json_archives_qc/qc_dataset_info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' ";
        if(eventType!=null){
            sql4 += " and event_type = "+eventType ;
        }
        sql4 += " group by org_code";
        ResultSet resultSet4 = elasticSearchUtil.findBySql(sql4);
        try {
            Map<String, Map<String, Object>> datasetMap1 = new HashMap<>(resultSet1.getFetchSize());
            while (resultSet4.next()) {
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
                    dataMap1 = initDataMap(datasetMap,orgCode);
                }
                dataMap1.put("receiveDataset",entry.getValue().size());//数据集个数
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end4 = System.currentTimeMillis();
        System.out.println("统计接收数据(数据集)时间："+(end4-end3));

        //资源化数据
        String sql5 = "SELECT count(*) c,org_code,archive_status FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' and (archive_status=2 or archive_status=3) group by org_code,archive_status";
        ResultSet resultSet5 = elasticSearchUtil.findBySql(sql5);
        try {
            while (resultSet5.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet5.getString("org_code");
                String archiveStatus = resultSet5.getString("archive_status");// 2失败，3成功
                double total = resultSet5.getDouble("c");//资源化 解析成功和失败
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgCode);
                }
                if("2".equals(archiveStatus)){
                    dataMap1.put("resourceFailure",total);//失败
                }else {
                    dataMap1.put("resourceSuccess",total);//成功
                }
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end5 = System.currentTimeMillis();
        System.out.println("资源化数据(解析成功和失败 )时间："+(end5-end4));

        String sql6 = "SELECT count(*) c,org_code FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' and defect=1 group by org_code";
        ResultSet resultSet6 = elasticSearchUtil.findBySql(sql6);
        try {
            while (resultSet6.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet6.getString("org_code");
                double total = resultSet6.getDouble("c");//资源化 解析异常
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgCode);
                }
                dataMap1.put("resourceException",total);
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end6 = System.currentTimeMillis();
        System.out.println("资源化数据(解析异常)时间："+(end6-end5));

        for (Map<String, Object> map:dataMap.values()){
            re.add(map);
        }

        System.out.println("总时间时间："+(System.currentTimeMillis()-startTime));

        return re;
    }

    /**
     * 初始化datamap 数据
     * @param datasetMap
     * @param orgCode
     * @return
     */
    private Map initDataMap(Map<String, Object> datasetMap,String orgCode){
        Map dataMap = new HashedMap();
        dataMap.put("orgCode",orgCode);//机构code
        dataMap.put("hospitalArchives",0);//医院档案数
        dataMap.put("hospitalDataset",0);//医院数据集
        dataMap.put("receiveArchives",0);//接收档案数
        dataMap.put("receiveDataset",0); //接收数据集
        dataMap.put("receiveException",0);//接收异常
        dataMap.put("resourceSuccess",0);//资源化-成功
        dataMap.put("resourceFailure",0);//资源化-失败
        dataMap.put("resourceException",0);//资源化-异常
        if(datasetMap.containsKey(orgCode)){
            dataMap.put("hospitalDataset",datasetMap.get(orgCode));
        }else {
            dataMap.put("hospitalDataset",datasetMap.get(defaultOrgCode));
        }
        return dataMap;
    }



}
