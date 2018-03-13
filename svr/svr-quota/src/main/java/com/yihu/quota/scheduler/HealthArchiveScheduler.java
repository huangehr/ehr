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
import com.yihu.quota.util.LatitudeUtils;
import com.yihu.quota.vo.CheckInfoModel;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.HealthArchiveInfoModel;
import com.yihu.quota.vo.PersonalInfoModel;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
    private ExtractUtil extractUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiabetesScheduler diabetesScheduler;

    @Scheduled(cron = "0 0 2 * * ?")
    public void validatorIdentityScheduler() throws Exception{

        String q =  ""; // 查询条件
        String fq = ""; // 过滤条件
        String sql = "SELECT id as town, name as townName from address_dict where pid = '361100';";
        String keyEventDate = "event_date";
        String keyArea = "EHR_001225";  // 行政区划代码
        String keyAreaName = "EHR_001225_VALUE";
        String keyPatientName = "patient_name";
        String keySex = "EHR_000019";   // 性别
        String keySexValue = "EHR_000019_VALUE";
        String keyAge = "EHR_000007";   // 出生日期
        List<HealthArchiveInfoModel> healthArchiveInfoModelList = new ArrayList<>();
        Map<String, String> dictDiseaseMap = new HashMap<>();
        Map<String, Integer> max = new HashMap<>();

        BasesicUtil basesicUtil = new BasesicUtil();
        String initializeDate = "2018-03-09";
        Date now = new Date();
        String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        if(basesicUtil.compareDate(initializeDate,nowDate) == -1){//  当前时间大于初始化时间，就所有数据初始化，当前时间小于于初始时间每天抽取
            Date yesterdayDate = DateUtils.addDays(now,-1);
            String yesterday = DateUtil.formatDate(yesterdayDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
            fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
        }else{
            fq = "event_date:[* TO *]";
        }

        List<DictModel> dictDatas = jdbcTemplate.query(sql, new BeanPropertyRowMapper(DictModel.class));
        if (null != dictDatas && dictDatas.size() > 0) {
            for (int i = 0; i < dictDatas.size(); i++) {
                for (int j = 1; j <= 2; j++) {
                    for (int k = 1; k <= 5; k++) {
                        max.put(dictDatas.get(i).getCode() + "-" + j + "-" + k, 0);
                    }
                }
            }
        }

		// 找出就诊档案数
        long count = solrUtil.count(ResourceCore.MasterTable, q,fq);
        List<String> rowKeyList = diabetesScheduler.selectSubRowKey(ResourceCore.MasterTable, q, fq, count);
        if(rowKeyList != null && rowKeyList.size() > 0){
            List<Map<String,Object>> hbaseDataList = diabetesScheduler.selectHbaseData(rowKeyList);
            if( hbaseDataList != null && hbaseDataList.size() > 0 ){
                for(Map<String,Object> map : hbaseDataList){
                    // 档案信息 > 姓名等
                    HealthArchiveInfoModel healthArchiveInfoModel = new HealthArchiveInfoModel();
                    healthArchiveInfoModel.setCreateTime(new Date());
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
                    healthArchiveInfoModelList.add(healthArchiveInfoModel);
                }
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                // 保存到ES库
                String index = "";
                String type = "";
                for(HealthArchiveInfoModel archiveInfo : healthArchiveInfoModelList){
                    index = "X";
                    type = "XX";
                    Map<String, Object> source = new HashMap<>();
                    String jsonPer = objectMapper.writeValueAsString(archiveInfo);
                    source = objectMapper.readValue(jsonPer, Map.class);
                    elasticSearchClient.index(index,type, source);
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
}
