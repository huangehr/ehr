package com.yihu.ehr.quota.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.quota.TjDimensionSlave;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjDimensionSlaveRepository;
import org.hibernate.SQLQuery;
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
public class TjDimensionSlaveService extends BaseJpaService<TjDimensionSlave, XTjDimensionSlaveRepository> {


    @Autowired
    XTjDimensionSlaveRepository tjDimensionSlaveRepository;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * 根据ID获取.
     * @param
     */
    public TjDimensionSlave getTjDimensionSlave(Integer id) {
        TjDimensionSlave tjDimensionSlave = tjDimensionSlaveRepository.findOne(Long.valueOf(id));
        return tjDimensionSlave;
    }

    public TjDimensionSlave getTjDimensionSlaveByCode(String quotaCode) {
        List<TjDimensionSlave>  dimensionSlaves = tjDimensionSlaveRepository.findByCode(quotaCode);
        if(dimensionSlaves!=null && dimensionSlaves.size()>0){
            return dimensionSlaves.get(0);
        }else {
            return null;
        }
    }

    /**
     * 查询细维度编码是否已存在， 返回已存在数据
     */
    public List tjDimensionSlaveIsExist(String[] values)
    {
        String sql = "SELECT code FROM tj_dimension_slave WHERE code in(:values)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("values", values);
        return sqlQuery.list();
    }
}
