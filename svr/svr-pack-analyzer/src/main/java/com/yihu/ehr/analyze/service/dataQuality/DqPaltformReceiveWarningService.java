package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqDatasetWarningDao;
import com.yihu.ehr.analyze.dao.DqPaltformReceiveWarningDao;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 平台接收预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqPaltformReceiveWarningService extends BaseJpaService<DqPaltformReceiveWarning, DqPaltformReceiveWarningDao> {

    @Autowired
    private DqPaltformReceiveWarningDao dqPaltformReceiveWarningDao;
    @Autowired
    private DqDatasetWarningDao dqDatasetWarningDao;

    public DqPaltformReceiveWarning findById(Long id) {
        return dqPaltformReceiveWarningDao.findOne(id);
    }

    public DqPaltformReceiveWarning findByOrgCode(String orgCode){
        return dqPaltformReceiveWarningDao.findByOrgCode(orgCode);
    }

    /**
     * 新增平台接收预警
     * @param warning
     * @return
     */
    public DqPaltformReceiveWarning paltformReceiveWarningAdd(DqPaltformReceiveWarning warning){

        warning.setCreateTime(new Date());
        warning.setUpdateTime(new Date());
        save(warning);
        if(warning.getDatasetWarningList()!=null&&warning.getDatasetWarningList().size()>0){
            warning.getDatasetWarningList().forEach(dataset->{
                dataset.setOrgCode(warning.getOrgCode());
                dataset.setType("1");
            });
            dqDatasetWarningDao.save(warning.getDatasetWarningList());
        }

        return warning;
    }

    /**
     * 修改平台接收预警
     * @param warning
     * @return
     */
    public DqPaltformReceiveWarning paltformReceiveWarningUpd(DqPaltformReceiveWarning warning){

        DqPaltformReceiveWarning oldWarning = findById(warning.getId());
        oldWarning.setErrorNum(warning.getErrorNum());
        oldWarning.setArchiveNum(warning.getErrorNum());
        oldWarning.setHospitalInTime(warning.getHospitalInTime());
        oldWarning.setHospitalInTimeRate(warning.getHospitalInTimeRate());
        oldWarning.setOutpatientInTime(warning.getOutpatientInTime());
        oldWarning.setOutpatientInTimeRate(warning.getOutpatientInTimeRate());
        oldWarning.setPeInTime(warning.getPeInTime());
        oldWarning.setPeInTimeRate(warning.getPeInTimeRate());
        oldWarning.setUpdateTime(new Date());
        oldWarning.setUpdateUserId(warning.getUpdateUserId());
        oldWarning.setUpdateUserName(warning.getUpdateUserName());
        save(oldWarning);
        return oldWarning;
    }
}
