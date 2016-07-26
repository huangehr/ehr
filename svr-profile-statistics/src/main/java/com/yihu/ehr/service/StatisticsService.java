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
     * 根据入库时间统计
     *
     * @param orgCode
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public long resolveStatisticsByCreateDate(String orgCode, String startDate, String endDate) throws Exception {
        StringBuilder fq = new StringBuilder();

        //机构条件
        fq.append("org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode));
        //入库时间
        fq.append(" && create_date:[");
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

        Map<String, Integer> query = solrUtil.getFacetQuery("HealthProfile", fq.toString());

        if (query != null) {
            return query.get(fq.toString());
        } else {
            return 0;
        }
    }

    /**
     * 根据事件时间统计
     *
     * @param orgCode
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public long resolveStatisticsByEventDate(String orgCode, String startDate, String endDate) throws Exception {
        StringBuilder fq = new StringBuilder();

        //机构条件
        fq.append("org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode));
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

        Map<String, Integer> query = solrUtil.getFacetQuery("HealthProfile", fq.toString());

        if (query != null) {
            return query.get(fq.toString());
        } else {
            return 0;
        }
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
    public Map<String, String> resolveStatisticsByEventDateGroup(String orgCode, String startDate, String endDate, String grap) throws Exception {
        String fq = "org_code:" + (StringUtils.isBlank(orgCode) ? "*" : orgCode);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(DateTimeUtil.simpleDateTimeParse(startDate + " 00:00:00"));
        start.add(Calendar.HOUR, 8);
        end.setTime(DateTimeUtil.simpleDateTimeParse(endDate + " 00:00:00"));
        end.add(Calendar.HOUR, 8);

        Map<String, String> result = new TreeMap<String, String>();

        List<RangeFacet> values = solrUtil.getFacetDateRange("HealthProfile", "event_date", start.getTime(), end.getTime(), grap, fq);

        if (values != null && values.size() > 0) {
            List<RangeFacet.Count> counts = values.get(0).getCounts();
            for (RangeFacet.Count count : counts) {
                result.put(count.getValue(), Long.toString(count.getCount()));
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
    public Map<String, String> groupStatistics(String field, String orgCode, String eventDate, String eventType) throws Exception {
        StringBuilder fq = new StringBuilder();
        Map<String, String> result = new TreeMap<>();

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
        FacetField facetField = solrUtil.getFacetField(core, field, fq.toString(), 1, 0, -1, false);

        if (facetField != null) {
            List<FacetField.Count> values = facetField.getValues();
            //查询结果处理
            for (FacetField.Count value : values) {
                //key为null或空字符串的统一为""
                String key = StringUtils.isBlank(value.getName()) ? "" : value.getName();
                long val = value.getCount();
                if (key.length() == 0 && result.get(key) != null) {
                    val += Long.parseLong(result.get(key));
                }
                result.put(key, Long.toString(val));
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
     * 指标查询
     *
     * @param indexId
     * @param params
     * @return
     */
    public Object indexStatistics(String indexId, JsonNode params) {
        int statType = 0;

        switch (statType) {
            case 1:

                break;
            case 2:
                break;
        }

        return null;
    }
}