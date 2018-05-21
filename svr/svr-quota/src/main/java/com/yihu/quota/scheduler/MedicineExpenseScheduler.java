package com.yihu.quota.scheduler;

import com.yihu.ehr.constants.ApiVersion;
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
 * 整合solr中药品费用跨数据集的数据到一起，
 * 保存整合后数据到ES，方便按某个跨数据集的维度统计药品费用。
 *
 * @author 张进军
 * @date 2018/5/15 17:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "整合药品费用跨数据集的数据到一起", tags = {"solr跨数据集数据抽取--整合药品费用跨数据集的数据到一起"})
public class MedicineExpenseScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MedicineExpenseScheduler.class);

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private HBaseDao hBaseDao;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    // ES 中药品费用 index
    private String ME_INDEX = "medicine_expense";
    // ES 中药品费用汇总 type
    private String ME_TYPE_COLLECTION = "medicine_expense_collection";
    // ES 中药品费用清单 type
    private String ME_TYPE_LIST = "medicine_expense_list";
    // 门诊药品费用汇总、住院药品费用汇总过滤条件
    private String Q_MZ_ZY_ME_COLLECTION = "(EHR_000045:* AND (EHR_000044:01 OR EHR_000044:02 OR EHR_000044:03)) OR (EHR_000175:* AND (EHR_000174:01 OR EHR_000174:02))";
    // 门诊药品费用清单、住院药品费用清单过滤条件
    private String Q_MZ_ZY_ME_LIST = "(EHR_000049:* AND (EHR_000044:01 OR EHR_000044:02 OR EHR_000044:03)) OR (EHR_000179:* AND (EHR_000174:01 OR EHR_000174:02))";

    /**
     * 抽取昨天的药品费用，整合来自其他数据集的部分数据到药品费用中。
     */
    @Scheduled(cron = "0 36 02 * * ?")
    public void extractMedicineExpenseJob() {
        try {
            Date currDate = new Date();
            // 抽取昨天数据
            Date yesterdayDate = DateUtils.addDays(currDate, -1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";

            // 保存整合后的药品费用汇总到ES
            saveMedicineExpenseCollection(fq);
            // 保存整合后的药品费用清单到ES
            saveMedicineExpenseList(fq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("抽取指定时间段药品费用，整合来自其他数据集的部分数据到药品费用中。")
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

            // 保存整合后的药品费用汇总到ES
            saveMedicineExpenseCollection(fq);
            // 保存整合后的药品费用清单到ES
            saveMedicineExpenseList(fq);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 保存整合后的药品费用汇总到ES
     */
    private void saveMedicineExpenseCollection(String fq) throws Exception {
        long startTime = System.currentTimeMillis();

        long count = solrUtil.count(ResourceCore.SubTable, Q_MZ_ZY_ME_COLLECTION, fq);
        List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, Q_MZ_ZY_ME_COLLECTION, fq, count);

        int currIndex = 0;
        List<Map<String, Object>> medicineExpenseInfoList = new ArrayList<>();
        List<Map<String, Object>> hBaseDataList = new ArrayList<>();
        List<String> pageRowKeyList = new ArrayList<>();
        logger.info("要整合并保存的药品费用汇总记录总数：" + count);
        while (currIndex < count) {
            // 避免一次操作太多次导致ES、HBase（厦门测试服务器）发生异常。
            long onceStartTime = System.currentTimeMillis();
            pageRowKeyList.clear();
            for (int i = 0; i < 1000; i++) {
                if (currIndex < count) {
                    pageRowKeyList.add(rowKeyList.get(currIndex));
                    currIndex++;
                } else {
                    break;
                }
            }

            long hbaseStart = System.currentTimeMillis();
            hBaseDataList.clear();
            medicineExpenseInfoList.clear();
            hBaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, pageRowKeyList);
            logger.info("单次查询药品费用汇总的HBase数量：" + pageRowKeyList.size() + "，耗时：" + ((System.currentTimeMillis() - hbaseStart) / 1000) + " 秒");

            long subStart = System.currentTimeMillis();
            for (Map<String, Object> subInfo : hBaseDataList) {
                Map<String, Object> medicineExpenseInfo = new HashMap<>();
                Map<String, Object> masterInfo = hBaseDao.getResultMap(ResourceCore.MasterTable, subInfo.get("profile_id").toString());
                // _id
                String id = subInfo.get("rowkey").toString();
                medicineExpenseInfo.put("_id", id);
                // 区县
                medicineExpenseInfo.put("town", subInfo.get("org_area"));
                // 机构编码
                medicineExpenseInfo.put("org_code", subInfo.get("org_code"));
                // 就诊日期
                medicineExpenseInfo.put("event_date", subInfo.get("event_date"));

                Object EHR000045 = subInfo.get("EHR_000045"); // 门诊费用
                Integer type = null; // 药品费用类型，1：门诊，2：住院。
                String typeName = null; // 药品费用类型名称
                Object expense = null; // 药品费用
                if (EHR000045 != null) {
                    type = 1;
                    typeName = "门诊";
                    expense = EHR000045;
                } else {
                    type = 2;
                    typeName = "住院";
                    expense = subInfo.get("EHR_000175");
                }

                // 药品费用类型，1：门诊，2：住院。
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
                // 修改时间
                String now = DateUtil.getNowDate(DateUtil.utcDateTimePattern);
                medicineExpenseInfo.put("modified_date", now);
                // 创建时间
                Map<String, Object> info = elasticSearchUtil.findById(ME_INDEX, ME_TYPE_COLLECTION, id);
                if (info == null) {
                    medicineExpenseInfo.put("created_date", now);
                }

                medicineExpenseInfoList.add(medicineExpenseInfo);
            }
            logger.info("收集药品费用汇总数据耗时：" + ((System.currentTimeMillis() - subStart) / 1000) + " 秒");

            // 保存到ES
            long saveStart = System.currentTimeMillis();
            elasticSearchUtil.bulkIndex(ME_INDEX, ME_TYPE_COLLECTION, medicineExpenseInfoList);
            logger.info("保存药品费用汇总的ES文档数量：" + medicineExpenseInfoList.size() + "，耗时：" + ((System.currentTimeMillis() - saveStart) / 1000) + " 秒");
        }

        logger.info("总耗时：" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + " 分");
    }

    /**
     * 保存整合后的药品费用清单到ES
     */
    private void saveMedicineExpenseList(String fq) throws Exception {
        long startTime = System.currentTimeMillis();

        long count = solrUtil.count(ResourceCore.SubTable, Q_MZ_ZY_ME_LIST, fq);
        List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.SubTable, Q_MZ_ZY_ME_LIST, fq, count);

        int currIndex = 0;
        List<Map<String, Object>> medicineExpenseInfoList = new ArrayList<>();
        List<Map<String, Object>> hBaseDataList = new ArrayList<>();
        List<String> pageRowKeyList = new ArrayList<>();
        logger.info("要整合并保存的药品费用清单记录总数：" + count);
        while (currIndex < count) {
            // 避免一次操作太多次导致ES、HBase（厦门测试服务器）发生异常。
            long onceStartTime = System.currentTimeMillis();
            pageRowKeyList.clear();
            for (int i = 0; i < 1000; i++) {
                if (currIndex < count) {
                    pageRowKeyList.add(rowKeyList.get(currIndex));
                    currIndex++;
                } else {
                    break;
                }
            }

            long hbaseStart = System.currentTimeMillis();
            hBaseDataList.clear();
            medicineExpenseInfoList.clear();
            hBaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, pageRowKeyList);
            logger.info("单次查询药品费用清单的HBase数量：" + pageRowKeyList.size() + "，耗时：" + ((System.currentTimeMillis() - hbaseStart) / 1000) + " 秒");

            long subStart = System.currentTimeMillis();
            for (Map<String, Object> subInfo : hBaseDataList) {
                Map<String, Object> medicineExpenseInfo = new HashMap<>();
                Map<String, Object> masterInfo = hBaseDao.getResultMap(ResourceCore.MasterTable, subInfo.get("profile_id").toString());
                // _id
                String id = subInfo.get("rowkey").toString();
                medicineExpenseInfo.put("_id", id);
                // 区县
                medicineExpenseInfo.put("town", subInfo.get("org_area"));
                // 机构编码
                medicineExpenseInfo.put("org_code", subInfo.get("org_code"));
                // 就诊日期
                medicineExpenseInfo.put("event_date", subInfo.get("event_date"));

                Object EHR000045 = subInfo.get("EHR_000049"); // 门诊费用
                Integer type = null; // 药品费用类型，1：门诊，2：住院。
                String typeName = null; // 药品费用类型名称
                Object expense = null; // 药品费用
                if (EHR000045 != null) {
                    type = 1;
                    typeName = "门诊";
                    expense = EHR000045;
                } else {
                    type = 2;
                    typeName = "住院";
                    expense = subInfo.get("EHR_000179");
                }

                // 药品费用类型，1：门诊，2：住院。
                medicineExpenseInfo.put("type", type);
                // 药品费用类型名称
                medicineExpenseInfo.put("type_value", typeName);
                // 费用
                medicineExpenseInfo.put("expense", Float.valueOf(expense.toString()));
                // 修改时间
                String now = DateUtil.getNowDate(DateUtil.utcDateTimePattern);
                medicineExpenseInfo.put("modified_date", now);
                // 创建时间
                Map<String, Object> info = elasticSearchUtil.findById(ME_INDEX, ME_TYPE_LIST, id);
                if (info == null) {
                    medicineExpenseInfo.put("created_date", now);
                }

                medicineExpenseInfoList.add(medicineExpenseInfo);
            }
            logger.info("收集药品费用清单数据耗时：" + ((System.currentTimeMillis() - subStart) / 1000) + " 秒");

            // 保存到ES
            long saveStart = System.currentTimeMillis();
            elasticSearchUtil.bulkIndex(ME_INDEX, ME_TYPE_LIST, medicineExpenseInfoList);
            logger.info("保存药品费用清单的ES文档数量：" + medicineExpenseInfoList.size() + "，耗时：" + ((System.currentTimeMillis() - saveStart) / 1000) + " 秒");
        }

        logger.info("总耗时：" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + " 分");
    }

}
