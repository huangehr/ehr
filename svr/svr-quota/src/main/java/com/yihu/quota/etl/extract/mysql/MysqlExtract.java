package com.yihu.quota.etl.extract.mysql;

import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.ExtractConverUtil;
import com.yihu.quota.etl.conver.ConvertHelper;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.FilterModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
    private SolrUtil solrUtil;
    @Autowired
    private SolrQuery solrQuery;
    @Autowired
    private ExtractUtil extractUtil;
    @Autowired
    private ExtractConverUtil extractConverUtil;
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
        initParams(this.startTime ,this.endTime);

        List<SaveModel> returnList = new ArrayList<>();
        //获取mysql
        String mysql = getSql(qdm,qds);
        logger.debug(mysql);
        System.out.println("统计mysql :" + mysql);
        if( !StringUtils.isEmpty(mysql)){
            Map<String,String> resultMap = new HashMap<>();
            Map<String, String> daySlaveDictMap = new HashMap<>();
            //执行MySQL
            List<Map<String, Object>> mapList = null;
            try {
                mapList =  jdbcTemplate.queryForList(mysql);
                FilterModel filterModel = new FilterModel(mapList,null);
                //数据转换
                extractConverUtil.convert(filterModel,qds);

            }catch (Exception e){
                throw new Exception("mysql查询数据出错" + e.getMessage());
            }
            if(mapList != null && mapList.size() > 0){
                for(Map<String, Object> map :mapList){
                    String keyVal  = "";
                    for(String key :map.keySet()){
                        if(!key.equals("result")){
                            keyVal = keyVal + map.get(key) +  "-";
                        }
                    }
                    String result = map.get("result").toString();
                    String mapKey = keyVal.substring(0, keyVal.length() - 1);
                    resultMap.put(mapKey, result);
                    daySlaveDictMap.put(mapKey,map.get("quotaDate").toString());
                }
                TjQuotaDimensionSlave tjQuotaDimensionSlave = new TjQuotaDimensionSlave();
                tjQuotaDimensionSlave.setQuotaCode(quotaVo.getCode());
                qds.add(tjQuotaDimensionSlave);
                extractUtil.compute(qdm, qds, returnList, resultMap,daySlaveDictMap,quotaVo);
            }
        }
        return returnList;

    }


    public void initParams(String startTime, String endTime) {
        // 初始执行指标，起止日期没有值
        this.startTime = startTime == null ? null : startTime.substring(0,10);
        String now = DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_YMD_FORMAT);
        this.endTime = endTime == null ? now : endTime.substring(0,10);;
    }

    /**
     * @param tjQuotaDimensionMains
     * @param tjQuotaDimensionSlaves
     * @return
     */
    private String getSql(List<TjQuotaDimensionMain> tjQuotaDimensionMains, List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves) {
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
        //拼接where语句 和 分组字段
        StringBuffer whereSql = new StringBuffer();
        whereSql.append(" where 1=1");
        if (!StringUtils.isEmpty(esConfig.getFilter())) {
            whereSql.append(" and " + esConfig.getFilter());
        }
        String selectGroupField = allField.toString();
        String whereGroupField = allField.toString();
        String timeKey = esConfig.getTimekey();
        if ( !StringUtils.isEmpty(timeKey)) {
            if ( !StringUtils.isEmpty(esConfig.getFullQuery() ) && esConfig.getFullQuery().equals("true")) {
                whereSql.append( " and " + timeKey + " < '" + endTime + "'");
                selectGroupField += " '"+ LocalDate.now().toString() +"' as quotaDate ,";
            }else{
                if ( !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                    whereSql.append(" and " + timeKey + " >= '" + startTime + "'");
                    whereSql.append( " and " + timeKey + " < '" + endTime + "'");
                }
                selectGroupField += " DATE_FORMAT(" + timeKey + ",'%Y-%m-%d') as quotaDate ,";
            }
            whereGroupField += timeKey;
        }else{
            whereGroupField = allField.substring(0,allField.length() - 1);
        }
        //拼接整个sql 语法
        StringBuffer sql = new StringBuffer();
        if(StringUtils.isEmpty(esConfig.getAggregation())){
            sql.append("select " + selectGroupField + " count(*) result from " + tableName + whereSql + " group by " + whereGroupField);
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
