package com.yihu.quota.etl.extract.solr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.dao.jpa.save.TjQuotaDataSaveDao;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.ExtractConverUtil;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.vo.FilterModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 对solr抽取数据，基于指标配置维度(不能包括按周、月、年等时间维度) +
 * 默认按天维度统计值作为最小单位分组聚合，
 * 保存聚合结果到ES。二次统计基于以上的聚合结果进行统计。
 * <p>
 * Created by janseny on 2017/7/10.
 */
@Component
@Scope("prototype")
public class SolrExtract {

    private Logger logger = LoggerFactory.getLogger(SolrExtract.class);

    @Autowired
    private ExtractUtil extractUtil;
    @Autowired
    private SolrQuery solrQuery;
    @Autowired
    private ExtractConverUtil extractConverUtil;
    @Autowired
    private ElasticSearchUtil esUtil;
    @Autowired
    private TjQuotaDataSaveDao tjQuotaDataSaveDao;
    @Autowired
    private ObjectMapper objectMapper;

    private QuotaVo quotaVo;
    private String startTime;
    private String endTime;
    private String timeLevel;
    private EsConfig esConfig;

    public List<SaveModel> extract(List<TjQuotaDimensionMain> qdm,//主维度
                                   List<TjQuotaDimensionSlave> qds,//细维度
                                   String startTime,//开始时间
                                   String endTime, //结束时间
                                   String timeLevel, // 时间维度，默认且只按天统计
                                   QuotaVo quotaVo,//指标配置
                                   EsConfig esConfig //es配置
    ) throws Exception {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeLevel = timeLevel;
        this.quotaVo = quotaVo;
        this.esConfig = esConfig;
        solrQuery.initParams(this.startTime, this.endTime);

        // 统计数据
        return statiscSlor(qdm, qds, quotaVo);
    }

    public int getExtractTotal(String startTime, String endTime,  EsConfig esConfig) throws Exception {
        this.startTime = startTime;
        this.endTime = endTime;
        this.esConfig = esConfig;
        solrQuery.initParams(this.startTime, this.endTime);
        String core = esConfig.getTable(); // solr的core名
        String q = null; // 过滤条件
        // 统计数据数量
        String timeKey = esConfig.getTimekey();
        if (!StringUtils.isEmpty(timeKey)) {
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                q = String.format("%s:[%s TO %s]", timeKey, startTime, endTime);
            } else {
                q = timeKey + ":[* TO *]";
            }
        }
        long rows = solrQuery.count(core, q + " AND " + esConfig.getFilter());
        return Integer.valueOf(String.valueOf(rows));
    }



    public List<SaveModel> statiscSlor(List<TjQuotaDimensionMain> qdm,
                                       List<TjQuotaDimensionSlave> qds,
                                       QuotaVo quotaVo) throws Exception {
        List<SaveModel> returnList = new ArrayList<>();

        String core = esConfig.getTable(); // solr的core名
        String q = null; // 查询条件
        String fq = null; // 过滤条件
        String fl = ""; // 结果指定查询字段
        List<SolrGroupEntity> dimensionGroupList = new ArrayList<>(); // 维度分组统计条件
        if (StringUtils.isEmpty(esConfig.getTimekey())) {
            throw new Exception("数据源配置 timeKey 不能为空！");
        }
        String timeKey = esConfig.getTimekey();

        Map<String, String> mainMap = new HashMap<>();
        Map<String, String> slaveMap = new HashMap<>();
        for (int i = 0; i < qdm.size(); i++) {
            String key = qdm.get(i).getKeyVal();
            mainMap.put(key, key);
            dimensionGroupList.add(new SolrGroupEntity(key, SolrGroupEntity.GroupType.FIELD_VALUE));
            fl += key + ",";
        }
        for (int i = 0; i < qds.size(); i++) {
            String key = qds.get(i).getKeyVal();
            slaveMap.put(key, key);
            dimensionGroupList.add(new SolrGroupEntity(key, SolrGroupEntity.GroupType.FIELD_VALUE));
            fl += key + ",";
        }
        fl += timeKey + ",rowkey";

        if (StringUtils.isEmpty(esConfig.getAggregation()) || !esConfig.getAggregation().equals(Contant.quota.aggregation_list)) {
            // 默认追加一个日期字段作为细维度，方便按天统计作为最小单位统计值。
            slaveMap.put(timeKey, timeKey);
            TjQuotaDimensionSlave daySlave = new TjQuotaDimensionSlave();
            daySlave.setSlaveCode(timeKey);
            qds.add(daySlave);
            dimensionGroupList.add(new SolrGroupEntity(timeKey, SolrGroupEntity.GroupType.DATE_RANGE, "+1DAY"));
        }

        // 拼接增量或全量的筛选条件
        if (!StringUtils.isEmpty(timeKey)) {
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                fq = String.format("%s:[%s TO %s]", timeKey, startTime, endTime);
            } else {
                fq = timeKey + ":[* TO *]";
            }
        }

        // 拼接过滤条件
        if (esConfig.getFilter() != null) {
            if (StringUtils.isEmpty(fq)) {
                fq = esConfig.getFilter();
            } else {
                fq += " AND " + esConfig.getFilter();
            }
        }

        boolean listFlag = false;
        // 最后一个维度基于其他维度组合作为条件的统计结果的集合
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isEmpty(esConfig.getAggregation())
                || Contant.quota.aggregation_count.equals(esConfig.getAggregation())) {
            // count 聚合
            list = solrQuery.getCountMultList(core, q, fq, dimensionGroupList, null);
        } else if (!StringUtils.isEmpty(esConfig.getAggregationKey()) && Contant.quota.aggregation_sum.equals(esConfig.getAggregation())) {
            // sum 聚合
            list = solrQuery.getSumMultList(core, q, fq, esConfig.getAggregationKey(), dimensionGroupList, null);
        } else if (Contant.quota.aggregation_list.equals(esConfig.getAggregation())) {
            listFlag = true;
            // 查询列表
            try {
                if (esConfig.getAggregation().equals(Contant.quota.aggregation_list) && !StringUtils.isEmpty(esConfig.getAggregationKey())) {
                    fl = fl + "," + esConfig.getAggregationKey();
                }
                list = solrQuery.queryReturnFieldList(core, q, fq, null, quotaVo.getStart(), quotaVo.getRows(), fl.split(","), null, null);
            } catch (Exception e) {
                throw new Exception("solr 查询异常 " + e.getMessage());
            }
        } else if (Contant.quota.aggregation_distinct.equals(esConfig.getAggregation())) {
            listFlag = true;
            // 去重查询
            fl += "," + esConfig.getDistinctGroupField();
            list = solrQuery.queryReturnFieldList(core, q, fq, null, 0, -1, fl.split(","), esConfig.getDistinctGroupField(), esConfig.getDistinctGroupSort());
            // 对比ES中如果已存在该条数据则更新，并从集合中移除该条数据。
            checkEsDistinctData(list);
        }

        //数据转换
        FilterModel filterModel = new FilterModel(list, null);
        filterModel = extractConverUtil.convert(filterModel, qds);
        if (filterModel != null && filterModel.getDataList() != null) {
            list = filterModel.getDataList();
        }
        Map<String, String> statisticsResultMap = new LinkedHashMap<>(); // 统计结果集
        Map<String, String> daySlaveDictMap = new LinkedHashMap<>(); // 按天统计的所有日期项
        if (listFlag) {
            if (list != null && list.size() > 0) {
                returnList = extractUtil.computeList(qdm, qds, list, timeKey, esConfig.getAggregationKey(), quotaVo);
            }
        } else {
            if (list != null && list.size() > 0) {
                for (Map<String, Object> objectMap : list) {
                    String statisticsKey = objectMap.get("$statisticsKey").toString();
                    String result = objectMap.get("$result").toString();
                    String quotaDate = objectMap.get(timeKey).toString();
                    statisticsResultMap.put(statisticsKey, result);
                    daySlaveDictMap.put(statisticsKey, quotaDate);
                }
            }
            // 融合主细维度、其组合统计值为SaveModel
            extractUtil.compute(qdm, qds, returnList, statisticsResultMap, daySlaveDictMap, quotaVo);
        }
        return returnList;
    }

    /**
     * 去重查询后，对比ES中如果已存在该条数据则更新，并从集合中移除该条数据。
     *
     * @param distinctQueryList 去重查询结果集
     */
    private void checkEsDistinctData(List<Map<String, Object>> distinctQueryList) throws IOException {
        List<Map<String, Object>> updateList = new ArrayList<>();

        String esIndex = null;
        String esType = null;
        if (distinctQueryList.size() > 0) {
            TjQuotaDataSave quotaDataSave = tjQuotaDataSaveDao.findByQuotaCode(quotaVo.getCode());
            Map<String, Object> configJson = objectMapper.readValue(quotaDataSave.getConfigJson(), Map.class);
            esIndex = configJson.get("index").toString();
            esType = configJson.get("type").toString();
        }

        Iterator<Map<String, Object>> iterator = distinctQueryList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> item = iterator.next();
            String distinctField = item.get("distinctField").toString();
            String distinctFieldValue = item.get("distinctFieldValue").toString();

            String filters = "quotaCode=" + quotaVo.getCode() + ";distinctField=" + distinctField + ";distinctFieldValue=" + distinctFieldValue;
            List<Map<String, Object>> existedList = esUtil.list(esIndex, esType, filters);
            if (existedList.size() > 0) {
                Map<String, Object> quotaData = existedList.get(0);
                updateList.add(quotaData);
                iterator.remove();
            }
        }

        if (updateList.size() != 0) {
            esUtil.bulkUpdate(esIndex, esType, updateList);
        }
    }

}
