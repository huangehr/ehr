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

import java.util.*;

/**
 * @author chenweida
 */
@Service
public class QuotaService {
    @Autowired
    private TjQuotaDao quotaDao;
    @Autowired
    private EsResultExtract esResultExtract;


    public List<Map<String, Object>> getQuotaResult(Integer id,String filters ,int pageNo,int pageSize) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        return  esResultExtract.queryResultListBySql(tjQuota,filters,pageNo,pageSize);
    }

    public int getQuotaTotalCount(){
        return  esResultExtract.getQuotaTotalCount();
    }

    public QuotaReport getQuotaReport(Integer id, String filters) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        QuotaReport quotaReport = new QuotaReport();
        Map<String, Integer> map = esResultExtract.getQuotaReport(tjQuota, filters);

        List<ReultModel> reultModels = new ArrayList<>();
        for (String key :map.keySet()){
            ReultModel reultModel = new ReultModel();
            reultModel.setKey(key);
            reultModel.setValue(map.get(key).toString());
            reultModels.add(reultModel);
        }
        quotaReport.setReultModelList(reultModels);
        quotaReport.setTjQuota(tjQuota);
        return quotaReport;
    }


}
