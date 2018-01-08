package com.yihu.ehr.basic.quota.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.quota.dao.XTjQuotaChartRepository;
import com.yihu.ehr.entity.quota.TjQuotaChart;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class TjQuotaChartService extends BaseJpaService<TjQuotaChart, XTjQuotaChartRepository> {

    @Autowired
    public XTjQuotaChartRepository xTjQuotaChartRepository;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据ID获取.
     * @param
     */
    public TjQuotaChart getTjQuotaChart(Integer id) {
        TjQuotaChart reportTemplate = xTjQuotaChartRepository.findOne(id);
        return reportTemplate;
    }

    public void deleteByQuotaCode(String quotaCode) {
        xTjQuotaChartRepository.deleteByQuotaCode(quotaCode);
    }

    public List<TjQuotaChart> getByQuotaCode(String quotaCode) {
        List<TjQuotaChart> tjQuotaChart = xTjQuotaChartRepository.getByQuotaCode(quotaCode);
        return tjQuotaChart;
    }

    public List<Integer> getChartTypeByQuotaCode(String quotaCode) {
        List<Integer> chartTypeList = xTjQuotaChartRepository.findByQuotaCode(quotaCode);
        return chartTypeList;
    }
}
