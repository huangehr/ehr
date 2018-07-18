package com.yihu.quota.service.quota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.dao.jpa.TjQuotaGovProvisionDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.save.TjDataSaveService;
import com.yihu.quota.service.singledisease.SingleDiseaseService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.SaveModel;
import net.bytebuddy.implementation.bytecode.Throw;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by janseny on 2018/01/15.
 */
@Service
public class BaseStatistsService {
    Logger log = LoggerFactory.getLogger(BaseStatistsService.class);
    @Autowired
    private TjQuotaDao quotaDao;
    @Autowired
    private EsResultExtract esResultExtract;
    @Autowired
    private TjDimensionMainService tjDimensionMainService;
    @Autowired
    private TjDimensionSlaveService tjDimensionSlaveService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OrgHealthCategoryStatisticsService orgHealthCategoryStatisticsService;
    @Autowired
    private TjDataSourceService dataSourceService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private SingleDiseaseService singleDiseaseService;
    @Autowired
    private TjQuotaGovProvisionDao tjQuotaGovProvisionDao;
    @Autowired
    private TjDataSaveService dataSaveService;

    private static String orgHealthCategory = "orgHealthCategory";
    public static String orgHealthCategoryCode = "orgHealthCategoryCode";
    public static String resultField = "result";
    public static String quotaDateField = "quotaDate";
    public static String firstColumnField = "firstColumn";

    /**
     *  特殊机构类型   根据 上级基础指标code 获取基础数据集
     * @param code
     * @param filter
     * @param dimension
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrgHealthCategoryQuotaResultList(String code,String dimension,String filter, String top) throws Exception {
        List<Map<String, Object>> dimenListResult = getOrgHealthCategoryAggregationResult(code, dimension, filter, top);
        return dimenListResult;
    }


    /**
     * 根据指标code 和维度及条件 分组获取指标查询结果集
     * @param code
     * @param filter
     * @param dimension
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getQuotaResultList(String code,String dimension,String filter,String dateType, String top) throws Exception {
        TjQuota tjQuota = quotaService.findByCode(code);
        if( tjQuota != null && StringUtils.isNotEmpty(tjQuota.getResultGetType())){
            if (tjQuota.getResultGetType().equals("2")){//二次统计
                TjQuotaDataSource quotaDataSorce = dataSourceService.findSourceByQuotaCode(code);
                if(quotaDataSorce != null){
                    JSONObject obj = new JSONObject().fromObject(quotaDataSorce.getConfigJson());
                    EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
                    if(StringUtils.isNotEmpty(esConfig.getSuperiorBaseQuotaCode()) && StringUtils.isEmpty(esConfig.getEspecialType()) ){
                        code =  esConfig.getSuperiorBaseQuotaCode();
                    }
                }
            }
        }
        List<Map<String, Object>> dimenListResult = new ArrayList<>();
        if(StringUtils.isNotEmpty(dateType)){
            dimenListResult = getTimeAggregationResult(code, dimension, filter, dateType);
        }else {
            dimenListResult = getAggregationResult(code, dimension, filter, top);
        }
        return dimenListResult;
    }


    public List<Map<String, Object>>  addQuota(String addFirstQuotaCode,String firstFilter, String addSecondQuotaCode, String secondFilter,String operation ,String dimension,String dateType, String top) throws Exception {
        List<Map<String, Object>> firstList = getQuotaResultList(addFirstQuotaCode,dimension,firstFilter,dateType, top);
        List<Map<String, Object>> secondList =  getQuotaResultList(addSecondQuotaCode,dimension,secondFilter,dateType, top);
        dimension = StringUtils.isNotEmpty(dateType)? (StringUtils.isNotEmpty(dimension)? dimension +";"+dateType : dateType):dimension;
        return addition(dimension, firstList, secondList, Integer.valueOf(operation));
    }


    public List<Map<String, Object>> addition(String dimension, List<Map<String, Object>> firstList, List<Map<String, Object>> secondList,int operation){
        List<Map<String, Object>> addResultList = new ArrayList<>();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(2);
        String [] moleDimensions = dimension.split(";");
        for(Map<String, Object> firstMap :firstList) {
            if (null != firstMap && firstMap.size() > 0 ) {
                Map<String, Object> map = new HashMap<>();
                double firstResultVal = Double.valueOf(firstMap.get("result") == null ? "0" : firstMap.get(resultField).toString());
                map.put(firstColumnField, firstMap.get(firstColumnField));
                String firstKeyVal = "";
                for(int i = 0 ;i < moleDimensions.length ; i++){
                    if(i == 0){
                        firstKeyVal = firstMap.get(moleDimensions[i]).toString();
                    }else {
                        firstKeyVal = firstKeyVal + "-" + firstMap.get(moleDimensions[i]).toString() ;
                    }
                    map.put(moleDimensions[i], firstMap.get(moleDimensions[i]).toString());
                }
                boolean pflag = true;
                for(Map<String, Object> secondMap :secondList) {
                    String secondKeyVal = "";
                    String [] dimeDimensions = dimension.split(";");
                    for(int i = 0 ;i < dimeDimensions.length ; i++){
                        if(i == 0){
                            secondKeyVal = secondMap.get(dimeDimensions[i]).toString();
                        }else {
                            secondKeyVal = secondKeyVal + "-" + secondMap.get(dimeDimensions[i]).toString() ;
                        }
                    }
                    if(firstKeyVal.equals(secondKeyVal) || "quotaName".equals(dimension)){  // 如果维度是quotaName，则进入逻辑
                        double point = 0;
                        double dimeResultVal = Double.valueOf(secondMap.get(resultField).toString());
                        BigDecimal first = new BigDecimal(Double.toString(firstResultVal));
                        BigDecimal second = new BigDecimal(Double.toString(dimeResultVal));
                        if(operation == 1){ //1 加法 默认
                            point = first.add(second).doubleValue();
                        }else if(operation == 2){ //2 减法
                            point = first.subtract(second).doubleValue();
                        }
                        map.put(resultField,nf.format(point));
                        addResultList.add(map);
                        pflag = false;
                        break;
                    }
                }
                if(pflag){
                    map.put(resultField,firstResultVal);
                    addResultList.add(map);
                }
            }
        }
        // 第一加数列表个数小雨第二个加数列表
        if (secondList.size() - firstList.size() > 0) {
            for(Map<String, Object> secondMap :secondList) {
                Map<String, Object> map = new HashMap<>();
                map.put(firstColumnField, secondMap.get(firstColumnField));
                for(int i = 0 ;i < moleDimensions.length ; i++){
                    map.put(moleDimensions[i], secondMap.get(moleDimensions[i]).toString());
                }
                double point = 0;
                double secondResultVal = Double.valueOf(secondMap.get("result") == null ? "0" : secondMap.get(resultField).toString());
                if (secondResultVal != 0) {
                    if(operation == 1){ //1 加法 默认
                        point = secondResultVal ;
                    }else if(operation == 2){ //2 减法
                        point = -secondResultVal;
                    }
                }
                map.put(resultField, nf.format(point));
                addResultList.add(map);
            }
        }
        //检查后面指标的维度是否全部有 累加进去
        /*Map<String, Object> addResuDimenMap = new HashMap<>();
        for(int k = 0;k < addResultList.size();k++) {
            Map<String, Object> addResultMap = addResultList.get(k);
            String addDimenStr = "";
            for (int i = 0; i < moleDimensions.length; i++) {
                addDimenStr += addResultMap.get(moleDimensions[i]).toString() + "-";
            }
            addResuDimenMap.put(addDimenStr,addDimenStr);
        }

        for(Map<String, Object> secondMap :secondList) {
            String secondDimenStr = "";
            String addDimenStr = "";
            for(int i = 0 ;i < moleDimensions.length ; i++){
                secondDimenStr +=  secondMap.get(moleDimensions[i]).toString() + "-";
            }
            if( !addResuDimenMap.containsKey(secondDimenStr)){
                if( !addDimenStr.equals(secondDimenStr)){
                    Map<String, Object> map = new HashMap<>();
                    float dimeResultVal = Float.valueOf(secondMap.get(resultField).toString());
                    map.put(resultField,df.format(dimeResultVal));
                    map.put(firstColumnField, secondMap.get(firstColumnField));
                    for(int i = 0 ;i < moleDimensions.length ; i++){
                        map.put(moleDimensions[i], secondMap.get(moleDimensions[i]).toString());
                    }
                    addResultList.add(map);
                }
            }
        }*/
        return  addResultList;
    }

    /**
     * 两个维度相同指标除法运算
     * @param molecular
     * @param denominator
     * @param dimension
     * @param molecularFilter
     * @param denominatorFilters
     * @param operation
     * @param operationValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>>  divisionQuota(String molecular, String denominator, String dimension,
        String molecularFilter,String denominatorFilters,String operation,String operationValue,String dateType, String top) throws Exception {
        if(StringUtils.isEmpty(dimension) && StringUtils.isNotEmpty(dateType)){
            dimension = dateType;
        }
        List<Map<String, Object>> moleList = getSimpleQuotaReport(molecular, molecularFilter,dimension ,false , null);
        List<Map<String, Object>> denoList =  getSimpleQuotaReport(denominator,denominatorFilters,dimension,false, null);

        dimension = StringUtils.isNotEmpty(dateType)? (StringUtils.isNotEmpty(dimension)? dimension +";"+dateType : dateType):dimension;
       return division(dimension,moleList,denoList,Integer.valueOf(operation),Integer.valueOf(operationValue));
    }

    /**
     * 指标除法运算 分母按年份获取总数
     * @param molecular 分子
     * @param dimension 分母
     * @param molecularFilter 分子条件
     * @param denominatorFilter 分母条件
     * @param operation
     * @param operationValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> divisionQuotaDenoConstant(String molecular, String dimension,String molecularFilter,String denominatorFilter,
        String operation,String operationValue,String dateType , String top) throws Exception {
        Double denominatorVal = 0.0;
        List<Map<String, Object>> moleList = getQuotaResultList(molecular,dimension,molecularFilter,dateType, top);
        String year = DateUtil.getYearFromYMD(new Date());
        String town = "";
        if(StringUtils.isNotEmpty(denominatorFilter)){
            String [] filter = denominatorFilter.split("and");
            if(filter.length > 0 ){
                for(String key : filter){
                    if(key.contains(quotaDateField)){
                        year = key.substring(key.indexOf("'")+1,key.indexOf("'")+5);//quotaDate >= '2018-03-01'
                    }
                    if(key.contains("town")){
                        town =  key.substring(key.indexOf("=")+1);
                    }
                }
            }
        }
        Long sumValue = null;
        if(StringUtils.isNotEmpty(town)){
            sumValue = tjQuotaGovProvisionDao.getSumByDistrict(Long.parseLong(town),year);
       }else {
            sumValue = tjQuotaGovProvisionDao.getSumByDistrict(year);
       }
        // 获取分母的数值
        if(sumValue != null && sumValue.doubleValue() != 0){
            denominatorVal = sumValue.doubleValue();
            return divisionDenoConstant(dimension, moleList, denominatorVal, Integer.valueOf(operation), Integer.valueOf(operationValue));
        }else {
            return  null;
        }
    }

    /**
     * 指标结果相除
     * 除法运算 分母按年份获取总数
     * @param dimension 维度
     * @param moleList 分子
     * @param denominatorVal 分母 数值
     * @param operation 运算方式 1 乘法 2 除法
     * @param operationValue 运算参数值
     *
     */
    public List<Map<String, Object>> divisionDenoConstant(String dimension, List<Map<String, Object>> moleList, Double denominatorVal,int operation,int operationValue) {
        List<Map<String, Object>> divisionResultList = new ArrayList<>();
        for (Map<String, Object> moleMap : moleList) {
            Map<String, Object> map = new HashMap<>();
            double moleResultVal = Double.valueOf(moleMap.get(resultField).toString());
            String moleKeyVal = "";
            String [] moleDimensions = dimension.split(";");
            for(int i = 0 ;i < moleDimensions.length ; i++){
                if(i == 0){
                    moleKeyVal = moleMap.get(moleDimensions[i]).toString();
                }else {
                    moleKeyVal = moleKeyVal + "-" + moleMap.get(moleDimensions[i]).toString() ;
                }
                map.put(moleDimensions[i], moleMap.get(moleDimensions[i]).toString());
            }
            if (moleResultVal == 0) {
                map.put(resultField,0);
                divisionResultList.add(map);
            } else {
                double point = 0;
                DecimalFormat df = new DecimalFormat("0.00");
                if (operation == 1) {
                    point = (moleResultVal / denominatorVal) * operationValue;
                } else if (operation == 2) {
                    point =  (moleResultVal / denominatorVal) / operationValue;
                }
                map.put(resultField, df.format(point));
                divisionResultList.add(map);
            }
        }
        return divisionResultList;
    }



    /**
     * 指标结果相除
     * @param dimension 维度
     * @param moleList 分子
     * @param denoList 分母
     * @param operation 运算方式 1 乘法 2 除法
     * @param operationValue 运算参数值
     *
     */
    public List<Map<String, Object>> division(String dimension, List<Map<String, Object>> moleList, List<Map<String, Object>> denoList,int operation,int operationValue){
        List<Map<String, Object>> divisionResultList = new ArrayList<>();
        for(Map<String, Object> moleMap :moleList) {
            if (null != moleMap && moleMap.size() > 0 ) {
                Map<String, Object> map = new HashMap<>();
                double moleResultVal = Double.valueOf(moleMap.get(resultField) == null ? "0" : moleMap.get(resultField).toString());
                String moleKeyVal = "";
                String [] moleDimensions = dimension.split(";");
                for(int i = 0 ;i < moleDimensions.length ; i++){
                    if(i == 0){
                        moleKeyVal = moleMap.get(moleDimensions[i]).toString();
                    }else {
                        moleKeyVal = moleKeyVal + "-" + moleMap.get(moleDimensions[i]).toString() ;
                    }
                    map.put(firstColumnField, moleMap.get(firstColumnField));
                    map.put(moleDimensions[i], moleMap.get(moleDimensions[i]).toString());
                }
                if (moleResultVal == 0) {
                    map.put(resultField,0);
                    divisionResultList.add(map);
                } else {
                    for(Map<String, Object> denoMap :denoList) {
                        String dimenKeyVal = "";
                        String [] dimeDimensions = dimension.split(";");
                        for(int i = 0 ;i < dimeDimensions.length ; i++){
                            if(i == 0){
                                dimenKeyVal = denoMap.get(dimeDimensions[i]).toString();
                            }else {
                                dimenKeyVal = dimenKeyVal + "-" + denoMap.get(dimeDimensions[i]).toString() ;
                            }
                        }
                        if(moleKeyVal.equals(dimenKeyVal)){
                            double point = 0;
                            DecimalFormat df = new DecimalFormat("0.0");
                            float dimeResultVal = Float.valueOf(denoMap.get(resultField).toString());
                            if(dimeResultVal != 0){
                                if(operation == 1){
                                    point = (moleResultVal/dimeResultVal) * operationValue;
                                }else if(operation == 2){
                                    point = (moleResultVal/dimeResultVal) / operationValue;
                                }
                            }
                            map.put(resultField,df.format(point));
                            divisionResultList.add(map);
                            break;
                        }
                    }
                }
            }
        }
        return  divisionResultList;
    }

    /**
     * 特殊机构类别 根据条件查询结果
     * @param code
     * @param filters
     * @param  dateType 日期类型
     * @param isTrunTree 是否转为机构类型树状机构
     * @throws Exception
     */
    public List<Map<String, Object>>  getOrgHealthCategory(String code,String filters,String dateType,boolean isTrunTree, String top) throws Exception {

        List<Map<String, Object>> dimenListResult = new ArrayList<>();
        if(dateType != null && (dateType.contains("year") || dateType.contains("quarter")|| dateType.contains("month") || dateType.contains("day"))){
            dimenListResult = getTimeAggregationResult(code,orgHealthCategoryCode,filters,dateType);//dimension 维度为 year,month,day
        }else {
            dimenListResult = getAggregationResult(code, orgHealthCategoryCode, filters, top);
        }
        if(isTrunTree){
            List<Map<String, Object>> orgHealthCategoryList = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
            List<Map<String, Object>> resultList = setResult(code,orgHealthCategoryList,dimenListResult,dateType);
            return resultList;
        }else {
            return dimenListResult;
        }
    }

    /**
     * 递归循环 查询机构类型对应的名称和父节点
     * @param orgHealthCategoryList
     * @param dimenListResult
     * @param
     * @return
     */
    public List<Map<String,Object>> setResult(String quotaCode,List<Map<String,Object>> orgHealthCategoryList,List<Map<String, Object>> dimenListResult,String dateType){
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i=0 ; i < orgHealthCategoryList.size() ; i++ ){
            Map<String,Object> mapCategory = orgHealthCategoryList.get(i);
            String code = mapCategory.get("code").toString();
            boolean notExitFalg = true;
            for(Map<String, Object> dimenMap : dimenListResult){
                boolean flag = false;
                if(dimenMap.get(orgHealthCategoryCode) != null){
                    flag = dimenMap.get(orgHealthCategoryCode).equals(code);
                }
                if(dimenMap.get(code) != null || flag ){
//                    mapCategory.putAll(dimenMap);
                    if(dimenMap.containsKey(code)){
                        mapCategory.put(code,dimenMap.get(code));
                        mapCategory.put(resultField,dimenMap.get(resultField)!=null ? dimenMap.get(resultField):dimenMap.get(code));
                    }
                    if(StringUtils.isNotEmpty(dateType)){
                        mapCategory.put(dimenMap.get(dateType).toString(),dimenMap.get(resultField));
                    }
                    mapCategory.put(quotaCode,dimenMap.get(resultField));
                    break;
                }
            }
            if(notExitFalg){
                mapCategory.put(resultField,0);
                mapCategory.put(quotaCode,0);
            }
            mapCategory.put(firstColumnField,mapCategory.get("text"));
            result.add(mapCategory);
            if(mapCategory.get("children") != null){
                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                mapCategory.put("children",setResult(quotaCode,childrenOrgHealthCategoryList,dimenListResult,dateType));
            }
        }
        return  result;
    }


//    /**
//     * 递归循环 查询机构类型对应的名称和父节点
//     * @param orgHealthCategoryList
//     * @param dimenListResult
//     * @param
//     * @return
//     */
//    public List<Map<String,Object>> setResultAllDimenMap(String quotaCode,List<Map<String,Object>> orgHealthCategoryList,List<Map<String, Object>> dimenListResult,String dateType){
//        List<Map<String,Object>> result = new ArrayList<>();
//        for(int i=0 ; i < orgHealthCategoryList.size() ; i++ ){
//            Map<String,Object> mapCategory = orgHealthCategoryList.get(i);
//            String code = mapCategory.get("code").toString();
//            boolean notExitFalg = true;
//            for(Map<String, Object> dimenMap : dimenListResult){
//                boolean flag = false;
//                if(dimenMap.get(orgHealthCategoryCode) != null){
//                    flag = dimenMap.get(orgHealthCategoryCode).equals(code);
//                }
//                if(dimenMap.get(code) != null || flag ){
//                    //补充所有信息
//                    mapCategory.putAll(dimenMap);
//                    if(dimenMap.containsKey(code)){
//                        mapCategory.put(code,dimenMap.get(code));
//                        mapCategory.put(resultField,dimenMap.get(resultField)!=null ? dimenMap.get(resultField):dimenMap.get(code));
//                    }
//                    if(StringUtils.isNotEmpty(dateType)){
//                        mapCategory.put(dimenMap.get(dateType).toString(),dimenMap.get(resultField));
//                    }
//                    mapCategory.put(quotaCode,dimenMap.get(resultField));
//                    notExitFalg = false;
//                    break;
//                }
//            }
//            if(notExitFalg){
//                mapCategory.put(resultField,0);
//                mapCategory.put(quotaCode,0);
//            }
//            mapCategory.put(firstColumnField,mapCategory.get("text"));
//            result.add(mapCategory);
//            if(mapCategory.get("children") != null){
//                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
//                mapCategory.put("children",setResultAllDimenMap(quotaCode, childrenOrgHealthCategoryList, dimenListResult, dateType));
//            }
//        }
//        return  result;
//    }

    /**
     * 递归循环 计算各目录结构的值
     * @param orgHealthCategoryList
     * @param dimenListResult
     * @param
     * @return
     */
    public List<Map<String,Object>> allCategoryResultMap(List<String> quotaCodes,List<Map<String,Object>> orgHealthCategoryList,List<Map<String, Object>> dimenListResult ){

        List<Map<String,Object>> resultMap = new ArrayList<>();
        for(int i=0 ; i < orgHealthCategoryList.size() ; i++ ){
            Map<String,Object> mapCategory = orgHealthCategoryList.get(i);
            Map<String,Object> map = new HashMap<>();
            double parentResult = 0;
            mapCategory.put(firstColumnField,mapCategory.get("text"));
            map = getParentAllChildren(quotaCodes,mapCategory,map, dimenListResult,parentResult);
            if(map == null || map.size() == 0 ){
                for(String quotaCode : quotaCodes){
                    map.put(quotaCode,0);
                }
                map.put(resultField,0);
            }
            mapCategory.putAll(map);
            resultMap.add(mapCategory);
            if(mapCategory.get("children") != null){
                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                mapCategory.put("children",allCategoryResultMap(quotaCodes,childrenOrgHealthCategoryList,dimenListResult));
            }
        }
        return  resultMap;
    }

    //获取该节点下所有末节点的结果和
    public   Map<String,Object> getParentAllChildren(List<String> quotaCodes, Map<String,Object> mapCategory,Map<String,Object> returnMap, List<Map<String, Object>> dimenListResult, double parentResult ){
        try {
                boolean childrenFlag = false;
                if(mapCategory.get("children") != null){
                    List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                    if(childrenOrgHealthCategoryList != null && childrenOrgHealthCategoryList.size() > 0){
                        childrenFlag = true;
                    }
                }
                if(childrenFlag){
                    List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                    for(int j=0 ; j < childrenOrgHealthCategoryList.size() ; j++ ){
                        Map<String,Object> childrenMapCategory = childrenOrgHealthCategoryList.get(j);
                        returnMap =  getParentAllChildren(quotaCodes ,childrenMapCategory,returnMap, dimenListResult,parentResult);
                    }
                }else{
                    for(Map<String, Object> dimenMap :dimenListResult){
                        if(dimenMap.get(orgHealthCategoryCode) != null && dimenMap.get(resultField) != null){
                            if(dimenMap.get(orgHealthCategoryCode).equals(mapCategory.get("code"))){
                                double result = Double.parseDouble(dimenMap.get(resultField).toString());
                                double oldResult = 0;
                                if(returnMap.get(resultField) != null){
                                    oldResult = Double.parseDouble(returnMap.get(resultField).toString());
                                }
                                returnMap.put(resultField,result + oldResult);
                                for(String quotaCode : quotaCodes){
                                    double quotaResult = Double.parseDouble(dimenMap.get(quotaCode).toString());
                                    double oldQuotaResult = 0;
                                    if( returnMap.get(quotaCode) != null ){
                                        oldQuotaResult = Double.parseDouble(returnMap.get(quotaCode).toString());
                                    }
                                    returnMap.put(quotaCode,quotaResult + oldQuotaResult);
                                }
                                break;
                            }
                        }
                    }
                }
        }catch (Exception e){
            throw new NumberFormatException("统计数据转换异常");
        }
        return returnMap;
    }


    /**
     * 时间聚合查询指标结果
     * @param code
     * @param dimension 多维度 ; 分开
     * @param filter
     * @throws Exception
     */
    public  List<Map<String, Object>> getTimeAggregationResult(String code,String dimension, String filter,String dateDime) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        Map<String,String>  dimensionDicMap = getDimensionDicMap(code,dimension);
        List<String> dimenList = getDimenList(dimension);
        String groupDimension = joinDimen(dimension);

        List<Map<String, Object>> dimenListResult = esResultExtract.searcherSumByGroupByTime(tjQuota, groupDimension, filter, dateDime);

        List<Map<String, Object>> resultList = new ArrayList<>();
        String dateHist = "date_histogram(field=quotaDate,interval="+ dateDime +")";

        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                if(key.equals(dateHist)) {
                    String value = "";
                    if (dateDime.equals("year")) {
                        value = map.get(key).toString().substring(0, 4);
                    } else if (dateDime.contains("quarter")) {
                        value = map.get(key).toString().substring(0, 7);
                        if(value.contains("-04")){
                            value = value.replace("-04","02");
                        }else if(value.contains("-07")){
                            value = value.replace("-07","03");
                        }else if(value.contains("-10")){
                            value = value.replace("-10","04");
                        }
                    } else if (dateDime.contains("month")) {
                        value = map.get(key).toString().substring(0, 7);
                    } else if (dateDime.contains("week")) {
                        value = map.get(key).toString().substring(0, 7);
                    } else if (dateDime.contains("day")) {
                        value = map.get(key).toString().substring(0, 10);
                    }
                    dataMap.put(dateDime, value);
                }
                if(dimenList.contains(key)){
                    if(dimensionDicMap.get(map.get(key).toString().toLowerCase())  != null){
                        String dictVal = dimensionDicMap.get(map.get(key).toString().toLowerCase());
                        dataMap.put(key+"Name",dictVal);
                        dataMap.put(key,map.get(key).toString());
                        dataMap.put(firstColumnField, dictVal);
                    }else {
                        if(key.equals(quotaDateField)){
                            String dateFormat = "yyyy-MM-dd";
                            if (dateDime.equals("year")) {
                                dateFormat = "yyyy";
                            }else if(dateDime.equals("month")){
                                dateFormat = "yyyy-MM";
                            }
                            SimpleDateFormat format =  new SimpleDateFormat(dateFormat);
                            Long time = new Long(Long.valueOf(map.get(key).toString()));
                            String quotaDate = format.format(time);
                            dataMap.put(key, quotaDate);
                        }else {
                            dataMap.put(key,map.get(key));
                        }
                    }
                }
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                    dataMap.put(firstColumnField,map.get("text"));
                }
                if(key.equals("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    dataMap.put(resultField,  nf.format(map.get(key)));
                }
            }
             resultList.add(dataMap);
         }
        return resultList;
    }

    /**
     * 获取聚合查询指标结果
     * @param code
     * @param dimension 多维度 ; 分开
     * @param filter
     * @throws Exception
     */
    public  List<Map<String, Object>> getOrgHealthCategoryAggregationResult(String code,String dimension, String filter, String top) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        String groupDimension = "";
        if(dimension.contains(";")){
            String[] dimens =  dimension.split(";");
            for(int i =0 ;i<dimens.length ;i++){
                groupDimension += dimens[i] + ",";
            }
            groupDimension = groupDimension.substring(0,groupDimension.length()-1);
        }else {
            groupDimension = dimension;
        }
        List<Map<String, Object>>  dimenListResult = esResultExtract.searcherSumGroup(tjQuota, groupDimension, filter, resultField, "", "", top);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            boolean quotaFlag = false;
            for(String key :map.keySet()){
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                }
                if(key.equals("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    dataMap.put(resultField,  nf.format(map.get(key)));
                }
                if(key.equals(quotaDateField)){
                    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
                    Long time = new Long(Long.valueOf(map.get(key).toString()));
                    String quotaDate = format.format(time);
                    dataMap.put(quotaDateField, quotaDate);
                    quotaFlag = true;
                }
            }
            if(quotaFlag){
                map.remove(quotaDateField);
            }
            dataMap.putAll(map);
            resultList.add(dataMap);
        }
        return resultList;
    }


    /**
     * 获取  特殊机构类别  聚合查询指标结果
     * @param code
     * @param dimension 多维度 ; 分开
     * @param filter
     * @throws Exception
     */
    public  List<Map<String, Object>> getAggregationResult(String code,String dimension, String filter, String top) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        Map<String,String>  dimensionDicMap = getDimensionDicMap(code,dimension);
        List<String> dimenList = getDimenList(dimension);
        String groupDimension = joinDimen(dimension);
        List<Map<String, Object>>  dimenListResult = esResultExtract.searcherSumGroup(tjQuota, groupDimension, filter, resultField, groupDimension, "asc", top);

        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                if(dimenList.contains(key)){
                    if(dimensionDicMap.get(map.get(key).toString().toLowerCase())  != null){
                        dataMap.put(key,dimensionDicMap.get(map.get(key).toString().toLowerCase()));
                        dataMap.put(key+"Name",dimensionDicMap.get(map.get(key).toString().toLowerCase()));
                        dataMap.put(firstColumnField,dimensionDicMap.get(map.get(key).toString().toLowerCase()));
                    }else {
                        dataMap.put(key,map.get(key));
                    }
                }
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                    dataMap.put(firstColumnField,map.get("text"));
                }
                if(key.equals("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    dataMap.put(resultField,  nf.format(map.get(key)));
                }
            }
            resultList.add(dataMap);
        }
        if (StringUtils.isEmpty(top) && "town".equals(dimension)) {
            resultList = noDataDimenDictionary(resultList,dimension,filter);
        }
        List<TjQuotaDimensionSlave> slaves = tjDimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(code);
        for(TjQuotaDimensionSlave slave :slaves){
            if(slave.getSlaveCode().equals("dept") ){
                resultList = filteUnKnowDept(resultList,dimension);
            }
            if(slave.getSlaveCode().equals("sex") ){
                resultList = filteUnKnowSex(resultList,dimension);
            }
        }
        return resultList;
    }

    /**
     * 过滤其他机构
     * @param dataList
     * @return
     */
    public List<Map<String, Object>> filteUnKnowDept(List<Map<String, Object>> dataList,String dimension){
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String,Object> map : dataList){
            if( !map.get(dimension).toString().contains("其他")){
                resultList.add(map);
            }
        }
        return  resultList;
    }

    /**
     * 过滤未说明的性别
     * @param dataList
     * @return
     */
    public List<Map<String, Object>> filteUnKnowSex(List<Map<String, Object>> dataList,String dimension){
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String,Object> map : dataList){
            if( !map.get(dimension).toString().contains("未说明")){
                resultList.add(map);
            }
        }
        return  resultList;
    }


    /**
     * 查询结果 对无数据的字典项补0
     * @param  dataList 数据集合
     * @param dimen 维度
     * @return
     */
    public List<Map<String, Object>> noDataDimenDictionary(List<Map<String, Object>> dataList,String dimen,String filter){
        Map<String, Object> dictMap = new HashMap<>();
        if(dimen.equals("town") || dimen.equals("org") ){
            String sql = "";
            if(dimen.equals("town") ){
                sql = "SELECT id as code,name as name  from address_dict where pid = '361100'";
            }
            if(dimen.equals("org") ){
                String areasql = "SELECT id from address_dict where pid = '361100'";
                String [] filters = filter.split("and");
                List<String> filterList = Arrays.asList(filter.split("and"));
                for(String fil : filterList){
                   if(fil.contains("town")){
                       areasql = fil.split("=")[1];
                   }
                }
                sql = "SELECT org_code as code,full_name as name from organizations WHERE administrative_division in(" + areasql + ")";
            }
            List<Map<String, Object>> dictDataList = jdbcTemplate.queryForList(sql);
            if(null != dictDataList) {
                for(int i = 0 ; i < dictDataList.size();i++){
                    if(null != dictDataList.get(i).get("code") && null != dictDataList.get(i).get("name")){
                        dictMap.put(dictDataList.get(i).get("code").toString(),dictDataList.get(i).get("name").toString());
                    }
                }
            }

            List<Map<String, Object>> resultList = new ArrayList<>();
            for(String code : dictMap.keySet()){
                Map<String,Object> oneMap = new HashMap<>();
                String result = "0";
                for(Map<String,Object> map : dataList){
                    if(map.get(dimen) !=null && map.get(dimen).equals(dictMap.get(code))){
                        result = map.get(resultField).toString();
                        break;
                    }
                }
                oneMap.put(firstColumnField,dictMap.get(code));
                oneMap.put(dimen+"Name",dictMap.get(code));
                oneMap.put(dimen,dictMap.get(code));
                oneMap.put(resultField,result);
                resultList.add(oneMap);
            }
            return  resultList;
        }
        return  dataList;
    }


    /**
     * 拼接维度分组
     * @param dimension
     * @return
     */
    public String joinDimen(String dimension){
        String groupDimension = "";
        if(dimension.contains(";")){
            String[] dimens =  dimension.split(";");
            for(int i =0 ;i<dimens.length ;i++){
                groupDimension += dimens[i] + ",";
            }
            groupDimension = groupDimension.substring(0,groupDimension.length()-1);
        }else {
            groupDimension = dimension;
        }
        return groupDimension;
    }

    /**
     * 获取维度List
     * @param dimension
     * @return 多维度 ；隔开
     */
    public List<String> getDimenList(String dimension){
        List<String> dimenList = new ArrayList<>();
        if(dimension.contains(";")){
            String[] dimens =  dimension.split(";");
            for(int i =0 ;i<dimens.length ;i++){
                dimenList.add(dimens[i]);
            }
        }else {
            dimenList.add(dimension);
        }
        return dimenList;
    }

    /**
     * 获取指标维度字典项
     * @param quotaCode
     * @param dimension 多维度 ；隔开
     * @return
     */
    public Map<String,String>  getDimensionDicMap(String quotaCode ,String dimension){
        Map<String,String>  dimensionDicMap = new HashMap<>();
        String[] dimens =  dimension.split(";");
        for(int i =0 ;i<dimens.length ;i++){
            String dictSql = getQuotaDimensionDictSql(quotaCode, dimens[i]);
            if(StringUtils.isNotEmpty(dictSql)){
                Map<String,String> dicMap = getDimensionMap(dictSql, dimens[i]);
                for(String key :dicMap.keySet()){
                    dimensionDicMap.put(key.toLowerCase(),dicMap.get(key));
                }
            }
        }
        return dimensionDicMap;
    }


    /**
     * 获取维度的字典 sql  -- 针对于查询结果 从ES库查询结果
     * @param quotaCode
     * @param dimension
     * @return
     */
    private String getQuotaDimensionDictSql(String quotaCode, String dimension) {
        boolean mainFlag = dimension.contains("province") || dimension.contains("city") ||dimension.contains("town")
                ||dimension.contains("org") ||dimension.contains("year") ||dimension.contains("quarter")
                ||dimension.contains("month") ||dimension.contains("day") || dimension.contains(quotaDateField) ;
        String dictSql = "";
        //查询维度 sql
        if( mainFlag){
            List<TjQuotaDimensionMain>  dimensionMains = tjDimensionMainService.findTjQuotaDimensionMainByQuotaCode(quotaCode);
            if(dimensionMains != null && dimensionMains.size() > 0){
                for(TjQuotaDimensionMain main:dimensionMains){
                    if(main.getMainCode().equals(dimension)){
                        dictSql = main.getDictSql();
                    }
                }
            }
        }else {
            if(StringUtils.isEmpty(dictSql)) {
                List<TjQuotaDimensionSlave> dimensionSlaves = tjDimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(quotaCode);
                if (dimensionSlaves != null && dimensionSlaves.size() > 0) {
                    if(StringUtils.isNotEmpty(dimension)){
                        String n = dimension.substring(dimension.length() - 1, dimension.length());
                        if(StringUtils.isNotEmpty(n) && (n.equals("1") || n.equals("2") || n.equals("3")) ){
                            int slave = Integer.valueOf(n);
                            if(dimensionSlaves.size() >= slave){
                                dictSql = dimensionSlaves.get(slave-1).getDictSql();
                            }
                        }
                    }
                }
            }
        }
        return dictSql;
    }

    /**
     *获取字典项数据集
     * @param dictSql
     * @param dimension
     * @return
     */
    public Map<String,String> getDimensionMap(String dictSql, String dimension) {
        Map<String,String> dimensionDicMap = new HashMap<>();
        if(StringUtils.isNotEmpty(dictSql)) {
            BasesicUtil baseUtil = new BasesicUtil();
            boolean main = dimension.contains("province") || dimension.contains("city") ||dimension.contains("town")
                    ||dimension.contains("org") ||dimension.contains("year") ||dimension.contains("month")
                    ||dimension.contains("quarter") ||dimension.contains("day");

            if( main){
                //主纬度字典项
                List<SaveModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(SaveModel.class));
                if(dictDatas != null ) {
                    for (SaveModel saveModel : dictDatas) {
                        String name = baseUtil.getFieldValueByName(dimension + "Name", saveModel);
                        String val = baseUtil.getFieldValueByName(dimension, saveModel);
                        if(StringUtils.isNotEmpty(val) && StringUtils.isNotEmpty(name)){
                            dimensionDicMap.put(val.toLowerCase(),name);
                        }
                    }
                }
            } else{
                //查询细维度字典数据
                List<DictModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(DictModel.class));
                for (DictModel dictModel : dictDatas) {
                    String name = baseUtil.getFieldValueByName("name", dictModel);
                    String val = baseUtil.getFieldValueByName("code", dictModel).toLowerCase();
                    dimensionDicMap.put(val,name);
                }
            }
        }
        return dimensionDicMap;
    }

    /**
     * 获取单个指标结果
     * @param code
     * @param filters 外部传入条件
     * @param dimension
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>>  getSimpleQuotaReport(String code,String filters,String dimension,boolean isTrunTree, String top) throws Exception {
        String dateType = "";
        //指标的展示维度，由视图中决定
        if(dimension.trim().equals("year")){
            dateType = "year";
            dimension = "";
        }else if(dimension.trim().equals("quarter")){
            dateType = "quarter";
            dimension = "";
        }else if(dimension.trim().equals("month")){
            dateType = "month";
            dimension = "";
        }else if(dimension.trim().equals("day")){
            dateType = "day";
            dimension = "";
        }
        List<Map<String, Object>> result = new ArrayList<>();
        TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(code);
        JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
        EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
        String configFilter = esConfig.getFilter();
        if(StringUtils.isNotEmpty(configFilter) && quotaDataSource.getSourceCode().equals("1")){//数据源为ES库
            TjQuotaDataSave quotaDataSave = dataSaveService.findByQuota(code);
            if(quotaDataSave != null && StringUtils.isNotEmpty(quotaDataSave.getConfigJson())){
                JSONObject objSave = new JSONObject().fromObject(quotaDataSave.getConfigJson());
                EsConfig esConfigSave = (EsConfig) JSONObject.toBean(objSave,EsConfig.class);
                if(StringUtils.isEmpty(esConfig.getIndex()) || esConfig.getIndex().equals(esConfigSave.getIndex()) ){
                    if(StringUtils.isNotEmpty(filters)){
                        filters += " and " + configFilter;
                    }else {
                        filters = configFilter;
                    }
                }
            }else {
                if(StringUtils.isNotEmpty(filters)){
                    filters += " and " + configFilter;
                }else {
                    filters = configFilter;
                }
            }
        }
        // 判断该指标是否需要同比， 需要的话拼接时间条件
        if (StringUtils.isNotEmpty(esConfig.getIncrementFlag())) {
            filters = filtersExchangeHandle(filters, esConfig);
            log.info("filters = {}", filters);
        }

        String molecularFilter = filters;
        String denominatorFilter = filters;

        if (StringUtils.isNotEmpty(esConfig.getGrowthFlag())) {
            result = getGrowthByQuota(dimension, filters, esConfig, dateType);
        } else {
            if (StringUtils.isNotEmpty(esConfig.getDateComparisonType())) {
                filters = getdateComparisonTypeFilter(esConfig,filters);
            }
            if( (StringUtils.isNotEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals(orgHealthCategory)){
                //特殊机构类型查询输出结果  只有查询条件没有维度 默认是 机构类型维度
                result = getOrgHealthCategory(code,filters,dateType,isTrunTree, top);
            }else if( (StringUtils.isNotEmpty(esConfig.getMolecular())) && StringUtils.isNotEmpty(esConfig.getDenominator())){//除法
                //除法指标查询输出结果
                molecularFilter = handleFilter(esConfig.getMolecularFilter(), molecularFilter);
                denominatorFilter = handleFilter(esConfig.getDenominatorFilter(), denominatorFilter);
                if (StringUtils.isNotEmpty(esConfig.getDivisionType()) && esConfig.getDivisionType().equals("2")) {
                    result = divisionQuotaDenoConstant(esConfig.getMolecular(), dimension, molecularFilter, denominatorFilter,esConfig.getPercentOperation(), esConfig.getPercentOperationValue(), dateType, top);
                } else {
                    result = divisionQuota(esConfig.getMolecular(), esConfig.getDenominator(), dimension, molecularFilter, denominatorFilter, esConfig.getPercentOperation(), esConfig.getPercentOperationValue(),dateType, top);
                }
            }else if(StringUtils.isNotEmpty(esConfig.getAddOperation())){
                String firstFilter = handleFilter(esConfig.getAddFirstFilter(), filters);
                String secondFilter = handleFilter(esConfig.getAddSecondFilter(), filters);
                result = addQuota(esConfig.getAddFirstQuotaCode(), firstFilter, esConfig.getAddSecondQuotaCode(), secondFilter, esConfig.getAddOperation(),dimension,dateType, top);
            }else if(StringUtils.isNotEmpty(esConfig.getSuperiorBaseQuotaCode())) {
                //二次统计 指标查询
                result = getQuotaResultList(esConfig.getSuperiorBaseQuotaCode(), dimension,filters,dateType, top);
            }else {
                //普通基础指标查询
                result = getQuotaResultList(code, dimension,filters,dateType, top);
            }
        }
        return result;
    }

    //时间对比类型 时的 查询条件处理
    public String getdateComparisonTypeFilter(EsConfig esConfig,String filters){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Map<String,String> filterMap = new HashMap<>();
        if(StringUtils.isNotEmpty(filters)){
            String [] filter = filters.split("and");
            String year = "";
            String month = "";
            String day = "";
            if(filter.length > 0 ){
                for(String key : filter){
                    filterMap.put(key,key);
                    Date date = new Date();
                    if(key.contains(quotaDateField)){
                        //quotaDate >= '2018-03-01'
                        String dateStr = key.substring(key.indexOf("'")+1,key.lastIndexOf("'"));
                        date = DateUtil.parseDate(dateStr,DateUtil.DEFAULT_DATE_YMD_FORMAT);
                    }
                    if(esConfig.getDateComparisonType().equals("lastYear")){
                        c.setTime(date);
                        c.add(Calendar.YEAR, -1);
                        Date lastYear = c.getTime();
                        filterMap.put(key,format.format(lastYear));
                    }else if(esConfig.getDateComparisonType().equals("lastMonth")) {
                        c.setTime(date);
                        c.add(Calendar.MONTH, -1);
                        Date lastYear = c.getTime();
                        filterMap.put(key,format.format(lastYear));
                    }else if(esConfig.getDateComparisonType().equals("lastDay")) {
                        c.setTime(date);
                        c.add(Calendar.DATE, -1);
                        Date lastYear = c.getTime();
                        filterMap.put(key,format.format(lastYear));
                    }
                }
            }
            filters = "";
            if(filterMap != null && filterMap.size() > 0){
                for(String key : filterMap.keySet()){
                    if(StringUtils.isEmpty(filters)){
                        filters = filterMap.get(key);
                    }else {
                        filters = " and " + filterMap.get(key);
                    }
                }
            }
        }
        return  filters;
    }

    public String handleFilter(String secondFilter, String resultFilter) {
        if (StringUtils.isNotEmpty(secondFilter)) {
            if (StringUtils.isEmpty(resultFilter)) {
                resultFilter = secondFilter;
            } else {
                resultFilter += " and " + secondFilter;
            }
        }
        return resultFilter;
    }


    /**
     * 门急诊费用
     * @return
     */
    public String getCostOfOutPatient() {
        String sum = "0";
        String sql = "select sum(result) from medical_service_index where quotaCode='HC041047'";
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            for (Map<String, Object> map : listData) {
                sum = nf.format(map.get("SUM(result)"));
            }
        }
        return sum;
    }

    /**
     * 门急诊人次
     * @return
     */
    public String getNumOfOutPatient() {
        int sum = 0;
        String sql = "select sum(result) from medical_service_index where quotaCode='HC041008'";
        List<Map<String, Object>> listData = singleDiseaseService.parseIntegerValue(sql);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            for (Map<String, Object> map : listData) {
                String value = nf.format(map.get("SUM(result)"));
                sum += Integer.parseInt(value);
            }
        }
        return sum + "";
    }

    /**
     * 入院费用
     * @return
     */
    public String getCostOfInPatient() {
        String sum = "0";
        String sql = "select sum(result) from medical_service_index where quotaCode='HC041068'";
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            for (Map<String, Object> map : listData) {
                sum = nf.format(map.get("SUM(result)"));
            }
        }
        return sum;
    }

    /**
     * 入院人次
     * @return
     */
    public String getNumOfInPatient() {
        int sum = 0;
        String sql = "select sum(result) from medical_service_index where quotaCode='HC041000'";
        List<Map<String, Object>> listData = singleDiseaseService.parseIntegerValue(sql);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            for (Map<String, Object> map : listData) {
                String value = nf.format(map.get("SUM(result)"));
                sum += Integer.parseInt(value);
            }
        }
        return sum + "";
    }

    public String getCostOfMedicalMonitor() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        // 获取门急诊费用
        Double costOfOutPatient = Double.parseDouble(getCostOfOutPatient());
        // 获取入院费用
        Double costOfInPatient = Double.parseDouble(getCostOfInPatient());
        // 医疗费用监测 = 获取门急诊费用 + 获取入院费用
        Double costOfMedicalMonitor = costOfInPatient + costOfOutPatient;
        return nf.format(costOfMedicalMonitor);
    }


    /**
     * 数据查询
     * @return
     */
    public Map<String, List<String>> getDataInfo(String sql ,String xdataName) {
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData  && listData.size() >0 && listData.get(0) !=null ) {
            listData.forEach(one -> {
                if(xdataName.contains("date_histogram")){
                    if(xdataName.contains("year")){
                        xData.add(one.get(xdataName).toString().substring(0,4) + "");
                    }else if(xdataName.contains("quarter")){
                        String quarter = "";
                        if(one.get(xdataName) != null){
                            quarter = one.get(xdataName).toString().substring(0,7);
                            if(quarter.contains("-04")){
                                quarter = quarter.replace("-04","02");
                            }else if(quarter.contains("-07")){
                                quarter = quarter.replace("-07","03");
                            }else if(quarter.contains("-10")){
                                quarter = quarter.replace("-10","04");
                            }
                        }
                        xData.add( quarter + "");
                    }else if(xdataName.contains("month")){
                        xData.add(one.get(xdataName).toString().substring(0,7) + "");
                    }
                }else {
                    xData.add(one.get(xdataName) + "");
                }
                valueData.add(one.get("count") + "");
            });
            map.put("xData", xData);
            map.put("valueData", valueData);
        }
        return map;
    }

    /**
     * 对查询结果key包含count、sum的value去掉小数点
     * @param sql
     * @return
     */
    public List<Map<String, Object>> parseIntegerValue(String sql) {
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
            List<Map<String, Object>> handleData = new ArrayList<>();
        listData.forEach(item -> {
            Map<String, Object> myMap = new HashMap<>();
            item.forEach((k,v) -> {
                if (k.contains("COUNT") || k.contains("SUM") || k.contains("count")) {
                    v = (int) Double.parseDouble(v + "");
                }
                myMap.put(k,v);
            });
            handleData.add(myMap);
        });
        return handleData;
    }

    /**
     * 百分比 增幅运算 维度不是时间维度
     * @param dimension
     * @param moleList
     * @param denoList
     * @param operation
     * @param operationValue
     * @return
     */
    public List<Map<String, Object>> divisionPercent(String dimension, List<Map<String, Object>> moleList, List<Map<String, Object>> denoList,int operation,int operationValue){
        List<Map<String, Object>> divisionResultList = new ArrayList<>();
        for(Map<String, Object> denoMap : denoList) {
            if (null != denoMap && denoMap.size() > 0 ) {
                Map<String, Object> map = new HashMap<>();
                double denoResultVal = Double.valueOf(denoMap.get(resultField) == null ? "0" : denoMap.get(resultField).toString());
                String denoKeyVal = "";
                String [] denoDimensions = dimension.split(";");
                for(int i = 0 ;i < denoDimensions.length ; i++){
                    if(i == 0){
                        denoKeyVal = denoMap.get(denoDimensions[i]).toString();
                    }else {
                        denoKeyVal = denoKeyVal + "-" + denoMap.get(denoDimensions[i]).toString() ;
                    }
                    map.put(firstColumnField, denoMap.get(firstColumnField));
                    map.put(denoDimensions[i], denoMap.get(denoDimensions[i]).toString());
                }
                if (denoResultVal == 0) {
                    map.put(resultField, "--");
                    divisionResultList.add(map);
                } else {
                    if(moleList != null && moleList.size() > 0){
                        for(Map<String, Object> moleMap :moleList) {
                            String moleKeyVal = "";
                            String [] moleDimensions = dimension.split(";");
                            for(int i = 0 ;i < moleDimensions.length ; i++){
                                if(i == 0){
                                    moleKeyVal = moleMap.get(moleDimensions[i]).toString();
                                }else {
                                    moleKeyVal = moleKeyVal + "-" + moleMap.get(moleDimensions[i]).toString() ;
                                }
                            }
                            if(denoKeyVal.equals(moleKeyVal)){
                                double point = 0;
                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setGroupingUsed(false);
                                nf.setMaximumFractionDigits(1);
                                float moleResultVal = Float.valueOf(moleMap.get(resultField).toString());
                                point = ((moleResultVal - denoResultVal)/denoResultVal) * operationValue;
                                map.put(resultField, nf.format(point));
                                divisionResultList.add(map);
                                break;
                            }
                        }
                    }else {
                        map.put(resultField, 0);
                        divisionResultList.add(map);
                    }
                }
            }
        }
        return  divisionResultList;
    }

    /**
     * 计算增幅 环比和 同比
     * @param dimension 不为空时，查询固定某个月或者某年的增幅
     * @param filters  外部过滤条件 如 quotaDate >= '2018-03-01' and quotaDate <= '2018-03-31'
     * @param esConfig 指标内部配置
     * @param dateType 不为空时，查询某个时间区间的增幅
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getGrowthByQuota(String dimension, String filters, EsConfig esConfig, String dateType) throws Exception {
        List<Map<String, Object>>  resultList = new ArrayList<>();
        String startQuotaDate = "";
        String endQuotaDate = "";
        String noDateFilter = "";
        if (StringUtils.isNotEmpty(filters) && filters.contains(quotaDateField)) {// 外部指定时间
            String params [] = filters.split("and ");
            for(int i =0 ;i< params.length ;i++){
                String quotaDateParam = params[i];
                if(quotaDateParam.contains(quotaDateField)){
                    boolean b = quotaDateParam.indexOf("'") > -1;
                    int start = b ? quotaDateParam.indexOf("'") : quotaDateParam.indexOf("\"");//查询条件不是' ,就是"
                    if(quotaDateParam.contains(">")){
                        startQuotaDate = quotaDateParam.substring(start + 1, start + 11);
                    }
                    if(quotaDateParam.contains("<")){
                        endQuotaDate = quotaDateParam.substring(start + 1, start + 11);
                    }
                }else{
                    if(StringUtils.isNotEmpty(noDateFilter)){
                        noDateFilter += " and " + quotaDateParam;
                    }else {
                        noDateFilter = quotaDateParam;
                    }
                }
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        String molecularFilter = "";
        String denominatorFilter = "";
        String growthFlag = esConfig.getGrowthFlag();
        if(StringUtils.isEmpty(dateType)){
            if ("1".equals(growthFlag)) { // 年增幅  没有传时间条件默认当前年份
                int now;
                int beforeNow;
                now = lastDate.get(Calendar.YEAR);
                if (StringUtils.isNotEmpty(endQuotaDate)) {// 外部指定时间
                    now = Integer.parseInt(endQuotaDate.substring(0,4));
                }
                beforeNow = now - 1;
                molecularFilter = "quotaDate >= '" + now + "-01-01' and quotaDate <= '" + now + "-12-31'";
                denominatorFilter = "quotaDate >= '" + beforeNow + "-01-01' and quotaDate <= '" + beforeNow + "-12-31'";
            } else if ("2".equals(growthFlag)) { // 月增幅  没有传时间条件默认当前月份
                // 如果有时间过滤条件，则按时间条件计算
                if (StringUtils.isNotEmpty(endQuotaDate)) {
                    lastDate.setTime(sdf.parse(endQuotaDate));
                }
                lastDate.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
                String firstDay = sdf.format(lastDate.getTime());
                lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                String lastDay = sdf.format(lastDate.getTime());
                lastDate.add(Calendar.MONTH, -1);
                lastDate.set(Calendar.DAY_OF_MONTH, 1);
                String preMonthFirstDay = sdf.format(lastDate.getTime());
                lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                String preMonthLastDay = sdf.format(lastDate.getTime());
                log.info("firstDay = {}, lastDay = {}", firstDay, lastDay);
                log.info("preMonthFirstDay = {}, preMonthLastDay = {}", preMonthFirstDay, preMonthLastDay);
                molecularFilter = "quotaDate >= '" + firstDay + "' and quotaDate <= '" + lastDay + "'";
                denominatorFilter = "quotaDate >= '" + preMonthFirstDay + "' and quotaDate <= '" + preMonthLastDay + "'";
            }
            if (StringUtils.isNotEmpty(esConfig.getMolecularFilter())) {
                molecularFilter += " and " + esConfig.getMolecularFilter();
            }
            if (StringUtils.isNotEmpty(esConfig.getDenominatorFilter())) {
                denominatorFilter += " and " + esConfig.getDenominatorFilter();
            }
            if(StringUtils.isNotEmpty(noDateFilter)){
                molecularFilter += " and " + noDateFilter;
                denominatorFilter += " and " + noDateFilter;
            }
            List<Map<String, Object>> moleList = getSimpleQuotaReport(esConfig.getMolecular(), molecularFilter,dimension ,false , null);
            List<Map<String, Object>> denoList =  getSimpleQuotaReport(esConfig.getDenominator(),denominatorFilter,dimension,false, null);
            resultList = divisionPercent(dimension, moleList, denoList, 1, 100);
            return resultList;

        }else {
            String dateFilter = "";
            //按时间维度 增幅，即时间区间增幅
            int nowYear;
            int beforeYear;
            Date firstMonth = new Date();
            Date endMonth = new Date();
            nowYear = lastDate.get(Calendar.YEAR);
            beforeYear = nowYear - 4;
            if ("1".equals(growthFlag)) { // 年增幅  没有传时间条件默认当前年份 向前推3年
                if (StringUtils.isNotEmpty(startQuotaDate)) {// 外部指定时间
                    beforeYear = Integer.parseInt(startQuotaDate.substring(0,4))-1;
                }
                if (StringUtils.isNotEmpty(endQuotaDate)) {// 外部指定时间
                    nowYear = Integer.parseInt(endQuotaDate.substring(0,4));
                }
                dateFilter = "quotaDate >= '" + beforeYear + "-01-01' and quotaDate <= '" + nowYear + "-12-31'";
            } else if ("2".equals(growthFlag)) { // 月增幅  没有传时间条件默认当前月份 向前推6个月
                lastDate.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
                endMonth =lastDate.getTime();
                lastDate.add(Calendar.MONTH, - 7);
                lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                firstMonth = lastDate.getTime();
                // 如果有时间过滤条件，则按时间条件计算
                if (StringUtils.isNotEmpty(startQuotaDate)) {
                    if (StringUtils.isNotEmpty(startQuotaDate)) {// 外部指定时间
                        firstMonth = sdf.parse(startQuotaDate);
                        lastDate.setTime(firstMonth);
                        lastDate.add(Calendar.MONTH, -1);//向前推一个月 用于计算最后一个月增幅
                        firstMonth = lastDate.getTime();
                    }
                    if (StringUtils.isNotEmpty(endQuotaDate)) {// 外部指定时间
                        endMonth = sdf.parse(endQuotaDate);
                    }
                }
                log.info("firstDay = {}, preMonthLastDay = {}", sdf.format(firstMonth), sdf.format(endMonth) );
                dateFilter = "quotaDate >= '" + sdf.format(firstMonth) + "' and quotaDate <= '" + sdf.format(endMonth) + "'";
            }
            if(StringUtils.isNotEmpty(noDateFilter)){
                filters = dateFilter + " and " + noDateFilter;
            }else{
                filters = dateFilter;
            }
            dimension = dateType;
            List<Map<String, Object>> dataList = getSimpleQuotaReport(esConfig.getMolecular(), filters,dimension ,false , null);
            DecimalFormat df = new DecimalFormat(".0");
            if(dataList != null && dataList.size() > 0){
                if(dateType.toLowerCase().equals("year")){
                    Map<String,Object> map = new HashMap<>();
                    double current = 0;
                    double last = 0;
                    for(int i = nowYear ; i > beforeYear ;i--){
                        for(Map<String,Object> dataMap : dataList){
                            if(dataMap.get(String.valueOf(i)) != null ){
                                map.put(firstColumnField, dataMap.get(firstColumnField));
                                map.put(dimension, dataMap.get(dimension).toString());
                                current = Double.valueOf(dataMap.get(resultField).toString());
                            }
                            if(dataMap.get(String.valueOf(i-1)) != null ){
                                last = Double.valueOf(dataMap.get(resultField).toString());
                            }
                        }
                        if(last == 0){
                            map.put(resultField,"--");
                        }else {
                            double precent = (current - last)/last;
                            map.put(resultField,df.format(precent));
                        }
                        resultList.add(map);
                    }
                }
                //quarter
                if(dateType.toLowerCase().equals("month")){

                    double current = 0;
                    double last = 0;
                    String starthMonthStr = sdf.format(firstMonth).substring(0,7);
                    String endMonthStr = sdf.format(endMonth).substring(0,7);
                    while ( !starthMonthStr.equals(endMonthStr)){
                        Map<String,Object> map = new HashMap<>();
                        String nowMonthStr = endMonthStr;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(sdf.parse(endMonthStr + "-01"));
                        calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间
                        String lastMontStr = sdf.format(calendar.getTime()).substring(0,7);
                        for(Map<String,Object> dataMap : dataList){
                            String val = dataMap.get(dimension).toString();
                            if(val.equals(nowMonthStr) ){
                                map.put(firstColumnField, val);
                                map.put(dimension, val);
                                current = Double.valueOf(dataMap.get(resultField).toString());
                            }
                            if(val.equals(lastMontStr) ){
                                last = Double.valueOf(dataMap.get(resultField).toString());
                            }
                        }
                        if(last == 0){
                            map.put(resultField,"--");
                        }else {
                            double precent = (current - last)/last*100;
                            if(precent == 0){
                                map.put(resultField,0);
                            }else {
                                map.put(resultField,df.format(precent));
                            }
                        }
                        resultList.add(map);

                        endMonthStr = lastMontStr;
                    }
                }
            }
            return resultList;
        }
    }

    public String filtersExchangeHandle(String filters, EsConfig esConfig) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
        String firstDay = sdf.format(lastDate.getTime());
        lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = sdf.format(lastDate.getTime());
        lastDate.add(Calendar.MONTH, -1);
        lastDate.set(Calendar.DAY_OF_MONTH, 1);
        String preMonthFirstDay = sdf.format(lastDate.getTime());
        lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        String preMonthLastDay = sdf.format(lastDate.getTime());
        // 如果有时间过滤条件，则按时间条件计算
        if (StringUtils.isNotEmpty(filters) && filters.contains(quotaDateField)) {
            Calendar cal = Calendar.getInstance();
            boolean b = filters.indexOf("'") > -1;
            int start = b ? filters.indexOf("'") : filters.indexOf("\"");
            String condition = filters.substring(start + 1, start + 5); // 获取年份
            int year = Integer.parseInt(condition);
            String condition2 = filters.substring(filters.indexOf("--") + 1, filters.indexOf("-") + 3);  // 获取月份
            int month = Integer.parseInt(condition2);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, 1); // 设置为1号,当前日期既为本月第一天
            firstDay = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            lastDay = sdf.format(cal.getTime());
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            preMonthFirstDay = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            preMonthLastDay = sdf.format(cal.getTime());
        }
        if ("1".equals(esConfig.getIncrementFlag())) {
            // 上月
            filters = "quotaDate >= '" + preMonthFirstDay + "' and quotaDate <= '" + preMonthLastDay + "'";
        } else if ("2".equals(esConfig.getIncrementFlag())) {
            // 当前月
            filters = "quotaDate >= '" + firstDay + "' and quotaDate <= '" + lastDay + "'";
        }
        if (StringUtils.isNotEmpty(esConfig.getFilter())) {
            filters += " and " + esConfig.getFilter();
        }
        return filters;
    }
}