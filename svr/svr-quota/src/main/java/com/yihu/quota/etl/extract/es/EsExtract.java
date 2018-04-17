package com.yihu.quota.etl.extract.es;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
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
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
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
                List<Map<String, Object>> mapList = baseStatistsService.getOrgHealthCategoryQuotaResultList(esConfig.getSuperiorBaseQuotaCode(),dimension,filter);
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
                for(List<Map<String, Object>> list:sumOrgTypeList){
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

            System.out.println("查询分组 mysql= " + sql.toString());
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


    private  List<SaveModel> queryEsBySql(Map<String, TjQuotaDimensionMain> sqls, List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves) {
        List<SaveModel> returnList = new ArrayList<>();
        //初始化es链接
        esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(esConfig), EsConfig.class);
        //初始化链接
        Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(), null);
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
                Map<String,String> map = new HashMap<>();
                //递归解析json
                expainJson(gradeBucketIt, map, null);
                compute(tjQuotaDimensionSlaves,returnList,one, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }

    private void compute(List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves, List<SaveModel> returnList, Map.Entry<String, TjQuotaDimensionMain> one, Map<String, String> map) throws Exception {
        Map<String, SaveModel> allData = new HashMap<>();
        //初始化主细维度
        allData= initDimension(tjQuotaDimensionSlaves, one, allData);


        for(String key :map.keySet()){
            SaveModel saveModel = allData.get(key);
            String count =  map.get(key);
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
    private void expainJson(Iterator<Terms.Bucket> gradeBucketIt,Map<String,String>map, StringBuffer sb) {
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
                if (b.getAggregations().asList().get(0) instanceof InternalValueCount) {
                    InternalValueCount count = (InternalValueCount) b.getAggregations().asList().get(0);
                    map.put(new StringBuffer((sb == null ? "" : (sb.toString() + "-"))+ b.getKey()).toString() , Long.valueOf(count.getValue()).toString());
                }else if (b.getAggregations().asList().get(0) instanceof InternalSum) {
                    InternalSum count = (InternalSum) b.getAggregations().asList().get(0);
                    map.put(new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + "-" + b.getKey()).toString() , count.getValue() + "");
                }
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
        if(StringUtils.isEmpty(esConfig.getAggregation())){
            sql.append("select " + selectGroupField + " count(1) from " + tableName + whereSql + " group by " + whereGroupField + timeGroup );
        }else if(esConfig.getAggregation().equals(Contant.quota.aggregation_sum)){
            if(StringUtils.isEmpty(selectGroupField)|| selectGroupField.length()==0){
                sql.append("select sum(" ).append(esConfig.getAggregationKey()).append(" )  from " + tableName + whereSql);
            }else {
                sql.append("select ").append(selectGroupField ).append(" sum(").append(esConfig.getAggregationKey()).append(" ) result from " + tableName + whereSql + " group by "  + whereGroupField + timeGroup );
            }
        }
        return sql.toString();
    }

}
