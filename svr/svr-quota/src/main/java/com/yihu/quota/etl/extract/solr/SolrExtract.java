package com.yihu.quota.etl.extract.solr;

import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.apache.solr.client.solrj.response.RangeFacet;
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
public class SolrExtract {
    private Logger logger = LoggerFactory.getLogger(SolrExtract.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    SolrUtil solrUtil;
    @Autowired
    ExtractUtil extractUtil;
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

        //根据solr 统计数据
        return statiscSlor(qdm,qds,quotaVo);

    }

    public List<SaveModel> statiscSlor(
            List<TjQuotaDimensionMain> qdm, List<TjQuotaDimensionSlave> qds,QuotaVo quotaVo) throws Exception {

        String core = esConfig.getTable();//"HealthProfile"
        String fq = null;
        String q = null;
        String groupFields = "";
        List<SolrGroupEntity> customGroup = null;//自定义分组

        Map<String,String> mainMap = new HashMap<>();
        Map<String,String> slaveMap = new HashMap<>();
        for (int i = 0; i < qdm.size(); i++) {
            mainMap.put(qdm.get(i).getKeyVal(),qdm.get(i).getKeyVal());
            if( !qdm.get(i).getMainCode().trim().equals("year")){
                groupFields = groupFields + qdm.get(i).getKeyVal() + ",";
            }
        }
        for (int i = 0; i < qds.size(); i++) {
            groupFields = groupFields + qds.get(i).getKeyVal() + ",";
            slaveMap.put(qds.get(i).getKeyVal(),qds.get(i).getKeyVal());
        }
        if(groupFields.length() > 2){
            groupFields = groupFields.substring(0,groupFields.length()-1);
        }
        if (!StringUtils.isEmpty(esConfig.getTimekey())) {
            //1 全量 2 增量
            if(quotaVo!=null && quotaVo.getDataLevel().equals(Contant.quota.dataLeval_oneDay)){
                fq = esConfig.getTimekey()+":[";
                //起始时间
                if (!StringUtils.isEmpty(startTime)) {
                    fq += startTime + "T00:00:00Z";
                } else {
                    fq += "*";
                }
                fq += " TO ";
                //结束时间
                if (!StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(startTime)) {
                    fq += startTime + "T23:59:59Z";
                } else {
                    fq += "*";
                }
                fq += "]";
            }else {
                fq = esConfig.getTimekey()+":[ * TO  ";
                //结束时间
                if (!StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(startTime)) {
                    fq += startTime + "T23:59:59Z";
                } else {
                    fq += "*";
                }
                fq += "]";
            }
            System.out.println(fq);
        }

        if(esConfig.getFilter() != null){
            if(StringUtils.isEmpty(fq)){
                fq = esConfig.getFilter();
            }else {
                fq += " AND " +  esConfig.getFilter();
            }
        }


        List<String> years = new ArrayList<>();
        if(qdm.size() > 0){
            for(TjQuotaDimensionMain main:qdm){
                if(main.getMainCode().trim().equals("year")){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(2015, 01, 01);
                    List<RangeFacet> rangeFacets = solrUtil.getFacetDateRange(core, main.getKeyVal(), calendar.getTime(), new Date(), "+1YEAR", null);
                    for(RangeFacet rangeFacet : rangeFacets){
                        List<RangeFacet.Count> counts  = rangeFacet.getCounts();
                        for(RangeFacet.Count count:counts){
                            years.add(count.getValue().substring(0, 4));
                        }
                    }
                }
            }
        }
        Map<String,Long> map = new HashMap<>();
        List<SaveModel> returnList = new ArrayList<>();
        String yearfq = esConfig.getTimekey() +":[year-01-01T00:00:00Z TO year-12-31T23:59:59Z]";
        //core 表名 q 查询条件  fq 过滤条件  group 分
        if( (qdm.size() + qds.size()) == 1 || ((qdm.size() + qds.size())==2 && years.size()>0)){
            //单分组查询
            if(years != null && years.size() > 0 ) {
                for (String year : years) {
                    fq += " " + yearfq.replaceAll("year", year);
                    Map<String, Long>  resMap = solrUtil.groupCount(core, q, fq, groupFields, 0, 1000);
                    computeYear(qdm, qds, returnList, resMap,year);
                }
                return returnList;
            }else{
                Map<String, Long>  resMap = solrUtil.groupCount(core, q, fq, groupFields, 0, 1000);
                compute(qdm,qds,returnList,resMap);
                return returnList;
            }
        }else {
            //多分组查询
            List<Map<String, Object>>   list = null;
            if(years != null && years.size() > 0 ){

                for(String year : years){
                    fq += " " + yearfq.replaceAll("year",year);
                    try {
                        List<Map<String, Object>>  groupList = solrQuery.getGroupMultList(core, groupFields, customGroup, q, fq);
                        for(Map<String, Object> mapObj : groupList){
                            list.add(mapObj);
                        }
                    }catch (Exception e){
                        throw  new Exception("solr查询数据出错！" + e.getMessage());
                    }
                }
            }else {

            }
            try {
                list = solrQuery.getGroupMultList(core, groupFields, customGroup, q, fq);
            }catch (Exception e){
                throw  new Exception("solr查询数据出错！" + e.getMessage());
            }

            if(list != null && list.size() > 0){
                for(Map<String, Object> objectMap : list){
                    long count = 0;
                    String mainSlavesStr = "";
                    int num = 1;
                    for(String key : objectMap.keySet()){
                        if(mainMap.get(key) != null){
                            if( num ==1){
                                mainSlavesStr = mainSlavesStr + objectMap.get(key);
                                num ++;
                            }else{
                                mainSlavesStr = mainSlavesStr + "-" + objectMap.get(key);
                            }
                        }else if(key.equals("$count")){
                            if(objectMap.get(key) != null){
                                count = Long.valueOf(objectMap.get(key).toString() );
                            }
                        }
                    }
                    for(String key : objectMap.keySet()){
                        if(slaveMap.get(key) != null) {
                            mainSlavesStr = mainSlavesStr + "-" + objectMap.get(key);
                        }
                    }
                    map.put(mainSlavesStr,count);
                }
            }
        }
        //整合数据
        if(map != null  && map.size() >0){
            compute(qdm,qds,returnList,map);
        }
        return  returnList;
    }

    private void compute(List<TjQuotaDimensionMain> qdm,List<TjQuotaDimensionSlave> qds,
            List<SaveModel> returnList, Map<String, Long> map) throws Exception {
        Map<String, SaveModel> allData = new HashMap<>();
        extractUtil.setQuotaVo(quotaVo);
        //初始化主细维度
        if(qdm!=null && qdm.size()>0){
            if(qdm.size() == 1){
                allData= extractUtil.initDimension(qds, qdm.get(0), allData);
            }else {
                allData= extractUtil.initDimensionMoreMain(qds, qdm, allData);
            }
        }else{
            allData= initDimension(qds, null, allData);
        }


        for(String key :map.keySet()){
            SaveModel saveModel = allData.get(key);
            Long count = 0L;
            if(map.get(key) != null){
                count = map.get(key);
            }
            if(saveModel != null && count != null){
                saveModel.setResult(count.toString());
                returnList.add(saveModel);
            }
        }
        //数据源中不存在的组合 保存数据为0  待实现
        //ToDo
    }

    private void computeYear(List<TjQuotaDimensionMain> qdm,List<TjQuotaDimensionSlave> qds,
                         List<SaveModel> returnList, Map<String, Long> map,String year) throws Exception {
        Map<String, SaveModel> allData = new HashMap<>();
        extractUtil.setQuotaVo(quotaVo);
        //初始化主细维度
        if(qdm!=null && qdm.size()>0){
            if(qdm.size() == 1){
                allData= extractUtil.initDimension(qds, qdm.get(0), allData);
            }else {
                allData= extractUtil.initDimensionMoreMain(qds, qdm, allData);
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
            saveModel.setYear(year);
            saveModel.setYearName(year);
            returnList.add(saveModel);
        }
    }

    /**
     * 初始化主细维度
     */
    private  Map<String, SaveModel>  initDimension(List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves, TjQuotaDimensionMain quotaDimensionMain, Map<String, SaveModel> allData) throws Exception {
       try {
           //查询字典数据
           List<SaveModel> dictData = jdbcTemplate.query(quotaDimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
           if (dictData == null) {
               throw new Exception("主纬度配置有误");
           }else {
               //设置到map里面
               setAllData(allData, dictData, quotaDimensionMain.getType());
               for (int i = 0; i < tjQuotaDimensionSlaves.size(); i++) {
                   List<DictModel> dictDataSlave = jdbcTemplate.query(tjQuotaDimensionSlaves.get(i).getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                   if (dictDataSlave == null) {
                       throw new Exception("细纬度配置有误");
                   }else {
                       allData = setAllSlaveData(allData, dictDataSlave,i);
                   }
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
     * @param dictData 细维度集合
     * @param dictType 主维度类型
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
            case Contant.main_dimension.time_year: {
                // 设置年份
                dictData.stream().forEach(one -> {
                    this.timeLevel = Contant.main_dimension_timeLevel.year;
                    setOneData(allData, one.getYear(), one, null);
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


}
