package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.echarts.Option;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.echarts.ChartDataModel;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.model.jpa.RsResourceQuota;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.quota.BaseStatistsService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.service.resource.ResourceQuotaService;
import com.yihu.quota.service.singledisease.SingleDiseaseService;
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
    private SingleDiseaseService singleDiseaseService;

    public static String orgHealthCategoryCode = "orgHealthCategoryCode";

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
            @RequestParam(value = "dimension", required = false) String dimension
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
                List<Map<String, Object>> quotaResult = baseStatistsService.getSimpleQuotaReport(code, filter, dimension,false);
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
                vMap.put(maxQuotaCode, vMap.get("result")==null ? 0 : nf.format(Double.valueOf(vMap.get("result").toString())));
                for (String viewQuotaCode : otherQuotaViewResult.keySet()) {
                    for (Map<String, Object> quotaResultMap : otherQuotaViewResult.get(viewQuotaCode)) {
                        if (quotaResultMap.get(dimension) != null) {
                            if (vMap.get(dimension).toString().trim().equals(quotaResultMap.get(dimension).toString().trim())) {
                                vMap.put(viewQuotaCode, quotaResultMap.get("result")==null ? 0 : nf.format(Double.valueOf(quotaResultMap.get("result").toString())));
                                break;
                            } else {
                                vMap.put(viewQuotaCode, 0);
                            }
                        } else {
                            vMap.put(viewQuotaCode, 0);
                        }
                    }
                }
            }
            List<Map<String, Object>> resultList = quotaViewResult.get(maxQuotaCode);

            if(dimension.equals(orgHealthCategoryCode)){//如果是特殊机构类型树状机构需要转成树状结构
                List<Map<String, Object>> orgHealthCategoryList = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
                dataList = baseStatistsService.setResult(maxQuotaCode, orgHealthCategoryList, resultList, null);
            }else {
                dataList = resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        //二次指标为除法运算的  合计需要重新计算
        //TODO
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
            sum += Double.valueOf(map.get(code).toString());
            if(map.containsKey("children")){
                calculateSum(sum,code,(List<Map<String, Object>>) map.get("children"));
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
            @RequestParam(value = "title", required = false) String title
    ) {
        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
        List<String> charTypes = Arrays.asList(charstr.split(","));
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        String dimensionName = dimension + "Name";
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
                    List<Map<String, Object>> resultListMap = baseStatistsService.getSimpleQuotaReport(tjQuota.getCode(), filter, dimension,true);
                    if (resultListMap != null && resultListMap.size() > 0) {
                        for (Map<String, Object> map : resultListMap) {
                            if (map != null && map.size() > 0) {
                                listMap.add(map);
                                //第一种 ES库中有定义的维度 如org,slaveKey1
                                //第二种 ES库中未定义的维度 如level，economic
                                if (map.containsKey(dimensionName)) {
                                    dataMap.put(map.get(dimensionName).toString(), map.get("result"));
                                    xAxisMap.put(map.get(dimensionName).toString(), map.get(dimension).toString());
                                } else {
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
                }
                int type = Integer.valueOf(typeStr);
                if (type == ReportOption.bar) {
                    option = reportOption.getLineEchartOptionMoreChart(title, "", "", xData, optionData, lineNames, charTypes);
                } else if (type == ReportOption.line) {
                    option = reportOption.getLineEchartOptionMoreChart(title, "", "", xData, optionData, lineNames, charTypes);
                } else if (type == ReportOption.pie) {
                    List<Map<String, Object>> datalist = new ArrayList<>();
                    for (Map<String, Object> resultMap : listMap) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("NAME", resultMap.get(dimensionName));
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

    @RequestMapping(value = ServiceApi.TJ.GetHeatMapByQuotaCode, method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    public Envelop getHeatMap(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception {
        Envelop envelop = new Envelop();
        String heatMapPoint = singleDiseaseService.getHeatMap(quotaCode);
        envelop.setSuccessFlg(true);
        envelop.setObj(heatMapPoint);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetNumberOfDiabetes, method = RequestMethod.GET)
    @ApiOperation(value = "糖尿病患者数")
    public Envelop getNumberOfDiabetes(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> numberOfDiabetes = singleDiseaseService.getNumberOfDiabetes(quotaCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(numberOfDiabetes);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetPieData, method = RequestMethod.GET)
    @ApiOperation(value = "获取饼图数据")
    public Envelop getPieData(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, Object> pieDataInfo = singleDiseaseService.getPieDataInfo(quotaCode);
        envelop.setSuccessFlg(true);
        envelop.setObj(pieDataInfo.get("legendData"));
        ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) pieDataInfo.get("seriesData");
        envelop.setDetailModelList(seriesData);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetLineData, method = RequestMethod.GET)
    @ApiOperation(value = "获取折线图数据")
    public Envelop getLineData(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, List<String>> map = singleDiseaseService.getLineDataInfo(quotaCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(map.get("xData"));
        envelop.setObj(map.get("valueData"));
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetBarData, method = RequestMethod.GET)
    @ApiOperation(value = "获取柱状图数据")
    public Envelop getBarData(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode,
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, List<String>> map = null;
        if ("1".equals(type)) {
            map = singleDiseaseService.getSingleBarDataInfo(quotaCode);
            envelop.setDetailModelList(map.get("valueData"));
            envelop.setObj(map.get("xData"));
        } else {
            map = singleDiseaseService.getMultipleBarDataInfo(quotaCode);
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> myMap = new HashMap<>();
            myMap.put("男", map.get("valueData1"));
            myMap.put("女", map.get("valueData2"));
            list.add(myMap);
            envelop.setDetailModelList(list);
            envelop.setObj(map.get("xData"));
        }
        envelop.setSuccessFlg(true);

        return envelop;
    }

    private Integer getNum(Map<String, Object> dataMap) {
        Integer num = 0;
        for (String key : dataMap.keySet()) {
            Integer result = null != dataMap.get(key) ? Integer.parseInt(dataMap.get(key).toString()) : 0;
            num += result;
        }
        return num;
    }
}
