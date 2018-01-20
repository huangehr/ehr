package com.yihu.quota.etl.extract.es;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.quota.BaseStatistsService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.joda.time.DateTime;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.parse.SqlParser;
import org.nlpcn.es4sql.query.AggregationQueryAction;
import org.nlpcn.es4sql.query.DefaultQueryAction;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by chenweida on 2017/6/6.
 */
@Component
@Scope("prototype")
public class EsExtract {
    private Logger logger = LoggerFactory.getLogger(EsExtract.class);
    @Autowired
    private ElasticFactory elasticFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private ExtractUtil extractUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
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

            List<SaveModel> saveModels = null;
            //普通通用 拼接sql 方式
            //拼凑查询的sql
            String sql = getSql(qdm, qds);
            //根据sql查询ES
            try {
                saveModels = queryEsBySql(sql,qdm, qds);
            }catch (Exception e){
                throw new Exception("es 查询数据出错！" +e.getMessage() );
            }
        return saveModels;

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

        List<SaveModel> saveModels = new ArrayList<>();
        try {
            if( (!StringUtils.isEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals(orgHealthCategory)){
                //二次统计   特殊类型：卫生机构类型
                List<Map<String, Object>> orgTypeResultList = new ArrayList<>();


                Map<String, Object> dimensionMap = new HashMap<>();
                Map<String,String>  dimensionDicMap = new HashMap<>();
                //查询除开机构维度  其他维度的字典项和 维度合并到map
                for(TjQuotaDimensionMain main:qdm){
                    if(!main.getMainCode().trim().equals("org")){
                        dimensionMap.put(main.getMainCode(),main.getMainCode());
                        if(org.apache.commons.lang.StringUtils.isNotEmpty(main.getDictSql())){
                            Map<String,String> dicMap = baseStatistsService.getDimensionMap(main.getDictSql(),main.getMainCode());
                            if(dicMap != null && dicMap.size() > 0){
                                dimensionDicMap.putAll(dicMap);
                            }
                        }
                    }
                }
                for(TjQuotaDimensionSlave slave:qds){
                    dimensionMap.put(slave.getKeyVal(),slave.getKeyVal());
                    if(org.apache.commons.lang.StringUtils.isNotEmpty(slave.getDictSql())){
                        Map<String,String> dicMap = baseStatistsService.getDimensionMap(slave.getDictSql(), slave.getSlaveCode());
                        if(dicMap != null && dicMap.size() > 0){
                            dimensionDicMap.putAll(dicMap);
                        }
                    }
                }
                String dimension = "";
                for(String key :dimensionMap.keySet()){
                    dimension += dimensionMap.get(key) + ";";
                }
                if(dimension.length() < 1 ){
                    throw new Exception("特殊机构类型转化时 维度维度不能为空！" );
                }else {
                    dimension = dimension.substring(0,dimension.length()-1);
                }
                String filter = "";
                if ( !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                    filter = " quotaDate >= '" + startTime.substring(0,10) + "' and quotaDate <= '" + endTime.substring(0,10) +"' ";
                }

                List<Map<String, Object>> mapList = baseStatistsService.getOrgHealthCategoryQuotaResultList(esConfig.getSuperiorBaseQuotaCode(),dimension,filter);
//            List<Map<String, Object>> mapList =  quotaService.queryResultPageByCode(quotaCode, "", 1, 10000);
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
                boolean resultFlag = false;
                List<List<Map<String, Object>>> sumOrgTypeList =  stastisOrtType(orgTypeResultList,dimensionMap, dimensionDicMap);
                for(List<Map<String, Object>> list:sumOrgTypeList){
                    resultFlag = orgHealthCategoryStatisticsService.countResultsAndSaveToEs(list);
                }

                SaveModel saveModel = new SaveModel();
                saveModel.setQuotaCode(orgHealthCategory);
                if(resultFlag){
                    saveModel.setSaasId("success");
                }else {
                    saveModel.setSaasId("fail");
                }
                saveModels.add(saveModel);
            }
        }catch (Exception e){
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

//        Map<String,String>  dimensionDicMap = new HashMap<>();
//        Map<String, Object> dimensionMap = new HashMap<>();
//        for(TjQuotaDimensionMain main:qdm){
//            if(!main.getMainCode().trim().equals("org")){
//                dimensionMap.put(main.getMainCode(),main.getMainCode());
//                if(org.apache.commons.lang.StringUtils.isNotEmpty(main.getDictSql())){
//                    Map<String,String> dicMap = baseStatistsService.getDimensionMap(main.getDictSql(),main.getMainCode());
//                    if(dicMap != null && dicMap.size() > 0){
//                        dimensionDicMap.putAll(dicMap);
//                    }
//                }
//            }
//        }
//        for(TjQuotaDimensionSlave slave:qds){
//            dimensionMap.put(slave.getSlaveCode(),slave.getSlaveCode());
//            if(org.apache.commons.lang.StringUtils.isNotEmpty(slave.getDictSql())){
//                Map<String,String> dicMap = baseStatistsService.getDimensionMap(slave.getDictSql(), slave.getSlaveCode());
//                if(dicMap != null && dicMap.size() > 0){
//                    dimensionDicMap.putAll(dicMap);
//                }
//            }
//        }


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
                                count = count + Double.valueOf(map.get("result").toString());
                                if(dimensionMap != null && dimensionMap.size() > 0){
                                    for(String dimen:dimensionMap.keySet()){
                                        sumDimenMap.put(dimen,map.get(dimen));
                                        sumDimenMap.put(dimen+"Name",dimensionDicMap.get(map.get(dimen)));
                                    }
                                }
                            }
                            sumDimenMap.put("quotaDate",map.get("quotaDate"));
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
        String yesterDay = (new DateTime().minusDays(1)).toString("yyyy-MM-dd");
        one.setQuotaDate(yesterDay);
        one.setQuotaCode(quotaVo.getCode());
        one.setQuotaName(quotaVo.getName());
        one.setTimeLevel(timeLevel);
        one.setSaasId(saasid);
        allData.put(key, one);
    }

    private  List<SaveModel> queryEsBySql(String sql,List<TjQuotaDimensionMain> qdm,  List<TjQuotaDimensionSlave> qds) {
        List<SaveModel> returnList = new ArrayList<>();
        //初始化es链接
        esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(esConfig), EsConfig.class);
        //初始化链接
        Client client = elasticSearchPool.getClient();
        logger.info("excute sql:" + sql);
        System.out.println(sql);
        try {
            SQLExprParser parser = new ElasticSqlExprParser(sql);
            SQLExpr expr = parser.expr();
            if (parser.getLexer().token() != Token.EOF) {
                throw new ParserException("illegal sql expr : " + sql);
            }
            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;
            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
            Select select = null;
            select = new SqlParser().parseSelect(queryExpr);

            AggregationQueryAction action = null;
            DefaultQueryAction queryAction = null;
            SqlElasticSearchRequestBuilder requestBuilder = null;
            if (select.isAgg) {
                //包含计算的的排序分组的
                action = new AggregationQueryAction(client, select);
                requestBuilder = action.explain();
            } else {
                //封装成自己的Select对象
                queryAction = new DefaultQueryAction(client, select);
                requestBuilder = queryAction.explain();
            }
            //之后就是对ES的操作
            SearchResponse response = (SearchResponse) requestBuilder.get();
            StringTerms stringTerms = (StringTerms) response.getAggregations().asList().get(0);
            Iterator<Terms.Bucket> gradeBucketIt = stringTerms.getBuckets().iterator();
            client.close();
            //里面存放的数据 例  350200-5-2-2    主维度  细维度1  细维度2  值
            Map<String,Integer> map = new HashMap<>();
            //递归解析json
            expainJson(gradeBucketIt, map, null);

            Map<String, String> daySlaveDictMap = new HashMap<>();
            extractUtil.compute(qdm, qds, returnList, map,daySlaveDictMap,quotaVo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnList;
    }


    private  List<SaveModel> queryEsBySql(Map<String, TjQuotaDimensionMain> sqls, List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves) {
        List<SaveModel> returnList = new ArrayList<>();
        //初始化es链接
        esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(esConfig), EsConfig.class);
        //初始化链接
        Client client = elasticFactory.getClient(esConfig.getHost(), esConfig.getPort(), null);
        for (Map.Entry<String, TjQuotaDimensionMain> one : sqls.entrySet()) {
            logger.info("excute sql:" + one.getKey());
            try {
                SQLExprParser parser = new ElasticSqlExprParser(one.getKey());
                SQLExpr expr = parser.expr();
                if (parser.getLexer().token() != Token.EOF) {
                    throw new ParserException("illegal sql expr : " + one);
                }
                SQLQueryExpr queryExpr = (SQLQueryExpr) expr;
                //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
                Select select = null;
                select = new SqlParser().parseSelect(queryExpr);

                AggregationQueryAction action = null;
                DefaultQueryAction queryAction = null;
                SqlElasticSearchRequestBuilder requestBuilder = null;
                if (select.isAgg) {
                    //包含计算的的排序分组的
                    action = new AggregationQueryAction(client, select);
                    requestBuilder = action.explain();
                } else {
                    //封装成自己的Select对象
                    queryAction = new DefaultQueryAction(client, select);
                    requestBuilder = queryAction.explain();
                }
                //之后就是对ES的操作
                SearchResponse response = (SearchResponse) requestBuilder.get();
                StringTerms stringTerms = (StringTerms) response.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketIt = stringTerms.getBuckets().iterator();
                client.close();
                //里面存放的数据 例  350200-5-2-2    主维度  细维度1  细维度2  值
                Map<String,Integer> map = new HashMap<>();
                //递归解析json
                expainJson(gradeBucketIt, map, null);
                compute(tjQuotaDimensionSlaves,returnList,one, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }

    private void compute(List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves, List<SaveModel> returnList, Map.Entry<String, TjQuotaDimensionMain> one, Map<String, Integer> map) throws Exception {
        Map<String, SaveModel> allData = new HashMap<>();
        //初始化主细维度
        allData= initDimension(tjQuotaDimensionSlaves, one, allData);


        for(String key :map.keySet()){
            SaveModel saveModel = allData.get(key);
            Integer count =  map.get(key);
            if(saveModel != null ){
                saveModel.setResult(count.toString());
                returnList.add(saveModel);
            }
        }
        //数据源中不存在的组合 保存数据为0  待实现
        //ToDo
    }

    /**
     * 初始化主细维度
     */
    private  Map<String, SaveModel>  initDimension(List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves, Map.Entry<String,
            TjQuotaDimensionMain> one, Map<String, SaveModel> allData) throws Exception {
        try {
            TjQuotaDimensionMain quotaDimensionMain = one.getValue();
            //查询字典数据
            List<SaveModel> dictData = jdbcTemplate.query(quotaDimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
            if (dictData == null) {
                throw new Exception("主纬度配置有误");
            }else{
                //设置到map里面
                setAllData(allData, dictData, quotaDimensionMain.getType());
                for (int i = 0; i < tjQuotaDimensionSlaves.size(); i++) {
                    List<DictModel> dictDataSlave = jdbcTemplate.query(tjQuotaDimensionSlaves.get(i).getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                    if (dictDataSlave == null) {
                        throw new Exception("细纬度配置有误");
                    }else{
                        allData = setAllSlaveData(allData, dictDataSlave,i);
                    }
                }
            }
        }catch (Exception e){
            throw new Exception("纬度配置有误");
        }
        return allData;
    }

    /**
     * 递归解析json
     *
     * @param gradeBucketIt
     * @param map
     * @param sb
     */
    private void expainJson(Iterator<Terms.Bucket> gradeBucketIt,Map<String,Integer>map, StringBuffer sb) {
        while (gradeBucketIt.hasNext()) {
            Terms.Bucket b =  gradeBucketIt.next();
            if (b.getAggregations().asList().get(0) instanceof StringTerms) {
                StringTerms stringTermsCh = (StringTerms) b.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketItCh = stringTermsCh.getBuckets().iterator();
                while (gradeBucketItCh.hasNext()) {
                    StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                    expainJson(gradeBucketItCh, map, sbTemp);
                }
            }else if (b.getAggregations().asList().get(0) instanceof LongTerms) {
                LongTerms longTermsCh = (LongTerms) b.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketItCh = longTermsCh.getBuckets().iterator();
                while (gradeBucketItCh.hasNext()) {
                    StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                    expainJson(gradeBucketItCh, map, sbTemp);
                }
            }else if (b.getAggregations().asList().get(0) instanceof DoubleTerms) {
                DoubleTerms doubleTermsCh = (DoubleTerms) b.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketItCh = doubleTermsCh.getBuckets().iterator();
                while (gradeBucketItCh.hasNext()) {
                    StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                    expainJson(gradeBucketItCh, map, sbTemp);
                }
            }else {
                    InternalValueCount count = (InternalValueCount) b.getAggregations().asList().get(0);
                    map.put(new StringBuffer(sb.toString() + "-" + b.getKey()).toString() , (int)count.getValue());
            }
        }
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
        String timeDimen = "";
        for (TjQuotaDimensionMain one :tjQuotaDimensionMains) {
            String code = one.getMainCode();
            if(code.equals("year")){
                timeDimen = "year";
            }else if(code.equals("month")){
                timeDimen = "month";
            }else{
                allField.append(code+ ",");
            }
        }
        for (TjQuotaDimensionSlave slave :tjQuotaDimensionSlaves) {
            allField.append(slave.getSlaveCode() + ",");
        }
        //拼接where语句 和 分组字段
        StringBuffer whereSql = new StringBuffer();
        if (!StringUtils.isEmpty(esConfig.getFilter())) {
            whereSql.append(esConfig.getFilter());
        }
        String timeKey = esConfig.getTimekey();
        if ( !StringUtils.isEmpty(timeKey)) {
            if ( !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                if(whereSql.length() > 1){
                    whereSql.append(" and " + timeKey + " >= '" + startTime + "'");
                    whereSql.append( " and " + timeKey + " < '" + endTime + "'");
                }else{
                    whereSql.append(timeKey + " >= '" + startTime + "'");
                    whereSql.append( " and " + timeKey + " < '" + endTime + "'");
                }
            }
        }
        String selectGroupField = allField.toString();
        String whereGroupField = allField.toString();
        //拼接整个sql 语法
        StringBuffer sql = new StringBuffer();
        if(StringUtils.isEmpty(esConfig.getAggregation())){
            sql.append("select " + selectGroupField + " count(*) from " + tableName + whereSql + " group by " + whereGroupField);
        }else if(esConfig.getAggregation().equals(Contant.quota.aggregation_sum)){
            if(StringUtils.isEmpty(selectGroupField)|| selectGroupField.length()==0){
                sql.append("select sum(" ).append(esConfig.getAggregationKey()).append(" ) result  from " + tableName + whereSql);
            }else {
                sql.append("select ").append(selectGroupField ).append(" sum(").append(esConfig.getAggregationKey()).append(" ) result from " + tableName + whereSql + " group by " + whereGroupField);
            }
        }
        return sql.toString();
    }

}
