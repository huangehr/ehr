package com.yihu.ehr.service;

import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.IdcardValidator;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.util.EnumCounters;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.handler.component.FacetComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    IdcardValidator idcardValidator;


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

        //累计统计
        FacetField totalFacet = solrUtil.getFacetField(core, "event_type", orgParam, 0, 0, -1, false);
        //累计统计
        FacetField intervalFacet = solrUtil.getFacetField(core, "event_type", orgParam + dq, 0, 0, -1, false);

        Map<String, Map<String, Long>> result = new HashMap<>();
        Map<String, Long> mzResult = new HashMap<>();
        Map<String, Long> zyResult = new HashMap<>();
        Map<String, Long> totalResult = new HashMap<>();

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

        result.put("outpatient", mzResult);
        result.put("hospitalization", zyResult);
        result.put("total", totalResult);

        return result;
    }

    /**
     * 可识别身份统计
     *
     * @param orgCode
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public long resolveStatisticsByIdNotNull(String orgCode, String startDate, String endDate) throws Exception {
        StringBuilder fq = new StringBuilder();

        //身份证号不为空
        fq.append("demographic_id:*");
        //机构条件
        fq.append(" && org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode));
        //入库时间
        fq.append(" && event_date:[");
        //起始时间
        if (!StringUtils.isBlank(startDate)) {
            fq.append(startDate + "T00:00:00Z");
        } else {
            fq.append("*");
        }
        fq.append(" TO ");
        //结束时间
        if (!StringUtils.isBlank(endDate)) {
            fq.append(endDate + "T23:59:59Z");
        } else {
            fq.append("*");
        }
        fq.append("]");

        //分组查询
        FacetField query = solrUtil.getFacetField("HealthProfile", "demographic_id", fq.toString(), 1, 0, -1, false);

        if (query != null) {
            List<FacetField.Count> values = query.getValues();
            long size = values.size();

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (!idcardValidator.isIdcard(count.getName())) {
                    size--;
                }
            }

            return size;
        } else {
            return 0;
        }
    }

    public Map<String, Map<String, Long>> statisticsByIdNotNull(String orgCode, String startDate, String endDate) throws Exception {
        Map<String, Map<String, Long>> result = new HashMap<>();
        String orgParam = "org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode);
        String dq = " && event_date" + ":[";
        String end = "";
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
            end += endDate + "T23:59:59Z";
        } else {
            dq += "*";
            end += "*";
        }
        dq += "]";

        //门诊可识别人数
        FacetField mzQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:0", 1, 0, -1, false);
        //住院可识别人数
        FacetField zyQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:1", 1, 0, -1, false);
        //全库合计
        FacetField totalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam, 1, 0, -1, false);
        //期间门诊可识别人次
        FacetField mzIntervalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:0" + dq, 1, 0, -1, false);
        //期间住院可识别人次
        FacetField zyIntervalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:1" + dq, 1, 0, -1, false);
        //截止结束时间可识别人次
        FacetField endDateQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:1"
                + " && event_date:[* TO " + end + "]", 1, 0, -1, false);
        //期间合计可识别人次
        FacetField intervalTotalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + dq, 1, 0, -1, false);

        Map<String, Long> totalResult = new HashMap<>();
        Map<String, Long> intervalResult = new HashMap<>();
        Set<String> repeat = new HashSet<>();
        long repeatCount = 0;

        if (mzQuery != null) {
            List<FacetField.Count> values = mzQuery.getValues();
            long size = values.size();

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (idcardValidator.isIdcard(count.getName())) {
                    repeat.add(count.getName());
                } else {
                    size--;
                }
            }
            totalResult.put("outpatient", size);
        } else {
            totalResult.put("outpatient", Long.valueOf(0));
        }
        if (zyQuery != null) {
            List<FacetField.Count> values = zyQuery.getValues();
            long size = values.size();

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (idcardValidator.isIdcard(count.getName())) {
                    if (!repeat.add(count.getName())) {
                        repeatCount++;
                    }
                } else {
                    size--;
                }
            }
            totalResult.put("hospitalization", size);
        } else {
            totalResult.put("hospitalization", Long.valueOf(0));
        }
        if (totalQuery != null) {
            List<FacetField.Count> values = totalQuery.getValues();
            long size = values.size();

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (!idcardValidator.isIdcard(count.getName())) {
                    size--;
                }
            }
            totalResult.put("total", size);
        } else {
            totalResult.put("total", Long.valueOf(0));
        }

        totalResult.put("repeat", repeatCount);
        repeat.clear();
        repeatCount = 0;

        if (mzIntervalQuery != null) {
            List<FacetField.Count> values = mzIntervalQuery.getValues();
            long size = 0;
            long sizeNoRepeat = values.size();

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (idcardValidator.isIdcard(count.getName())) {
                    size += count.getCount();
                    repeat.add(count.getName());
                } else {
                    sizeNoRepeat--;
                }
            }
            intervalResult.put("outpatient", size);
        } else {
            intervalResult.put("outpatient", Long.valueOf(0));
        }
        if (zyIntervalQuery != null) {
            List<FacetField.Count> values = zyIntervalQuery.getValues();
            long size = 0;
            long sizeNoRepeat = values.size();

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (idcardValidator.isIdcard(count.getName())) {
                    size += count.getCount();
                    if (!repeat.add(count.getName())) {
                        repeatCount++;
                    }
                } else {
                    sizeNoRepeat--;
                }
            }
            intervalResult.put("hospitalization", size);
        } else {
            intervalResult.put("hospitalization", Long.valueOf(0));
        }
        if (endDateQuery != null) {
            List<FacetField.Count> values = endDateQuery.getValues();
            long size = 0;

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (idcardValidator.isIdcard(count.getName())) {
                    size += count.getCount();
                }
            }
            intervalResult.put("endTotal", size);
        } else {
            intervalResult.put("endTotal", Long.valueOf(0));
        }
        if (intervalTotalQuery != null) {
            List<FacetField.Count> values = intervalTotalQuery.getValues();
            long size = 0;

            //验证身份证有效性
            for (FacetField.Count count : values) {
                if (idcardValidator.isIdcard(count.getName())) {
                    size += count.getCount();
                }
            }
            intervalResult.put("total", size);
        } else {
            intervalResult.put("total", Long.valueOf(0));
        }

        intervalResult.put("repeat", repeatCount);
        repeat.clear();
        repeatCount = 0;

        result.put("total", totalResult);
        result.put("period", intervalResult);

        return result;
    }

    /**
     * 按事件时间指定步长统计
     *
     * @param orgCode
     * @param startTime
     * @param endTime
     * @param groupType
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
    public Map<String, Long> groupStatistics(String field, String orgCode, String eventDate, String eventType, boolean missing) throws Exception {
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
            if (total > 0) {
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
        int statType = 0;
        String[] stats = items.split(",");
        String orgCode = params.has("orgCode") ? params.get("orgCode").textValue() : "";
        String startDate = params.has("startDate") ? params.get("startDate").textValue() : "";
        String endDate = params.has("endDate") ? params.get("endDate").textValue() : "";
        String grap = params.has("grap") ? params.get("grap").textValue() : "";
        String eventType = params.has("eventType") ? params.get("eventType").textValue() : "";

        for (String stat : stats) {
            switch (stat) {
                case "byCreateDate":
                    result.put("byCreateDate", resolveStatisticsByCreateDateOrEventDate(orgCode, startDate, endDate, 1));
                    break;
                case "byEventDate":
                    result.put("byEventDate", resolveStatisticsByCreateDateOrEventDate(orgCode, startDate, endDate, 2));
                    break;
                case "byIdNotNull":
                    result.put("byIdNotNull", statisticsByIdNotNull(orgCode, startDate, endDate));
                    break;
                case "byEventDateGroup":
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
    public Map<String, Object> outpatientAndHospitalStatistics(String items, JsonNode params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        int statType = 0;
        String[] stats = items.split(",");
        String orgCode = params.has("orgCode") ? params.get("orgCode").textValue() : "";
        String eventDate = params.has("eventDate") ? params.get("eventDate").textValue() : "";

        for (String stat : stats) {
            switch (stat) {
                case "outpatientDept":
                    result.put(stat, groupStatistics("EHR_000082", orgCode, eventDate, "0", true));
                    break;
                case "outpatientSex":
                    result.put(stat, groupStatistics("EHR_000019_VALUE", orgCode, eventDate, "0", true));
                    break;
                case "outpatient":
                    result.put(stat, filterQueryStatistics(orgCode, eventDate, "0"));
                    break;
                case "hospitalDept":
                    result.put(stat, groupStatistics("EHR_000223", orgCode, eventDate, "1", true));
                    break;
                case "hospitalSex":
                    result.put(stat, groupStatistics("EHR_000019_VALUE", orgCode, eventDate, "1", true));
                    break;
                case "hospitalDisease":
                    result.put(stat, groupStatistics("EHR_000163_VALUE", orgCode, eventDate, "1", true));
                    break;
                case "hospital":
                    result.put(stat, filterQueryStatistics(orgCode, eventDate, "1"));
                    break;
            }
        }

        return result;
    }
}