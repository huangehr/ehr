package com.yihu.quota.service.job;

import com.yihu.quota.dao.jpa.TjQuotaDao;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.util.QuartzHelper;
import com.yihu.quota.vo.QuotaVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author chenweida
 */
@Service
public class JobService {
    @Autowired
    private QuartzHelper quartzHelper;
    @Autowired
    private TjQuotaDao quotaDao;
    @Autowired
    private EsResultExtract esResultExtract;

    public void execuJob(Integer id) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        if(tjQuota != null){
            QuotaVo quotaVo =new QuotaVo();
            BeanUtils.copyProperties(tjQuota, quotaVo);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("quota", quotaVo);
            params.put("saasid", "testId");
            params.put("startTime", "");
            params.put("endTime", "");

            quartzHelper.startNow(Class.forName(quotaVo.getJobClazz()),  UUID.randomUUID().toString().replace("-", ""), params);
           //往quartz框架添加任务
//           if (!StringUtils.isEmpty(tjQuota.getJobClazz()) && tjQuota.getExecType().equals("1")) {
//               quartzHelper.startNow(Class.forName(quotaVo.getJobClazz()),  UUID.randomUUID().toString().replace("-", ""), params);
//           }else {
//               quartzHelper.addJob(Class.forName(quotaVo.getJobClazz()), quotaVo.getCron(), UUID.randomUUID().toString().replace("-", ""), params);
//           }
        }
    }


    public List<Map<String, Object>> getQuotaResult(Integer id,String filters ,int pageNo,int pageSize) throws Exception {
        TjQuota tjQuota= quotaDao.findOne(id);
        return  esResultExtract.queryResultListBySql(tjQuota,filters,pageNo,pageSize);
    }

    public int getQuotaTotalCount(){
        return  esResultExtract.getQuotaTotalCount();
    }

}
