package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.DataQualityStatisticsService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yeshijie on 2018/6/1.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DataQualityStatisticsEndPoint", description = "质控-统计", tags = {"档案分析服务-质控-统计"})
public class DataQualityStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DataQualityStatisticsService dataQualityStatisticsService;


    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ApiOperation(value = "测试")
    public Envelop paltformResourceWarningList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end,
            @ApiParam(name = "eventType", value = "就诊类型 0门诊 1住院 2体检,null全部", defaultValue = "")
            @RequestParam(value = "eventType", required = false) Integer eventType) throws Exception {
        return success(dataQualityStatisticsService.dataset(start,end,eventType));
    }


}
