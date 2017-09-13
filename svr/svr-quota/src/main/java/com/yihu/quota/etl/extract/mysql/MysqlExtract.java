package com.yihu.quota.etl.extract.mysql;

import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by janseny on 2017/7/10.
 */
@Component
@Scope("prototype")
public class MysqlExtract {
    private Logger logger = LoggerFactory.getLogger(MysqlExtract.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    SolrUtil solrUtil;
    @Autowired
    SolrQuery solrQuery;

    private QuotaVo quotaVo;
    private String startTime;
    private String endTime;
    private String timeLevel;
    private EsConfig esConfig;


    public List<SaveModel> extract(List<TjQuotaDimensionMain> qdm,//主维度
                                   List<TjQuotaDimensionSlave> qds,//细维度
                                   String startTime,//开始时间
                                   String endTime, //结束时间
                                   String timeLevel, //时间维度  1日,2 周, 3 月,4 年
                                   QuotaVo quotaVo,//指标code
                                   EsConfig esConfig //es配置
    ) throws Exception {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeLevel = timeLevel;
        this.quotaVo = quotaVo;
        this.esConfig = esConfig;

        List<SaveModel> returnList = new ArrayList<>();
       //获取mysql
        String mysql = getSql(qdm,qds);
        logger.debug(mysql);
        if( !StringUtils.isEmpty(mysql)){
            Map<String,Long> resultMap = new HashMap<>();
            //执行MySQL
            List<Map<String, Object>> mapList = null;
            try {
                 mapList =  jdbcTemplate.queryForList(mysql);
            }catch (Exception e){
                throw new Exception("mysql查询数据出错" + e.getMessage());
            }
            if(mapList != null){
                for(Map<String, Object> map :mapList){
                    String keyVal  = "";
                    for(String key :map.keySet()){
                        if(!key.equals("result")){
                            keyVal = keyVal + map.get(key) +  "-";
                        }
                    }
                    resultMap.put(keyVal.substring(0, keyVal.length() - 1), (long) map.get("result"));
                }
                compute(qdm, qds, returnList, resultMap);
            }
        }
        return returnList;

    }



    /**
     * @param tjQuotaDimensionMains
     * @param tjQuotaDimensionSlaves
     * @return
     */
    private String getSql(List<TjQuotaDimensionMain> tjQuotaDimensionMains, List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves) {
        Map<String, TjQuotaDimensionMain> sqlS = new HashMap<>();
        StringBuffer allField = new StringBuffer("");
        String tableName = esConfig.getTable();
        for (int j = 0; j < tjQuotaDimensionMains.size(); j++) {
            TjQuotaDimensionMain one = tjQuotaDimensionMains.get(j);
            if ( !StringUtils.isEmpty(one.getKeyVal())) {
                allField.append(one.getKeyVal() + ",");
            }
        }
        for (int i = 0; i < tjQuotaDimensionSlaves.size(); i++) {
            allField.append(tjQuotaDimensionSlaves.get(i).getKeyVal()+ ",");
        }
        //拼凑where语句
        StringBuffer whereSql = new StringBuffer();
        if ( !StringUtils.isEmpty(esConfig.getTimekey())) {
            if (Contant.quota.dataLeval_oneDay.endsWith(quotaVo.getDataLevel())) {
                whereSql.append(" where " + esConfig.getTimekey() + " >= '" + startTime + "'");//startTime 默认是 昨天
                whereSql.append( " and " + esConfig.getTimekey() + " < '" + endTime + "'");//默认今天
            }else{
                whereSql.append( " where " + esConfig.getTimekey() + " < '" + endTime + "'");//默认今天
            }
        }
        StringBuffer sql = new StringBuffer();
        if(StringUtils.isEmpty(allField)|| allField.length()==0){
            sql.append("select count(*)  result  from " + tableName + whereSql);
        }else {
            String groupByField = allField.substring(0,allField.length() - 1);
            sql.append("select " + allField + "  count(*) result from " + tableName + whereSql + " group by " + groupByField);
        }
        return sql.toString();
    }


    private void compute(List<TjQuotaDimensionMain> qdm,List<TjQuotaDimensionSlave> qds,
            List<SaveModel> returnList, Map<String, Long> map) throws Exception {
        Map<String, SaveModel> allData = new HashMap<>();
       if(qdm.size() + qds.size() == 0){
           SaveModel saveModel = new SaveModel();
           Long num = map.get("result");
           saveModel = setSaveModel(saveModel);
           saveModel.setResult(num.toString());
           returnList.add(saveModel);
       }else {
           //初始化主细维度
           if(qdm!=null && qdm.size()>0){
               for(TjQuotaDimensionMain qmain:qdm){
                   allData= initDimension(qds, qmain, allData);
               }
           }else{
               allData= initDimension(qds, null, allData);
           }
           for(Map.Entry<String,SaveModel> oneMap:allData.entrySet()){
               String key = oneMap.getKey();
               SaveModel saveModel = oneMap.getValue();
               Long num = map.get(key);
               if(saveModel != null && num != null){
                   saveModel.setResult(num.toString());
               }else{
                   saveModel.setResult("0");
               }
               returnList.add(saveModel);
           }
       }
    }

    /**
     * 初始化主细维度
     */
    private  Map<String, SaveModel>  initDimension(List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves, TjQuotaDimensionMain quotaDimensionMain, Map<String, SaveModel> allData) throws Exception {
        try{
            if(quotaDimensionMain !=null){
                //查询字典数据
                List<SaveModel> dictData = jdbcTemplate.query(quotaDimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                if (dictData == null) {
                    throw new Exception("主纬度配置有误");
                }else {
                    //设置到map里面
                    setAllData(allData, dictData, quotaDimensionMain.getType());
                }
            }
            for (int i = 0; i < tjQuotaDimensionSlaves.size(); i++) {
                List<DictModel> dictDataSlave = jdbcTemplate.query(tjQuotaDimensionSlaves.get(i).getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                if (dictDataSlave == null) {
                    throw new Exception("细纬度配置有误");
                }else {
                    allData = setAllSlaveData(allData, dictDataSlave,i);
                }
            }
        }catch (Exception e){
            throw new Exception("纬度配置有误");
        }
        return allData;
    }

    private Map<String, SaveModel> setAllSlaveData(Map<String, SaveModel> allData, List<DictModel> dictData,Integer key) {
        try {
            Map<String, SaveModel> returnAllData = new HashMap<>();
            for (Map.Entry<String, SaveModel> one : allData.entrySet()) {
                for (int i = 0; i < dictData.size(); i++) {
                    DictModel dictOne = dictData.get(i);
                    //设置新key
                    StringBuffer newKey = new StringBuffer(one.getKey() + "-" + dictOne.getCode());
                    //设置新的value
                    SaveModel saveModelTemp = new SaveModel();
                    BeanUtils.copyProperties(one.getValue(), saveModelTemp);

                    StringBuffer keyMethodName = new StringBuffer("setSlaveKey" + (key + 1));
                    StringBuffer nameMethodName = new StringBuffer("setSlaveKey" + (key + 1) + "Name");

                    SaveModel.class.getMethod(keyMethodName.toString(), String.class).invoke(saveModelTemp, dictOne.getCode());
                    SaveModel.class.getMethod(nameMethodName.toString(), String.class).invoke(saveModelTemp, dictOne.getName());
                    returnAllData.put(newKey.toString(), saveModelTemp);
                }
            }
            return returnAllData;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @param allData
     * @param dictData
     * @param dictType
     */
    private void setAllData(Map<String, SaveModel> allData, List<SaveModel> dictData, String dictType) {
        switch (dictType) {
            case Contant.main_dimension.area_province: {
                //设置省的全部的值
                dictData.stream().forEach(one -> {
                    //StringBuffer key = new StringBuffer(one.getProvince());
                    setOneData(allData, one.getProvince(), one, Contant.main_dimension_areaLevel.area_province);
                });
                break;
            }
            case Contant.main_dimension.area_city: {
                //设置市的全部的值
                dictData.stream().forEach(one -> {
                    //StringBuffer key = new StringBuffer(one.getProvince() + "-" + one.getCity());
                    setOneData(allData, one.getCity(), one, Contant.main_dimension_areaLevel.area_city);
                });
                break;
            }
            case Contant.main_dimension.area_town: {
                //设置区的全部的值
                dictData.stream().forEach(one -> {
                    //StringBuffer key = new StringBuffer(one.getProvince() + "-" + one.getCity() + "-" + one.getTown());
                    setOneData(allData, one.getTown(), one, Contant.main_dimension_areaLevel.area_town);
                });
                break;
            }
            case Contant.main_dimension.area_org: {
                //设置机构
                dictData.stream().forEach(one -> {
                    // StringBuffer key = new StringBuffer(one.getProvince() + "-" + one.getCity() + "-" + one.getTown() + "-" + one.getHospital());
                    setOneData(allData, one.getOrg(), one, Contant.main_dimension_areaLevel.area_org);
                });
                break;
            }
            case Contant.main_dimension.area_team: {
                //设置团队
                dictData.stream().forEach(one -> {
                    // StringBuffer key = new StringBuffer(one.getProvince() + "-" + one.getCity() + "-" + one.getTown() + "-" + one.getHospital() + "-" + one.getTeam());
                    setOneData(allData, one.getTeam(), one, Contant.main_dimension_areaLevel.area_team);
                });
                break;
            }
        }
    }

    private void setOneData(Map<String, SaveModel> allData, String key, SaveModel one, String areaLevel) {
        one.setAreaLevel(areaLevel);
        one.setResult("0");
        one.setCreateTime(new Date());
        LocalDate today = LocalDate.now();
        String yesterDay = (new DateTime().minusDays(1)).toString("yyyy-MM-dd");
        one.setQuotaDate(yesterDay);
        one.setQuotaCode(quotaVo.getCode());
        one.setQuotaName(quotaVo.getName());
        one.setTimeLevel(timeLevel);
        one.setSaasId(null);
        allData.put(key, one);
    }

    private SaveModel setSaveModel(SaveModel one ) {
        one.setResult("0");
        one.setCreateTime(new Date());
        LocalDate today = LocalDate.now();
        String yesterDay = (new DateTime().minusDays(1)).toString("yyyy-MM-dd");
        one.setQuotaDate(yesterDay);
        one.setQuotaCode(quotaVo.getCode());
        one.setQuotaName(quotaVo.getName());
        one.setTimeLevel(timeLevel);
        one.setSaasId(null);
        return one;
    }



}
