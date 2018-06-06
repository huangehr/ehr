package com.yihu.ehr.quality.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.quality.service.DataQualityStatisticsClient;
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
 * @author yeshijie on 2018/6/5.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DataQualityStatisticsEndPoint", description = "质控-统计", tags = {"档案分析服务-质控-统计"})
public class DataQualityStatisticsController extends EnvelopRestEndPoint {

    @Autowired
    private DataQualityStatisticsClient dataQualityStatisticsClient;

    @RequestMapping(value = ServiceApi.DataQuality.QualityMonitoringList, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控查询")
    public Envelop qualityMonitoringList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end,
            @ApiParam(name = "eventType", value = "就诊类型 0门诊 1住院 2体检,null全部", defaultValue = "")
            @RequestParam(value = "eventType", required = false) Integer eventType) throws Exception {
        return dataQualityStatisticsClient.qualityMonitoringList(start,end,eventType);
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceptionList, method = RequestMethod.GET)
    @ApiOperation(value = "接收情况")
    public Envelop receptionList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end) throws Exception {
        return dataQualityStatisticsClient.receptionList(start,end);
    }


}
