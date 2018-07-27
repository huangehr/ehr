package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.scheduler.HealthArchiveSchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by wxw on 2018/3/24.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "统计疾病费用", tags = {"solr跨表数据抽取--统计疾病费用"})
public class OutPatientCostScheduler {

    DecimalFormat df = new DecimalFormat("#.00");

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    private static final Logger log = LoggerFactory.getLogger(HealthArchiveScheduler.class);

    /**
     * 门急诊疾病费用统计
     */
    @Scheduled(cron = "0 36 07 * * ?")
    public void statisticOutPatientDiseaseCostScheduler() {
        try {
            Date now = new Date();
            Date yesterdayDate = DateUtils.addDays(now,-1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
            String q =  "EHR_000109:*"; // 查询条件 诊断代码（门诊）
            String sQuery = "EHR_000045:* AND profile_id:";
            String sQuery2 = "EHR_000109:* AND profile_id:";

            String keyEventDate = "event_date";
            String keyArea = "org_area";  // 行政区划代码  EHR_001225
            String keyDisease = "EHR_000109";  // 诊断代码（门诊）
            String keyDiseaseName = "EHR_000112";  // 诊断名称（门诊）
            String keyCost = "EHR_000045";  // 门诊费用金额（元）
            // 抽取并保存到ES
            saveDiseaseExpenseData(fq, q, sQuery, sQuery2, keyEventDate, keyArea, keyDisease, keyDiseaseName, keyCost, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 住院疾病费用统计
     */
    @Scheduled(cron = "0 56 07 * * ?")
    public void statisticInPatientDiseaseCostScheduler() {
        try {
            Date now = new Date();
            Date yesterdayDate = DateUtils.addDays(now,-1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
            String q =  "EHR_000293:*"; // 查询条件 诊断代码（住院）
            String sQuery = "EHR_000175:* AND profile_id:";
            String sQuery2 = "EHR_000293:* AND profile_id:";

            String keyEventDate = "event_date";
            String keyArea = "org_area";  // 行政区划代码
            String keyDisease = "EHR_000293";  // 诊断代码（住院）
            String keyDiseaseName = "EHR_000295";  // 诊断名称（住院）
            String keyCost = "EHR_000175";  // 门诊费用金额（元）
            // 抽取并保存到ES
            saveDiseaseExpenseData(fq, q, sQuery, sQuery2, keyEventDate, keyArea, keyDisease, keyDiseaseName, keyCost, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("抽取指定时间段住院疾病费用")
    @RequestMapping(value = "/extractInPatientDiseaseExpense", method = RequestMethod.GET)
    public Envelop extractInPatientDiseaseExpense(
            @ApiParam(name = "startDate", value = "开始日期，格式 YYYY-MM-DD，接口里自动拼接 T00:00:00Z，", required = true)
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "截止日期，格式 YYYY-MM-DD，接口里自动拼接 T23:59:59Z", required = true)
            @RequestParam(value = "endDate") String endDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try {
            String fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T23:59:59Z]";
            String q =  "EHR_000293:*"; // 查询条件 诊断代码（住院）
            String sQuery = "EHR_000175:* AND profile_id:";
            String sQuery2 = "EHR_000293:* AND profile_id:";

            String keyEventDate = "event_date";
            String keyArea = "org_area";  // 行政区划代码
            String keyDisease = "EHR_000293";  // 诊断代码（住院）
            String keyDiseaseName = "EHR_000295";  // 诊断名称（住院）
            String keyCost = "EHR_000175";  // 门诊费用金额（元）
            // 抽取并保存到ES
            saveDiseaseExpenseData(fq, q, sQuery, sQuery2, keyEventDate, keyArea, keyDisease, keyDiseaseName, keyCost, 3);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("抽取指定时间段门急诊疾病费用")
    @RequestMapping(value = "/extractOutPatientDiseaseExpense", method = RequestMethod.GET)
    public Envelop extractOutPatientDiseaseExpense(
            @ApiParam(name = "startDate", value = "开始日期，格式 YYYY-MM-DD，接口里自动拼接 T00:00:00Z，", required = true)
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "截止日期，格式 YYYY-MM-DD，接口里自动拼接 T23:59:59Z", required = true)
            @RequestParam(value = "endDate") String endDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try {
            String fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T23:59:59Z]";
            String q =  "EHR_000109:*"; // 查询条件 诊断代码（门诊）
            String sQuery = "EHR_000045:* AND profile_id:";
            String sQuery2 = "EHR_000109:* AND profile_id:";

            String keyEventDate = "event_date";
            String keyArea = "org_area";  // 行政区划代码  EHR_001225
            String keyDisease = "EHR_000109";  // 诊断代码（门诊）
            String keyDiseaseName = "EHR_000112";  // 诊断名称（门诊）
            String keyCost = "EHR_000045";  // 门诊费用金额（元）
            // 抽取并保存到ES
            saveDiseaseExpenseData(fq, q, sQuery, sQuery2, keyEventDate, keyArea, keyDisease, keyDiseaseName, keyCost, 1);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 抽取疾病费用并保存到ES
     * @param fq
     * @throws Exception
     */
    private void saveDiseaseExpenseData(String fq, String q, String sQuery, String sQuery2, String keyEventDate, String keyArea,
                    String keyDisease, String keyDiseaseName, String keyCost, Integer type) throws Exception {

        String sq = "";
        String sq2 = "";
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        List<String> profileIdList = new ArrayList<>() ;

        profileIdList.clear();

        // 疾病的处理
        // 找出就诊档案数
        long count = solrUtil.count(ResourceCore.SubTable, q, fq);
        // 细表中查询EHR_000109:* 的记录, 返回细表的profile_id
        profileIdList = selectSubProfileId(ResourceCore.SubTable, q, fq, count < 1000 ? 1000 : count);
        if(profileIdList != null && profileIdList.size() > 0) {
            for (String profileId : profileIdList) {
                sq = sQuery + profileId;    // EHR_000045:* AND profile_id:xx
                sq2 = sQuery2 + profileId;   // 查询疾病编码
                List<String> keyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, sq2, fq, count < 1000 ? 1000 : count);
                if (keyList != null && keyList.size() > 0) {
                    Double value = 0.0;
                    Map<String, String> diseaseName = new HashMap<>();
                    Map<String, String> rowKeyName = new HashMap<>();
                    List<Map<String, Object>> dataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, keyList);
                    if (dataList != null && dataList.size() > 0) {
                        for(Map<String, Object> map : dataList) {
                            diseaseName.put(map.get(keyDisease) + "", map.get(keyDiseaseName) + "");
                            rowKeyName.put(map.get(keyDisease) + "", map.get("rowkey") + "");
                        }
                        List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, sq, fq, count < 1000 ? 1000 : count);
                        if(rowKeyList != null && rowKeyList.size() > 0) {
                            String town = "";
                            String org = "";
                            String dept = "";
                            String eventDate = "";
                            List<Map<String,Object>> hbaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, rowKeyList);
                            if( hbaseDataList != null && hbaseDataList.size() > 0 ) {
                                for(Map<String,Object> map : hbaseDataList) {
                                    if (StringUtils.isEmpty(town)) {
                                        town = map.get(keyArea).toString();
                                    }
                                    if (StringUtils.isEmpty(org)) {
                                        org = map.get("org_code").toString();
                                    }
                                    if (StringUtils.isEmpty(dept)) {
                                        dept = map.get("dept_code").toString();
                                    }
                                    if (StringUtils.isEmpty(eventDate)) {
                                        eventDate = map.get(keyEventDate) + "";
                                    }
                                    value += Double.parseDouble(map.get(keyCost) + "");
                                }
                            }
                            String valueStr = value == 0 ? value.toString() : df.format(value);
                            for (Map.Entry<String, String> m : diseaseName.entrySet()) {
                                Map<String, Object> costInfo = new HashMap<>();
                                costInfo.put("_id", rowKeyName.get(m.getKey()) + type.toString());
                                costInfo.put("type", type);
                                costInfo.put("code", m.getKey());
                                costInfo.put("name", m.getValue());
                                costInfo.put("result", valueStr);
                                costInfo.put("town", town);
                                costInfo.put("org", org);
                                costInfo.put("dept", dept);
                                costInfo.put("eventDate", DateUtil.formatCharDate(eventDate, DateUtil.DATE_WORLD_FORMAT));
                                costInfo.put("createTime", new Date());

                                // 将疾病费用保存到ES
                                saveToEs(costInfo);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 存储到es, index:hospitalCost(别名)
     * @param costInfo
     * @throws Exception
     */
    public void saveToEs(Map<String, Object> costInfo) throws Exception {
        String index = "hospitalCost";
        String type = "cost_info";
        long start = System.currentTimeMillis();
        log.info("开始保存时间 = {}", costInfo, start);
        elasticSearchClient.index(index, type, costInfo);
        long end = System.currentTimeMillis();
        log.info("用时 = {}毫秒", end - start);
    }

    // 获取查询结果中的profile_id
    private List<String> selectSubProfileId(String core , String q, String fq, long count) throws Exception {
        List<String> data = new ArrayList<>();
        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 0, count);
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
