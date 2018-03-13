package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.HealthArchiveInfoModel;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by wxw on 2018/3/12.
 */
@Component
public class HealthArchiveScheduler {
    private static final Logger log = LoggerFactory.getLogger(HealthArchiveScheduler.class);

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void validatorIdentityScheduler() throws Exception{

        String q =  ""; // 查询条件
        String fq = ""; // 过滤条件
        String keyEventDate = "event_date";
        String keyEventType = "event_type"; // 就诊类型
        String keyArea = "EHR_001225";  // 行政区划代码
        String keyDemographicId = "demographic_id"; // 身份证
        String keyCardId = "card_id	";
        String keyHealthProblem = "health_problem"; // 健康问题
        String keyPatientName = "patient_name"; // 患者姓名
        String keySex = "EHR_000019";   // 性别
        String keySexValue = "EHR_000019_VALUE";
        String keyAge = "EHR_000007";   // 出生日期
        String keyOrgCode = "org_code"; // 机构编码
        String keyOrgName = "org_name";
        String keyAddress = "EHR_001211"; //地址 EHR_001227
        List<HealthArchiveInfoModel> healthArchiveInfoModelList = new ArrayList<>();

        BasesicUtil basesicUtil = new BasesicUtil();
        String initializeDate = "2018-03-13";
        Date now = new Date();
        String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        boolean flag = true;
        String startDate = "2015-01-01";
        String endDate = "2015-02-01";
        while (flag) {
            // 当前时间大于初始化时间，就所有数据初始化，每个月递增查询，当前时间小于于初始时间每天抽取
            if(basesicUtil.compareDate(initializeDate,nowDate) == -1){
                Date yesterdayDate = DateUtils.addDays(now,-1);
                String yesterday = DateUtil.formatDate(yesterdayDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
                fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
                flag = false;
            }else{
                fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T00:00:00Z]";
                Date sDate = DateUtils.addMonths(DateUtil.parseDate(startDate,DateUtil.DEFAULT_DATE_YMD_FORMAT),1);
                startDate = DateUtil.formatDate(sDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
                Date eDate = DateUtils.addMonths(DateUtil.parseDate(startDate,DateUtil.DEFAULT_DATE_YMD_FORMAT),1);
                endDate = DateUtil.formatDate(eDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
                if(startDate.equals("2018-04-01")){
                    flag = false;
                }
                log.debug("startDate = " + startDate);
            }

            // 找出就诊档案数
            long count = solrUtil.count(ResourceCore.MasterTable, q, fq);
            List<String> rowKeyList = this.selectSubRowKey(ResourceCore.MasterTable, q, fq, count);
            if(rowKeyList != null && rowKeyList.size() > 0){
                List<Map<String,Object>> hbaseDataList = this.selectHbaseData(rowKeyList);
                if( hbaseDataList != null && hbaseDataList.size() > 0 ){
                    for(Map<String,Object> map : hbaseDataList){
                        // 档案信息 > 姓名等
                        HealthArchiveInfoModel healthArchiveInfoModel = new HealthArchiveInfoModel();
                        healthArchiveInfoModel.setCreateTime(new Date());
                        if(map.get(keyDemographicId) != null){
                            healthArchiveInfoModel.setDemographicId(map.get(keyDemographicId).toString());
                        }
                        if(map.get(keyCardId) != null){
                            healthArchiveInfoModel.setCardId(map.get(keyCardId).toString());
                        }
                        if(map.get(keyEventDate) != null){
                            healthArchiveInfoModel.setEventDate(DateUtil.formatCharDate(map.get(keyEventDate).toString(), DateUtil.DATE_WORLD_FORMAT));					}
                        if(map.get(keyArea) != null){
                            healthArchiveInfoModel.setTown(map.get(keyArea).toString());
                        }
                        if(map.get(keyPatientName) != null){
                            healthArchiveInfoModel.setName(map.get(keyPatientName).toString());
                        }
                        if(map.get(keySex) != null){
                            healthArchiveInfoModel.setSex(Integer.valueOf(map.get(keySex).toString()));
                            healthArchiveInfoModel.setSexName(map.get(keySexValue).toString());
                        }
                        if(map.get(keyAge) != null){
                            String birthday = map.get(keyAge).toString().substring(0, 10);
                            // 计算年龄
                            int age = getAgeByBirth(birthday);
                            healthArchiveInfoModel.setAgeCode(exchangeCodeByAge(age));
                            healthArchiveInfoModel.setAgeName(exchangeNameByAge(age));
                        }
                        if (map.get(keyEventType) != null) {
                            healthArchiveInfoModel.setEventType(map.get(keyEventType).toString());
                        }
                        if (map.get(keyAddress) != null) {
                            healthArchiveInfoModel.setAddress(map.get(keyAddress).toString());
                        }
                        if (map.get(keyHealthProblem) != null) {
                            healthArchiveInfoModel.setHealthProblem(map.get(keyHealthProblem).toString());
                        }
                        if (map.get(keyOrgCode) != null) {
                            healthArchiveInfoModel.setOrgCode(map.get(keyOrgCode).toString());
                        }
                        if (map.get(keyOrgName) != null) {
                            healthArchiveInfoModel.setOrgName(map.get(keyOrgName).toString());
                        }
                        healthArchiveInfoModel.setResult(1);
                        healthArchiveInfoModelList.add(healthArchiveInfoModel);
                    }
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    // 保存到ES库
                    String index = "health_archive_index";
                    String type = "archive_info";
                    for(HealthArchiveInfoModel archiveInfo : healthArchiveInfoModelList){
                        Map<String, Object> source = new HashMap<>();
                        String jsonPer = objectMapper.writeValueAsString(archiveInfo);
                        source = objectMapper.readValue(jsonPer, Map.class);
                        if(null != archiveInfo.getDemographicId()) {
                            List<Map<String, Object>> list = elasticSearchUtil.findByField(index, type, "demographicId", archiveInfo.getDemographicId());
                            if(list == null || list.size() == 0){
                                elasticSearchClient.index(index, type, source);
                            }
                        }else if(null != archiveInfo.getCardId()) {
                            List<Map<String, Object>> list = elasticSearchUtil.findByField(index, type, "cardId",archiveInfo.getCardId());
                            if(list == null || list.size() == 0){
                                elasticSearchClient.index(index, type, source);
                            }
                        }else {
                            elasticSearchClient.index(index, type, source);
                        }
                    }
                }
            }
        }
    }

    /**
     * 计算年龄
     * @param birthInfo
     * @return
     */
    private int getAgeByBirth(String birthInfo) {
        Date birthday = DateUtil.strToDate(birthInfo);
        int age = 0;
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());// 当前时间

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);

        if (birth.after(now)) {
            // 如果传入的时间，在当前时间的后面，返回0岁
            age = 0;
        } else {
            age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            int system = now.get(Calendar.DAY_OF_YEAR);
            int user = birth.get(Calendar.DAY_OF_YEAR);
            if (system < user) {
                age -= 1;
            }
        }
        return age;
    }

    /**
     * 按年龄返回所在年龄段编码
     * @param age
     * @return
     */
    private int exchangeCodeByAge(int age) {
        int code = 1;
        if (age >= 7 && age < 18) {
            code = 2;
        } else if (age >= 18 && age < 41) {
            code = 3;
        } else if (age >= 41 && age < 66) {
            code = 4;
        } else if (age >= 66) {
            code = 5;
        }
        return code;
    }

    /**
     * 按年龄返回所在年龄段
     * @param age
     * @return
     */
    private String exchangeNameByAge(int age) {
        String name = "0-6岁";
        if (age >= 7 && age < 18) {
            name = "7-17岁";
        } else if (age >= 18 && age < 41) {
            name = "18-40岁";
        } else if (age >= 41 && age < 66) {
            name = "41-65岁";
        } else if (age >= 66) {
            name = "65岁以上";
        }
        return name;
    }

    // 获取查询结果中的rowKey
    private List<String> selectSubRowKey(String core ,String q,String fq,long count) throws Exception {
        List<String> data = new ArrayList<>();
        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 1,count);
        if(solrList!=null && solrList.getNumFound()>0){
            for (SolrDocument doc : solrList){
                String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
                data.add(rowkey);
            }
        }
        return  data;
    }

    //查询habase里面数据
    private List<Map<String,Object>> selectHbaseData(List<String> list) throws Exception {
        List<Map<String,Object>> data = new ArrayList<>();
        /***** Hbase查询 ********/
        Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable,list, "", ""); // hbase结果集
        if(resultList != null && resultList.length > 0){
            for (Result result :resultList) {
                Map<String,Object> obj = resultToMap(result, "");
                if(obj!=null) {
                    data.add(obj);
                }
            }
        }
        return  data;
    }

    /**
     * Result 转 JSON
     * @return
     */
    private Map<String,Object> resultToMap(Result result,String fl){
        String rowkey = Bytes.toString(result.getRow());
        if(rowkey!=null && rowkey.length()>0){
            Map<String,Object> obj = new HashMap<>();
            obj.put("rowkey", rowkey);
            for  (Cell cell : result.rawCells()) {
                String fieldName = Bytes.toString(CellUtil.cloneQualifier(cell));
                String fieldValue = Bytes.toString(CellUtil. cloneValue(cell));
                if(fl!=null&&!fl.equals("")&&!fl.equals("*")){
                    String[] fileds = fl.split(",");
                    for(String filed : fileds){
                        if(filed.equals(fieldName)){
                            obj.put(fieldName, fieldValue);
                            continue;
                        }
                    }
                }else{
                    obj.put(fieldName, fieldValue);
                }
            }
            return obj;
        }else{
            return null;
        }
    }
}
