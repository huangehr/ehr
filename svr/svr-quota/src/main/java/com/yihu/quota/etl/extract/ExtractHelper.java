package com.yihu.quota.etl.extract;

import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.extract.mysql.MysqlExtract;
import com.yihu.quota.etl.extract.solr.SolrExtract;
import com.yihu.quota.etl.model.EsConfig;
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
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenweida on 2017/6/1.
 */
@Component
@Scope("prototype")
public class ExtractHelper {
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
    private SolrExtract solrExtract;

    private Logger logger = LoggerFactory.getLogger(ExtractHelper.class);

    public EsConfig getEsConfig(String quotaCode) throws Exception {
        //得到该指标的数据存储位置
        TjQuotaDataSave quotaDataSave = datsSaveService.findByQuota(quotaCode);
        //如果为空说明数据错误
        if (quotaDataSave == null) {
            throw new Exception("QuotaDataSave data error");
        }
        JSONObject obj = new JSONObject().fromObject(quotaDataSave.getConfigJson());
        EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
        return  esConfig;
    }

    public EsConfig getDataSourceEsConfig(String quotaCode) throws Exception {
        TjQuotaDataSource quotaDataSource= dataSourceService.findSourceByQuotaCode(quotaCode);
        if (quotaDataSource == null) {
            throw new Exception("quotaDataSource data error");
        }
        JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
        EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
        return  esConfig;
    }

    /**
     * 抽取数据统计值
     *
     * @param quotaVo
     * @return
     * @throws Exception
     */
    public List<SaveModel> extractData(QuotaVo quotaVo, String startTime, String endTime,String timeLevel,String saasid) throws Exception {
        try {
            TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(quotaVo.getCode());
            if (quotaDataSource == null) {
                throw new Exception("数据源配置错误");
            }
            JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
            EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
            //得到主维度
            List<TjQuotaDimensionMain> tjQuotaDimensionMains = dimensionMainService.findTjQuotaDimensionMainByQuotaCode(quotaDataSource.getQuotaCode());
            //当维度中有机构维度时，机构对应的区县会自动关联出来，所以区县不用在分组计算
            boolean orgFlag = false;
            TjQuotaDimensionMain townMain = null;
            for (int j = 0; j < tjQuotaDimensionMains.size(); j++) {
                TjQuotaDimensionMain one = tjQuotaDimensionMains.get(j);
                if ( one.getMainCode().equals("org")) {
                    orgFlag = true;
                }
                if ( one.getMainCode().equals("town")) {
                    townMain = one;
                }
            }
            if(orgFlag){
                tjQuotaDimensionMains.remove(townMain);
            }

            //得到细维度
            List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves = dimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(quotaDataSource.getQuotaCode());

            if ( TjDataSource.type_es.equals(quotaDataSource.getCode()) ) {
                // 抽取 ES 统计值
                if( (!StringUtils.isEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals("orgHealthCategory")){
                    return esExtract.extractOrgHealthCategory(tjQuotaDimensionMains,tjQuotaDimensionSlaves,startTime,endTime,timeLevel,saasid, quotaVo,esConfig);
                }else{
                    return esExtract.extract(tjQuotaDimensionMains,tjQuotaDimensionSlaves,startTime,endTime,timeLevel,saasid, quotaVo,esConfig);
                }
            }else if( TjDataSource.type_solr.equals(quotaDataSource.getCode()) ){
                // 抽取 solr 统计值
                return  solrExtract.extract(tjQuotaDimensionMains,tjQuotaDimensionSlaves,startTime,endTime,timeLevel, quotaVo,esConfig);
            }else if( TjDataSource.type_mysql.equals(quotaDataSource.getCode()) ){
                // 抽取 mysql 统计值
                return  SpringUtil.getBean(MysqlExtract.class).extract(tjQuotaDimensionMains,tjQuotaDimensionSlaves,startTime,endTime,timeLevel, quotaVo,esConfig);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("数据抽取错误," + e.getMessage() );
        }
    }

    // 该方法没有被用到，在 ExtractUtil 中有相同方法，推测可替代。  -- 张进军 2018.1.10
    /*private Map<String, SaveModel> setAllSlaveData(Map<String, SaveModel> allData, List<DictModel> dictData) {
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

                    StringBuffer keyMethodName = new StringBuffer("setSlaveKey" + (i + 1));
                    StringBuffer nameMethodName = new StringBuffer("setSlaveKey" + (i + 1) + "Name");

                    SaveModel.class.getMethod(keyMethodName.toString(), String.class).invoke(saveModelTemp, dictOne.getCode());
                    SaveModel.class.getMethod(nameMethodName.toString(), String.class).invoke(saveModelTemp, dictOne.getName());
                    returnAllData.put(newKey.toString(), saveModelTemp);
                }
            }
            return returnAllData;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }*/

}
