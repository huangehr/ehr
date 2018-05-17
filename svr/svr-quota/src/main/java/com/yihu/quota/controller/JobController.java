package com.yihu.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.quota.service.job.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 指标任务控制
 *
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "指标任务控制")
public class JobController extends BaseController {

    @Autowired
    private JobService jobService;

    /**
     * 初始执行指标
     */
    @ApiOperation(value = "初始执行指标")
    @RequestMapping(value = ServiceApi.TJ.FirstExecuteQuota, method = RequestMethod.POST)
    @Transactional
    public boolean firstExecuteQuota(
            @ApiParam(name = "id", value = "指标ID", required = true)
            @RequestParam(value = "id", required = true) Integer id) {
        try {
            jobService.executeJob(id, "1", null, null);
            return true;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "初始执行指标发生异常:" + e.getMessage());
            return false;
        }
    }

    /**
     * 指标执行
     */
    @ApiOperation(value = "执行指标")
    @RequestMapping(value = ServiceApi.TJ.TjQuotaExecute, method = RequestMethod.POST)
    public boolean executeQuota(
            @ApiParam(name = "id", value = "指标ID", required = true)
            @RequestParam(value = "id", required = true) Integer id,
            @ApiParam(name = "startDate", value = "起始日期")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "截止日期")
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            jobService.executeJob(id, "2", startDate, endDate);
            return true;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "执行指标发生异常:" + e.getMessage());
            return false;
        }
    }

    @ApiOperation(value = "停止指标")
    @RequestMapping(value = ServiceApi.TJ.TjQuotaRemove, method = RequestMethod.POST)
    public boolean removeQuota(
            @ApiParam(name = "id", value = "指标ID", required = true)
            @RequestParam(value = "id", required = true) Integer id) {
        try {
            jobService.removeJob(id);
            return true;
        } catch (Exception e) {
            error(e);
            e.printStackTrace();
            return false;
        }
    }


}
