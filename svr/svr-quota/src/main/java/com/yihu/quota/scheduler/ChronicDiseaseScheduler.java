package com.yihu.quota.scheduler;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.scheduler.HealthArchiveSchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.response.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 去重记录高血压疾病、糖尿病疾病患者
 *
 * @author 张进军
 * @date 2018/6/27 17:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "去重记录高血压疾病、糖尿病疾病患者", tags = {"solr跨数据集数据抽取--去重记录高血压疾病、糖尿病疾病患者"})
public class ChronicDiseaseScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ChronicDiseaseScheduler.class);

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private HBaseDao hBaseDao;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    // ES 中慢性病 index
    private String CD_INDEX = "chronic_disease";
    // ES 中慢性病患者记录汇总 type
    private String CD_TYPE_PATIENT = "chronic_disease_patient";

    /**
     * 抽取昨天的去重记录高血压疾病、糖尿病疾病患者
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void extractChronicDiseaseJob() {
        try {
            Date currDate = new Date();
            // 抽取昨天数据
            Date yesterdayDate = DateUtils.addDays(currDate, -1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO " + yesterday + "T23:59:59Z]";

            // 整合并保存到ES
            saveTotalData(fq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("抽取指定时间段去重记录高血压疾病、糖尿病疾病患者")
    @RequestMapping(value = ServiceApi.TJ.Scheduler.ExtractChronicDisease, method = RequestMethod.GET)
    public Envelop extractChronicDisease(
            @ApiParam(name = "startDate", value = "开始日期，格式 YYYY-MM-DD，接口里自动拼接 T00:00:00Z，", required = true)
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "截止日期，格式 YYYY-MM-DD，接口里自动拼接 T23:59:59Z", required = true)
            @RequestParam(value = "endDate") String endDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try {
            String fq = "event_date:[" + startDate + "T00:00:00Z TO " + endDate + "T23:59:59Z]";

            // 整合并保存到ES
            saveTotalData(fq);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 整合并保存到ES
     */
    private void saveTotalData(String fq) throws Exception {
        String idCardField = "EHR_000017"; // 身份证号数据元
        // 查询结果的返回字段
        String[] showFields = {"rowkey"};

        // 去重查询糖尿病患者记录
        List<String> diabetesRowkeyList = new ArrayList();
        String q_diabetes = "(rowkey:*$HDSD00_73$* AND EHR_000109:[E10 TO E14.900]) OR (rowkey:*$HDSD00_69$* AND EHR_000293:[E10 TO E14.900])";
        int countDiabetes = (int) solrUtil.count(ResourceCore.SubTable, q_diabetes, fq);
        List<Group> diabetesGroupList = solrUtil.queryDistinctOneField(ResourceCore.SubTable, q_diabetes, fq, null, 0, countDiabetes, showFields, idCardField, "event_date asc");
        SchedulerUtil.collectRowkeyFromDistinctGroup(diabetesRowkeyList, solrUtil, diabetesGroupList, q_diabetes, fq, idCardField, showFields);
        this.translateAndSaveData(diabetesRowkeyList, "1");

        // 去重查询高血压患者记录
        List<String> hypertensionRowkeyList = new ArrayList();
        String q_hypertension = "(rowkey:*$HDSD00_73$* AND EHR_000109:[I10 TO I15.900]) OR (rowkey:*$HDSD00_69$* AND EHR_000293:[I10 TO I15.900])";
        int countHypertension = (int) solrUtil.count(ResourceCore.SubTable, q_hypertension, fq);
        List<Group> hypertensionGroupList = solrUtil.queryDistinctOneField(ResourceCore.SubTable, q_hypertension, fq, null, 0, countHypertension, showFields, idCardField, "event_date asc");
        SchedulerUtil.collectRowkeyFromDistinctGroup(hypertensionRowkeyList, solrUtil, hypertensionGroupList, q_hypertension, fq, idCardField, showFields);
        this.translateAndSaveData(hypertensionRowkeyList, "2");
    }

    /**
     * 转化并保存慢性病患者数据
     *
     * @param rowKeyList 档案细表 rowKey 字段值集合
     * @param type       慢性病患者类型，1：糖尿病，2：高血压
     * @throws Exception
     */
    private void translateAndSaveData(List<String> rowKeyList, String type) throws Exception {
        int currIndex = 0;
        int count = rowKeyList.size();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, Object>> hBaseDataList = new ArrayList<>();
        List<String> pageRowKeyList = new ArrayList<>();
        while (currIndex < count) {
            // 避免一次操作太多次导致ES、HBase（厦门测试服务器）发生异常。
            pageRowKeyList.clear();
            for (int i = 0; i < 1000; i++) {
                if (currIndex < count) {
                    pageRowKeyList.add(rowKeyList.get(currIndex));
                    currIndex++;
                } else {
                    break;
                }
            }

            hBaseDataList.clear();
            dataList.clear();
            hBaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, pageRowKeyList);

            List<String> fields = new ArrayList<>();
            fields.add("id_card");
            fields.add("type");
            for (Map<String, Object> subInfo : hBaseDataList) {
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> masterInfo = hBaseDao.getResultMap(ResourceCore.MasterTable, subInfo.get("profile_id").toString());

                // 身份证号
                String idCard = masterInfo.get("EHR_000017") != null ? masterInfo.get("EHR_000017").toString() : "";
                data.put("id_card", idCard);

                // 检测是否已有该患者，已有则不更新。
                String sql = "SELECT id_card, type FROM chronic_disease/chronic_disease_patient WHERE id_card = '" + idCard + "' AND type = '" + type + "'";
                List<Map<String, Object>> infoList = elasticSearchUtil.findBySql(fields, sql);
                if (infoList != null || infoList.size() != 0) {
                    continue;
                }

                // 区县
                data.put("town", subInfo.get("org_area"));
                // 机构编码
                data.put("org_code", subInfo.get("org_code"));
                // 就诊日期
                data.put("event_date", subInfo.get("event_date"));
                // 就诊类型
                data.put("event_type", subInfo.get("event_type"));
                // 慢性病类型
                data.put("type", type);
                // 慢性病类型名称
                String typeName = null; // 患者疾病类型名称
                if ("1".equals(type)) {
                    typeName = "糖尿病";
                } else if ("2".equals(type)) {
                    typeName = "高血压";
                }
                data.put("type_value", typeName);
                // 修改时间
                String now = DateUtil.getNowDate(DateUtil.utcDateTimePattern);
                data.put("modified_date", now);
                // 创建时间
                data.put("created_date", now);

                dataList.add(data);
            }

            // 保存到ES
            elasticSearchUtil.bulkIndex(CD_INDEX, CD_TYPE_PATIENT, dataList);
        }
    }

}
