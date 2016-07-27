package com.yihu.ehr.service;

import com.yihu.ehr.dao.model.DailyMonitorFile;
import com.yihu.ehr.dao.repository.DailyMonitorRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by lyr on 2016/7/27.
 */
@Service
public class DailyMonitorService extends BaseJpaService<DailyMonitorFile, DailyMonitorRepository> {

    @Autowired
    DailyMonitorRepository monitorRepository;

    /**
     * 保存日报监测文件信息
     * @param monitorDate
     * @param fileName
     * @return
     */
    public DailyMonitorFile saveDailyMonitorFile(String monitorDate, String fileName) {
        DailyMonitorFile monitorFile = monitorRepository.findByMonitorDate(monitorDate);

        if(monitorFile == null){
            monitorFile = new DailyMonitorFile();
        }

        monitorFile.setMonitorDate(monitorDate);
        monitorFile.setFileName(fileName);
        monitorFile.setCreateTime(new Date());

        return monitorRepository.save(monitorFile);
    }

    /**
     * 分页排序查询日常监测文件
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public Page<DailyMonitorFile> findByPageAndSort(int page, int size, String sort) {
        PageRequest pageRequest = new PageRequest(page, size, parseSorts(sort));

        return monitorRepository.findAll(pageRequest);
    }
}
