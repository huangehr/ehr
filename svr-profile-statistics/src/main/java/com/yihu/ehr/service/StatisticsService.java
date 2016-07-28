package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.dao.model.DailyMonitorFile;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.OrgKeySchema;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.ExcelUtils;
import com.yihu.ehr.util.IdcardValidator;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yihu.ehr.config.FastDFSConfig;

import java.io.*;
import java.util.*;

/**
 * Created by lyr on 2016/7/21.
 */
@Service
public class StatisticsService {

    private static String core = "HealthProfile";

    @Autowired
    FastDFSConfig FastDFSConfig;

    @Autowired
    SolrUtil solrUtil;

    @Autowired
    IdcardValidator idcardValidator;

    @Autowired
    RedisClient redisClient;

    @Autowired
    OrgKeySchema orgKeySchema;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DailyMonitorService dailyMonitorService;

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
     * 可识别身份入库统计
     *
     * @param orgCode
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
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

        //累计统计
        Map<String, Long> totalResult = new HashMap<>();
        //期间统计
        Map<String, Long> intervalResult = new HashMap<>();
        //身份证号码集(计算门诊住院重复用)
        Set<String> repeat = new HashSet<>();
        //门诊住院重复数
        long repeatCount = 0;

        //门诊可识别人数
        FacetField mzQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:0", 1, 0, -1, false);
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
            mzQuery = null;
        } else {
            totalResult.put("outpatient", Long.valueOf(0));
        }

        //住院可识别人数
        FacetField zyQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:1", 1, 0, -1, false);
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
            zyQuery = null;
        } else {
            totalResult.put("hospitalization", Long.valueOf(0));
        }

        //全库合计
        FacetField totalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam, 1, 0, -1, false);
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
            totalQuery = null;
        } else {
            totalResult.put("total", Long.valueOf(0));
        }

        //累计门诊住院重复人数
        totalResult.put("repeat", repeatCount);
        repeat.clear();
        repeatCount = 0;

        //期间门诊可识别人次
        FacetField mzIntervalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:0" + dq, 1, 0, -1, false);
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
            mzIntervalQuery = null;
        } else {
            intervalResult.put("outpatient", Long.valueOf(0));
        }

        //期间住院可识别人次
        FacetField zyIntervalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:1" + dq, 1, 0, -1, false);
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
            zyIntervalQuery = null;
        } else {
            intervalResult.put("hospitalization", Long.valueOf(0));
        }

        //截止结束时间可识别人次
        FacetField endDateQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + " && event_type:1"
                + " && event_date:[* TO " + end + "]", 1, 0, -1, false);
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
            endDateQuery = null;
        } else {
            intervalResult.put("endTotal", Long.valueOf(0));
        }

        //期间合计可识别人次
        FacetField intervalTotalQuery = solrUtil.getFacetField("HealthProfile", "demographic_id", orgParam + dq, 1, 0, -1, false);
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
            intervalTotalQuery = null;
        } else {
            intervalResult.put("total", Long.valueOf(0));
        }

        //期间门诊住院重复人数
        intervalResult.put("repeat", repeatCount);
        repeat.clear();

        //累计
        result.put("total", totalResult);
        //期间
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
        int statType = 0;
        String[] stats = items.split(",");
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

    /**
     * 生成日常监测文件
     *
     * @param date
     * @return
     * @throws Exception
     */
    public DailyMonitorFile generateDailyReportFile(String date) throws Exception {
        HSSFWorkbook wb = ExcelUtils.createWorkBook();

        boolean totalFlag = generateTotalReportSheet(wb, date);
        boolean orgFlag = generateOrgReportSheets(wb, date);

        if (totalFlag || orgFlag) {
            //输出文件
            String rootPath = StatisticsService.class.getResource("/").getPath();
            rootPath = new File(rootPath).getParent() + Path.SEPARATOR + "dailyReport.xlsx";
            FileOutputStream out = new FileOutputStream(rootPath);
            wb.write(out);
            out.close();

            //保存到fastdfs
            InputStream in = new FileInputStream(rootPath);
            FastDFSUtil fdfs = FastDFSConfig.fastDFSUtil();
            ObjectNode jsonResult = fdfs.upload(in, "xlsx", "");
            String fastDfsFileName = jsonResult.get("fid").textValue();
            in.close();
            new File(rootPath).delete();
            return dailyMonitorService.saveDailyMonitorFile(date.replaceAll("-", ""), fastDfsFileName);
        }
        return null;
    }

    /**
     * 日报监测数据生成
     *
     * @param date
     * @return
     * @throws Exception
     */
    public Map<String, Object> generateDailyProfileReportData(String date) throws Exception {
        Map<String, Object> result = new HashMap<>();
        //入库时间条件
        String dq = "create_date:[" + date + "T00:00:00Z TO " + date + "T23:59:59Z]";
        //查询时间范围内有入库机构
        FacetField facetField = solrUtil.getFacetField(core, "org_code", dq, 1, 0, -1, false);

        if (facetField != null && facetField.getValues() != null) {
            List<FacetField.Count> counts = facetField.getValues();

            for (FacetField.Count count : counts) {
                Object orgResult = generateOrgDailyProfileReportData(count.getName(), date);
                result.put(count.getName(), orgResult);
            }
        }

        return result;
    }

    /**
     * 统计机构某天档案入库统计
     *
     * @param orgCode
     * @param date
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, Long>> generateOrgDailyProfileReportData(String orgCode, String date) throws Exception {
        if (StringUtils.isBlank(orgCode)) {
            LogService.getLogger("daily monitor report").error("orgCode is null");
            throw new Exception("orgCode is null");
        }
        if (StringUtils.isBlank(date)) {
            LogService.getLogger("daily monitor report").error("date is null");
            throw new Exception("date is null");
        }

        Map<String, Map<String, Long>> result = new HashMap<>();
        String orgParam = "org_code:" + orgCode;
        String dq = " && create_date:[" + date + "T00:00:00Z TO " + date + "T23:59:59Z]";

        /*--------------累计入库档案数-----------*/
        //累计门诊
        long totalMz = fqStatistics(orgParam + " && event_type:0");
        //累计住院
        long totalZy = fqStatistics(orgParam + " && event_type:1");
        //累计入库统计结果
        Map<String, Long> totalResult = new HashMap<>();
        totalResult.put("outpatient", totalMz);
        totalResult.put("hospitalization", totalZy);
        totalResult.put("total", totalMz + totalZy);

        /*--------------当天入库档案数------------*/
        //当天门诊
        long intervalMz = fqStatistics(orgParam + dq + " && event_type:0");
        //当天住院
        long intervalZy = fqStatistics(orgParam + dq + " && event_type:1");
        //当天入库统计结果
        Map<String, Long> intervalResult = new HashMap<>();
        intervalResult.put("outpatient", intervalMz);
        intervalResult.put("hospitalization", intervalZy);
        intervalResult.put("total", intervalMz + intervalZy);

        /*------------累计入库可识别人数-----------*/
        //累计可识别身份入库统计结果
        Map<String, Long> totalIdResult = new HashMap<>();

        //累计门诊可识别
        FacetField totalIdMzFacet = solrUtil.getFacetField(core, "demographic_id", orgParam + " && event_type:0", 1, 0, -1, false);
        if (totalIdMzFacet != null && totalIdMzFacet.getValues() != null) {
            List<FacetField.Count> counts = totalIdMzFacet.getValues();
            long size = counts.size();

            for (FacetField.Count count : counts) {
                if (!idcardValidator.isIdcard(count.getName())) {
                    size--;
                }
            }

            totalIdResult.put("outpatient", Long.valueOf(size));
            totalIdMzFacet = null;
        } else {
            totalIdResult.put("outpatient", Long.valueOf(0));
        }

        //累计住院可识别
        FacetField totalIdZyFacet = solrUtil.getFacetField(core, "demographic_id", orgParam + " && event_type:1", 1, 0, -1, false);
        if (totalIdZyFacet != null) {
            List<FacetField.Count> counts = totalIdZyFacet.getValues();
            long size = counts.size();

            for (FacetField.Count count : counts) {
                if (!idcardValidator.isIdcard(count.getName())) {
                    size--;
                }
            }

            totalIdResult.put("hospitalization", Long.valueOf(size));
            totalIdZyFacet = null;
        } else {
            totalIdResult.put("hospitalization", Long.valueOf(0));
        }

        totalIdResult.put("total", totalIdResult.get("outpatient") + totalIdResult.get("hospitalization"));

        /*------------期间入库可识别人次-----------*/
        //期间入库可识别人次统计结果
        Map<String, Long> intervalIdResult = new HashMap<>();

        //期间门诊可识别
        FacetField intervalIdMzFacet = solrUtil.getFacetField(core, "demographic_id", orgParam + dq + " && event_type:0", 1, 0, -1, false);
        if (intervalIdMzFacet != null && intervalIdMzFacet.getValues() != null) {
            List<FacetField.Count> counts = intervalIdMzFacet.getValues();
            long size = 0;

            for (FacetField.Count count : counts) {
                if (idcardValidator.isIdcard(count.getName())) {
                    size += count.getCount();
                }
            }

            intervalIdResult.put("outpatient", Long.valueOf(size));
            intervalIdMzFacet = null;
        } else {
            intervalIdResult.put("outpatient", Long.valueOf(0));
        }

        //期间住院可识别
        FacetField intervalIdMzFacetZyFacet = solrUtil.getFacetField(core, "demographic_id", orgParam + dq + " && event_type:1", 1, 0, -1, false);
        if (intervalIdMzFacetZyFacet != null && intervalIdMzFacetZyFacet.getValues() != null) {
            List<FacetField.Count> counts = intervalIdMzFacetZyFacet.getValues();
            long size = 0;

            for (FacetField.Count count : counts) {
                if (idcardValidator.isIdcard(count.getName())) {
                    size += count.getCount();
                }
            }

            intervalIdResult.put("hospitalization", Long.valueOf(size));
            intervalIdMzFacetZyFacet = null;
        } else {
            intervalIdResult.put("hospitalization", Long.valueOf(0));
        }

        intervalIdResult.put("total", intervalIdResult.get("outpatient") + intervalIdResult.get("hospitalization"));

        result.put("total", totalResult);
        result.put("interval", intervalResult);
        result.put("totalId", totalIdResult);
        result.put("intervalId", intervalIdResult);

        return result;
    }

    /**
     * 汇总统计表生成
     *
     * @param wb
     * @param sheet
     * @param reportData
     * @param date
     */
    public boolean generateTotalReportSheet(HSSFWorkbook wb, String date) throws Exception {
        Map<String, Object> reportData = generateDailyProfileReportData(date);

        if (reportData != null && reportData.size() > 0) {
            HSSFSheet sheet = ExcelUtils.createSheet(wb, "汇总统计表");
            Set<String> keys = reportData.keySet();
            HSSFCellStyle leftAlignStyle = ExcelUtils.createCellStyle(wb, HSSFCellStyle.ALIGN_LEFT, false, false, (short) -1);
            HSSFCellStyle centerBoldStyle = ExcelUtils.createCellStyle(wb, HSSFCellStyle.ALIGN_CENTER, true, false, (short) -1);
            HSSFCellStyle centerStyle = ExcelUtils.createCellStyle(wb, HSSFCellStyle.ALIGN_CENTER, false, true, (short) -1);

            //合并单元格
            ExcelUtils.createRows(sheet, 0, 2, 13, null);
            ExcelUtils.createRows(sheet, 2, 2, 13, centerStyle);
            ExcelUtils.mergeRegion(sheet, 1, 1, 0, 12);
            ExcelUtils.mergeRegion(sheet, 2, 3, 0, 0);
            ExcelUtils.mergeRegion(sheet, 2, 2, 1, 3);
            ExcelUtils.mergeRegion(sheet, 2, 2, 4, 6);
            ExcelUtils.mergeRegion(sheet, 2, 2, 7, 9);
            ExcelUtils.mergeRegion(sheet, 2, 2, 10, 12);

            sheet.getRow(0).getCell(0).setCellStyle(leftAlignStyle);
            sheet.getRow(1).getCell(0).setCellStyle(centerBoldStyle);

            //设置日期、标题、列标题
            sheet.getRow(0).getCell(0).setCellValue("日期:" + date.replaceAll("-", "/"));
            sheet.getRow(1).getCell(0).setCellValue("健康档案管理平台数据采集汇总统计报表");
            sheet.getRow(2).getCell(0).setCellValue("机构名称");
            sheet.getRow(2).getCell(1).setCellValue("累计入库档案数");
            sheet.getRow(2).getCell(4).setCellValue("当日入库档案数");
            sheet.getRow(2).getCell(7).setCellValue("累计可识别人数");
            sheet.getRow(2).getCell(10).setCellValue("区间新增加可识别就诊人次");
            sheet.getRow(3).getCell(1).setCellValue("门诊");
            sheet.getRow(3).getCell(2).setCellValue("住院");
            sheet.getRow(3).getCell(3).setCellValue("合计");
            sheet.getRow(3).getCell(4).setCellValue("门诊");
            sheet.getRow(3).getCell(5).setCellValue("住院");
            sheet.getRow(3).getCell(6).setCellValue("合计");
            sheet.getRow(3).getCell(7).setCellValue("门诊");
            sheet.getRow(3).getCell(8).setCellValue("住院");
            sheet.getRow(3).getCell(9).setCellValue("合计");
            sheet.getRow(3).getCell(10).setCellValue("门诊");
            sheet.getRow(3).getCell(11).setCellValue("住院");
            sheet.getRow(3).getCell(12).setCellValue("合计");

            int rowNum = 4;
            //数据
            for (String key : keys) {
                HSSFRow row = ExcelUtils.createRow(sheet, rowNum, 13, centerStyle);
                String orgName = redisClient.get(orgKeySchema.name(key));

                Map<String, Map<String, Long>> data = (Map<String, Map<String, Long>>) reportData.get(key);

                //机构名称
                row.getCell(0).setCellValue(StringUtils.isBlank(orgName) ? key : orgName);
                //累计入库
                row.getCell(1).setCellValue(data.get("total").get("outpatient"));
                row.getCell(2).setCellValue(data.get("total").get("hospitalization"));
                row.getCell(3).setCellValue(data.get("total").get("total"));
                //当天入库
                row.getCell(4).setCellValue(data.get("interval").get("outpatient"));
                row.getCell(5).setCellValue(data.get("interval").get("hospitalization"));
                row.getCell(6).setCellValue(data.get("interval").get("total"));
                //累计可识别
                row.getCell(7).setCellValue(data.get("totalId").get("outpatient"));
                row.getCell(8).setCellValue(data.get("totalId").get("hospitalization"));
                row.getCell(9).setCellValue(data.get("totalId").get("total"));
                //期间可识别
                row.getCell(10).setCellValue(data.get("intervalId").get("outpatient"));
                row.getCell(11).setCellValue(data.get("intervalId").get("hospitalization"));
                row.getCell(12).setCellValue(data.get("intervalId").get("total"));

                rowNum++;
            }

            //合计行
            if (rowNum > 4) {
                HSSFRow row = ExcelUtils.createRow(sheet, rowNum, 13, centerStyle);

                //机构名称
                row.getCell(0).setCellValue("总计");
                //累计入库
                row.getCell(1).setCellFormula("SUM(B5:B" + rowNum + ")");
                row.getCell(2).setCellFormula("SUM(C5:C" + rowNum + ")");
                row.getCell(3).setCellFormula("SUM(D5:D" + rowNum + ")");
                //当天入库
                row.getCell(4).setCellFormula("SUM(E5:E" + rowNum + ")");
                row.getCell(5).setCellFormula("SUM(F5:F" + rowNum + ")");
                row.getCell(6).setCellFormula("SUM(G5:G" + rowNum + ")");
                //累计可识别
                row.getCell(7).setCellFormula("SUM(H5:H" + rowNum + ")");
                row.getCell(8).setCellFormula("SUM(I5:I" + rowNum + ")");
                row.getCell(9).setCellFormula("SUM(J5:J" + rowNum + ")");
                //期间可识别
                row.getCell(10).setCellFormula("SUM(K5:K" + rowNum + ")");
                row.getCell(11).setCellFormula("SUM(L5:L" + rowNum + ")");
                row.getCell(12).setCellFormula("SUM(M5:M" + rowNum + ")");
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 生成机构人次指标报表
     *
     * @param wb
     * @param date
     * @throws Exception
     */
    public boolean generateOrgReportSheets(HSSFWorkbook wb, String date) throws Exception {
        //入库时间条件
        String dq = "event_date:[" + date + "T00:00:00Z TO " + date + "T23:59:59Z]";
        //查询时间范围内有入库机构
        FacetField facetField = solrUtil.getFacetField(core, "org_code", dq, 1, 0, -1, false);

        if (facetField != null && facetField.getValues() != null) {
            List<FacetField.Count> counts = facetField.getValues();

            for (FacetField.Count count : counts) {
                String orgName = redisClient.get(orgKeySchema.name(count.getName()));
                HSSFSheet sheet = ExcelUtils.createSheet(wb, "机构" + (StringUtils.isBlank(orgName) ? count.getName() : orgName)
                        + "统计表(" + date.replaceAll("-", "") + ")");
                generateOrgReportSheet(wb, sheet, count.getName(), date);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 生成机构人次指标报表
     *
     * @param wb
     * @param sheet
     * @param orgCode
     * @param date
     * @throws Exception
     */
    public void generateOrgReportSheet(HSSFWorkbook wb, HSSFSheet sheet, String orgCode, String date) throws Exception {
        String items = "outpatientDept,outpatientSex,hospitalDept,hospitalSex,hospitalDisease";
        String params = "{\"orgCode\":\"" + orgCode + "\",\"eventDate\":\"" + date + "\"}";
        String orgName = redisClient.get(orgKeySchema.name(orgCode));

        Map<String, Object> data = outpatientAndHospitalStatistics(items, objectMapper.readTree(params), true);

        if (data != null && data.size() > 0) {
            //单元格样式
            HSSFCellStyle leftStyle = ExcelUtils.createCellStyle(wb, HSSFCellStyle.ALIGN_LEFT, false, false, (short) -1);
            HSSFCellStyle centerStyle = ExcelUtils.createCellStyle(wb, HSSFCellStyle.ALIGN_CENTER, false, true, (short) -1);
            HSSFCellStyle centerBackStyle = ExcelUtils.createCellStyle(wb, HSSFCellStyle.ALIGN_CENTER, false, true, HSSFColor.LIGHT_BLUE.index);
            //合并单元格
            ExcelUtils.createRows(sheet, 0, 2, 16, null);
            ExcelUtils.createRows(sheet, 2, 4, 16, null);
            ExcelUtils.mergeRegion(sheet, 0, 0, 0, 3);
            ExcelUtils.mergeRegion(sheet, 1, 1, 0, 1);
            ExcelUtils.mergeRegion(sheet, 1, 1, 5, 6);
            ExcelUtils.mergeRegion(sheet, 1, 1, 11, 12);
            ExcelUtils.mergeRegion(sheet, 2, 2, 6, 7);
            ExcelUtils.mergeRegion(sheet, 2, 2, 8, 9);
            ExcelUtils.mergeRegion(sheet, 2, 2, 12, 13);
            ExcelUtils.mergeRegion(sheet, 2, 2, 14, 15);

            //机构名称
            sheet.getRow(0).getCell(0).setCellValue(StringUtils.isBlank(orgName) ? orgCode : orgName);

            //统计类别
            HSSFRow titleRow = sheet.getRow(1);
            titleRow.getCell(0).setCellStyle(leftStyle);
            titleRow.getCell(5).setCellStyle(leftStyle);
            titleRow.getCell(11).setCellStyle(leftStyle);
            titleRow.getCell(0).setCellValue("按性别人次统计");
            titleRow.getCell(5).setCellValue("按科室人次统计");
            titleRow.getCell(11).setCellValue("按疾病人次统计");

            //列标题
            HSSFRow colTitleRowFirst = sheet.getRow(2);
            colTitleRowFirst.getCell(0).setCellValue("序号");
            colTitleRowFirst.getCell(0).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(1).setCellValue("性别");
            colTitleRowFirst.getCell(1).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(2).setCellValue("门诊人次");
            colTitleRowFirst.getCell(2).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(3).setCellValue("住院人次");
            colTitleRowFirst.getCell(3).setCellStyle(centerBackStyle);

            colTitleRowFirst.getCell(5).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(6).setCellValue("门诊");
            colTitleRowFirst.getCell(6).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(7).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(8).setCellValue("住院");
            colTitleRowFirst.getCell(8).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(9).setCellStyle(centerBackStyle);

            colTitleRowFirst.getCell(11).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(12).setCellValue("门诊");
            colTitleRowFirst.getCell(12).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(13).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(14).setCellValue("住院");
            colTitleRowFirst.getCell(14).setCellStyle(centerBackStyle);
            colTitleRowFirst.getCell(15).setCellStyle(centerBackStyle);

            HSSFRow colTitleRowSecond = sheet.getRow(3);
            colTitleRowSecond.getCell(0).setCellValue("1");
            colTitleRowSecond.getCell(0).setCellStyle(centerStyle);
            colTitleRowSecond.getCell(1).setCellValue("男性");
            colTitleRowSecond.getCell(1).setCellStyle(centerStyle);
            colTitleRowSecond.getCell(2).setCellStyle(centerStyle);
            colTitleRowSecond.getCell(3).setCellStyle(centerStyle);
            colTitleRowSecond.getCell(5).setCellValue("序号");
            colTitleRowSecond.getCell(5).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(6).setCellValue("科室");
            colTitleRowSecond.getCell(6).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(7).setCellValue("门诊人次");
            colTitleRowSecond.getCell(7).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(8).setCellValue("科室");
            colTitleRowSecond.getCell(8).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(9).setCellValue("出院人次");
            colTitleRowSecond.getCell(9).setCellStyle(centerBackStyle);

            colTitleRowSecond.getCell(11).setCellValue("序号");
            colTitleRowSecond.getCell(11).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(12).setCellValue("疾病名称");
            colTitleRowSecond.getCell(12).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(13).setCellValue("出院人次");
            colTitleRowSecond.getCell(13).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(14).setCellValue("疾病名称");
            colTitleRowSecond.getCell(14).setCellStyle(centerBackStyle);
            colTitleRowSecond.getCell(15).setCellValue("门诊人次");
            colTitleRowSecond.getCell(15).setCellStyle(centerBackStyle);

            sheet.getRow(4).getCell(0).setCellValue("2");
            sheet.getRow(4).getCell(0).setCellStyle(centerStyle);
            sheet.getRow(4).getCell(1).setCellValue("女性");
            sheet.getRow(4).getCell(1).setCellStyle(centerStyle);
            sheet.getRow(4).getCell(2).setCellStyle(centerStyle);
            sheet.getRow(4).getCell(3).setCellStyle(centerStyle);

            sheet.getRow(5).getCell(0).setCellStyle(centerStyle);
            sheet.getRow(5).getCell(1).setCellValue("合计");
            sheet.getRow(5).getCell(1).setCellStyle(centerStyle);
            sheet.getRow(5).getCell(2).setCellStyle(centerStyle);
            sheet.getRow(5).getCell(3).setCellStyle(centerStyle);

            //门诊性别
            if (data.containsKey("outpatientSex") && ((Map<String, Long>) data.get("outpatientSex")).size() > 0) {
                Map<String, Long> sexData = (Map<String, Long>) data.get("outpatientSex");

                sheet.getRow(3).getCell(2).setCellValue(sexData.containsKey("男性") ? sexData.get("男性") : 0);
                sheet.getRow(4).getCell(2).setCellValue(sexData.containsKey("女性") ? sexData.get("女性") : 0);
                sheet.getRow(5).getCell(2).setCellFormula("SUM(C4:C5)");
            }
            //出院性别
            if (data.containsKey("hospitalSex") && ((Map<String, Long>) data.get("hospitalSex")).size() > 0) {
                Map<String, Long> sexData = (Map<String, Long>) data.get("hospitalSex");

                sheet.getRow(3).getCell(3).setCellValue(sexData.containsKey("男性") ? sexData.get("男性") : 0);
                sheet.getRow(4).getCell(3).setCellValue(sexData.containsKey("女性") ? sexData.get("女性") : 0);
                sheet.getRow(5).getCell(3).setCellFormula("SUM(D4:D5)");
            }
            //门诊科室
            if (data.containsKey("outpatientDept") && ((Map<String, Long>) data.get("outpatientDept")).size() > 0) {
                Map<String, Long> deptData = (Map<String, Long>) data.get("outpatientDept");
                int count = 0;

                for (String key : deptData.keySet()) {
                    HSSFRow row;
                    if (count > 1) {
                        row = ExcelUtils.createRow(sheet, 4 + count, 16, null);
                    } else {
                        row = sheet.getRow(4 + count);
                    }

                    row.getCell(5).setCellStyle(centerStyle);
                    row.getCell(6).setCellStyle(centerStyle);
                    row.getCell(7).setCellStyle(centerStyle);
                    row.getCell(8).setCellStyle(centerStyle);
                    row.getCell(9).setCellStyle(centerStyle);

                    row.getCell(5).setCellValue(count + 1);
                    row.getCell(6).setCellValue(key);
                    row.getCell(7).setCellValue(deptData.get(key));
                    count++;
                }

                //合计行
                if (count > 0) {
                    HSSFRow row;
                    if ((4 + count) > sheet.getLastRowNum()) {
                        row = ExcelUtils.createRow(sheet, 4 + count, 16, null);
                    } else {
                        row = sheet.getRow(4 + count);
                    }
                    row.getCell(5).setCellStyle(centerStyle);
                    row.getCell(6).setCellStyle(centerStyle);
                    row.getCell(7).setCellStyle(centerStyle);
                    row.getCell(8).setCellStyle(centerStyle);
                    row.getCell(9).setCellStyle(centerStyle);

                    row.getCell(6).setCellValue("合计");
                    row.getCell(7).setCellFormula("SUM(H5:H" + (4 + count) + ")");
                }
            }
            //出院科室
            if (data.containsKey("hospitalDept") && ((Map<String, Long>) data.get("hospitalDept")).size() > 0) {
                Map<String, Long> deptData = (Map<String, Long>) data.get("hospitalDept");
                int count = 0;

                for (String key : deptData.keySet()) {
                    HSSFRow row;
                    if ((4 + count) > sheet.getLastRowNum()) {
                        row = ExcelUtils.createRow(sheet, 4 + count, 16, null);
                    } else {
                        row = sheet.getRow(4 + count);
                    }

                    row.getCell(5).setCellStyle(centerStyle);
                    row.getCell(6).setCellStyle(centerStyle);
                    row.getCell(7).setCellStyle(centerStyle);
                    row.getCell(8).setCellStyle(centerStyle);
                    row.getCell(9).setCellStyle(centerStyle);

                    row.getCell(5).setCellValue(count + 1);
                    row.getCell(8).setCellValue(key);
                    row.getCell(9).setCellValue(deptData.get(key));
                    count++;
                }

                if (count > 0) {
                    HSSFRow row;
                    if ((4 + count) > sheet.getLastRowNum()) {
                        row = ExcelUtils.createRow(sheet, 4 + count, 16, null);
                    } else {
                        row = sheet.getRow(4 + count);
                    }
                    row.getCell(5).setCellStyle(centerStyle);
                    row.getCell(6).setCellStyle(centerStyle);
                    row.getCell(7).setCellStyle(centerStyle);
                    row.getCell(8).setCellStyle(centerStyle);
                    row.getCell(9).setCellStyle(centerStyle);

                    row.getCell(8).setCellValue("合计");
                    row.getCell(9).setCellFormula("SUM(J5:J" + (4 + count) + ")");
                }
            }
            //出院疾病
            if (data.containsKey("hospitalDisease") && ((Map<String, Long>) data.get("hospitalDisease")).size() > 0) {
                Map<String, Long> diseaseData = (Map<String, Long>) data.get("hospitalDisease");
                int count = 0;

                for (String key : diseaseData.keySet()) {
                    HSSFRow row;
                    if ((4 + count) > sheet.getLastRowNum()) {
                        row = ExcelUtils.createRow(sheet, 4 + count, 16, null);
                    } else {
                        row = sheet.getRow(4 + count);
                    }

                    row.getCell(11).setCellStyle(centerStyle);
                    row.getCell(12).setCellStyle(centerStyle);
                    row.getCell(13).setCellStyle(centerStyle);
                    row.getCell(14).setCellStyle(centerStyle);
                    row.getCell(15).setCellStyle(centerStyle);

                    row.getCell(11).setCellValue(count + 1);
                    row.getCell(12).setCellValue(key);
                    row.getCell(13).setCellValue(diseaseData.get(key));
                    count++;
                }

                if (count > 0) {
                    HSSFRow row;
                    if ((4 + count) > sheet.getLastRowNum()) {
                        row = ExcelUtils.createRow(sheet, 4 + count, 16, null);
                    } else {
                        row = sheet.getRow(4 + count);
                    }
                    row.getCell(11).setCellStyle(centerStyle);
                    row.getCell(12).setCellStyle(centerStyle);
                    row.getCell(13).setCellStyle(centerStyle);
                    row.getCell(14).setCellStyle(centerStyle);
                    row.getCell(15).setCellStyle(centerStyle);

                    row.getCell(12).setCellValue("合计");
                    row.getCell(13).setCellFormula("SUM(N5:N" + (4 + count) + ")");
                }
            }
        }
    }
}