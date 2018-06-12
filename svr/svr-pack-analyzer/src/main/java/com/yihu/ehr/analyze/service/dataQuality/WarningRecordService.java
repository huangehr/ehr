package com.yihu.ehr.analyze.service.dataQuality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预警问题查询
 * @author yeshijie on 2018/6/11.
 */
@Service
public class WarningRecordService {

    @Autowired
    private WarningSettingService warningSettingService;

}
