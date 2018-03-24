package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.service.scheduler.HealthArchiveSchedulerService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.OutPatientCostModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by wxw on 2018/3/24.
 */
@Component
public class OutPatientCostScheduler {
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;

    private static final Logger log = LoggerFactory.getLogger(HealthArchiveScheduler.class);

    @Scheduled(cron = "0 51 17 * * ?")
    public void validatorIdentityScheduler() throws Exception{
        String q =  "EHR_000109:*"; // 查询条件 诊断代码（门诊）
        String sq = "";
        String sq2 = "";
        String q2 = "EHR_000081:* AND event_type:0";   // 科室

        String fq = ""; // 过滤条件
        String keyEventDate = "event_date";
        String keyArea = "org_area";  // 行政区划代码  EHR_001225
        String keyDisease = "EHR_000109";  // 诊断代码（门诊）
        String keyDiseaseName = "EHR_000112";  // 诊断名称（门诊）
        String keyCost = "EHR_000045";  // 门诊费用金额（元）
        String keyDept = "EHR_000081";
        String keyDeptName = "EHR_000082";
        String keyProfileId = "rowkey";
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        BasesicUtil basesicUtil = new BasesicUtil();
        String initializeDate = "2018-03-25";
        Date now = new Date();
        String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        boolean flag = true;
        String startDate = "2017-01-01";
        String endDate = "2017-02-01";
        List<String> profileIdList = new ArrayList<>() ;

        while(flag) {
            profileIdList.clear();
            //  当前时间大于初始化时间，就所有数据初始化，每个月递增查询，当前时间小于于初始时间每天抽取
            if(basesicUtil.compareDate(initializeDate,nowDate) == -1){
                Date yesterdayDate = DateUtils.addDays(now,-1);
                String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
                fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
                flag = false;
            }else{
                fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T00:00:00Z]";
                Date sDate = DateUtils.addMonths(DateUtil.parseDate(startDate, DateUtil.DEFAULT_DATE_YMD_FORMAT),1);
                startDate = DateUtil.formatDate(sDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
                Date eDate = DateUtils.addMonths(DateUtil.parseDate(startDate,DateUtil.DEFAULT_DATE_YMD_FORMAT),1);
                endDate = DateUtil.formatDate(eDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
                if(startDate.equals("2017-12-01")){
                    flag = false;
                }
                System.out.println("startDate =" + startDate);
            }
            // 疾病的处理
            // 找出就诊档案数
            List<OutPatientCostModel> outPatientCostModelList = new ArrayList<>();
            long count = solrUtil.count(ResourceCore.SubTable, q, fq);
            // 细表中查询EHR_000109:* 的记录, 返回细表的profile_id
            profileIdList = selectSubProfileId(ResourceCore.SubTable, q, fq, count < 1000 ? 1000 : count);
            if(profileIdList != null && profileIdList.size() > 0) {
                for (String profileId : profileIdList) {
                    sq = "EHR_000045:* AND profile_id:" + profileId;    // EHR_000045:* AND profile_id:xx
                    sq2 = "EHR_000109:* AND profile_id:" + profileId;   // 查询疾病编码
                    log.info(sq);
                    log.info(sq2);
                    List<String> keyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, sq2, fq, count < 1000 ? 1000 : count);
                    if (keyList != null && keyList.size() > 0) {
                        Double value = 0.0;
//                        Set<String> hash = new HashSet<>();
                        Map<String, String> diseaseName = new HashMap<>();
                        List<Map<String,Object>> dataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, keyList);
                        if (dataList != null && dataList.size() > 0) {
                            for(Map<String,Object> map : dataList) {
//                                hash.add(map.get(keyDisease) + "");
                                diseaseName.put(map.get(keyDisease) + "", map.get(keyDiseaseName) + "");
                            }
                            List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, sq, fq, count);
                            if(rowKeyList != null && rowKeyList.size() > 0) {
                                String town = "";
                                String eventDate = "";
                                List<Map<String,Object>> hbaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, rowKeyList);
                                if( hbaseDataList != null && hbaseDataList.size() > 0 ) {
                                    for(Map<String,Object> map : hbaseDataList) {
                                        if (StringUtils.isEmpty(town)) {
                                            town = map.get(keyArea) + "";
                                        }
                                        if (StringUtils.isEmpty(eventDate)) {
                                            eventDate = map.get(keyEventDate) + "";
                                        }
                                        value += Double.parseDouble(map.get(keyCost) + "");
                                    }
                                }
                                for (Map.Entry<String, String> m : diseaseName.entrySet()) {
                                    OutPatientCostModel outPatientCostModel = new OutPatientCostModel();
                                    outPatientCostModel.setType(1);
                                    outPatientCostModel.setCode(m.getKey());
                                    outPatientCostModel.setName(m.getValue());
                                    outPatientCostModel.setResult(value);
                                    outPatientCostModel.setTown(town);
                                    outPatientCostModel.setEventDate(DateUtil.formatCharDate(eventDate, DateUtil.DATE_WORLD_FORMAT));
                                    outPatientCostModel.setCreateTime(new Date());
                                    outPatientCostModelList.add(outPatientCostModel);
                                }
                            }
                        }
                    }
                }
            }
            // 将疾病费用保存到ES

            // 科室的处理
            long mCount = solrUtil.count(ResourceCore.MasterTable, q2, fq);
            List<OutPatientCostModel> outPatientCostModels = new ArrayList<>();
            List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.MasterTable, q2, fq, mCount < 1000 ? 1000 : mCount);
            if(rowKeyList != null && rowKeyList.size() > 0) {
                List<Map<String,Object>> hbaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.MasterTable, rowKeyList);
                if( hbaseDataList != null && hbaseDataList.size() > 0 ) {
                    for(Map<String,Object> map : hbaseDataList) {
                        sq = "EHR_000045:* AND profile_id:" + map.get(keyProfileId);
                        log.info(sq);
                        List<String> keyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, sq, fq, count);
                        if (keyList != null && keyList.size() > 0) {
                            Double result = 0.0;
                            List<Map<String,Object>> dataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, keyList);
                            if (dataList != null && dataList.size() > 0) {
                                for(Map<String,Object> data : dataList) {
                                    result += Double.parseDouble(data.get(keyCost) + "");
                                }
                            }
                            OutPatientCostModel outPatientCostModel = new OutPatientCostModel();
                            outPatientCostModel.setType(2);
                            outPatientCostModel.setCode(map.get(keyDept) + "");
                            outPatientCostModel.setName(map.get(keyDeptName) + "");
                            outPatientCostModel.setTown(map.get(keyArea) + "");
                            outPatientCostModel.setResult(result);
                            outPatientCostModel.setEventDate(DateUtil.formatCharDate(map.get(keyEventDate).toString(), DateUtil.DATE_WORLD_FORMAT));
                            outPatientCostModel.setEventDate(new Date());
                            outPatientCostModels.add(outPatientCostModel);
                        }
                    }
                }
            }
            System.out.println(outPatientCostModels);
        }
    }

    // 获取查询结果中的profile_id
    private List<String> selectSubProfileId(String core , String q, String fq, long count) throws Exception {
        List<String> data = new ArrayList<>();
        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 1,count);
        if(solrList != null && solrList.getNumFound() > 0) {
            for (SolrDocument doc : solrList){
                String rowkey = String.valueOf(doc.getFieldValue("profile_id"));
                data.add(rowkey);
            }
            // 去掉重复
            Set<String> hashSet = new HashSet<>(data);
            data.clear();
            data.addAll(hashSet);
        }
        return  data;
    }

}
