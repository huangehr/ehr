package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.job.JobService;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 任务启动
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "后台-任务控制")
public class JobController extends BaseController {
    @Autowired
    private JobService jobService;

    /**
     * 启动任务
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID执行任务")
    @RequestMapping(value = ServiceApi.TJ.TjQuotaExecute, method = RequestMethod.GET)
    public boolean execuJob(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id", required = true) Integer id) {
        try {
            jobService.execuJob(id);
            return true;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "启动失败:" + e.getMessage());
            return false;
        }
    }


}
