package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.quota.TjQuotaDimensionSlave;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjQuotaDimensionSlaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class TjQuotaDimensionSlaveService extends BaseJpaService<TjQuotaDimensionSlave, XTjQuotaDimensionSlaveRepository> {

    @Autowired
    XTjQuotaDimensionSlaveRepository tjQuotaDimensionSlaveRepository;

    public void deleteByQuotaCode(String quotaCode) {
        tjQuotaDimensionSlaveRepository.deleteByQuotaCode(quotaCode);
    }

    public List<TjQuotaDimensionSlave> getTjQuotaDimensionSlaveByCode(String quotaCode) {
        return tjQuotaDimensionSlaveRepository.findByQuotaCode(quotaCode);
    }

    /**
     * 批量细维度
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addTjQuotaDimensionSlaveBatch(List<Map<String, Object>> QuotaDimensionSlaveLs) throws SQLException, InstantiationException, IllegalAccessException {
        TjQuotaDimensionSlave data;
       for(Map<String, Object> map: QuotaDimensionSlaveLs){
           data = new TjQuotaDimensionSlave();
           data.setQuotaCode(String.valueOf(map.get("quotaCode")));
           data.setSlaveCode(String.valueOf(map.get("slaveCode")));
           data.setDictSql(String.valueOf(map.get("dictSql")));
           data.setKeyVal(String.valueOf(map.get("keyVal")));
           data.setSort(Integer.valueOf(String.valueOf(map.get("sort"))));
           save(data);
       }
    }
}
