package com.yihu.ehr.quality.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 *  质控-统计
 * @author yeshijie on 2018/6/5.
 */
@FeignClient(name= MicroServices.Analyzer)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DataQualityStatisticsClient {


    @RequestMapping(value = ServiceApi.DataQuality.QualityMonitoringList, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控查询")
    Envelop qualityMonitoringList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end,
            @ApiParam(name = "eventType", value = "就诊类型 0门诊 1住院 2体检,null全部", defaultValue = "")
            @RequestParam(value = "eventType", required = false) Integer eventType);

    @RequestMapping(value = ServiceApi.DataQuality.ReceptionList, method = RequestMethod.GET)
    @ApiOperation(value = "接收情况")
    Envelop receptionList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end);


}
