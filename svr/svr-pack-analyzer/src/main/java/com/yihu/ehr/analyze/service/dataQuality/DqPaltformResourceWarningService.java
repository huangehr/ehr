package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqPaltformResourceWarningDao;
import com.yihu.ehr.analyze.service.scheduler.WarningSchedulerService;
import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 数据质量-平台资源化预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqPaltformResourceWarningService extends BaseJpaService<DqPaltformResourceWarning, DqPaltformResourceWarningDao> {

    @Autowired
    private DqPaltformResourceWarningDao dqPaltformResourceWarningDao;
    @Autowired
    private WarningSchedulerService warningSchedulerService;

    public DqPaltformResourceWarning findById(Long id) {
        return dqPaltformResourceWarningDao.findOne(id);
    }

    public DqPaltformResourceWarning findByOrgCode(String orgCode){
        return dqPaltformResourceWarningDao.findByOrgCode(orgCode);
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

        DqPaltformResourceWarning oldWarning = findById(warning.getId());
        oldWarning.setErrorNum(warning.getErrorNum());
        oldWarning.setFailureNum(warning.getFailureNum());
        oldWarning.setUnparsingNum(warning.getUnparsingNum());
        oldWarning.setUpdateTime(new Date());
        oldWarning.setUpdateUserId(warning.getUpdateUserId());
        oldWarning.setUpdateUserName(warning.getUpdateUserName());
        boolean flag = false;
        if(!oldWarning.getUnparsingPeriod().equals(warning.getUnparsingPeriod())){
            oldWarning.setUnparsingPeriod(warning.getUnparsingPeriod());
            flag = true;
        }
        save(oldWarning);
        if(flag){
            String cronExp = getCronExp(warning.getUnparsingPeriod());
            warningSchedulerService.addJob(cronExp);
        }
        return oldWarning;
    }

    /**
     * 返回 cron
     * @param unparsingPeriod
     * @return
     */
    public String getCronExp(String unparsingPeriod){
        if(StringUtils.isBlank(unparsingPeriod)){
            DqPaltformResourceWarning warning = dqPaltformResourceWarningDao.findByFirst();
            unparsingPeriod = warning.getUnparsingPeriod();
        }

        String cron = "0 m h * * ?";// h 小时，m 分钟
        String[] str = unparsingPeriod.split(":");
        if(str.length>1){
            return cron.replace("m",Integer.valueOf(str[1])+"").replace("h",Integer.valueOf(str[0])+"");
        }else {
            return cron.replace("m","0").replace("h",Integer.valueOf(str[0])+"");
        }
    }
}
