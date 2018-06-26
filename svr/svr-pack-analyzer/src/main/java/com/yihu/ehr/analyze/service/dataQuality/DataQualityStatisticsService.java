package com.yihu.ehr.analyze.service.dataQuality;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihu.ehr.analyze.dao.DqPaltformReceiveWarningDao;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author yeshijie on 2018/5/31.
 */
@Service
public class DataQualityStatisticsService extends BaseJpaService {

    private final static Logger logger = LoggerFactory.getLogger(WarningQuestionService.class);
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    private WarningSettingService warningSettingService;
    @Autowired
    private DqPaltformReceiveWarningDao dqPaltformReceiveWarningDao;
    @Autowired
    private PackQcReportService packQcReportService;
    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;

    /**
     * 统计查询
     * @param start 接收时间
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
        double totalHospitalAcrhives = 0;//医疗云总档案数
        double totalHospitalDataset = 0;//医疗云总数据集数
        double totalReceiveArchives = 0;//医疗云总接收档案数
        double totalReceiveException = 0;//医疗云总接收质量异常数
        double totalReceiveDataset = 0;//医疗云总接收据集数
        double totalResourceFailure = 0;//医疗云总资源化失败数
        double totalResourceSuccess = 0;//医疗云总资源化成功数
        double totalResourceException = 0;//医疗云总资源化异常数

        //初始化 数据集数据
        Session session = currentSession();
        Query query = session.createSQLQuery("SELECT org_code,COUNT(*) c from dq_dataset_warning WHERE type = 1 GROUP BY org_code");
        List<Object[]> datasetList = query.list();
        Map<String, Object> datasetMap = new HashedMap();
        datasetList.forEach(one->{
            String orgCode = one[0].toString();
            Integer num = Integer.valueOf(one[1].toString());
            datasetMap.put(orgCode,num);
        });
        //统计医疗云平台数据集总数
        query = session.createSQLQuery("SELECT count(DISTINCT code) c from dq_dataset_warning WHERE type = 1 and org_code != '"+defaultOrgCode+"'");
        List<Object> tmpList = query.list();
        totalHospitalDataset = Integer.valueOf(tmpList.get(0).toString());

        Long en = System.currentTimeMillis();
        logger.info("初始化 数据集数据时间："+(en-startTime));

        //获取医院数据
        Query query1 = session.createSQLQuery("SELECT org_code,full_name from organizations where org_type = 'Hospital' ");
        List<Object[]> orgList = query1.list();
        Map<String, Object> orgMap = new HashedMap();
        orgList.forEach(one->{
            String orgCode = one[0].toString();
            String name = one[1].toString();
            orgMap.put(orgCode,name);
        });

        Long end0 = System.currentTimeMillis();
        logger.info("获取医院名称时间："+(end0-en));

        //统计医院数据
        String sql1 = "SELECT sum(HSI07_01_001) s1,sum(HSI07_01_002) s2,sum(HSI07_01_004) s3,sum(HSI07_01_012) s4,org_code FROM qc/daily_report where create_date>= '"+start+"T00:00:00' AND create_date <='" +  end + "T23:59:59' group by org_code";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        try {
            while (resultSet1.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet1.getString("org_code");
                double HSI07_01_001 = resultSet1.getDouble("s1");//总诊疗=门急诊+出院+体检（入院的不算和js开头的暂时没用）
                double HSI07_01_002 = resultSet1.getDouble("s2");//门急诊
                double HSI07_01_004 = resultSet1.getDouble("s3");//体检
                double HSI07_01_012 = resultSet1.getDouble("s4");//出院
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgMap.get(orgCode),orgCode);
                }
                if(eventType == null){
                    dataMap1.put("hospitalArchives",HSI07_01_001);
                    totalHospitalAcrhives+=HSI07_01_001;
                }else if(eventType == 1){
                    dataMap1.put("hospitalArchives",HSI07_01_012);
                    totalHospitalAcrhives+=HSI07_01_012;
                }else if(eventType == 2){
                    dataMap1.put("hospitalArchives",HSI07_01_004);
                    totalHospitalAcrhives+=HSI07_01_004;
                }else if(eventType == 0) {
                    dataMap1.put("hospitalArchives",HSI07_01_002);
                    totalHospitalAcrhives+=HSI07_01_002;
                }
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end1 = System.currentTimeMillis();
        logger.info("统计医院数据时间："+(end1-end0));

        //统计接收数据
        String sql2 = "SELECT count(distinct event_no) c,org_code FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' AND pack_type=1 ";
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
                    dataMap1 = initDataMap(datasetMap,orgMap.get(orgCode),orgCode);
                }
                dataMap1.put("receiveArchives",total);
                totalReceiveArchives+=total;
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end2 = System.currentTimeMillis();
        logger.info("统计接收数据(档案数)时间："+(end2-end1));

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
                    dataMap1 = initDataMap(datasetMap,orgMap.get(orgCode),orgCode);
                }
                dataMap1.put("receiveException",total);
                totalReceiveException+=total;
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end3 = System.currentTimeMillis();
        logger.info("统计接收数据(质量异常)时间："+(end3-end2));

        String sql4 = "SELECT details,org_code FROM json_archives_qc/qc_dataset_info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' ";
        if(eventType!=null){
            sql4 += " and event_type = "+eventType ;
        }
        sql4 += " group by org_code,details";
        ResultSet resultSet4 = elasticSearchUtil.findBySql(sql4);
        try {
            Map<String,String> totalMap = new HashedMap();
            Map<String, Map<String, Object>> datasetMap1 = new HashMap<>();
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
                    JSONObject tmp = jsonArray.getJSONObject(i);
                    for(String dataset:tmp.keySet()){
                        if(!datasetMap2.containsKey(dataset)){
                            datasetMap2.put(dataset,dataset);
                        }
                        if(!totalMap.containsKey(dataset)){
                            totalMap.put(dataset,dataset);
                        }
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
                    dataMap1 = initDataMap(datasetMap,orgMap.get(orgCode),orgCode);
                }
                dataMap1.put("receiveDataset",entry.getValue().size());//数据集个数
                dataMap.put(orgCode,dataMap1);
            }
            totalReceiveDataset = totalMap.size();
        }catch (Exception e){
            e.getMessage();
        }

        Long end4 = System.currentTimeMillis();
        logger.info("统计接收数据(数据集)时间："+(end4-end3));

        //资源化数据
        String sql5 = "SELECT count(distinct event_no) c,org_code,archive_status FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' AND pack_type=1 and (archive_status=2 or archive_status=3) group by org_code,archive_status";
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
                    dataMap1 = initDataMap(datasetMap,orgMap.get(orgCode),orgCode);
                }
                if("2".equals(archiveStatus)){
                    dataMap1.put("resourceFailure",total);//失败
                    totalResourceFailure+=total;
                }else {
                    dataMap1.put("resourceSuccess",total);//成功
                    totalResourceSuccess+=total;
                }
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end5 = System.currentTimeMillis();
        logger.info("资源化数据(解析成功和失败 )时间："+(end5-end4));

        String sql6 = "SELECT count(distinct event_no) c,org_code FROM json_archives/info where receive_date>= '"+start+" 00:00:00' AND receive_date<='" +  end + " 23:59:59' AND pack_type=1 and defect=1 group by org_code";
        ResultSet resultSet6 = elasticSearchUtil.findBySql(sql6);
        try {
            while (resultSet6.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet6.getString("org_code");
                double total = resultSet6.getDouble("c");//资源化 解析异常
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initDataMap(datasetMap,orgMap.get(orgCode),orgCode);
                }
                dataMap1.put("resourceException",total);
                totalResourceException+=total;
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        Long end6 = System.currentTimeMillis();
        logger.info("资源化数据(解析异常)时间："+(end6-end5));

        //新增医疗云平台数据
        Map<String, Object> totalMap = new HashedMap();
        totalMap.put("orgCode",cloud);
        totalMap.put("orgName",cloudName);
        totalMap.put("hospitalArchives",totalHospitalAcrhives);//医院档案数
        totalMap.put("hospitalDataset",totalHospitalDataset);//医院数据集
        totalMap.put("receiveArchives",totalReceiveArchives);//接收档案数
        totalMap.put("receiveDataset",totalReceiveDataset); //接收数据集
        totalMap.put("receiveException",totalReceiveException);//接收异常
        totalMap.put("resourceSuccess",totalResourceSuccess);//资源化-成功
        totalMap.put("resourceFailure",totalResourceFailure);//资源化-失败
        totalMap.put("resourceException",totalResourceException);//资源化-异常
        re.add(totalMap);
        for (Map<String, Object> map:dataMap.values()){
            re.add(map);
        }

        logger.info("总时间时间："+(System.currentTimeMillis()-startTime));

        return re;
    }

    /**
     * 统计机构档案包数量
     *
     * @param orgInfoList    机构编码、名称，例：[{orgCode:'xx',orgName:'xx'},{...},...]，医疗云汇总记录的 orgCode 用 all 表示。
     * @param archiveStatus  解析状态，可为空
     * @param eventDateStart 就诊时间（起始），格式 yyyy-MM-dd
     * @param eventDateEnd   就诊时间（截止），格式 yyyy-MM-dd
     * @return
     */
    public Long packetCount(List<Map<String, String>> orgInfoList, String archiveStatus, String eventDateStart, String eventDateEnd) {
        Long count = 0L;
        if (orgInfoList.size() == 1 && cloud.equals(orgInfoList.get(0).get("orgCode"))) {
            String sql1 = "SELECT count(distinct event_no) c FROM json_archives/info where receive_date>= '"+eventDateStart+"' AND receive_date<='" +  eventDateEnd + "' AND pack_type=1 ";
            if (!StringUtils.isEmpty(archiveStatus)) {
                sql1+=" and archive_status ="+archiveStatus;
            }
            try {
                ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                while (resultSet1.next()) {
                    Double total = resultSet1.getDouble("c");//接收 档案数
                    count+=total.longValue();
                }
            }catch (Exception e){
                e.getMessage();
            }
        } else {
            for (Map<String, String> orgInfo : orgInfoList) {
                //统计接收数据
                String sql2 = "SELECT count(distinct event_no) c FROM json_archives/info where receive_date>= '"+eventDateStart+"' AND receive_date<='" +  eventDateEnd + "' AND pack_type=1 and org_code='" + orgInfo.get("orgCode")+"'";
                if (!StringUtils.isEmpty(archiveStatus)) {
                    sql2+=" and archive_status ="+archiveStatus;
                }
                try {
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    while (resultSet2.next()) {
                        Double total = resultSet2.getDouble("c");//接收 档案数
                        count+=total.longValue();
                    }
                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
        return count;
    }

    /**
     * 机构的档案包接收报告数据
     *
     * @param orgInfoList    机构编码、名称，例：[{orgCode:'xx',orgName:'xx'},{...},...]，医疗云汇总记录的 orgCode 用 all 表示。
     * @param eventDateStart 就诊时间（起始），格式 yyyy-MM-dd
     * @param eventDateEnd   就诊时间（截止），格式 yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> orgPackReportData(List<Map<String, String>> orgInfoList, String eventDateStart, String eventDateEnd) throws Exception {
        List<Map<String, Object>> reportDataList = new ArrayList<>();
        StringBuilder filter = new StringBuilder("event_date[" + eventDateStart + " TO " + eventDateEnd + "]");
        for (Map<String, String> orgInfo : orgInfoList) {
            String orgCode = orgInfo.get("orgCode");
            String orgName = orgInfo.get("orgName");

            Map<String, Object> reportData = new HashMap<>();
            reportData.put("orgName", orgName);
            reportData.put("orgCode", orgCode);

            // 医院上报数
            String reportedNumSql1 = "SELECT SUM(HSI07_01_001) total,SUM(HSI07_01_002) outpatientNum, SUM(HSI07_01_004) healthExaminationNum, SUM(HSI07_01_012) hospitalDischargeNum FROM qc/daily_report " +
                    "WHERE event_date BETWEEN '" + eventDateStart.replace(" ","T") + "' AND '" + eventDateEnd.replace(" ","T")+"'";
            if (!cloud.equals(orgInfo.get("orgCode"))) {
                reportedNumSql1 += " AND org_code='" + orgCode + "'";
            }
            String reportedNumFields1 = "total,outpatientNum,healthExaminationNum,hospitalDischargeNum";
            List<Map<String, Object>> reportedNumList1 = elasticSearchUtil.findBySql(Arrays.asList(reportedNumFields1.split(",")), reportedNumSql1);
            reportData.put("reportedNumList1", reportedNumList1);
            // TODO 采集情况
            String reportedNumSql2 = "SELECT count(distinct event_no) total FROM json_archives/info where receive_date BETWEEN '"+eventDateStart+"' AND '" +  eventDateEnd + "' and pack_type=1 " ;
            if (!cloud.equals(orgInfo.get("orgCode"))) {
                reportedNumSql2 += " AND org_code='" + orgCode + "'";
            }
            reportedNumSql2 += " group by event_type";
            String reportedNumFields2 = "event_type,total";
            List<Map<String, Object>> reportedNumList2 = elasticSearchUtil.findBySql(Arrays.asList(reportedNumFields2.split(",")), reportedNumSql2);
            Map<String, Object> collectionMap = new HashedMap();
            collectionMap.put("outpatientNum",0);
            collectionMap.put("healthExaminationNum",0);
            collectionMap.put("hospitalDischargeNum",0);
            double totalCollection = 0;
            for (Map<String, Object> map : reportedNumList2){
                double total = Double.valueOf(map.get("total").toString());
                String eventType = map.get("event_type").toString();
                totalCollection+=total;
                if("1".equals(eventType)){
                    collectionMap.put("hospitalDischargeNum",total);
                }else if("2".equals(eventType)){
                    collectionMap.put("healthExaminationNum",total);
                }else if("0".equals(eventType)){
                    collectionMap.put("outpatientNum",total);
                }
            }
            collectionMap.put("total",totalCollection);
            reportData.put("collectionMap", collectionMap);
            // TODO 采集内容
            String reportedNumSql3 = "SELECT count(distinct event_no) total FROM json_archives/info where receive_date BETWEEN '"+eventDateStart+"' AND '" +  eventDateEnd + "' and pack_type=1 " ;
            if (!cloud.equals(orgInfo.get("orgCode"))) {
                reportedNumSql3 += " AND org_code='" + orgCode + "'";
            }
            reportedNumSql3 += " group by event_type,date_histogram(field='receive_date','interval'='1d',format='yyyy-MM-dd',alias=receiveDate)";
            String reportedNumFields3 = "event_type,receiveDate,total";
            List<Map<String, Object>> reportedNumList3 = elasticSearchUtil.findBySql(Arrays.asList(reportedNumFields3.split(",")), reportedNumSql3);
            Map<String, Map<String, Object>> collectionMap2 = new HashedMap();
            reportedNumList3.forEach(map->{
                String receiveDate = map.get("receiveDate").toString();
                String eventType = map.get("event_type").toString();
                double total = Double.valueOf(map.get("total").toString());
                Map<String, Object> tmpMap = null;
                if(collectionMap2.containsKey(receiveDate)){
                    tmpMap = collectionMap2.get(receiveDate);
                }else{
                    tmpMap = new HashedMap();
                    tmpMap.put("outpatientNum",0);
                    tmpMap.put("healthExaminationNum",0);
                    tmpMap.put("hospitalDischargeNum",0);
                    tmpMap.put("receiveDate",receiveDate);
                }
                if("1".equals(eventType)){
                    tmpMap.put("hospitalDischargeNum",total);
                }else if("2".equals(eventType)){
                    tmpMap.put("healthExaminationNum",total);
                }else if("0".equals(eventType)){
                    tmpMap.put("outpatientNum",total);
                }
                collectionMap2.put(receiveDate,tmpMap);
            });
            List<Map<String, Object>> reportedList3 = new ArrayList<>();
            for (Map<String, Object> map:collectionMap2.values()){
                double total = Double.valueOf(map.get("outpatientNum").toString()) +
                        Double.valueOf(map.get("healthExaminationNum").toString()) +
                        Double.valueOf(map.get("hospitalDischargeNum").toString());
                if(total>0){
                    map.put("total",total);
                    reportedList3.add(map);
                }
            }

            reportData.put("reportedNumList3", reportedList3);
            // TODO 解析情况
            String reportedNumSql4 = "SELECT count(distinct event_no) total FROM json_archives/info where receive_date BETWEEN '"+eventDateStart+"' AND '" +  eventDateEnd + "' and (archive_status=2 or archive_status=3 or archive_status=0 ) and pack_type=1 " ;
            if (!cloud.equals(orgInfo.get("orgCode"))) {
                reportedNumSql4 += " AND org_code='" + orgCode + "'";
            }
            reportedNumSql4 += " group by archive_status";
            String reportedNumFields4 = "archive_status,total";
            List<Map<String, Object>> reportedNumList4 = elasticSearchUtil.findBySql(Arrays.asList(reportedNumFields4.split(",")), reportedNumSql4);
            Map<String, Object> archiveMap = new HashedMap();
            archiveMap.put("archive_status0",0);//archive_status 资源化解析状态 0未解析 1正在解析 2解析失败 3解析完成
            archiveMap.put("archive_status2",0);
            archiveMap.put("archive_status3",0);
            for (Map<String, Object> map : reportedNumList4){
                double total = Double.valueOf(map.get("total").toString());
                String archiveStatus = map.get("archive_status").toString();
                if("3".equals(archiveStatus)){
                    archiveMap.put("archive_status3",total);
                }else if("2".equals(archiveStatus)){
                    archiveMap.put("archive_status2",total);
                }else if("0".equals(archiveStatus)){
                    archiveMap.put("archive_status0",total);
                }
            }
            reportData.put("archiveMap", archiveMap);
            // TODO 数据集总量
            List<Map<String, Object>> res = new ArrayList<>();
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append("qc_step=2;");
            stringBuilder1.append("receive_date>=" + eventDateStart + ";");
            stringBuilder1.append("receive_date<" + eventDateEnd + ";");
            if (!cloud.equals(orgInfo.get("orgCode"))){
                stringBuilder1.append("org_code=" + orgCode);
            }
            List<Map<String, Object>> list = elasticSearchUtil.list("json_archives_qc", "qc_dataset_info", stringBuilder1.toString());
            for(Map<String, Object> map : list){
                List<Map<String,Object>> dataSets = objectMapper.readValue(map.get("details").toString(), List.class);
                for(Map<String, Object> dataSet : dataSets){
                    for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
                        packQcReportService.getDataSets(map.get("version")+"", entry.getKey(), (int)entry.getValue(), res);
                    }
                }
            }
            reportData.put("reportedNumList5", res);
            // TODO 解析失败分析
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("archive_status=2;");
            stringBuilder2.append("receive_date>=" + eventDateStart + ";");
            stringBuilder2.append("receive_date<" + eventDateEnd + ";");
            if (!cloud.equals(orgInfo.get("orgCode"))){
                stringBuilder2.append("org_code=" + orgCode);
            }
            TransportClient transportClient = elasticSearchPool.getClient();
            try {
                List<Map<String, Object>> resultList = new ArrayList<>();
                SearchRequestBuilder builder = transportClient.prepareSearch("json_archives");
                builder.setTypes("info");
                builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
                builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder2.toString()));
                AggregationBuilder terms = AggregationBuilders.terms("error_type").field("error_type");
                builder.addAggregation(terms);
                builder.setSize(0);
                builder.setExplain(true);
                SearchResponse response = builder.get();
                LongTerms longTerms = response.getAggregations().get("error_type");
                for(Terms.Bucket item: longTerms.getBuckets()){
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("error_type", item.getKeyAsString());
                    temp.put("error_count", item.getDocCount());
                    resultList.add(temp);
                }
                reportData.put("reportedNumList6", resultList);
            } finally {
                elasticSearchPool.releaseClient(transportClient);
            }
            reportDataList.add(reportData);
        }
        return reportDataList;
    }

    /**
     * 初始化datamap 数据
     * @param datasetMap
     * @param orgCode
     * @return
     */
    private Map initDataMap(Map<String, Object> datasetMap,Object orgName,String orgCode){
        Map dataMap = new HashedMap();
        dataMap.put("orgCode",orgCode);//机构code
        dataMap.put("orgName",orgName);//机构名称
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

    /**
     * 初始化ratemap 数据
     * @param warningMap
     * @param orgCode
     * @return
     */
    private Map initRateMap(Map<String, DqPaltformReceiveWarning> warningMap,Object orgName,String orgCode){
        Map dataMap = new HashedMap();
        dataMap.put("orgCode",orgCode);//机构code
        dataMap.put("orgName",orgName);//机构名称
        dataMap.put("outpatientInTime",0);//门诊及时数
        dataMap.put("hospitalInTime",0);//住院及时数
        dataMap.put("peInTime",0);//体检及时数
        dataMap.put("outpatientIntegrity",0);//门诊完整数
        dataMap.put("hospitalIntegrity",0);//住院完整数
        dataMap.put("peIntegrity",0);//体检完整数
        dataMap.put("totalVisit",0);//总就诊数
        dataMap.put("totalOutpatient",0);//总门诊数
        dataMap.put("totalPe",0);//总体检数
        dataMap.put("totalHospital",0);//总住院数
        return dataMap;
    }

    /**
     * 判断是否及时
     * @param warningMap
     * @param orgCode
     * @param eventType 就诊类型
     * @param delay 延时时间（天）
     * @return
     */
    private boolean isInTime(Map<String, DqPaltformReceiveWarning> warningMap,String orgCode,String eventType,long delay){
        if(StringUtils.isBlank(eventType)||"null".equals(eventType)){
            //就诊类型为空 直接返回false
            return false;
        }
        DqPaltformReceiveWarning warning = null;
        if(warningMap.containsKey(orgCode)){
            warning = warningMap.get(orgCode);
        }else {
            warning = warningMap.get(defaultOrgCode);
        }

        boolean re = false;
        switch (eventType){
            case "0":
                //0门诊
                re = warning.getOutpatientInTime() >= delay;
                break;
            case "1":
                //1住院
                re = warning.getHospitalInTime() >= delay;
                break;
            case "2":
                //2体检
                re = warning.getPeInTime() >= delay;
                break;
            default:
                break;
        }

        return re;
    }

    /**
     * 完整采集的档案包数量集合
     * @param pageIndex
     * @param pageSize
     * @param orgCode
     * @param eventDateStart
     * @param eventDateEnd
     * @param eventType
     * @return
     */
    public Map<String,Object> receivedPacketNumList(Integer pageIndex,Integer pageSize,String orgCode,String eventDateStart,String eventDateEnd,Integer eventType){
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String,Object> re = new HashedMap();
        try{
            String filters = "";
            if(cloud.equals(orgCode)){
                filters = " event_date BETWEEN '" + eventDateStart + " 00:00:00' AND '" + eventDateEnd + " 23:59:59' and pack_type=1 ";
            }else{
                filters = "org_code='" + orgCode
                        + "' AND event_date BETWEEN '" + eventDateStart + " 00:00:00' AND '" + eventDateEnd + " 23:59:59' and pack_type=1 ";
            }
            // 及时率场合

            StringBuilder sql = new StringBuilder("SELECT COUNT(distinct event_no) packetCount FROM json_archives/info WHERE ");
            sql.append(filters);
            sql.append(" GROUP BY date_histogram(field='receive_date','interval'='1d',format='yyyy-MM-dd',alias=receiveDate)");
            List<String> fields = new ArrayList<>(2);
            fields.add("packetCount");
            fields.add("receiveDate");
            List<Map<String, Object>> searchList = elasticSearchUtil.findBySql(fields, sql.toString());
            int count = searchList.size();

            // 截取当前页数据
            int startLine = (pageIndex - 1) * pageSize;
            int endLine = startLine + pageSize - 1;
            for (int i = startLine; i <= endLine; i++) {
                if (i < count) {
                    resultList.add(searchList.get(i));
                } else {
                    break;
                }
            }
            re.put("count",count);
        }catch (Exception e){
            e.getMessage();
            re.put("count",0);
        }
        re.put("list",resultList);
        return re;
    }


    /**
     * 及时率和完整率
     * @param start 就诊时间
     * @param end
     */
    public List<Map<String,Object>> inTimeAndIntegrityRate(String start,String end) throws Exception{
        List<Map<String,Object>> re = new ArrayList<>();
        String dateStr = DateUtil.toString(new Date());
        if(StringUtils.isBlank(start)){
            start = dateStr;
        }
        if(StringUtils.isBlank(end)){
            end = dateStr;
        }

        Long startTime = System.currentTimeMillis();

        //初始化 及时率数据
        Session session = currentSession();
        List<DqPaltformReceiveWarning> warningList = dqPaltformReceiveWarningDao.findAll();
        Map<String, DqPaltformReceiveWarning> warningMap = new HashedMap(warningList.size());
        warningList.forEach(one->{
            String orgCode = one.getOrgCode();
            warningMap.put(orgCode,one);
        });

        Long en = System.currentTimeMillis();
        logger.info("初始化 数据集数据时间："+(en-startTime));

        //获取医院数据
        Query query1 = session.createSQLQuery("SELECT org_code,full_name from organizations where org_type = 'Hospital' ");
        List<Object[]> orgList = query1.list();
        Map<String, Object> orgMap = new HashedMap(orgList.size());
        orgList.forEach(one->{
            String orgCode = one[0].toString();
            String name = one[1].toString();
            orgMap.put(orgCode,name);
        });

        Long end0 = System.currentTimeMillis();
        logger.info("获取医院名称时间："+(end0-en));

        double totalVisitNum = 0;//总就诊数
        double totalOutpatientNum = 0;//总门诊数
        double totalPeNum = 0;//总体检数
        double totalHospitalNum = 0;//总住院数
        double totalOutpatientInTime = 0;//总门诊及时数
        double totalPeInTime = 0;//总体检及时数
        double totalHospitalInTime = 0;//总住院及时数
        double totalOutpatientIntegrity = 0;//总门诊完整数
        double totalHospitalIntegrity = 0;//总住院完整数
        double totalPeIntegrity = 0;//总体检完整数

        //统计总数
        String sql1 = "SELECT sum(HSI07_01_001) s1,sum(HSI07_01_002) s2,sum(HSI07_01_004) s3,sum(HSI07_01_012) s4,org_code FROM qc/daily_report where event_date>= '"+start+"T00:00:00' AND event_date <='" +  end + "T23:59:59' group by org_code";
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        try {
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            while (resultSet1.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet1.getString("org_code");
                double HSI07_01_001 = resultSet1.getDouble("s1");//总诊疗=门急诊+出院+体检（入院的不算和js开头的暂时没用）
                double HSI07_01_002 = resultSet1.getDouble("s2");//门急诊
                double HSI07_01_004 = resultSet1.getDouble("s3");//体检
                double HSI07_01_012 = resultSet1.getDouble("s4");//出院
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initRateMap(warningMap,orgMap.get(orgCode),orgCode);
                }
                dataMap1.put("totalVisit",HSI07_01_001);
                dataMap1.put("totalOutpatient",HSI07_01_002);
                dataMap1.put("totalPe",HSI07_01_004);
                dataMap1.put("totalHospital",HSI07_01_012);
                totalVisitNum+=HSI07_01_001;
                totalOutpatientNum+=HSI07_01_002;
                totalPeNum+=HSI07_01_004;
                totalHospitalNum+=HSI07_01_012;
                dataMap.put(orgCode,dataMap1);
            }
        }catch (Exception e){
            if(!"Error".equals(e.getMessage())){
                e.printStackTrace();
            }
        }

        Long end1 = System.currentTimeMillis();
        logger.info("统计总数时间："+(end1-startTime));

        //统计及时数
        String sql2 = "SELECT count(distinct event_no) c,org_code,event_type,delay FROM json_archives/info where event_date>= '"+start+" 00:00:00' AND event_date<='" +  end + " 23:59:59' AND pack_type=1 and delay is not null group by org_code,event_type,delay ";
        try {
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            while (resultSet2.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet2.getString("org_code");
                long delay = Long.valueOf(resultSet2.getString("delay"));// 延时时间
                String eventType = resultSet2.getString("event_type");// 事件类型 0门诊 1住院 2体检
                double total = resultSet2.getDouble("c");//及时数
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initRateMap(warningMap,orgMap.get(orgCode),orgCode);
                }
                boolean flag = StringUtils.isNotBlank(eventType)&&!"null".equals(eventType)&&total>0;
                if(flag&&isInTime(warningMap,orgCode,eventType,delay)){
                    if("0".equals(eventType)){
                        total += Double.parseDouble(dataMap1.get("outpatientInTime").toString());
                        totalOutpatientInTime += total;
                        dataMap1.put("outpatientInTime",total);
                    }else if("1".equals(eventType)){
                        total += Double.parseDouble(dataMap1.get("hospitalInTime").toString());
                        totalHospitalInTime += total;
                        dataMap1.put("hospitalInTime",total);
                    }else if("2".equals(eventType)){
                        total += Double.parseDouble(dataMap1.get("peInTime").toString());
                        totalPeInTime += total;
                        dataMap1.put("peInTime",total);
                    }

                    dataMap.put(orgCode,dataMap1);
                }
            }
        }catch (Exception e){
            if(!"Error".equals(e.getMessage())){
                e.printStackTrace();
            }
        }

        Long end2 = System.currentTimeMillis();
        logger.info("统计及时数时间："+(end2-end1));

        //统计完整数
        String sql3 = "SELECT count(distinct event_no) c,org_code,event_type FROM json_archives/info where event_date>= '"+start+" 00:00:00' AND event_date<='" +  end + " 23:59:59' AND pack_type=1 group by org_code,event_type";
        try {
            ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
            while (resultSet3.next()) {
                Map<String, Object> dataMap1 = null;
                String orgCode = resultSet3.getString("org_code");
                String eventType = resultSet3.getString("event_type");// 事件类型 0门诊 1住院 2体检
                double total = resultSet3.getDouble("c");//完整数
                if(dataMap.containsKey(orgCode)){
                    dataMap1 = dataMap.get(orgCode);
                }else {
                    dataMap1 = initRateMap(warningMap,orgMap.get(orgCode),orgCode);
                }
                boolean flag = StringUtils.isNotBlank(eventType)&&!"null".equals(eventType)&&total>0;
                if(flag){
                    if("0".equals(eventType)){
                        dataMap1.put("outpatientIntegrity",total);
                        totalOutpatientIntegrity += total;
                    }else if("1".equals(eventType)){
                        dataMap1.put("hospitalIntegrity",total);
                        totalHospitalIntegrity += total;
                    }else if("2".equals(eventType)){
                        dataMap1.put("peIntegrity",total);
                        totalPeIntegrity += total;
                    }
                    dataMap.put(orgCode,dataMap1);
                }
            }
        }catch (Exception e){
            if(!"Error".equals(e.getMessage())){
                e.printStackTrace();
            }
        }

        Long end3 = System.currentTimeMillis();
        logger.info("统计完整数时间："+(end3-end2));

        //计算总数
        Map<String, Object> totalMap = new HashedMap();
        totalMap.put("orgCode",cloud);//机构code
        totalMap.put("orgName",cloudName);//机构名称
        totalMap.put("outpatientInTime",totalOutpatientInTime);//门诊及时数
        totalMap.put("hospitalInTime",totalHospitalInTime);//住院及时数
        totalMap.put("peInTime",totalPeInTime);//体检及时数
        totalMap.put("outpatientIntegrity",totalOutpatientIntegrity);//门诊完整数
        totalMap.put("hospitalIntegrity",totalHospitalIntegrity);//住院完整数
        totalMap.put("peIntegrity",totalPeIntegrity);//体检完整数
        totalMap.put("totalVisit",totalVisitNum);//总就诊数
        totalMap.put("totalOutpatient",totalOutpatientNum);//总门诊数
        totalMap.put("totalPe",totalPeNum);//总体检数
        totalMap.put("totalHospital",totalHospitalNum);//总住院数
        double totalVisitIntime = totalOutpatientInTime + totalHospitalInTime + totalPeInTime;
        double totalVisitIntegrity = totalOutpatientIntegrity + totalHospitalIntegrity + totalPeIntegrity;
        totalMap.put("visitIntime", totalVisitIntime);
        totalMap.put("visitIntegrity", totalVisitIntegrity);
        totalMap.put("outpatientInTimeRate",calRate(totalOutpatientInTime,totalOutpatientNum));
        totalMap.put("hospitalInTimeRate",calRate(totalHospitalInTime,totalHospitalNum));
        totalMap.put("peInTimeRate",calRate(totalPeInTime,totalPeNum));
        totalMap.put("visitIntimeRate",calRate(totalVisitIntime,totalVisitNum));
        totalMap.put("outpatientIntegrityRate",calRate(totalOutpatientIntegrity,totalOutpatientNum));
        totalMap.put("hospitalIntegrityRate",calRate(totalHospitalIntegrity,totalHospitalNum));
        totalMap.put("peIntegrityRate",calRate(totalPeIntegrity,totalPeNum));
        totalMap.put("visitIntegrityRate",calRate(totalVisitIntegrity,totalVisitNum));
        re.add(totalMap);

        //计算及时率及完整率
        for (Map<String, Object> map:dataMap.values()){
            double outpatientInTime = Double.parseDouble(map.get("outpatientInTime").toString());//门诊及时数
            double hospitalInTime = Double.parseDouble(map.get("hospitalInTime").toString());//住院及时数
            double peInTime = Double.parseDouble(map.get("peInTime").toString());//体检及时数
            double outpatientIntegrity = Double.parseDouble(map.get("outpatientIntegrity").toString());//门诊完整数
            double hospitalIntegrity = Double.parseDouble(map.get("hospitalIntegrity").toString());//住院完整数
            double peIntegrity = Double.parseDouble(map.get("peIntegrity").toString());//体检完整数
            double totalVisit = Double.parseDouble(map.get("totalVisit").toString());//总就诊数
            double totalOutpatient = Double.parseDouble(map.get("totalOutpatient").toString());//总门诊数
            double totalPe = Double.parseDouble(map.get("totalPe").toString());//总体检数
            double totalHospital = Double.parseDouble(map.get("totalHospital").toString());//总住院数

            double visitIntime = outpatientInTime + hospitalInTime + peInTime;
            double visitIntegrity = outpatientIntegrity + hospitalIntegrity + peIntegrity;
            map.put("visitIntime", visitIntime);
            map.put("visitIntegrity", visitIntegrity);
            map.put("outpatientInTimeRate",calRate(outpatientInTime,totalOutpatient));
            map.put("hospitalInTimeRate",calRate(hospitalInTime,totalHospital));
            map.put("peInTimeRate",calRate(peInTime,totalPe));
            map.put("visitIntimeRate",calRate(visitIntime,totalVisit));
            map.put("outpatientIntegrityRate",calRate(outpatientIntegrity,totalOutpatient));
            map.put("hospitalIntegrityRate",calRate(hospitalIntegrity,totalHospital));
            map.put("peIntegrityRate",calRate(peIntegrity,totalPe));
            map.put("visitIntegrityRate",calRate(visitIntegrity,totalVisit));
            re.add(map);
        }

        logger.info("总时间时间："+(System.currentTimeMillis()-startTime));

        return re;
    }

    /**
     * 计算及时率和完整率
     * @param molecular 分子
     * @param denominator 分母
     * @return
     */
    public String calRate(double molecular, double denominator){
        if(molecular==0){
            return "0.00%";
        }else if(denominator==0){
            return "100.00%";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00%");
        return decimalFormat.format(molecular/denominator);
    }

}
