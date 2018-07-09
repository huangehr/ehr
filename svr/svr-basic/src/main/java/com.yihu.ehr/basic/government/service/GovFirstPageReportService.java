package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

/**
 * 政府服务平台首页报表接口 Service
 *
 * @author 张进军
 * @date 2018/7/6 14:01
 */
@Service
public class GovFirstPageReportService {

    String dateSourceFormat = "yyyy-MM";
    String lastDateFormat = "yyyy-MM-dd'T'23:59:59'Z'";
    DecimalFormat df = new DecimalFormat("#.00");

    @Autowired
    private SolrUtil solrUtil;

    /**
     * 按月统计门急诊人次
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public Long countEmergencyAttendance(String orgCode, String date) {
        Long result = 0L;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);
            String q = String.format("event_type:0 AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            result = solrUtil.count(ResourceCore.MasterTable, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院人次
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public Long countHospitalizationAttendance(String orgCode, String date) {
        Long result = 0L;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);
            String q = String.format("event_type:1 AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            result = solrUtil.count(ResourceCore.MasterTable, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计平均住院日
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public Double averageHospitalStayDay(String orgCode, String date) {
        Double result = 0D;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 总住院日，说明：EHR_000155 出院日期，EHR_000170 实际住院天数
            String q_days = String.format("event_type:1 AND EHR_000155:* AND EHR_000170:[1 TO *] AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            FieldStatsInfo daysStatsInfo = solrUtil.getStats(ResourceCore.MasterTable, q_days, null, "EHR_000170");
            Long hospitalizationDays = Long.getLong(daysStatsInfo.getSum().toString());
            // 总住院人次
            String q_count = String.format("event_type:1 AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            Long count = solrUtil.count(ResourceCore.MasterTable, q_count);

            result = Math.floor(hospitalizationDays / count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院人次人头比
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationHeadToHeadRatio(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            String q = String.format("event_type:1 AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            // 总住院人次
            Long count = solrUtil.count(ResourceCore.MasterTable, q);
            // 总住院人数
            String[] fields = new String[]{"event_no"};
            SolrDocumentList patientSolrDocList = solrUtil.queryDistinctOneField(ResourceCore.MasterTable, q, null, null, 0, -1, fields, "EHR_000017", "event_date desc");
            Long patientCount = 0L;
            if (patientSolrDocList != null) {
                patientCount = patientSolrDocList.getNumFound();
            }

            result = df.format((count / patientCount) * 100) + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计门急诊费用
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statEmergencyExpense(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 门急诊费用，说明：HDSD00_71 门诊-费用汇总，EHR_000045 门诊费用金额
            String q = String.format("rowkey:*$HDSD00_71$* AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            FieldStatsInfo statsInfo = solrUtil.getStats(ResourceCore.SubTable, q, null, "EHR_000045");
            Double expense = Double.parseDouble(statsInfo.getSum().toString());

            result = df.format(expense);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院费用
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationExpense(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 统计住院费用，说明：HDSD00_68 住院-费用汇总，EHR_000175 住院费用金额
            String q = String.format("rowkey:*$HDSD00_68$* AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            FieldStatsInfo statsInfo = solrUtil.getStats(ResourceCore.SubTable, q, null, "EHR_000175");
            Double expense = Double.parseDouble(statsInfo.getSum().toString());

            result = df.format(expense);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计门急诊费用环比
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statEmergencyExpenseRatio(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            Double expense = Double.parseDouble(statEmergencyExpense(orgCode, date));
            // 上月门急诊费用
            String preFirstDate = DateUtil.getFirstDateOfLashMonth(firstDate, DateUtil.utcDateTimePattern);
            Double preExpense = Double.parseDouble(statEmergencyExpense(orgCode, preFirstDate));

            // 环比
            result = df.format((expense - preExpense) / preExpense * 100) + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院费用环比
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationExpenseRatio(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            Double expense = Double.parseDouble(statHospitalizationExpense(orgCode, date));
            // 上月住院费用
            String preFirstDate = DateUtil.getFirstDateOfLashMonth(firstDate, DateUtil.utcDateTimePattern);
            Double preExpense = Double.parseDouble(statHospitalizationExpense(orgCode, preFirstDate));

            // 环比
            result = df.format((expense - preExpense) / preExpense * 100) + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计门急诊人均费用
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String averageEmergencyExpense(String orgCode, String date) {
        String result = null;
        try {
            Long count = countEmergencyAttendance(orgCode, date);
            Double expense = Double.parseDouble(statEmergencyExpense(orgCode, date));

            result = df.format(expense / count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院人均费用
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String averageHospitalizationExpense(String orgCode, String date) {
        String result = null;
        try {
            Long count = countHospitalizationAttendance(orgCode, date);
            Double expense = Double.parseDouble(statHospitalizationExpense(orgCode, date));

            result = df.format(expense / count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计门急诊医药费用
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statEmergencyMedicineExpense(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 门急诊医药费用，说明：HDSD00_71 门诊-费用汇总，EHR_000044 门诊费用分类代码，EHR_000045 门诊费用金额
            String q = String.format("rowkey:*$HDSD00_71$* AND (EHR_000044:01 OR EHR_000044:02 OR EHR_000044:03) AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            FieldStatsInfo statsInfo = solrUtil.getStats(ResourceCore.SubTable, q, null, "EHR_000045");
            Double expense = Double.parseDouble(statsInfo.getSum().toString());

            result = df.format(expense);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院医药费用
     *
     * @param orgCode 机构编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationMedicineExpense(String orgCode, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 统计住院费用，说明：HDSD00_68 住院-费用汇总，EHR_000174 住院费用分类代码，EHR_000175 住院费用金额
            String q = String.format("rowkey:*$HDSD00_68$* AND (EHR_000174:03 OR EHR_000174:04 OR EHR_000174:05) AND event_date:[%s TO %s] AND org_code:%s", firstDate, lastDate, orgCode);
            FieldStatsInfo statsInfo = solrUtil.getStats(ResourceCore.SubTable, q, null, "EHR_000175");
            Double expense = Double.parseDouble(statsInfo.getSum().toString());

            result = df.format(expense);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
