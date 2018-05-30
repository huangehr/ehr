package com.yihu.ehr.analyze.service.dataQuality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yeshijie on 2018/5/28.
 */
@Service
public class WarningSettingService {

    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private DqPaltformReceiveWarningService dqPaltformReceiveWarningService;
    @Autowired
    private DqPaltformResourceWarningService dqPaltformResourceWarningService;
    @Autowired
    private DqPaltformUploadWarningService dqPaltformUploadWarningService;


    /**
     * 查找接收预警设置
     * @param orgCode
     */
    public void getReceiveWarning(String orgCode){

    }

}
