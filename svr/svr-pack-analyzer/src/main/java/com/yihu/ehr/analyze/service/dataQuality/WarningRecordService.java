package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqWarningRecordDao;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预警问题查询
 * @author yeshijie on 2018/6/11.
 */
@Service
public class WarningRecordService extends BaseJpaService<DqWarningRecord, DqWarningRecordDao> {

    @Autowired
    private WarningSettingService warningSettingService;
    @Autowired
    private DqWarningRecordDao dqWarningRecordDao;

    public DqWarningRecord findById(String id) {
        return dqWarningRecordDao.findOne(id);
    }

    /**
     * 处理问题
     * @param solveTime
     * @param solveId
     * @param solveName
     * @param solveType
     * @param id
     */
    public int warningRecordUpd(String solveTime,String solveId,String solveName,String solveType,String id){
        DqWarningRecord record = dqWarningRecordDao.findOne(id);
        if(record != null){
            record.setStatus("2");
            record.setSolveId(solveId);
            record.setSolveName(solveName);
            record.setSolveTime(DateUtil.formatCharDateYMD(solveTime));
            record.setSolveType(solveType);
            dqWarningRecordDao.save(record);
            return 0;
        }else if("2".equals(record.getStatus())){
            return -2;
        }
        return -1;
    }
}
