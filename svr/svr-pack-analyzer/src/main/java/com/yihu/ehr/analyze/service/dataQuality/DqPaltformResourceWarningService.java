package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqPaltformResourceWarningDao;
import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据质量-平台资源化预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqPaltformResourceWarningService extends BaseJpaService<DqPaltformResourceWarning, DqPaltformResourceWarningDao> {

    @Autowired
    private DqPaltformResourceWarningDao dqPaltformResourceWarningDao;

    public DqPaltformResourceWarning findById(Long id) {
        return dqPaltformResourceWarningDao.findOne(id);
    }

    /**
     * 新增平台资源化预警
     * @param warning
     * @return
     */
    public DqPaltformResourceWarning paltformResourceWarningAdd(DqPaltformResourceWarning warning){

        save(warning);
        return warning;
    }

    /**
     * 修改平台资源化预警
     * @param warning
     * @return
     */
    public DqPaltformResourceWarning paltformResourceWarningUpd(DqPaltformResourceWarning warning){

        save(warning);
        return warning;
    }
}
