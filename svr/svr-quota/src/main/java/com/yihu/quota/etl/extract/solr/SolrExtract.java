package com.yihu.quota.etl.extract.solr;

import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
                                   String timeLevel, // 时间维度，默认且只按天统计
                                   QuotaVo quotaVo,//指标code
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

    public List<SaveModel> statiscSlor(List<TjQuotaDimensionMain> qdm,
                                       List<TjQuotaDimensionSlave> qds,
                                       QuotaVo quotaVo) throws Exception {
        List<SaveModel> returnList = new ArrayList<>();

        String core = esConfig.getTable(); // solr的core名
        String q = null; // 查询条件
        String fq = null; // 过滤条件
        List<SolrGroupEntity> dimensionGroupList = new ArrayList<>(); // 维度分组统计条件
        String timeKey = esConfig.getTimekey();

        Map<String, String> mainMap = new HashMap<>();
        Map<String, String> slaveMap = new HashMap<>();
        for (int i = 0; i < qdm.size(); i++) {
            String key = qdm.get(i).getKeyVal();
            String mainCode = qdm.get(i).getMainCode();
            mainMap.put(key, key);
            dimensionGroupList.add(new SolrGroupEntity(key, SolrGroupEntity.GroupType.FIELD_VALUE, null));
        }
        for (int i = 0; i < qds.size(); i++) {
            String key = qds.get(i).getKeyVal();
            slaveMap.put(key, key);
            dimensionGroupList.add(new SolrGroupEntity(key, SolrGroupEntity.GroupType.FIELD_VALUE, null));
        }
        // 默认追加一个日期字段作为细维度，方便按天统计数据作为最小单位统计值。
        dimensionGroupList.add(new SolrGroupEntity(timeKey, SolrGroupEntity.GroupType.DATE_RANGE, null));
        slaveMap.put(timeKey, timeKey);
        TjQuotaDimensionSlave daySlave = new TjQuotaDimensionSlave();
        daySlave.setSlaveCode(timeKey);
        qds.add(daySlave);

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

        // 最后一个维度基于其他维度组合作为条件的统计结果的集合
        List<Map<String, Object>> list = solrQuery.getGroupMultList(core, dimensionGroupList, null, q, fq);
        Map<String, Integer> countsMap = new LinkedHashMap<>(); // 统计结果集
        Map<String, String> daySlaveDictMap = new LinkedHashMap<>(); // 按天统计的所有日期项
        if (list != null && list.size() > 0) {
            for (Map<String, Object> objectMap : list) {
                String countKey = objectMap.get("$countKey").toString();
                Integer count = Integer.parseInt(objectMap.get("$count").toString());
                String quotaDate = objectMap.get(timeKey).toString();
                countsMap.put(countKey, count);
                daySlaveDictMap.put(countKey, quotaDate);
            }
        }

        // 融合主细维度、其组合统计值为SaveModel
        extractUtil.compute(qdm, qds, returnList, countsMap, daySlaveDictMap, quotaVo);

        return returnList;
    }

}
