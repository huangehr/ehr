package com.yihu.quota.etl.extract.solr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.extract.mysql.MysqlExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.model.jpa.source.TjDataSource;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.save.TjDataSaveService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by janseny on 2017/08/22.
 */
@Component
@Scope("prototype")
public class ExtractPercentHelper {
    @Autowired
    private TjDataSourceService dataSourceService;
    @Autowired
    private TjDataSaveService datsSaveService;
    @Autowired
    private TjDimensionMainService dimensionMainService;
    @Autowired
    private TjDimensionSlaveService dimensionSlaveService;
    @Autowired
    private EsExtract esExtract;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    EsClientUtil esClientUtil;
    @Autowired
    ObjectMapper objectMapper;


    private Logger logger = LoggerFactory.getLogger(ExtractPercentHelper.class);

    /**
     * 公共的抽取数据
     *
     * @param quotaVo
     * @return
     * @throws Exception
     */
    public List<SaveModel> extractData(QuotaVo quotaVo, String startTime, String endTime,String timeLevel) throws Exception {
        try {
            //得到该指标的数据来源
            TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(quotaVo.getCode());
            //如果为空说明数据错误
            if (quotaDataSource == null) {
                throw new Exception("QuotaDataSource data error");
            }
            JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
            EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
            if(StringUtils.isNotEmpty(esConfig.getMolecular()) ){
                //得到主维度
                List<TjQuotaDimensionMain> tjQuotaDimensionMains = dimensionMainService.findTjQuotaDimensionMainByQuotaIncudeAddress(esConfig.getMolecular());
                //得到细维度
                List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves = dimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(esConfig.getMolecular());

                List<String> dimensionList = new ArrayList<>();
                for(TjQuotaDimensionMain main:tjQuotaDimensionMains){
                    dimensionList.add(main.getMainCode());
                }

                for(int i=1;i <= tjQuotaDimensionMains.size();i++){
                    dimensionList.add("slaveKey"+i);
                }

                List<Map<String, Object>> molecularList = getQuotaData(esConfig.getMolecular(),esConfig,100000);
                List<Map<String, Object>> denominatorList =  getQuotaData(esConfig.getDenominator(),esConfig,100000);

                List<Map<String, Object>> datalist = getPercentResult(molecularList,denominatorList,dimensionList);
                List<SaveModel> saveModelList = new ArrayList<>();
                for(Map<String, Object> map : datalist){
                    SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                    saveModelList.add(saveModel);
                }

//                List<SaveModel> dictData = jdbcTemplate.query(quotaDimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));

                return saveModelList;
            }else{
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error("extract error:" + e.getMessage());
            logger.error("quotaVOr:" + quotaVo.toString());
        }
        return null;
    }

    private List<Map<String, Object>> getQuotaData(String quotaCode,EsConfig esConfig,int size){
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery("quotaCode", quotaCode);
        boolQueryBuilder.must(termQueryQuotaCode);
        esClientUtil.addNewClient(esConfig.getHost(),esConfig.getPort(),esConfig.getClusterName());
        List<Map<String, Object>> list = elasticsearchUtil.queryList(esClientUtil.getClient(esConfig.getClusterName()),boolQueryBuilder, "quotaDate",size);
        return  list;
    }

    private List<Map<String, Object>> getPercentResult(List<Map<String, Object>> molecularList,List<Map<String, Object>> denominatorList,List<String> dimensionList){
        List<Map<String, Object>> resultList  = new ArrayList<>();
        Iterator<Map<String, Object>> denominator =  denominatorList.iterator();
        while(denominator.hasNext()){
            Map<String, Object> tempMap = denominator.next();
            Map<String, Object> resultMap = new HashMap<>();
            String sameDime = "";
            for(String dime:dimensionList){
                resultMap.put(dime,tempMap.get(dime));
                sameDime = sameDime + tempMap.get(dime) + "-";
            }
            if(tempMap.get("result").toString().equals("0")){
                resultMap.put("result",0);
            }else {
                int mole = 0;
                int deno = (int)tempMap.get("result");
                for(Map<String, Object> moleMap : molecularList){
                    String moleDime = "";
                    //找到维度相同一条数据
                    for(String key :moleMap.keySet()){
                        moleDime = moleDime + moleMap.get(key) + "-";
                    }
                    if (moleDime.equals(sameDime)){
                        mole = (int)tempMap.get("result");
                        break;
                    }
                }
                DecimalFormat df = new DecimalFormat("0.00");
                String point ="0.00";
                point = df.format(((float)mole/(float)deno)*100)+"%";
                resultMap.put("result",point);
                resultList.add(resultMap);
            }
        }
        return resultList;
    }

}
