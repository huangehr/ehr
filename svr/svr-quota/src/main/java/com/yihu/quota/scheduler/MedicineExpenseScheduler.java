package com.yihu.quota.scheduler;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
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
 * 整合solr中档案主表（HealthProfile）性别、年龄和档案细表（HealthProfileSub）费用数据到一起，
 * 保存整合后数据到ES，方便按性别、年龄段统计药品费用。
 *
 * @author 张进军
 * @date 2018/5/15 17:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "整合性别、年龄段数据到药品费用中", tags = {"solr跨表数据抽取--整合性别、年龄段数据到药品费用中"})
public class MedicineExpenseScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MedicineExpenseScheduler.class);

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private HBaseDao hBaseDao;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;
    @Autowired
    private ElasticSearchClient elasticSearchClient;

    // 门诊药品费用、住院药品费用过滤条件
    private String q = "(EHR_000045:* AND (EHR_000044:01 OR EHR_000044:02 OR EHR_000044:03)) OR (EHR_000175:* AND (EHR_000174:01 OR EHR_000174:02))";

    /**
     * 抽取昨天的药品费用，整合性别、年龄段数据到药品费用中。
     */
    @Scheduled(cron = "0 36 02 * * ?")
    public void extractMedicineExpenseJob() {
        try {
            Date currDate = new Date();
            // 抽取昨天数据
            Date yesterdayDate = DateUtils.addDays(currDate, -1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";

            // 保存药品费用、性别、年龄段到ES
            saveData(fq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("抽取指定时间段药品费用，整合性别、年龄段数据到药品费用中")
    @RequestMapping(value = "extractMedicineExpense", method = RequestMethod.GET)
    public Envelop extractMedicineExpense(
            @ApiParam(name = "startDate", value = "开始日期，格式 YYYY-MM-DD，接口里自动拼接 T00:00:00Z，", required = true)
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "截止日期，格式 YYYY-MM-DD，接口里自动拼接 T23:59:59Z", required = true)
            @RequestParam(value = "endDate") String endDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try {
            String fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T23:59:59Z]";
            // 保存药品费用、性别、年龄段到ES
            saveData(fq);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 保存药品费用、性别、年龄段到ES
     */
    private void saveData(String fq) throws Exception {
        long startTime = System.currentTimeMillis();

        long count = solrUtil.count(ResourceCore.SubTable, q, fq);
        List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, q, fq, count);

        int currIndex = 0;
        List<Map<String, Object>> hBaseDataList = new ArrayList<>();
        List<String> pageRowKeyList = new ArrayList<>();
        logger.info("要整合并保存的药品费用记录总数：" + count);
        while (currIndex < count) { // 避免一次操作hbase太多次导致发生异常。
            long onceStartTime = System.currentTimeMillis();
            pageRowKeyList.clear();
            for (int i = 0; i < 100; i++) {
                if (currIndex < count) {
                    pageRowKeyList.add(rowKeyList.get(currIndex));
                    currIndex++;
                } else {
                    break;
                }
            }

            hBaseDataList.clear();
            hBaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, pageRowKeyList);
            for (Map<String, Object> subInfo : hBaseDataList) {
                Map<String, Object> medicineExpenseInfo = new HashMap<>();
                Map<String, Object> masterInfo = hBaseDao.getResultMap(ResourceCore.MasterTable, subInfo.get("profile_id").toString());
                // _id
                medicineExpenseInfo.put("_id", subInfo.get("rowkey"));
                // 区县
                medicineExpenseInfo.put("town", subInfo.get("org_area"));
                // 机构编码
                medicineExpenseInfo.put("org_code", subInfo.get("org_code"));
                // 就诊日期
                medicineExpenseInfo.put("event_date", subInfo.get("event_date"));
                // 药品费用类型，1：门诊，2：住院。
                Object EHR000045 = subInfo.get("EHR_000045");
                int type = EHR000045 != null ? 1 : 2;
                String typeName = EHR000045 != null ? "门诊" : "住院";
                Object expense = EHR000045 != null ? EHR000045 : subInfo.get("EHR_000175"); // 药品费用
                medicineExpenseInfo.put("type", type);
                // 药品费用类型名称
                medicineExpenseInfo.put("type_value", typeName);
                // 性别、性别名称
                Object sexObj = masterInfo.get("EHR_000019");
                if (sexObj != null) {
                    medicineExpenseInfo.put("sex", sexObj);
                    medicineExpenseInfo.put("sex_value", masterInfo.get("EHR_000019_VALUE"));
                } else {
                    medicineExpenseInfo.put("sex", "");
                    medicineExpenseInfo.put("sex_value", "");
                }
                // 年龄段、年龄段名称
                Object birthdayObj = masterInfo.get("EHR_000007"); // 出生日期
                if (birthdayObj != null) {
                    String birthday = birthdayObj.toString().substring(0, 10);
                    int age = healthArchiveSchedulerService.getAgeByBirth(birthday);
                    medicineExpenseInfo.put("age_range", healthArchiveSchedulerService.exchangeCodeByAge(age));
                    medicineExpenseInfo.put("age_range_value", healthArchiveSchedulerService.exchangeNameByAge(age));
                } else {
                    medicineExpenseInfo.put("age_range", "");
                    medicineExpenseInfo.put("age_range_value", "");
                }
                // 费用
                medicineExpenseInfo.put("expense", Float.valueOf(expense.toString()));
                // 创建时间
                medicineExpenseInfo.put("create_date", new Date());

                // 保存到ES
                elasticSearchClient.index("medicine_expense", "medicine_expense_info", medicineExpenseInfo);
            }
            logger.info("单次遍历数量：" + hBaseDataList.size() + "，耗时：" + ((System.currentTimeMillis() - onceStartTime)/1000) + " 秒");
        }

        logger.info("总耗时：" + ((System.currentTimeMillis() - startTime)/1000/60) + " 分");
    }

}
