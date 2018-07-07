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
            String firstDate = DateUtil.getFirstDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            lastDate.replace("00:00:00", "23:59:59");
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
            String firstDate = DateUtil.getFirstDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            lastDate.replace("00:00:00", "23:59:59");
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
            String firstDate = DateUtil.getFirstDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            lastDate.replace("00:00:00", "23:59:59");

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
            String firstDate = DateUtil.getFirstDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            String lastDate = DateUtil.getLastDate(date, "yyyy-MM", DateUtil.utcDateTimePattern);
            lastDate.replace("00:00:00", "23:59:59");

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

            DecimalFormat df = new DecimalFormat("#.00");
            result = df.format((count / patientCount) * 100) + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
