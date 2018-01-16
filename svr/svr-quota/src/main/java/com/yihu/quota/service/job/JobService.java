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

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 启动指标任务
     *
     * @param id          指标ID
     * @param executeFlag 执行动作标识，1:初始执行（全量统计），2：立即执行、周期执行（增量统计）
     * @param startDate   抽取数据起始日期。初始执行时为NULL；立即执行时需要传值；周期执行时也为NULL，如果是基础指标周期执行，后续默认赋值昨天开始。
     * @param endDate     抽取数据截止日期。初始执行时无NULL；立即执行时需要传值；周期执行时也为NULL，如果是基础指标周期执行，后续默认赋值昨天截止。
     * @throws Exception
     */
    public void executeJob(Integer id, String executeFlag, String startDate, String endDate) throws Exception {
        TjQuota tjQuota = quotaDao.findOne(id);
        if (tjQuota != null) {
            QuotaVo quotaVo = new QuotaVo();
            BeanUtils.copyProperties(tjQuota, quotaVo);
            Map<String, Object> params = new HashMap<>();
            params.put("quota", quotaVo);
            params.put("executeFlag", executeFlag);
            params.put("startTime", startDate);
            params.put("endTime", endDate);
            String quotaCode = quotaVo.getCode().replace("_", "");
            String quotaCodeImmediately = quotaCode + "immediately";
            boolean existJob = quartzHelper.isExistJob(quotaCode);
            boolean existJobImmediately = quartzHelper.isExistJob(quotaCodeImmediately);
            if (existJob) {
                quartzHelper.removeJob(quotaCode);
            }
            if (existJobImmediately) {
                quartzHelper.removeJob(quotaCodeImmediately);
            }
            //往quartz框架添加任务
            if ((!StringUtils.isEmpty(executeFlag) && executeFlag.equals("1")) || // 初始执行
                    (!StringUtils.isEmpty(tjQuota.getJobClazz()) && tjQuota.getExecType().equals("1"))) { // 立即执行
                quartzHelper.startNow(Class.forName(quotaVo.getJobClazz()), quotaCodeImmediately, params);
            } else {
                quartzHelper.addJob(Class.forName(quotaVo.getJobClazz()), quotaVo.getCron(), quotaCode, params);
            }
        }
    }

}
