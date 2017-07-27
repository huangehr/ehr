package com.yihu.quota.service.quota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Created by lyr on 2016/7/21.
 */
@Service
public class StatisticsService {

    private static String core = "HealthProfile";

    @Autowired
    SolrUtil solrUtil;
    @Autowired
    SolrQuery solrQuery;
    @Autowired
    ObjectMapper objectMapper;


    /**
     * 根据入库时间或事件时间统计入库档案
     *
     * @param orgCode
     * @param startDate
     * @param endDate
     * @param type
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, Long>> resolveStatisticsByCreateDateOrEventDate(String orgCode, String startDate, String endDate, int type) throws Exception {
        String orgParam = "org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode);
        String dq = " && " + (type == 1 ? "create_date" : "event_date") + ":[";

        //起始时间
        if (!StringUtils.isBlank(startDate)) {
            dq += startDate + "T00:00:00Z";
        } else {
            dq += "*";
        }
        dq += " TO ";
        //结束时间
        if (!StringUtils.isBlank(endDate)) {
            dq += endDate + "T23:59:59Z";
        } else {
            dq += "*";
        }
        dq += "]";

        System.out.println(dq);
        //累计统计
        FacetField totalFacet = solrUtil.getFacetField(core, "event_type", orgParam, 0, 0, -1, false);

        String groupFields = "event_type,org_code,patient_id";

        List<PivotField> listPivot = solrUtil.groupCountMult(core,null,orgParam,groupFields,0,10);

        Page<Map<String,Object>> mapPage = solrQuery.getGroupMult(core, groupFields, null, orgParam, 1, 10);

        //期间统计
        FacetField intervalFacet = solrUtil.getFacetField(core, "event_type", orgParam + dq, 0, 0, -1, false);

        Map<String, Map<String, Long>> result = new HashMap<>();
        Map<String, Long> mzResult = new HashMap<>();
        Map<String, Long> zyResult = new HashMap<>();
        Map<String, Long> totalResult = new HashMap<>();

        //累计统计计算
        if (totalFacet != null && totalFacet.getValues() != null && totalFacet.getValues().size() > 0) {
            List<FacetField.Count> counts = totalFacet.getValues();

            for (FacetField.Count count : counts) {
                if (count.getName().equals("0")) {
                    mzResult.put("total", Long.valueOf(count.getCount()));
                }
                if (count.getName().equals("1")) {
                    zyResult.put("total", Long.valueOf(count.getCount()));
                }
            }
            totalResult.put("total", mzResult.get("total") + zyResult.get("total"));
        } else {
            mzResult.put("total", Long.valueOf(0));
            zyResult.put("total", Long.valueOf(0));
            totalResult.put("total", Long.valueOf(0));
        }

        //期间统计计算
        if (intervalFacet != null && intervalFacet.getValues() != null && intervalFacet.getValues().size() > 0) {
            List<FacetField.Count> counts = intervalFacet.getValues();

            for (FacetField.Count count : counts) {
                if (count.getName().equals("0")) {
                    mzResult.put("period", Long.valueOf(count.getCount()));
                }
                if (count.getName().equals("1")) {
                    zyResult.put("period", Long.valueOf(count.getCount()));
                }
            }
            totalResult.put("period", mzResult.get("period") + zyResult.get("period"));
        } else {
            mzResult.put("period", Long.valueOf(0));
            zyResult.put("period", Long.valueOf(0));
            totalResult.put("period", Long.valueOf(0));
        }

        //门诊
        result.put("outpatient", mzResult);
        //住院
        result.put("hospitalization", zyResult);
        //合计
        result.put("total", totalResult);

        return result;
    }


    /**
     * 按事件时间指定步长统计
     *
     * @param orgCode
     * @param startDate
     * @param endDate
     * @param grap
     * @param  eventType
     * @return
     * @throws Exception
     */
    public Map<String, String> resolveStatisticsByEventDateGroup(String orgCode, String startDate, String endDate, String grap, String eventType) throws Exception {
        //过滤条件
        String fq = "org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode);

        if (!StringUtils.isBlank(eventType)) {
            fq += " && event_type:" + eventType;
        }

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //开始时间
        start.setTime(DateTimeUtil.simpleDateTimeParse(startDate + " 00:00:00"));
        start.add(Calendar.HOUR, 8);
        //结束时间
        end.setTime(DateTimeUtil.simpleDateTimeParse(endDate + " 23:59:59"));
        end.add(Calendar.HOUR, 8);

        Map<String, String> result = new TreeMap<String, String>();


        //"+1MONTH" "+1DAY"
        List<RangeFacet> values = solrUtil.getFacetDateRange("HealthProfile", "event_date", start.getTime(), end.getTime(), grap, fq);

        if (values != null && values.size() > 0) {
            List<RangeFacet.Count> counts = values.get(0).getCounts();
            for (RangeFacet.Count count : counts) {
                result.put(count.getValue().substring(0, 10), Long.toString(count.getCount()));
            }
        }
        return result;
    }

    /**
     * 门诊、出院分组统计查询
     *
     * @param field
     * @param orgCode
     * @param eventDate
     * @return
     * @throws Exception
     */
    public Map<String, Long> groupStatistics(String field, String orgCode, String eventDate, String eventType, boolean missing, boolean noTotal) throws Exception {
        StringBuilder fq = new StringBuilder();
        Map<String, Long> result = new TreeMap<>();

        if (StringUtils.isBlank(orgCode)) {
            LogService.getLogger().error("orgCode is null,check your params");
            throw new Exception("orgCode is null,check your params");
        }
        if (StringUtils.isBlank(eventDate)) {
            LogService.getLogger().error("eventDate is null,check your params");
            throw new Exception("eventDate is null,check your params");
        }

        //事件类型为门诊
        fq.append("event_type:" + eventType);
        //机构
        fq.append(" && org_code:" + orgCode);
        //事件日期
        fq.append(" && event_date:[" + eventDate + "T00:00:00Z TO " + eventDate + "T23:59:59Z]");
        //统计查询
        FacetField facetField = solrUtil.getFacetField(core, field, fq.toString(), 1, 0, -1, missing);

        if (facetField != null) {
            List<FacetField.Count> values = facetField.getValues();
            long total = 0;
            //查询结果处理
            for (FacetField.Count value : values) {
                //key为null或空字符串的统一为""
                String key = StringUtils.isBlank(value.getName()) ? "" : value.getName();
                long val = value.getCount();
                if (key.length() == 0 && result.get(key) != null) {
                    val += result.get(key);
                }
                result.put(key, val);
                total += val;
            }
            if (total > 0 && !noTotal) {
                result.put("合计", total);
            }
            if (result.containsKey("") && result.get("") < 1) {
                result.remove("");
            }
        }

        return result;
    }

    /**
     * 门诊、出院过滤统计查询
     *
     * @param orgCode
     * @param eventDate
     * @return
     * @throws Exception
     */
    public long filterQueryStatistics(String orgCode, String eventDate, String eventType) throws Exception {
        StringBuilder fq = new StringBuilder();

        if (StringUtils.isBlank(orgCode)) {
            LogService.getLogger().error("orgCode is null,check your params");
            throw new Exception("orgCode is null,check your params");
        }
        if (StringUtils.isBlank(eventDate)) {
            LogService.getLogger().error("eventDate is null,check your params");
            throw new Exception("eventDate is null,check your params");
        }

        //事件类型为门诊
        fq.append("event_type:" + eventType);
        //机构
        fq.append(" && org_code:" + orgCode);
        //事件日期
        fq.append(" && event_date:[" + eventDate + "T00:00:00Z TO " + eventDate + "T23:59:59Z]");
        //统计查询
        Map<String, Integer> count = solrUtil.getFacetQuery(core, fq.toString());

        if (count != null) {
            return count.get(fq.toString());
        }

        return 0;
    }

    /**
     * 查询统计
     *
     * @param queryString
     * @return
     * @throws Exception
     */
    public long fqStatistics(String queryString) throws Exception {
        //统计查询
        Map<String, Integer> count = solrUtil.getFacetQuery(core, queryString);

        if (count != null) {
            return count.get(queryString);
        }

        return 0;
    }

    /**
     * 单字段分组统计
     *
     * @param groupField
     * @param queryString
     * @return
     * @throws Exception
     */
    public Map<String, Long> facetFieldStatistics(String groupField, String queryString) throws Exception {
        Map<String, Long> result = new TreeMap<>();
        //统计查询
        FacetField facetField = solrUtil.getFacetField(core, groupField, queryString, 1, 0, -1, false);

        if (facetField != null) {
            List<FacetField.Count> values = facetField.getValues();
            //查询结果处理
            for (FacetField.Count value : values) {
                //key为null或空字符串的统一为""
                String key = StringUtils.isBlank(value.getName()) ? "" : value.getName();
                Long val = value.getCount();
                if (key.length() == 0 && result.get(key) != null) {
                    val += result.get(key);
                }
                result.put(key, val);
            }
        }

        return result;
    }

    /**
     * 按日期范围步长统计
     *
     * @param dateField
     * @param queryString
     * @param startDate
     * @param endDate
     * @param grap
     * @return
     * @throws Exception
     */
    public Map<String, Long> facetDateRangeStatistics(String dateField, String queryString, String startDate, String endDate, String grap) throws Exception {
        Map<String, Long> result = new TreeMap<>();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //开始时间
        start.setTime(DateTimeUtil.simpleDateTimeParse(startDate + " 00:00:00"));
        start.add(Calendar.HOUR, 8);
        //结束时间
        end.setTime(DateTimeUtil.simpleDateTimeParse(endDate + " 00:00:00"));
        end.add(Calendar.HOUR, 8);

        List<RangeFacet> values = solrUtil.getFacetDateRange(core, dateField, start.getTime(), end.getTime(), grap, queryString);

        if (values != null && values.size() > 0) {
            List<RangeFacet.Count> counts = values.get(0).getCounts();
            for (RangeFacet.Count count : counts) {
                result.put(count.getValue(), Long.valueOf(count.getCount()));
            }
        }
        return result;
    }

    /**
     * 数值范围按步长统计
     *
     * @param numField
     * @param queryString
     * @param start
     * @param end
     * @param grap
     * @return
     * @throws Exception
     */
    public Map<String, Long> facetNumRangeStatistics(String numField, String queryString, int start, int end, int grap) throws Exception {
        Map<String, Long> result = new TreeMap<>();
        List<RangeFacet> values = solrUtil.getFacetNumRange(core, numField, start, end, grap, queryString);

        if (values != null && values.size() > 0) {
            List<RangeFacet.Count> counts = values.get(0).getCounts();
            for (RangeFacet.Count count : counts) {
                result.put(count.getValue(), Long.valueOf(count.getCount()));
            }
        }
        return result;
    }

    /**
     * 档案入库查询
     *
     * @param items
     * @param params
     * @return
     */
    public Map<String, Object> profileStatistics(String items, JsonNode params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String orgCode = params.has("orgCode") ? params.get("orgCode").textValue() : "";
        String startDate = params.has("startDate") ? params.get("startDate").textValue() : "";
        String endDate = params.has("endDate") ? params.get("endDate").textValue() : "";
        String grap = params.has("grap") ? params.get("grap").textValue() : "";
        String eventType = params.has("eventType") ? params.get("eventType").textValue() : "";

        if (StringUtils.isBlank(orgCode)) {
            LogService.getLogger().error("orgCode is null");
            throw new Exception("orgCode is null");
        }
        if (StringUtils.isBlank(startDate)) {
            LogService.getLogger().error("startDate is null");
            throw new Exception("startDate is null");
        }
        if (StringUtils.isBlank(endDate)) {
            LogService.getLogger().error("endDate is null");
            throw new Exception("endDate is null");
        }

        String[] stats = items.split(",");
        for (String stat : stats) {
            switch (stat) {
                case "byCreateDate":
                    result.put("byCreateDate", resolveStatisticsByCreateDateOrEventDate(orgCode, startDate, endDate, 1));
                    break;
                case "byEventDate":
                    result.put("byEventDate", resolveStatisticsByCreateDateOrEventDate(orgCode, startDate, endDate, 2));
                    break;
                case "byEventDateGroup":
                    if (StringUtils.isBlank(grap)) {
                        LogService.getLogger().error("grap is null");
                        throw new Exception("grap is null");
                    }
                    if (StringUtils.isBlank(eventType)) {
                        LogService.getLogger().error("eventType is null");
                        throw new Exception("eventType is null");
                    }
                    result.put("byIdNotNull", resolveStatisticsByEventDateGroup(orgCode, startDate, endDate, grap, eventType));
                    break;
            }
        }
        return result;
    }

    /**
     * 门诊出院指标查询
     *
     * @param items
     * @param params
     * @return
     */
    public Map<String, Object> outpatientAndHospitalStatistics(String items, JsonNode params, boolean noTotal) throws Exception {
        Map<String, Object> result = new HashMap<>();
        int statType = 0;
        String[] stats = items.split(",");
        String orgCode = params.has("orgCode") ? params.get("orgCode").textValue() : "";
        String eventDate = params.has("eventDate") ? params.get("eventDate").textValue() : "";

        if (StringUtils.isBlank(orgCode)) {
            LogService.getLogger().error("orgCode is null");
            throw new Exception("orgCode is null");
        }
        if (StringUtils.isBlank(eventDate)) {
            LogService.getLogger().error("eventDate is null");
            throw new Exception("eventDate is null");
        }

        for (String stat : stats) {
            switch (stat) {
                case "outpatientDept":
                    result.put(stat, groupStatistics("EHR_000082", orgCode, eventDate, "0", true, noTotal));
                    break;
                case "outpatientSex":
                    result.put(stat, groupStatistics("EHR_000019_VALUE", orgCode, eventDate, "0", true, noTotal));
                    break;
                case "outpatient":
                    result.put(stat, filterQueryStatistics(orgCode, eventDate, "0"));
                    break;
                case "hospitalDept":
                    result.put(stat, groupStatistics("EHR_000223", orgCode, eventDate, "1", true, noTotal));
                    break;
                case "hospitalSex":
                    result.put(stat, groupStatistics("EHR_000019_VALUE", orgCode, eventDate, "1", true, noTotal));
                    break;
                case "hospitalDisease":
                    result.put(stat, groupStatistics("EHR_000163_VALUE", orgCode, eventDate, "1", true, noTotal));
                    break;
                case "hospital":
                    result.put(stat, filterQueryStatistics(orgCode, eventDate, "1"));
                    break;
            }
        }

        return result;
    }


}