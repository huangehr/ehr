package com.yihu.quota.service.quota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.SaveModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    ObjectMapper objectMapper;


    public static String orgHealthCategoryCode = "orgHealthCategoryCode";


    /**
     * 指标除法运算
     * @param molecular
     * @param denominator
     * @param dimension
     * @param filters
     * @param operation
     * @param operationValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> divisionQuota(String molecular, String denominator, String dimension, String filters,String operation,String operationValue) throws Exception {
        Map<String, Integer> moleMap = getQuotaResultList(molecular,filters,dimension);
        Map<String, Integer> denoMap =  getQuotaResultList(denominator,filters,dimension);
        int type= 2;
       return division(moleMap,  denoMap,Integer.valueOf(operation),Integer.valueOf(operationValue),type);
    }

    /**
     * 根据指标code 和维度及条件 分组获取指标查询结果集
     *
     * @param code
     * @param filters
     * @param dimension
     * @return
     * 返回结果实例： groupDataMap -> "4205000000-儿-1": 200 =>group by 三个字段
     * @throws Exception
     */
    public Map<String, Integer> getQuotaResultList(String code,String dimension,String filters) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        Map<String, Integer> map = esResultExtract.searcherSumByGroupBySql(tjQuota, dimension, filters, "result", "", "");
        return map;
    }

    /**
     * 指标结果相除
     * @param moleMap 分子
     * @param dimeMap 分母
     * @param operation 运算方式 1 乘法 2 除法
     * @param operationValue 运算参数值
     * @param type 1 维度除以 一个数值   2 维度除以对应维度数据
     *
     */
    public Map<String, Object> division(Map<String, Integer> moleMap,Map<String, Integer> dimeMap,int operation,int operationValue,int type){
        Map<String, Object> divisionResultMap = new HashMap<>();
        for(String moleKey :moleMap.keySet()) {
            int point = 0;
            double moleVal = Double.valueOf(moleMap.get(moleKey));
            if (moleVal == 0) {
                divisionResultMap.put(moleKey,0);
            } else {
                float dimeVal = 0;
                if(type==1){
                    dimeVal = Float.valueOf(dimeMap.get("result").toString());
                }else {
                    dimeVal = Float.valueOf(dimeMap.get("moleKey").toString());
                }
                if(operation == 1){
                    point = (int)(moleVal/dimeVal) * operationValue;
                }else if(operation == 2){
                    point = (int)(moleVal/dimeVal) / operationValue;
                }
                divisionResultMap.put(moleKey,point);
            }
        }
        return  divisionResultMap;
    }

    /**
     * 特殊机构类别 根据条件查询结果
     * @param code
     * @param filters
     * @throws Exception
     */
    public List<Map<String, Object>>  getOrgHealthCategory(String code,String dimension,String filters) throws Exception {

        List<Map<String, Object>> dimenListResult = new ArrayList<>();
        if(dimension != null && (dimension.contains("year") || dimension.contains("month") || dimension.contains("day"))){
            dimenListResult = getTimeAggregationResult(code,orgHealthCategoryCode,filters,dimension);//dimension 维度为 year,month,day
        }else {
            TjQuota tjQuota= quotaDao.findByCode(code);
            dimenListResult = esResultExtract.searcherByGroup(tjQuota, filters, orgHealthCategoryCode);
        }
        List<Map<String, Object>> orgHealthCategoryList = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
        List<Map<String, Object>> resultList = setResult(orgHealthCategoryList,dimenListResult);
        return resultList;
    }

    /**
     * 递归循环 查询机构类型对应的名称和父节点
     * @param orgHealthCategoryList
     * @param dimenListResult
     * @param
     * @return
     */
    public List<Map<String,Object>> setResult(List<Map<String,Object>> orgHealthCategoryList,List<Map<String, Object>> dimenListResult){
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i=0 ; i < orgHealthCategoryList.size() ; i++ ){
            Map<String,Object> mapCategory = orgHealthCategoryList.get(i);
            String code = mapCategory.get("code").toString();
            for(Map<String, Object> dimenMap : dimenListResult){
                if(dimenMap.get(code) != null){
                    mapCategory.put("result", dimenMap.get(code) != null ? dimenMap.get(code).toString() : "0");
                    for(String key : dimenMap.keySet()){
                        mapCategory.put(key,dimenMap.get(key));
                    }
                    break;
                }else {
                    mapCategory.put("result",0);
                }
            }
            result.add(mapCategory);
            if(mapCategory.get("children") != null){
                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                mapCategory.put("children",setResult(childrenOrgHealthCategoryList,dimenListResult));
            }
        }
        return  result;
    }


    /**
     * 时间聚合查询指标结果
     *
     * //目前先实现一个维度，多个维度后续扩展
     * @param code
     * @param dimension
     * @param filter
     * @throws Exception
     */
    public  List<Map<String, Object>> getTimeAggregationResult(String code,String dimension, String filter,String dateDime) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        List<Map<String, Object>> dimenListResult = esResultExtract.searcherSumByGroupByTime(tjQuota, dimension, filter, dateDime);

        List<Map<String, Object>> resultList = new ArrayList<>();
        String dateHist = "date_histogram(field=quotaDate,interval="+ dateDime +")";
        Map<String,String>  dimensionDicMap = new HashMap<>();
        if(dimension != orgHealthCategoryCode){
            String dictSql = getQuotaDimensionDictSql(tjQuota.getCode(), dimension);
            dimensionDicMap = getDimensionMap(dictSql, dimension);
        }
        for(Map<String, Object> map : dimenListResult){
            Map<String,Object> dataMap = new HashMap<>();
            for(String key :map.keySet()){
                if(key.equals(dimension) && !dimension.equals(orgHealthCategoryCode)){
                    dataMap.put(dimensionDicMap.get(map.get(key).toString()),dimensionDicMap.get(map.get(key).toString()));
                }
                if(dimension.equals(orgHealthCategoryCode)){
                    dataMap.put(map.get(orgHealthCategoryCode).toString(),map.get(orgHealthCategoryCode));
                }
                if(key.equals(dateHist)) {
                    if (dateDime.equals("year")) {
                        String value = map.get(key).toString().substring(0, 4);
                        dataMap.put(value, value);
                    } else if (dateDime.contains("month")) {
                        String value = map.get(key).toString().substring(0, 7);
                        dataMap.put(value, value);
                    } else if (dateDime.contains("week")) {
                        String value = map.get(key).toString().substring(0, 7);
                        dataMap.put(value, value);
                    } else if (dateDime.contains("day")) {
                        String value = map.get(key).toString().substring(0, 10);
                        dataMap.put(value, value);
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
     * 获取维度的字典 sql
     * @param quotaCode
     * @param dimension
     * @return
     */
    private String getQuotaDimensionDictSql(String quotaCode, String dimension) {
        String dictSql = "";
        //查询维度
        List<TjQuotaDimensionMain>  dimensionMains = tjDimensionMainService.findTjQuotaDimensionMainByQuotaCode(quotaCode);
        if(dimensionMains != null && dimensionMains.size() > 0){
            for(TjQuotaDimensionMain main:dimensionMains){
                if(main.getMainCode().equals(dimension)){
                    dictSql = main.getDictSql();
                }
            }
        }
        if(StringUtils.isEmpty(dictSql)) {
            List<TjQuotaDimensionSlave> dimensionSlaves = tjDimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(quotaCode);
            if (dimensionSlaves != null && dimensionSlaves.size() > 0) {
                int slave = Integer.valueOf(dimension.substring(dimension.length()-1,dimension.length()));
                if(dimensionSlaves.size() >= slave){
                    dictSql = dimensionSlaves.get(slave-1).getDictSql();
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
    private Map<String,String> getDimensionMap(String dictSql, String dimension) {
        Map<String,String> dimensionDicMap = new HashMap<>();
        if(StringUtils.isNotEmpty(dictSql)) {
            BasesicUtil baseUtil = new BasesicUtil();
            if(dimension.contains("slaveKey")){
                //查询字典数据
                List<DictModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(DictModel.class));
                for (DictModel dictModel : dictDatas) {
                    String name = baseUtil.getFieldValueByName("name", dictModel);
                    String val = baseUtil.getFieldValueByName("code", dictModel).toLowerCase();
                    dimensionDicMap.put(val,name);
                }
            } else{
                List<SaveModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(SaveModel.class));
                if(dictDatas != null ) {
                    for (SaveModel saveModel : dictDatas) {
                        String name = baseUtil.getFieldValueByName(dimension + "Name", saveModel);
                        String val = baseUtil.getFieldValueByName(dimension,saveModel).toLowerCase();
                        dimensionDicMap.put(val,name);
                    }
                }
            }
        }
        return dimensionDicMap;
    }

}
