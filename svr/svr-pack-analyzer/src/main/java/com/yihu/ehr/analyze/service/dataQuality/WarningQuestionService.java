package com.yihu.ehr.analyze.service.dataQuality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yihu.ehr.analyze.dao.*;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.*;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.*;

import static com.yihu.ehr.entity.quality.DqWarningRecord.DqWarningRecordWarningType.archiveNum;
import static com.yihu.ehr.entity.quality.DqWarningRecord.DqWarningRecordWarningType.errorNum;

/**
 * @author yeshijie on 2018/6/11.
 */
@Service
public class WarningQuestionService extends BaseJpaService {

    private final static Logger logger = LoggerFactory.getLogger(WarningQuestionService.class);
    @Autowired
    private WarningSettingService warningSettingService;
    @Autowired
    private DqWarningRecordDao dqWarningRecordDao;
    @Autowired
    private DqDatasetWarningDao dqDatasetWarningDao;
    @Autowired
    private DqPaltformReceiveWarningDao dqPaltformReceiveWarningDao;
    @Autowired
    private DqPaltformResourceWarningDao dqPaltformResourceWarningDao;
    @Autowired
    private DqPaltformUploadWarningDao dqPaltformUploadWarningDao;
    @Autowired
    private DataQualityStatisticsService dataQualityStatisticsService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Value("${quality.orgCode}")
    private String defaultOrgCode;

    /**
     * 预警问题生成分析
     */
    public void analyze(){
        String dateStr = DateUtil.formatDate(DateUtil.addDate(-1, new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        receive(dateStr);
        resource(dateStr);
        upload(dateStr);
    }

    /**
     * 接收预警
     */
    public void receive(String dateStr){
        Session session = currentSession();
        //0.获取医院数据
        Query query1 = session.createSQLQuery("SELECT org_code,full_name from organizations where org_type = 'Hospital'");
        List<Object[]> orgList = query1.list();
        Map<String, String> orgMap = new HashedMap(orgList.size());
        orgList.forEach(one->{
            String orgCode = one[0].toString();
            String name = one[1].toString();
            orgMap.put(orgCode,name);
        });

        //1.查找预警设置
        List<DqPaltformReceiveWarning> warningList = dqPaltformReceiveWarningDao.findAll();
        Map<String, DqPaltformReceiveWarning> warningMap = new HashedMap(warningList.size());
        warningList.forEach(one->{
            String orgCode = one.getOrgCode();
            warningMap.put(orgCode,one);
        });

        DqPaltformReceiveWarning defaultWarning = warningMap.get(defaultOrgCode);

        List<DqDatasetWarning> datasetList = dqDatasetWarningDao.findByType("1");
        Map<String, List<DqDatasetWarning>> datasetMap = new HashedMap(datasetList.size());
        datasetList.forEach(one->{
            String orgCode = one.getOrgCode();
            if(datasetMap.containsKey(orgCode)){
                datasetMap.get(orgCode).add(one);
            }else {
                List<DqDatasetWarning> list = new ArrayList<DqDatasetWarning>();
                list.add(one);
                datasetMap.put(orgCode,list);
            }
        });

        for (Map.Entry<String, DqPaltformReceiveWarning> entry : warningMap.entrySet()) {
            String orgCode = entry.getKey();
            entry.getValue().setDatasetWarningList(datasetMap.get(orgCode));
        }


        //2.统计实际值
        Map<String, Map<String, Object>> dataMap = new HashMap<>(warningList.size());

        //统计接收档案数据
        String sql1 = "SELECT count(*) c,org_code FROM json_archives/info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' group by org_code";
        try {
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            while (resultSet1.next()) {
                Map<String, Object> map = null;
                String orgCode = resultSet1.getString("org_code");
                double total = resultSet1.getDouble("c");//接收 档案数
                if(dataMap.containsKey(orgCode)){
                    map = dataMap.get(orgCode);
                }else {
                    map = initDataMap(defaultWarning);
                }
                map.put("receiveArchives",total);
                dataMap.put(orgCode,map);
            }
        }catch (Exception e){
            e.getMessage();
        }

        //统计质量异常
        String sql2 = "SELECT count(*) c,org_code FROM json_archives_qc/qc_metadata_info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' group by org_code";
        try {
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            while (resultSet2.next()) {
                Map<String, Object> map = null;
                String orgCode = resultSet2.getString("org_code");
                double total = resultSet2.getDouble("c");//接收 质量异常
                if(dataMap.containsKey(orgCode)){
                    map = dataMap.get(orgCode);
                }else {
                    map = initDataMap(defaultWarning);
                }
                map.put("receiveException",total);
                dataMap.put(orgCode,map);
            }
        }catch (Exception e){
            e.getMessage();
        }

        //数据集
        String sql3 = "SELECT details,org_code FROM json_archives_qc/qc_dataset_info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' group by org_code";
        try {
            ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
            Map<String, Map<String, Object>> datasetMap1 = new HashMap<>();
            while (resultSet3.next()) {
                Map<String, Object> map = null;
                String orgCode = resultSet3.getString("org_code");
                String details = resultSet3.getString("details");//接收 数据集
                if(datasetMap1.containsKey(orgCode)){
                    map = datasetMap1.get(orgCode);
                }else {
                    map = initDataMap(defaultWarning);
                }
                JSONArray jsonArray = JSON.parseArray(details);
                for(int i=0;i<jsonArray.size();i++){
                    String dataset = jsonArray.get(i).toString();
                    if(!map.containsKey(dataset)){
                        map.put(dataset,dataMap);
                    }
                }
                datasetMap1.put(orgCode,map);
            }

            for (Map.Entry<String, Map<String, Object>> entry : datasetMap1.entrySet()) {
                String orgCode = entry.getKey();
                Map<String, Object> map = null;
                if(dataMap.containsKey(orgCode)){
                    map = dataMap.get(orgCode);
                }else {
                    map = initDataMap(defaultWarning);
                }
                map.put("receiveDataset",entry.getValue().size());//数据集个数
                dataMap.put(orgCode,map);
            }
        }catch (Exception e){
            e.getMessage();
        }

        //及时率
        inTimeWarning(warningMap,dataMap);

        //3.预警
        List<DqWarningRecord> list = new ArrayList<>();
        Date recordTime = DateUtil.formatCharDateYMD(dateStr);
        String unqualified = "不合格";
        NumberFormat nf = NumberFormat.getPercentInstance();
        for (String orgCode : warningMap.keySet()) {
            if(defaultOrgCode.equals(orgCode)){
                continue;
            }
            try {
                String orgName = orgMap.get(orgCode);
                DqPaltformReceiveWarning warning = warningMap.get(orgCode);

                Map<String, Object> hospitalMap = dataMap.get(orgCode);
                if(hospitalMap==null){
                    //该医院没有上传数据
                    DqWarningRecord record1 = new DqWarningRecord();
                    record1.setOrgCode(orgCode);
                    record1.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record1.setActualValue("0");
                    record1.setId(getCode());
                    record1.setOrgName(orgName);
                    record1.setWarningType(DqWarningRecord.DqWarningRecordWarningType.archives.getValue());
                    record1.setQuota(DqWarningRecord.DqWarningRecordWarningType.archives.getName());
                    record1.setRecordTime(recordTime);
                    record1.setWarningTime(new Date());
                    record1.setWarningValue(warning.getArchiveNum()+"");
                    record1.setStatus("1");
                    record1.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.archives.getName()+unqualified);
                    list.add(record1);

                    DqWarningRecord record2 = new DqWarningRecord();
                    record2.setOrgCode(orgCode);
                    record2.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record2.setActualValue("0");
                    record2.setId(getCode());
                    record2.setOrgName(orgName);
                    record2.setWarningType(errorNum.getValue());
                    record2.setQuota(errorNum.getName());
                    record2.setRecordTime(recordTime);
                    record2.setWarningTime(new Date());
                    record2.setWarningValue(warning.getErrorNum()+"");
                    record2.setStatus("1");
                    record2.setProblemDescription(errorNum.getName()+unqualified);
                    list.add(record2);

                    DqWarningRecord record3 = new DqWarningRecord();
                    record3.setOrgCode(orgCode);
                    record3.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record3.setActualValue("0");
                    record3.setId(getCode());
                    record3.setOrgName(orgName);
                    record3.setWarningType(DqWarningRecord.DqWarningRecordWarningType.datasetWarningNum.getValue());
                    record3.setQuota(DqWarningRecord.DqWarningRecordWarningType.datasetWarningNum.getName());
                    record3.setRecordTime(recordTime);
                    record3.setWarningTime(new Date());
                    record3.setWarningValue(warning.getDatasetWarningNum()+"");
                    record3.setStatus("1");
                    record3.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.datasetWarningNum.getName()+unqualified);
                    list.add(record3);

                    DqWarningRecord record4 = new DqWarningRecord();
                    record4.setOrgCode(orgCode);
                    record4.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record4.setActualValue("0");
                    record4.setId(getCode());
                    record4.setOrgName(orgName);
                    record4.setWarningType(DqWarningRecord.DqWarningRecordWarningType.outpatientInTimeRate.getValue());
                    record4.setQuota(DqWarningRecord.DqWarningRecordWarningType.outpatientInTimeRate.getName());
                    record4.setRecordTime(recordTime);
                    record4.setWarningTime(new Date());
                    record4.setWarningValue(warning.getOutpatientInTimeRate());
                    record4.setStatus("1");
                    String description4 = "就诊日期为："+hospitalMap.get("outpatientReceiveTime")+"的"+
                            DqWarningRecord.DqWarningRecordWarningType.outpatientInTimeRate.getName()+unqualified;
                    record4.setProblemDescription(description4);
                    list.add(record4);

                    DqWarningRecord record5 = new DqWarningRecord();
                    record5.setOrgCode(orgCode);
                    record5.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record5.setActualValue("0");
                    record5.setId(getCode());
                    record5.setOrgName(orgName);
                    record5.setWarningType(DqWarningRecord.DqWarningRecordWarningType.hospitalInTimeRate.getValue());
                    record5.setQuota(DqWarningRecord.DqWarningRecordWarningType.hospitalInTimeRate.getName());
                    record5.setRecordTime(recordTime);
                    record5.setWarningTime(new Date());
                    record5.setWarningValue(warning.getHospitalInTimeRate());
                    record5.setStatus("1");
                    String description5 = "就诊日期为："+hospitalMap.get("hospitalReceiveTime")+"的"+
                            DqWarningRecord.DqWarningRecordWarningType.hospitalInTimeRate.getName()+unqualified;
                    record5.setProblemDescription(description5);
                    list.add(record5);

                    DqWarningRecord record6 = new DqWarningRecord();
                    record6.setOrgCode(orgCode);
                    record6.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record6.setActualValue("0");
                    record6.setId(getCode());
                    record6.setOrgName(orgName);
                    record6.setWarningType(DqWarningRecord.DqWarningRecordWarningType.peInTimeRate.getValue());
                    record6.setQuota(DqWarningRecord.DqWarningRecordWarningType.peInTimeRate.getName());
                    record6.setRecordTime(recordTime);
                    record6.setWarningTime(new Date());
                    record6.setWarningValue(warning.getPeInTimeRate());
                    record6.setStatus("1");
                    String description6 = "就诊日期为："+hospitalMap.get("peReceiveTime")+"的"+
                            DqWarningRecord.DqWarningRecordWarningType.peInTimeRate.getName()+unqualified;
                    record6.setProblemDescription(description6);
                    list.add(record6);

                    continue;
                }

                //1、档案数
                Long archiveNum = Long.valueOf(hospitalMap.get("receiveArchives").toString());
                if(archiveNum<warning.getArchiveNum()){
                    //接收的档案数小于预警值
                    DqWarningRecord record = new DqWarningRecord();
                    record.setOrgCode(orgCode);
                    record.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record.setActualValue(archiveNum+"");
                    record.setId(getCode());
                    record.setOrgName(orgName);
                    record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.archives.getValue());
                    record.setQuota(DqWarningRecord.DqWarningRecordWarningType.archives.getName());
                    record.setRecordTime(recordTime);
                    record.setWarningTime(new Date());
                    record.setWarningValue(warning.getArchiveNum()+"");
                    record.setStatus("1");
                    record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.archives.getName()+unqualified);
                    list.add(record);
                }

                //2、质量异常问题数
                Long errorNum = Long.valueOf(hospitalMap.get("receiveException").toString());
                if(errorNum>warning.getErrorNum()){
                    //接收的质量异常问题数大于预警值
                    DqWarningRecord record = new DqWarningRecord();
                    record.setOrgCode(orgCode);
                    record.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record.setActualValue(errorNum+"");
                    record.setId(getCode());
                    record.setOrgName(orgName);
                    record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.errorNum.getValue());
                    record.setQuota(DqWarningRecord.DqWarningRecordWarningType.errorNum.getName());
                    record.setRecordTime(recordTime);
                    record.setWarningTime(new Date());
                    record.setWarningValue(warning.getErrorNum()+"");
                    record.setStatus("1");
                    record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.errorNum.getName()+unqualified);
                    list.add(record);
                }
                //3、数据集
                Long datasetNum = Long.valueOf(hospitalMap.get("receiveDataset").toString());
                if(datasetNum<warning.getDatasetWarningList().size()){
                    //接收的数据集小于预警值
                    DqWarningRecord record = new DqWarningRecord();
                    record.setOrgCode(orgCode);
                    record.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                    record.setActualValue(datasetNum+"");
                    record.setId(getCode());
                    record.setOrgName(orgName);
                    record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.datasetWarningNum.getValue());
                    record.setQuota(DqWarningRecord.DqWarningRecordWarningType.datasetWarningNum.getName());
                    record.setRecordTime(recordTime);
                    record.setWarningTime(new Date());
                    record.setWarningValue(warning.getDatasetWarningList().size()+"");
                    record.setStatus("1");
                    record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.datasetWarningNum.getName()+unqualified);
                    list.add(record);
                }
                //4、门诊及时率
                try {
                    double totalOutpatient = Double.valueOf(hospitalMap.get("totalOutpatient").toString());
                    double outpatientIntime = Double.valueOf(hospitalMap.get("outpatientIntime").toString());
                    String outpatientRate = dataQualityStatisticsService.calRate(outpatientIntime,totalOutpatient);
                    double outpatientIntimeRate = Double.valueOf(outpatientRate.replace("%",""));
                    double intimeRate = Double.valueOf(warning.getOutpatientInTimeRate().replace("%",""));
                    if(outpatientIntimeRate<intimeRate){
                        //门诊及时率小于预警值
                        DqWarningRecord record = new DqWarningRecord();
                        record.setOrgCode(orgCode);
                        record.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                        record.setActualValue(outpatientRate);
                        record.setId(getCode());
                        record.setOrgName(orgName);
                        record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.outpatientInTimeRate.getValue());
                        record.setQuota(DqWarningRecord.DqWarningRecordWarningType.outpatientInTimeRate.getName());
                        record.setRecordTime(recordTime);
                        record.setWarningTime(new Date());
                        record.setWarningValue(warning.getOutpatientInTimeRate());
                        record.setStatus("1");
                        String description = "就诊日期为："+hospitalMap.get("outpatientReceiveTime")+"的"+
                                DqWarningRecord.DqWarningRecordWarningType.outpatientInTimeRate.getName()+unqualified;
                        record.setProblemDescription(description);
                        list.add(record);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                //5、住院及时率
                try {
                    double totalHospital = Double.valueOf(hospitalMap.get("totalHospital").toString());
                    double hospitalIntime = Double.valueOf(hospitalMap.get("hospitalIntime").toString());
                    String hospitalRate = dataQualityStatisticsService.calRate(hospitalIntime,totalHospital);
                    double hospitalIntimeRate = Double.valueOf(hospitalRate.replace("%",""));
                    double intimeRate = Double.valueOf(warning.getHospitalInTimeRate().replace("%",""));
                    if(hospitalIntimeRate<intimeRate){
                        //住院及时率小于预警值
                        DqWarningRecord record = new DqWarningRecord();
                        record.setOrgCode(orgCode);
                        record.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                        record.setActualValue(hospitalRate);
                        record.setId(getCode());
                        record.setOrgName(orgName);
                        record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.hospitalInTimeRate.getValue());
                        record.setQuota(DqWarningRecord.DqWarningRecordWarningType.hospitalInTimeRate.getName());
                        record.setRecordTime(recordTime);
                        record.setWarningTime(new Date());
                        record.setWarningValue(warning.getHospitalInTimeRate());
                        record.setStatus("1");
                        String description = "就诊日期为："+hospitalMap.get("hospitalReceiveTime")+"的"+
                                DqWarningRecord.DqWarningRecordWarningType.hospitalInTimeRate.getName()+unqualified;
                        record.setProblemDescription(description);
                        list.add(record);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //6、体检及时率
                try {
                    double totalPe = Double.valueOf(hospitalMap.get("totalPe").toString());
                    double peIntime = Double.valueOf(hospitalMap.get("peIntime").toString());
                    String peRate = dataQualityStatisticsService.calRate(peIntime,totalPe);
                    double peIntimeRate = Double.valueOf(peRate.replace("%",""));
                    double intimeRate = Double.valueOf(warning.getPeInTimeRate().replace("%",""));
                    if(peIntimeRate<intimeRate){
                        //住院及时率小于预警值
                        DqWarningRecord record = new DqWarningRecord();
                        record.setOrgCode(orgCode);
                        record.setType(DqWarningRecord.DqWarningRecordType.receive.getValue());
                        record.setActualValue(peRate);
                        record.setId(getCode());
                        record.setOrgName(orgName);
                        record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.peInTimeRate.getValue());
                        record.setQuota(DqWarningRecord.DqWarningRecordWarningType.peInTimeRate.getName());
                        record.setRecordTime(recordTime);
                        record.setWarningTime(new Date());
                        record.setWarningValue(warning.getPeInTimeRate());
                        record.setStatus("1");
                        String description = "就诊日期为："+hospitalMap.get("peReceiveTime")+"的"+
                                DqWarningRecord.DqWarningRecordWarningType.peInTimeRate.getName()+unqualified;
                        record.setProblemDescription(description);
                        list.add(record);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(list.size()>0){
            dqWarningRecordDao.save(list);
        }
    }

    /**
     * 及时率预警
     */
    public void inTimeWarning(Map<String, DqPaltformReceiveWarning> warningMap,Map<String, Map<String, Object>> dataMap){
        //1.先统计通用的及时数
        DqPaltformReceiveWarning defaultWarning = warningMap.get(defaultOrgCode);
        Integer defaultPe = defaultWarning.getPeInTime();
        Integer defaultHospital = defaultWarning.getHospitalInTime();
        Integer defaultOutpatient = defaultWarning.getOutpatientInTime();
        String defaultPeDateStr = DateUtil.formatDate(DateUtil.addDate(-(defaultPe+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String defaultHospitalDateStr = DateUtil.formatDate(DateUtil.addDate(-(defaultHospital+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        try{
            //统计总数
            String sql1 = "SELECT sum(HSI07_01_002) s2,sum(HSI07_01_004) s3,org_code FROM qc/daily_report where event_date>= '"+defaultPeDateStr+"T00:00:00' AND event_date <='" +  defaultPeDateStr + "T23:59:59' group by org_code";
            try {
                ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                while (resultSet1.next()) {
                    String orgCode = resultSet1.getString("org_code");
                    double HSI07_01_002 = resultSet1.getDouble("s2");//门急诊
                    double HSI07_01_004 = resultSet1.getDouble("s3");//体检
                    Map<String, Object> map = null;
                    if(dataMap.containsKey(orgCode)){
                        map = dataMap.get(orgCode);
                    }else {
                        map = initDataMap(defaultWarning);
                    }
                    map.put("totalOutpatient",HSI07_01_002);
                    map.put("totalPe",HSI07_01_004);
                    dataMap.put(orgCode,map);
                }
            }catch (Exception e){
                e.getMessage();
            }
            String sql2 = "SELECT sum(HSI07_01_012) s4,org_code FROM qc/daily_report where event_date>= '"+defaultHospitalDateStr+"T00:00:00' AND event_date <='" +  defaultHospitalDateStr + "T23:59:59' group by org_code";
            try {
                ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                while (resultSet2.next()) {
                    String orgCode = resultSet2.getString("org_code");
                    double HSI07_01_012 = resultSet2.getDouble("s4");//住院
                    Map<String, Object> map = null;
                    if(dataMap.containsKey(orgCode)){
                        map = dataMap.get(orgCode);
                    }else {
                        map = initDataMap(defaultWarning);
                    }
                    map.put("totalHospital",HSI07_01_012);
                    dataMap.put(orgCode,map);
                }
            }catch (Exception e){
                e.getMessage();
            }

            //统计及时数
            String sql3 = "SELECT count(*) c,org_code,event_type,delay FROM json_archives/info where event_date>= '"+defaultPeDateStr+" 00:00:00' AND event_date<='" +  defaultPeDateStr + " 23:59:59' and delay is not null group by org_code,event_type,delay ";
            try {
                ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
                while (resultSet3.next()) {
                    String orgCode = resultSet3.getString("org_code");
                    long delay = resultSet3.getLong("delay");// 延时时间
                    String eventType = resultSet3.getString("event_type");// 事件类型 0门诊 1住院 2体检
                    double total = resultSet3.getDouble("c");//及时数
                    Map<String, Object> map = null;
                    if(dataMap.containsKey(orgCode)){
                        map = dataMap.get(orgCode);
                    }else {
                        map = initDataMap(defaultWarning);
                    }
                    boolean flag = StringUtils.isNotBlank(eventType)&&!"null".equals(eventType)&&total>0;
                    if(flag&&isInTime(warningMap,orgCode,eventType,delay)){
                        if("0".equals(eventType)){
                            Object o = map.get("outpatientInTime");
                            if(o!=null){
                                total += Integer.parseInt(o.toString());
                            }
                            map.put("outpatientInTime",total);
                        }else if("2".equals(eventType)){
                            Object o = map.get("peInTime");
                            if(o!=null){
                                total += Integer.parseInt(o.toString());
                            }
                            map.put("peInTime",total);
                        }
                        dataMap.put(orgCode,map);
                    }
                }
            }catch (Exception e){
                e.getMessage();
            }

            String sql4 = "SELECT count(*) c,org_code,delay FROM json_archives/info where event_date>= '"+defaultHospitalDateStr+" 00:00:00' AND event_date<='" +  defaultHospitalDateStr + " 23:59:59' and event_type= '"+1+"' and delay is not null group by org_code,delay ";
            try {
                ResultSet resultSet4 = elasticSearchUtil.findBySql(sql4);
                while (resultSet4.next()) {
                    String orgCode = resultSet4.getString("org_code");
                    long delay = resultSet4.getLong("delay");// 延时时间
                    double total = resultSet4.getDouble("c");//及时数
                    Map<String, Object> map = null;
                    if(dataMap.containsKey(orgCode)){
                        map = dataMap.get(orgCode);
                    }else {
                        map = initDataMap(defaultWarning);
                    }
                    boolean flag = total>0;
                    if(flag&&isInTime(warningMap,orgCode,"1",delay)){
                        Object o = map.get("hospitalInTime");
                        if(o!=null){
                            total += Integer.parseInt(o.toString());
                        }
                        map.put("hospitalInTime",total);
                        dataMap.put(orgCode,map);
                    }
                }
            }catch (Exception e){
                e.getMessage();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //2、统计门诊体检及时时间不等于2，住院及时时间不等于7的数据
        for (String orgCode : warningMap.keySet()) {
            try {
                if(defaultOrgCode.equals(orgCode)){
                    continue;
                }
                Map<String, Object> map = null;
                if(dataMap.containsKey(orgCode)){
                    map = dataMap.get(orgCode);
                }else {
                    map = initDataMap(defaultWarning);
                }
                DqPaltformReceiveWarning warning = warningMap.get(orgCode);
                boolean flag = warning.getHospitalInTime().equals(defaultHospital)
                        &&warning.getPeInTime().equals(defaultPe)
                        &&warning.getOutpatientInTime().equals(defaultOutpatient);
                if(flag){
                    //预警值=默认值的 直接按默认的第一步已经计算过了
                    continue;
                }

                String peDateStr = DateUtil.formatDate(DateUtil.addDate(-(warning.getPeInTime()+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                String hospitalDateStr = DateUtil.formatDate(DateUtil.addDate(-(warning.getHospitalInTime()+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                String outpatientDateStr = DateUtil.formatDate(DateUtil.addDate(-(warning.getOutpatientInTime()+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);

                //统计总数
                //体检
                if(!warning.getPeInTime().equals(defaultPe)){
                    String sql1 = "SELECT sum(HSI07_01_004) s3 FROM qc/daily_report where event_date>= '"+peDateStr+"T00:00:00' AND event_date <='" +  peDateStr + "T23:59:59' and org_code = '"+orgCode+"' ";
                    try {
                        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                        while (resultSet1.next()) {
                            double HSI07_01_004 = resultSet1.getDouble("s3");//体检
                            map.put("totalPe",HSI07_01_004);
                            map.put("peReceiveTime",peDateStr);
                            dataMap.put(orgCode,map);
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }

                    String sql2 = "SELECT count(*) c,delay FROM json_archives/info where event_date>= '"+outpatientDateStr+" 00:00:00' AND event_date<='" +  outpatientDateStr + " 23:59:59' and org_code = '"+orgCode+"' and event_type= '2' group by delay ";
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    try {
                        while (resultSet2.next()) {
                            long delay = resultSet2.getLong("delay");// 延时时间
                            double total = resultSet2.getDouble("c");//及时数
                            if(total>0&&isInTime(warningMap,orgCode,"2",delay)){
                                Object o = map.get("peInTime");
                                if(o!=null){
                                    total += Integer.parseInt(o.toString());
                                }
                                map.put("peInTime",total);
                                map.put("peReceiveTime",peDateStr);
                                dataMap.put(orgCode,map);
                            }
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                }

                if(!warning.getOutpatientInTime().equals(defaultOutpatient)){
                    //门诊
                    String sql1 = "SELECT sum(HSI07_01_002) s2 FROM qc/daily_report where event_date>= '"+outpatientDateStr+"T00:00:00' AND event_date <='"+outpatientDateStr+"T23:59:59' and org_code = '"+orgCode+"' ";
                    try {
                        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                        while (resultSet1.next()) {
                            double HSI07_01_002 = resultSet1.getDouble("s4");//门急诊
                            map.put("totalOutpatient",HSI07_01_002);
                            map.put("outpatientReceiveTime",outpatientDateStr);
                            dataMap.put(orgCode,map);
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }

                    String sql2 = "SELECT count(*) c,delay FROM json_archives/info where event_date>= '"+outpatientDateStr+" 00:00:00' AND event_date<='" +  outpatientDateStr + " 23:59:59' and org_code = '"+orgCode+"' and event_type= '0' group by delay ";
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    try {
                        while (resultSet2.next()) {
                            long delay = resultSet2.getLong("delay");// 延时时间
                            double total = resultSet2.getDouble("c");//及时数
                            if(total>0&&isInTime(warningMap,orgCode,"0",delay)){
                                Object o = map.get("outpatientInTime");
                                if(o!=null){
                                    total += Integer.parseInt(o.toString());
                                }
                                map.put("outpatientInTime",total);
                                map.put("outpatientReceiveTime",outpatientDateStr);
                                dataMap.put(orgCode,map);
                            }
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                }

                if(!warning.getHospitalInTime().equals(defaultHospital)){
                    //住院
                    String sql1 = "SELECT sum(HSI07_01_012) s4 FROM qc/daily_report where event_date>= '"+hospitalDateStr+"T00:00:00' AND event_date <='"+hospitalDateStr+"T23:59:59' and org_code = '"+orgCode+"' ";
                    try {
                        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                        while (resultSet1.next()) {
                            double HSI07_01_012 = resultSet1.getDouble("s4");//住院
                            map.put("totalHospital",HSI07_01_012);
                            map.put("hospitalReceiveTime",hospitalDateStr);
                            dataMap.put(orgCode,map);
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }

                    String sql2 = "SELECT count(*) c,delay FROM json_archives/info where event_date>= '"+hospitalDateStr+" 00:00:00' AND event_date<='" +  hospitalDateStr + " 23:59:59' and org_code = '"+orgCode+"' and event_type= '1' group by delay ";
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    try {
                        while (resultSet2.next()) {
                            long delay = resultSet2.getLong("delay");// 延时时间
                            double total = resultSet2.getDouble("c");//及时数
                            if(total>0&&isInTime(warningMap,orgCode,"1",delay)){
                                Object o = map.get("hospitalInTime");
                                if(o!=null){
                                    total += Integer.parseInt(o.toString());
                                }
                                map.put("hospitalInTime",total);
                                map.put("hospitalReceiveTime",hospitalDateStr);
                                dataMap.put(orgCode,map);
                            }
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 资源化预警
     */
    public void resource(String dateStr){
        //1.查找预警设置
        List<DqPaltformResourceWarning> warningList = dqPaltformResourceWarningDao.findAll();
        if(warningList.size()==0){
            return;
        }
        DqPaltformResourceWarning warning = warningList.get(0);
        double resourceFailure = 0;
        double resourceUnArchive = 0;
        double resourceException = 0;
        //资源化数据
        String sql1 = "SELECT count(*) c,archive_status FROM json_archives/info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' and (archive_status=2 or archive_status=0) group by archive_status";
        try {
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            while (resultSet1.next()) {
                String archiveStatus = resultSet1.getString("archive_status");// 2失败，0未解析
                double total = resultSet1.getDouble("c");
                if("2".equals(archiveStatus)){
                    resourceFailure = total;
                }else {
                    resourceUnArchive = total;
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

        String sql2 = "SELECT count(*) c FROM json_archives/info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' and defect=1";
        try {
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            resultSet2.next();
            resourceUnArchive = resultSet2.getDouble("c");//资源化 解析异常
        }catch (Exception e){
            e.getMessage();
        }

        List<DqWarningRecord> list = new ArrayList<>(3);
        Date recordTime = DateUtil.formatCharDateYMD(dateStr);
        String unqualified = "不合格";
        String orgName = "医疗云平台";
        //1、失败数
        if(resourceFailure>warning.getFailureNum()){
            //失败数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecord.DqWarningRecordType.resource.getValue());
            record.setActualValue(resourceFailure+"");
            record.setId(getCode());
            record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.resourceFailureNum.getValue());
            record.setQuota(DqWarningRecord.DqWarningRecordWarningType.resourceFailureNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getFailureNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.resourceFailureNum.getName()+unqualified);
            list.add(record);
        }
        //2、质量问题数
        if(resourceException>warning.getErrorNum()){
            //质量问题数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecord.DqWarningRecordType.resource.getValue());
            record.setActualValue(resourceException+"");
            record.setId(getCode());
            record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.resourceErrorNum.getValue());
            record.setQuota(DqWarningRecord.DqWarningRecordWarningType.resourceErrorNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getErrorNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.resourceErrorNum.getName()+unqualified);
            list.add(record);
        }
        //3、未解析量
        if(resourceUnArchive>warning.getUnparsingNum()){
            //未解析量>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecord.DqWarningRecordType.resource.getValue());
            record.setActualValue(resourceUnArchive+"");
            record.setId(getCode());
            record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.unArchiveNum.getValue());
            record.setQuota(DqWarningRecord.DqWarningRecordWarningType.unArchiveNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getUnparsingNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.unArchiveNum.getName()+unqualified);
            list.add(record);
        }

        if(list.size()>0){
            dqWarningRecordDao.save(list);
        }
    }

    /**
     * 上传预警
     */
    public void upload(String dateStr){
        //1.查找预警设置
        List<DqPaltformUploadWarning> warningList = dqPaltformUploadWarningDao.findAll();
        if(warningList.size()==0){
            return;
        }
        DqPaltformUploadWarning warning = warningList.get(0);
        List<DqDatasetWarning> dtList =  dqDatasetWarningDao.findByType("2");
        double datasetNum = dtList.size();
        double uploadSuccessNum = 0;
        double uploadErrorNum = 0;
        double uploadDatasetNum = 0;
        double uploadArchiveNum = 0;

        //统计数据量和错误数
        String sql1 = "SELECT count(*) c,upload_status FROM upload/record where event_date>= '"+dateStr+" 00:00:00' AND event_date<='" +  dateStr + " 23:59:59' group by upload_status";
        try {
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            while (resultSet1.next()) {
                String status = resultSet1.getString("upload_status");// 0失败，1成功
                double total = resultSet1.getDouble("c");
                if("0".equals(status)){
                    uploadErrorNum = total;
                }else {
                    uploadSuccessNum = total;
                }
            }
            uploadArchiveNum = uploadErrorNum + uploadSuccessNum;
        }catch (Exception e){
            e.getMessage();
        }

        //数据集
        String sql2 = "SELECT datasets FROM upload/record where event_date>= '"+dateStr+" 00:00:00' AND event_date<='" +  dateStr + " 23:59:59' ";
        try {
            Map<String, String> datasetMap = new HashMap<>();
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            while (resultSet2.next()) {
                String datasets = resultSet2.getString("datasets");//接收 数据集
                JSONArray jsonArray = JSON.parseArray(datasets);
                for(int i=0;i<jsonArray.size();i++){
                    String dataset = jsonArray.get(i).toString();
                    if(!datasetMap.containsKey(dataset)){
                        datasetMap.put(dataset,dataset);
                    }
                }
            }

            uploadDatasetNum = datasetMap.size();
        }catch (Exception e){
            e.getMessage();
        }

        List<DqWarningRecord> list = new ArrayList<>(3);
        Date recordTime = DateUtil.formatCharDateYMD(dateStr);
        String unqualified = "不合格";
        String orgName = "省平台";
        //1、档案数
        if(uploadArchiveNum<warning.getAcrhiveNum()){
            //档案数<预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecord.DqWarningRecordType.upload.getValue());
            record.setActualValue(uploadArchiveNum+"");
            record.setId(getCode());
            record.setWarningType(archiveNum.getValue());
            record.setQuota(archiveNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getAcrhiveNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(archiveNum.getName()+unqualified);
            list.add(record);
        }
        //2、数据错误问题数
        if(uploadErrorNum>warning.getErrorNum()){
            //数据错误问题数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecord.DqWarningRecordType.upload.getValue());
            record.setActualValue(uploadErrorNum+"");
            record.setId(getCode());
            record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.dataErrorNum.getValue());
            record.setQuota(DqWarningRecord.DqWarningRecordWarningType.dataErrorNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getErrorNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.dataErrorNum.getName()+unqualified);
            list.add(record);
        }
        //3、数据集
        if(uploadDatasetNum > datasetNum){
            //数据错误问题数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecord.DqWarningRecordType.upload.getValue());
            record.setActualValue(uploadDatasetNum+"");
            record.setId(getCode());
            record.setWarningType(DqWarningRecord.DqWarningRecordWarningType.uploadDatasetNum.getValue());
            record.setQuota(DqWarningRecord.DqWarningRecordWarningType.uploadDatasetNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(datasetNum+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecord.DqWarningRecordWarningType.uploadDatasetNum.getName()+unqualified);
            list.add(record);
        }
        if(list.size()>0){
            dqWarningRecordDao.save(list);
        }
    }

    /**
     * 初始化datamap 数据
     * @return
     */
    private Map initDataMap(DqPaltformReceiveWarning defaultWarning){
        Map dataMap = new HashedMap();
        Integer defaultPe = defaultWarning.getPeInTime();
        Integer defaultOutpatient = defaultWarning.getOutpatientInTime();
        Integer defaultHospital = defaultWarning.getHospitalInTime();
        String defaultPeDateStr = DateUtil.formatDate(DateUtil.addDate(-(defaultPe+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String defaultOutpatientDateStr = DateUtil.formatDate(DateUtil.addDate(-(defaultOutpatient+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String defaultHospitalDateStr = DateUtil.formatDate(DateUtil.addDate(-(defaultHospital+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        dataMap.put("receiveArchives",0);//接收档案数
        dataMap.put("receiveDataset",0); //接收数据集
        dataMap.put("receiveException",0);//接收异常
        dataMap.put("totalOutpatient",0);//门诊总数
        dataMap.put("totalPe",0);//体检总数
        dataMap.put("totalHospital",0);//住院总数
        dataMap.put("outpatientIntime",0);//门诊及时数
        dataMap.put("peIntime",0);//体检及时数
        dataMap.put("hospitalIntime",0);//住院及时数
//        dataMap.put("outpatientIntimeRate",0);//门诊及时率
//        dataMap.put("peIntimeRate",0);//体检及时率
//        dataMap.put("hospitalIntimeRate",0);//住院及时率
        dataMap.put("outpatientReceiveTime",defaultOutpatientDateStr);//门诊接收时间
        dataMap.put("peReceiveTime",defaultPeDateStr);//体检接收时间
        dataMap.put("hospitalReceiveTime",defaultHospitalDateStr);//住院接收时间
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
                re = warning.getOutpatientInTime() < delay;
                break;
            case "1":
                //1住院
                re = warning.getHospitalInTime() < delay;
                break;
            case "2":
                //2体检
                re = warning.getPeInTime() < delay;
                break;
            default:
                break;
        }

        return re;
    }
}
