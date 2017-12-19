package com.yihu.ehr.resource.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.resource.client.RsResourceStatisticsClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by janseny on 2017/12/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "rs_resources_statistics",  description = "资源报表统计视图", tags = {"资源报表统计视图接口"})
public class RsResourceStatisticsController extends ExtendController {

    @Autowired
    private RsResourceStatisticsClient rsResourceStatisticsClient;

    @RequestMapping(value = ServiceApi.Resources.StatisticsGetDoctorsGroupByTown, method = RequestMethod.GET)
    @ApiOperation(value = "获取各行政区划总卫生人员")
    public Envelop statisticsGetDoctorsGroupByTown() {
        Envelop envelop = new Envelop();
        try {
            envelop = rsResourceStatisticsClient.statisticsGetDoctorsGroupByTown();
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
        return envelop;
    }

}
