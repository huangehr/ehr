package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.quota.TjQuotaDimensionMain;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjQuotaDimensionMainRepository;
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
public class TjQuotaDimensionMainService extends BaseJpaService<TjQuotaDimensionMain, XTjQuotaDimensionMainRepository> {

    @Autowired
    XTjQuotaDimensionMainRepository tjQuotaDimensionMainRepository;

    public void deleteByQuotaCode(String quotaCode) {
        tjQuotaDimensionMainRepository.deleteByQuotaCode(quotaCode);
    }

    public List<TjQuotaDimensionMain> getTjQuotaDimensionMainByCode(String code){
       return tjQuotaDimensionMainRepository.findByQuotaCode(code);
    }


    /**
     * 批量主维度
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addTjQuotaDimensionMainBatch(List<Map<String, Object>> QuotaDimensionMainLs) throws SQLException, InstantiationException, IllegalAccessException {
        TjQuotaDimensionMain data = new TjQuotaDimensionMain();
        for(Map<String, Object> map: QuotaDimensionMainLs){
            data.setQuotaCode(String.valueOf(map.get("quotaCode")));
            data.setMainCode(String.valueOf(map.get("mainCode")));
            data.setDictSql(String.valueOf(map.get("dictSql")));
            data.setKeyVal(String.valueOf(map.get("keyVal")));
            save(data);
        }
    }

}
