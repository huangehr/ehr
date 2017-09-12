package com.yihu.ehr.statistics.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.statistics.service.StatisticsClient;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zdm on 2017/9/12
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "Statistics", description = "数据中心首页统计相关", tags = {"数据中心首页-统计相关"})
public class StatisticsController extends BaseController {
    @Autowired
    private StatisticsClient statisticsClient;

}
