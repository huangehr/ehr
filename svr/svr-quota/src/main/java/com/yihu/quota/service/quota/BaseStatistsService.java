package com.yihu.quota.service.quota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.SaveModel;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by janseny on 2018/01/15.
 */
@Service
public class BaseStatistsService {

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
    ObjectMapper objectMapper;
    private static String orgHealthCategory = "orgHealthCategory";
    public static String orgHealthCategoryCode = "orgHealthCategoryCode";

    /**
     * 根据指标code 和维度及条件 分组获取指标查询结果集
     * @param code
     * @param filter
     * @param dimension
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getQuotaResultList(String code,String dimension,String filter,String dateType) throws Exception {
        List<Map<String, Object>> dimenListResult = new ArrayList<>();
        if(StringUtils.isNotEmpty(dateType)){
            dimenListResult = getTimeAggregationResult(code, dimension, filter, dateType);
        }else {
            dimenListResult = getAggregationResult(code, dimension, filter);
        }
        return dimenListResult;
    }


    /**
     *  特殊机构类型   根据 上级基础指标code 获取基础数据集
     * @param code
     * @param filter
     * @param dimension
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrgHealthCategoryQuotaResultList(String code,String dimension,String filter) throws Exception {
        List<Map<String, Object>> dimenListResult = getOrgHealthCategoryAggregationResult(code, dimension, filter);
        return dimenListResult;
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
                                                    String molecularFilter,String denominatorFilters,String operation,String operationValue,String dateType) throws Exception {
        List<Map<String, Object>> moleList = getQuotaResultList(molecular,dimension,molecularFilter,dateType);
        List<Map<String, Object>> denoList =  getQuotaResultList(denominator,dimension,denominatorFilters,dateType);
        dimension = StringUtils.isNotEmpty(dateType)?dimension+";"+dateType:dimension;
       return division(dimension,moleList,denoList,Integer.valueOf(operation),Integer.valueOf(operationValue));
    }

    /**
     * 指标除法运算 分母为常量
     * @param molecular
     * @param denominatorVal
     * @param dimension
     * @param filters
     * @param operation
     * @param operationValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>>  divisionQuotaDenoConstant(String molecular, String dimension,String filters,
                                                                String operation,String operationValue,String dateType,Double denominatorVal) throws Exception {
        List<Map<String, Object>> moleList = getQuotaResultList(molecular,dimension,filters,dateType);
        return divisionDenoConstant(dimension, moleList, denominatorVal, Integer.valueOf(operation), Integer.valueOf(operationValue));
    }

    /**
     * 指标结果相除
     * 除法运算 分母为常量
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
            double moleResultVal = Double.valueOf(moleMap.get("result").toString());
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
                map.put("result",0);
                divisionResultList.add(map);
            } else {
                int point = 0;
                if (operation == 1) {
                    point = (int) (moleResultVal / denominatorVal) * operationValue;
                } else if (operation == 2) {
                    point = (int) (moleResultVal / denominatorVal) / operationValue;
                }
                map.put("result", point);
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
            Map<String, Object> map = new HashMap<>();
            double moleResultVal = Double.valueOf(moleMap.get("result") == null ? "0" : moleMap.get("result").toString());
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
                map.put("result",0);
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
                        DecimalFormat df = new DecimalFormat("#.0");
                        float dimeResultVal = Float.valueOf(denoMap.get("result").toString());
                        if(dimeResultVal != 0){
                            if(operation == 1){
                                point = (moleResultVal/dimeResultVal) * operationValue;
                            }else if(operation == 2){
                                point = (moleResultVal/dimeResultVal) / operationValue;
                            }
                        }
                        map.put("result",df.format(point));
                        divisionResultList.add(map);
                        break;
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
     * @throws Exception
     */
    public List<Map<String, Object>>  getOrgHealthCategory(String code,String filters,String dateType) throws Exception {

        List<Map<String, Object>> dimenListResult = new ArrayList<>();
        if(dateType != null && (dateType.contains("year") || dateType.contains("month") || dateType.contains("day"))){
            dimenListResult = getTimeAggregationResult(code,orgHealthCategoryCode,filters,dateType);//dimension 维度为 year,month,day
        }else {
            TjQuota tjQuota= quotaDao.findByCode(code);
            dimenListResult = esResultExtract.searcherByGroup(tjQuota, filters, orgHealthCategoryCode);
        }
        List<Map<String, Object>> orgHealthCategoryList = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
        List<Map<String, Object>> resultList = setResult(orgHealthCategoryList,dimenListResult,dateType);
        return resultList;
    }

    /**
     * 递归循环 查询机构类型对应的名称和父节点
     * @param orgHealthCategoryList
     * @param dimenListResult
     * @param
     * @return
     */
    public List<Map<String,Object>> setResult(List<Map<String,Object>> orgHealthCategoryList,List<Map<String, Object>> dimenListResult,String dateType){
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i=0 ; i < orgHealthCategoryList.size() ; i++ ){
            Map<String,Object> mapCategory = orgHealthCategoryList.get(i);
            String code = mapCategory.get("code").toString();
            for(Map<String, Object> dimenMap : dimenListResult){
                if(dimenMap.get(code) != null){
                    mapCategory.putAll(dimenMap);
                    if(StringUtils.isNotEmpty(dateType)){
                        mapCategory.put(dimenMap.get(dateType).toString(),dimenMap.get("result"));
                    }
                    break;
                }
            }
            result.add(mapCategory);
            if(mapCategory.get("children") != null){
                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                mapCategory.put("children",setResult(childrenOrgHealthCategoryList,dimenListResult,dateType));
            }
        }
        return  result;
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
        Map<String,String>  dimensionDicMap = new HashMap<>();
        List<String> dimenList = new ArrayList<>();
        String groupDimension = "";
        if(dimension.contains(";")){
            String[] dimens =  dimension.split(";");
            for(int i =0 ;i<dimens.length ;i++){
                dimenList.add(dimens[i]);
                String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimens[i]);
                if(StringUtils.isNotEmpty(dictSql)){
                    Map<String,String> dicMap = getDimensionMap(dictSql, dimens[i]);
                    if(dicMap != null && dicMap.size() > 0){
                        for(String key :dicMap.keySet()){
                            dimensionDicMap.put(key.toLowerCase(),dicMap.get(key));
                        }
                    }
                }
                groupDimension += dimens[i] + ",";
            }
            groupDimension = groupDimension.substring(0,groupDimension.length()-1);
        }else {
            String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimension);
            if(StringUtils.isNotEmpty(dictSql)){
                Map<String,String> dicMap = getDimensionMap(dictSql, dimension);
                if(dicMap != null && dicMap.size() > 0){
                    for(String key :dicMap.keySet()){
                        dimensionDicMap.put(key.toLowerCase(),dicMap.get(key));
                    }
                }
            }
            groupDimension = dimension;
            dimenList.add(dimension);
        }
        List<Map<String, Object>> dimenListResult = esResultExtract.searcherSumByGroupByTime(tjQuota, groupDimension, filter, dateDime);

        List<Map<String, Object>> resultList = new ArrayList<>();
        String dateHist = "date_histogram(field=quotaDate,interval="+ dateDime +")";

        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                if(dimenList.contains(key)){
                    if(dimensionDicMap.get(map.get(key))  != null){
                        String dictVal = dimensionDicMap.get(map.get(key).toString().toLowerCase());
                        dataMap.put(key,dictVal);
                    }else {
                        dataMap.put(key,map.get(key));
                    }
                }
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                }
                if(key.equals(dateHist)) {
                    if (dateDime.equals("year")) {
                        String value = map.get(key).toString().substring(0, 4);
                        dataMap.put(dateDime, value);
                    } else if (dateDime.contains("month")) {
                        String value = map.get(key).toString().substring(0, 7);
                        dataMap.put(dateDime, value);
                    } else if (dateDime.contains("week")) {
                        String value = map.get(key).toString().substring(0, 7);
                        dataMap.put(dateDime, value);
                    } else if (dateDime.contains("day")) {
                        String value = map.get(key).toString().substring(0, 10);
                        dataMap.put(dateDime, value);
                    }
                }
                if(key.equals("SUM(result)")){
                    dataMap.put("result", map.get(key).toString());
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
    public  List<Map<String, Object>> getOrgHealthCategoryAggregationResult(String code,String dimension, String filter) throws Exception {
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
        groupDimension += ",org,quotaDate ";
        List<Map<String, Object>>  dimenListResult = esResultExtract.searcherSumGroup(tjQuota, groupDimension, filter, "result", "", "");
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                }
                if(key.equals("SUM(result)")){
                    dataMap.put("result", map.get(key).toString());
                }
                if(key.equals("quotaDate")){
                    dataMap.put("quotaDate", map.get(key).toString().substring(0,10));
                }
                dataMap.putAll(map);
            }
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
    public  List<Map<String, Object>> getAggregationResult(String code,String dimension, String filter) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        Map<String,String>  dimensionDicMap = new HashMap<>();
        List<String> dimenList = new ArrayList<>();
        String groupDimension = "";
        if(dimension.contains(";")){
            String[] dimens =  dimension.split(";");
            for(int i =0 ;i<dimens.length ;i++){
                dimenList.add(dimens[i]);
                String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimens[i]);
                if(StringUtils.isNotEmpty(dictSql)){
                    Map<String,String> dicMap = getDimensionMap(dictSql, dimens[i]);
                    if(dicMap != null && dicMap.size() > 0){
                        dimensionDicMap.putAll(dicMap);
                    }
                }
                groupDimension += dimens[i] + ",";
            }
            groupDimension = groupDimension.substring(0,groupDimension.length()-1);
        }else {
            String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimension);
            if(StringUtils.isNotEmpty(dictSql)){
                Map<String,String> dicMap = getDimensionMap(dictSql, dimension);
                if(dicMap != null && dicMap.size() > 0){
                    dimensionDicMap.putAll(dicMap);
                }
            }
            groupDimension = dimension;
            dimenList.add(dimension);
        }

        List<Map<String, Object>>  dimenListResult = esResultExtract.searcherSumGroup(tjQuota, groupDimension, filter, "result", "", "");

        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                if(dimenList.contains(key)){
                    if(dimensionDicMap.get(map.get(key))  != null){
                        String dictVal = dimensionDicMap.get(map.get(key).toString());
                        dataMap.put(key,dictVal);
                    }else {
                        dataMap.put(key,map.get(key));
                    }
                }
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                }
                if(key.equals("SUM(result)")){
                    dataMap.put("result", map.get(key).toString());
                }
            }
            resultList.add(dataMap);
        }
        return resultList;
    }



    /**
     * 获取维度的字典 sql
     * @param quotaCode
     * @param dimension
     * @return
     */
    private String getQuotaDimensionDictSql(String quotaCode, String dimension) {
        boolean mainFlag = dimension.contains("province") || dimension.contains("city") ||dimension.contains("town") ||dimension.contains("org") ||dimension.contains("year") ||dimension.contains("month") ;
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
                    for(TjQuotaDimensionSlave slave:dimensionSlaves){
                        if(slave.getKeyVal().equals(dimension)){
                            dictSql = slave.getDictSql();
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
            boolean main = dimension.contains("province") || dimension.contains("city") ||dimension.contains("town") ||dimension.contains("org") ||dimension.contains("year") ||dimension.contains("month") ;
            if( main){
                //主纬度字典项
                List<SaveModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(SaveModel.class));
                if(dictDatas != null ) {
                    for (SaveModel saveModel : dictDatas) {
                        String name = baseUtil.getFieldValueByName(dimension + "Name", saveModel);
                        String val = baseUtil.getFieldValueByName(dimension,saveModel).toLowerCase();
                        dimensionDicMap.put(val,name);
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
     * @param filters
     * @param dimension
     * @param dateType
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>>  getSimpleQuotaReport(String code,String filters,String dimension,String dateType) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(code);
        JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
        EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
        String configFilter = esConfig.getFilter();
        if(StringUtils.isNotEmpty(configFilter)){
            if(StringUtils.isNotEmpty(filters)){
                filters += "and " + configFilter;
            }else {
                filters = configFilter;
            }
        }
        String molecularFilter = filters;
        String denominatorFilter = filters;
        if( (StringUtils.isNotEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals(orgHealthCategory)){
            //特殊机构类型查询输出结果  只有查询条件没有维度 默认是 机构类型维度
             result = getOrgHealthCategory(code,filters,dateType);
        }else if( (StringUtils.isNotEmpty(esConfig.getMolecular())) && StringUtils.isNotEmpty(esConfig.getDenominator())){//除法
            //除法指标查询输出结果
            molecularFilter = handleFilter(esConfig.getMolecularFilter(), molecularFilter);
            denominatorFilter = handleFilter(esConfig.getDenominatorFilter(), denominatorFilter);
            result =  divisionQuota(esConfig.getMolecular(), esConfig.getDenominator(), dimension, molecularFilter, denominatorFilter, esConfig.getPercentOperation(), esConfig.getPercentOperationValue(),dateType);
        }else if( (StringUtils.isNotEmpty(esConfig.getThousandDmolecular())) && StringUtils.isNotEmpty(esConfig.getThousandDenominator())){//除法
            //除法指标查询输出结果
           result =  divisionQuota(esConfig.getThousandDmolecular(), esConfig.getThousandDenominator(), dimension, molecularFilter, denominatorFilter, "1", esConfig.getThousandFlag(),dateType);
        }else if(StringUtils.isNotEmpty(esConfig.getSuperiorBaseQuotaCode())) {
            //二次统计 指标查询
            result = getQuotaResultList(esConfig.getSuperiorBaseQuotaCode(), dimension,filters,dateType);
        }else {
            //普通基础指标查询
            result = getQuotaResultList(code, dimension,filters,dateType);
        }
        return result;
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
}
