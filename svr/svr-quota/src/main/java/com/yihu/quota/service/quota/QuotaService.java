package com.yihu.quota.service.quota;

import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ResultModel;
import com.yihu.quota.service.source.TjDataSourceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author janseny
 */
@Service
public class QuotaService {
    @Autowired
    private TjQuotaDao quotaDao;
    @Autowired
    private EsResultExtract esResultExtract;
    @Autowired
    private TjDataSourceService dataSourceService;

    public TjQuota findOne(int id){
        return quotaDao.findOne(id);
    }

    public TjQuota findByCode(String code){
        return quotaDao.findByCode(code);
    }

    public List<Map<String, Object>> queryResultPage(Integer id,String filters ,int pageNo,int pageSize) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        return  esResultExtract.queryResultPage(tjQuota, filters, pageNo, pageSize);
    }

    public List<Map<String, Object>> queryResultPageByCode(String code,String filters ,int pageNo,int pageSize) throws Exception {
        TjQuota tjQuota= quotaDao.findByCode(code);
        return  esResultExtract.queryResultPage(tjQuota, filters, pageNo, pageSize);
    }

    public long getQuotaTotalCount(Integer id,String filters) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        long count = esResultExtract.getQuotaTotalCount(tjQuota,filters);
        return count;
    }

    //指标分组统计数量 - 只支持一个字段
    public List<Map<String, Object>> searcherByGroup(TjQuota tjQuota,String filters,String aggsField ) throws Exception {
        return  esResultExtract.searcherByGroup(tjQuota, filters,aggsField );
    }

    //根据mysql 指标分组求和 支持一个和多个字段
    public Map<String, Integer> searcherSumByGroupBySql(TjQuota tjQuota,String aggsField ,String filters,String sumField ,String orderFild,String order) throws Exception {
        return  esResultExtract.searcherSumByGroupBySql(tjQuota, aggsField, filters,sumField,orderFild,order);
    }

    //多维度 数据的总和
    public QuotaReport getQuotaReport(TjQuota tjQuota, String filters,String dimension,int size) throws Exception {
        String[] dimensions = null;
        if(StringUtils.isNotEmpty(dimension)){
          dimensions = dimension.split(";");
        }else{
            dimensions = new String[]{"quotaDate"};
        }
        QuotaReport quotaReport = new QuotaReport();
        List<ResultModel> reultModelList = new ArrayList<>();
        List<Map<String, Object>> listMap = esResultExtract.getQuotaReport(tjQuota, filters,size);
        if(listMap != null && listMap.size() >0){
            for(int i=0 ; i< listMap.size() ;i++){
                Object resultVal = listMap.get(i).get("result");
                //多个列
                List<String> cloumns = new ArrayList<>();
                String nameVal = null;
                for(int k=0 ; k <dimensions.length ; k++){
                    if(dimensions[k].equals("quotaDate")){
                        nameVal = listMap.get(i).get(dimensions[k]).toString();
                    }else{
                        if(null != listMap.get(i).get(dimensions[k]+"Name")){
                            nameVal = listMap.get(i).get(dimensions[k]+"Name").toString();
                        }else {
                            nameVal = listMap.get(i).get(dimensions[k]).toString();
                        }
                    }
                    cloumns.add(nameVal);
                }
                boolean repeat = false;
                ResultModel oldresult = null;
                for(ResultModel result:reultModelList){
                    if(result.getCloumns().equals(cloumns)){
                        repeat = true;
                        oldresult = result;
                    }
                }
                ResultModel reultModel = new ResultModel();
                if( !repeat){
                    reultModel.setCloumns(cloumns);
                    reultModel.setValue(resultVal);
                    reultModelList.add(reultModel);
                }else {
                    //如果有重复 先删除listl里面的数据，然后添加新数据
                    reultModelList.remove(oldresult);
                    reultModel.setCloumns(cloumns);
                    Object totalResultVal = ( Double.valueOf(resultVal.toString()) + Double.valueOf(oldresult.getValue().toString()) );
                    reultModel.setValue(totalResultVal);
                    reultModelList.add(reultModel);
                }
            }
        }
        quotaReport.setReultModelList(reultModelList);
        quotaReport.setTjQuota(tjQuota);
        return quotaReport;
    }


    //多维度数据的总和 返回数据结果集
    //返回结果 ：key为共同维度的组合值，value为数据结果集
    public Map<String,Map<String, Object>> getQuotaResult(Integer id, String filters,String dimension) throws Exception {

        String[] dimensions = null;
        if(StringUtils.isNotEmpty(dimension)){
            dimensions = dimension.split(";");
        }else{
            dimensions = new String[]{"quotaDate"};
        }
        TjQuota tjQuota= quotaDao.findOne(id);
        List<Map<String, Object>> resultListMap = esResultExtract.getQuotaReport(tjQuota, filters,10000);

        Map<String,Map<String, Object>> cloumnMap = new HashMap<>();

        for(int i=0 ; i < resultListMap.size() ;i++){
            Map<String, Object> resultMap = resultListMap.get(i);
            Object resultVal = resultMap.get("result");

            String cloumnStr = "";//多个列 值 拼接串
            for(int k=0 ; k < dimensions.length ; k++){
                String nameVal = resultMap.get(dimensions[k]).toString();
                cloumnStr = cloumnStr + nameVal + "-";
            }
            boolean repeat = false;
            Object oldResultVal = null;
            if( cloumnMap.size() == 0 ){//第一个
                cloumnMap.put(cloumnStr,resultMap);
            }else {
                for(String key:cloumnMap.keySet()){
                    if(cloumnStr.equals(key)){
                        repeat = true;
                        Map<String, Object> oldMap = cloumnMap.get(key);
                        oldResultVal = oldMap.get("result");
                    }
                }
            }
            if( !repeat){
                cloumnMap.put(cloumnStr,resultMap);
            }else {
                //如果有重复 先计删除listl里面的数据，再添加新数据
                cloumnMap.remove(cloumnStr);
                resultMap.remove("result");
                Object newResultVal = ( Integer.valueOf(resultVal.toString()) + Integer.valueOf(oldResultVal.toString()) );
                resultMap.put("result",newResultVal);
                cloumnMap.put(cloumnStr, resultMap);
            }


        }
        return cloumnMap;
    }

    //多维度 数据的总和
    public QuotaReport getQuotaReportGeneral( TjQuota tjQuota,String filters,String dimension,int size) throws Exception {
        String[] dimensions = null;
        if(StringUtils.isNotEmpty(dimension)){
            dimensions = dimension.split(";");
        }else{
            dimensions = new String[]{"quotaDate"};
        }
        QuotaReport quotaReport = new QuotaReport();
        List<Map<String, Object>> listMap = esResultExtract.getQuotaReport(tjQuota, filters,size);
        List<ResultModel> reultModelList = new ArrayList<>();
        for(int i=0 ; i< listMap.size() ;i++){
            Object resultVal = listMap.get(i).get("result");
            //多个列
            List<String> cloumns = new ArrayList<>();
            String nameVal = null;
            for(int k=0 ; k <dimensions.length ; k++){
                nameVal = listMap.get(i).get(dimensions[k]).toString();
                cloumns.add(nameVal);
            }
            boolean repeat = false;
            ResultModel oldresult = null;
            for(ResultModel result:reultModelList){
                if(result.getCloumns().equals(cloumns)){
                    repeat = true;
                    oldresult = result;
                }
            }
            ResultModel reultModel = new ResultModel();
            if( !repeat){
                reultModel.setCloumns(cloumns);
                reultModel.setValue(resultVal);
                reultModelList.add(reultModel);
            }else {
                //如果有重复 先删除listl里面的数据，然后添加新数据
                reultModelList.remove(oldresult);
                reultModel.setCloumns(cloumns);
                Object totalResultVal = ( Integer.valueOf(resultVal.toString()) + Integer.valueOf(oldresult.getValue().toString()) );
                reultModel.setValue(totalResultVal);
                reultModelList.add(reultModel);
            }
        }
        quotaReport.setReultModelList(reultModelList);
        quotaReport.setTjQuota(tjQuota);
        return quotaReport;
    }


    public  List<Map<String, Object>> getQuotaResultList(TjQuota tjQuota,String filters) throws Exception {
        List<Map<String, Object>> resultListMap = esResultExtract.getQuotaReport(tjQuota, filters,10000);
        return  resultListMap;
    }


    /**
     * 获取指标报表结果集
     * @param code
     * @param filters
     * @param dimension
     */
    public void getQuotaReportResult(String code, String filters, String dimension) {
        TjQuota tjQuota= quotaDao.findByCode(code);
        TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(code);

    }
}
