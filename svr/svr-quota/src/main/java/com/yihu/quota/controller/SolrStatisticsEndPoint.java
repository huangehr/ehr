package com.yihu.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.org.OrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "SolrStatisticsEndPoint", description = "门急诊服务统计接口", tags = {"临时报表接口--门急诊服务统计接口"})
public class SolrStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private SolrUtil solr;
    @Autowired
    private OrgService orgService;

    @ApiOperation("本月科室门诊人次")
    @RequestMapping(value = ServiceApi.OutpatientServiceStatistic.StatisticDeptOutpatientSum, method = RequestMethod.GET)
    public Envelop statisticDeptOutpatientSum() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String startDay = getCurrMonthFirstDay();
            String endDay = getCurrMonthLastDay();
            String fq = String.format("event_type:0 AND event_date:[%s TO %s]", startDay, endDay);
            String facetField = "EHR_000081"; // 按【科室】分组
            Map<String, Object> result = oneFieldGroup(facetField, fq);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("统计【本月科室门诊人次】发生异常");
        }
        return envelop;
    }

    @ApiOperation("本月科室转诊人次")
    @RequestMapping(value = ServiceApi.OutpatientServiceStatistic.StatisticDeptTransferTreatmentSum, method = RequestMethod.GET)
    public Envelop statisticDeptTransferTreatmentSum() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String startDay = getCurrMonthFirstDay();
            String endDay = getCurrMonthLastDay();
            String fq = String.format("event_type:0 AND EHR_000083:T AND event_date:[%s TO %s]", startDay, endDay);
            String facetField = "EHR_000081"; // 按【科室】分组
            Map<String, Object> result = oneFieldGroup(facetField, fq);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("统计【本月科室转诊人次】发生异常");
        }
        return envelop;
    }

    /**
     * 根据指定字段分组统计
     * @param facetField 分组字段名
     * @param fq 筛选条件
     * @return
     * @throws Exception
     */
    private Map<String, Object> oneFieldGroup(String facetField, String fq) throws Exception {
        String startDay = getCurrMonthFirstDay();
        String endDay = getCurrMonthLastDay();
        FacetField facetResult = solr.getFacetField("HealthProfile", facetField, fq, 0, 0, -1, false);
        List<FacetField.Count> facetCountList = facetResult.getValues();
        List<String> nameList = new ArrayList<>();
        List<Long> valList = new ArrayList<>();
        for(FacetField.Count item : facetCountList) {
            nameList.add(item.getName());
            valList.add(item.getCount());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("nameList", nameList);
        result.put("valList", valList);
        return result;
    }

    @ApiOperation("当月相关数据")
    @RequestMapping(value = "/statistics/{position}", method = RequestMethod.POST)
    public Envelop statistics(
            @ApiParam(name = "core", value = "集合", required = true)
            @RequestParam(value = "core") String core,
            @ApiParam(name = "position", value = "位置参数1,2,3,4", required = true)
            @PathVariable(value = "position") String position) throws Exception {
        if (position.equals("1")) {
            return emergencyRoom(core);
        }else if(position.equals("2")){
            return hundredPeople(core);
        }else if(position.equals("3")){
            return emergency(core);
        }else if(position.equals("4")) {
            return referral(core);
        }else {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("参数：" + position + "，有误！");
            return envelop;
        }
    }

    /**
     * 本月门急诊人次
     * @return
     */
    private Envelop emergencyRoom(String core) throws Exception {
        Envelop envelop = new Envelop();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = String.format("%s-%s-01T00:00:00Z", year, monthStr);
        String end = dateFormat.format(calendar.getTime());
        String q = String.format("event_type:0 AND event_date:[%s TO %s]", start, end);
        Map<String, Integer> data1 = solr.getFacetQuery(core, q);
        Integer clinic = data1.get(q);
        envelop.setSuccessFlg(true);
        envelop.setObj(clinic);
        return envelop;
    }

    /**
     * 本月每百门急诊入院人数
     * @param core
     * @return
     * @throws Exception
     */
    private Envelop hundredPeople(String core) throws Exception {
        Envelop envelop = new Envelop();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //获取当月住院数据
        String start = String.format("%s-%s-01T00:00:00Z", year, monthStr);
        String end = dateFormat.format(calendar.getTime());
        String q = String.format("event_type:1 AND event_date:[%s TO %s]", start, end);
        Map<String, Integer> data1 = solr.getFacetQuery(core, q);
        Integer hospitalized = data1.get(q);
        //获取当月门诊数据
        q = String.format("event_type:0 AND event_date:[%s TO %s]", start, end);
        Map<String, Integer> data2 = solr.getFacetQuery(core, q);
        Integer clinic = data2.get(q);
        if (clinic == 0) {
            clinic = 1;
        }
        int count = hospitalized / clinic / 100;
        envelop.setSuccessFlg(true);
        envelop.setObj(count);
        return envelop;
    }

    /**
     * 本月急诊总人次数
     * @return
     */
    private Envelop emergency(String core) throws Exception {
        Envelop envelop = new Envelop();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = String.format("%s-%s-01T00:00:00Z", year, monthStr);
        String end = dateFormat.format(calendar.getTime());
        String q = String.format("event_type:0 AND EHR_001240:51 AND event_date:[%s TO %s]", start, end);
        Map<String, Integer> data1 = solr.getFacetQuery(core, q);
        Integer clinic = data1.get(q);
        envelop.setSuccessFlg(true);
        envelop.setObj(clinic);
        return envelop;
    }

    /**
     * 本月转诊人次
     *
     * @return
     */
    private Envelop referral(String core) throws Exception {
        Envelop envelop = new Envelop();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = String.format("%s-%s-01T00:00:00Z", year, monthStr);
        String end = dateFormat.format(calendar.getTime());
        String q = String.format("event_type:0 AND EHR_000083:T AND event_date:[%s TO %s]", start, end);
        Map<String, Integer> data1 = solr.getFacetQuery(core, q);
        Integer clinic = data1.get(q);
        envelop.setSuccessFlg(true);
        envelop.setObj(clinic);
        return envelop;
    }

    @ApiOperation("医院门急诊人次分布")
    @RequestMapping(value = "/statistics/monthDistribution", method = RequestMethod.POST)
    public Envelop monthDistribution(
            @ApiParam(name = "core", value = "集合", required = true)
            @RequestParam(value = "core") String core,
            @ApiParam(name = "year", value = "年份", required = true)
            @RequestParam(value = "year") int year) throws Exception {
        Envelop envelop = new Envelop();
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        if(nowYear > year) {
            calendar.set(Calendar.MONTH, 11);
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int month = calendar.get(Calendar.MONTH) + 1;
        List<Map<String, Integer>> dataList = new ArrayList<>(month);
        for(int i = 1; i <= month; i ++) {
            String monthStr;
            if(i < 10) {
                monthStr = "0" + i;
            }else {
                monthStr = "" + i;
            }
            String start = String.format("%s-%s-01T00:00:00Z", year, monthStr);
            String end;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            calendar.set(Calendar.MONTH, i - 1);
            int day;
            if(i == month && nowYear == year) {
                Calendar calendar1 = Calendar.getInstance();
                end = dateFormat.format(calendar1.getTime());
            }else {
                day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                end = String.format("%s-%s-%sT23:59:59Z", year, monthStr, day);
            }
            String q = String.format("event_type:0 AND event_date:[%s TO %s]", start, end);
            Map<String, Integer> data = solr.getFacetQuery(core, q);
            data.put(monthStr, data.get(q));
            data.remove(q);
            dataList.add(data);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(dataList);
        return envelop;
    }

    @ApiOperation("本月各类医院门急诊人次")
    @RequestMapping(value = "/statistics/rescue", method = RequestMethod.POST)
    public Envelop variousTypes() throws Exception{
        Envelop envelop = new Envelop();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr;
        if(month < 10) {
            monthStr = "0" + month;
        }else {
            monthStr = "" + month;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = String.format("%s-%s-01T00:00:00Z", year, monthStr);
        String end = dateFormat.format(calendar.getTime());
        String fq = String.format("event_type:0 AND event_date:[%s TO %s]", start, end);
        FacetField facetField = solr.getFacetField("HealthProfile", "org_code", fq, 0, 0, 1000000, false);
        List<FacetField.Count> list = facetField.getValues();
        Map<String, Long> dataMap = new HashMap<>(list.size());
        for(FacetField.Count count : list) {
            dataMap.put(count.getName(), count.getCount());
        }
        Map<String, Long> resultMap = new HashMap<>();
        for(String code : dataMap.keySet()) {
            String level = orgService.getLevel(code);
            if(!StringUtils.isEmpty(level)) {
                if(resultMap.containsKey(level)) {
                    long count = resultMap.get(level) + dataMap.get(code);
                    resultMap.put(level, count);
                }else {
                    resultMap.put(level, dataMap.get(code));
                }
            }else {
                if(resultMap.containsKey("9")) {
                    long count = resultMap.get("9") + dataMap.get(code);
                    resultMap.put("9", count);
                }else {
                    resultMap.put("9", dataMap.get(code));
                }
            }
        }

        Map<String, Object> resap = new HashMap<>();
        // dictId=99 (医院等级)
        Map<String,Object> dictEntrysMap = orgService.getDictEntries(99);
        for(String code : resultMap.keySet()) {
            if(null != dictEntrysMap.get(code)){
                resap.put(dictEntrysMap.get(code).toString(),resultMap.get(code));
            }else{
                resap.put(code,resultMap.get(code));
            }
        }

        envelop.setSuccessFlg(true);
        envelop.setObj(resap);
        return envelop;
    }

    // 获取当月第一天日期（精确到00:00:00）
    private String getCurrMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sf.format(calendar.getTime());
        return date + "T00:00:00Z";
    }

    // 获取当月最后一天（精确到23:59:59）
    private String getCurrMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(calendar.DATE));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sf.format(calendar.getTime());
        return date + "T23:59:59Z";
    }

}
