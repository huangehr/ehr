package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.service.quota.QuotaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by janseny on 2017/7/27.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "指标统计- 档案相关统计")
public class ArchiveController  extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotaService quotaService;

    /**
     * 获取指标当天统计结果曲线性和柱状报表
     * @param id
     * @return
     */
    @ApiOperation(value = "获取档案统计接口")
    @RequestMapping(value = "getArchiveReport", method = RequestMethod.GET)
    public Envelop getArchiveReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters
    ) {
        Envelop envelop = new Envelop();
        try {
            //id=23 每日新增可识别档案人数
            //每日总共新增多少档案 -- 相加

            //31  每天住院门诊档案数

            //22 到目前为止累计新增的数量

            //16 的总和 每日累计就诊的人数

            //18  的总和  每日累计疾病人数


            QuotaReport quotaReport = quotaService.getQuotaReport(id, filters);
            envelop.setSuccessFlg(true);
            envelop.setObj(quotaReport);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

}
