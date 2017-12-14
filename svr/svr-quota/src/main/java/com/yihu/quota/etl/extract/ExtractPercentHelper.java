package com.yihu.quota.etl.extract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ResultModel;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
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
    private QuotaService quotaService;
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
        String message = "";
        try {
            //得到该指标的数据来源
            TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(quotaVo.getCode());
            //如果为空说明数据错误
            if (quotaDataSource == null) {
                message = "数据源配置错误";
                throw new Exception(message);
            }
            JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
            EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);

            if(StringUtils.isEmpty(esConfig.getThousandFlag())){
                if(StringUtils.isNotEmpty(esConfig.getMolecular()) &&  StringUtils.isNotEmpty(esConfig.getDenominator())){
                    Map<String,DictModel> dimensionMap = getQuotaDimension(quotaDataSource.getQuotaCode());
                    Map<String,DictModel> moleDimensionMap = getQuotaDimension(esConfig.getMolecular());
                    Map<String,DictModel> denoDimensionMap = getQuotaDimension(esConfig.getDenominator());
                    List<String> quotaDimension = new ArrayList<>();
                    String moleDimension = "";
                    String denoDimension = "";
                    int num = 0;
                    int count = 0;
                    for(String key : dimensionMap.keySet()){
                        for(String molekey : moleDimensionMap.keySet()){
                            if(key.equals(molekey) && dimensionMap.get(key).getCode().equals( moleDimensionMap.get(molekey).getCode() )){
                                moleDimension = moleDimension + moleDimensionMap.get(molekey).getName() + ";";
                                num ++;
                            }
                        }
                        for(String denokey : denoDimensionMap.keySet()){
                            if(key.equals(denokey) && dimensionMap.get(key).getCode().equals( denoDimensionMap.get(denokey).getCode() )){
                                denoDimension = denoDimension + denoDimensionMap.get(denokey).getName() + ";";
                                count ++;
                            }
                        }
                        quotaDimension.add(dimensionMap.get(key).getName());
                    }
                    if(num != dimensionMap.size()){
                        message = "指标维度无法与分子指标维度匹配";
                        throw new Exception(message);
                    }

                    if(count != dimensionMap.size()){
                        message = "指标维度无法与分母指标维度匹配";
                        throw new Exception(message);
                    }

                    TjQuota moleTjQuota = quotaService.findByCode(esConfig.getMolecular());
                    TjQuota denoTjQuota = quotaService.findByCode(esConfig.getDenominator());

                    Map<String,String> param =  new HashMap<>();
                    param.put("startTime",startTime);
                    param.put("endTime",endTime);
                    Map<String,Map<String, Object>>  moleResultMap = quotaService.getQuotaResult(moleTjQuota.getId(), objectMapper.writeValueAsString(param), moleDimension.substring(0, moleDimension.length() - 1));
                    Map<String,Map<String, Object>>  denoResultMap = quotaService.getQuotaResult(denoTjQuota.getId(), objectMapper.writeValueAsString(param), denoDimension.substring(0, denoDimension.length() - 1));

                    List<SaveModel>  resultModel = getPercentResult(moleResultMap, denoResultMap,quotaVo);
                    return resultModel;
                }else{
                    message = "配置错误，分子或分母指标没有配置";
                    throw new Exception(message);
                }
            }else {
                // 每千每万人口 计算
                if(StringUtils.isNotEmpty(esConfig.getThousandDmolecular()) &&  StringUtils.isNotEmpty(esConfig.getThousandDenominator())){
                    Map<String,DictModel> dimensionMap = getQuotaDimension(quotaDataSource.getQuotaCode());
                    Map<String,DictModel> moleDimensionMap = getQuotaDimension(esConfig.getThousandDmolecular());
                    String moleDimension = "";
                    int num = 0;
                    for(String key : dimensionMap.keySet()){
                        for(String molekey : moleDimensionMap.keySet()){
                            if(key.equals(molekey)){
                                moleDimension = moleDimension + moleDimensionMap.get(molekey).getName() + ";";
                                num ++;
                            }
                        }
                    }
                    if(num != dimensionMap.size()){
                        message = "指标维度无法与分子指标维度匹配";
                        throw new Exception(message);
                    }

                    TjQuota moleTjQuota = quotaService.findByCode(esConfig.getThousandDmolecular());
                    TjQuota denoTjQuota = quotaService.findByCode(esConfig.getThousandDenominator());

                    Map<String,String> param =  new HashMap<>();
                    param.put("startTime",startTime);
                    param.put("endTime",endTime);
                    Map<String,Map<String, Object>>  moleResultMap = quotaService.getQuotaResult(moleTjQuota.getId(), objectMapper.writeValueAsString(param), moleDimension.substring(0, moleDimension.length() - 1));
                    int totalCount = 0;
                    Calendar calendar = Calendar.getInstance();
                    Map<String,Integer>  doneResultMap = quotaService.searcherSumByGroupBySql(denoTjQuota, "year", "year=" + calendar.get(Calendar.YEAR));
                    if(doneResultMap != null && doneResultMap.size()>0){
                        for(String key :doneResultMap.keySet())
                        totalCount = totalCount + doneResultMap.get(key);
                    }
                    List<SaveModel>  resultModel = new ArrayList<>();
                    if(moleResultMap != null && moleResultMap.size() > 0 && totalCount > 0){
                        resultModel = getThousandPercentResult(moleResultMap,totalCount, quotaVo,esConfig.getThousandFlag());
                    }
                    return resultModel;
                }else{
                    message = "配置错误，分子或分母指标没有配置";
                    throw new Exception(message);
                }
            }


        } catch (Exception e) {
            message = "数据抽取错误";
            throw new Exception(message);
        }
    }

    //获取指标维度
    public Map<String,DictModel> getQuotaDimension(String quotaCode){
        // 指标 主维度
        List<TjQuotaDimensionMain> dimensionMains = dimensionMainService.findTjQuotaDimensionMainByQuotaCode(quotaCode);
        // 指标 细维度
        List<TjQuotaDimensionSlave> dimensionSlaves = dimensionSlaveService.findTjQuotaDimensionSlaveByQuotaCode(quotaCode);
        Map<String,DictModel> map = new HashMap<>();
        String mainKey = "";
        String mainVal = "";
        for(int i = 0 ;i < dimensionMains.size() ; i++){
            mainKey =  dimensionMains.get(i).getMainCode();
            mainVal =  dimensionMains.get(i).getMainCode() + " - " + dimensionMains.get(i).getDictSql();
            DictModel dictModel = new DictModel();
            dictModel.setName(mainKey);
            dictModel.setCode(mainVal);
            map.put(mainKey,dictModel);
        }
        String slaveKey = "";
        String slaveVal = "";
        String slaveName = "";
        for(int i = 0 ;i< dimensionSlaves.size(); i++){
            slaveName = slaveName + "slaveKey" + (i+1);
            slaveKey =  dimensionSlaves.get(i).getSlaveCode();
            slaveVal =  dimensionSlaves.get(i).getSlaveCode() + " - " + dimensionSlaves.get(i).getDictSql() ;
            DictModel dictModel = new DictModel();
            dictModel.setName(slaveName);
            dictModel.setCode(slaveVal);
            map.put(slaveKey,dictModel);
        }
        return map;
    }

    public List<SaveModel> getPercentResult(Map<String,Map<String, Object>> moleReultMap,Map<String,Map<String, Object>> denoReultMap,QuotaVo quotaVo){

        List<SaveModel> saveModelList = new ArrayList<>();
        for(String dekey :denoReultMap.keySet()){
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> denoMap = denoReultMap.get(dekey);
            if(denoMap.get("result").toString().equals("0")){
                map = denoMap;
            }else {
                for(String mokey :moleReultMap.keySet()){
                    Map<String, Object> moleMap = moleReultMap.get(mokey);
                    if(dekey.equals(mokey)) {
                        if(moleMap.get("result").toString().equals("0")){
                            map = moleMap;
                       }else{
                           int point = 0;
                           float moleVal = Float.valueOf(moleMap.get("result").toString());
                           float denoVal = Float.valueOf(denoMap.get("result").toString());
                           point = (int)(moleVal/denoVal)*100;
                           moleMap.remove("result");
                           moleMap.put("result",point);
                           map = moleMap;
                       }
                        break;
                    }
                }
            }
            SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
            saveModel.setQuotaName(quotaVo.getName());
            saveModel.setQuotaCode(quotaVo.getCode());
            saveModelList.add(saveModel);
        }
        return saveModelList;
    }

    public List<SaveModel> getThousandPercentResult(Map<String,Map<String, Object>> moleReultMap,int totalCount,QuotaVo quotaVo,String thousandFlag){
        List<SaveModel> saveModelList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for(String mokey :moleReultMap.keySet()){
            Map<String, Object> moleMap = moleReultMap.get(mokey);
            if(moleMap.get("result").toString().equals("0")){
                map = moleMap;
                moleMap.put("result","0");
            }else{
                String point = "0";
                float moleVal = Float.valueOf(moleMap.get("result").toString());
                DecimalFormat   df = new   DecimalFormat("#.##");
                point = df.format( (moleVal / totalCount) * Integer.valueOf(thousandFlag));
                moleMap.put("result",point);
                map = moleMap;
            }
            SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
            saveModel.setQuotaName(quotaVo.getName());
            saveModel.setQuotaCode(quotaVo.getCode());
            saveModelList.add(saveModel);
        }
        return  saveModelList;
    }


}
