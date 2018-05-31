package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqDatasetWarningDao;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据质量-数据集预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqDatasetWarningService extends BaseJpaService<DqDatasetWarning, DqDatasetWarningDao> {

    @Autowired
    private DqDatasetWarningDao dqDatasetWarningDao;

    public List<DqDatasetWarning> findByOrgCodeAndType(String orgCode,String type){
        return dqDatasetWarningDao.findByOrgCodeAndType(orgCode,type);
    }
}
