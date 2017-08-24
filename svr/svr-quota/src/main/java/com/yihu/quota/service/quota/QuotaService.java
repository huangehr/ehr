package com.yihu.quota.service.quota;

import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ReultModel;
import com.yihu.quota.util.QuartzHelper;
import com.yihu.quota.vo.QuotaVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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

    public TjQuota findOne(int id){
        return quotaDao.findOne(id);
    }

    public List<Map<String, Object>> queryResultPage(Integer id,String filters ,int pageNo,int pageSize) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        return  esResultExtract.queryResultPage(tjQuota, filters, pageNo, pageSize);
    }


    public long getQuotaTotalCount(Integer id,String filters) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        long count = esResultExtract.getQuotaTotalCount(tjQuota,filters);
        return count;
    }

    public List<Map<String, Object>> searcherByGroup(Integer id,String filters,String aggsField ) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        return  esResultExtract.searcherByGroup(tjQuota, filters,aggsField );
    }


    public Map<String, Integer> searcherByGroupBySql(Integer id,String aggsField ,String filters ) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        return  esResultExtract.searcherByGroupBySql(tjQuota,aggsField,filters);
    }

    //多列
    public QuotaReport getQuotaReport(Integer id, String filters,String dimension,int size) throws Exception {
        String[] dimensions = null;
        if(StringUtils.isNotEmpty(dimension)){
          dimensions = dimension.split(";");
        }else{
            dimensions = new String[]{"quotaDate"};
        }
        TjQuota tjQuota= quotaDao.findOne(id);
        QuotaReport quotaReport = new QuotaReport();
        List<Map<String, Object>> listMap = esResultExtract.getQuotaReport(tjQuota, filters,size);
        List<ReultModel> reultModelList = new ArrayList<>();
        for(int i=0 ; i< listMap.size() ;i++){
            Object resultVal = listMap.get(i).get("result");
            //多个列
            List<String> cloumns = new ArrayList<>();
            String nameVal = null;
            for(int k=0 ; k <dimensions.length ; k++){
                if(dimensions[k].equals("quotaDate")){
                    nameVal = listMap.get(i).get(dimensions[k]).toString();
                }else{
                    nameVal = listMap.get(i).get(dimensions[k]+"Name").toString();
                }
                cloumns.add(nameVal);
            }
            boolean repeat = false;
            ReultModel oldresult = null;
            for(ReultModel result:reultModelList){
                if(result.getCloumns().equals(cloumns)){
                    repeat = true;
                    oldresult = result;
                }
            }
            ReultModel reultModel = new ReultModel();

            if( !repeat){
                reultModel.setCloumns(cloumns);
                reultModel.setValue(resultVal);
                reultModelList.add(reultModel);
            }else {
                //如果有重复 先删除listl里面的数据，然后添加新数据
                reultModelList.remove(oldresult);
                reultModel.setCloumns(cloumns);
                Object totalResultVal = ( (Integer) resultVal + (Integer)oldresult.getValue());
                reultModel.setValue(totalResultVal);
                reultModelList.add(reultModel);
            }

        }
        quotaReport.setReultModelList(reultModelList);
        quotaReport.setTjQuota(tjQuota);
        return quotaReport;
    }

    public List<Map<String, Object>> getOpetionData(List<Map<String, Object>> resultList ){
        List<Map<String, Object>> dataList = new ArrayList<>();
        for(Map<String, Object> reultModel:resultList){
            Map<String, Object> map = new HashMap<>();
            if(reultModel.get("orgName") !=null){
                map.put("NAME",reultModel.get("orgName"));
            }else  if(reultModel.get("townName") !=null){
                map.put("NAME",reultModel.get("townName"));
            }else  if(reultModel.get("cityName") !=null){
                map.put("NAME",reultModel.get("cityName"));
            }else  if(reultModel.get("cityName") !=null){
                map.put("NAME",reultModel.get("cityName"));
            }else {
                map.put("NAME",reultModel.get("slaveKey1Name"));
            }
            map.put("TOTAL",reultModel.get("result"));
            dataList.add(map);
        }
        return dataList;
    }






}
