package com.yihu.quota.service.quota;

import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ReultModel;
import com.yihu.quota.util.QuartzHelper;
import com.yihu.quota.vo.QuotaVo;
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

    public QuotaReport getQuotaReport(Integer id, String filters) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        QuotaReport quotaReport = new QuotaReport();
        List<Map<String, Object>> listMap = esResultExtract.getQuotaReport(tjQuota, filters);
        List<ReultModel> reultModelList = new ArrayList<>();
        for(int i=0 ; i< listMap.size() ;i++){
            ReultModel reultModel = new ReultModel();
            if(tjQuota.getCode().contains("depart_treat")||tjQuota.getCode().contains("disease")){
                if(listMap.get(i).get("slaveKey2Name") !=null){
                    reultModel.setKey(listMap.get(i).get("slaveKey2Name").toString());
                }else {
                    break;
                }
            }else if(tjQuota.getCode().contains("age")){
                if(listMap.get(i).get("slaveKey1Name") !=null){
                    reultModel.setKey(listMap.get(i).get("slaveKey1Name").toString());
                }else{
                    break;
                }
            }else {
                reultModel.setKey(listMap.get(i).get("quotaDate").toString());
            }
            reultModel.setValue(listMap.get(i).get("result"));
            reultModelList.add(reultModel);
        }
        quotaReport.setReultModelList(reultModelList);
        quotaReport.setTjQuota(tjQuota);
        return quotaReport;
    }

}
