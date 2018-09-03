package com.yihu.ehr.analyze.service.dataQuality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yihu.ehr.analyze.dao.*;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.*;
import com.yihu.ehr.profile.qualilty.DqWarningRecordType;
import com.yihu.ehr.profile.qualilty.DqWarningRecordWarningType;
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
import java.util.*;

import static com.yihu.ehr.profile.qualilty.DqWarningRecordWarningType.errorNum;

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
    @Value("${quality.cloud}")
    private String cloud;

    /**
     * 预警问题生成分析
     */
    public void analyze(String date){
        String dateStr = null;
        if(StringUtils.isBlank(date)){
            dateStr = DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        }else {
            dateStr = date;
        }
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
        Query query1 = session.createSQLQuery("SELECT org_code,full_name from organizations");
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
        //及时率
        inTimeWarning(warningMap,dataMap);

        //3.预警
        List<DqWarningRecord> list = new ArrayList<>();
        Date recordTime = DateUtil.formatCharDateYMD(dateStr);
        String unqualified = "不合格";
        for (String orgCode : warningMap.keySet()) {
            if(defaultOrgCode.equals(orgCode)){
                continue;
            }
            try {
                String orgName = orgMap.get(orgCode);
                String id = DateUtil.getCurrentString(DateUtil.DEFAULT_CHAR_DATE_YMD_FORMAT)+"_"+ dateStr +"_"+orgCode+"_";
                DqPaltformReceiveWarning warning = warningMap.get(orgCode);
                Map<String, Object> hospitalMap = dataMap.get(orgCode);
                //统计接收档案数据
                String sql1 = "SELECT count(*) c FROM json_archives/info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59'  AND pack_type=1 and org_code='"+orgCode+"'";
                try {
                    ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                    resultSet1.next();
                    double total = resultSet1.getDouble("c");//接收 档案数
                    hospitalMap.put("receiveArchives",total);
                }catch (Exception e){
                    e.getMessage();
                }
                //统计质量异常
                String sql2 = "SELECT count(*) c FROM json_archives_qc/qc_metadata_info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' and qc_step=1 and org_code='"+orgCode+"'";
                try {
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    resultSet2.next();
                    double total = resultSet2.getDouble("c");//接收 质量异常
                    hospitalMap.put("receiveException",total);
                }catch (Exception e){
                    e.getMessage();
                }

                //数据集
                String sql3 = "SELECT distinct details FROM json_archives_qc/qc_dataset_info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' and qc_step=1 and org_code='"+orgCode+"' ";
                try {
                    ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
                    Map<String, Object> datasetMap1 = new HashMap<>();
                    while (resultSet3.next()) {
                        String details = resultSet3.getString("details");//接收 数据集
                        JSONArray jsonArray = JSON.parseArray(details);
                        for(int i=0;i<jsonArray.size();i++){
                            String dataset = jsonArray.get(i).toString();
                            if(!datasetMap1.containsKey(dataset)){
                                datasetMap1.put(dataset,dataMap);
                            }
                        }
                    }
                    hospitalMap.put("receiveDataset",datasetMap1.size());//数据集个数
                }catch (Exception e){
                    e.getMessage();
                }

                if(hospitalMap==null){
                    //该医院没有上传数据
                    DqWarningRecord record1 = new DqWarningRecord();
                    record1.setOrgCode(orgCode);
                    record1.setType(DqWarningRecordType.receive.getValue());
                    record1.setActualValue("0");
                    record1.setId(id+DqWarningRecordWarningType.archives.getValue());
                    record1.setOrgName(orgName);
                    record1.setWarningType(DqWarningRecordWarningType.archives.getValue());
                    record1.setQuota(DqWarningRecordWarningType.archives.getName());
                    record1.setRecordTime(recordTime);
                    record1.setWarningTime(new Date());
                    record1.setWarningValue(warning.getArchiveNum()+"");
                    record1.setStatus("1");
                    record1.setProblemDescription(DqWarningRecordWarningType.archives.getName()+unqualified);
                    list.add(record1);

                    DqWarningRecord record2 = new DqWarningRecord();
                    record2.setOrgCode(orgCode);
                    record2.setType(DqWarningRecordType.receive.getValue());
                    record2.setActualValue("0");
                    record2.setId(id+DqWarningRecordWarningType.errorNum.getValue());
                    record2.setOrgName(orgName);
                    record2.setWarningType(DqWarningRecordWarningType.errorNum.getValue());
                    record2.setQuota(DqWarningRecordWarningType.errorNum.getName());
                    record2.setRecordTime(recordTime);
                    record2.setWarningTime(new Date());
                    record2.setWarningValue(warning.getErrorNum()+"");
                    record2.setStatus("1");
                    record2.setProblemDescription(errorNum.getName()+unqualified);
                    list.add(record2);

                    DqWarningRecord record3 = new DqWarningRecord();
                    record3.setOrgCode(orgCode);
                    record3.setType(DqWarningRecordType.receive.getValue());
                    record3.setActualValue("0");
                    record3.setId(id+DqWarningRecordWarningType.datasetWarningNum.getValue());
                    record3.setOrgName(orgName);
                    record3.setWarningType(DqWarningRecordWarningType.datasetWarningNum.getValue());
                    record3.setQuota(DqWarningRecordWarningType.datasetWarningNum.getName());
                    record3.setRecordTime(recordTime);
                    record3.setWarningTime(new Date());
                    record3.setWarningValue(warning.getDatasetWarningNum()+"");
                    record3.setStatus("1");
                    record3.setProblemDescription(DqWarningRecordWarningType.datasetWarningNum.getName()+unqualified);
                    list.add(record3);

                    DqWarningRecord record4 = new DqWarningRecord();
                    record4.setOrgCode(orgCode);
                    record4.setType(DqWarningRecordType.receive.getValue());
                    record4.setActualValue("0");
                    record4.setId(id+DqWarningRecordWarningType.outpatientInTimeRate.getValue());
                    record4.setOrgName(orgName);
                    record4.setWarningType(DqWarningRecordWarningType.outpatientInTimeRate.getValue());
                    record4.setQuota(DqWarningRecordWarningType.outpatientInTimeRate.getName());
                    record4.setRecordTime(recordTime);
                    record4.setWarningTime(new Date());
                    record4.setWarningValue(warning.getOutpatientInTimeRate()+"%");
                    record4.setStatus("1");
                    String description4 = "就诊日期为："+dateStr+"的"+
                            DqWarningRecordWarningType.outpatientInTimeRate.getName()+unqualified;
                    record4.setProblemDescription(description4);
                    list.add(record4);

                    DqWarningRecord record5 = new DqWarningRecord();
                    record5.setOrgCode(orgCode);
                    record5.setType(DqWarningRecordType.receive.getValue());
                    record5.setActualValue("0");
                    record5.setId(id+DqWarningRecordWarningType.hospitalInTimeRate.getValue());
                    record5.setOrgName(orgName);
                    record5.setWarningType(DqWarningRecordWarningType.hospitalInTimeRate.getValue());
                    record5.setQuota(DqWarningRecordWarningType.hospitalInTimeRate.getName());
                    record5.setRecordTime(recordTime);
                    record5.setWarningTime(new Date());
                    record5.setWarningValue(warning.getHospitalInTimeRate()+"%");
                    record5.setStatus("1");
                    String description5 = "就诊日期为："+dateStr+"的"+
                            DqWarningRecordWarningType.hospitalInTimeRate.getName()+unqualified;
                    record5.setProblemDescription(description5);
                    list.add(record5);

                    DqWarningRecord record6 = new DqWarningRecord();
                    record6.setOrgCode(orgCode);
                    record6.setType(DqWarningRecordType.receive.getValue());
                    record6.setActualValue("0");
                    record6.setId(id+DqWarningRecordWarningType.peInTimeRate.getValue());
                    record6.setOrgName(orgName);
                    record6.setWarningType(DqWarningRecordWarningType.peInTimeRate.getValue());
                    record6.setQuota(DqWarningRecordWarningType.peInTimeRate.getName());
                    record6.setRecordTime(recordTime);
                    record6.setWarningTime(new Date());
                    record6.setWarningValue(warning.getPeInTimeRate()+"%");
                    record6.setStatus("1");
                    String description6 = "就诊日期为："+dateStr+"的"+
                            DqWarningRecordWarningType.peInTimeRate.getName()+unqualified;
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
                    record.setType(DqWarningRecordType.receive.getValue());
                    record.setActualValue(archiveNum+"");
                    record.setId(id+DqWarningRecordWarningType.archives.getValue());
                    record.setOrgName(orgName);
                    record.setWarningType(DqWarningRecordWarningType.archives.getValue());
                    record.setQuota(DqWarningRecordWarningType.archives.getName());
                    record.setRecordTime(recordTime);
                    record.setWarningTime(new Date());
                    record.setWarningValue(warning.getArchiveNum()+"");
                    record.setStatus("1");
                    record.setProblemDescription(DqWarningRecordWarningType.archives.getName()+unqualified);
                    list.add(record);
                }

                //2、质量异常问题数
                Long errorNum = Long.valueOf(hospitalMap.get("receiveException").toString());
                if(errorNum>warning.getErrorNum()){
                    //接收的质量异常问题数大于预警值
                    DqWarningRecord record = new DqWarningRecord();
                    record.setOrgCode(orgCode);
                    record.setType(DqWarningRecordType.receive.getValue());
                    record.setActualValue(errorNum+"");
                    record.setId(id+DqWarningRecordWarningType.errorNum.getValue());
                    record.setOrgName(orgName);
                    record.setWarningType(DqWarningRecordWarningType.errorNum.getValue());
                    record.setQuota(DqWarningRecordWarningType.errorNum.getName());
                    record.setRecordTime(recordTime);
                    record.setWarningTime(new Date());
                    record.setWarningValue(warning.getErrorNum()+"");
                    record.setStatus("1");
                    record.setProblemDescription(DqWarningRecordWarningType.errorNum.getName()+unqualified);
                    list.add(record);
                }
                //3、数据集
                Long datasetNum = Long.valueOf(hospitalMap.get("receiveDataset").toString());
                if(warning.getDatasetWarningList()!=null&&datasetNum<warning.getDatasetWarningList().size()){
                    //接收的数据集小于预警值
                    DqWarningRecord record = new DqWarningRecord();
                    record.setOrgCode(orgCode);
                    record.setType(DqWarningRecordType.receive.getValue());
                    record.setActualValue(datasetNum+"");
                    record.setId(id+DqWarningRecordWarningType.datasetWarningNum.getValue());
                    record.setOrgName(orgName);
                    record.setWarningType(DqWarningRecordWarningType.datasetWarningNum.getValue());
                    record.setQuota(DqWarningRecordWarningType.datasetWarningNum.getName());
                    record.setRecordTime(recordTime);
                    record.setWarningTime(new Date());
                    record.setWarningValue(warning.getDatasetWarningList().size()+"");
                    record.setStatus("1");
                    record.setProblemDescription(DqWarningRecordWarningType.datasetWarningNum.getName()+unqualified);
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
                        record.setType(DqWarningRecordType.receive.getValue());
                        record.setActualValue(outpatientRate);
                        record.setId(id+DqWarningRecordWarningType.outpatientInTimeRate.getValue());
                        record.setOrgName(orgName);
                        record.setWarningType(DqWarningRecordWarningType.outpatientInTimeRate.getValue());
                        record.setQuota(DqWarningRecordWarningType.outpatientInTimeRate.getName());
                        record.setRecordTime(recordTime);
                        record.setWarningTime(new Date());
                        record.setWarningValue(warning.getOutpatientInTimeRate()+"%");
                        record.setStatus("1");
                        String description = "就诊日期为："+hospitalMap.get("outpatientReceiveTime")+"的"+
                                DqWarningRecordWarningType.outpatientInTimeRate.getName()+unqualified;
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
                        record.setType(DqWarningRecordType.receive.getValue());
                        record.setActualValue(hospitalRate);
                        record.setId(id+DqWarningRecordWarningType.hospitalInTimeRate.getValue());
                        record.setOrgName(orgName);
                        record.setWarningType(DqWarningRecordWarningType.hospitalInTimeRate.getValue());
                        record.setQuota(DqWarningRecordWarningType.hospitalInTimeRate.getName());
                        record.setRecordTime(recordTime);
                        record.setWarningTime(new Date());
                        record.setWarningValue(warning.getHospitalInTimeRate()+"%");
                        record.setStatus("1");
                        String description = "就诊日期为："+hospitalMap.get("hospitalReceiveTime")+"的"+
                                DqWarningRecordWarningType.hospitalInTimeRate.getName()+unqualified;
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
                        record.setType(DqWarningRecordType.receive.getValue());
                        record.setActualValue(peRate);
                        record.setId(id+DqWarningRecordWarningType.peInTimeRate.getValue());
                        record.setOrgName(orgName);
                        record.setWarningType(DqWarningRecordWarningType.peInTimeRate.getValue());
                        record.setQuota(DqWarningRecordWarningType.peInTimeRate.getName());
                        record.setRecordTime(recordTime);
                        record.setWarningTime(new Date());
                        record.setWarningValue(warning.getPeInTimeRate()+"%");
                        record.setStatus("1");
                        String description = "就诊日期为："+hospitalMap.get("peReceiveTime")+"的"+
                                DqWarningRecordWarningType.peInTimeRate.getName()+unqualified;
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
        //统计
        for (String orgCode : warningMap.keySet()) {
            try {
                if(defaultOrgCode.equals(orgCode)){
                    continue;
                }
                Map<String, Object> map = null;
                DqPaltformReceiveWarning warning = warningMap.get(orgCode);
                map = initDataMap(warning);
                String peDateStr = DateUtil.formatDate(DateUtil.addDate(-(warning.getPeInTime()+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                String hospitalDateStr = DateUtil.formatDate(DateUtil.addDate(-(warning.getHospitalInTime()+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                String outpatientDateStr = DateUtil.formatDate(DateUtil.addDate(-(warning.getOutpatientInTime()+1), new Date()),DateUtil.DEFAULT_DATE_YMD_FORMAT);

                //体检
                try {
                    String sql1 = "SELECT sum(HSI07_01_004) s3 FROM qc/daily_report where event_date>= '"+peDateStr+"T00:00:00' AND event_date <='" +  peDateStr + "T23:59:59' and org_code = '"+orgCode+"' ";
                    ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                    resultSet1.next();
                    double HSI07_01_004 = resultSet1.getDouble("s3");//体检
                    map.put("totalPe",HSI07_01_004);
                    map.put("peReceiveTime",peDateStr);
                }catch (Exception e){
                    if(!"Error".equals(e.getMessage())){
                        e.printStackTrace();
                    }
                }
                try {
                    String sql2 = "SELECT count(distinct event_no) c FROM json_archives/info where event_date>= '"+peDateStr+" 00:00:00' AND event_date<='" +  peDateStr + " 23:59:59' and org_code = '"+orgCode+"' AND pack_type=1 and event_type=2 and delay <="+warning.getPeInTime();
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    resultSet2.next();
                    double total = resultSet2.getDouble("c");//及时数
                    map.put("peInTime",total);
                    map.put("peReceiveTime",peDateStr);
                }catch (Exception e){
                    if(!"Error".equals(e.getMessage())){
                        e.printStackTrace();
                    }
                }

                //门诊
                try {
                    String sql1 = "SELECT sum(HSI07_01_002) s2 FROM qc/daily_report where event_date>= '"+outpatientDateStr+"T00:00:00' AND event_date <='"+outpatientDateStr+"T23:59:59' and org_code = '"+orgCode+"' ";
                    ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                    resultSet1.next();
                    double HSI07_01_002 = resultSet1.getDouble("s4");//门急诊
                    map.put("totalOutpatient",HSI07_01_002);
                    map.put("outpatientReceiveTime",outpatientDateStr);
                }catch (Exception e){
                    if(!"Error".equals(e.getMessage())){
                        e.printStackTrace();
                    }
                }

                try {
                    String sql2 = "SELECT count(distinct event_no) c FROM json_archives/info where event_date>= '"+outpatientDateStr+" 00:00:00' AND event_date<='" +  outpatientDateStr + " 23:59:59' and org_code = '"+orgCode+"' AND pack_type=1 and event_type=0 and delay <="+warning.getOutpatientInTime();
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    resultSet2.next();
                    double total = resultSet2.getDouble("c");//及时数
                    map.put("outpatientInTime",total);
                    map.put("outpatientReceiveTime",outpatientDateStr);
                }catch (Exception e){
                    if(!"Error".equals(e.getMessage())){
                        e.printStackTrace();
                    }
                }

                //住院
                String sql1 = "SELECT sum(HSI07_01_012) s4 FROM qc/daily_report where event_date>= '"+hospitalDateStr+"T00:00:00' AND event_date <='"+hospitalDateStr+"T23:59:59' and org_code = '"+orgCode+"' ";
                try {
                    ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
                    resultSet1.next();
                    double HSI07_01_012 = resultSet1.getDouble("s4");//住院
                    map.put("totalHospital",HSI07_01_012);
                    map.put("hospitalReceiveTime",hospitalDateStr);
                }catch (Exception e){
                    if(!"Error".equals(e.getMessage())){
                        e.printStackTrace();
                    }
                }

                String sql2 = "SELECT count(distinct event_no) c FROM json_archives/info where event_date>= '"+hospitalDateStr+" 00:00:00' AND event_date<='" +  hospitalDateStr + " 23:59:59' and org_code = '"+orgCode+"' AND pack_type=1 and event_type=1 and delay <="+warning.getHospitalInTime();

                try {
                    ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
                    resultSet2.next();
                    double total = resultSet2.getDouble("c");//及时数
                    map.put("hospitalInTime",total);
                    map.put("hospitalReceiveTime",hospitalDateStr);
                }catch (Exception e){
                    if(!"Error".equals(e.getMessage())){
                        e.printStackTrace();
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
        String sql1 = "SELECT count(*) c,archive_status FROM json_archives/info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' AND pack_type=1 and (archive_status=2 or archive_status=0) group by archive_status";
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

        String sql2 = "SELECT count(*) c FROM json_archives_qc/qc_metadata_info where receive_date>= '"+dateStr+" 00:00:00' AND receive_date<='" +  dateStr + " 23:59:59' AND qc_step=2 ";
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
        String id = DateUtil.getCurrentString(DateUtil.DEFAULT_CHAR_DATE_YMD_FORMAT)+"_"+ dateStr +"_"+cloud+"_";
        //1、失败数
        if(resourceFailure>warning.getFailureNum()){
            //失败数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecordType.resource.getValue());
            record.setActualValue(resourceFailure+"");
            record.setId(id+DqWarningRecordWarningType.resourceFailureNum.getValue());
            record.setWarningType(DqWarningRecordWarningType.resourceFailureNum.getValue());
            record.setQuota(DqWarningRecordWarningType.resourceFailureNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getFailureNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecordWarningType.resourceFailureNum.getName()+unqualified);
            list.add(record);
        }
        //2、质量问题数
        if(resourceException>warning.getErrorNum()){
            //质量问题数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecordType.resource.getValue());
            record.setActualValue(resourceException+"");
            record.setId(id+DqWarningRecordWarningType.resourceErrorNum.getValue());
            record.setWarningType(DqWarningRecordWarningType.resourceErrorNum.getValue());
            record.setQuota(DqWarningRecordWarningType.resourceErrorNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getErrorNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecordWarningType.resourceErrorNum.getName()+unqualified);
            list.add(record);
        }
        //3、未解析量
        if(resourceUnArchive>warning.getUnparsingNum()){
            //未解析量>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecordType.resource.getValue());
            record.setActualValue(resourceUnArchive+"");
            record.setId(id+DqWarningRecordWarningType.unArchiveNum.getValue());
            record.setWarningType(DqWarningRecordWarningType.unArchiveNum.getValue());
            record.setQuota(DqWarningRecordWarningType.unArchiveNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getUnparsingNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setProblemDescription(DqWarningRecordWarningType.unArchiveNum.getName()+unqualified);
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
                if("1".equals(status)){
                    uploadSuccessNum = total;
                }else {
                    uploadErrorNum = total;
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
        String orgName = warning.getOrgName();
        String orgCode = warning.getOrgCode();
        String id = DateUtil.getCurrentString(DateUtil.DEFAULT_CHAR_DATE_YMD_FORMAT)+"_"+ dateStr +"_"+orgCode+"_";
        //1、档案数
        if(uploadArchiveNum<warning.getArchiveNum()){
            //档案数<预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecordType.upload.getValue());
            record.setActualValue(uploadArchiveNum+"");
            record.setId(id+DqWarningRecordWarningType.archiveNum.getValue());
            record.setWarningType(DqWarningRecordWarningType.archiveNum.getValue());
            record.setQuota(DqWarningRecordWarningType.archiveNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getArchiveNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setOrgCode(orgCode);
            record.setProblemDescription(DqWarningRecordWarningType.archiveNum.getName()+unqualified);
            list.add(record);
        }
        //2、数据错误问题数
        if(uploadErrorNum>warning.getErrorNum()){
            //数据错误问题数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecordType.upload.getValue());
            record.setActualValue(uploadErrorNum+"");
            record.setId(id+DqWarningRecordWarningType.dataErrorNum.getValue());
            record.setWarningType(DqWarningRecordWarningType.dataErrorNum.getValue());
            record.setQuota(DqWarningRecordWarningType.dataErrorNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(warning.getErrorNum()+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setOrgCode(orgCode);
            record.setProblemDescription(DqWarningRecordWarningType.dataErrorNum.getName()+unqualified);
            list.add(record);
        }
        //3、数据集
        if(uploadDatasetNum > datasetNum){
            //数据错误问题数>预警值
            DqWarningRecord record = new DqWarningRecord();
            record.setType(DqWarningRecordType.upload.getValue());
            record.setActualValue(uploadDatasetNum+"");
            record.setId(id+DqWarningRecordWarningType.uploadDatasetNum.getValue());
            record.setWarningType(DqWarningRecordWarningType.uploadDatasetNum.getValue());
            record.setQuota(DqWarningRecordWarningType.uploadDatasetNum.getName());
            record.setRecordTime(recordTime);
            record.setWarningTime(new Date());
            record.setWarningValue(datasetNum+"");
            record.setStatus("1");
            record.setOrgName(orgName);
            record.setOrgCode(orgCode);
            record.setProblemDescription(DqWarningRecordWarningType.uploadDatasetNum.getName()+unqualified);
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
}
