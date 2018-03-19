package com.yihu.quota.service.quota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.singledisease.SingleDiseaseService;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private SingleDiseaseService singleDiseaseService;

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
        dimension = StringUtils.isNotEmpty(dateType)? (StringUtils.isNotEmpty(dimension)? dimension +";"+dateType : dateType):dimension;
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
            if (null != moleMap && moleMap.size() > 0 ) {
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
                    map.put("firstColumn", moleMap.get("firstColumn"));
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
                            DecimalFormat df = new DecimalFormat("0.0");
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
    public List<Map<String, Object>>  getOrgHealthCategory(String code,String filters,String dateType,boolean isTrunTree) throws Exception {

        List<Map<String, Object>> dimenListResult = new ArrayList<>();
        if(dateType != null && (dateType.contains("year") || dateType.contains("month") || dateType.contains("day"))){
            dimenListResult = getTimeAggregationResult(code,orgHealthCategoryCode,filters,dateType);//dimension 维度为 year,month,day
        }else {
            dimenListResult = getAggregationResult(code, orgHealthCategoryCode, filters);
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
            mapCategory.put("firstColumn",mapCategory.get("text"));
            for(Map<String, Object> dimenMap : dimenListResult){
                if(dimenMap.get(code) != null){
//                    mapCategory.putAll(dimenMap);
                    if(dimenMap.containsKey(code)){
                        mapCategory.put(code,dimenMap.get(code));
                        mapCategory.put("result",dimenMap.get("result")!=null ? dimenMap.get("result"):dimenMap.get(code));
                    }
                    if(StringUtils.isNotEmpty(dateType)){
                        mapCategory.put(dimenMap.get(dateType).toString(),dimenMap.get("result"));
                    }
                    mapCategory.put(quotaCode,dimenMap.get("result"));
                    break;
                }else {
                    mapCategory.put("result",0);
                    mapCategory.put(quotaCode,0);
                }
            }

            result.add(mapCategory);
            if(mapCategory.get("children") != null){
                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                mapCategory.put("children",setResult(quotaCode,childrenOrgHealthCategoryList,dimenListResult,dateType));
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
                        dataMap.put("firstColumn", dictVal);
                    }else {
                        if(key.equals("quotaDate")){
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
                    dataMap.put("firstColumn",map.get("text"));
                }
                if(key.equals("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    dataMap.put("result",  nf.format(map.get(key)));
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
        List<Map<String, Object>>  dimenListResult = esResultExtract.searcherSumGroup(tjQuota, groupDimension, filter, "result", "", "");
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                dataMap.putAll(map);
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                }
                if(key.equals("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    dataMap.put("result",  nf.format(map.get(key)));
                }
                if(key.equals("quotaDate")){
                    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
                    Long time = new Long(Long.valueOf(map.get(key).toString()));
                    String quotaDate = format.format(time);
                    dataMap.put("quotaDate", quotaDate);
                }
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
        Map<String,String>  dimensionDicMap = getDimensionDicMap(code,dimension);
        List<String> dimenList = getDimenList(dimension);
        String groupDimension = joinDimen(dimension);
        List<Map<String, Object>>  dimenListResult = esResultExtract.searcherSumGroup(tjQuota, groupDimension, filter, "result", "", "");

        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                if(dimenList.contains(key)){
                    if(dimensionDicMap.get(map.get(key).toString().toLowerCase())  != null){
                        dataMap.put(key,dimensionDicMap.get(map.get(key).toString().toLowerCase()));
                        dataMap.put(key+"Name",dimensionDicMap.get(map.get(key).toString().toLowerCase()));
                        dataMap.put("firstColumn",dimensionDicMap.get(map.get(key).toString().toLowerCase()));
                    }else {
                        dataMap.put(key,map.get(key));
                    }
                }
                //维度为特殊机构类型时
                if(key.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                    dataMap.put("firstColumn",map.get("text"));
                }
                if(key.equals("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    dataMap.put("result",  nf.format(map.get(key)));
                }
            }
            resultList.add(dataMap);
        }
        return noDataDimenDictionary(resultList,dimension,filter);
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
                        result = map.get("result").toString();
                        break;
                    }
                }
                oneMap.put("firstColumn",dictMap.get(code));
                oneMap.put(dimen+"Name",dictMap.get(code));
                oneMap.put(dimen,dictMap.get(code));
                oneMap.put("result",result);
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
                ||dimension.contains("org") ||dimension.contains("year") ||dimension.contains("month") ||dimension.contains("day") || dimension.contains("quotaDate") ;
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
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>>  getSimpleQuotaReport(String code,String filters,String dimension,boolean isTrunTree) throws Exception {
        String dateType = "";
        //指标的展示维度，由视图中决定
        if(dimension.trim().equals("year")){
            dateType = "year";
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
            if(StringUtils.isNotEmpty(filters)){
                filters += " and " + configFilter;
            }else {
                filters = configFilter;
            }
        }
        String molecularFilter = filters;
        String denominatorFilter = filters;
        if( (StringUtils.isNotEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals(orgHealthCategory)){
            //特殊机构类型查询输出结果  只有查询条件没有维度 默认是 机构类型维度
             result = getOrgHealthCategory(code,filters,dateType,isTrunTree);
        }else if( (StringUtils.isNotEmpty(esConfig.getMolecular())) && StringUtils.isNotEmpty(esConfig.getDenominator())){//除法
            //除法指标查询输出结果
            molecularFilter = handleFilter(esConfig.getMolecularFilter(), molecularFilter);
            denominatorFilter = handleFilter(esConfig.getDenominatorFilter(), denominatorFilter);
            result =  divisionQuota(esConfig.getMolecular(), esConfig.getDenominator(), dimension, molecularFilter, denominatorFilter, esConfig.getPercentOperation(), esConfig.getPercentOperationValue(),dateType);
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

    /**
     * 获取solr主表HealthProfile的总记录数
     * @return
     * @throws Exception
     */
    public long getArchiveCount() throws Exception {
        long count = solrUtil.count(ResourceCore.MasterTable, "", "");
        return count;
    }

    /**
     * 获取ES中health_archive_index索引的总记录数
     * @return
     */
    public long getArchiveManCount() {
        String sql = "SELECT count(*) FROM health_archive_index";
        long count = elasticsearchUtil.getCountBySql(sql);
        return count;
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
        String sql = "select sum(result) from medical_service_index where quotaCode='HC041068";
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
        // 获取门急诊费用
        Double costOfOutPatient = Double.parseDouble(getCostOfOutPatient());
        // 获取入院费用
        Double costOfInPatient = Double.parseDouble(getCostOfInPatient());
        // 医疗费用监测 = 获取门急诊费用 + 获取入院费用
        Double costOfMedicalMonitor = costOfInPatient + costOfOutPatient;
        return costOfMedicalMonitor + "";
    }
}
