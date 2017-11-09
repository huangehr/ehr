package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EndPoint - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AttendanceEndPoint", description = "出勤记录", tags = {"应急指挥"})
public class AttendanceEndPoint extends BaseRestEndPoint {
}
