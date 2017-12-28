package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "门急诊服务统计接口", tags = {"临时报表接口--门急诊服务统计接口"})
public class SolrStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private SolrQuery solrQuery;
    @Autowired
    private SolrUtil solr;

    @ApiOperation("本月科室门诊人次")
    @RequestMapping(value = "/outpatientService/statisticDeptOutpatientSum", method = RequestMethod.GET)
    public Envelop statisticDeptOutpatientSum() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String startDay = "2017-01-01T00:00:00Z";
//            String startDay = getCurrMonthFirstDay();
            String endDay = getCurrMonthLastDay();
            String fq = "event_type:0 AND event_date:[" + startDay + " TO " + endDay + "]";
            String statsField = "event_no";
            String groupField = "EHR_000081"; // 科室
            List<FieldStatsInfo> list = solr.getStats("HealthProfile", null, fq, statsField, groupField);

            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("统计【本月科室门诊人次】发生异常");
        }
        return envelop;
    }

    @ApiOperation("每月就诊或住院人次")
    @RequestMapping(value = "/statistics/mothVisit", method = RequestMethod.POST)
    public Envelop statistics(
            @ApiParam(name = "core", value = "集合", required = true)
            @RequestParam(value = "core") String core,
            @ApiParam(name = "filter", value = "查询条件", required = true)
            @RequestParam(value = "filter") String filter) throws Exception {
        Envelop envelop = new Envelop();
        String queryParams = "";
        List<QueryCondition> ql = parseCondition(filter);
        queryParams = addParams(queryParams, "q", solrQuery.conditionToString(ql));
        //Solr查询
        Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
        Map<String, Integer> dataMap = solr.getFacetQuery(core, obj.get("q"));
        if (dataMap != null) {
            envelop.setObj(dataMap.get(obj.get("q")));
        } else {
            envelop.setObj(0);
        }
        envelop.setSuccessFlg(true);
        return envelop;
        /**
         SolrDocumentList solrList = solr.query(core, obj.get("q"), null, null, 0, 1000000);
         List<String> list = new ArrayList<String>();
         long count = 0;
         if(solrList!=null && solrList.getNumFound()>0) {
         count = solrList.getNumFound();
         for (SolrDocument doc : solrList){
         String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
         list.add(rowkey);
         }
         }
         envelop.setSuccessFlg(true);
         envelop.setObj(count);
         return envelop;
         */
    }


    /**
     * 新增参数
     *
     * @return
     */
    private String addParams(String oldParams, String key, String value) {
        String newParam = "";
        if (value.startsWith("[") && value.endsWith("]")) {
            newParam = "\"" + key + "\":" + value;
        } else {
            newParam = "\"" + key + "\":\"" + value.replace("\"", "\\\"") + "\"";
        }
        if (oldParams != null && oldParams.length() > 3 && oldParams.startsWith("{") && oldParams.endsWith("}")) {
            return oldParams.substring(0, oldParams.length() - 1) + "," + newParam + "}";
        } else {
            return "{" + newParam + "}";
        }
    }

    /**
     * 查询条件转换
     *
     * @param queryCondition
     * @return
     * @throws Exception
     */
    private List<QueryCondition> parseCondition(String queryCondition) throws IOException {
        List<QueryCondition> ql = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, Object>> list = objectMapper.readValue(queryCondition, javaType);
        if (list != null && list.size() > 0) {
            for (Map<String, Object> item : list) {
                String andOr = String.valueOf(item.get("andOr")).trim();
                String field = String.valueOf(item.get("field")).trim();
                String cond = String.valueOf(item.get("condition")).trim();
                String value = String.valueOf(item.get("value"));
                if (value.indexOf(",") > 0) {
                    ql.add(new QueryCondition(andOr, cond, field, value.split(",")));
                } else {
                    ql.add(new QueryCondition(andOr, cond, field, value));
                }
            }
        }
        return ql;
    }

    public String getCurrMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sf.format(calendar.getTime());
        return date + "T00:00:00Z";
    }

    public String getCurrMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(calendar.DATE));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sf.format(calendar.getTime());
        return date + "T23:59:59Z";
    }

}
