package com.yihu.quota.etl.extract.es;

import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.quota.BaseStatistsService;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
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

import java.text.NumberFormat;
import java.util.*;

/**
 * Created by chenweida on 2017/6/6.
 */
@Component
@Scope("prototype")
public class EsExtract {
    private Logger logger = LoggerFactory.getLogger(EsExtract.class);
    @Autowired
    private EsClientUtil esClientUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ExtractUtil extractUtil;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private BaseStatistsService baseStatistsService;

    @Autowired
    private OrgHealthCategoryStatisticsService orgHealthCategoryStatisticsService;

    private String startTime;
    private String endTime;
    private String timeLevel;
    private String saasid;
    private QuotaVo quotaVo;
    private EsConfig esConfig;

    private static String orgHealthCategory = "orgHealthCategory";


    public List<SaveModel> extract(List<TjQuotaDimensionMain> qdm,//主维度
                                   List<TjQuotaDimensionSlave> qds,//细维度
                                   String startTime,//开始时间
                                   String endTime, //结束时间
                                   String timeLevel, //时间维度  1日,2 周, 3 月,4 年
                                   String saasid,//saasid
                                   QuotaVo quotaVo,//指标code
                                   EsConfig esConfig //es配置
    ) throws Exception {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeLevel = timeLevel;
        this.saasid = saasid;
        this.quotaVo = quotaVo;
        this.esConfig = esConfig;
        initParams(this.startTime ,this.endTime);
        List<SaveModel> saveModels = null;
        //普通通用 拼接sql 方式
        //拼凑查询的sql
        String sql = getSql(qdm, qds);
        logger.debug("查询sql:" + sql);
        //根据sql查询ES
        try {
            saveModels = queryEsBySql(sql,esConfig.getTimekey(),qdm, qds);
        }catch (Exception e){
            throw new Exception("es 查询数据出错！" +e.getMessage() );
        }
        return saveModels;
    }

    public void initParams(String startTime, String endTime) {
        // 初始执行指标，起止日期没有值
        this.startTime = startTime == null ? null : startTime.substring(0,10);
        String now = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
        this.endTime = endTime == null ? now : endTime.substring(0,10);;
    }

    /**
     * 卫生机构类别 抽取
     * @param qdm
     * @param qds
     * @param startTime
     * @param endTime
     * @param timeLevel
     * @param saasid
     * @param quotaVo
     * @param esConfig
     * @return
     * @throws Exception
     */
    public List<SaveModel> extractOrgHealthCategory(List<TjQuotaDimensionMain> qdm,//主维度
                                   List<TjQuotaDimensionSlave> qds,//细维度
                                   String startTime,//开始时间
                                   String endTime, //结束时间
                                   String timeLevel, //时间维度  1日,2 周, 3 月,4 年
                                   String saasid,//saasid
                                   QuotaVo quotaVo,//指标code
                                   EsConfig esConfig //es配置
    ) throws Exception {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeLevel = timeLevel;
        this.saasid = saasid;
        this.quotaVo = quotaVo;
        this.esConfig = esConfig;
        System.out.println();

        List<SaveModel> saveModels = new ArrayList<>();
        try {
            //二次统计   特殊类型：卫生机构类型
            if( (!StringUtils.isEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals(orgHealthCategory)){
                Map<String, Object> dimensionMap = new HashMap<>();
                Map<String,String>  dimensionDicMap = new HashMap<>();
                //查询除开 机构维度  其他维度的字典项和 维度合并到map
                //维度key 统一变小写
                for(TjQuotaDimensionMain main:qdm){
                    if(!main.getMainCode().trim().equals("org")){
                        dimensionMap.put(main.getMainCode(),main.getMainCode());
                        if(org.apache.commons.lang.StringUtils.isNotEmpty(main.getDictSql())){
                            Map<String,String> dicMap = baseStatistsService.getDimensionMap(main.getDictSql(),main.getMainCode());
                            if(dicMap != null && dicMap.size() > 0){
                                for(String key :dicMap.keySet()){
                                    dimensionDicMap.put(key.toLowerCase(),dicMap.get(key));
                                }
                            }
                        }
                    }
                }
                for(TjQuotaDimensionSlave slave:qds){
                    dimensionMap.put(slave.getKeyVal(),slave.getKeyVal());
                    if(org.apache.commons.lang.StringUtils.isNotEmpty(slave.getDictSql())){
                        Map<String,String> dicMap = baseStatistsService.getDimensionMap(slave.getDictSql(), slave.getSlaveCode());
                        if(dicMap != null && dicMap.size() > 0){
                            for(String key :dicMap.keySet()){
                                dimensionDicMap.put(key.toLowerCase(),dicMap.get(key));
                            }
                        }
                    }
                }
                String dimension = "";
                for(String key :dimensionMap.keySet()){
                    dimension += dimensionMap.get(key) + ";";
                }
                if(dimension.length() < 1 ){
                    throw new Exception("特殊机构类型转化时 维度不能为空！" );
                }else {
                    dimension = dimension.substring(0,dimension.length()-1);
                }
                String filter = "";
                if ( !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                    filter = " quotaDate >= '" + startTime.substring(0,10) + "' and quotaDate <= '" + endTime.substring(0,10) +"' ";
                }
                //查询分组的时候要增加 org 时间 ，每天的不同机构的数据
                dimension += ";org;quotaDate";
                dimensionMap.put("org", "org");
                dimensionMap.put("quotaDate","quotaDate");

                List<Map<String, Object>> orgTypeResultList = new ArrayList<>();
                List<Map<String, Object>> mapList = baseStatistsService.getOrgHealthCategoryQuotaResultList(esConfig.getSuperiorBaseQuotaCode(),dimension,filter, "");
                if(mapList != null && mapList.size() > 0){
                    for(Map<String,Object> map : mapList){
                        String dictSql = "SELECT org_code as orgCode,hos_type_id as hosTypeId from organizations where org_code=";
                        dictSql = dictSql + "'"+ map.get("org") + "'";
                        List<MOrganization> organizations = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(MOrganization.class));
                        if(organizations != null && organizations.size() > 0){
                            if(!StringUtils.isEmpty(organizations.get(0).getHosTypeId())){
                                map.put(orgHealthCategory,organizations.get(0).getHosTypeId());
                                orgTypeResultList.add(map);
                            }
                        }
                    }
                }

                List<List<Map<String, Object>>> sumOrgTypeList =  stastisOrtType(orgTypeResultList,dimensionMap, dimensionDicMap);


                Map<String,String> dimenKeyValMap = new HashMap<>();
                dimensionMap.remove("org");
                for(Map<String,Object> map : orgTypeResultList){
                    String key = "";
                    for(String dimen:dimensionMap.keySet()){
                        key +=  map.get(dimen) + "-" ;
                    }
                    dimenKeyValMap.put(key,key);
                }
                List<List<Map<String, Object>>> dimenSumOrgTypeList = new ArrayList<>();

                for(String dimenKey :dimenKeyValMap.keySet()){
                    List<Map<String, Object>> newDimenList = new ArrayList<>();
                    for(List<Map<String, Object>> list:sumOrgTypeList){
                        String key = "";
                        for(Map<String, Object> map :list){
                            for(String dimen:dimensionMap.keySet()){
                                key += map.get(dimen)  + "-" ;
                            }
                            if(dimenKey.equals(key)){
                                key +=  map.get("code");
                                boolean flag = true;
                                //判断有没有维度一致的 有就结果叠加
                                if(newDimenList != null && newDimenList.size() > 0){
                                    for(Map<String, Object> newDimeMap :newDimenList) {
                                        String newkey = "";
                                        for (String dimen : dimensionMap.keySet()) {
                                            newkey += newDimeMap.get(dimen) + "-";
                                        }
                                        newkey +=  newDimeMap.get("code");
                                        if(newkey.equals(key)){
                                            int newResult = Integer.valueOf(newDimeMap.get("result").toString());
                                            int result = Integer.valueOf(map.get("result").toString());
                                            newDimeMap.put("result",newResult + result );
                                            flag = false;
                                        }
                                    }
                                }
                                if(flag){
                                    newDimenList.add(map);
                                }
                            }
                        }
                    }
                    dimenSumOrgTypeList.add(newDimenList);
                }

                for(List<Map<String, Object>> list:dimenSumOrgTypeList){
                    saveModels.addAll(orgHealthCategoryStatisticsService.getAllNodesStatistic(list));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("ES 特殊机构转化 查询数据出错！" +e.getMessage() );
        }

        return saveModels;
    }


    /**
     * 统计整理 不同维度的组合数据
     * @param orgTypeList
     * @param dimensionMap 维度集合
     * @param dimensionDicMap 字典项key-value map
     * @return
     */
    public List<List<Map<String, Object>>> stastisOrtType(List<Map<String, Object>> orgTypeList,Map<String, Object> dimensionMap,
                                                          Map<String,String>  dimensionDicMap){

        Map<String,String> dimenTypeMap = new HashMap<>();
        Map<String,String> orgDimenTypeMap = new HashMap<>();
        if(orgTypeList != null && orgTypeList.size() > 0){
            for(Map<String,Object> map : orgTypeList){
                if( !StringUtils.isEmpty(map.get(orgHealthCategory))){
                    String key = "";
                    String orgTypekey = map.get(orgHealthCategory).toString();
                    for(String dimen:dimensionMap.keySet()){
                        key += "-" + map.get(dimen);
                        orgTypekey += "-" + map.get(dimen);
                    }
                    dimenTypeMap.put(key, key);//map 不重复
                    orgDimenTypeMap.put(orgTypekey, orgTypekey);//map 不重复
                }
            }
        }
        List<List<Map<String, Object>>> dimenSumList = new ArrayList<>();
        if(dimenTypeMap != null && dimenTypeMap.size() > 0){
            for(String type : dimenTypeMap.keySet()){
                List<Map<String, Object>> sumOrgTypeList = new ArrayList<>();
                double count = 0.0;
                for(String orgDimenType : orgDimenTypeMap.keySet()){
                    Map<String, Object> sumDimenMap = new HashMap<>();
                    if(orgDimenType.contains(type) && orgTypeList != null && orgTypeList.size() > 0){
                        for(Map<String,Object> map : orgTypeList){
                            String key = map.get(orgHealthCategory).toString();
                            for(String dimen:dimensionMap.keySet()){
                                key += "-" + map.get(dimen);
                            }
                            if(orgDimenType.equals(key)){
                                sumDimenMap.put("code",map.get(orgHealthCategory).toString());
                                sumDimenMap.put("quotaDate",map.get("quotaDate"));
                                count = count + Double.valueOf(map.get("result").toString());
                                if(dimensionMap != null && dimensionMap.size() > 0){
                                    for(String dimen:dimensionMap.keySet()){
                                        sumDimenMap.put(dimen,map.get(dimen));
                                        sumDimenMap.put(dimen+"Name",dimensionDicMap.get(map.get(dimen).toString().toLowerCase()));
                                    }
                                }
                            }
                        }
                        sumDimenMap.put("quotaCode",quotaVo.getCode());
                        sumDimenMap.put("quotaName",quotaVo.getName());
                        sumDimenMap.put("result",(int)count);
                        sumOrgTypeList.add(sumDimenMap);
                    }
                }
                dimenSumList.add(sumOrgTypeList);
            }
        }
        return  dimenSumList;
    }

    private  List<SaveModel> queryEsBySql(String sql,String timekey,List<TjQuotaDimensionMain> qdm,  List<TjQuotaDimensionSlave> qds) {
        List<SaveModel> returnList = new ArrayList<>();
        try {
            List<String> dimenList = new ArrayList<>();
            for(TjQuotaDimensionMain main:qdm){
                dimenList.add(main.getKeyVal());
            }
            for(TjQuotaDimensionSlave slave:qds){
                dimenList.add(slave.getKeyVal());
            }
            Map<String,String> resultMap = new HashMap<>();
            Map<String, String> daySlaveDictMap = new HashMap<>();
            List<Map<String, Object>> listMap = elasticsearchUtil.excuteDataModel(sql.toString());
            for(Map<String, Object> map : listMap){
                String keyVal = "";
                for(String dimen :dimenList){
                    if(map.get(dimen) != null){
                        if(keyVal.length()==0){
                            keyVal = map.get(dimen).toString();
                        }else {
                            keyVal += "-" + map.get(dimen) ;
                        }
                    }
                }
                String dateKey = "date_histogram(field=" + timekey + ",interval=day)";
                if(map.containsKey(dateKey)){
                    keyVal += "-" + map.get(dateKey).toString().substring(0, 10);
                    daySlaveDictMap.put(keyVal,map.get(dateKey).toString().substring(0,10));
                }
                if(map.containsKey("result")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    resultMap.put(keyVal, nf.format(map.get("result")));
                }
                if(map.containsKey("count(1)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    resultMap.put(keyVal, nf.format(map.get("count(1)")));
                }
                if(map.containsKey("SUM(result)")){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    resultMap.put(keyVal, nf.format(map.get("SUM(result)")));
                }
            }
            TjQuotaDimensionSlave tjQuotaDimensionSlave = new TjQuotaDimensionSlave();
            tjQuotaDimensionSlave.setQuotaCode(quotaVo.getCode());
            qds.add(tjQuotaDimensionSlave);
            extractUtil.compute(qdm, qds, returnList, resultMap, daySlaveDictMap, quotaVo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnList;
    }

    /**
     * 拼接sql
     * @param tjQuotaDimensionMains
     * @param tjQuotaDimensionSlaves
     * @return
     */
    private String getSql(List<TjQuotaDimensionMain> tjQuotaDimensionMains, List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves) {

        StringBuffer allField = new StringBuffer("");
        String tableName = esConfig.getIndex();
        for (TjQuotaDimensionMain one :tjQuotaDimensionMains) {
            String code = one.getKeyVal();
            allField.append(code+ ",");
        }
        for (TjQuotaDimensionSlave slave :tjQuotaDimensionSlaves) {
            allField.append(slave.getKeyVal() + ",");
        }

        //拼接where语句 和 分组字段
        StringBuffer whereSql = new StringBuffer(" where ");
        boolean filterFlag = false;
        if (!StringUtils.isEmpty(esConfig.getFilter())) {
            whereSql.append("" + esConfig.getFilter());
            filterFlag = true;
        }
        String timeKey = esConfig.getTimekey();
        String timeGroup = "";
        if ( !StringUtils.isEmpty(timeKey)) {
            timeGroup = ",date_histogram(field='"+ timeKey +"','interval'='day')";
            if(filterFlag){
                whereSql.append(" and ");
            }
            if ( !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                whereSql.append( timeKey + " >= '" + startTime + "' and ");
                whereSql.append( timeKey + " < '" + endTime + "'");
            }
        }

        String selectGroupField = allField.toString();
        String whereGroupField = allField.substring(0,allField.length() - 1);
        //拼接整个sql 语法
        StringBuffer sql = new StringBuffer();
        if(StringUtils.isEmpty(esConfig.getAggregation()) && StringUtils.isEmpty(esConfig.getAddFirstQuotaCode())){
            sql.append("select " + selectGroupField + " count(1) from " + tableName + whereSql + " group by " + whereGroupField + timeGroup );
        } else if (!StringUtils.isEmpty(esConfig.getAddFirstQuotaCode())) {
            String myWhere = "quotaCode in('" + esConfig.getAddFirstQuotaCode().replace("_", "") + "','" + esConfig.getAddSecondQuotaCode().replace("_", "") + "')";
            if (!"where".equals(whereSql.toString().trim())) {
                myWhere = " and " + myWhere;
            }
            sql.append("select " + selectGroupField + " sum(result) from " + tableName + whereSql + myWhere + " group by " + whereGroupField + timeGroup);
        } else if(esConfig.getAggregation().equals(Contant.quota.aggregation_sum)){
            if(!StringUtils.isEmpty(esConfig.getAggregation()) && StringUtils.isEmpty(selectGroupField)|| selectGroupField.length()==0){
                sql.append("select sum(" ).append(esConfig.getAggregationKey()).append(" )  from " + tableName + whereSql);
            }else {
                sql.append("select ").append(selectGroupField ).append(" sum(").append(esConfig.getAggregationKey()).append(" ) result from " + tableName + whereSql + " group by "  + whereGroupField + timeGroup );
            }
        }else if(esConfig.getAggregation().equals(Contant.quota.aggregation_list)){
            if( StringUtils.isEmpty( esConfig.getAggregationKey()) ){
                sql.append("select " + selectGroupField + " 1 result from " + tableName + whereSql);
            }else {
                sql.append("select " + selectGroupField + esConfig.getAggregationKey() + " result from " + tableName + whereSql);
            }
        }
        return sql.toString();
    }

}
