package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public Long countEmergencyAttendance(String orgArea, String date) {
        Long result = 0L;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);
            String q = String.format("event_type:0 AND event_date:[%s TO %s]", firstDate, lastDate);
            if (StringUtils.isNotEmpty(orgArea)) {
                q += " AND org_area:" + orgArea;
            }
            result = solrUtil.count(ResourceCore.MasterTable, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按月统计住院人次
     *
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public Long countHospitalizationAttendance(String orgArea, String date) {
        Long result = 0L;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);
            String q = String.format("event_type:0 AND event_date:[%s TO %s]", firstDate, lastDate);
            if (StringUtils.isNotEmpty(orgArea)) {
                q += " AND org_area:" + orgArea;
            }
            result = solrUtil.count(ResourceCore.MasterTable, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 统计近6个月的门急诊/住院人次趋势
     *
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @param type    类型，0：门急诊，1：住院
     * @return
     */
    public Map<String, Object> statAttendanceTrend(String orgArea, String date, int type) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<Integer> valueList = new ArrayList<>();
        try {
            String endDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);
            String startFirstDate = DateUtil.getFirstDateOfSomeMonth(date, dateSourceFormat, -5);

            String fq = String.format("event_type:%s", type);
            if (StringUtils.isNotEmpty(orgArea)) {
                fq += " AND org_area:" + orgArea;
            }
            List<RangeFacet> rangeFacetList = solrUtil.getFacetDateRange(ResourceCore.MasterTable, "event_date", startFirstDate, endDate, "+1MONTH", fq, null);
            for (RangeFacet item : rangeFacetList) {
                List<RangeFacet.Count> countList = item.getCounts();
                for (RangeFacet.Count count : countList) {
                    String name = count.getValue().substring(0, 7);
                    nameList.add(name);
                    valueList.add(count.getCount());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 按月统计门急诊费用
     *
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statEmergencyExpense(String orgArea, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 门急诊费用，说明：HDSD00_71 门诊-费用汇总，EHR_000045 门诊费用金额
            String q = String.format("rowkey:*$HDSD00_71$* AND event_date:[%s TO %s]", firstDate, lastDate);
            if (StringUtils.isNotEmpty(orgArea)) {
                q += " AND org_area:" + orgArea;
            }
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
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationExpense(String orgArea, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 统计住院费用，说明：HDSD00_68 住院-费用汇总，EHR_000175 住院费用金额
            String q = String.format("rowkey:*$HDSD00_68$* AND event_date:[%s TO %s]", firstDate, lastDate);
            FieldStatsInfo statsInfo = solrUtil.getStats(ResourceCore.SubTable, q, null, "EHR_000175");
            if (StringUtils.isNotEmpty(orgArea)) {
                q += " AND org_area:" + orgArea;
            }
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
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statEmergencyExpenseRatio(String orgArea, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            Double expense = Double.parseDouble(statEmergencyExpense(orgArea, date));
            // 上月门急诊费用
            String preFirstDate = DateUtil.getFirstDateOfLashMonth(firstDate, DateUtil.utcDateTimePattern);
            Double preExpense = Double.parseDouble(statEmergencyExpense(orgArea, preFirstDate));

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
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationExpenseRatio(String orgArea, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            Double expense = Double.parseDouble(statHospitalizationExpense(orgArea, date));
            // 上月住院费用
            String preFirstDate = DateUtil.getFirstDateOfLashMonth(firstDate, DateUtil.utcDateTimePattern);
            Double preExpense = Double.parseDouble(statHospitalizationExpense(orgArea, preFirstDate));

            // 环比
            result = df.format((expense - preExpense) / preExpense * 100) + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 统计近6个月的门急诊/住院费用趋势
     *
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @param type    类型，0：门急诊，1：住院
     * @return
     */
    public Map<String, Object> statExpenseTrend(String orgArea, String date, int type) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        try {
            String currFirstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String pre1FirstDate = DateUtil.getFirstDateOfSomeMonth(date, dateSourceFormat, -1);
            String pre2FirstDate = DateUtil.getFirstDateOfSomeMonth(date, dateSourceFormat, -2);
            String pre3FirstDate = DateUtil.getFirstDateOfSomeMonth(date, dateSourceFormat, -3);
            String pre4FirstDate = DateUtil.getFirstDateOfSomeMonth(date, dateSourceFormat, -4);
            String pre5FirstDate = DateUtil.getFirstDateOfSomeMonth(date, dateSourceFormat, -5);

            this.collectExpense(nameList, valueList, type, orgArea, pre5FirstDate);
            this.collectExpense(nameList, valueList, type, orgArea, pre4FirstDate);
            this.collectExpense(nameList, valueList, type, orgArea, pre3FirstDate);
            this.collectExpense(nameList, valueList, type, orgArea, pre2FirstDate);
            this.collectExpense(nameList, valueList, type, orgArea, pre1FirstDate);
            this.collectExpense(nameList, valueList, type, orgArea, currFirstDate);

            resultMap.put("nameList", nameList);
            resultMap.put("valueList", valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    // 收集月份门急诊/住院费用
    private void collectExpense(List<String> nameList, List<String> valueList, int type, String orgArea, String date) {
        String expense = null;
        if (type == 0) { // 门急诊费用
            expense = this.statEmergencyExpense(orgArea, date);
        } else if (type == 1) { // 住院费用
            expense = this.statHospitalizationExpense(orgArea, date);
        }
        String name = date.substring(0, 7);
        nameList.add(name);
        valueList.add(expense);
    }

    /**
     * 按月统计门急诊医药费用
     *
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statEmergencyMedicineExpense(String orgArea, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 门急诊医药费用，说明：HDSD00_71 门诊-费用汇总，EHR_000044 门诊费用分类代码，EHR_000045 门诊费用金额
            String q = String.format("rowkey:*$HDSD00_71$* AND (EHR_000044:01 OR EHR_000044:02 OR EHR_000044:03) AND event_date:[%s TO %s]", firstDate, lastDate);
            if (StringUtils.isNotEmpty(orgArea)) {
                q += " AND org_area:" + orgArea;
            }
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
     * @param orgArea 区县编码
     * @param date    日期（年月），格式：yyyy-MM-dd
     * @return
     */
    public String statHospitalizationMedicineExpense(String orgArea, String date) {
        String result = null;
        try {
            String firstDate = DateUtil.getFirstDate(date, dateSourceFormat, DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, dateSourceFormat, lastDateFormat);

            // 统计住院费用，说明：HDSD00_68 住院-费用汇总，EHR_000174 住院费用分类代码，EHR_000175 住院费用金额
            String q = String.format("rowkey:*$HDSD00_68$* AND (EHR_000174:03 OR EHR_000174:04 OR EHR_000174:05) AND event_date:[%s TO %s]", firstDate, lastDate);
            if (StringUtils.isNotEmpty(orgArea)) {
                q += " AND org_area:" + orgArea;
            }
            FieldStatsInfo statsInfo = solrUtil.getStats(ResourceCore.SubTable, q, null, "EHR_000175");
            Double expense = Double.parseDouble(statsInfo.getSum().toString());

            result = df.format(expense);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
