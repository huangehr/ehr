package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqPaltformReceiveWarningDao;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 平台接收预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqPaltformReceiveWarningService extends BaseJpaService<DqPaltformReceiveWarning, DqPaltformReceiveWarningDao> {

    @Autowired
    private DqPaltformReceiveWarningDao dqPaltformReceiveWarningDao;

    public DqPaltformReceiveWarning findById(Long id) {
        return dqPaltformReceiveWarningDao.findOne(id);
    }

    /**
     * 新增平台接收预警
     * @param warning
     * @return
     */
    public DqPaltformReceiveWarning paltformReceiveWarningAdd(DqPaltformReceiveWarning warning){

        save(warning);
        return warning;
    }

    /**
     * 修改平台接收预警
     * @param warning
     * @return
     */
    public DqPaltformReceiveWarning paltformReceiveWarningUpd(DqPaltformReceiveWarning warning){

        save(warning);
        return warning;
    }
}
