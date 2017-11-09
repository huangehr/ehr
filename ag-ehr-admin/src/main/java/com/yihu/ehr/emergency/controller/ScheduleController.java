package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ScheduleHistroyController", description = "排班历史", tags = {"应急指挥"})
public class ScheduleController extends BaseController {

}
