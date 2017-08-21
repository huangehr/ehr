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


//    /**
//     * 单列
//     * @param id 指标ID
//     * @param filters 过滤查询条件
//     * @param dimension 返回的维度
//     * @return
//     * @throws Exception
//     */
//    public QuotaReport getQuotaReport(Integer id, String filters,String dimension) throws Exception {
//        TjQuota tjQuota= quotaDao.findOne(id);
//        QuotaReport quotaReport = new QuotaReport();
//        List<Map<String, Object>> listMap = esResultExtract.getQuotaReport(tjQuota, filters);
//        List<ReultModel> reultModelList = new ArrayList<>();
//        for(int i=0 ; i< listMap.size() ;i++){
//            Object resultVal = listMap.get(i).get("result");
//            String nameVal = listMap.get(i).get(dimension+"Name").toString();
//            boolean repeat = false;
//            ReultModel oldresult = null;
//            for(ReultModel result:reultModelList){
//                if(result.getKey().equals(nameVal)){
//                    repeat = true;
//                    oldresult = result;
//                }
//            }
//            ReultModel reultModel = new ReultModel();
//
//            if( !repeat){
//                if(StringUtils.isNotEmpty(dimension)){
//                    reultModel.setKey(nameVal);
//                }else {
//                    reultModel.setKey(listMap.get(i).get("quotaDate").toString());
//                }
//                reultModel.setValue(resultVal);
//                reultModelList.add(reultModel);
//            }else {
//                //如果有重复 先删除listl里面的数据，然后添加新数据
//                reultModelList.remove(oldresult);
//                reultModel.setKey(nameVal);
//                Object totalResultVal = ( (Integer) resultVal + (Integer)oldresult.getValue());
//                reultModel.setValue(totalResultVal);
//                reultModelList.add(reultModel);
//            }
//
//        }
//        quotaReport.setReultModelList(reultModelList);
//        quotaReport.setTjQuota(tjQuota);
//        return quotaReport;
//    }

    //多列
    public QuotaReport getQuotaReport(Integer id, String filters,String dimension) throws Exception {
        String[] dimensions = dimension.split(";");
        TjQuota tjQuota= quotaDao.findOne(id);
        QuotaReport quotaReport = new QuotaReport();
        List<Map<String, Object>> listMap = esResultExtract.getQuotaReport(tjQuota, filters);
        List<ReultModel> reultModelList = new ArrayList<>();
        for(int i=0 ; i< listMap.size() ;i++){
            Object resultVal = listMap.get(i).get("result");

            //多个列
            List<String> cloumns = new ArrayList<>();
            String nameVal = null;
            for(int k=0 ; k <dimensions.length ; k++){
                nameVal = listMap.get(i).get(dimensions[k]+"Name").toString();
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






}
