package com.yihu.quota.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.echarts.Option;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.echarts.ChartDataModel;
import com.yihu.ehr.model.report.MQcDevice;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.model.jpa.RsResourceQuota;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.quota.BaseStatistsService;
import com.yihu.quota.service.quota.DeviceService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.service.resource.ResourceQuotaService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.util.ReportOption;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "指标报表统计 -指标报表统计控制入口")
public class QuotaReportController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TjDimensionMainService tjDimensionMainService;
    @Autowired
    private TjDimensionSlaveService tjDimensionSlaveService;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ResourceQuotaService resourceQuotaService;
    @Autowired
    private OrgHealthCategoryStatisticsService orgHealthCategoryStatisticsService;
    @Autowired
    private BaseStatistsService baseStatistsService;
    @Autowired
    private DeviceService deviceService;

    public static String orgHealthCategoryCode = "orgHealthCategoryCode";

    @RequestMapping(value = ServiceApi.TJ.GetYearDropdownList, method = RequestMethod.GET)
    @ApiOperation(value = "获取二维表查询年份下拉数据")
    public Envelop getDropdownList(
            @ApiParam(name = "type", value = "类型 1增量型报表 2 全量型报表")
            @RequestParam(value = "type" ,required =  true ) int type,
            @ApiParam(name = "index", value = "索引")
            @RequestParam(value = "index" ,required =  true ) String index) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, List<String>> map = null;
        String sql = "select count(1) from "+ index +" group by date_histogram(field='quotaDate','interval'='year') order by quotaDate desc";
        map = baseStatistsService.getDataInfo(sql, "date_histogram(field=quotaDate,interval=year)");
        if (null != map && map.size() > 0) {
            Collections.sort(map.get("xData"), Collections.reverseOrder());
            if(type == 1){
                envelop.setObj(map.get("xData"));
            }else {
                Map<String,String> resultMap = new HashMap<>();
                for(String key : map.get("xData")){
                    String filter = "quotaDate >= '" + key + "-01-01'" + " and quotaDate <= '" + key +"-12-31'";
                    sql = "select count(1) from "+ index +" where " + filter + " group by date_histogram(field='quotaDate','interval'='month') order by quotaDate desc";
                    System.out.println(sql);
                    Map<String, List<String>>  monthMap = baseStatistsService.getDataInfo(sql, "date_histogram(field=quotaDate,interval=month)");
                    if(monthMap != null && monthMap.size() > 0){
                        resultMap.put(key,monthMap.get("xData").get(0).toString());
                    }
                }
                envelop.setObj(resultMap);
            }
        }
        envelop.setSuccessFlg(true);
        return  envelop;
    }

    /**
     * 获取指标统计结果
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标统计结果")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReport, method = RequestMethod.GET)
    public Envelop getQuotaReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id", required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            //To DO
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @ApiOperation(value = "获取统计报表一个视图下多个指标组合  二维表数据")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReportTwoDimensionalTable, method = RequestMethod.GET)
    public List<Map<String, Object>> getQuotaReportTwoDimensionalTable(
            @ApiParam(name = "quotaCodeStr", value = "指标Code,多个用,拼接", required = true)
            @RequestParam(value = "quotaCodeStr", required = true) String quotaCodeStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top
    ) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> quotaViewResult = new HashMap<>();
        List<String> quotaCodes = Arrays.asList(quotaCodeStr.split(","));
        String maxQuotaCode = "";
        int num = 0;
        try {
            for (String code : quotaCodes) {
                List<Map<String, Object>> quotaResult = baseStatistsService.getSimpleQuotaReport(code, filter, dimension,false, top);
                if (quotaResult.size() >= num) {
                    num = quotaResult.size();
                    maxQuotaCode = code;
                }
                quotaViewResult.put(code, quotaResult);
            }
            Map<String, List<Map<String, Object>>> otherQuotaViewResult = new HashMap<>();
            for (String key : quotaViewResult.keySet()) {
                if (key != maxQuotaCode) {
                    otherQuotaViewResult.put(key, quotaViewResult.get(key));
                }
            }
            //以查询结果数据最多的指标为主，其他指标对应维度没有数据的补充0
            for (Map<String, Object> vMap : quotaViewResult.get(maxQuotaCode)) {
                vMap.put(maxQuotaCode, vMap.get("result")==null ? 0 : ("--".equals(vMap.get("result")) ? vMap.get("result") : nf.format(Double.valueOf(vMap.get("result").toString()))));
                for (String viewQuotaCode : otherQuotaViewResult.keySet()) {
                    if(otherQuotaViewResult != null && otherQuotaViewResult.get(viewQuotaCode) != null && otherQuotaViewResult.get(viewQuotaCode).size()>0 ){
                        for (Map<String, Object> quotaResultMap : otherQuotaViewResult.get(viewQuotaCode)) {
                            if (quotaResultMap.get(dimension) != null && vMap.get(dimension) != null ) {
                                if (vMap.get(dimension).toString().trim().equals(quotaResultMap.get(dimension).toString().trim())) {
                                    vMap.put(viewQuotaCode, quotaResultMap.get("result")==null ? 0 : ("--".equals(quotaResultMap.get("result")) ? quotaResultMap.get("result") : nf.format(Double.valueOf(quotaResultMap.get("result").toString()))));
                                    break;
                                } else {
                                    vMap.put(viewQuotaCode, 0);
                                }
                            } else {
                                vMap.put(viewQuotaCode, 0);
                            }
                        }
                    }else {
                        vMap.put(viewQuotaCode, 0);
                    }
                }
            }
            List<Map<String, Object>> resultList = quotaViewResult.get(maxQuotaCode);

            if(dimension.equals(orgHealthCategoryCode)){//如果是特殊机构类型树状机构需要转成树状结构
                List<Map<String, Object>> orgHealthCategoryList = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
//                dataList = baseStatistsService.setResultAllDimenMap(maxQuotaCode, orgHealthCategoryList, resultList,null);
                //采用新的
                dataList = baseStatistsService.allCategoryResultMap(quotaCodes, orgHealthCategoryList, resultList);

            }else {
                dataList = resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Map<String, Object> map : dataList){
            if(map.get("firstColumn") == null || map.get("firstColumn").toString().equals("null") || StringUtils.isEmpty(map.get("firstColumn").toString())){
                if(map.get("level")!= null && StringUtils.isNotEmpty(map.get("level").toString())){
                    String level = map.get("level").toString();
                    if (level.equals("1")){
                        level = "一级";
                    }else if (level.equals("2")){
                        level = "二级";
                    }else if (level.equals("3")){
                        level = "三级";
                    }else {
                        level = "未指定";
                    }
                    map.put("firstColumn",level);
                }
                if(map.get("economic")!= null && StringUtils.isNotEmpty(map.get("economic").toString())){
                    String economic = map.get("economic").toString();
                    if (economic.equals("1021")){
                        economic = "公立";
                    }else if (economic.equals("1022")){
                        economic = "非公立";
                    }else {
                        economic = "其他";
                    }
                    map.put("firstColumn",economic);
                }
            }
        }
        //计算合计
        if(dataList != null && dataList.size() > 0){
            Map<String, Object> sumMap = new HashMap<>();
            sumMap.put("firstColumn","合计");
            for (String code : quotaCodes) {
                double sum = 0;
                sum = calculateSum(sum,code,dataList);
                sumMap.put(code, nf.format(sum));
            }
            dataList.add(0,sumMap);
        }
        return dataList;
    }

    /**
     * 统计每列合计
     * @param sum
     * @param code
     * @param dataList
     * @return
     */
    private double calculateSum( double sum,String code,List<Map<String, Object>> dataList){
        for(Map<String, Object> map : dataList){
            if(map.get(code) != null){
                sum += Double.valueOf("--".equals(map.get(code)) ? "0" : map.get(code).toString());
            }
        }
        return sum;
    }

    @ApiOperation(value = "获取指标统计结果echart图表，支持多条组合")
    @RequestMapping(value = ServiceApi.TJ.GetMoreQuotaGraphicReportPreviews, method = RequestMethod.GET)
    public MChartInfoModel getQuotaGraphicReports(
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
            @ApiParam(name = "charstr", value = "多图表类型用,拼接,混合类型只支持柱状和线性", defaultValue = "1")
            @RequestParam(value = "charstr", required = true) String charstr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "视图名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top,
            @ApiParam(name = "MRsResource json串")
            @RequestParam(value = "mRsResource", required = false) String mRsResource
            ) {
        String xName = "";
        String yName = "";
        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
        List<String> charTypes = Arrays.asList(charstr.split(","));
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        String dimensionName = dimension + "Name";
        if ("quotaName".equals(dimension)) {
            dimensionName = "quotaName";
        }
        try {
            Option option = null;
            List<List<Object>> optionData = new ArrayList<>();
            List<String> lineNames = new ArrayList<>();
            Map<String, Map<String, Object>> lineData = new LinkedHashMap<>();
            Map<String, String> xAxisMap = new LinkedHashMap<>();
            Integer i = 0;
            List<Map<String, Object>> listMap = new ArrayList<>();

            for (String quotaId : quotaIds) {
                Map<String, Object> dataMap = new LinkedHashMap<>();
                TjQuota tjQuota = quotaService.findOne(Integer.valueOf(quotaId));
                if (tjQuota != null) {
                    List<Map<String, Object>> resultListMap = baseStatistsService.getSimpleQuotaReport(tjQuota.getCode(), filter, dimension,true, top);
                    if (resultListMap != null && resultListMap.size() > 0) {
                        for (Map<String, Object> map : resultListMap) {
                            if (map != null && map.size() > 0) {
                                if (map.containsKey("quotaName")) {
                                    map.put("quotaName",tjQuota.getName());
                                }
                                listMap.add(map);
                                //第一种 ES库中有定义的维度 如org,slaveKey1
                                //第二种 ES库中未定义的维度 如level，economic
                                if (map.containsKey(dimensionName)) {
                                    if(map.get(dimensionName) != null){
                                        dataMap.put(map.get(dimensionName).toString(), map.get("result"));
                                        xAxisMap.put(map.get(dimensionName).toString(), map.get(dimension).toString());
                                    }
                                } else {
                                    if(map.get(dimension) != null){
                                        dataMap.put(map.get(dimension).toString(), map.get("result"));
                                        xAxisMap.put(map.get(dimension).toString(), map.get(dimension).toString());
                                    }
                                }
                            }

                        }
                    }
                    lineData.put("" + i, dataMap);
                    lineNames.add(tjQuota.getName());
                    i++;
                }
            }
//            lineNames.add(title);
//            lineData.put("", dataMap);
            Map<String, Object> quotaMap = new LinkedHashMap<>();
            ReportOption reportOption = new ReportOption();
            int size = 0;
            String quota = "0";
            if (lineData != null && lineData.size() > 0) {
                for (String key : lineData.keySet()) {
                    int tempSize = lineData.get(key).size();
                    if (tempSize > size) {
                        size = tempSize;
                        quota = key;
                        quotaMap = lineData.get(key);
                    }
                }
                for (String key : lineData.keySet()) {
                    List<Object> dataList = new ArrayList<>();
                    Map<String, Object> valMap = lineData.get(key);
                    if (key != quota) {
                        for (String name : quotaMap.keySet()) {
                            if (valMap.containsKey(name)) {
                                dataList.add(valMap.get(name));
                            } else {
                                dataList.add(0);
                            }
                        }
                    } else {
                        for (String name : valMap.keySet()) {
                            dataList.add(valMap.get(name));
                        }
                    }
                    optionData.add(dataList);
                }
            }
            Object[] xData = (Object[]) quotaMap.keySet().toArray(new Object[quotaMap.size()]);
            for (String typeStr : charTypes) {
                if (typeStr.equals("common")) {
                    typeStr = "1";
                } else if (typeStr.equals("twoDimensional")) { // 这个需要与前端商榷  访问2次（二维表类型）
                    return null;
                }
                int type = Integer.valueOf(typeStr);
                String dataMeasurement = "";
                if (StringUtils.isNotEmpty(mRsResource)) {
                    MRsResources mRsResources = objectMapper.readValue(mRsResource, MRsResources.class);
                    dataMeasurement = mRsResources.getDataMeasurement();
                    String dataPosition = mRsResources.getDataPosition();
                    if (StringUtils.isNotEmpty(dataPosition) && "x".equalsIgnoreCase(dataPosition)) {
                        xName = "单位：" + mRsResources.getDataUnit();
                    } else if (StringUtils.isNotEmpty(dataPosition) && "y".equalsIgnoreCase(dataPosition)) {
                        yName = "单位：" + mRsResources.getDataUnit();
                    }
                }
                if (type == ReportOption.bar) {
                    option = reportOption.getLineEchartOptionMoreChart(title, xName, yName, xData, discountByMeasurement(optionData, dataMeasurement), lineNames, charTypes);
                } else if (type == ReportOption.line) {
                    option = reportOption.getLineEchartOptionMoreChart(title, xName, yName, xData, discountByMeasurement(optionData, dataMeasurement), lineNames, charTypes);
                } else if (type == ReportOption.pie) {
                    List<Map<String, Object>> datalist = new ArrayList<>();
                    for (Map<String, Object> resultMap : listMap) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("NAME", null == resultMap.get(dimensionName) ? resultMap.get(dimension) : resultMap.get(dimensionName));
                        map.put("TOTAL", resultMap.get("result"));
                        if(resultMap.get(dimensionName) != null){
                            map.put("NAME",resultMap.get(dimensionName));
                        }else {
                            //非 指标中配置的维度 关联出来的字段
                            if(dimensionName.equals("levelName")){
                                if(resultMap.get(dimension).equals("1")){
                                    map.put("NAME","一级医院");
                                }else  if(resultMap.get(dimension).equals("2")){
                                    map.put("NAME","二级医院");
                                }else  if(resultMap.get(dimension).equals("3")){
                                    map.put("NAME","三级医院");
                                }
                            }
                        }
                        map.put("TOTAL",resultMap.get("result"));
                        datalist.add(map);
                    }
                    option = reportOption.getPieEchartOption(title, "", "", datalist, lineNames.get(0), null);
                }
            }
            chartInfoModel.setOption(option.toString());
            chartInfoModel.setTitle(title);
            chartInfoModel.setxAxisMap(xAxisMap);
            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            return null;
        }
    }


//    @ApiOperation(value = "获取指标统计结果echart图表，支持多条组合")
//    @RequestMapping(value = ServiceApi.TJ.GetMoreQuotaGraphicReportPreviews, method = RequestMethod.GET)
//    public MChartInfoModel getQuotaGraphicReports(
//            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
//            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
//            @ApiParam(name = "charstr", value = "多图表类型用,拼接,混合类型只支持柱状和线性", defaultValue = "1")
//            @RequestParam(value = "charstr" , required = true) String charstr,
//            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
//            @RequestParam(value = "filter", required = false) String filter,
//            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
//            @RequestParam(value = "dimension", required = false) String dimension,
//            @ApiParam(name = "title", value = "视图名称", defaultValue = "")
//            @RequestParam(value = "title", required = false) String title
//    ) {
//        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
//        List<String> charTypes = Arrays.asList(charstr.split(","));
//        MChartInfoModel chartInfoModel = new MChartInfoModel();
//        try {
//            Option option = null;
//            List<List<Object>> optionData = new ArrayList<>();
//            List<String> lineNames = new ArrayList<>();
//            Map<String,Map<String, Object>> lineData = new LinkedHashMap<>();
//            Map<String, String> xAxisMap = new LinkedHashMap<>();
//            for(String quotaId:quotaIds){
//                Map<String, Object> dataMap = new LinkedHashMap<>();
//                TjQuota tjQuota = quotaService.findOne(Integer.valueOf(quotaId));
//                if(tjQuota != null){
//                    String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(),dimension);
//                    Map<String,String> dimensionDicMap = new HashMap<>();
//                    if(StringUtils.isNotEmpty(dictSql)){
//                        BasesicUtil baseUtil = new BasesicUtil();
//                        if(dimension.contains("slaveKey")){
//                            //查询字典数据
//                            List<DictModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(DictModel.class));
//                            for (DictModel dictModel : dictDatas) {
//                                String name = baseUtil.getFieldValueByName("name", dictModel);
//                                String val = baseUtil.getFieldValueByName("code", dictModel).toLowerCase();
//                                dimensionDicMap.put(val,name);
//                            }
//                        } else{
//                            List<SaveModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(SaveModel.class));
//                            if(dictDatas != null ) {
//                                for (SaveModel saveModel : dictDatas) {
//                                    String name = baseUtil.getFieldValueByName(dimension + "Name", saveModel);
//                                    String val = baseUtil.getFieldValueByName(dimension,saveModel).toLowerCase();
//                                    dimensionDicMap.put(val,name);
//                                }
//                            }
//                        }
//                    }
//
//                    Map<String, Object> groupDataMap = new HashMap<>();
//                    if(tjQuota.getResultGetType().trim().equals("1")){
//                        //使用分组计算 返回结果实例： groupDataMap -> "4205000000-儿-1": 200 =>group by 三个字段
//                        Map<String, Integer> resultDataMap =  quotaService.searcherSumByGroupBySql(tjQuota, dimension, filter,"result","","");
//                        for(String key: resultDataMap.keySet()){
//                            groupDataMap.put(key,resultDataMap.get(key));
//                        }
//                    }else{//二次统计指标获取 结果接口
//                        List<Map<String, Object>> listMap = baseStatistsService.getSimpleQuotaReport(tjQuota.getCode(), filter, dimension);
//                        if(listMap != null && listMap.size() > 0){
//                            for(Map<String, Object> map : listMap){
//                                String keyName = "";
//                                String val = "";
//                                for (String key : map.keySet()) {
//                                    if ("result".equals(key)) {
//                                        val = map.get(key).toString();
//                                    } else {
//                                        keyName = map.get(key).toString();
//                                    }
//                                }
//                                groupDataMap.put(keyName, val);
//                            }
//                        }
//
//                    }
//
//                    for(String key : groupDataMap.keySet()){
//                        key = key.toLowerCase();
//                        dataMap.put(dimensionDicMap.containsKey(key)?dimensionDicMap.get(key):key,groupDataMap.get(key));
//                        xAxisMap.put(dimensionDicMap.containsKey(key)?dimensionDicMap.get(key): key,key);
//                    }
//                    lineNames.add(tjQuota.getName());
//                    lineData.put(tjQuota.getCode(), dataMap);
//                }
//            }
//            Map<String, Object> quotaMap = new LinkedHashMap<>();
//            ReportOption reportOption = new ReportOption();
//
//            int size = 0;
//            String quota = "";
//            if(lineData != null && lineData.size() > 0){
//                for(String key : lineData.keySet()){
//                    int tempSize = lineData.get(key).size();
//                    if (tempSize > size){
//                        size = tempSize;
//                        quota = key;
//                        quotaMap = lineData.get(key);
//                    }
//                }
//                for(String key : lineData.keySet()){
//                    List<Object> dataList = new ArrayList<>();
//                    Map<String,Object> valMap = lineData.get(key);
//                    if(key != quota){
//                        for(String name :quotaMap .keySet()){
//                            if(valMap.containsKey(name)){
//                                dataList.add(valMap.get(name));
//                            }else {
//                                dataList.add(0);
//                            }
//                        }
//                    }else{
//                        for(String name :valMap .keySet()){
//                            dataList.add(valMap.get(name));
//                        }
//                    }
//                    optionData.add(dataList);
//                }
//            }
//            Object[] xData = (Object[])quotaMap.keySet().toArray(new Object[quotaMap.size()]);
//            option = reportOption.getLineEchartOptionMoreChart(title, "", "", xData, optionData, lineNames,charTypes);
//            chartInfoModel.setOption(option.toString());
//            chartInfoModel.setTitle(title);
//            chartInfoModel.setxAxisMap(xAxisMap);
//            return chartInfoModel;
//        } catch (Exception e) {
//            error(e);
//            invalidUserException(e, -1, "查询失败:" + e.getMessage());
//            return null;
//        }
//    }


    @ApiOperation(value = "指标统计分组查询")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaGroupBy, method = RequestMethod.GET)
    public Envelop getQuotaGroupBy(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id", required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            TjQuota tjQuota = quotaService.findOne(id);
            Map<String, Integer>  resultMap = quotaService.searcherSumByGroupBySql(tjQuota,dimension, filters,"result","","");
            envelop.setSuccessFlg(true);
            envelop.setObj(resultMap);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }


    private String getQuotaDimensionDictSql(String quotaCode, String dimension) {
        String dictSql = "";
        //查询维度
        List<TjQuotaDimensionMain> dimensionMains = tjDimensionMainService.findTjQuotaDimensionMainByQuotaCode(quotaCode);
        if (dimensionMains != null && dimensionMains.size() > 0) {
            for (TjQuotaDimensionMain main : dimensionMains) {
                if (main.getMainCode().equals(dimension)) {
                    dictSql = main.getDictSql();
                }
            }
        }
        if (StringUtils.isEmpty(dictSql)) {
            List<TjQuotaDimensionSlave> dimensionSlaves = tjDimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(quotaCode);
            if (dimensionSlaves != null && dimensionSlaves.size() > 0) {
                int slave = Integer.valueOf(dimension.substring(dimension.length() - 1, dimension.length()));
                if (dimensionSlaves.size() >= slave) {
                    dictSql = dimensionSlaves.get(slave - 1).getDictSql();
                }
            }
        }
        return dictSql;
    }

    @ApiOperation(value = "获取指标统计结果echart radar雷达图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaRadarGraphicReportPreviews, method = RequestMethod.GET)
    public MChartInfoModel getQuotaRadarGraphicReports(
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr", required = true) String quotaIdStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title) {
        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        List<Integer> arrayNum = new ArrayList<>();
        Integer polorCount = 50;
        try {
            Option option = null;
            List<String> radarNames = new ArrayList<>();
            Map<String, Map<String, Object>> radarData = new HashMap<>();
            List<Map<String, Object>> listData = new ArrayList<>();

            Map<String, String> xAxisMap = new HashMap<>();
            for (String quotaId : quotaIds) {
                Map<String, Object> dataMap = new HashMap<>();
                TjQuota tjQuota = quotaService.findOne(Integer.valueOf(quotaId));
                if (null != tjQuota) {
                    String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimension);
                    Map<String, String> dimensionDicMap = new HashMap<>();
                    dimensionDicMap = setDimensionMap(dictSql, dimension, dimensionDicMap);
                    //使用分组计算 返回结果实例： groupDataMap -> "4205000000-儿-1": 200 =>group by 三个字段
                    Map<String, Integer> groupDataMap = quotaService.searcherSumByGroupBySql(tjQuota, dimension, filter, "result", "", "");
                    for (String key : groupDataMap.keySet()) {
                        key = key.toLowerCase();
                        dataMap.put(dimensionDicMap.containsKey(key) ? dimensionDicMap.get(key) : key, groupDataMap.get(key));
                        xAxisMap.put(dimensionDicMap.containsKey(key) ? dimensionDicMap.get(key) : key, key);
                    }
                    radarNames.add(tjQuota.getName());
                    radarData.put(tjQuota.getCode(), dataMap);
                }
                Integer num = getNum(dataMap);
                arrayNum.add(num);
                Map<String, Object> map = new HashMap();
                map.put(tjQuota.getName(), num);
                listData.add(map);
            }
            ReportOption reportOption = new ReportOption();
            Integer[] array = arrayNum.toArray(new Integer[arrayNum.size()]);
            Arrays.sort(array); // 进行升序排序
            polorCount += array[arrayNum.size() - 1];   // 雷达图极坐标
            option = reportOption.getRadarEchartOption(title, listData, polorCount);
            chartInfoModel.setOption(option.toString());
            chartInfoModel.setTitle(title);
            chartInfoModel.setxAxisMap(xAxisMap);
            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            return null;
        }
    }

    @ApiOperation(value = "获取指标统计结果echart NestedPie图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaNestedPieReportPreviews, method = RequestMethod.GET)
    public MChartInfoModel getQuotaNestedPieGraphicReports(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr", required = true) String quotaIdStr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title) {
        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
        Option option = null;
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        Map<String, String> xAxisMap = new HashMap<>();
        try {
            Integer quotaCount = resourceQuotaService.getQuotaCount(resourceId);
            // 获取最顶层的资源
            List<Integer> quotaId = new ArrayList<>();
            ChartDataModel chartDataModel = getChartDataModel(quotaId, quotaCount, resourceId, dimension, filter, xAxisMap);
            ReportOption reportOption = new ReportOption();

            option = reportOption.getNestedPieEchartOption(title, chartDataModel);
            chartInfoModel.setOption(option.toString());
            chartInfoModel.setTitle(title);
            chartInfoModel.setxAxisMap(xAxisMap);
            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            return null;
        }
    }

    private ChartDataModel getChartDataModel(List<Integer> quotaId, Integer count, String resourceId, String dimension, String filter, Map<String, String> xAxisMap) throws Exception {
        ChartDataModel chartDataModel = new ChartDataModel();
        List<RsResourceQuota> resultList = resourceQuotaService.getChildrenByPidList(quotaId, resourceId);
        quotaId.clear();
        for (RsResourceQuota rq : resultList) {
            quotaId.add(Integer.valueOf(rq.getQuotaId()));
        }
        count = count - resultList.size();
        if (null != resultList && resultList.size() > 0) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (RsResourceQuota rq : resultList) {
                RsResourceQuota parent = rq;
                TjQuota tjQuota = quotaService.findOne(Integer.valueOf(rq.getQuotaId()));
                Map<String, Object> dataMap = new HashMap<>();
                if (null != tjQuota) {
                    String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimension);
                    Map<String, String> dimensionDicMap = new HashMap<>();
                    dimensionDicMap = setDimensionMap(dictSql, dimension, dimensionDicMap);
                    //使用分组计算 返回结果实例： groupDataMap -> "4205000000-儿-1": 200 =>group by 三个字段
                    Map<String, Integer> groupDataMap = quotaService.searcherSumByGroupBySql(tjQuota, dimension, filter, "result", "", "");
                    for (String key : groupDataMap.keySet()) {
                        key = key.toLowerCase();
                        dataMap.put(dimensionDicMap.containsKey(key) ? dimensionDicMap.get(key) : key, groupDataMap.get(key));
                        xAxisMap.put(dimensionDicMap.containsKey(key) ? dimensionDicMap.get(key) : key, key);
                    }
                }
                Integer num = getNum(dataMap);
                List<Map<String, Object>> mapList = new ArrayList<>();
                Map<String, Object> map = new HashMap();
                map.put("NAME", tjQuota.getName());
                map.put("TOTAL", num);
                mapList.add(map);
                rq.setMapList(mapList);
                list.addAll(rq.getMapList());
            }
            chartDataModel.setList(list);
        }
        if (count > 0) {
            ChartDataModel chartDataModel2 = getChartDataModel(quotaId, count, resourceId, dimension, filter, xAxisMap);
            if (null != chartDataModel2) {
                chartDataModel.setChildren(chartDataModel2);
            }
        }
        return chartDataModel;
    }

    private Map<String, String> setDimensionMap(String dictSql, String dimension, Map<String, String> dimensionDicMap) {
        if (StringUtils.isNotEmpty(dictSql)) {
            BasesicUtil baseUtil = new BasesicUtil();
            if (dimension.contains("slaveKey")) {
                //查询字典数据
                List<DictModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(DictModel.class));
                for (DictModel dictModel : dictDatas) {
                    String name = baseUtil.getFieldValueByName("name", dictModel);
                    String val = baseUtil.getFieldValueByName("code", dictModel).toLowerCase();
                    dimensionDicMap.put(val, name);
                }
            } else {
                List<SaveModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(SaveModel.class));
                if (dictDatas != null) {
                    for (SaveModel saveModel : dictDatas) {
                        String name = baseUtil.getFieldValueByName(dimension + "Name", saveModel);
                        String val = baseUtil.getFieldValueByName(dimension, saveModel).toLowerCase();
                        dimensionDicMap.put(val, name);
                    }
                }
            }
        }
        return dimensionDicMap;
    }

    private Integer getNum(Map<String, Object> dataMap) {
        Integer num = 0;
        for (String key : dataMap.keySet()) {
            Integer result = null != dataMap.get(key) ? Integer.parseInt(dataMap.get(key).toString()) : 0;
            num += result;
        }
        return num;
    }
    @RequestMapping(value = "/tj/getCostAndNumOfOutPatient", method = RequestMethod.GET)
    @ApiOperation(value = "门急诊费用、人次")
    public Map<String, String> getCostOfOutPatient() {
        Map<String, String> map = new HashMap<>();
        String costOfOutPatient = baseStatistsService.getCostOfOutPatient();
        String numOfOutPatient = baseStatistsService.getNumOfOutPatient();
        map.put("costOfOutPatient", costOfOutPatient);
        map.put("numOfOutPatient", numOfOutPatient);
        return map;
    }

    @RequestMapping(value = "/tj/getCostAndNumOfInPatient", method = RequestMethod.GET)
    @ApiOperation(value = "入院费用、人次")
    public Map<String, String> getNumOfOutPatient() {
        Map<String, String> map = new HashMap<>();
        String costOfInPatient = baseStatistsService.getCostOfInPatient();
        String numOfInPatient = baseStatistsService.getNumOfInPatient();
        map.put("costOfInPatient", costOfInPatient);
        map.put("numOfInPatient", numOfInPatient);
        return map;
    }

    @RequestMapping(value = "/tj/getMedicalMonitorInfo", method = RequestMethod.GET)
    @ApiOperation(value = "医改监测信息")
    public Map<String, String> getMedicalMonitorInfo() {
        Map<String, String> map = new HashMap<>();
        String costOfMedicalMonitor = baseStatistsService.getCostOfMedicalMonitor();
        map.put("costOfMedicalMonitor", costOfMedicalMonitor);
        return map;
    }

    @ApiOperation(value = "首页费用组成")
    @RequestMapping(value = ServiceApi.TJ.GetCostComposeReports, method = RequestMethod.GET)
    public MChartInfoModel getCostComposeReports(
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
            @ApiParam(name = "charType", value = "图表类型用", defaultValue = "1")
            @RequestParam(value = "charType", required = true) String charType,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "视图名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top
    ) {
        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        String dimensionName = dimension + "Name";
        if ("quotaName".equals(dimension)) {
            dimensionName = "quotaName";
        }
        try {
            Option option = null;
            List<String> lineNames = new ArrayList<>();
            Map<String, Map<String, Object>> lineData = new LinkedHashMap<>();
            Map<String, String> xAxisMap = new LinkedHashMap<>();
            Integer i = 0;
            List<Map<String, Object>> listMap = new ArrayList<>();

            for (String quotaId : quotaIds) {
                Map<String, Object> dataMap = new LinkedHashMap<>();
                TjQuota tjQuota = quotaService.findOne(Integer.valueOf(quotaId));
                if (tjQuota != null) {
                    List<Map<String, Object>> resultListMap = baseStatistsService.getSimpleQuotaReport(tjQuota.getCode(), filter, dimension,true, top);
                    if (resultListMap != null && resultListMap.size() > 0) {
                        for (Map<String, Object> map : resultListMap) {
                            if (map != null && map.size() > 0) {
                                if (map.containsKey("quotaName")) {
                                    map.put("quotaName",tjQuota.getName());
                                }
                                //第一种 ES库中有定义的维度 如org,slaveKey1
                                //第二种 ES库中未定义的维度 如level，economic
                                if (map.containsKey(dimensionName)) {
                                    if(map.get(dimensionName) != null){
                                        dataMap.put(map.get(dimensionName).toString(), map.get("result"));
                                        xAxisMap.put(map.get(dimensionName).toString(), map.get(dimension).toString());
                                    }
                                } else {
                                    if(map.get(dimension) != null){
                                        dataMap.put(map.get(dimension).toString(), map.get("result"));
                                        xAxisMap.put(map.get(dimension).toString(), map.get(dimension).toString());
                                    }
                                }
                            }

                        }
                    }
                }
                lineData.put("" + i, dataMap);
                lineNames.add(tjQuota.getName());
                i++;
            }
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            Set<String> hashSet = new HashSet<>(lineNames);
            Map<String, Object> newMap = new HashMap<>();
            for (Map<String, Object> data : lineData.values()) {
                for (Map.Entry<String, Object> lastMap : data.entrySet()) {
                    if (newMap.containsKey(lastMap.getKey())) {
                        double v = Double.parseDouble(newMap.get(lastMap.getKey()) + "") + Double.parseDouble(lastMap.getValue() + "");
                        newMap.put(lastMap.getKey() + "", nf.format(v));
                    } else {
                        newMap.put(lastMap.getKey(), lastMap.getValue());
                    }
                    hashSet.add(lastMap.getKey());
                }
            }
            lineNames.clear();
            lineNames.addAll(hashSet);
            lineData.clear();
            for (int j = 0; j < lineNames.size(); j++) {
                String name = lineNames.get(j);
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put(name, newMap.get(name));
                lineData.put("" + j, tempMap);
                Map<String, Object> listMapCon = new HashMap<>();
                listMapCon.put("quotaName", name);
                listMapCon.put("result", newMap.get(name));
                listMap.add(listMapCon);
            }


            ReportOption reportOption = new ReportOption();
            if (charType.equals("common")) {
                charType = "1";
            } else if (charType.equals("twoDimensional")) {
                return null;
            }
            List<String> charTypes = Arrays.asList(charType);
            int type = Integer.valueOf(charType);
            if (type == ReportOption.bar) {
                return null;    // 尚未拓展
            } else if (type == ReportOption.line) {
                return null;    // 尚未拓展
            } else if (type == ReportOption.pie) {
                List<Map<String, Object>> datalist = new ArrayList<>();
                for (Map<String, Object> resultMap : listMap) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("NAME", null == resultMap.get(dimensionName) ? resultMap.get(dimension) : resultMap.get(dimensionName));
                    map.put("TOTAL", resultMap.get("result"));
                    if(resultMap.get(dimensionName) != null){
                        map.put("NAME",resultMap.get(dimensionName));
                    }else {
                        //非 指标中配置的维度 关联出来的字段
                        if(dimensionName.equals("levelName")){
                            if(resultMap.get(dimension).equals("1")){
                                map.put("NAME","一级医院");
                            }else  if(resultMap.get(dimension).equals("2")){
                                map.put("NAME","二级医院");
                            }else  if(resultMap.get(dimension).equals("3")){
                                map.put("NAME","三级医院");
                            }
                        }
                    }
                    map.put("TOTAL",resultMap.get("result"));
                    datalist.add(map);
                }
                option = reportOption.getPieEchartOption(title, "", "", datalist, lineNames.get(0), null);
            }
            chartInfoModel.setOption(option.toString());
            chartInfoModel.setTitle(title);
            chartInfoModel.setxAxisMap(xAxisMap);
            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetDeviceReports, method = RequestMethod.GET)
    @ApiOperation(value = "报表-卫生设备一览")
    public Envelop getDeviceReports(
            @ApiParam(name = "year", value = "年份", required = true)
            @RequestParam(value = "year" , required = true) String year,
            @ApiParam(name = "district", value = "区县编码")
            @RequestParam(value = "district", required = false) String district,
            @ApiParam(name = "organization", value = "机构名称或者组织机构代码", defaultValue = "")
            @RequestParam(value = "organization", required = false) String organization,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{
        Envelop envelop = new Envelop();
        List<MQcDevice> list = new ArrayList<>();
        DataList dataList = deviceService.listMQcDeviceByYearAndDistrictAndOrg(year,district,organization,page ,size);
        if(null!= dataList && null!=dataList.getList() && dataList.getList().size()>0){
            for(int i=0; i<dataList.getList().size(); i++){
                Map<String,Object> dataMap = (Map<String, Object>) dataList.getList().get(i);
                MQcDevice newEntity = objectMapper.readValue(toJson(dataMap), MQcDevice.class);
                list.add(newEntity);
            }
        }
        envelop.setDetailModelList(list);
        envelop.setSuccessFlg(true);
        envelop.setPageSize(size);
        envelop.setCurrPage(page);
        envelop.setTotalPage((int)dataList.getPage());
        envelop.setTotalCount((int)dataList.getCount());
        return envelop;
    }

    protected String toJson(Object obj) throws JsonProcessingException {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 如果有计量单位，则把数值换算成相应的数值单位
     * @param optionData
     * @param dataMeasurement
     * @return
     */
    public List<List<Object>> discountByMeasurement(List<List<Object>> optionData, String dataMeasurement) {
        if (!StringUtils.isEmpty(dataMeasurement)) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            nf.setMaximumFractionDigits(2);
            List<List<Object>> handleList = new ArrayList<>();
            double v = Double.parseDouble(dataMeasurement);
            optionData.forEach(one -> {
                List<Object> list = new ArrayList<>();
                one.forEach(item -> {
                    if(item != null && !item.toString().equals("--")){
                        item = nf.format(Double.parseDouble(item.toString()) / v);
                        list.add(item);
                    }else {
                        list.add(0);
                    }
                });
                handleList.add(list);
            });
            return handleList;
        }
        return optionData;
    }
}
